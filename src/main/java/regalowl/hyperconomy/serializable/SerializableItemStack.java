package regalowl.hyperconomy.serializable;

import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.ArrayList;

import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

import regalowl.hyperconomy.HyperConomy;
import regalowl.hyperconomy.account.HyperPlayer;
 

public class SerializableItemStack extends SerializableObject implements Serializable {

	private static final long serialVersionUID = 8634824379403255552L;
	private String material;
    private short durability;
    private byte data;
    private SerializableItemMeta itemMeta;
    private int amount;
    private int maxStackSize;
    private int maxDurability;
    private boolean isBlank;
  
 
    public SerializableItemStack() {
    	this.isBlank = true;
    	this.material = "AIR";
    }
    
    public SerializableItemStack(SerializableItemMeta itemMeta, String material, short durability, byte data, int amount, int maxStackSize, int maxDurability) {
    	this.itemMeta = itemMeta;
    	this.material = material;
    	this.durability = durability;
    	this.data = data;
    	this.amount = amount;
    	this.maxStackSize = maxStackSize;
    	this.maxDurability = maxDurability;
    	this.isBlank = false;
    }
    

	public SerializableItemStack(String base64String) {
    	try {
			byte[] data = Base64Coder.decode(base64String);
			ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(data));
			Object o = ois.readObject();
			ois.close();
			if (!(o instanceof SerializableItemStack)) {return;}
			SerializableItemStack sis = (SerializableItemStack)o;
	        this.material = sis.getMaterial();
	        this.durability = sis.getDurability();
	        this.data = sis.getData();
	        this.itemMeta = sis.getItemMeta();
    	} catch (Exception e) {
    		HyperConomy.hc.getDataBukkit().writeError(e);
    	}
    }

	public void displayInfo(HyperPlayer p, String color1, String color2) {
		p.sendMessage(color1 + "Material: " + color2 + material);
		p.sendMessage(color1 + "Durability: " + color2 + durability);
		p.sendMessage(color1 + "Data: " + color2 + data);
		if (itemMeta != null) {
			itemMeta.displayInfo(p, color1, color2);
		}
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

	public SerializableItemMeta getItemMeta() {
		return itemMeta;
	}
	
	public int getAmount() {
		return amount;
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
	
	
	public boolean considerDamage() {
		boolean ignoreDamage = HyperConomy.hc.getConf().getBoolean("enable-feature.treat-damaged-items-as-equals-to-undamaged-ones");
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
		return HyperConomy.hc.getMC().canEnchantItem(this);
	}
	
	public boolean canAcceptEnchantment(SerializableEnchantment e) {
		if (material.equalsIgnoreCase("AIR")) return false;
		if (material.equalsIgnoreCase("BOOK")) return true;
		if (material.equalsIgnoreCase("ENCHANTED_BOOK")) return false;
		if (amount > 1) return false;
		if (itemMeta != null) {
			for (SerializableEnchantment en:itemMeta.getEnchantments()) {
				if (HyperConomy.hc.getMC().conflictsWith(e, en)) return false;
			}
		}
		return canEnchantItem();
	}
	
	public boolean containsEnchantment(SerializableEnchantment e) {
		if (itemMeta == null) return false;
		return itemMeta.containsEnchantment(e);
	}
	
	public void addEnchantment(SerializableEnchantment e) {
		if (itemMeta == null) itemMeta = new SerializableItemMeta("", new ArrayList<String>(), new ArrayList<SerializableEnchantment>());
		itemMeta.addEnchantment(e);
	}
	
	public void removeEnchantment(SerializableEnchantment e) {
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
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SerializableItemStack other = (SerializableItemStack) obj;
		if (data != other.data)
			return false;
		if (durability != other.durability)
			return false;
		if (isBlank != other.isBlank)
			return false;
		if (itemMeta == null) {
			if (other.itemMeta != null)
				return false;
		} else if (!itemMeta.equals(other.itemMeta))
			return false;
		if (material == null) {
			if (other.material != null)
				return false;
		} else if (!material.equals(other.material))
			return false;
		if (maxDurability != other.maxDurability)
			return false;
		if (maxStackSize != other.maxStackSize)
			return false;
		return true;
	}

	



}