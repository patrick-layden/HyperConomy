package regalowl.hyperconomy.serializable;

import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.FireworkEffect;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;


 

public class SerializableFireworkMeta extends SerializableItemMeta implements Serializable {

	private static final long serialVersionUID = 7131977924010280498L;
	private List<SerializableFireworkEffect> effects = new ArrayList<SerializableFireworkEffect>();
	private int power;

	public SerializableFireworkMeta(ItemMeta im) {
		super(im);
		if (im instanceof FireworkMeta) {
			FireworkMeta fm = (FireworkMeta)im;
			for (FireworkEffect fe:fm.getEffects()) {
				effects.add(new SerializableFireworkEffect(fe));
			}
			this.power = fm.getPower();
		}
    }

	public SerializableFireworkMeta(String base64String) {
		super(base64String);
    	try {
			byte[] data = Base64Coder.decode(base64String);
			ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(data));
			Object o = ois.readObject();
			ois.close();
			if (!(o instanceof SerializableFireworkMeta)) {return;}
			SerializableFireworkMeta sfm = (SerializableFireworkMeta)o;
			this.effects = sfm.getEffects();
			this.power = sfm.getPower();
    	} catch (Exception e) {
    		
    	}
    }
	
	
	@Override
	public ItemMeta getItemMeta() {
		ItemStack s = new ItemStack(Material.FIREWORK);
		FireworkMeta fm = (FireworkMeta)s.getItemMeta();
		fm.setDisplayName(displayName);
		fm.setLore(lore);
		for (SerializableEnchantment se:enchantments) {
			fm.addEnchant(se.getEnchantment(), se.getLvl(), true);
		}
		for (SerializableFireworkEffect sfe:effects) {
			fm.addEffect(sfe.getFireworkEffect());
		}
		fm.setPower(power);
		return fm;
	}
	
	public List<SerializableFireworkEffect> getEffects() {
		return effects;
	}
	public int getPower() {
		return power;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((effects == null) ? 0 : effects.hashCode());
		result = prime * result + power;
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
		SerializableFireworkMeta other = (SerializableFireworkMeta) obj;
		if (effects == null) {
			if (other.effects != null)
				return false;
		} else if (!effects.equals(other.effects))
			return false;
		if (power != other.power)
			return false;
		return true;
	}

}