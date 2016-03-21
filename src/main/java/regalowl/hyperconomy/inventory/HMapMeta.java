package regalowl.hyperconomy.inventory;


import java.util.ArrayList;
import java.util.HashMap;

import regalowl.simpledatalib.CommonFunctions;

 

public class HMapMeta extends HItemMeta {

	private boolean isScaling;

	public HMapMeta(String displayName, ArrayList<String> lore, ArrayList<HEnchantment> enchantments, ArrayList<HItemFlag> itemFlags, boolean isScaling) {
		super(displayName, lore, enchantments, itemFlags);
		this.isScaling = isScaling;
	}

	public HMapMeta(String serialized) {
		super(serialized);
		HashMap<String,String> data = CommonFunctions.explodeMap(serialized);
		isScaling = Boolean.parseBoolean(data.get("isScaling"));
    }

	public String serialize() {
		HashMap<String,String> data = super.getMap();
		data.put("isScaling", isScaling+"");
		return CommonFunctions.implodeMap(data);
	}
	
	@Override
	public HItemMetaType getType() {
		return HItemMetaType.MAP;
	}

	public boolean isScaling() {
		return isScaling;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + (isScaling ? 1231 : 1237);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		HMapMeta other = (HMapMeta) obj;
		if (isScaling != other.isScaling)
			return false;
		return true;
	}
	


}