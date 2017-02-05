package regalowl.hyperconomy.inventory;

import java.util.ArrayList;
import java.util.HashMap;

import regalowl.simpledatalib.CommonFunctions;

 

public class HSkullMeta extends HItemMeta {


	private String owner;

	public HSkullMeta(String displayName, ArrayList<String> lore, ArrayList<HEnchantment> enchantments, ArrayList<HItemFlag> itemFlags, boolean unbreakable, int repairCost, String owner) {
		super(displayName, lore, enchantments, itemFlags, unbreakable, repairCost);
		this.owner = owner;
	}

	public HSkullMeta(String serialized) {
		super(serialized);
		HashMap<String,String> data = CommonFunctions.explodeMap(serialized);
		this.owner = data.get("owner");
    }

	@Override
	public String serialize() {
		HashMap<String,String> data = super.getMap();
		data.put("owner", owner);
		return CommonFunctions.implodeMap(data);
	}
	
	@Override
	public HItemMetaType getType() {
		return HItemMetaType.SKULL;
	}
	

	public String getOwner() {
		return owner;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((owner == null) ? 0 : owner.hashCode());
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
		HSkullMeta other = (HSkullMeta) obj;
		if (owner == null) {
			if (other.owner != null)
				return false;
		} else if (!owner.equals(other.owner))
			return false;
		return true;
	}

}