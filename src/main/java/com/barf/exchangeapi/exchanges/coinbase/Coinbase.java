package com.barf.exchangeapi.exchanges.coinbase;

import java.io.IOException;
import java.math.BigDecimal;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections4.BidiMap;
import org.apache.commons.collections4.bidimap.DualHashBidiMap;
import org.apache.commons.collections4.bidimap.UnmodifiableBidiMap;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import com.barf.exchangeapi.domain.AssetPair;
import com.barf.exchangeapi.domain.AssetPairInfo;
import com.barf.exchangeapi.domain.Currency;
import com.barf.exchangeapi.domain.Interval;
import com.barf.exchangeapi.domain.OHLC;
import com.barf.exchangeapi.domain.Order;
import com.barf.exchangeapi.domain.OrderAction;
import com.barf.exchangeapi.domain.OrderStatus;
import com.barf.exchangeapi.domain.OrderType;
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

  private static final BidiMap<Currency, String> CURRENCY_NAMES;
  static {
    final BidiMap<Currency, String> tmp = new DualHashBidiMap<>();
    tmp.put(Currency.EUR, "EUR");
    tmp.put(Currency.USD, "USD");
    tmp.put(Currency.BTC, "BTC");
    tmp.put(Currency.ETH, "ETH");
    tmp.put(Currency.DOGE, "DOGE");
    CURRENCY_NAMES = UnmodifiableBidiMap.unmodifiableBidiMap(tmp);
  }

  private final String key;
  private final String secret;
  private final String passphrase;

  public Coinbase(final String key, final String secret, final String passphrase) {
    this.key = key;
    this.secret = secret;
    this.passphrase = passphrase;
  }

  private static String getPairName(final AssetPair assetPair) {
    return Coinbase.CURRENCY_NAMES.get(assetPair.getBase()) + "-" + Coinbase.CURRENCY_NAMES.get(assetPair.getQuote());
  }

  @Override
  public AssetPairInfo getInfo(final AssetPair assetPair) throws ApiException {
    final JSONObject json = this.callObjectEndpoint(false, Coinbase.GET_REQUEST, "/products/" + Coinbase.getPairName(assetPair), null);

    return new AssetPairInfo.Builder()
        .setBaseDecimals(json.getBigDecimal("base_increment").stripTrailingZeros().scale())
        .setQuoteDecimals(json.getBigDecimal("quote_increment").stripTrailingZeros().scale())
        .setMinOrder(new Volume.Builder().setAmount(json.getBigDecimal("quote_increment")).setCurrency(assetPair.getQuote()).build())
        .build();
  }

  @Override
  public LocalDateTime getServerTime() throws ApiException {
    final JSONObject json = this.callObjectEndpoint(false, Coinbase.GET_REQUEST, "/time", null);
    final long timeInSeconds = json.getLong("epoch");

    return Utils.secondsToDate(timeInSeconds);
  }

  @Override
  public Ticker getTicker(final AssetPair assetPair) throws ApiException {
    final JSONObject json = this.callObjectEndpoint(false, Coinbase.GET_REQUEST, "/products/" + Coinbase.getPairName(assetPair) + "/ticker",
        null);

    return new Ticker.Builder()
        .setCurrency(assetPair.getQuote())
        .setAsk(json.getBigDecimal("ask"))
        .setBid(json.getBigDecimal("bid"))
        .build();
  }

  @Override
  public List<OHLC> getOHLC(final AssetPair assetPair, final Interval interval, final LocalDateTime since) throws ApiException {
    if (interval == Interval.WEEK) {
      throw new ApiException("Coinbase doesn't support an ohlc granularity of week");
    }
    final int granularity = interval.getMinutes() * 60;

    final Map<String, String> input = new HashMap<>();
    input.put("granularity", String.valueOf(granularity));
    input.put("start", since.toString());
    input.put("end", LocalDateTime.now().toString());

    final JSONArray json = this.callArrayEndpoint(false, Coinbase.GET_REQUEST,
        "/products/" + Coinbase.getPairName(assetPair) + "/candles" + this.createParamURL(input), null);

    final List<OHLC> ohlcList = new ArrayList<>();
    for (int i = 0; i < json.length(); i++) {
      final JSONArray entry = json.getJSONArray(i);

      final OHLC ohlc = new OHLC.Builder()
          .setDate(Utils.secondsToDate(entry.getLong(0)))
          .setCurrency(assetPair.getQuote())
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
  public Set<Volume> getPortfolio() throws ApiException {
    final JSONArray jsonArray = this.callArrayEndpoint(true, Coinbase.GET_REQUEST, "/accounts", null);

    final Set<Volume> balances = new HashSet<>();
    for (int i = 0; i < jsonArray.length(); i++) {
      final JSONObject json = jsonArray.getJSONObject(i);

      final Map<String, Currency> inverse = Coinbase.CURRENCY_NAMES.inverseBidiMap();

      final Currency currency = inverse.get(json.getString("currency"));
      if (currency != null) {
        balances.add(new Volume.Builder().setAmount(json.getBigDecimal("balance")).setCurrency(currency).build());
      }
    }

    return balances;
  }

  @Override
  public List<Order> getOpen() throws ApiException {
    final Map<String, String> input = new HashMap<>();
    input.put("status", "open");

    final JSONArray jsonArray = this.callArrayEndpoint(true, Coinbase.GET_REQUEST, "/orders" + this.createParamURL(input), null);

    final List<Order> openOrders = new ArrayList<>();

    for (int i = 0; i < jsonArray.length(); i++) {
      openOrders.add(this.fromJSON(jsonArray.getJSONObject(i)));
    }

    return openOrders;
  }

  @Override
  public List<Order> getClosed(final LocalDateTime since) throws ApiException {
    final Map<String, String> input = new HashMap<>();
    input.put("status", "done");
    input.put("start_date", since.toString());

    final JSONArray jsonArray = this.callArrayEndpoint(true, Coinbase.GET_REQUEST, "/orders" + this.createParamURL(input), null);

    final List<Order> closedOrders = new ArrayList<>();

    for (int i = 0; i < jsonArray.length(); i++) {
      final Order order = this.fromJSON(jsonArray.getJSONObject(i));

      if (order.getStatus() == OrderStatus.CLOSED) {
        closedOrders.add(order);
      }
    }

    return closedOrders;
  }

  @Override
  public List<String> createMarketOrder(final AssetPair assetPair, final OrderAction action, final Volume volume) throws ApiException {
    String volumeParam;
    if (volume.getCurrency() == assetPair.getBase()) {
      volumeParam = "size";
    } else if (volume.getCurrency() == assetPair.getQuote()) {
      volumeParam = "funds";
    } else {
      throw new ApiException("Volume currency does not match asset pair");
    }

    final Map<String, String> input = new HashMap<>();
    input.put("product_id", Coinbase.getPairName(assetPair));
    input.put("side", action.name().toLowerCase());
    input.put("type", "limit");
    input.put(volumeParam, String.valueOf(volume.getAmount()));
    input.put("post_only", "true");

    final JSONObject json = this.callObjectEndpoint(true, Coinbase.POST_REQUEST, "/orders", input);

    return Arrays.asList(json.getString("id"));
  }

  @Override
  public List<String> createLimitOrder(final AssetPair assetPair, final OrderAction action, final Volume volume, final Price price)
      throws ApiException {
    if (volume.getCurrency() != assetPair.getBase()) {
      throw new ApiException("Volume currency does not match asset pair");
    }

    if (price.getCurrency() != assetPair.getQuote()) {
      throw new ApiException("price currency does not match asset pair");
    }

    final Map<String, String> input = new HashMap<>();
    input.put("product_id", Coinbase.getPairName(assetPair));
    input.put("side", action.name().toLowerCase());
    input.put("type", "limit");
    input.put("price", String.valueOf(price.getAmount()));
    input.put("size", String.valueOf(volume.getAmount()));
    input.put("post_only", "true");

    final JSONObject json = this.callObjectEndpoint(true, Coinbase.POST_REQUEST, "/orders", input);

    return Arrays.asList(json.getString("id"));
  }

  @Override
  public Order getOrder(final String id) throws ApiException {
    final JSONObject json = this.callObjectEndpoint(true, Coinbase.GET_REQUEST, "/orders/" + id, null);

    return this.fromJSON(json);
  }

  @Override
  public OrderStatus cancelOrder(final String id) throws ApiException {
    final String cancelledId = this.callStringEndpoint(true, Coinbase.DELETE_REQUEST, "/orders/" + id, null);

    return cancelledId.equals("\"" + id + "\"") ? OrderStatus.CANCELED : OrderStatus.UNKNOWN;
  }

  private Order fromJSON(final JSONObject json) throws JSONException, ApiException {
    final AssetPair assetPair = this.parseAssetPair(json.getString("product_id"));
    final BigDecimal execVolumeAmount = json.getBigDecimal("filled_size");

    final Order.Builder builder = new Order.Builder()
        .setId(json.getString("id"))
        .setStatus(this.parseOrderStatus(json.getString("status"), execVolumeAmount))
        .setPair(assetPair)
        .setAction(OrderAction.valueOf(json.getString("side").toUpperCase()))
        .setType(OrderType.valueOf(json.getString("type").toUpperCase()))
        .setVolume(new Volume.Builder().setAmount(json.getBigDecimal("size")).setCurrency(assetPair.getBase()).build())
        .setPrice(new Price.Builder().setAmount(json.getBigDecimal("price")).setCurrency(assetPair.getQuote()).build())
        .setStart(this.parseDate(json.getString("created_at")))
        .setExecVolume(new Volume.Builder().setAmount(execVolumeAmount).setCurrency(assetPair.getBase()).build());

    if (json.has("done_at")) {
      builder.setClose(this.parseDate(json.getString("done_at")));
    }

    return builder.build();
  }

  private AssetPair parseAssetPair(final String pair) throws ApiException {
    final Map<String, Currency> inverse = Coinbase.CURRENCY_NAMES.inverseBidiMap();

    final String[] currencies = pair.split("-");
    final AssetPair assetPair = AssetPair.fromCurrencies(inverse.get(currencies[0]), inverse.get(currencies[1]));

    if (assetPair == null) {
      throw new ApiException("Unknown asset pair: " + pair);
    }

    return assetPair;
  }

  private OrderStatus parseOrderStatus(final String status, final BigDecimal filledSize) {
    switch (status) {
    case "open":
      return OrderStatus.OPEN;
    case "pending":
      return OrderStatus.PENDING;
    case "rejected":
      return OrderStatus.CANCELED;
    case "done":
      return BigDecimal.ZERO.compareTo(filledSize) == 0 ? OrderStatus.CANCELED : OrderStatus.CLOSED;
    default:
      return OrderStatus.UNKNOWN;
    }
  }

  private LocalDateTime parseDate(final String dateString) {
    return LocalDateTime.parse(dateString.substring(0, 16), DateTimeFormatter.ISO_LOCAL_DATE_TIME);
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

  private String callStringEndpoint(final boolean isPrivate, final String requestMethod, final String path,
      final Map<String, String> params) throws ApiException {
    final String response;

    try {
      response = this.query(isPrivate, requestMethod, path, params);
    } catch (InvalidKeyException | NoSuchAlgorithmException | IOException e) {
      throw new ApiException(e);
    }

    final Object json = new JSONTokener(response).nextValue();
    if (json instanceof JSONObject) {
      throw new ApiException(path + ": " + ((JSONObject) json).getString("message"));
    }

    return response;
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
