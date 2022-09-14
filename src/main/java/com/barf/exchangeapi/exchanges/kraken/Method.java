package com.barf.exchangeapi.exchanges.kraken;

/**
 * Represents an API method url.
 *
 */
public enum Method {

  //@formatter:off
  /* Public methods */
  TIME                  (false, "Time"),
  ASSETS                (false, "Assets"),
  ASSET_PAIRS           (false, "AssetPairs"),
  TICKER                (false, "Ticker"),
  OHLC                  (false, "OHLC"),
  DEPTH                 (false, "Depth"),
  TRADES                (false, "Trades"),
  SPREAD                (false, "Spread"),

  /* Private methods */
  BALANCE               (true, "Balance"),
  TRADE_BALANCE         (true, "TradeBalance"),
  OPEN_ORDERS           (true, "OpenOrders"),
  CLOSED_ORDERS         (true, "ClosedOrders"),
  QUERY_ORDERS          (true, "QueryOrders"),
  TRADES_HISTORY        (true, "TradesHistory"),
  QUERY_TRADES          (true, "QueryTrades"),
  OPEN_POSITIONS        (true, "OpenPositions"),
  LEDGERS               (true, "Ledgers"),
  QUERY_LEDGERS         (true, "QueryLedgers"),
  TRADE_VOLUME          (true, "TradeVolume"),
  ADD_ORDER             (true, "AddOrder"),
  CANCEL_ORDER          (true, "CancelOrder"),
  DEPOSIT_METHODS       (true, "DepositMethods"),
  DEPOSIT_ADDRESSES     (true, "DepositAddresses"),
  DEPOSIT_STATUS        (true, "DepositStatus"),
  WITHDRAW_INFO         (true, "WithdrawInfo"),
  WITHDRAW              (true, "Withdraw"),
  WITHDRAW_STATUS       (true, "WithdrawStatus"),
  WITHDRAW_CANCEL       (true, "WithdrawCancel");
  //@formatter:on

  private final boolean isPrivate;
  private final String url;

  Method(final boolean isPrivate, final String url) {
    this.isPrivate = isPrivate;
    this.url = url;
  }

  public boolean isPrivate() {
    return this.isPrivate;
  }

  public String getURL() {
    return this.url;
  }
}
