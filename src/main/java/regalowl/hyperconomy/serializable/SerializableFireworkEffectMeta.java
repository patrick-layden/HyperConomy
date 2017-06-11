package regalowl.hyperconomy.serializable;

import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;
import java.io.Serializable;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkEffectMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;


 

public class SerializableFireworkEffectMeta extends SerializableItemMeta implements Serializable {

	private static final long serialVersionUID = -6227758269858375863L;
	private SerializableFireworkEffect effect;

	public SerializableFireworkEffectMeta(ItemMeta im) {
		super(im);
		if (im instanceof FireworkEffectMeta) {
			FireworkEffectMeta fem = (FireworkEffectMeta)im;
			this.effect = new SerializableFireworkEffect(fem.getEffect());
		}
    }

	public SerializableFireworkEffectMeta(String base64String) {
		super(base64String);
    	try {
			byte[] data = Base64Coder.decode(base64String);
			ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(data));
			Object o = ois.readObject();
			ois.close();
			if (!(o instanceof SerializableFireworkEffectMeta)) {return;}
			SerializableFireworkEffectMeta sfem = (SerializableFireworkEffectMeta)o;
			this.effect = sfem.getEffect();
    	} catch (Exception e) {
    		
    	}
    }
	
	
	@Override
	public ItemMeta getItemMeta() {
		ItemStack s = new ItemStack(Material.FIREWORK_CHARGE);
		FireworkEffectMeta fem = (FireworkEffectMeta)s.getItemMeta();
		fem.setDisplayName(displayName);
		fem.setLore(lore);
		for (SerializableEnchantment se:enchantments) {
			fem.addEnchant(se.getEnchantment(), se.getLvl(), true);
		}
		fem.setEffect(effect.getFireworkEffect());
		return fem;
	}
	
	public SerializableFireworkEffect getEffect() {
		return effect;
	}
	
	

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((effect == null) ? 0 : effect.hashCode());
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
		SerializableFireworkEffectMeta other = (SerializableFireworkEffectMeta) obj;
		if (effect == null) {
			if (other.effect != null)
				return false;
		} else if (!effect.equals(other.effect))
			return false;
		return true;
	}
}