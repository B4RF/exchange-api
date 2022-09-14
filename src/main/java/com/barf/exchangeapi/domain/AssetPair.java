package com.barf.exchangeapi.domain;

public enum AssetPair {

  //@formatter:off
  BTC_EUR(Currency.BTC, Currency.EUR),
  BTC_USD(Currency.BTC, Currency.USD),

  ETH_EUR(Currency.ETH, Currency.EUR),
  ETH_USD(Currency.ETH, Currency.USD),

  DOGE_EUR(Currency.DOGE, Currency.EUR),
  DOGE_USD(Currency.DOGE, Currency.USD);
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

  public static AssetPair fromCurrencies(final Currency baseCurrency, final Currency quoteCurrency) {
    for (final AssetPair assetPair : AssetPair.values()) {
      if (assetPair.base == baseCurrency && assetPair.quote == quoteCurrency) {
        return assetPair;
      }
    }

    return null;
  }
}
