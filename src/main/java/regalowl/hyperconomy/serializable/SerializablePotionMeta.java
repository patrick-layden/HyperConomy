package regalowl.hyperconomy.serializable;

import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;


 

public class SerializablePotionMeta extends SerializableItemMeta {

	private static final long serialVersionUID = 7131977924010280498L;
	private List<SerializablePotionEffect> potionEffects = new ArrayList<SerializablePotionEffect>();

	public SerializablePotionMeta(ItemMeta im) {
		super(im);
		if (im instanceof PotionMeta) {
			PotionMeta pm = (PotionMeta)im;
			for (PotionEffect pe:pm.getCustomEffects()) {
				potionEffects.add(new SerializablePotionEffect(pe));
			}
		}
    }

	public SerializablePotionMeta(String base64String) {
		super(base64String);
    	try {
			byte[] data = Base64Coder.decode(base64String);
			ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(data));
			Object o = ois.readObject();
			ois.close();
			if (!(o instanceof SerializablePotionMeta)) {return;}
			SerializablePotionMeta spm = (SerializablePotionMeta)o;
			this.potionEffects = spm.getPotionEffects();
    	} catch (Exception e) {
    		
    	}
    }
	
	
	@Override
	public ItemMeta getItemMeta() {
		ItemStack s = new ItemStack(Material.POTION);
		PotionMeta pm = (PotionMeta)s.getItemMeta();
		pm.setDisplayName(displayName);
		pm.setLore(lore);
		for (SerializableEnchantment se:enchantments) {
			pm.addEnchant(se.getEnchantment(), se.getLvl(), true);
		}
		for (SerializablePotionEffect spe:potionEffects) {
			pm.addCustomEffect(spe.getPotionEffect(), true);
		}
		return pm;
	}
	
	
	public List<SerializablePotionEffect> getPotionEffects() {
		return potionEffects;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((potionEffects == null) ? 0 : potionEffects.hashCode());
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
		SerializablePotionMeta other = (SerializablePotionMeta) obj;
		if (potionEffects == null) {
			if (other.potionEffects != null)
				return false;
		} else if (!potionEffects.equals(other.potionEffects))
			return false;
		return true;
	}

	
	

}