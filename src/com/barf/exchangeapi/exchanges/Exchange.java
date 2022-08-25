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

  // static data

  public AssetPairInfo getInfo(AssetPair assetPair) throws ApiException;

  // market data

  public LocalDateTime getServerTime() throws ApiException;

  public Ticker getTicker() throws ApiException;

  public List<OHLC> getOHLC(Interval interval, LocalDateTime since) throws ApiException;

  // user data

  public Set<Volume> getBalance() throws ApiException;

  public List<Order> getOpen() throws ApiException;

  public List<Order> getClosed(LocalDateTime since) throws ApiException;

  public List<String> createMarketOrder(OrderAction action, Volume volume) throws ApiException;

  public List<String> createLimitOrder(OrderAction action, Volume volume, Price price) throws ApiException;

  public Order getOrder(String id) throws ApiException;

  public OrderStatus cancelOrder(String id) throws ApiException;

}
