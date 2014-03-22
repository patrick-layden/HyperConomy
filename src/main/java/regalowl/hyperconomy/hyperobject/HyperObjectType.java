package regalowl.hyperconomy.hyperobject;


public enum HyperObjectType {
	ITEM, ENCHANTMENT, EXPERIENCE;

	public static HyperObjectType fromString(String type) {
		type = type.toLowerCase();
		if (type == null) {
			return null;
		} else if (type.contains("item")) {
			return HyperObjectType.ITEM;
		} else if (type.contains("enchantment")) {
			return HyperObjectType.ENCHANTMENT;
		} else if (type.contains("experience")) {
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
				return null;
		}
	}

}
