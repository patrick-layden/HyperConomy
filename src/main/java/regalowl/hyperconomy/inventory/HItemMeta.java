package regalowl.hyperconomy.inventory;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import regalowl.simpledatalib.CommonFunctions;
import regalowl.hyperconomy.account.HyperPlayer;
 

public class HItemMeta {

	
	protected String displayName;
	protected ArrayList<String> lore = new ArrayList<String>();
	protected ArrayList<HEnchantment> enchantments = new ArrayList<HEnchantment>();
	protected ArrayList<HItemFlag> itemFlags = new ArrayList<HItemFlag>();
 

	public HItemMeta(String displayName, ArrayList<String> lore, ArrayList<HEnchantment> enchantments, ArrayList<HItemFlag> itemFlags) {
        this.displayName = displayName;
        this.lore = lore;
        this.enchantments = enchantments;
        this.itemFlags = itemFlags;
    }
	
	public HItemMeta(HItemMeta meta) {
        this.displayName = meta.displayName;
        this.lore = meta.lore;
        this.enchantments = meta.enchantments;
        this.itemFlags = meta.itemFlags;
    }
	
	public HItemMeta(String serialized) {
		HashMap<String,String> data = CommonFunctions.explodeMap(serialized);
		this.displayName = data.get("displayName");
		this.lore = CommonFunctions.explode(data.get("lore"));
		ArrayList<String> sEnchants = CommonFunctions.explode(data.get("enchantments"));
		for (String e:sEnchants) {
			enchantments.add(new HEnchantment(e));
		}
		ArrayList<String> sItemFlags = CommonFunctions.explode(data.get("itemFlags"));
		for (String f:sItemFlags) {
			itemFlags.add(HItemFlag.deserialize(f));
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
		ArrayList<String> sItemFlags = new ArrayList<String>();
		for (HItemFlag f:itemFlags) {
			sItemFlags.add(f.serialize());
		}
		data.put("itemFlags", CommonFunctions.implode(sItemFlags));
		return data;
	}

	
	public ArrayList<String> displayInfo(HyperPlayer p, String color1, String color2) {
		ArrayList<String> info = new ArrayList<String>();
		info.add(color1 + "Meta Type: " + color2 + getType().toString());
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
		String itemFlagString = "";
		if (itemFlags != null && itemFlags.size() > 0) {
			for(HItemFlag f:itemFlags) {
				itemFlagString += f.getItemFlag() + ",";
			}
			itemFlagString = itemFlagString.substring(0, itemFlagString.length() - 1);
		}
		info.add(color1 + "Item flags: " + color2 + itemFlagString);
		return info;
	}

	public HItemMetaType getType() {
		return HItemMetaType.ITEM;
	}

	
	public String getDisplayName() {
		return displayName;
	}
	public void setDisplayName(String name) {
		this.displayName = name;
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
		if (!containsEnchantment(e)) enchantments.add(e);
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
	
	
	public List<HItemFlag> getItemFlags() {
		return itemFlags;
	}
	public boolean hasItemFlags() {
		if (itemFlags.size() > 0) return true;
		return false;
	}
	public void addItemFlag(HItemFlag f) {
		if (!containsItemFlag(f)) itemFlags.add(f);
	}
	public void removeItemFlag(HItemFlag f) {
		if (containsItemFlag(f)) itemFlags.remove(f);
	}
	public boolean containsItemFlag(HItemFlag f) {
		for (HItemFlag fl:itemFlags) {
			if (fl.equals(f)) return true;
		}
		return false;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((displayName == null) ? 0 : displayName.hashCode());
		result = prime * result + ((enchantments == null) ? 0 : enchantments.hashCode());
		result = prime * result + ((itemFlags == null) ? 0 : itemFlags.hashCode());
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
		} else if (!displayName.equals(other.displayName))
			return false;
		if (enchantments == null) {
			if (other.enchantments != null)
				return false;
		} else if (!enchantments.equals(other.enchantments))
			return false;
		if (itemFlags == null) {
			if (other.itemFlags != null)
				return false;
		} else if (!itemFlags.equals(other.itemFlags))
			return false;
		if (lore == null) {
			if (other.lore != null)
				return false;
		} else if (!lore.equals(other.lore))
			return false;
		return true;
	}
	
	




}