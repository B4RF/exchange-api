package com.barf.exchangeapi.domain;

import java.math.BigDecimal;

public class Volume extends Price {

  public Volume(final BigDecimal amount, final Currency currency) {
    super(amount, currency);
  }
}
