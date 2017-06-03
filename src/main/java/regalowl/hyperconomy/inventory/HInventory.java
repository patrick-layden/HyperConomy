package regalowl.hyperconomy.inventory;

import java.util.ArrayList;
import java.util.Iterator;

import regalowl.hyperconomy.HyperConomy;
import regalowl.hyperconomy.account.HyperPlayer;
import regalowl.hyperconomy.minecraft.HLocation;

public class HInventory {

	private transient HyperConomy hc;
	
	private ArrayList<HItemStack> items = new ArrayList<HItemStack>();
	private int heldSlot;
	private HInventoryType inventoryType;
	private String owner;
	private HLocation location;

	public HInventory(HyperConomy hc, ArrayList<HItemStack> items, HInventoryType inventoryType) {
		this.hc = hc;
		this.items.addAll(items);
		this.inventoryType = inventoryType;
	}
	/***
	 * Provides a deep clone of the given inventory
	 * @param inv
	 */
	public HInventory(HInventory inv) {
		if (inv == null) return;
		this.hc = inv.hc;
		for (HItemStack stack:inv.items) {
			this.items.add(new HItemStack(stack));
		}
		this.heldSlot = inv.heldSlot;
		this.inventoryType = inv.inventoryType;
		this.owner = inv.owner;
		this.location = new HLocation(inv.location);
	}
	
	public ArrayList<HItemStack> getItems() {
		return items;
	}
	
	public void setHeldSlot(int heldSlot) {
		this.heldSlot = heldSlot;
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
	
	public void addItem(HItemStack is) {
		items.add(is);
	}
	
	public HInventoryType getInventoryType() {
		return inventoryType;
	}
	
	public HyperPlayer getHyperPlayer() {
		if (isPlayerInventory()) return hc.getHyperPlayerManager().getHyperPlayer(owner);
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

	
	public void setQuantity(int slot, int quantity, boolean update) {
		if (quantity <= 0) quantity = 0;
		if (quantity == 0) {
			items.set(slot, hc.getBlankStack());
		} else {
			items.get(slot).setAmount(quantity);
		}
		if (update) {
			if (inventoryType == HInventoryType.PLAYER) {
				hc.getMC().setItemQuantity(getHyperPlayer(), quantity, slot);
			} else if (inventoryType == HInventoryType.CHEST) {
				hc.getMC().setItemQuantity(location, quantity, slot);
			}
		}
	}
	public void setItem(int slot, HItemStack item, boolean update) {
		items.set(slot, item);
		if (update) {
			if (inventoryType == HInventoryType.PLAYER) {
				hc.getMC().setItem(getHyperPlayer(), item, slot);
			} else if (inventoryType == HInventoryType.CHEST) {
				hc.getMC().setItem(location, item, slot);
			}
		}
	}

	
	public void setOwner(String owner) {
		this.owner = owner;
	}
	
	public void setLocation(HLocation location) {
		this.location = location;
	}

	
	public void updateInventory() {
		hc.getMC().setInventory(this);
	}
	
	public void updateLore(int slot) {
		HItemStack is = getItem(slot);
		if (is.isBlank() || is.getItemMeta() == null) return;
		hc.getMC().setItemLore(this, is.getItemMeta().getLore(), slot);
	}
	
	public void add(int addAmount, HItemStack addStack) {
		int maxStackSize = addStack.getMaxStackSize();
		if (inventoryType == HInventoryType.PLAYER) {
			HItemStack heldStack = getHeldItem();
			if (heldStack.isBlank() || addStack.equals(heldStack)) {
				if (heldStack.isBlank()) {
					HItemStack newStack = new HItemStack(addStack);
					if (addAmount > maxStackSize) {
						newStack.setAmount(maxStackSize);
						setItem(heldSlot, newStack, true);
						addAmount -= maxStackSize;
					} else {
						newStack.setAmount(addAmount);
						setItem(heldSlot, newStack, true);
						addAmount = 0;
					}
				} else if (addStack.equals(heldStack)) {
					int spaceInStack = maxStackSize - heldStack.getAmount();
					if (spaceInStack > 0) {
						if (spaceInStack < addAmount) {
							addAmount -= spaceInStack;
							setQuantity(heldSlot, maxStackSize, true);
						} else {
							setQuantity(heldSlot, addAmount + heldStack.getAmount(), true);
							addAmount = 0;
						}
					}
				}
				if (addAmount <= 0) return;
			}
		}
		for (int slot = 0; slot < getSize(); slot++) {
			HItemStack currentItem = getItem(slot);
			if (currentItem.isBlank()) {
				HItemStack newStack = new HItemStack(addStack);
				if (addAmount > maxStackSize) {
					newStack.setAmount(maxStackSize);
					setItem(slot, newStack, true);
					addAmount -= maxStackSize;
				} else {
					newStack.setAmount(addAmount);
					setItem(slot, newStack, true);
					addAmount = 0;
				}
			} else if (addStack.equals(currentItem)) {
				int spaceInStack = maxStackSize - currentItem.getAmount();
				if (spaceInStack == 0) continue;
				if (spaceInStack < addAmount) {
					addAmount -= spaceInStack;
					setQuantity(slot, maxStackSize, true);
				} else {
					setQuantity(slot, addAmount + currentItem.getAmount(), true);
					addAmount = 0;
				}
			}
			if (addAmount <= 0) break;
		}
		if (addAmount != 0) hc.gSDL().getErrorWriter().writeError("HInventory add() failure; " + addAmount + " remaining.");
		//updateInventory();
	}
	
	
	public double remove(int removeAmount, HItemStack removeStack) {
		double actuallyRemoved = 0.0;
		if (inventoryType == HInventoryType.PLAYER) {
			HItemStack heldStack = getHeldItem();
			if (removeStack.equals(heldStack)) {
				if (removeAmount >= heldStack.getAmount()) {
					actuallyRemoved += heldStack.getTrueAmount();
					setQuantity(heldSlot, 0, true);
					removeAmount -= heldStack.getAmount();
				} else {
					actuallyRemoved += removeAmount * heldStack.getDurabilityPercent();
					setQuantity(heldSlot, heldStack.getAmount() - removeAmount, true);
					removeAmount = 0;
				}
			}
		}
		int slot = 0;
		while (removeAmount > 0) {
			HItemStack currentItem = getItem(slot);
			if (removeStack.equals(currentItem)) {
				if (removeAmount >= currentItem.getAmount()) {
					actuallyRemoved += currentItem.getTrueAmount();
					setQuantity(slot, 0, true);
					removeAmount -= currentItem.getAmount();
				} else {
					actuallyRemoved += removeAmount * currentItem.getDurabilityPercent();
					setQuantity(slot, currentItem.getAmount() - removeAmount, true);
					removeAmount = 0;
				}
			}
			slot++;
			if (slot >= getSize()) break;
		}
		if (removeAmount != 0) hc.gSDL().getErrorWriter().writeError("HInventory remove() failure.  Items not successfully removed; amount = '" + removeAmount + "'");
		//updateInventory();
		return actuallyRemoved;
	}
	
	public int findSlot(HItemStack stack) {
		Iterator<HItemStack> iterator = items.iterator();
		int slot = 0;
		while (iterator.hasNext()) {
			HItemStack item = iterator.next();
			if (item.equals(stack)) return slot;
			slot++;
		}
		return -1;
	}

	
	public int getAvailableSpace(HItemStack stack) {
		int availableSpace = 0;
		for (int slot = 0; slot < getSize(); slot++) {
			HItemStack currentItem = getItem(slot);
			if (currentItem == null || currentItem.isBlank()) {
				availableSpace += stack.getMaxStackSize();
			} else if (stack.equals(currentItem)) {
				availableSpace += (stack.getMaxStackSize() - currentItem.getAmount());
			}
		}
		return availableSpace;
	}
	
	public int count(HItemStack stack) {
		int itemCount = 0;
		for (int slot = 0; slot < getSize(); slot++) {
			HItemStack currentItem = getItem(slot);
			if (currentItem.equals(stack)) {
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
			if (heldItem.equals(stack)) {
				heldslot = getHeldSlot();
				totalDamage += heldItem.getDurabilityPercent();
				totalItems += heldItem.getAmount();
				if (totalItems >= amount) return totalDamage;
			}
		}
		for (int slot = 0; slot < getSize(); slot++) {
			if (slot == heldslot) continue;
			HItemStack ci = getItem(slot);
			if (!ci.equals(stack)) continue;
			totalDamage += ci.getDurabilityPercent();
			totalItems += ci.getAmount();
			if (totalItems >= amount) break;
		}
		totalDamage /= amount;
		return totalDamage;
	}
	


}
