package regalowl.hyperconomy.inventory;

import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

import regalowl.hyperconomy.HC;
 

public class HEnchantmentStorageMeta extends HItemMeta implements Serializable {

	private static final long serialVersionUID = -6050487153906386305L;
	
	private List<HEnchantment> storedEnchantments = new ArrayList<HEnchantment>();

	public HEnchantmentStorageMeta(String displayName, List<String> lore, List<HEnchantment> enchantments, List<HEnchantment> storedEnchantments) {
		super(displayName, lore, enchantments);
		this.storedEnchantments = storedEnchantments;
	}

	public HEnchantmentStorageMeta(String base64String) {
		super(base64String);
    	try {
			byte[] data = Base64Coder.decode(base64String);
			ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(data));
			Object o = ois.readObject();
			ois.close();
			if (!(o instanceof HEnchantmentStorageMeta)) {return;}
			HEnchantmentStorageMeta se = (HEnchantmentStorageMeta)o;
			this.storedEnchantments = se.getEnchantments();
    	} catch (Exception e) {
    		HC.hc.getDataBukkit().writeError(e);
    	}
    }
	
	@Override
	public List<HEnchantment> getEnchantments() {
		return storedEnchantments;
	}
	
	@Override
	public boolean containsEnchantment(HEnchantment e) {
		for (HEnchantment se:storedEnchantments) {
			if (se.equals(e)) return true;
		}
		return false;
	}
	
	@Override
	public void addEnchantment(HEnchantment e) {
		storedEnchantments.add(e);
	}
	
	@Override
	public void removeEnchantment(HEnchantment e) {
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
		HEnchantmentStorageMeta other = (HEnchantmentStorageMeta) obj;
		if (storedEnchantments == null) {
			if (other.storedEnchantments != null)
				return false;
		} else if (!storedEnchantments.equals(other.storedEnchantments))
			return false;
		return true;
	}
	


}