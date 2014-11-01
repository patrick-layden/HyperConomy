package regalowl.hyperconomy.inventory;

import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.List;


import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

import regalowl.hyperconomy.HC;
 

public class HMapMeta extends HItemMeta implements Serializable {

	private static final long serialVersionUID = -1095975801937823837L;

	private boolean isScaling;

	public HMapMeta(String displayName, List<String> lore, List<HEnchantment> enchantments, boolean isScaling) {
		super(displayName, lore, enchantments);
		this.isScaling = isScaling;
	}

	public HMapMeta(String base64String) {
		super(base64String);
    	try {
			byte[] data = Base64Coder.decode(base64String);
			ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(data));
			Object o = ois.readObject();
			ois.close();
			if (!(o instanceof HMapMeta)) {return;}
			HMapMeta mm = (HMapMeta)o;
			this.isScaling = mm.isScaling();
    	} catch (Exception e) {
    		HC.hc.getDataBukkit().writeError(e);
    	}
    }
	

	public boolean isScaling() {
		return isScaling;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + (isScaling ? 1231 : 1237);
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
		HMapMeta other = (HMapMeta) obj;
		if (isScaling != other.isScaling)
			return false;
		return true;
	}
	


}