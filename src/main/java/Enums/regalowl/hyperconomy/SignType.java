package regalowl.hyperconomy;

public enum SignType {
	BUY, SELL, STOCK, VALUE, STATUS, STATICPRICE, STARTPRICE, MEDIAN, HISTORY, TAX, SB;

	public static SignType fromString(String type) {
		if (type == null) {
			return null;
		} else if (type.equalsIgnoreCase("buy")) {
			return SignType.BUY;
		} else if (type.equalsIgnoreCase("sell")) {
			return SignType.SELL;
		} else if (type.equalsIgnoreCase("stock")) {
			return SignType.STOCK;
		} else if (type.equalsIgnoreCase("value")) {
			return SignType.VALUE;
		} else if (type.equalsIgnoreCase("status")) {
			return SignType.STATUS;
		} else if (type.equalsIgnoreCase("staticprice")) {
			return SignType.STATICPRICE;
		} else if (type.equalsIgnoreCase("startprice")) {
			return SignType.STARTPRICE;
		} else if (type.equalsIgnoreCase("median")) {
			return SignType.MEDIAN;
		} else if (type.equalsIgnoreCase("history")) {
			return SignType.HISTORY;
		} else if (type.equalsIgnoreCase("tax")) {
			return SignType.TAX;
		} else if (type.equalsIgnoreCase("sb")) {
			return SignType.SB;
		} else {
			return null;
		}
	}
}
