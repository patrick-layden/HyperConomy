package regalowl.hyperconomy.serializable;

import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.List;

import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

import regalowl.hyperconomy.HyperConomy;
 

public class SerializableFireworkEffectMeta extends SerializableItemMeta implements Serializable {

	private static final long serialVersionUID = -6227758269858375863L;
	private SerializableFireworkEffect effect;

	
	public SerializableFireworkEffectMeta(String displayName, List<String> lore, List<SerializableEnchantment> enchantments, SerializableFireworkEffect effect) {
		super(displayName, lore, enchantments);
		this.effect = effect;
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
    		HyperConomy.hc.getDataBukkit().writeError(e);
    	}
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