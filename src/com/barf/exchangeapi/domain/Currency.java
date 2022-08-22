package com.barf.exchangeapi.domain;

public enum Currency {

  //@formatter:off
  EUR("\u20ac"),
  XBT("\u20bf"),
  XDG("\u00D0");
  //@formatter:on

  String unicode;

  private Currency(final String unicode) {
    this.unicode = unicode;
  }

  @Override
  public String toString() {
    return this.unicode;
  }
}
