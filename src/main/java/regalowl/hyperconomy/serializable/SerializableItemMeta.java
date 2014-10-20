package regalowl.hyperconomy.serializable;

import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

import regalowl.hyperconomy.HyperConomy;
import regalowl.hyperconomy.account.HyperPlayer;
 

public class SerializableItemMeta extends SerializableObject implements Serializable {

	private static final long serialVersionUID = 4510326523024526205L;
	
	protected String displayName;
	protected List<String> lore = new ArrayList<String>();
	protected List<SerializableEnchantment> enchantments = new ArrayList<SerializableEnchantment>();
 
	
	public SerializableItemMeta(String displayName, List<String> lore, List<SerializableEnchantment> enchantments) {
        this.displayName = displayName;
        this.lore = lore;
        this.enchantments = enchantments;
    }

	public SerializableItemMeta(String base64String) {
    	try {
			byte[] data = Base64Coder.decode(base64String);
			ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(data));
			Object o = ois.readObject();
			ois.close();
			if (!(o instanceof SerializableItemMeta)) {return;}
			SerializableItemMeta se = (SerializableItemMeta)o;
	        this.displayName = se.getDisplayName();
	        this.lore = se.getLore();
	        this.enchantments = se.getEnchantments();
    	} catch (Exception e) {
    		HyperConomy.hc.getDataBukkit().writeError(e);
    	}
    }
	
	public void displayInfo(HyperPlayer p, String color1, String color2) {
		p.sendMessage(color1 + "Display Name: " + color2 + displayName);
		String loreString = "";
		if (lore != null && lore.size() > 0) {
			for(String l:lore) {
				loreString += l + ",";
			}
			loreString = loreString.substring(0, loreString.length() - 1);
		}
		p.sendMessage(color1 + "Lore: " + color2 + loreString);
		String enchantString = "";
		if (enchantments != null && enchantments.size() > 0) {
			for(SerializableEnchantment se:enchantments) {
				enchantString += se.getEnchantmentName() + ",";
			}
			enchantString = enchantString.substring(0, enchantString.length() - 1);
		}
		p.sendMessage(color1 + "Enchantments: " + color2 + enchantString);
	}
	

	public String getDisplayName() {
		return displayName;
	}

	public List<String> getLore() {
		return lore;
	}
	
	public List<SerializableEnchantment> getEnchantments() {
		return enchantments;
	}
	
	public boolean hasEnchantments() {
		if (enchantments.size() > 0) return true;
		return false;
	}
	
	public void addEnchantment(SerializableEnchantment e) {
		enchantments.add(e);
	}
	
	public void removeEnchantment(SerializableEnchantment e) {
		if (containsEnchantment(e)) enchantments.remove(e);
	}

	
	public boolean containsEnchantment(SerializableEnchantment e) {
		for (SerializableEnchantment se:enchantments) {
			if (se.equals(e)) return true;
		}
		return false;
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
		SerializableItemMeta other = (SerializableItemMeta) obj;
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
		if (lore == null) {
			if (other.lore != null)
				return false;
		} else if (!lore.equals(other.lore))
			return false;
		return true;
	}

}