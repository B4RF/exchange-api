package com.barf.exchangeapi.exchanges.gdax;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import com.barf.exchangeapi.domain.Interval;
import com.barf.exchangeapi.domain.OHLC;
import com.barf.exchangeapi.domain.Order;
import com.barf.exchangeapi.domain.Ticker;
import com.barf.exchangeapi.domain.Volume;
import com.barf.exchangeapi.exchanges.ApiException;
import com.barf.exchangeapi.exchanges.Exchange;

public class GDAX implements Exchange {

  @Override
  public LocalDateTime getServerTime() throws ApiException {
    throw new ApiException("endpoint not implemented");
  }

  @Override
  public Ticker getTicker() throws ApiException {
    throw new ApiException("endpoint not implemented");
  }

  @Override
  public List<OHLC> getOHLC(final Interval interval, final LocalDateTime since) throws ApiException {
    throw new ApiException("endpoint not implemented");
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
  public List<Order> getClosed() throws ApiException {
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

}
