package regalowl.hyperconomy.inventory;

public class HItemMetaFactory {
	
	public static HItemMeta generate(HItemMetaType type, String data) {
		if (type == null) return null;
		switch (type) {
			case ITEM: return new HItemMeta(data);
			case BOOK: return new HBookMeta(data);
			case ENCHANTMENT_STORAGE: return new HEnchantmentStorageMeta(data);
			case FIREWORK_EFFECT: return new HFireworkEffectMeta(data);
			case FIREWORK: return new HFireworkMeta(data);
			case LEATHER_ARMOR: return new HLeatherArmorMeta(data);
			case MAP: return new HMapMeta(data);
			case POTION: return new HPotionMeta(data);
			case SKULL: return new HSkullMeta(data);
			default: return null;
		}
	}
	
}
