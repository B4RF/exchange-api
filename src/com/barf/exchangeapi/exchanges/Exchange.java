package com.barf.exchangeapi.exchanges;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

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

public interface Exchange {

  // ##### static data #####

  /**
   * Returns an object which contains the min order volume and maximum fraction
   * digits of both asset pair currencies.
   * 
   * @param assetPair
   *          the pair for which the info is requested
   * @return the asset pair info
   * @throws ApiException
   */
  public AssetPairInfo getInfo(AssetPair assetPair) throws ApiException;

  // ##### market data #####

  /**
   * Returns the server time of this exchange.
   * 
   * @return server time
   * @throws ApiException
   */
  public LocalDateTime getServerTime() throws ApiException;

  /**
   * Returns a ticker object containing the ask and bid price.
   * 
   * @param assetPair
   *          the pair for which the price is requested
   * @return the ticker info
   * @throws ApiException
   */
  public Ticker getTicker(AssetPair assetPair) throws ApiException;

  /**
   * Returns a list of OHLC objects which contain candle data for historical
   * price movements.
   * 
   * @param assetPair
   *          the pair for which the candles are requested
   * @param interval
   *          the time interval of one candle stick
   * @param since
   *          the date from which the candles should start
   * @return the list of candles
   * @throws ApiException
   */
  public List<OHLC> getOHLC(AssetPair assetPair, Interval interval, LocalDateTime since) throws ApiException;

  // ##### user data #####

  /**
   * Returns the account balance for all supported currencies.
   * 
   * @return a volume object for each currency
   * @throws ApiException
   */
  public Set<Volume> getPortfolio() throws ApiException;

  /**
   * Returns open orders for all asset pairs.
   * 
   * @return a list of open orders
   * @throws ApiException
   */
  public List<Order> getOpen() throws ApiException;

  /**
   * Returns closed orders for all asset pairs.
   * 
   * @param since
   *          the date from which closed orders should be included
   * @return a list of closed orders
   * @throws ApiException
   */
  public List<Order> getClosed(LocalDateTime since) throws ApiException;

  /**
   * Creates a market order.
   * 
   * @param assetPair
   *          the pair for which the order should be created
   * @param action
   *          decides if a buy or sell order is created
   * @param volume
   *          the volume of the order in base or quote currency
   * @return resulting order ids
   * @throws ApiException
   */
  public List<String> createMarketOrder(AssetPair assetPair, OrderAction action, Volume volume) throws ApiException;

  /**
   * Creates a limit order.
   * 
   * @param assetPair
   *          the pair for which the order should be created
   * @param action
   *          decides if a buy or sell order is created
   * @param volume
   *          the volume of the order in base currency
   * @param price
   *          the price of the order in quote currency
   * @return resulting order ids
   * @throws ApiException
   */
  public List<String> createLimitOrder(AssetPair assetPair, OrderAction action, Volume volume, Price price) throws ApiException;

  /**
   * Returns an order object containing all available order info for the given
   * order id.
   * 
   * @param id
   *          the id of the order of interest
   * @return the order information
   * @throws ApiException
   */
  public Order getOrder(String id) throws ApiException;

  /**
   * Cancels the order with the given id.
   * 
   * @param id
   *          the id of the order which should be closed
   * @return the status of the order after the cancel request
   * @throws ApiException
   */
  public OrderStatus cancelOrder(String id) throws ApiException;

}
