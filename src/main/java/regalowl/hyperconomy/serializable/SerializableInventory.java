package regalowl.hyperconomy.serializable;

import java.io.Serializable;
import java.util.ArrayList;

import regalowl.hyperconomy.HyperConomy;
import regalowl.hyperconomy.account.HyperPlayer;

public class SerializableInventory extends SerializableObject implements Serializable {


	private static final long serialVersionUID = 4247823594626362499L;
	private ArrayList<SerializableItemStack> items = new ArrayList<SerializableItemStack>();
	private int heldSlot;

	public SerializableInventory(ArrayList<SerializableItemStack> items, int heldSlot) {
		this.items.addAll(items);
		this.heldSlot = heldSlot;
	}
	
	public ArrayList<SerializableItemStack> getItems() {
		return items;
	}
	
	public int getHeldSlot() {
		return heldSlot;
	}
	
	public SerializableItemStack getItem(int slot) {
		if (slot > items.size() - 1) return null;
		return items.get(slot);
	}
	
	public void setItem(int slot, SerializableItemStack item) {
		items.set(slot, item);
	}
	
	public void updateInventory(HyperPlayer hp) {
		HyperConomy.hc.getMC().setInventory(hp, this);
	}
	
	public int getSize() {
		return items.size();
	}
	
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + heldSlot;
		result = prime * result + ((items == null) ? 0 : items.hashCode());
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
		SerializableInventory other = (SerializableInventory) obj;
		if (heldSlot != other.heldSlot)
			return false;
		if (items == null) {
			if (other.items != null)
				return false;
		} else if (!items.equals(other.items))
			return false;
		return true;
	}
}
