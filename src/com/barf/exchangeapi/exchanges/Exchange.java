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

  public LocalDateTime getServerTime();

  public Ticker getTicker();

  public Collection<OHLC> getOHLC();

  // user data

  public Map<Currency, BigDecimal> getBalance();

  public Collection<Order> getOpen();

  public Collection<Order> getClosed();

  public Order getOrder(String id);

  public boolean addOrder(Order order);

  public boolean cancelOrder(String id);

}
