package com.barf.exchangeapi.domain;

public class AssetPairInfo {

  private final Volume minOrder;
  private final int baseDecimals;
  private final int quoteDecimals;

  private AssetPairInfo(final Builder builder) {
    this.minOrder = builder.minOrder;
    this.baseDecimals = builder.baseDecimals;
    this.quoteDecimals = builder.quoteDecimals;
  }

  /**
   * @return minimum order size
   */
  public Volume getMinOrder() {
    return this.minOrder;
  }

  /**
   * @return the maximum number of decimals which can be used for base currency
   *         amounts
   */
  public int getBaseDecimals() {
    return this.baseDecimals;
  }

  /**
   * @return the maximum number of decimals which can be used for quote currency
   *         amounts
   */
  public int getQuoteDecimals() {
    return this.quoteDecimals;
  }

  public static class Builder {
    private Volume minOrder;
    private int baseDecimals;
    private int quoteDecimals;

    public AssetPairInfo build() {
      return new AssetPairInfo(this);
    }

    public Builder setMinOrder(final Volume minOrder) {
      this.minOrder = minOrder;
      return this;
    }

    public Builder setBaseDecimals(final int decimals) {
      this.baseDecimals = decimals;
      return this;
    }

    public Builder setQuoteDecimals(final int decimals) {
      this.quoteDecimals = decimals;
      return this;
    }
  }
}
