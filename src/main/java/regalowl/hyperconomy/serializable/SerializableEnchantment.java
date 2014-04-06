package regalowl.hyperconomy.serializable;

import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;
import java.io.Serializable;

import org.bukkit.enchantments.Enchantment;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

import regalowl.hyperconomy.HyperConomy;
 

public class SerializableEnchantment extends SerializableObject implements Serializable {
	private static final long serialVersionUID = 4510326523024526205L;
	private String enchantment;
    private int lvl;
 
	public SerializableEnchantment(Enchantment e, int lvl) {
        this.enchantment = e.getName();
        this.lvl = lvl;
    }

	public SerializableEnchantment(String base64String) {
    	try {
			byte[] data = Base64Coder.decode(base64String);
			ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(data));
			Object o = ois.readObject();
			ois.close();
			if (!(o instanceof SerializableEnchantment)) {return;}
			SerializableEnchantment se = (SerializableEnchantment)o;
	        this.enchantment = se.getEnchantmentName();
	        this.lvl = se.getLvl();
    	} catch (Exception e) {
    		HyperConomy.hc.getDataBukkit().writeError(e);
    	}
    }

	public Enchantment getEnchantment() {
		return Enchantment.getByName(enchantment);
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
		SerializableEnchantment other = (SerializableEnchantment) obj;
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