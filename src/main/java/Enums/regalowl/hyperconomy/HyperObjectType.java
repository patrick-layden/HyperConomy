package regalowl.hyperconomy;


public enum HyperObjectType {
	ITEM, ENCHANTMENT, EXPERIENCE;

	public static HyperObjectType fromString(String type) {
		if (type == null) {
			return null;
		} else if (type.equalsIgnoreCase("item")) {
			return HyperObjectType.ITEM;
		} else if (type.equalsIgnoreCase("enchantment")) {
			return HyperObjectType.ENCHANTMENT;
		} else if (type.equalsIgnoreCase("stock")) {
			return HyperObjectType.EXPERIENCE;
		} else {
			return null;
		}
	}
	
	public static String getString(HyperObjectType type) {
		switch (type) {
			case ITEM:
				return "item";
			case ENCHANTMENT:
				return "enchantment";
			case EXPERIENCE:
				return "experience";
			default:
				return "";
		}
	}
}
