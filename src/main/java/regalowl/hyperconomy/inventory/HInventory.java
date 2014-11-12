package regalowl.hyperconomy.inventory;

import java.util.ArrayList;

import regalowl.hyperconomy.HC;
import regalowl.hyperconomy.account.HyperPlayer;
import regalowl.hyperconomy.minecraft.HLocation;

public class HInventory {

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
	
	public HItemStack getHeldItem() {
		return getItem(heldSlot);
	}

	public HItemStack getItem(int slot) {
		if (slot > items.size() - 1) return null;
		return items.get(slot);
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
	
	public int getSize() {
		return items.size();
	}
	
	public HLocation getLocation() {
		return location;
	}
	
	public void clearSlot(int slot) {
		items.set(slot, new HItemStack());
	}

	public void setItem(int slot, HItemStack item) {
		items.set(slot, item);
	}

	public void setHeldSlot(int heldSlot) {
		this.heldSlot = heldSlot;
	}

	public void setOwner(String owner) {
		this.owner = owner;
	}
	
	public void setLocation(HLocation location) {
		this.location = location;
	}
	
	public void updateInventory() {
		HC.mc.setInventory(this);
	}
	
	public void add(int addAmount, HItemStack addStack) {
		int maxStackSize = addStack.getMaxStackSize();
		for (int slot = 0; slot < getSize(); slot++) {
			HItemStack currentItem = getItem(slot);
			if (getItem(slot).isBlank()) {
				HItemStack newStack = new HItemStack(addStack.serialize());
				if (addAmount > maxStackSize) {
					newStack.setAmount(maxStackSize);
					setItem(slot, newStack);
					addAmount -= maxStackSize;
				} else {
					newStack.setAmount(addAmount);
					setItem(slot, newStack);
					addAmount = 0;
				}
			} else if (addStack.isSimilarTo(currentItem)) {
				int spaceInStack = maxStackSize - currentItem.getAmount();
				if (spaceInStack == 0) continue;
				if (spaceInStack < addAmount) {
					addAmount -= spaceInStack;
					currentItem.setAmount(maxStackSize);
				} else {
					currentItem.setAmount(addAmount + currentItem.getAmount());
					addAmount = 0;
				}
			}
			if (addAmount <= 0) break;
		}
		if (addAmount != 0) HC.hc.gSDL().getErrorWriter().writeError("HInventory add() failure; " + addAmount + " remaining.");
		updateInventory();
	}
	
	
	public double remove(int removeAmount, HItemStack removeStack) {
		double actuallyRemoved = 0.0;
		if (inventoryType == HInventoryType.PLAYER) {
			HItemStack heldStack = getHeldItem();
			if (removeStack.isSimilarTo(heldStack)) {
				if (removeAmount >= heldStack.getAmount()) {
					actuallyRemoved += heldStack.getTrueAmount();
					clearSlot(heldSlot);
					removeAmount -= heldStack.getAmount();
				} else {
					actuallyRemoved += removeAmount * heldStack.getDurabilityPercent();
					heldStack.setAmount(heldStack.getAmount() - removeAmount);
					removeAmount = 0;
				}
			}
		}
		int slot = 0;
		while (removeAmount > 0) {
			HItemStack currentItem = getItem(slot);
			if (removeStack.isSimilarTo(currentItem)) {
				if (removeAmount >= currentItem.getAmount()) {
					actuallyRemoved += currentItem.getTrueAmount();
					clearSlot(slot);
					removeAmount -= currentItem.getAmount();
				} else {
					actuallyRemoved += removeAmount * currentItem.getDurabilityPercent();
					currentItem.setAmount(currentItem.getAmount() - removeAmount);
					removeAmount = 0;
				}
			}
			slot++;
			if (slot >= getSize()) break;
		}
		if (removeAmount != 0) HC.hc.gSDL().getErrorWriter().writeError("HInventory remove() failure.  Items not successfully removed; amount = '" + removeAmount + "'");
		updateInventory();
		return actuallyRemoved;
	}

	
	public int getAvailableSpace(HItemStack stack) {
		int availableSpace = 0;
		for (int slot = 0; slot < getSize(); slot++) {
			HItemStack currentItem = getItem(slot);
			if (currentItem == null || currentItem.isBlank()) {
				availableSpace += stack.getMaxStackSize();
			} else if (stack.isSimilarTo(currentItem)) {
				availableSpace += (stack.getMaxStackSize() - currentItem.getAmount());
			}
		}
		return availableSpace;
	}
	
	public int count(HItemStack stack) {
		int itemCount = 0;
		for (int slot = 0; slot < getSize(); slot++) {
			HItemStack currentItem = getItem(slot);
			if (currentItem.isSimilarTo(stack)) {
				itemCount += currentItem.getAmount();
			}
		}
		return itemCount;
	}
	
	public double getPercentDamaged(int amount, HItemStack stack) {
		double totalDamage = 0;
		if (!stack.isDurable()) return 1;
		int totalItems = 0;
		int heldslot = -1;
		if (inventoryType == HInventoryType.PLAYER) {
			HItemStack heldItem = getHeldItem();
			if (heldItem.isSimilarTo(stack)) {
				heldslot = getHeldSlot();
				totalDamage += heldItem.getDurabilityPercent();
				totalItems += heldItem.getAmount();
				if (totalItems >= amount) return totalDamage;
			}
		}
		for (int slot = 0; slot < getSize(); slot++) {
			if (slot == heldslot) continue;
			HItemStack ci = getItem(slot);
			if (!ci.isSimilarTo(stack)) continue;
			totalDamage += ci.getDurabilityPercent();
			totalItems += ci.getAmount();
			if (totalItems >= amount) break;
		}
		totalDamage /= amount;
		return totalDamage;
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
