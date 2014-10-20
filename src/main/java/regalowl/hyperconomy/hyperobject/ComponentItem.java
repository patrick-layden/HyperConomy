package regalowl.hyperconomy.hyperobject;

import java.awt.Image;
import java.net.URL;

import javax.imageio.ImageIO;


import regalowl.hyperconomy.HyperConomy;
import regalowl.hyperconomy.account.HyperPlayer;
import regalowl.hyperconomy.event.HyperObjectModificationEvent;
import regalowl.hyperconomy.serializable.SerializableInventory;
import regalowl.hyperconomy.serializable.SerializableInventoryType;
import regalowl.hyperconomy.serializable.SerializableItemStack;


public class ComponentItem extends BasicObject implements HyperObject {

	private static final long serialVersionUID = -845888542311735442L;
	private String base64Item;

	

	public ComponentItem(String name, String economy, String displayName, String aliases, String type, double value, String isstatic, double staticprice, double stock, double median, String initiation, double startprice, double ceiling, double floor, double maxstock, String base64ItemData) {
		super(name, economy, displayName, aliases, type, value, isstatic, staticprice, stock, median, initiation, startprice, ceiling, floor, maxstock);
		this.base64Item = base64ItemData;
	}
	
	private SerializableItemStack getSIS() {
		return new SerializableItemStack(base64Item);
	}
	
	@Override
	public Image getImage(int width, int height) {
		HyperConomy hc = HyperConomy.hc;
		Image i = null;
		URL url = null;
		SerializableItemStack sis = getSIS();
		if (sis.getMaterial().equalsIgnoreCase("POTION")) {
			url = hc.getClass().getClassLoader().getResource("Images/potion.png");
		} else {
			url = hc.getClass().getClassLoader().getResource("Images/" + sis.getMaterial().toLowerCase() + "_" + sis.getData() + ".png");
		}
		try {
			i = ImageIO.read(url);
			if (i != null) {
				return i.getScaledInstance(width, height, Image.SCALE_DEFAULT);
			}
		} catch (Exception e) {}
		return null;
	}


	@Override
	public double getSellPrice(double amount, HyperPlayer hp) {
		return super.getSellPrice(amount) * getDamageMultiplier((int)Math.ceil(amount), hp.getInventory());
	}

	@Override
	public int count(SerializableInventory inventory) {
		int totalitems = 0;
		for (int slot = 0; slot < inventory.getSize(); slot++) {
			SerializableItemStack currentItem = inventory.getItem(slot);
			if (matchesItemStack(currentItem)) {
				totalitems += currentItem.getAmount();
			}
		}
		return totalitems;
	}
	@Override
	public int getAvailableSpace(SerializableInventory inventory) {
		HyperConomy hc = HyperConomy.hc;
		try {
			SerializableItemStack stack = getItem();
			int maxstack = stack.getMaxStackSize();
			int availablespace = 0;
			for (int slot = 0; slot < inventory.getSize(); slot++) {
				SerializableItemStack currentItem = inventory.getItem(slot);
				if (currentItem == null || currentItem.isBlank()) {
					availablespace += maxstack;
				} else if (matchesItemStack(currentItem)) {
					availablespace += (maxstack - currentItem.getAmount());
				}
			}
			return availablespace;
		} catch (Exception e) {
			hc.gDB().writeError(e);
			return 0;
		}
	}
	
	@Override
	public SerializableItemStack getItemStack(int amount) {
		SerializableItemStack sis = getSIS();
		sis.setAmount(amount);
		return sis;
	}
	@Override
	public SerializableItemStack getItem() {
		return getSIS();
	}
	@Override
	public boolean matchesItemStack(SerializableItemStack stack) {
		SerializableItemStack thisSis = getSIS();
		if (stack == null) {return false;}
		return stack.equals(thisSis);
	}
	@Override
	public String getData() {
		return base64Item;
	}
	
	@Override
	public void setItemStack(SerializableItemStack stack) {
		setData(stack.serialize());
	}
	@Override
	public void setData(String data) {
		HyperConomy hc = HyperConomy.hc;
		this.base64Item = data;
		String statement = "UPDATE hyperconomy_objects SET DATA='" + data + "' WHERE NAME = '" + this.name + "' AND ECONOMY = '" + economy + "'";
		hc.getSQLWrite().addToQueue(statement);
		hc.getHyperEventHandler().fireEvent(new HyperObjectModificationEvent(this));
	}


	@Override
	public void add(int amount, SerializableInventory inventory) {
		HyperConomy hc = HyperConomy.hc;
		try {
			SerializableItemStack stack = getItem();
			int maxStack = stack.getMaxStackSize();
			for (int slot = 0; slot < inventory.getSize(); slot++) {
				SerializableItemStack currentItem = inventory.getItem(slot);
				if (matchesItemStack(currentItem)) {
					int stackSize = currentItem.getAmount();
					int availableSpace = maxStack - stackSize;
					if (availableSpace == 0) {continue;}
					if (availableSpace < amount) {
						amount -= availableSpace;
						currentItem.setAmount(maxStack);
					} else {
						currentItem.setAmount(amount + stackSize);
						amount = 0;
					}
				} else if (inventory.getItem(slot) == null) {
					if (amount > maxStack) {
						stack.setAmount(maxStack);
						inventory.setItem(slot, stack);
						amount -= maxStack;
					} else {
						stack.setAmount(amount);
						inventory.setItem(slot, stack);
						amount = 0;
					}
				}
				if (amount <= 0) {break;}
			}
			if (amount != 0) {
				String info = "ComponentItem add() failure; " + amount + " remaining.";
				hc.gDB().writeError(info);
			}
			inventory.updateInventory();
		} catch (Exception e) {
			hc.gDB().writeError(e);
		}
	}
	@Override
	public double remove(int amount, SerializableInventory inventory) {
		HyperConomy hc = HyperConomy.hc;
		try {
			double amountRemoved = 0;
			if (inventory.getInventoryType() == SerializableInventoryType.PLAYER) {
				SerializableItemStack heldStack = inventory.getHeldItem();
				if (matchesItemStack(heldStack)) {
					if (amount >= heldStack.getAmount()) {
						amount -= heldStack.getAmount();
						amountRemoved += heldStack.getAmount() * heldStack.getDurabilityPercent();
						inventory.clearSlot(inventory.getHeldSlot());
					} else {
						amountRemoved += amount * heldStack.getDurabilityPercent();
						heldStack.setAmount(heldStack.getAmount() - amount);
						inventory.updateInventory();
						return amountRemoved;
					}
				}
			}
			for (int slot = 0; slot < inventory.getSize(); slot++) {
				SerializableItemStack currentItem = inventory.getItem(slot);
				if (matchesItemStack(currentItem)) {
					if (amount >= currentItem.getAmount()) {
						amount -= currentItem.getAmount();
						amountRemoved += currentItem.getAmount() * currentItem.getDurabilityPercent();
						inventory.clearSlot(slot);
					} else {
						amountRemoved += amount * currentItem.getDurabilityPercent();
						currentItem.setAmount(currentItem.getAmount() - amount);
						inventory.updateInventory();
						return amountRemoved;
					}
				}
			}
			if (amount != 0) {
				hc.gDB().writeError("remove() failure.  Items not successfully removed; amount = '" + amount + "'");
				inventory.updateInventory();
				return amountRemoved;	
			} else {
				inventory.updateInventory();
				return amountRemoved;
			}
		} catch (Exception e) {
			hc.gDB().writeError(e);
			return 0;
		}
	}
	

	@Override
	public double getDamageMultiplier(int amount, SerializableInventory inventory) {
		HyperConomy hc = HyperConomy.hc;
		SerializableItemStack sis = getSIS();
		try {
			double damage = 0;
			if (!isDurable()) {return 1;}
			int totalitems = 0;
			int heldslot = -1;
			if (inventory.getInventoryType() == SerializableInventoryType.PLAYER) {
				SerializableItemStack ci = inventory.getHeldItem();
				if (ci.equals(sis)) {
					damage += ci.getDurabilityPercent();
					totalitems++;
					heldslot = inventory.getHeldSlot();
					if (totalitems >= amount) {return damage;}
				}
			}
			for (int slot = 0; slot < inventory.getSize(); slot++) {
				if (slot == heldslot) {continue;}
				SerializableItemStack ci = inventory.getItem(slot);
				if (!ci.equals(sis)) {continue;}
				damage += ci.getDurabilityPercent();
				totalitems++;
				if (totalitems >= amount) {break;}
			}
			damage /= amount;
			if (damage == 0) {return 1;}
			return damage;
		} catch (Exception e) {
			String info = "getDamageMultiplier() passed values amount='" + amount + "'";
			hc.gDB().writeError(e, info);
			return 0;
		}
	}


}
