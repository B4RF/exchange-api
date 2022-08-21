package com.barf.exchangeapi.domain;

public enum Currency {
  EUR("\u20ac"), XBT("\u20bf");

  String unicode;

  private Currency(final String unicode) {
    this.unicode = unicode;
  }

  @Override
  public String toString() {
    return this.unicode;
  }
}
