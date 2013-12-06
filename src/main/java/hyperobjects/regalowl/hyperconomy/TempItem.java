package regalowl.hyperconomy;

import java.util.HashMap;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;
import org.bukkit.potion.Potion;

public class TempItem extends BasicObject implements HyperItem {

	private String material;
	private Material materialEnum;
	private int data;
	private int durability;

	

	public TempItem(String name, String economy, String displayName, String aliases, String type, String material, int data, int durability, double value, String isstatic, double staticprice, double stock, double median, String initiation, double startprice, double ceiling, double floor, double maxstock) {
		super(name, economy, displayName, aliases, type, value, isstatic, staticprice, stock, median, initiation, startprice, ceiling, floor, maxstock);
		this.material = material;
		this.materialEnum = Material.matchMaterial(material);
		this.data = data;
		this.durability = durability;
	}
	
	
	public String getMaterial() {
		return material;
	}
	public int getData() {
		return data;
	}
	public int getDurability() {
		return durability;
	}
	

	

	public void setMaterial(String material) {
		this.material = material;
		this.materialEnum = Material.matchMaterial(material);
	}
	public void setMaterial(Material material) {
		String materialS = material.toString();
		this.material = materialS;
		this.materialEnum = material;
	}
	public void setData(int data) {
		this.data = data;
	}
	public void setDurability(int durability) {
		this.durability = durability;
	}
	
	
	
	
	@Override
	public void setName(String name) {
		this.name = name;
	}
	@Override
	public void setEconomy(String economy) {
		this.economy = economy;
	}
	@Override
	public void setType(String type) {
		this.type = HyperObjectType.fromString(type);
	}
	@Override
	public void setValue(double value) {
		this.value = value;
	}
	@Override
	public void setIsstatic(String isstatic) {
		this.isstatic = isstatic;
	}
	@Override
	public void setStaticprice(double staticprice) {
		this.staticprice = staticprice;
	}
	@Override
	public void setStock(double stock) {
		this.stock = stock;
	}
	@Override
	public void setMedian(double median) {
		this.median = median;
	}
	@Override
	public void setInitiation(String initiation) {
		this.initiation = initiation;
	}
	@Override
	public void setStartprice(double startprice) {
		this.startprice = startprice;
	}
	@Override
	public void setCeiling(double ceiling) {
		this.ceiling = ceiling;
	}
	@Override
	public void setFloor(double floor) {
		this.floor = floor;
	}
	@Override
	public void setMaxstock(double maxstock) {
		this.maxstock = maxstock;
	}
	
	
	
	
	
	
	
	
	
	
	
	public boolean isDurable() {
		if (materialEnum != null && materialEnum.getMaxDurability() > 0) {
			return true;
		}
		return false;
	}
	public double getValue(int amount, HyperPlayer hp) {
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
			String info = "Calculation countItems() passed values name='" + getName() + "', amount='" + amount + "', player='" + hp.getName() + "'";
			hc.gDB().writeError(e, info);
			double totalvalue = 0;
			return totalvalue;
		}
	}
	
	public double getdurabilityPercent(ItemStack i) {
		try {
			double durabilitypercent = 1;
			try {
				double cdurability = i.getDurability();
				double maxdurability = i.getData().getItemType().getMaxDurability();
				durabilitypercent = (1 - (cdurability / maxdurability));
			} catch (Exception e) {
				durabilitypercent = 1;
			}
			if (durabilitypercent < 0) {
				durabilitypercent = 1;
			}
			return durabilitypercent;
		} catch (Exception e) {
			String info = "Calculation getdurabilityPercent() passed values ItemStack='" + i + "'";
			hc.gDB().writeError(e, info);
			double durabilitypercent = 1;
			return durabilitypercent;
		}
	}
	
	private double getDamageMultiplier(int amount, Inventory inventory) {
		try {
			double damage = 0;
			if (isDurable()) {
				int heldslot = -1;
				int totalitems = 0;
				HashMap<Integer, ? extends ItemStack> stacks = inventory.all(materialEnum);
				if (inventory.getType() == InventoryType.PLAYER) {
					Player p = (Player) inventory.getHolder();
					heldslot = p.getInventory().getHeldItemSlot();
					HyperItemStack his = new HyperItemStack(stacks.get(heldslot));
					if (p.getItemInHand().getType() == materialEnum && !his.hasenchants()) {
						damage = getdurabilityPercent(stacks.get(heldslot));
						totalitems++;
					}
				}
				for (int slot = 0; slot < inventory.getSize(); slot++) {
					if (slot == heldslot) {
						continue;
					}
					HyperItemStack his = new HyperItemStack(stacks.get(heldslot));
					if (stacks.get(slot) != null && totalitems < amount && !his.hasenchants()) {
						damage = getdurabilityPercent(stacks.get(slot)) + damage;
						totalitems++;
					}
				}
				damage = damage / amount;
			} else {
				damage = 1;
			}
			return damage;
		} catch (Exception e) {
			String info = "Calculation getDamage() passed values amount='" + amount + "'";
			hc.gDB().writeError(e, info);
			double damage = 0;
			return damage;
		}	
	}
	
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
			String info = "Transaction getAvailableSpace() passed values inventory='" + inventory.getName() + "', data='" + data + "'";
			hc.gDB().writeError(e, info);
			int availablespace = 0;
			return availablespace;
		}
	}
	
	@SuppressWarnings("deprecation")
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
			String info = "Transaction addItems() passed values inventory='" + inventory.getName() + "', data='" + data + "', amount='" + amount + "'";
			hc.gDB().writeError(e, info);
		}
	}
	
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
				hc.gDB().writeError("removesoldItems() failure.  Items not successfully removed.  Passed data = '" + data + "', amount = '" + amount + "'");
				return amountRemoved;	
			} else {
				return amountRemoved;
			}
		} catch (Exception e) {
			String info = "Transaction removeSoldItems() passed values inventory='" + inventory.getName() + "', data='" + data + "', amount='" + amount + "'";
			hc.gDB().writeError(e, info);
			return -1;
		}
	}


	@SuppressWarnings("deprecation")
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
	
	public ItemStack getItemStack() {
		return getItemStack(1);
	}
	
	public Material getMaterialEnum() {
		return materialEnum;
	}

}
