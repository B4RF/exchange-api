package com.barf.exchangeapi.exchanges;

public class ApiException extends Exception {
  private static final long serialVersionUID = -7321671253719158268L;

  public ApiException(final String message) {
    super(message);
  }

  public ApiException(final Throwable cause) {
    super(cause);
  }

}
