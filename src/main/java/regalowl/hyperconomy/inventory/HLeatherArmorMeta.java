package regalowl.hyperconomy.inventory;


import java.util.ArrayList;
import java.util.HashMap;

import regalowl.simpledatalib.CommonFunctions;

 

public class HLeatherArmorMeta extends HItemMeta {

	private static final long serialVersionUID = -7590943225453827214L;
	private HColor color;

	
	public HLeatherArmorMeta(String displayName, ArrayList<String> lore, ArrayList<HEnchantment> enchantments, ArrayList<HItemFlag> itemFlags, boolean unbreakable, int repairCost, HColor color) {
		super(displayName, lore, enchantments, itemFlags, unbreakable, repairCost);
		this.color = color;
	}

	public HLeatherArmorMeta(String serialized) {
		super(serialized);
		HashMap<String,String> data = CommonFunctions.explodeMap(serialized);
		color = new HColor(data.get("color"));
    }
	
	public HLeatherArmorMeta(HLeatherArmorMeta meta) {
		super(meta);
		this.color = new HColor(meta.color);
    }

	public String serialize() {
		HashMap<String,String> data = super.getMap();
		data.put("color", color.serialize());
		return CommonFunctions.implodeMap(data);
	}
	
	@Override
	public HItemMetaType getType() {
		return HItemMetaType.LEATHER_ARMOR;
	}

	public HColor getColor() {
		return color;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((color == null) ? 0 : color.hashCode());
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
		HLeatherArmorMeta other = (HLeatherArmorMeta) obj;
		if (color == null) {
			if (other.color != null)
				return false;
		} else if (!color.equals(other.color))
			return false;
		return true;
	}


}