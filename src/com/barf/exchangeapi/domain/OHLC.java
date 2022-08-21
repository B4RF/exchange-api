package com.barf.exchangeapi.domain;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class OHLC {

  public final LocalDateTime date;
  public final Price open;
  public final Price high;
  public final Price low;
  public final Price close;

  private OHLC(final Builder builder) {
    this.date = builder.date;
    this.open = new Price(builder.open, builder.currency);
    this.high = new Price(builder.high, builder.currency);
    this.low = new Price(builder.low, builder.currency);
    this.close = new Price(builder.close, builder.currency);
  }

  public static class Builder {
    private LocalDateTime date;
    private Currency currency;
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

    public Builder setCurrency(final Currency currency) {
      this.currency = currency;
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
