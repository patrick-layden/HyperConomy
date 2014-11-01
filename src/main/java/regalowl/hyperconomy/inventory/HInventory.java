package regalowl.hyperconomy.inventory;

import java.io.Serializable;
import java.util.ArrayList;

import regalowl.hyperconomy.HC;
import regalowl.hyperconomy.account.HyperPlayer;
import regalowl.hyperconomy.minecraft.HLocation;

public class HInventory extends SerializableObject implements Serializable {


	private static final long serialVersionUID = 4247823594626362499L;
	private ArrayList<HItemStack> items = new ArrayList<HItemStack>();
	private int heldSlot;
	private HInventoryType inventoryType;
	private String owner;
	private HLocation location;

	public HInventory(ArrayList<HItemStack> items, HInventoryType inventoryType) {
		this.items.addAll(items);
		this.inventoryType = inventoryType;
	}
	
	public ArrayList<HItemStack> getItems() {
		return items;
	}
	
	public int getHeldSlot() {
		return heldSlot;
	}
	
	public HItemStack getItem(int slot) {
		if (slot > items.size() - 1) return null;
		return items.get(slot);
	}
	
	public void setItem(int slot, HItemStack item) {
		items.set(slot, item);
	}
	
	public void clearSlot(int slot) {
		items.set(slot, new HItemStack());
	}
	
	
	public HInventoryType getInventoryType() {
		return inventoryType;
	}
	
	
	public HyperPlayer getHyperPlayer() {
		if (isPlayerInventory()) return HC.hc.getHyperPlayerManager().getHyperPlayer(owner);
		return null;
	}
	
	public boolean isPlayerInventory() {
		if (inventoryType.equals(HInventoryType.PLAYER)) return true;
		return false;
	}
	
	public void updateInventory() {
		HC.mc.setInventory(this);
	}
	
	public int getSize() {
		return items.size();
	}
	
	public HLocation getLocation() {
		return location;
	}
	
	public void setHeldSlot(int heldSlot) {
		this.heldSlot = heldSlot;
	}
	
	public HItemStack getHeldItem() {
		return getItem(heldSlot);
	}
	
	public void setOwner(String owner) {
		this.owner = owner;
	}
	
	public void setLocation(HLocation location) {
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
		HInventory other = (HInventory) obj;
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
