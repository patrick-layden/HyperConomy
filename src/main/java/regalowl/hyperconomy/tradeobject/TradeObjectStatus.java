package regalowl.hyperconomy.tradeobject;

public enum TradeObjectStatus {
	TRADE, BUY, SELL, NONE;

	public static TradeObjectStatus fromString(String type) {
		type = type.toLowerCase();
		if (type.contains("trade")) {
			return TradeObjectStatus.TRADE;
		} else if (type.contains("buy")) {
			return TradeObjectStatus.BUY;
		} else if (type.contains("sell")) {
			return TradeObjectStatus.SELL;
		} else {
			return TradeObjectStatus.NONE;
		}

	}
}