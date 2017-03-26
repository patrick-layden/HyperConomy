package regalowl.hyperconomy.inventory;


import java.util.ArrayList;
import java.util.HashMap;

import regalowl.simpledatalib.CommonFunctions;
import regalowl.hyperconomy.HyperConomy;
import regalowl.hyperconomy.account.HyperPlayer;
import regalowl.hyperconomy.tradeobject.StackComparisonData;
 

public class HItemStack {

	private transient HyperConomy hc;
	
	private String material;
    private short durability;
    private byte data;
    private HItemMeta itemMeta;
    private int amount;
    private int maxStackSize;
    private int maxDurability;
    private boolean isBlank;
  
 
    public HItemStack(HyperConomy hc) {
    	this.hc = hc;
    	this.isBlank = true;
    	this.material = "AIR";
    }
    
    public HItemStack(HyperConomy hc, HItemMeta itemMeta, String material, short durability, byte data, int amount, int maxStackSize, int maxDurability) {
    	this.hc = hc;
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
    	this.itemMeta = HItemMetaFactory.generate(HItemMetaType.fromString(data.get("metaType")), data.get("itemMetaData"));
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
		data.put("metaType", itemMeta.getType().toString());
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

	/**
	 * Makes a deep clone of the given HItemStack
	 * @param serialized
	 */
	public HItemStack(HItemStack stack) {
    	this.itemMeta = HItemMetaFactory.generate(stack.itemMeta);
    	this.material = stack.material;
    	this.durability = stack.durability;
    	this.data = stack.data;
    	this.amount = stack.amount;
    	this.maxStackSize = stack.maxStackSize;
    	this.maxDurability = stack.maxDurability;
    	this.isBlank = stack.isBlank;
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
	public void setMaterial(String material) {
		this.material = material;
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
		boolean ignoreDamage = hc.getConf().getBoolean("enable-feature.treat-damaged-items-as-equals-to-undamaged-ones");
		if (!ignoreDamage) return true;
		if (maxDurability > 0) return false;
		return true;
	}
	
	public double getDurabilityPercent() {return (isDurable()) ? (1.0 - ((double)durability / (double)maxDurability)) : 1.0;}
	public boolean isDurable() {return (maxDurability > 0) ? true : false;}
	public boolean isDamaged() {return (isDurable() && durability > 0) ? true : false;}
	public byte getComparisonData() {return (isDamaged()) ? 0:data;}
	public short getComparisonDurability() {return (isDamaged()) ? 0:durability;}
	
	public void setBlank() {
		this.isBlank = true;
	}
	public boolean isBlank() {
		return isBlank;
	}
	
	public boolean canEnchantItem() {
		if (material.equalsIgnoreCase("AIR")) return false;
		if (material.equalsIgnoreCase("BOOK")) return true;
		return hc.getMC().canEnchantItem(this);
	}
	
	public boolean canAcceptEnchantment(HEnchantment e) {
		if (material.equalsIgnoreCase("AIR")) return false;
		if (material.equalsIgnoreCase("BOOK")) return true;
		if (material.equalsIgnoreCase("ENCHANTED_BOOK")) return false;
		if (amount > 1) return false;
		if (itemMeta != null) {
			for (HEnchantment en:itemMeta.getEnchantments()) {
				if (hc.getMC().conflictsWith(e, en)) return false;
			}
		}
		return canEnchantItem();
	}
	
	public boolean containsEnchantment(HEnchantment e) {
		if (itemMeta == null) return false;
		return itemMeta.containsEnchantment(e);
	}
	
	public double addEnchantment(HEnchantment e) {
		if (itemMeta == null) itemMeta = new HItemMeta("", new ArrayList<String>(), new ArrayList<HEnchantment>(), new ArrayList<HItemFlag>(), false, 0);
		if (itemMeta.containsEnchantment(e)) return 0;
		itemMeta.addEnchantment(e);
		return 1;
	}
	
	public double removeEnchantment(HEnchantment e) {
		if (itemMeta == null) return 0;
		if (!itemMeta.containsEnchantment(e)) return 0;
		itemMeta.removeEnchantment(e);
		return getDurabilityPercent();
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
	
	public StackComparisonData getStackComparisonData() {
		StackComparisonData data = new StackComparisonData();
		data.data = getComparisonData();
		data.durability = getComparisonDurability();
		data.material = material;
		data.itemMeta = itemMeta;
		data.maxStackSize = maxStackSize;
		data.maxDurability = maxDurability;
		data.isBlank = isBlank;
		return data;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		HItemStack other = (HItemStack) obj;
		if (amount != other.getAmount()) return false;
		if (data != other.getData()) return false;
		if (durability != other.getDurability()) return false;
		if (isBlank != other.isBlank()) return false;
		if (itemMeta == null) {
			if (other.getItemMeta() != null) return false;
		} else if (!itemMeta.equals(other.getItemMeta()))
			return false;
		if (material == null) {
			if (other.getMaterial() != null) return false;
		} else if (!material.equals(other.getMaterial()))
			return false;
		if (maxDurability != other.getMaxDurability()) return false;
		if (maxStackSize != other.getMaxStackSize()) return false;
		return true;
	}
	
	public boolean isSimilarTo(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		HItemStack other = (HItemStack) obj;
		if (getComparisonData() != other.getComparisonData()) return false;
		if (getComparisonDurability() != other.getComparisonDurability()) return false;
		if (isBlank != other.isBlank()) return false;
		if (itemMeta == null) {
			if (other.getItemMeta() != null) return false;
		} else if (!itemMeta.equals(other.getItemMeta()))
			return false;
		if (material == null) {
			if (other.getMaterial() != null) return false;
		} else if (!material.equals(other.getMaterial()))
			return false;
		if (maxDurability != other.getMaxDurability()) return false;
		if (maxStackSize != other.getMaxStackSize()) return false;
		return true;
	}






}