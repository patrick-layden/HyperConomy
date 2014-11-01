package regalowl.hyperconomy.inventory;

import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.List;

import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

import regalowl.hyperconomy.HC;
 

public class HFireworkEffectMeta extends HItemMeta implements Serializable {

	private static final long serialVersionUID = -6227758269858375863L;
	private HFireworkEffect effect;

	
	public HFireworkEffectMeta(String displayName, List<String> lore, List<HEnchantment> enchantments, HFireworkEffect effect) {
		super(displayName, lore, enchantments);
		this.effect = effect;
	}
	

	public HFireworkEffectMeta(String base64String) {
		super(base64String);
    	try {
			byte[] data = Base64Coder.decode(base64String);
			ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(data));
			Object o = ois.readObject();
			ois.close();
			if (!(o instanceof HFireworkEffectMeta)) {return;}
			HFireworkEffectMeta sfem = (HFireworkEffectMeta)o;
			this.effect = sfem.getEffect();
    	} catch (Exception e) {
    		HC.hc.getDataBukkit().writeError(e);
    	}
    }

	public HFireworkEffect getEffect() {
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
		HFireworkEffectMeta other = (HFireworkEffectMeta) obj;
		if (effect == null) {
			if (other.effect != null)
				return false;
		} else if (!effect.equals(other.effect))
			return false;
		return true;
	}
}