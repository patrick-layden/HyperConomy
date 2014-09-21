package regalowl.hyperconomy.hyperobject;

import java.awt.Image;
import java.net.URL;

import javax.imageio.ImageIO;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import regalowl.hyperconomy.HyperConomy;
import regalowl.hyperconomy.account.HyperPlayer;
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
		if (sis.getMaterialEnum() == Material.POTION) {
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
	public boolean isDurable() {
		SerializableItemStack sis = getSIS();
		if (sis.getMaterialEnum() != null && sis.getMaterialEnum().getMaxDurability() > 0) {
			return true;
		}
		return false;
	}
	

	@Override
	public double getSellPrice(double amount, HyperPlayer hp) {
		return super.getSellPrice(amount) * getDamageMultiplier((int)Math.ceil(amount), hp.getPlayer().getInventory());
	}

	@Override
	public int count(Inventory inventory) {
		int totalitems = 0;
		for (int slot = 0; slot < inventory.getSize(); slot++) {
			ItemStack currentItem = inventory.getItem(slot);
			if (matchesItemStack(currentItem)) {
				totalitems += currentItem.getAmount();
			}
		}
		return totalitems;
	}
	@Override
	public int getAvailableSpace(Inventory inventory) {
		HyperConomy hc = HyperConomy.hc;
		try {
			ItemStack stack = getItemStack();
			int maxstack = stack.getMaxStackSize();
			int availablespace = 0;
			for (int slot = 0; slot < inventory.getSize(); slot++) {
				ItemStack currentItem = inventory.getItem(slot);
				if (currentItem == null) {
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
	public ItemStack getItemStack(int amount) {
		SerializableItemStack sis = getSIS();
		ItemStack stack = sis.getItem();
		stack.setAmount(amount);
		return stack;
	}
	@Override
	public ItemStack getItemStack() {
		SerializableItemStack sis = getSIS();
		return sis.getItem();
	}
	@Override
	public boolean matchesItemStack(ItemStack stack) {
		SerializableItemStack thisSis = getSIS();
		if (stack == null) {return false;}
		SerializableItemStack sis = new SerializableItemStack(stack);
		return sis.equals(thisSis);
	}
	@Override
	public String getData() {
		return base64Item;
	}
	@Override
	public void setData(String data) {
		HyperConomy hc = HyperConomy.hc;
		this.base64Item = data;
		String statement = "UPDATE hyperconomy_objects SET DATA='" + data + "' WHERE NAME = '" + this.name + "' AND ECONOMY = '" + economy + "'";
		hc.getSQLWrite().addToQueue(statement);
		hc.getHyperEventHandler().fireHyperObjectModificationEvent(this);
	}

	
	@SuppressWarnings("deprecation")
	@Override
	public void add(int amount, Inventory inventory) {
		HyperConomy hc = HyperConomy.hc;
		try {
			ItemStack stack = getItemStack();
			int maxStack = stack.getMaxStackSize();
			for (int slot = 0; slot < inventory.getSize(); slot++) {
				ItemStack currentItem = inventory.getItem(slot);
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
			if (inventory.getType() == InventoryType.PLAYER) {
				Player p = (Player) inventory.getHolder();
				p.updateInventory();
			}
		} catch (Exception e) {
			hc.gDB().writeError(e);
		}
	}
	@Override
	public double remove(int amount, Inventory inventory) {
		HyperConomy hc = HyperConomy.hc;
		try {
			double amountRemoved = 0;
			if (inventory.getType() == InventoryType.PLAYER) {
				Player p = (Player) inventory.getHolder();
				ItemStack heldStack = p.getItemInHand();
				if (matchesItemStack(heldStack)) {
					if (amount >= heldStack.getAmount()) {
						amount -= heldStack.getAmount();
						amountRemoved += heldStack.getAmount() * getDurabilityPercent(heldStack);
						inventory.clear(p.getInventory().getHeldItemSlot());
					} else {
						amountRemoved += amount * getDurabilityPercent(heldStack);
						heldStack.setAmount(heldStack.getAmount() - amount);
						return amountRemoved;
					}
				}
			}
			for (int slot = 0; slot < inventory.getSize(); slot++) {
				ItemStack currentItem = inventory.getItem(slot);
				if (matchesItemStack(currentItem)) {
					if (amount >= currentItem.getAmount()) {
						amount -= currentItem.getAmount();
						amountRemoved += currentItem.getAmount() * getDurabilityPercent(currentItem);
						inventory.clear(slot);
					} else {
						amountRemoved += amount * getDurabilityPercent(currentItem);
						currentItem.setAmount(currentItem.getAmount() - amount);
						return amountRemoved;
					}
				}
			}
			if (amount != 0) {
				hc.gDB().writeError("remove() failure.  Items not successfully removed; amount = '" + amount + "'");
				return amountRemoved;	
			} else {
				return amountRemoved;
			}
		} catch (Exception e) {
			hc.gDB().writeError(e);
			return 0;
		}
	}
	

	@Override
	public double getDamageMultiplier(int amount, Inventory inventory) {
		HyperConomy hc = HyperConomy.hc;
		SerializableItemStack sis = getSIS();
		try {
			double damage = 0;
			if (!isDurable()) {return 1;}
			int totalitems = 0;
			int heldslot = -1;
			if (inventory.getType() == InventoryType.PLAYER) {
				Player p = (Player) inventory.getHolder();
				ItemStack ci = p.getItemInHand();
				if (ci.getType() == sis.getMaterialEnum() && !new HyperItemStack(ci).hasEnchants()) {
					damage += getDurabilityPercent(ci);
					totalitems++;
					heldslot = p.getInventory().getHeldItemSlot();
					if (totalitems >= amount) {return damage;}
				}
			}
			for (int slot = 0; slot < inventory.getSize(); slot++) {
				if (slot == heldslot) {continue;}
				ItemStack ci = inventory.getItem(slot);
				if (ci == null) {continue;}
				if (!(ci.getType() == sis.getMaterialEnum())) {continue;}
				if (new HyperItemStack(ci).hasEnchants()) {continue;}
				damage += getDurabilityPercent(ci);
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

	private double getDurabilityPercent(ItemStack stack) {
		double currentDurability = stack.getDurability();
		double maxDurability = stack.getType().getMaxDurability();
		if (maxDurability == 0) {return 1;}
		return Math.abs(1 - (currentDurability / maxDurability));
	}
	
	@Override
	public double getDurabilityPercent() {
		SerializableItemStack sis = getSIS();
		double durabilityMultiplier = 1;
		if (isDurable()) {
			double maxDurability = sis.getMaterialEnum().getMaxDurability();
			durabilityMultiplier = (1 - sis.getDurability() / maxDurability);
		}
		return durabilityMultiplier;
	}
	
	@Override
	public boolean isDamaged() {
		SerializableItemStack sis = getSIS();
		if (sis.getMaterialEnum().getMaxDurability() > 0) {
			if (sis.getDurability() > 0) {
				return true;
			}
		}
		return false;
	}

}
