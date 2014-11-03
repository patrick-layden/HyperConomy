package regalowl.hyperconomy.inventory;


import java.util.HashMap;
import java.util.List;

import regalowl.databukkit.CommonFunctions;

 

public class HLeatherArmorMeta extends HItemMeta {

	private HColor color;

	
	public HLeatherArmorMeta(String displayName, List<String> lore, List<HEnchantment> enchantments, HColor color) {
		super(displayName, lore, enchantments);
		this.color = color;
	}

	public HLeatherArmorMeta(String serialized) {
		super(serialized);
		HashMap<String,String> data = CommonFunctions.explodeMap(serialized);
		color = new HColor(data.get("color"));
    }

	public String serialize() {
		HashMap<String,String> data = super.getMap();
		data.put("color", color.serialize());
		return CommonFunctions.implodeMap(data);
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