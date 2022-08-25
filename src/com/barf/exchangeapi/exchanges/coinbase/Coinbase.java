package com.barf.exchangeapi.exchanges.coinbase;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.barf.exchangeapi.domain.AssetPair;
import com.barf.exchangeapi.domain.AssetPairInfo;
import com.barf.exchangeapi.domain.Interval;
import com.barf.exchangeapi.domain.OHLC;
import com.barf.exchangeapi.domain.Order;
import com.barf.exchangeapi.domain.OrderAction;
import com.barf.exchangeapi.domain.OrderStatus;
import com.barf.exchangeapi.domain.Price;
import com.barf.exchangeapi.domain.Ticker;
import com.barf.exchangeapi.domain.Volume;
import com.barf.exchangeapi.exchanges.ApiException;
import com.barf.exchangeapi.exchanges.ApiRequest;
import com.barf.exchangeapi.exchanges.Exchange;
import com.barf.exchangeapi.logic.Utils;

public class Coinbase implements Exchange {

  private static final String URL = "https://api.pro.coinbase.com";

  private static final String REQUEST_API_KEY = "CB-ACCESS-KEY";
  private static final String REQUEST_API_SIGN = "CB-ACCESS-SIGN";
  private static final String REQUEST_API_TIMESTAMP = "CB-ACCESS-TIMESTAMP";
  private static final String REQUEST_API_PASSPHRASE = "CB-ACCESS-PASSPHRASE";

  private static final String HEADER_USER_AGENT = "User-Agent";
  private static final String HEADER_ACCEPT = "Accept";
  private static final String HEADER_CONTENT_TYPE = "Content-Type";
  private static final String VALUE_USER_AGENT = "gdax-node-client";
  private static final String VALUE_CONTENT_TYPE = "application/json";

  private static final String GET_REQUEST = "GET";
  private static final String POST_REQUEST = "POST";
  private static final String DELETE_REQUEST = "DELETE";

  private static final Map<AssetPair, String> ASSET_PAIR_NAMES;
  static {
    final Map<AssetPair, String> tmp = new HashMap<>();
    tmp.put(AssetPair.XBTEUR, "BTC-EUR");
    ASSET_PAIR_NAMES = Collections.unmodifiableMap(tmp);
  }

  private final String key;
  private final String secret;
  private final String passphrase;

  public Coinbase(final String key, final String secret, final String passphrase) {
    this.key = key;
    this.secret = secret;
    this.passphrase = passphrase;
  }

  private String getPairName(final AssetPair currencyPair) {
    return Coinbase.ASSET_PAIR_NAMES.get(currencyPair);
  }

  @Override
  public AssetPairInfo getInfo(final AssetPair assetPair) throws ApiException {
    final JSONObject json = this.callObjectEndpoint(false, Coinbase.GET_REQUEST, "/products/" + this.getPairName(assetPair), null);

    return new AssetPairInfo.Builder()
        .setBaseDecimals(json.getBigDecimal("base_increment").stripTrailingZeros().scale())
        .setQuoteDecimals(json.getBigDecimal("quote_increment").stripTrailingZeros().scale())
        .setMinOrder(new Volume(json.getBigDecimal("quote_increment"), assetPair.getQuote()))
        .build();
  }

  @Override
  public LocalDateTime getServerTime() throws ApiException {
    final JSONObject json = this.callObjectEndpoint(false, Coinbase.GET_REQUEST, "/time", null);
    final long timeInSeconds = json.getLong("epoch");

    return Utils.secondsToDate(timeInSeconds);
  }

  @Override
  public Ticker getTicker() throws ApiException {
    final AssetPair pair = AssetPair.XBTEUR;

    final JSONObject json = this.callObjectEndpoint(false, Coinbase.GET_REQUEST, "/products/" + this.getPairName(pair) + "/ticker", null);

    return new Ticker.Builder()
        .setCurrency(pair.getQuote())
        .setAsk(json.getBigDecimal("ask"))
        .setBid(json.getBigDecimal("bid"))
        .build();
  }

  @Override
  public List<OHLC> getOHLC(final Interval interval, final LocalDateTime since) throws ApiException {
    final AssetPair pair = AssetPair.XBTEUR;

    if (interval == Interval.WEEK) {
      throw new ApiException("Coinbase doesn't support an ohlc granularity of week");
    }
    final int granularity = interval.getMinutes() * 60;

    final Map<String, String> input = new HashMap<>();
    input.put("granularity", String.valueOf(granularity));
    input.put("start", since.toString());
    input.put("end", LocalDateTime.now().toString());

    final JSONArray json = this.callArrayEndpoint(false, Coinbase.GET_REQUEST,
        "/products/" + this.getPairName(pair) + "/candles" + this.createParamURL(input), null);

    final List<OHLC> ohlcList = new ArrayList<>();
    for (int i = 0; i < json.length(); i++) {
      final JSONArray entry = json.getJSONArray(i);

      final OHLC ohlc = new OHLC.Builder()
          .setDate(Utils.secondsToDate(entry.getLong(0)))
          .setCurrency(pair.getQuote())
          .setOpen(entry.getBigDecimal(3))
          .setHigh(entry.getBigDecimal(2))
          .setLow(entry.getBigDecimal(1))
          .setClose(entry.getBigDecimal(4))
          .build();

      ohlcList.add(ohlc);
    }

    return ohlcList;
  }

  @Override
  public Set<Volume> getBalance() throws ApiException {
    throw new ApiException("endpoint not implemented");
  }

  @Override
  public List<Order> getOpen() throws ApiException {
    throw new ApiException("endpoint not implemented");
  }

  @Override
  public List<Order> getClosed(final LocalDateTime since) throws ApiException {
    throw new ApiException("endpoint not implemented");
  }

  @Override
  public List<String> createMarketOrder(final OrderAction action, final Volume volume) throws ApiException {
    throw new ApiException("endpoint not implemented");
  }

  @Override
  public List<String> createLimitOrder(final OrderAction action, final Volume volume, final Price price) throws ApiException {
    throw new ApiException("endpoint not implemented");
  }

  @Override
  public Order getOrder(final String id) throws ApiException {
    throw new ApiException("endpoint not implemented");
  }

  @Override
  public OrderStatus cancelOrder(final String id) throws ApiException {
    throw new ApiException("endpoint not implemented");
  }

  private JSONObject callObjectEndpoint(final boolean isPrivate, final String requestMethod, final String path,
      final Map<String, String> params) throws ApiException {
    JSONObject json;

    try {
      json = new JSONObject(this.query(isPrivate, requestMethod, path, params));

    } catch (InvalidKeyException | NoSuchAlgorithmException | IOException e) {
      throw new ApiException(e);
    }

    if (json.has("message")) {
      throw new ApiException(path + ": " + json.getString("message"));
    }

    return json;
  }

  private JSONArray callArrayEndpoint(final boolean isPrivate, final String requestMethod, final String path,
      final Map<String, String> params) throws ApiException {
    JSONArray json;
    final String response;

    try {
      response = this.query(isPrivate, requestMethod, path, params);
    } catch (InvalidKeyException | NoSuchAlgorithmException | IOException e) {
      throw new ApiException(e);
    }

    try {
      json = new JSONArray(response);
    } catch (final JSONException e) {
      throw new ApiException(path + ": " + new JSONObject(response).getString("message"));
    }

    return json;
  }

  private String query(final boolean isPrivate, final String requestMethod, final String path, final Map<String, String> params)
      throws IOException, InvalidKeyException, NoSuchAlgorithmException {
    final ApiRequest request = new ApiRequest();

    request.setRequestMethod(requestMethod);
    request.setURL(Coinbase.URL + path);
    final String postData = this.createPostData(params);
    request.setPostData(postData);

    if (isPrivate) {
      final String timestamp = String.valueOf(System.currentTimeMillis() / 1000d);

      final byte[] hmacMessage = Utils.stringToBytes(timestamp + requestMethod + path + postData);
      final byte[] hmacKey = Utils.base64Decode(this.secret);

      final String hmacDigest = Utils.base64Encode(Utils.hmacSha256(hmacKey, hmacMessage));
      request.addHeader(Coinbase.REQUEST_API_KEY, this.key);
      request.addHeader(Coinbase.REQUEST_API_SIGN, hmacDigest);
      request.addHeader(Coinbase.REQUEST_API_TIMESTAMP, timestamp);
      request.addHeader(Coinbase.REQUEST_API_PASSPHRASE, this.passphrase);

      request.addHeader(Coinbase.HEADER_ACCEPT, Coinbase.VALUE_CONTENT_TYPE);
      request.addHeader(Coinbase.HEADER_CONTENT_TYPE, Coinbase.VALUE_CONTENT_TYPE);
    }
    request.addHeader(Coinbase.HEADER_USER_AGENT, Coinbase.VALUE_USER_AGENT);

    return request.execute();
  }

  private String createParamURL(final Map<String, String> parameters) {
    String urlParams = "";

    if (parameters != null) {
      for (final String param : parameters.keySet()) {
        final String appendix = urlParams.isEmpty() ? "?" : "&";

        urlParams += appendix + param + "=" + parameters.get(param);
      }
    }

    return urlParams;
  }

  private String createPostData(final Map<String, String> parameters) {
    String data = "";

    if ((parameters != null) && !parameters.isEmpty()) {
      final JSONObject json = new JSONObject(parameters);
      data = json.toString();
    }

    return data;
  }
}
