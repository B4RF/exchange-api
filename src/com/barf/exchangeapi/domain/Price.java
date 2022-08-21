package com.barf.exchangeapi.domain;

import java.math.BigDecimal;

public class Price {

  public final BigDecimal amount;
  public final Currency currency;

  public Price(final BigDecimal amount, final Currency currency) {
    this.amount = amount;
    this.currency = currency;
  }

  @Override
  public String toString() {
    return this.amount.toPlainString() + this.currency.unicode;
  }
}
