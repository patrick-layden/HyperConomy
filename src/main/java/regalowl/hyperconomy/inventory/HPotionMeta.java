package regalowl.hyperconomy.inventory;


import java.util.ArrayList;
import java.util.HashMap;

import regalowl.databukkit.CommonFunctions;
 

public class HPotionMeta extends HItemMeta {

	private ArrayList<HPotionEffect> potionEffects = new ArrayList<HPotionEffect>();

	public HPotionMeta(String displayName, ArrayList<String> lore, ArrayList<HEnchantment> enchantments, ArrayList<HPotionEffect> potionEffects) {
		super(displayName, lore, enchantments);
		this.potionEffects = potionEffects;
	}

	public HPotionMeta(String serialized) {
		super(serialized);
		HashMap<String,String> data = CommonFunctions.explodeMap(serialized);
		ArrayList<String> sEffects = CommonFunctions.explode(data.get("potionEffects"));
		for (String e:sEffects) {
			potionEffects.add(new HPotionEffect(e));
		}
    }

	public String serialize() {
		HashMap<String,String> data = super.getMap();
		ArrayList<String> sEffects = new ArrayList<String>();
		for (HPotionEffect e:potionEffects) {
			sEffects.add(e.serialize());
		}
		data.put("potionEffects", CommonFunctions.implode(sEffects));
		return CommonFunctions.implodeMap(data);
	}
	
	@Override
	public HItemMetaType getType() {
		return HItemMetaType.POTION;
	}
	
	public ArrayList<HPotionEffect> getPotionEffects() {
		return potionEffects;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((potionEffects == null) ? 0 : potionEffects.hashCode());
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
		HPotionMeta other = (HPotionMeta) obj;
		if (potionEffects == null) {
			if (other.potionEffects != null)
				return false;
		} else if (!potionEffects.equals(other.potionEffects))
			return false;
		return true;
	}

	
	

}