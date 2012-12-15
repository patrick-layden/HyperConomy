package regalowl.hyperconomy;

public enum EnchantmentClass {
	WOOD, LEATHER, STONE, CHAINMAIL, IRON, GOLD, DIAMOND, BOW, NONE;
	
	public static EnchantmentClass fromString(String type) {
		if (type == null) {
			return EnchantmentClass.NONE;
		} else if (type.equalsIgnoreCase("wood")) {
			return EnchantmentClass.WOOD;
		} else if (type.equalsIgnoreCase("leather")) {
			return EnchantmentClass.LEATHER;
		} else if (type.equalsIgnoreCase("stone")) {
			return EnchantmentClass.STONE;
		} else if (type.equalsIgnoreCase("chainmail")) {
			return EnchantmentClass.CHAINMAIL;
		} else if (type.equalsIgnoreCase("iron")) {
			return EnchantmentClass.IRON;
		} else if (type.equalsIgnoreCase("gold")) {
			return EnchantmentClass.GOLD;
		} else if (type.equalsIgnoreCase("diamond")) {
			return EnchantmentClass.DIAMOND;
		} else if (type.equalsIgnoreCase("bow")) {
			return EnchantmentClass.BOW;
		} else {
			return EnchantmentClass.NONE;
		}
	}
}
