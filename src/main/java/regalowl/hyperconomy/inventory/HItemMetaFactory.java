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
			case BANNER: return new HBannerMeta(data);
			case SPAWN_EGG: return new HSpawnEggMeta(data);
			default: return null;
		}
	}
	
	public static HItemMeta generate(HItemMeta meta) {
		if (meta == null || meta.getType() == null) return null;
		switch (meta.getType()) {
			case ITEM: return new HItemMeta(meta);
			case BOOK: return new HBookMeta((HBookMeta)meta);
			case ENCHANTMENT_STORAGE: return new HEnchantmentStorageMeta(meta);
			case FIREWORK_EFFECT: return new HFireworkEffectMeta((HFireworkEffectMeta)meta);
			case FIREWORK: return new HFireworkMeta((HFireworkMeta)meta);
			case LEATHER_ARMOR: return new HLeatherArmorMeta((HLeatherArmorMeta)meta);
			case MAP: return new HMapMeta((HMapMeta)meta);
			case POTION: return new HPotionMeta((HPotionMeta)meta);
			case SKULL: return new HSkullMeta((HSkullMeta)meta);
			case BANNER: return new HBannerMeta((HBannerMeta)meta);
			case SPAWN_EGG: return new HSpawnEggMeta((HSpawnEggMeta)meta);
			default: return null;
		}
	}
	
}
