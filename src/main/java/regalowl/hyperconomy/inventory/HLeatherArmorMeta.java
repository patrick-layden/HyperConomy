package regalowl.hyperconomy.inventory;

import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.List;


import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

import regalowl.hyperconomy.HC;
 

public class HLeatherArmorMeta extends HItemMeta implements Serializable {

	private static final long serialVersionUID = -7716626610545205516L;
	private HColor color;

	
	public HLeatherArmorMeta(String displayName, List<String> lore, List<HEnchantment> enchantments, HColor color) {
		super(displayName, lore, enchantments);
		this.color = color;
	}

	public HLeatherArmorMeta(String base64String) {
		super(base64String);
    	try {
			byte[] data = Base64Coder.decode(base64String);
			ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(data));
			Object o = ois.readObject();
			ois.close();
			if (!(o instanceof HLeatherArmorMeta)) {return;}
			HLeatherArmorMeta slam = (HLeatherArmorMeta)o;
			this.color = slam.getColor();
    	} catch (Exception e) {
    		HC.hc.getDataBukkit().writeError(e);
    	}
    }

	public HColor getColor() {
		return color;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((color == null) ? 0 : color.hashCode());
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
		HLeatherArmorMeta other = (HLeatherArmorMeta) obj;
		if (color == null) {
			if (other.color != null)
				return false;
		} else if (!color.equals(other.color))
			return false;
		return true;
	}


}