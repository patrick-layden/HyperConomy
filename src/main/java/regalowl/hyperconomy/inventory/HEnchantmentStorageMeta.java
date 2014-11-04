package regalowl.hyperconomy.inventory;


import java.util.ArrayList;
import java.util.HashMap;


import regalowl.databukkit.CommonFunctions;

 

public class HEnchantmentStorageMeta extends HItemMeta {

	private ArrayList<HEnchantment> storedEnchantments = new ArrayList<HEnchantment>();

	public HEnchantmentStorageMeta(String displayName, ArrayList<String> lore, ArrayList<HEnchantment> enchantments, ArrayList<HEnchantment> storedEnchantments) {
		super(displayName, lore, enchantments);
		this.storedEnchantments = storedEnchantments;
	}

	public HEnchantmentStorageMeta(String serialized) {
		super(serialized);
		HashMap<String,String> data = CommonFunctions.explodeMap(serialized);
		ArrayList<String> stEnchants = CommonFunctions.explode(data.get("storedEnchantments"));
		for (String e:stEnchants) {
			storedEnchantments.add(new HEnchantment(e));
		}
    }

	public String serialize() {
		HashMap<String,String> data = super.getMap();
		ArrayList<String> stEnchants = new ArrayList<String>();
		for (HEnchantment e:storedEnchantments) {
			stEnchants.add(e.serialize());
		}
		data.put("storedEnchantments", CommonFunctions.implode(stEnchants));
		return CommonFunctions.implodeMap(data);
	}
	
	
	@Override
	public ArrayList<HEnchantment> getEnchantments() {
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