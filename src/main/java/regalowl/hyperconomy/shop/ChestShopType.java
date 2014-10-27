package regalowl.hyperconomy.shop;

public enum ChestShopType {
	BUY,SELL,TRADE;
	
	public static ChestShopType fromString(String s) {
		if (s.equalsIgnoreCase("[Trade]") || s.equalsIgnoreCase("Trade")) {
			return TRADE;
		} else if (s.equalsIgnoreCase("[Buy]") || s.equalsIgnoreCase("Buy")) {
			return BUY;
		} else if (s.equalsIgnoreCase("[Sell]") || s.equalsIgnoreCase("Sell")) {
			return SELL;
		}
		return null;
	}
}
