package com.barf.exchangeapi.domain;

import java.math.BigDecimal;
import java.util.Objects;

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

  @Override
  public int hashCode() {
    return Objects.hash(this.ask, this.bid);
  }

  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if (!(obj instanceof Ticker)) {
      return false;
    }
    final Ticker other = (Ticker) obj;
    return Objects.equals(this.ask, other.ask) && Objects.equals(this.bid, other.bid);
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
