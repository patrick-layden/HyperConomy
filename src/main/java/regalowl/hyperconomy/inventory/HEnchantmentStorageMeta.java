package regalowl.hyperconomy.inventory;


import java.util.ArrayList;

 

public class HEnchantmentStorageMeta extends HItemMeta {

	public HEnchantmentStorageMeta(String displayName, ArrayList<String> lore, ArrayList<HItemFlag> itemFlags, boolean unbreakable, int repairCost, ArrayList<HEnchantment> enchantments) {
		super(displayName, lore, enchantments, itemFlags, unbreakable, repairCost);
	}
	
	public HEnchantmentStorageMeta(HItemMeta meta) {
		super(meta);
	}
	
	public HEnchantmentStorageMeta(String serialized) {
		super(serialized);
    }
	@Override
	public HItemMetaType getType() {
		return HItemMetaType.ENCHANTMENT_STORAGE;
	}

}