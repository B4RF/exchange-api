package com.barf.exchangeapi.domain;

import java.math.BigDecimal;

public class Ticker {

  private final Price ask;
  private final Price bid;

  private Ticker(final Builder builder) {
    this.ask = new Price(builder.ask, builder.currency);
    this.bid = new Price(builder.bid, builder.currency);
  }

  public Price getAsk() {
    return this.ask;
  }

  public Price getBid() {
    return this.bid;
  }

  public static class Builder {
    private Currency currency;
    private BigDecimal ask;
    private BigDecimal bid;

    public Ticker build() {
      return new Ticker(this);
    }

    public Builder setCurrency(final Currency currency) {
      this.currency = currency;
      return this;
    }

    public Builder setAsk(final BigDecimal ask) {
      this.ask = ask;
      return this;
    }

    public Builder setBid(final BigDecimal bid) {
      this.bid = bid;
      return this;
    }
  }
}
