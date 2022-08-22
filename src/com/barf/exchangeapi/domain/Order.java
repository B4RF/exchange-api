package com.barf.exchangeapi.domain;

import java.time.LocalDateTime;

public class Order {

  public final String txid;
  public final OrderStatus status;
  public final AssetPair pair;
  public final OrderAction action;
  public final OrderType type;
  public final Volume volume;
  public final Price price;
  public final LocalDateTime start;
  public final LocalDateTime close;
  public final Volume execVolume;

  private Order(final Builder builder) {
    this.txid = builder.txid;
    this.status = builder.status;
    this.pair = builder.pair;
    this.action = builder.action;
    this.type = builder.type;
    this.volume = builder.volume;
    this.price = builder.price;
    this.start = builder.start;
    this.close = builder.close;
    this.execVolume = builder.execVolume;
  }

  public static class Builder {
    private String txid;
    private OrderStatus status;
    private AssetPair pair;
    private OrderAction action;
    private OrderType type;
    private Volume volume;
    private Price price;
    private LocalDateTime start;
    private LocalDateTime close;
    private Volume execVolume;

    public Order build() {
      return new Order(this);
    }

    public Builder setId(final String txid) {
      this.txid = txid;
      return this;
    }

    public Builder setStatus(final OrderStatus status) {
      this.status = status;
      return this;
    }

    public Builder setPair(final AssetPair pair) {
      this.pair = pair;
      return this;
    }

    public Builder setAction(final OrderAction action) {
      this.action = action;
      return this;
    }

    public Builder setType(final OrderType type) {
      this.type = type;
      return this;
    }

    public Builder setVolume(final Volume volume) {
      this.volume = volume;
      return this;
    }

    public Builder setPrice(final Price price) {
      this.price = price;
      return this;
    }

    public Builder setStart(final LocalDateTime start) {
      this.start = start;
      return this;
    }

    public Builder setClose(final LocalDateTime close) {
      this.close = close;
      return this;
    }

    public Builder setExecVolume(final Volume execVolume) {
      this.execVolume = execVolume;
      return this;
    }
  }
}
