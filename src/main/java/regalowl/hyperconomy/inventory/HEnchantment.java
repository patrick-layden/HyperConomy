package regalowl.hyperconomy.inventory;

import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;
import java.io.Serializable;

import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

import regalowl.hyperconomy.HC;
 

public class HEnchantment extends SerializableObject implements Serializable {
	private static final long serialVersionUID = 4510326523024526205L;
	private String enchantment;
    private int lvl;
 
	public HEnchantment(String enchantment, int lvl) {
        this.enchantment = enchantment;
        this.lvl = lvl;
    }

	public HEnchantment(String base64String) {
    	try {
			byte[] data = Base64Coder.decode(base64String);
			ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(data));
			Object o = ois.readObject();
			ois.close();
			if (!(o instanceof HEnchantment)) {return;}
			HEnchantment se = (HEnchantment)o;
	        this.enchantment = se.getEnchantmentName();
	        this.lvl = se.getLvl();
    	} catch (Exception e) {
    		HC.hc.getDataBukkit().writeError(e);
    	}
    }


	public String getEnchantmentName() {
		return enchantment;
	}

	public int getLvl() {
		return lvl;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((enchantment == null) ? 0 : enchantment.hashCode());
		result = prime * result + lvl;
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
		HEnchantment other = (HEnchantment) obj;
		if (enchantment == null) {
			if (other.enchantment != null)
				return false;
		} else if (!enchantment.equals(other.enchantment))
			return false;
		if (lvl != other.lvl)
			return false;
		return true;
	}

}