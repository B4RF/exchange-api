# exchange-api
Java library which allows to easily interact with the REST API of **Kraken** and **Coinbase Pro**.  
The API supports market entpoints like OHLC data and user endpoints like creating orders.  
Currently only the following asset pairs are supported: BTC/EUR, BTC/USD, DOGE/EUR, DOGE/USD, ETH/EUR and ETH/USD.

## Preparation
You will have to generate an Api key in your preferred exchange:
- Kraken: https://support.kraken.com/hc/en-us/articles/360000919966-How-to-generate-an-API-key-pair-
- Coinbase Pro: https://help.coinbase.com/en/pro/other-topics/api/how-do-i-create-an-api-key-for-coinbase-pro

## Example usage
```Java
// Create an exchange object
Exchange exchange = new Kraken("Your Key", "Your Secret");
exchange = new Coinbase("Your Key", "Your Secret", "Your Passphrase");

// Market data - ticker info
Ticker ticker = exchange.getTicker(AssetPair.BTC_EUR);
Price bid = ticker.getBid();
Price ask = ticker.getAsk();

// User data - buy 1 BTC at 10000 EUR
List<String> orderIds = exchange.createLimitOrder(AssetPair.BTC_EUR, OrderAction.BUY, new Volume(BigDecimal.ONE, Currency.BTC), new Price(new BigDecimal("10000"), Currency.EUR));

```

### Donations
BTC - bc1q5f9hfua55cctpre7aswea2dsupdmcqa2d8mhek
