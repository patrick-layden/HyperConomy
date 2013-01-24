package regalowl.hyperconomy;

public enum EnchantmentClass {
	WOOD, LEATHER, STONE, CHAINMAIL, IRON, GOLD, DIAMOND, BOW, BOOK, NONE;

	public static EnchantmentClass fromString(String type) {
		type = type.toLowerCase();
		if (type == null) {
			return EnchantmentClass.NONE;
		} else if (type.contains("wood")) {
			return EnchantmentClass.WOOD;
		} else if (type.contains("leather")) {
			return EnchantmentClass.LEATHER;
		} else if (type.contains("stone")) {
			return EnchantmentClass.STONE;
		} else if (type.contains("chainmail")) {
			return EnchantmentClass.CHAINMAIL;
		} else if (type.contains("iron")) {
			return EnchantmentClass.IRON;
		} else if (type.contains("gold")) {
			return EnchantmentClass.GOLD;
		} else if (type.contains("diamond")) {
			return EnchantmentClass.DIAMOND;
		} else if (type.contains("bow")) {
			return EnchantmentClass.BOW;
		} else if (type.contains("book")) {
			return EnchantmentClass.BOOK;
		} else {
			return EnchantmentClass.NONE;
		}
	}
}
