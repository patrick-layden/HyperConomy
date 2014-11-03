package regalowl.hyperconomy.inventory;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import regalowl.databukkit.CommonFunctions;
import regalowl.hyperconomy.HC;
import regalowl.hyperconomy.account.HyperPlayer;
 

public class HItemMeta {

	
	protected String displayName;
	protected List<String> lore = new ArrayList<String>();
	protected List<HEnchantment> enchantments = new ArrayList<HEnchantment>();
 

	public HItemMeta(String displayName, List<String> lore, List<HEnchantment> enchantments) {
        this.displayName = displayName;
        this.lore = lore;
        this.enchantments = enchantments;
    }
	
	public HItemMeta(String serialized) {
		HashMap<String,String> data = CommonFunctions.explodeMap(serialized);
		this.displayName = data.get("displayName");
		this.lore = CommonFunctions.explode(data.get("lore"));
		ArrayList<String> sEnchants = CommonFunctions.explode(data.get("enchantments"));
		for (String e:sEnchants) {
			enchantments.add(new HEnchantment(e));
		}
    }

	public String serialize() {
		return CommonFunctions.implodeMap(getMap());
	}
	
	public HashMap<String,String> getMap() {
		HashMap<String,String> data = new HashMap<String,String>();
		data.put("displayName", displayName);
		data.put("lore", CommonFunctions.implode(lore));
		ArrayList<String> sEnchants = new ArrayList<String>();
		for (HEnchantment e:enchantments) {
			sEnchants.add(e.serialize());
		}
		data.put("enchantments", CommonFunctions.implode(sEnchants));
		return data;
	}


	
	public ArrayList<String> displayInfo(HyperPlayer p, String color1, String color2) {
		ArrayList<String> info = new ArrayList<String>();
		info.add(color1 + "Display Name: " + color2 + displayName);
		String loreString = "";
		if (lore != null && lore.size() > 0) {
			for(String l:lore) {
				loreString += l + ",";
			}
			loreString = loreString.substring(0, loreString.length() - 1);
		}
		info.add(color1 + "Lore: " + color2 + loreString);
		String enchantString = "";
		if (enchantments != null && enchantments.size() > 0) {
			for(HEnchantment se:enchantments) {
				enchantString += se.getEnchantmentName() + ",";
			}
			enchantString = enchantString.substring(0, enchantString.length() - 1);
		}
		info.add(color1 + "Enchantments: " + color2 + enchantString);
		return info;
	}
	

	public String getDisplayName() {
		return displayName;
	}

	public List<String> getLore() {
		return lore;
	}
	
	public List<HEnchantment> getEnchantments() {
		return enchantments;
	}
	
	public boolean hasEnchantments() {
		if (enchantments.size() > 0) return true;
		return false;
	}
	
	public void addEnchantment(HEnchantment e) {
		enchantments.add(e);
	}
	
	public void removeEnchantment(HEnchantment e) {
		if (containsEnchantment(e)) enchantments.remove(e);
	}

	
	public boolean containsEnchantment(HEnchantment e) {
		for (HEnchantment se:enchantments) {
			if (se.equals(e)) return true;
		}
		return false;
	}
	
	
	public static HItemMeta fromClass(String s, String data) {
		if (s == null || data == null) return null;
		if (s.equalsIgnoreCase("HItemMeta")) {
			return new HItemMeta(data);
		} else if (s.equalsIgnoreCase("HBookMeta")) {
			return new HBookMeta(data);
		} else if (s.equalsIgnoreCase("HEnchantmentStorageMeta")) {
			return new HEnchantmentStorageMeta(data);
		} else if (s.equalsIgnoreCase("HFireworkEffectMeta")) {
			return new HFireworkEffectMeta(data);
		} else if (s.equalsIgnoreCase("HFireworkMeta")) {
			return new HFireworkMeta(data);
		} else if (s.equalsIgnoreCase("HLeatherArmorMeta")) {
			return new HLeatherArmorMeta(data);
		} else if (s.equalsIgnoreCase("HMapMeta")) {
			return new HMapMeta(data);
		} else if (s.equalsIgnoreCase("HPotionMeta")) {
			return new HPotionMeta(data);
		} else if (s.equalsIgnoreCase("HSkullMeta")) {
			return new HSkullMeta(data);
		} else {
			return null;
		}
	}
	
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((displayName == null) ? 0 : displayName.hashCode());
		result = prime * result + ((enchantments == null) ? 0 : enchantments.hashCode());
		result = prime * result + ((lore == null) ? 0 : lore.hashCode());
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
		HItemMeta other = (HItemMeta) obj;
		if (displayName == null) {
			if (other.displayName != null)
				return false;
		} else if (!displayName.equals(other.displayName)) {
			return false;
		}
		if (enchantments == null) {
			if (other.enchantments != null)
				return false;
		} else if (!enchantments.equals(other.enchantments)) {
			return false;
		}
		if (lore == null) {
			if (other.lore != null)
				return false;
		} else if (!lore.equals(other.lore))
			return false;
		return true;
	}

}