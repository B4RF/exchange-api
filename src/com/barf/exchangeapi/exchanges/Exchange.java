package com.barf.exchangeapi.exchanges;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import com.barf.exchangeapi.domain.Interval;
import com.barf.exchangeapi.domain.OHLC;
import com.barf.exchangeapi.domain.Order;
import com.barf.exchangeapi.domain.Ticker;
import com.barf.exchangeapi.domain.Volume;

public interface Exchange {

  // market data

  public LocalDateTime getServerTime() throws ApiException;

  public Ticker getTicker() throws ApiException;

  public List<OHLC> getOHLC(Interval interval, LocalDateTime since) throws ApiException;

  // user data

  public Set<Volume> getBalance() throws ApiException;

  public List<Order> getOpen() throws ApiException;

  public List<Order> getClosed(LocalDateTime since) throws ApiException;

  public boolean createOrder(Order order) throws ApiException;

  public Order getOrder(String id) throws ApiException;

  public boolean cancelOrder(String id) throws ApiException;

}
