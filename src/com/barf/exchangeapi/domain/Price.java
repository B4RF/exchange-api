package com.barf.exchangeapi.domain;

import java.math.BigDecimal;

public class Price {

  private final BigDecimal amount;
  private final Currency currency;

  public Price(final BigDecimal amount, final Currency currency) {
    this.amount = amount;
    this.currency = currency;
  }

  public BigDecimal getAmount() {
    return this.amount;
  }

  public Currency getCurrency() {
    return this.currency;
  }

  @Override
  public String toString() {
    return this.amount.toPlainString() + this.currency.toString();
  }
}
