package regalowl.hyperconomy.inventory;


import java.util.ArrayList;
import java.util.HashMap;

import regalowl.databukkit.CommonFunctions;
import regalowl.hyperconomy.HC;
import regalowl.hyperconomy.account.HyperPlayer;
 

public class HItemStack {

	private String material;
    private short durability;
    private byte data;
    private HItemMeta itemMeta;
    private int amount;
    private int maxStackSize;
    private int maxDurability;
    private boolean isBlank;
  
 
    public HItemStack() {
    	this.isBlank = true;
    	this.material = "AIR";
    }
    
    public HItemStack(HItemMeta itemMeta, String material, short durability, byte data, int amount, int maxStackSize, int maxDurability) {
    	this.itemMeta = itemMeta;
    	this.material = material;
    	this.durability = durability;
    	this.data = data;
    	this.amount = amount;
    	this.maxStackSize = maxStackSize;
    	this.maxDurability = maxDurability;
    	this.isBlank = false;
    }
    
    
	public HItemStack(String serialized) {
		HashMap<String,String> data = CommonFunctions.explodeMap(serialized);
    	this.itemMeta = HItemMeta.fromClass(data.get("itemMetaClass"), data.get("itemMetaData"));
    	this.material = data.get("material");
    	this.durability = Short.parseShort(data.get("durability"));
    	this.data = Byte.parseByte(data.get("data"));
    	this.amount = Integer.parseInt(data.get("amount"));
    	this.maxStackSize = Integer.parseInt(data.get("maxStackSize"));
    	this.maxDurability = Integer.parseInt(data.get("maxDurability"));
    	this.isBlank = Boolean.parseBoolean(data.get("isBlank"));
    }
	
	public String serialize() {
		HashMap<String,String> data = new HashMap<String,String>();
		data.put("itemMetaClass", itemMeta.getClass().getSimpleName());
		data.put("itemMetaData", itemMeta.serialize());
		data.put("material", material);
		data.put("durability", durability+"");
		data.put("data", this.data+"");
		data.put("amount", amount+"");
		data.put("maxStackSize", maxStackSize+"");
		data.put("maxDurability", maxDurability+"");
		data.put("isBlank", isBlank+"");
		return CommonFunctions.implodeMap(data);
	}


	public ArrayList<String> displayInfo(HyperPlayer p, String color1, String color2) {
		ArrayList<String> info = new ArrayList<String>();
		info.add(color1 + "Material: " + color2 + material);
		info.add(color1 + "Durability: " + color2 + durability);
		info.add(color1 + "Data: " + color2 + data);
		info.add(color1 + "Amount: " + color2 + amount);
		info.add(color1 + "Max Stack Size: " + color2 + maxStackSize);
		info.add(color1 + "Max Durability: " + color2 + maxDurability);
		if (itemMeta != null) {
			info.addAll(itemMeta.displayInfo(p, color1, color2));
		}
		return info;
	}

	public String getMaterial() {
		return material;
	}
	
	public short getDurability() {
		return durability;
	}

	public byte getData() {
		return data;
	}

	public HItemMeta getItemMeta() {
		return itemMeta;
	}
	
	public int getAmount() {
		return amount;
	}
	
	public double getTrueAmount() {
		return amount * getDurabilityPercent();
	}
	
	public int getMaxStackSize() {
		return maxStackSize;
	}
	
	public int getMaxDurability() {
		return maxDurability;
	}
	
	public void setAmount(int amount) {
		this.amount = amount;
	}
	
	public void setHItemMeta(HItemMeta itemMeta) {
		this.itemMeta = itemMeta;
	}
	
	
	public boolean considerDamage() {
		boolean ignoreDamage = HC.hc.getConf().getBoolean("enable-feature.treat-damaged-items-as-equals-to-undamaged-ones");
		if (!ignoreDamage) return true;
		if (maxDurability > 0) return false;
		return true;
	}
	
	public double getDurabilityPercent() {return (isDurable()) ? (1 - (durability / maxDurability)) : 1.0;}
	public boolean isDurable() {return (maxDurability > 0) ? true : false;}
	public boolean isDamaged() {return (isDurable() && durability > 0) ? true : false;}
	
	public void setBlank() {
		this.isBlank = true;
	}
	public boolean isBlank() {
		return isBlank;
	}
	
	public boolean canEnchantItem() {
		if (material.equalsIgnoreCase("AIR")) return false;
		if (material.equalsIgnoreCase("BOOK")) return true;
		return HC.mc.canEnchantItem(this);
	}
	
	public boolean canAcceptEnchantment(HEnchantment e) {
		if (material.equalsIgnoreCase("AIR")) return false;
		if (material.equalsIgnoreCase("BOOK")) return true;
		if (material.equalsIgnoreCase("ENCHANTED_BOOK")) return false;
		if (amount > 1) return false;
		if (itemMeta != null) {
			for (HEnchantment en:itemMeta.getEnchantments()) {
				if (HC.mc.conflictsWith(e, en)) return false;
			}
		}
		return canEnchantItem();
	}
	
	public boolean containsEnchantment(HEnchantment e) {
		if (itemMeta == null) return false;
		return itemMeta.containsEnchantment(e);
	}
	
	public void addEnchantment(HEnchantment e) {
		if (itemMeta == null) itemMeta = new HItemMeta("", new ArrayList<String>(), new ArrayList<HEnchantment>());
		itemMeta.addEnchantment(e);
	}
	
	public void removeEnchantment(HEnchantment e) {
		if (itemMeta == null) return;
		itemMeta.removeEnchantment(e);
	}
	
	public boolean hasEnchantments() {
		if (itemMeta == null) return false;
		return itemMeta.hasEnchantments();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + amount;
		result = prime * result + data;
		result = prime * result + durability;
		result = prime * result + (isBlank ? 1231 : 1237);
		result = prime * result + ((itemMeta == null) ? 0 : itemMeta.hashCode());
		result = prime * result + ((material == null) ? 0 : material.hashCode());
		result = prime * result + maxDurability;
		result = prime * result + maxStackSize;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		HItemStack other = (HItemStack) obj;
		if (amount != other.amount) return false;
		if (data != other.data) return false;
		if (durability != other.durability) return false;
		if (isBlank != other.isBlank) return false;
		if (itemMeta == null) {
			if (other.itemMeta != null) return false;
		} else if (!itemMeta.equals(other.itemMeta)) return false;
		if (material == null) {
			if (other.material != null) return false;
		} else if (!material.equals(other.material)) return false;
		if (maxDurability != other.maxDurability) return false;
		if (maxStackSize != other.maxStackSize) return false;
		return true;
	}

	public boolean isSimilarTo(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		HItemStack other = (HItemStack) obj;
		if (data != other.data) return false;
		if (durability != other.durability) return false;
		if (isBlank != other.isBlank) return false;
		if (itemMeta == null) {
			if (other.itemMeta != null) return false;
		} else if (!itemMeta.equals(other.itemMeta)) return false;
		if (material == null) {
			if (other.material != null) return false;
		} else if (!material.equals(other.material)) return false;
		if (maxDurability != other.maxDurability) return false;
		if (maxStackSize != other.maxStackSize) return false;
		return true;
	}






}