package com.barf.exchangeapi.domain;

import java.math.BigDecimal;
import java.util.Objects;

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
  public int hashCode() {
    return Objects.hash(this.amount, this.currency);
  }

  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if (!(obj instanceof Price)) {
      return false;
    }
    final Price other = (Price) obj;
    return Objects.equals(this.amount, other.amount) && this.currency == other.currency;
  }

  @Override
  public String toString() {
    return this.amount.stripTrailingZeros().toPlainString() + this.currency.toString();
  }
}
