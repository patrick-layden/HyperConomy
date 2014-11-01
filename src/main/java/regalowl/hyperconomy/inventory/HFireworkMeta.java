package regalowl.hyperconomy.inventory;

import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

import regalowl.hyperconomy.HC;
 

public class HFireworkMeta extends HItemMeta implements Serializable {

	private static final long serialVersionUID = 7131977924010280498L;
	private List<HFireworkEffect> effects = new ArrayList<HFireworkEffect>();
	private int power;

	
	public HFireworkMeta(String displayName, List<String> lore, List<HEnchantment> enchantments, List<HFireworkEffect> effects, int power) {
		super(displayName, lore, enchantments);
		this.effects = effects;
		this.power = power;
	}

	public HFireworkMeta(String base64String) {
		super(base64String);
    	try {
			byte[] data = Base64Coder.decode(base64String);
			ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(data));
			Object o = ois.readObject();
			ois.close();
			if (!(o instanceof HFireworkMeta)) {return;}
			HFireworkMeta sfm = (HFireworkMeta)o;
			this.effects = sfm.getEffects();
			this.power = sfm.getPower();
    	} catch (Exception e) {
    		HC.hc.getDataBukkit().writeError(e);
    	}
    }
	

	public List<HFireworkEffect> getEffects() {
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
		HFireworkMeta other = (HFireworkMeta) obj;
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