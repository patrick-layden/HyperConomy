package regalowl.hyperconomy.inventory;


import java.util.ArrayList;

 

public class HEnchantmentStorageMeta extends HItemMeta {

	public HEnchantmentStorageMeta(String displayName, ArrayList<String> lore, ArrayList<HItemFlag> itemFlags, ArrayList<HEnchantment> enchantments) {
		super(displayName, lore, enchantments, itemFlags);
	}
	public HEnchantmentStorageMeta(HItemMeta meta) {
		super(meta.displayName, meta.lore, meta.enchantments, meta.itemFlags);
	}
	public HEnchantmentStorageMeta(String serialized) {
		super(serialized);
    }
	@Override
	public HItemMetaType getType() {
		return HItemMetaType.ENCHANTMENT_STORAGE;
	}

}