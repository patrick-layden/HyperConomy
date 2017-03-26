package regalowl.hyperconomy.tradeobject;

import regalowl.hyperconomy.inventory.HItemMeta;

public class StackComparisonData {
	public String material;
	public short durability;
	public byte data;
	public HItemMeta itemMeta;
	public int maxStackSize;
	public int maxDurability;
	public boolean isBlank;
	
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + data;
		result = prime * result + durability;
		result = prime * result + (isBlank ? 1231 : 1237);
		result = prime * result + ((itemMeta == null) ? 0 : itemMeta.hashCode());
		result = prime * result + ((material == null) ? 0 : material.hashCode());
		result = prime * result + maxDurability;
		result = prime * result + maxStackSize;
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		StackComparisonData other = (StackComparisonData) obj;
		if (data != other.data) return false;
		if (durability != other.durability) return false;
		if (isBlank != other.isBlank) return false;
		if (itemMeta == null) {
			if (other.itemMeta != null) return false;
		} else if (!itemMeta.equals(other.itemMeta)) return false;
		if (material == null) {
			if (other.material != null) return false;
		} else if (!material.equals(other.material)) return false;
		if (maxDurability != other.maxDurability) return false;
		if (maxStackSize != other.maxStackSize) return false;
		return true;
	}
}
