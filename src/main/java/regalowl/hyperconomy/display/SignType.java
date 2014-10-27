package regalowl.hyperconomy.display;

public enum SignType {
	BUY, SELL, STOCK, VALUE, STATUS, STATICPRICE, STARTPRICE, MEDIAN, HISTORY, TAX, SB, TOTALSTOCK, NONE;

	public static SignType fromString(String type) {
		if (type == null) {
			return SignType.NONE;
		}
		type = type.toLowerCase();
		if (type == null) {
			return null;
		} else if (type.contains("buy")) {
			return SignType.BUY;
		} else if (type.contains("sell")) {
			return SignType.SELL;
		} else if (type.contains("stock")) {
			return SignType.STOCK;
		} else if (type.contains("value")) {
			return SignType.VALUE;
		} else if (type.contains("status")) {
			return SignType.STATUS;
		} else if (type.contains("staticprice")) {
			return SignType.STATICPRICE;
		} else if (type.contains("startprice")) {
			return SignType.STARTPRICE;
		} else if (type.contains("median")) {
			return SignType.MEDIAN;
		} else if (type.contains("history")) {
			return SignType.HISTORY;
		} else if (type.contains("tax")) {
			return SignType.TAX;
		} else if (type.contains("sb")) {
			return SignType.SB;
		} else if (type.contains("totalstock")) {
			return SignType.TOTALSTOCK;
		} else {
			return SignType.NONE;
		}
	}
	
	public static boolean isSignType(String type) {
		SignType t = fromString(type);
		if (t == SignType.NONE) return false;
		if (type.toUpperCase().equalsIgnoreCase(t.toString())) return true;
		return false;
	}
}
