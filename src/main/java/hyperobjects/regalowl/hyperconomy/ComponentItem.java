package regalowl.hyperconomy;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;
import org.bukkit.potion.Potion;


public class ComponentItem extends BasicObject implements HyperObject {

	protected String material;
	protected Material materialEnum;
	protected int data;
	protected int durability;

	

	public ComponentItem(String name, String economy, String displayName, String aliases, String type, String material, int data, int durability, double value, String isstatic, double staticprice, double stock, double median, String initiation, double startprice, double ceiling, double floor, double maxstock) {
		super(name, economy, displayName, aliases, type, value, isstatic, staticprice, stock, median, initiation, startprice, ceiling, floor, maxstock);
		this.material = material;
		this.materialEnum = Material.matchMaterial(material);
		this.data = data;
		this.durability = durability;
	}
	
	@Override
	public String getMaterial() {
		return material;
	}
	@Override
	public Material getMaterialEnum() {
		return materialEnum;
	}
	@Override
	public int getData() {
		return data;
	}
	@Override
	public int getDurability() {
		return durability;
	}
	

	
	@Override
	public void setMaterial(String material) {
		String statement = "UPDATE hyperconomy_objects SET MATERIAL='" + material + "' WHERE NAME = '" + name + "' AND ECONOMY = '" + economy + "'";
		hc.getSQLWrite().addToQueue(statement);
		this.material = material;
		this.materialEnum = Material.matchMaterial(material);
	}
	@Override
	public void setMaterial(Material material) {
		String materialS = material.toString();
		String statement = "UPDATE hyperconomy_objects SET MATERIAL='" + materialS + "' WHERE NAME = '" + name + "' AND ECONOMY = '" + economy + "'";
		hc.getSQLWrite().addToQueue(statement);
		this.material = materialS;
		this.materialEnum = material;
	}
	@Override
	public void setData(int data) {
		String statement = "UPDATE hyperconomy_objects SET DATA='" + data + "' WHERE NAME = '" + name + "' AND ECONOMY = '" + economy + "'";
		hc.getSQLWrite().addToQueue(statement);
		this.data = data;
	}
	@Override
	public void setDurability(int durability) {
		String statement = "UPDATE hyperconomy_objects SET DURABILITY='" + durability + "' WHERE NAME = '" + name + "' AND ECONOMY = '" + economy + "'";
		hc.getSQLWrite().addToQueue(statement);
		this.durability = durability;
	}
	@Override
	public boolean isDurable() {
		if (materialEnum != null && materialEnum.getMaxDurability() > 0) {
			return true;
		}
		return false;
	}
	@Override
	public double getSellPrice(int amount, HyperPlayer hp) {
		try {
			double totalvalue = 0;
			double damage = 0;
			boolean isstatic = false;
			isstatic = Boolean.parseBoolean(getIsstatic());
			if (!isstatic) {
				damage = getDamageMultiplier(amount, hp.getPlayer().getInventory());
				double shopstock = 0;
				double value = 0;
				double median = 0;
				double icost = 0;
				shopstock = getTotalStock();
				value = getValue();
				median = getMedian();
				icost = getStartprice();
				if (icost >= ((median * value) / shopstock) && shopstock > 1) {
					setInitiation("false");
				}
				int counter = 0;
				while (counter < amount) {
					double price = ((median * value) / shopstock);
					price = applyCeilingFloor(price);
					shopstock = shopstock + 1;
					totalvalue = totalvalue + price;
					counter++;
				}
				totalvalue = totalvalue * damage;
				Boolean initial = false;
				initial = Boolean.parseBoolean(getInitiation());
				if (initial == true) {
					double ivalue = applyCeilingFloor(icost);
					totalvalue = ivalue * damage * amount;
				}
			} else {
				damage = getDamageMultiplier(amount, hp.getPlayer().getInventory());
				double statprice = getStaticprice();
				double svalue = applyCeilingFloor(statprice);
				totalvalue = svalue * amount * damage;
			}
			return cf.twoDecimals(totalvalue);
		} catch (Exception e) {
			String info = "getSellPrice() passed values name='" + getName() + "', amount='" + amount + "', player='" + hp.getName() + "'";
			hc.gDB().writeError(e, info);
			double totalvalue = 0;
			return totalvalue;
		}
	}

	@Override
	public int count(Inventory inventory) {
		int totalitems = 0;
		for (int slot = 0; slot < inventory.getSize(); slot++) {
			ItemStack stack = inventory.getItem(slot);
			HyperItemStack his = new HyperItemStack(stack);
			if (stack != null && !his.hasenchants()) {
				if (stack.getType() == materialEnum && his.getDamageValue() == data) {
					totalitems += stack.getAmount();
				}
			}
		}
		return totalitems;
	}
	@Override
	public int getAvailableSpace(Inventory inventory) {
		try {
			ItemStack stack = getItemStack();
			int maxstack = stack.getMaxStackSize();
			int availablespace = 0;
			for (int slot = 0; slot < inventory.getSize(); slot++) {
				ItemStack citem = inventory.getItem(slot);
				HyperItemStack his = new HyperItemStack(citem);
				if (citem == null) {
					availablespace += maxstack;
				} else if (citem.getType() == materialEnum && his.getDamageValue() == data) {
					availablespace += (maxstack - citem.getAmount());
				}
			}
			return availablespace;
		} catch (Exception e) {
			String info = "getAvailableSpace() passed values inventory='" + inventory.getName() + "', data='" + data + "'";
			hc.gDB().writeError(e, info);
			int availablespace = 0;
			return availablespace;
		}
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public ItemStack getItemStack(int amount) {
		MaterialData md = new MaterialData(materialEnum);
		md.setData((byte) data);
		ItemStack stack = md.toItemStack();
		if (materialEnum == Material.POTION && data != 0) {
			Potion pot = Potion.fromDamage(data);
			stack = pot.toItemStack(amount);
		}
		stack.setAmount(amount);
		return stack;
	}
	@Override
	public ItemStack getItemStack() {
		return getItemStack(1);
	}

	
	@SuppressWarnings("deprecation")
	@Override
	public void add(int amount, Inventory inventory) {
		try {
			ItemStack stack = getItemStack();
			int maxstack = stack.getMaxStackSize();
			for (int slot = 0; slot < inventory.getSize(); slot++) {
				int pamount = 0;
				ItemStack citem = inventory.getItem(slot);
				HyperItemStack his = new HyperItemStack(citem);
				if (citem != null && citem.getType() == materialEnum && data == his.getDamageValue()) {
					int currentamount = citem.getAmount();
					if ((maxstack - currentamount) >= amount) {
						pamount = amount;
						citem.setAmount(pamount + currentamount);
					} else {
						pamount = maxstack - currentamount;
						citem.setAmount(maxstack);
					}
				} else if (inventory.getItem(slot) == null) {
					if (amount > maxstack) {
						pamount = maxstack;
					} else {
						pamount = amount;
					}
					stack.setAmount(pamount);
					inventory.setItem(slot, stack);
				}
				amount -= pamount;
				if (amount <= 0) {
					break;
				}
			}
			if (amount != 0) {
				String info = "Error adding items to inventory; + '" + amount + "' remaining. Transaction addBoughtItems() passed values inventory='" + inventory.getName() + "', data='" + data + "', amount='" + amount + "'";
				hc.gDB().writeError(info);
			}
			if (inventory.getType() == InventoryType.PLAYER) {
				Player p = (Player) inventory.getHolder();
				p.updateInventory();
			}
		} catch (Exception e) {
			String info = "add() passed values inventory='" + inventory.getName() + "', data='" + data + "', amount='" + amount + "'";
			hc.gDB().writeError(e, info);
		}
	}
	@Override
	public double remove(int amount, Inventory inventory) {
		try {
			int remainingAmount = 0;
			double amountRemoved = 0;
			remainingAmount = amount;
			if (inventory.getType() == InventoryType.PLAYER) {
				Player p = (Player) inventory.getHolder();
				ItemStack hstack = p.getItemInHand();
				HyperItemStack his = new HyperItemStack(hstack);
				if (hstack != null && !his.hasenchants()) {
					if (hstack.getType() == materialEnum && his.getDamageValue() == data) {
						if (remainingAmount >= hstack.getAmount()) {
							remainingAmount -= hstack.getAmount();
							amountRemoved += hstack.getAmount() * his.getDurabilityMultiplier();
							inventory.clear(p.getInventory().getHeldItemSlot());
						} else {
							amountRemoved += remainingAmount * his.getDurabilityMultiplier();
							hstack.setAmount(hstack.getAmount() - remainingAmount);
							return amountRemoved;
						}
					}
				}
			}
			for (int i = 0; i < inventory.getSize(); i++) {
				ItemStack stack = inventory.getItem(i);
				HyperItemStack his = new HyperItemStack(stack);
				if (stack != null && !his.hasenchants()) {
					if (stack.getType() == materialEnum && his.getDamageValue() == data) {
						if (remainingAmount >= stack.getAmount()) {
							remainingAmount -= stack.getAmount();
							amountRemoved += stack.getAmount() * his.getDurabilityMultiplier();
							inventory.clear(i);
						} else {
							amountRemoved += remainingAmount * his.getDurabilityMultiplier();
							stack.setAmount(stack.getAmount() - remainingAmount);
							return amountRemoved;
						}
					}
				}
			}
			if (remainingAmount != 0) {
				hc.gDB().writeError("remove() failure.  Items not successfully removed.  Passed data = '" + data + "', amount = '" + amount + "'");
				return amountRemoved;	
			} else {
				return amountRemoved;
			}
		} catch (Exception e) {
			String info = "remove() passed values inventory='" + inventory.getName() + "', data='" + data + "', amount='" + amount + "'";
			hc.gDB().writeError(e, info);
			return -1;
		}
	}
	

	@Override
	public double getDamageMultiplier(int amount, Inventory inventory) {
		try {
			double damage = 0;
			if (!isDurable()) {return 1;}
			int totalitems = 0;
			int heldslot = -1;
			if (inventory.getType() == InventoryType.PLAYER) {
				Player p = (Player) inventory.getHolder();
				ItemStack ci = p.getItemInHand();
				if (ci.getType() == materialEnum && !new HyperItemStack(ci).hasenchants()) {
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
				if (!(ci.getType() == materialEnum)) {continue;}
				if (new HyperItemStack(ci).hasenchants()) {continue;}
				damage += getDurabilityPercent(ci);
				totalitems++;
				if (totalitems >= amount) {break;}
			}
			damage /= amount;
			return damage;
		} catch (Exception e) {
			String info = "getDamageMultiplier() passed values amount='" + amount + "'";
			hc.gDB().writeError(e, info);
			return 0;
		}
	}
	
	private double getDurabilityPercent(ItemStack stack) {
		try {
			double durabilityPercent = 1;
			try {
				double currentDurability = stack.getDurability();
				double maxDurability = stack.getData().getItemType().getMaxDurability();
				durabilityPercent = Math.abs(1 - (currentDurability / maxDurability));
			} catch (Exception e) {
				durabilityPercent = 1;
			}
			return durabilityPercent;
		} catch (Exception e) {
			String info = "getDurabilityPercent() passed values ItemStack='" + stack + "'";
			hc.gDB().writeError(e, info);
			return 1;
		}
	}

}
