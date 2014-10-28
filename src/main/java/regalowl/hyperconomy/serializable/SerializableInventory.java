package regalowl.hyperconomy.serializable;

import java.io.Serializable;
import java.util.ArrayList;

import regalowl.hyperconomy.HyperConomy;
import regalowl.hyperconomy.account.HyperPlayer;
import regalowl.hyperconomy.util.SimpleLocation;

public class SerializableInventory extends SerializableObject implements Serializable {


	private static final long serialVersionUID = 4247823594626362499L;
	private ArrayList<SerializableItemStack> items = new ArrayList<SerializableItemStack>();
	private int heldSlot;
	private SerializableInventoryType inventoryType;
	private String owner;
	private SimpleLocation location;

	public SerializableInventory(ArrayList<SerializableItemStack> items, SerializableInventoryType inventoryType) {
		this.items.addAll(items);
		this.inventoryType = inventoryType;
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
	
	public void clearSlot(int slot) {
		items.set(slot, null);
	}
	
	
	public SerializableInventoryType getInventoryType() {
		return inventoryType;
	}
	
	
	public HyperPlayer getHyperPlayer() {
		if (isPlayerInventory()) return HyperConomy.hc.getHyperPlayerManager().getHyperPlayer(owner);
		return null;
	}
	
	public boolean isPlayerInventory() {
		if (inventoryType.equals(SerializableInventoryType.PLAYER)) return true;
		return false;
	}
	
	public void updateInventory() {
		HyperConomy.mc.setInventory(this);
	}
	
	public int getSize() {
		return items.size();
	}
	
	public SimpleLocation getLocation() {
		return location;
	}
	
	public void setHeldSlot(int heldSlot) {
		this.heldSlot = heldSlot;
	}
	
	public SerializableItemStack getHeldItem() {
		return getItem(heldSlot);
	}
	
	public void setOwner(String owner) {
		this.owner = owner;
	}
	
	public void setLocation(SimpleLocation location) {
		this.location = location;
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
