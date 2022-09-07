package com.barf.exchangeapi.domain;

import java.time.LocalDateTime;
import java.util.Objects;

public class Order {

  private final String id;
  private final OrderStatus status;
  private final AssetPair pair;
  private final OrderAction action;
  private final OrderType type;
  private final Volume volume;
  private final Price price;
  private final LocalDateTime start;
  private final LocalDateTime close;
  private final Volume execVolume;

  private Order(final Builder builder) {
    this.id = builder.id;
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

  public String getId() {
    return this.id;
  }

  public OrderStatus getStatus() {
    return this.status;
  }

  public AssetPair getPair() {
    return this.pair;
  }

  public OrderAction getAction() {
    return this.action;
  }

  public OrderType getType() {
    return this.type;
  }

  public Volume getVolume() {
    return this.volume;
  }

  public Price getPrice() {
    return this.price;
  }

  public LocalDateTime getStart() {
    return this.start;
  }

  public LocalDateTime getClose() {
    return this.close;
  }

  public Volume getExecVolume() {
    return this.execVolume;
  }

  @Override
  public int hashCode() {
    return Objects.hash(this.action, this.close, this.execVolume, this.id, this.pair, this.price, this.start, this.status, this.type,
        this.volume);
  }

  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if (!(obj instanceof Order)) {
      return false;
    }
    final Order other = (Order) obj;
    return this.action == other.action && Objects.equals(this.close, other.close) && Objects.equals(this.execVolume, other.execVolume)
        && Objects.equals(this.id, other.id) && this.pair == other.pair && Objects.equals(this.price, other.price)
        && Objects.equals(this.start, other.start)
        && this.status == other.status && this.type == other.type && Objects.equals(this.volume, other.volume);
  }

  public static class Builder {
    private String id;
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

    public Builder setId(final String id) {
      this.id = id;
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
