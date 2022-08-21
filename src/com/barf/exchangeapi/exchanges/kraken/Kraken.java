package com.barf.exchangeapi.exchanges.kraken;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.barf.exchangeapi.domain.Currency;
import com.barf.exchangeapi.domain.CurrencyPair;
import com.barf.exchangeapi.domain.Interval;
import com.barf.exchangeapi.domain.OHLC;
import com.barf.exchangeapi.domain.Order;
import com.barf.exchangeapi.domain.Ticker;
import com.barf.exchangeapi.exchanges.ApiException;
import com.barf.exchangeapi.exchanges.ApiRequest;
import com.barf.exchangeapi.exchanges.Exchange;
import com.barf.exchangeapi.logic.Utils;

public class Kraken implements Exchange {

  private static final String KRAKEN_UNAVAILABLE = "EService:Unavailable";
  private static final String KRAKEN_BUSY = "EService:Busy";
  private static final String KRAKEN_UNKNOWN_ORDER = "EOrder:Unknown order";
  private static final String KRAKEN_CANCEL_PENDING = "WOrder:Cancel pending";

  private static final String PUBLIC_URL = "https://api.kraken.com/0/public/";
  private static final String PRIVATE_URL = "https://api.kraken.com/0/private/";

  private static final String REQUEST_API_KEY = "API-Key";
  private static final String REQUEST_API_SIGN = "API-Sign";
  private static final String NONCE = "nonce";
  private static final String MICRO_SECONDS = "000";

  private final String key;
  private final String secret;

  public Kraken(final String key, final String secret) {
    this.key = key;
    this.secret = secret;
  }

  @Override
  public LocalDateTime getServerTime() throws ApiException {
    final JSONObject json = this.callEndpoint(Method.TIME, null);
    final long timeInSeconds = json.getJSONObject("result").getLong("unixtime");

    return Utils.secondsToDate(timeInSeconds);
  }

  @Override
  public Ticker getTicker() throws ApiException {
    final CurrencyPair pair = CurrencyPair.XBTEUR;

    final Map<String, String> input = new HashMap<>();
    input.put("pair", pair.name());

    final JSONObject json = this.callEndpoint(Method.TICKER, input);

    final String resultPair = this.getCurrencyPairName(pair);
    final BigDecimal ask = json.getJSONObject("result").getJSONObject(resultPair).getJSONArray("a").getBigDecimal(0);
    final BigDecimal bid = json.getJSONObject("result").getJSONObject(resultPair).getJSONArray("b").getBigDecimal(0);

    return new Ticker(ask, bid);
  }

  @Override
  public List<OHLC> getOHLC(final Interval interval, final LocalDateTime since) throws ApiException {
    final CurrencyPair pair = CurrencyPair.XBTEUR;

    final Map<String, String> input = new HashMap<>();
    input.put("pair", pair.name());
    input.put("interval", String.valueOf(interval.minutes));
    if (since != null) {
      input.put("since", String.valueOf(Utils.dateToSeconds(since)));
    }

    final JSONObject json = this.callEndpoint(Method.OHLC, input);

    final String resultPair = this.getCurrencyPairName(pair);
    final JSONArray result = json.getJSONObject("result").getJSONArray(resultPair);
    final List<OHLC> ohlcList = new ArrayList<>();

    for (int i = 0; i < result.length(); i++) {
      final JSONArray entry = result.getJSONArray(i);

      final OHLC ohlc = new OHLC.Builder()
          .setDate(Utils.secondsToDate(entry.getLong(0)))
          .setOpen(entry.getBigDecimal(1))
          .setHigh(entry.getBigDecimal(2))
          .setLow(entry.getBigDecimal(3))
          .setClose(entry.getBigDecimal(4))
          .build();

      ohlcList.add(ohlc);
    }

    return ohlcList;
  }

  @Override
  public Map<Currency, BigDecimal> getBalance() throws ApiException {
    throw new ApiException("endpoint not implemented");
  }

  @Override
  public List<Order> getOpen() throws ApiException {
    throw new ApiException("endpoint not implemented");
  }

  @Override
  public List<Order> getClosed() throws ApiException {
    throw new ApiException("endpoint not implemented");
  }

  @Override
  public boolean createOrder(final Order order) throws ApiException {
    throw new ApiException("endpoint not implemented");
  }

  @Override
  public Order getOrder(final String id) throws ApiException {
    throw new ApiException("endpoint not implemented");
  }

  @Override
  public boolean cancelOrder(final String id) throws ApiException {
    throw new ApiException("endpoint not implemented");
  }

  /**
   * @param method
   *          the api endpoint method
   * @param params
   *          list of endpoint parameters
   * @return a successful api json response
   * @throws ApiException
   *           in case of errors
   */
  private JSONObject callEndpoint(final Method method, final Map<String, String> params) throws ApiException {
    JSONObject json = null;

    try {
      json = this.query(method, params);

    } catch (InvalidKeyException | NoSuchAlgorithmException | IOException e) {
      throw new ApiException(e);
    }

    final JSONArray error = json.getJSONArray("error");

    if (json.getJSONArray("error").length() > 0) {
      throw new ApiException(method.name() + ": " + error.join(", "));
    }

    return json;
  }

  private JSONObject query(final Method method, final Map<String, String> params)
      throws IOException, InvalidKeyException, NoSuchAlgorithmException {
    final ApiRequest request = new ApiRequest();

    if (method.isPrivate()) {
      request.addHeader(Kraken.REQUEST_API_KEY, this.key);

      final Map<String, String> parameters = params == null ? new HashMap<>() : new HashMap<>(params);

      final String nonce = String.valueOf(System.currentTimeMillis()) + Kraken.MICRO_SECONDS;
      parameters.put(Kraken.NONCE, nonce);
      final String postData = this.createPostData(parameters);
      request.setPostData(postData);
      final String url = Kraken.PRIVATE_URL + method.getURL();

      final byte[] sha256 = Utils.sha256(nonce + postData);
      final byte[] path = Utils.stringToBytes(request.setURL(url));
      final byte[] hmacKey = Utils.base64Decode(this.secret);
      final byte[] hmacMessage = Utils.concatArrays(path, sha256);

      final String hmacDigest = Utils.base64Encode(Utils.hmacSha512(hmacKey, hmacMessage));
      request.addHeader(Kraken.REQUEST_API_SIGN, hmacDigest);

    } else {
      final String url = Kraken.PUBLIC_URL + method.getURL();
      request.setURL(url);
      request.setPostData(this.createPostData(params));
    }

    final String response = request.execute();
    JSONObject json;
    try {
      json = new JSONObject(response);
    } catch (final JSONException e) {
      json = new JSONObject("{\"error\":[" + JSONObject.quote("ENetwork:" + response) + "]}");
    }
    return json;
  }

  private String createPostData(final Map<String, String> parameters) throws UnsupportedEncodingException {
    final StringBuilder sb = new StringBuilder();

    if (parameters != null) {
      for (final Entry<String, String> entry : parameters.entrySet()) {
        sb.append(entry.getKey()).append("=").append(Utils.urlEncode(entry.getValue())).append("&");
      }
    }
    return sb.toString();
  }

  private static final Map<Currency, String> CURRENCY_NAMES;
  static {
    final Map<Currency, String> tmp = new HashMap<>();
    tmp.put(Currency.XBT, "XXBT");
    tmp.put(Currency.EUR, "ZEUR");
    CURRENCY_NAMES = Collections.unmodifiableMap(tmp);
  }

  private String getCurrencyPairName(final CurrencyPair currencyPair) {
    return Kraken.CURRENCY_NAMES.get(currencyPair.base) + Kraken.CURRENCY_NAMES.get(currencyPair.quote);
  }
}
