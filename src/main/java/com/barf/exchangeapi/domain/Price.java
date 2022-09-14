package com.barf.exchangeapi.domain;

import java.math.BigDecimal;

public class Price extends CurrencyBasedAmount {

  public Price(final BigDecimal amount, final Currency currency) {
    super(amount, currency);
  }

}
