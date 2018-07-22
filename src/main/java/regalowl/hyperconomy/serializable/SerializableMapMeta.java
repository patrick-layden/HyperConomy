package regalowl.hyperconomy.serializable;

import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.MapMeta;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;


 

public class SerializableMapMeta extends SerializableItemMeta {

	private static final long serialVersionUID = -1095975801937823837L;

	private boolean isScaling;

	public SerializableMapMeta(ItemMeta im) {
		super(im);
		if (im instanceof MapMeta) {
			MapMeta mm = (MapMeta)im;
			this.isScaling = mm.isScaling();
		}
    }

	public SerializableMapMeta(String base64String) {
		super(base64String);
    	try {
			byte[] data = Base64Coder.decode(base64String);
			ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(data));
			Object o = ois.readObject();
			ois.close();
			if (!(o instanceof SerializableMapMeta)) {return;}
			SerializableMapMeta mm = (SerializableMapMeta)o;
			this.isScaling = mm.isScaling();
    	} catch (Exception e) {
    		
    	}
    }
	
	
	@Override
	public ItemMeta getItemMeta() {
		ItemStack s = new ItemStack(Material.MAP);
		MapMeta mm = (MapMeta)s.getItemMeta();
		mm.setDisplayName(displayName);
		mm.setLore(lore);
		for (SerializableEnchantment se:enchantments) {
			mm.addEnchant(se.getEnchantment(), se.getLvl(), true);
		}
		mm.setScaling(isScaling);
		return mm;
	}
	
	public boolean isScaling() {
		return isScaling;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + (isScaling ? 1231 : 1237);
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
		SerializableMapMeta other = (SerializableMapMeta) obj;
		if (isScaling != other.isScaling)
			return false;
		return true;
	}
	


}