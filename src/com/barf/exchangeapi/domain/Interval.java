package com.barf.exchangeapi.domain;

public enum Interval {

  MINUTE(1), HOUR(60), DAY(1440), WEEK(10080);

  public int minutes;

  private Interval(final int minutes) {
    this.minutes = minutes;
  }
}
