package com.barf.exchangeapi.domain;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class OHLC {

  public final LocalDateTime date;
  public final BigDecimal open;
  public final BigDecimal high;
  public final BigDecimal low;
  public final BigDecimal close;

  private OHLC(final Builder builder) {
    this.date = builder.date;
    this.open = builder.open;
    this.high = builder.high;
    this.low = builder.low;
    this.close = builder.close;
  }

  public static class Builder {
    private LocalDateTime date;
    private BigDecimal open;
    private BigDecimal high;
    private BigDecimal low;
    private BigDecimal close;

    public OHLC build() {
      return new OHLC(this);
    }

    public Builder setDate(final LocalDateTime date) {
      this.date = date;
      return this;
    }

    public Builder setOpen(final BigDecimal open) {
      this.open = open;
      return this;
    }

    public Builder setHigh(final BigDecimal high) {
      this.high = high;
      return this;
    }

    public Builder setLow(final BigDecimal low) {
      this.low = low;
      return this;
    }

    public Builder setClose(final BigDecimal close) {
      this.close = close;
      return this;
    }
  }
}
