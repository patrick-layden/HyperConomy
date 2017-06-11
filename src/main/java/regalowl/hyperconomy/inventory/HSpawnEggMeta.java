package regalowl.hyperconomy.inventory;

import java.util.ArrayList;
import java.util.HashMap;

import regalowl.simpledatalib.CommonFunctions;

 

public class HSpawnEggMeta extends HItemMeta {

	private static final long serialVersionUID = -6901893813106515124L;
	private String entityType;

	public HSpawnEggMeta(String displayName, ArrayList<String> lore, ArrayList<HEnchantment> enchantments, ArrayList<HItemFlag> itemFlags, boolean unbreakable, int repairCost, String entityType) {
		super(displayName, lore, enchantments, itemFlags, unbreakable, repairCost);
		this.entityType = entityType;
	}

	public HSpawnEggMeta(String serialized) {
		super(serialized);
		HashMap<String,String> data = CommonFunctions.explodeMap(serialized);
		this.entityType = data.get("entityType");
    }
	
	public HSpawnEggMeta(HSpawnEggMeta meta) {
		super(meta);
		this.entityType = meta.entityType;
    }

	@Override
	public String serialize() {
		HashMap<String,String> data = super.getMap();
		data.put("entityType", entityType);
		return CommonFunctions.implodeMap(data);
	}
	
	@Override
	public HItemMetaType getType() {
		return HItemMetaType.SPAWN_EGG;
	}
	

	public String getEntityType() {
		return entityType;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((entityType == null) ? 0 : entityType.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (!super.equals(obj)) return false;
		if (getClass() != obj.getClass()) return false;
		HSpawnEggMeta other = (HSpawnEggMeta) obj;
		if (entityType == null) {
			if (other.entityType != null) return false;
		} else if (!entityType.equals(other.entityType)) return false;
		return true;
	}



}