package regalowl.hyperconomy.inventory;

import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

import regalowl.hyperconomy.HC;
 

public class HPotionMeta extends HItemMeta implements Serializable {

	private static final long serialVersionUID = 7131977924010280498L;
	private List<HPotionEffect> potionEffects = new ArrayList<HPotionEffect>();

	public HPotionMeta(String displayName, List<String> lore, List<HEnchantment> enchantments, List<HPotionEffect> potionEffects) {
		super(displayName, lore, enchantments);
		this.potionEffects = potionEffects;
	}

	public HPotionMeta(String base64String) {
		super(base64String);
    	try {
			byte[] data = Base64Coder.decode(base64String);
			ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(data));
			Object o = ois.readObject();
			ois.close();
			if (!(o instanceof HPotionMeta)) {return;}
			HPotionMeta spm = (HPotionMeta)o;
			this.potionEffects = spm.getPotionEffects();
    	} catch (Exception e) {
    		HC.hc.getDataBukkit().writeError(e);
    	}
    }
	

	
	public List<HPotionEffect> getPotionEffects() {
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
		HPotionMeta other = (HPotionMeta) obj;
		if (potionEffects == null) {
			if (other.potionEffects != null)
				return false;
		} else if (!potionEffects.equals(other.potionEffects))
			return false;
		return true;
	}

	
	

}