package com.barf.exchangeapi.domain;

public enum CurrencyPair {

  XBTEUR(Currency.XBT, Currency.EUR);

  public Currency base;
  public Currency quote;

  private CurrencyPair(final Currency baseCurrency, final Currency quoteCurrency) {
    this.base = baseCurrency;
    this.quote = quoteCurrency;
  }
}
