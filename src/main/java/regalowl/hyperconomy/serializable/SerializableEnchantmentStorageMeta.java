package regalowl.hyperconomy.serializable;

import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

import regalowl.hyperconomy.HyperConomy;
 

public class SerializableEnchantmentStorageMeta extends SerializableItemMeta implements Serializable {

	private static final long serialVersionUID = -6050487153906386305L;
	
	private List<SerializableEnchantment> storedEnchantments = new ArrayList<SerializableEnchantment>();

	public SerializableEnchantmentStorageMeta(String displayName, List<String> lore, List<SerializableEnchantment> enchantments, List<SerializableEnchantment> storedEnchantments) {
		super(displayName, lore, enchantments);
		this.storedEnchantments = storedEnchantments;
	}

	public SerializableEnchantmentStorageMeta(String base64String) {
		super(base64String);
    	try {
			byte[] data = Base64Coder.decode(base64String);
			ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(data));
			Object o = ois.readObject();
			ois.close();
			if (!(o instanceof SerializableEnchantmentStorageMeta)) {return;}
			SerializableEnchantmentStorageMeta se = (SerializableEnchantmentStorageMeta)o;
			this.storedEnchantments = se.getEnchantments();
    	} catch (Exception e) {
    		HyperConomy.hc.getDataBukkit().writeError(e);
    	}
    }
	
	@Override
	public List<SerializableEnchantment> getEnchantments() {
		return storedEnchantments;
	}
	
	@Override
	public boolean containsEnchantment(SerializableEnchantment e) {
		for (SerializableEnchantment se:storedEnchantments) {
			if (se.equals(e)) return true;
		}
		return false;
	}
	
	@Override
	public void addEnchantment(SerializableEnchantment e) {
		storedEnchantments.add(e);
	}
	
	@Override
	public void removeEnchantment(SerializableEnchantment e) {
		if (containsEnchantment(e)) storedEnchantments.remove(e);
	}
	
	@Override
	public boolean hasEnchantments() {
		if (enchantments.size() > 0) return true;
		if (storedEnchantments.size() > 0) return true;
		return false;
	}
	
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((storedEnchantments == null) ? 0 : storedEnchantments.hashCode());
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
		SerializableEnchantmentStorageMeta other = (SerializableEnchantmentStorageMeta) obj;
		if (storedEnchantments == null) {
			if (other.storedEnchantments != null)
				return false;
		} else if (!storedEnchantments.equals(other.storedEnchantments))
			return false;
		return true;
	}
	


}