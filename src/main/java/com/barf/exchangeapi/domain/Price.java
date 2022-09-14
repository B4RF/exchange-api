package com.barf.exchangeapi.domain;

public class Price extends CurrencyBasedAmount {

  private Price(final Builder builder) {
    super(builder);
  }

  public static class Builder extends CurrencyBasedAmount.Builder<Price> {

    @Override
    public Price build() {
      return new Price(this);
    }
  }
}
