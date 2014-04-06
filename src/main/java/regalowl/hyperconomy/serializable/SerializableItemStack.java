package regalowl.hyperconomy.serializable;

import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;
import java.io.Serializable;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.FireworkEffectMeta;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.MapMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

import regalowl.hyperconomy.HyperConomy;
 

public class SerializableItemStack extends SerializableObject implements Serializable {

	private static final long serialVersionUID = 8634824379403255552L;
	private String material;
    private short durability;
    private byte data;
    private SerializableItemMeta itemMeta;
  
 
    @SuppressWarnings("deprecation")
	public SerializableItemStack(ItemStack item) {
        this.material = item.getType().toString();
        this.durability = item.getDurability();
        this.data = item.getData().getData(); 
        if (item.hasItemMeta()) {
        	ItemMeta im = item.getItemMeta();
        	if (im instanceof EnchantmentStorageMeta) {
        		itemMeta = new SerializableEnchantmentStorageMeta(item.getItemMeta());
        	} else if (im instanceof BookMeta) {
        		itemMeta = new SerializableBookMeta(item.getItemMeta());
        	} else if (im instanceof FireworkEffectMeta) {
        		itemMeta = new SerializableFireworkEffectMeta(item.getItemMeta());
        	} else if (im instanceof FireworkMeta) {
        		itemMeta = new SerializableFireworkMeta(item.getItemMeta());
        	} else if (im instanceof LeatherArmorMeta) {
        		itemMeta = new SerializableLeatherArmorMeta(item.getItemMeta());
        	} else if (im instanceof PotionMeta) {
        		itemMeta = new SerializablePotionMeta(item.getItemMeta());
        	} else if (im instanceof SkullMeta) {
        		itemMeta = new SerializableSkullMeta(item.getItemMeta());
        	} else if (im instanceof MapMeta) {
        		itemMeta = new SerializableMapMeta(item.getItemMeta());
        	} else {
        		itemMeta = new SerializableItemMeta(item.getItemMeta());
        	}
        }
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
 
    @SuppressWarnings("deprecation")
	public ItemStack getItem() {
        ItemStack item = new ItemStack(Material.matchMaterial(material));
        item.setAmount(1);
        item.setDurability(durability);
        if (itemMeta != null) {
        	item.setItemMeta(itemMeta.getItemMeta());
        }
        item.getData().setData(data);
        return item;
    }
    
	public void displayInfo(Player p, ChatColor color1, ChatColor color2) {
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
	
	public Material getMaterialEnum() {
		return Material.matchMaterial(material);
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

	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + data;
		result = prime * result + durability;
		result = prime * result + ((itemMeta == null) ? 0 : itemMeta.hashCode());
		result = prime * result + ((material == null) ? 0 : material.hashCode());
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
		return true;
	}

}