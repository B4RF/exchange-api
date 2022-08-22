package com.barf.exchangeapi.domain;

public enum AssetPair {

  //@formatter:off
  XBTEUR(Currency.XBT, Currency.EUR),
  XDGEUR(Currency.XDG, Currency.EUR);
  //@formatter:on

  public Currency base;
  public Currency quote;

  private AssetPair(final Currency baseCurrency, final Currency quoteCurrency) {
    this.base = baseCurrency;
    this.quote = quoteCurrency;
  }
}
