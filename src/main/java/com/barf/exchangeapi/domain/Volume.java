package com.barf.exchangeapi.domain;

import java.math.BigDecimal;

public class Volume extends CurrencyBasedAmount {

  public Volume(final BigDecimal amount, final Currency currency) {
    super(amount, currency);
  }
}
