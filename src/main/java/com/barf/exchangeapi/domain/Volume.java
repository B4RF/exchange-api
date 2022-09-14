package com.barf.exchangeapi.domain;

public class Volume extends CurrencyBasedAmount {

  private Volume(final Builder builder) {
    super(builder);
  }

  public static class Builder extends CurrencyBasedAmount.Builder<Volume> {

    @Override
    public Volume build() {
      return new Volume(this);
    }
  }
}
