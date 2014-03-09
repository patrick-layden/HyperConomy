package regalowl.hyperconomy;

public enum EnchantmentClass {
	WOOD, LEATHER, STONE, CHAINMAIL, IRON, GOLD, DIAMOND, BOW, BOOK, NONE;

	public static EnchantmentClass fromString(String type) {
		
		if (type == null) {
			return EnchantmentClass.NONE;
		}
		type = type.toLowerCase();
		if (type.contains("wood")) {
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
	
	public static double getclassValue(EnchantmentClass eclass) {
		try {
			HyperConomy hc = HyperConomy.hc;
			if (eclass.equals(EnchantmentClass.LEATHER)) {
				return hc.gYH().gFC("config").getDouble("config.enchantment.classvalue.leather");
			} else if (eclass.equals(EnchantmentClass.WOOD)) {
				return hc.gYH().gFC("config").getDouble("config.enchantment.classvalue.wood");
			} else if (eclass.equals(EnchantmentClass.STONE)) {
				return hc.gYH().gFC("config").getDouble("config.enchantment.classvalue.stone");
			} else if (eclass.equals(EnchantmentClass.CHAINMAIL)) {
				return hc.gYH().gFC("config").getDouble("config.enchantment.classvalue.chainmail");
			} else if (eclass.equals(EnchantmentClass.IRON)) {
				return hc.gYH().gFC("config").getDouble("config.enchantment.classvalue.iron");
			} else if (eclass.equals(EnchantmentClass.GOLD)) {
				return hc.gYH().gFC("config").getDouble("config.enchantment.classvalue.gold");
			} else if (eclass.equals(EnchantmentClass.DIAMOND)) {
				return hc.gYH().gFC("config").getDouble("config.enchantment.classvalue.diamond");
			} else if (eclass.equals(EnchantmentClass.BOOK)) {
				return hc.gYH().gFC("config").getDouble("config.enchantment.classvalue.book");
			} else if (eclass.equals(EnchantmentClass.BOW)) {
				return hc.gYH().gFC("config").getDouble("config.enchantment.classvalue.bow");
			} else {
				return hc.gYH().gFC("config").getDouble("config.enchantment.classvalue.diamond");
			}
		} catch (Exception e) {
			String info = "getclassValue() passed values eclass='" + eclass.toString() + "'";
			HyperConomy.hc.gDB().writeError(e, info);
			return 0;
		}
	}
}
