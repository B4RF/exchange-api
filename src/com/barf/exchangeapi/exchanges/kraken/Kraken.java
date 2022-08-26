package com.barf.exchangeapi.exchanges.kraken;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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

public class Kraken implements Exchange {

  private static final String PUBLIC_URL = "https://api.kraken.com/0/public/";
  private static final String PRIVATE_URL = "https://api.kraken.com/0/private/";

  private static final String REQUEST_API_KEY = "API-Key";
  private static final String REQUEST_API_SIGN = "API-Sign";
  private static final String NONCE = "nonce";
  private static final String MICRO_SECONDS = "000";

  private static final Map<Currency, String> CURRENCY_NAMES;
  static {
    final Map<Currency, String> tmp = new HashMap<>();
    tmp.put(Currency.EUR, "ZEUR");
    tmp.put(Currency.USD, "ZUSD");
    tmp.put(Currency.BTC, "XXBT");
    tmp.put(Currency.ETH, "XETH");
    tmp.put(Currency.DOGE, "XXDG");
    CURRENCY_NAMES = Collections.unmodifiableMap(tmp);
  }

  private final String key;
  private final String secret;

  public Kraken(final String key, final String secret) {
    this.key = key;
    this.secret = secret;
  }

  private String getPairName(final AssetPair assetPair) {
    if (assetPair.getBase() == Currency.DOGE) {
      return this.getShortPairName(assetPair);
    }
    return Kraken.CURRENCY_NAMES.get(assetPair.getBase()) + Kraken.CURRENCY_NAMES.get(assetPair.getQuote());
  }

  private String getShortPairName(final AssetPair assetPair) {
    return Kraken.CURRENCY_NAMES.get(assetPair.getBase()).substring(1) + Kraken.CURRENCY_NAMES.get(assetPair.getQuote()).substring(1);
  }

  @Override
  public AssetPairInfo getInfo(final AssetPair assetPair) throws ApiException {
    final Map<String, String> input = new HashMap<>();
    input.put("pair", this.getPairName(assetPair));

    final JSONObject json = this.callEndpoint(Method.ASSET_PAIRS, input);
    final JSONObject currencyPair = json.getJSONObject(this.getPairName(assetPair));

    return new AssetPairInfo.Builder()
        .setBaseDecimals(currencyPair.getInt("lot_decimals"))
        .setQuoteDecimals(currencyPair.getInt("pair_decimals"))
        .setMinOrder(new Volume(currencyPair.getBigDecimal("ordermin"), assetPair.getBase()))
        .build();
  }

  @Override
  public LocalDateTime getServerTime() throws ApiException {
    final JSONObject json = this.callEndpoint(Method.TIME, null);
    final long timeInSeconds = json.getLong("unixtime");

    return Utils.secondsToDate(timeInSeconds);
  }

  @Override
  public Ticker getTicker(final AssetPair assetPair) throws ApiException {
    final Map<String, String> input = new HashMap<>();
    input.put("pair", this.getShortPairName(assetPair));

    final JSONObject json = this.callEndpoint(Method.TICKER, input);
    final JSONObject currencyPair = json.getJSONObject(this.getPairName(assetPair));

    return new Ticker.Builder()
        .setCurrency(assetPair.getQuote())
        .setAsk(currencyPair.getJSONArray("a").getBigDecimal(0))
        .setBid(currencyPair.getJSONArray("b").getBigDecimal(0))
        .build();
  }

  @Override
  public List<OHLC> getOHLC(final AssetPair assetPair, final Interval interval, final LocalDateTime since) throws ApiException {
    final Map<String, String> input = new HashMap<>();
    input.put("pair", this.getShortPairName(assetPair));
    input.put("interval", String.valueOf(interval.getMinutes()));
    if (since != null) {
      input.put("since", String.valueOf(Utils.dateToSeconds(since)));
    }

    final JSONObject json = this.callEndpoint(Method.OHLC, input);
    final JSONArray currencyPair = json.getJSONArray(this.getPairName(assetPair));

    final List<OHLC> ohlcList = new ArrayList<>();
    for (int i = 0; i < currencyPair.length(); i++) {
      final JSONArray entry = currencyPair.getJSONArray(i);

      final OHLC ohlc = new OHLC.Builder()
          .setDate(Utils.secondsToDate(entry.getLong(0)))
          .setCurrency(assetPair.getQuote())
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
  public Set<Volume> getPortfolio() throws ApiException {
    final JSONObject json = this.callEndpoint(Method.BALANCE, null);

    final Set<Volume> balances = new HashSet<>();
    for (final Entry<Currency, String> entry : Kraken.CURRENCY_NAMES.entrySet()) {
      if (json.has(entry.getValue())) {
        balances.add(new Volume(json.getBigDecimal(entry.getValue()), entry.getKey()));
      }
    }

    return balances;
  }

  @Override
  public List<Order> getOpen() throws ApiException {
    final JSONObject json = this.callEndpoint(Method.OPEN_ORDERS, null);
    final JSONObject open = json.getJSONObject("open");

    final List<Order> openOrders = new ArrayList<>();

    if ((open != null) && (open.length() > 0)) {
      for (final String id : open.keySet()) {
        openOrders.add(this.fromJSON(id, open.getJSONObject(id)));
      }
    }

    return openOrders;
  }

  @Override
  public List<Order> getClosed(final LocalDateTime since) throws ApiException {
    final Map<String, String> input = new HashMap<>();
    if (since != null) {
      input.put("since", String.valueOf(Utils.dateToSeconds(since)));
    }
    input.put("closetime", "close");

    final JSONObject json = this.callEndpoint(Method.CLOSED_ORDERS, input);
    final JSONObject closed = json.getJSONObject("closed");

    final List<Order> closedOrders = new ArrayList<>();

    if ((closed != null) && (closed.length() > 0)) {
      for (final String id : closed.keySet()) {
        final JSONObject order = closed.getJSONObject(id);

        if (order.getString("status").equals("closed")) {
          closedOrders.add(this.fromJSON(id, order));
        }
      }
    }

    return closedOrders;
  }

  @Override
  public List<String> createMarketOrder(final AssetPair assetPair, final OrderAction action, final Volume volume) throws ApiException {
    String oflags;
    if (volume.getCurrency() == assetPair.getBase()) {
      oflags = "fciq";
    } else if (volume.getCurrency() == assetPair.getQuote()) {
      oflags = "fciq,viqc";
    } else {
      throw new ApiException("Volume currency does not match asset pair");
    }

    final Map<String, String> input = new HashMap<>();
    input.put("pair", this.getShortPairName(assetPair));
    input.put("ordertype", "market");
    input.put("type", action.name().toLowerCase());
    input.put("volume", String.valueOf(volume.getAmount()));
    input.put("oflags", oflags);

    final JSONObject json = this.callEndpoint(Method.ADD_ORDER, input);
    final JSONArray txids = json.getJSONArray("txid");

    final List<String> orders = new ArrayList<>();

    for (int i = 0; i < txids.length(); i++) {
      orders.add(txids.getString(i));
    }

    return orders;
  }

  /**
   * Creates a limit order.
   * 
   * @param assetPair
   *          the pair for which the order should be created
   * @param action
   *          decides if a buy or sell order is created
   * @param volume
   *          the volume of the order in base or quote currency
   * @param price
   *          the price of the order in quote currency
   * @return resulting order ids
   * @throws ApiException
   */
  @Override
  public List<String> createLimitOrder(final AssetPair assetPair, final OrderAction action, final Volume volume, final Price price)
      throws ApiException {
    String oflags;
    if (volume.getCurrency() == assetPair.getBase()) {
      oflags = "fciq";
    } else if (volume.getCurrency() == assetPair.getQuote()) {
      oflags = "fciq,viqc";
    } else {
      throw new ApiException("Volume currency does not match asset pair");
    }

    if (price.getCurrency() != assetPair.getQuote()) {
      throw new ApiException("price currency does not match asset pair");
    }

    final Map<String, String> input = new HashMap<>();
    input.put("pair", this.getShortPairName(assetPair));
    input.put("ordertype", "limit");
    input.put("type", action.name().toLowerCase());
    input.put("price", String.valueOf(price.getAmount()));
    input.put("volume", String.valueOf(volume.getAmount()));
    input.put("oflags", oflags);

    final JSONObject json = this.callEndpoint(Method.ADD_ORDER, input);
    final JSONArray txids = json.getJSONArray("txid");

    final List<String> ids = new ArrayList<>();

    for (int i = 0; i < txids.length(); i++) {
      ids.add(txids.getString(i));
    }

    return ids;
  }

  @Override
  public Order getOrder(final String id) throws ApiException {
    final Map<String, String> input = new HashMap<>();
    input.put("txid", id);

    final JSONObject json = this.callEndpoint(Method.QUERY_ORDERS, input);

    return this.fromJSON(id, json.getJSONObject(id));
  }

  @Override
  public OrderStatus cancelOrder(final String id) throws ApiException {
    final Map<String, String> input = new HashMap<>();
    input.put("txid", id);

    final JSONObject json = this.callEndpoint(Method.CANCEL_ORDER, input);

    OrderStatus status = OrderStatus.UNKNOWN;

    if (json.getInt("count") > 0) {
      status = OrderStatus.CANCELED;
    } else if (json.getBoolean("pending")) {
      status = OrderStatus.PENDING;
    }

    return status;
  }

  private Order fromJSON(final String id, final JSONObject json) throws JSONException, ApiException {
    final JSONObject descr = json.getJSONObject("descr");

    final AssetPair assetPair = this.parseAssetPair(descr.getString("pair"));
    final OrderType orderType = OrderType.valueOf(descr.getString("ordertype").toUpperCase());

    final Currency volumeCurrency = json.getString("oflags").contains("viqc") ? assetPair.getQuote() : assetPair.getBase();

    Order.Builder builder = new Order.Builder()
        .setId(id)
        .setStatus(OrderStatus.valueOf(json.getString("status").toUpperCase()))
        .setPair(assetPair)
        .setAction(OrderAction.valueOf(descr.getString("type").toUpperCase()))
        .setType(orderType)
        .setVolume(new Volume(json.getBigDecimal("vol"), volumeCurrency))
        .setExecVolume(new Volume(json.getBigDecimal("vol_exec"), volumeCurrency));

    if (orderType == OrderType.LIMIT) {
      builder = builder.setPrice(new Price(descr.getBigDecimal("price"), assetPair.getQuote()));
    } else if (json.has("price")) {
      builder = builder.setPrice(new Price(json.getBigDecimal("price"), assetPair.getQuote()));
    }

    final long start = json.getLong("opentm");
    if (start != 0L) {
      builder = builder.setStart(Utils.secondsToDate(start));
    }

    if (json.has("closetm")) {
      final long close = json.getLong("closetm");
      if (close != 0L) {
        builder = builder.setClose(Utils.secondsToDate(close));
      }
    }

    return builder.build();
  }

  private AssetPair parseAssetPair(final String pair) throws ApiException {
    AssetPair assetPair = null;

    loop: for (final Entry<Currency, String> entryBase : Kraken.CURRENCY_NAMES.entrySet()) {
      if (pair.startsWith(entryBase.getValue().substring(1))) {
        final String quote = pair.substring(entryBase.getValue().length() - 1);

        for (final Entry<Currency, String> entryQuote : Kraken.CURRENCY_NAMES.entrySet()) {
          if (entryQuote.getValue().substring(1).equals(quote)) {
            assetPair = AssetPair.fromCurrencies(entryBase.getKey(), entryQuote.getKey());
            break loop;
          }
        }
      }
    }

    if (assetPair == null) {
      throw new ApiException("Unknown asset pair: " + pair);
    }

    return assetPair;
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

    return json.getJSONObject("result");
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
