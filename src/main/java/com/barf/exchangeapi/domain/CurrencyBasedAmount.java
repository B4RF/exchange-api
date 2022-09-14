package com.barf.exchangeapi.domain;

import java.math.BigDecimal;
import java.util.Objects;

public abstract class CurrencyBasedAmount {

  private final BigDecimal amount;
  private final Currency currency;

  protected CurrencyBasedAmount(final Builder<? extends CurrencyBasedAmount> builder) {
    this.amount = builder.amount;
    this.currency = builder.currency;
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
    return Objects.equals(this.amount, other.getAmount()) && this.currency == other.getCurrency();
  }

  @Override
  public String toString() {
    return this.amount.stripTrailingZeros().toPlainString() + this.currency.toString();
  }

  public static abstract class Builder<T extends CurrencyBasedAmount> {
    private BigDecimal amount;
    private Currency currency;

    public abstract T build();

    public Builder<T> setAmount(final String amount) {
      this.amount = new BigDecimal(amount);
      return this;
    }

    public Builder<T> setAmount(final BigDecimal amount) {
      this.amount = amount;
      return this;
    }

    public Builder<T> setCurrency(final Currency currency) {
      this.currency = currency;
      return this;
    }
  }
}
