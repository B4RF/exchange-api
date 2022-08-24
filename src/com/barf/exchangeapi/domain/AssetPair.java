package com.barf.exchangeapi.domain;

public enum AssetPair {

  //@formatter:off
  XBTEUR(Currency.XBT, Currency.EUR),
  XDGEUR(Currency.XDG, Currency.EUR);
  //@formatter:on

  private final Currency base;
  private final Currency quote;

  private AssetPair(final Currency baseCurrency, final Currency quoteCurrency) {
    this.base = baseCurrency;
    this.quote = quoteCurrency;
  }

  public Currency getBase() {
    return this.base;
  }

  public Currency getQuote() {
    return this.quote;
  }
}
