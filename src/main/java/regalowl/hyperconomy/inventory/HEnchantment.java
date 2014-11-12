package regalowl.hyperconomy.inventory;


import java.util.HashMap;

import regalowl.simpledatalib.CommonFunctions;

 

public class HEnchantment {
	private String enchantment;
    private int lvl;
 
	public HEnchantment(String enchantment, int lvl) {
        this.enchantment = enchantment;
        this.lvl = lvl;
    }
	
	public String serialize() {
		HashMap<String,String> data = new HashMap<String,String>();
		data.put("enchantment", enchantment);
		data.put("lvl", lvl+"");
		return CommonFunctions.implodeMap(data);
	}
	
	public HEnchantment(String serialized) {
		HashMap<String,String> data = CommonFunctions.explodeMap(serialized);
		this.enchantment = data.get("enchantment");
		this.lvl = Integer.parseInt(data.get("lvl"));
    }


	public String getEnchantmentName() {
		return enchantment;
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