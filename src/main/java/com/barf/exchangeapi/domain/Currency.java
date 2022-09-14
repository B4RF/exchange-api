package com.barf.exchangeapi.domain;

public enum Currency {

  //@formatter:off
  BTC("\u20bf", "Bitcoin"),
  ETH("\u039E", "Ethereum"),
  DOGE("\u00D0", "Dogecoin"),
  
  EUR("\u20ac", "Euro"),
  USD("\u0024", "US Dollar");
  //@formatter:on

  private final String unicode;
  private final String altName;

  private Currency(final String unicode, final String altName) {
    this.unicode = unicode;
    this.altName = altName;
  }

  @Override
  public String toString() {
    return this.unicode;
  }

  public String getAltName() {
    return this.altName;
  }
}
