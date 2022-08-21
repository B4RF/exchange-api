package com.barf.exchangeapi.exchanges;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Map;

import com.barf.exchangeapi.domain.Currency;
import com.barf.exchangeapi.domain.OHLC;
import com.barf.exchangeapi.domain.Order;
import com.barf.exchangeapi.domain.Ticker;

public interface Exchange {

  // market data

  public LocalDateTime getServerTime() throws ApiException;

  public Ticker getTicker() throws ApiException;

  public Collection<OHLC> getOHLC() throws ApiException;

  // user data

  public Map<Currency, BigDecimal> getBalance() throws ApiException;

  public Collection<Order> getOpen() throws ApiException;

  public Collection<Order> getClosed() throws ApiException;

  public boolean createOrder(Order order) throws ApiException;

  public Order getOrder(String id) throws ApiException;

  public boolean cancelOrder(String id) throws ApiException;

}
