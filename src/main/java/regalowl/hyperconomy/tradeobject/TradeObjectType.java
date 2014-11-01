package regalowl.hyperconomy.tradeobject;


public enum TradeObjectType {
	ITEM, ENCHANTMENT, EXPERIENCE;

	public static TradeObjectType fromString(String type) {
		type = type.toLowerCase();
		if (type == null) {
			return null;
		} else if (type.contains("item")) {
			return TradeObjectType.ITEM;
		} else if (type.contains("enchantment")) {
			return TradeObjectType.ENCHANTMENT;
		} else if (type.contains("experience")) {
			return TradeObjectType.EXPERIENCE;
		} else {
			return null;
		}
	}
	
	public static String getString(TradeObjectType type) {
		switch (type) {
			case ITEM:
				return "item";
			case ENCHANTMENT:
				return "enchantment";
			case EXPERIENCE:
				return "experience";
			default:
				return null;
		}
	}

}
