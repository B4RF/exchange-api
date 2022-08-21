package com.barf.exchangeapi.exchanges.gdax;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Map;

import com.barf.exchangeapi.domain.Currency;
import com.barf.exchangeapi.domain.OHLC;
import com.barf.exchangeapi.domain.Order;
import com.barf.exchangeapi.domain.Ticker;
import com.barf.exchangeapi.exchanges.Exchange;

public class GDAX implements Exchange {

  @Override
  public LocalDateTime getServerTime() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Ticker getTicker() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Collection<OHLC> getOHLC() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Map<Currency, BigDecimal> getBalance() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Collection<Order> getOpen() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Collection<Order> getClosed() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Order getOrder(final String id) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public boolean addOrder(final Order order) {
    // TODO Auto-generated method stub
    return false;
  }

  @Override
  public boolean cancelOrder(final String id) {
    // TODO Auto-generated method stub
    return false;
  }

}
