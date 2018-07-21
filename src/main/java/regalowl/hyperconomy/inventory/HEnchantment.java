package regalowl.hyperconomy.inventory;


import java.util.HashMap;

import org.bukkit.NamespacedKey;

import regalowl.simpledatalib.CommonFunctions;

 

public class HEnchantment {
	private NamespacedKey enchantment;
    private int lvl;
 
	public HEnchantment(String enchantment, int lvl) {
		this.enchantment = NamespacedKey.minecraft(enchantment);
        this.lvl = lvl;
    }
	
	public HEnchantment(HEnchantment he) {
        this.enchantment = he.enchantment;
        this.lvl = he.lvl;
    }
	
	public String serialize() {
		HashMap<String,String> data = new HashMap<String,String>();
		data.put("enchantment", enchantment.getKey());
		data.put("lvl", lvl+"");
		return CommonFunctions.implodeMap(data);
	}
	
	public HEnchantment(String serialized) {
		HashMap<String,String> data = CommonFunctions.explodeMap(serialized);
		this.enchantment = NamespacedKey.minecraft(data.get("enchantment"));
		this.lvl = Integer.parseInt(data.get("lvl"));
    }


	public NamespacedKey getEnchantmentKey() {
		return enchantment;
	}

	public String getEnchantmentKeyString() {
		return enchantment.getKey();
	}

	public int getLvl() {
		return lvl;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((enchantment == null) ? 0 : enchantment.hashCode());
		result = prime * result + lvl;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		HEnchantment other = (HEnchantment) obj;
		if (enchantment == null) {
			if (other.enchantment != null)
				return false;
		} else if (!enchantment.equals(other.enchantment))
			return false;
		if (lvl != other.lvl)
			return false;
		return true;
	}

}