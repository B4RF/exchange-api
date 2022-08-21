package com.barf.exchangeapi.domain;

import java.math.BigDecimal;

public class Ticker {

  public final BigDecimal ask;
  public final BigDecimal bid;

  public Ticker(final BigDecimal ask, final BigDecimal bid) {
    this.ask = ask;
    this.bid = bid;
  }

}
