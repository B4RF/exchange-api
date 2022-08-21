package com.barf.exchangeapi.exchanges.kraken;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.barf.exchangeapi.domain.Currency;
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

    return LocalDateTime.ofInstant(Instant.ofEpochSecond(timeInSeconds), ZoneId.systemDefault());
  }

  @Override
  public Ticker getTicker() throws ApiException {
    throw new ApiException("endpoint not implemented");
  }

  @Override
  public Collection<OHLC> getOHLC() throws ApiException {
    throw new ApiException("endpoint not implemented");
  }

  @Override
  public Map<Currency, BigDecimal> getBalance() throws ApiException {
    throw new ApiException("endpoint not implemented");
  }

  @Override
  public Collection<Order> getOpen() throws ApiException {
    throw new ApiException("endpoint not implemented");
  }

  @Override
  public Collection<Order> getClosed() throws ApiException {
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
      json = this.query(method, null);

    } catch (InvalidKeyException | NoSuchAlgorithmException | IOException e) {
      throw new ApiException(e);
    }

    final JSONArray error = json.getJSONArray("error");

    if (json.getJSONArray("error").length() > 0) {
      throw new ApiException(error.join("\n"));
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
}
