package regalowl.hyperconomy;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.material.MaterialData;
import org.bukkit.potion.Potion;

public class InventoryManipulation {

	
	
	private HyperConomy hc;

	
	InventoryManipulation() {
		hc = HyperConomy.hc;
	}
	
	
	
	
	
	/**
	 * 
	 * 
	 * This function counts the number of the specified item in a player's
	 * inventory. It ignores durability.
	 * 
	 */
	public int countItems(int id, int data, Inventory inventory) {
		try {
			int totalitems = 0;
			Calculation calc = hc.getCalculation();
			data = calc.newData(id, data);
			ItemStack[] stacks = inventory.getContents();
			for (ItemStack stack:stacks) {
				if (stack != null && !hasenchants(stack)) {
					int stackid = stack.getTypeId();
					int stackdata = calc.getDamageValue(stack);
					if (stackid == id && stackdata == data) {
						totalitems += stack.getAmount();
					}
				}
			}
			return totalitems;
		} catch (Exception e) {
			int totalitems = 0;
			String info = "Transaction countItems() passed values inventory='" + inventory.getName() + "', id='" + id + "', data='" + data + "'";
			new HyperError(e, info);
			return totalitems;
		}
	}


	/**
	 * 
	 * 
	 * This function determines how much more of an item a player's inventory
	 * can hold.
	 * 
	 */
	public int getAvailableSpace(int id, int data, Inventory inventory) {
		try {
			Calculation calc = hc.getCalculation();
			MaterialData md = new MaterialData(id, (byte) data);
			ItemStack stack = md.toItemStack();
			int maxstack = stack.getMaxStackSize();
			int availablespace = 0;
			for (int slot = 0; slot < inventory.getSize(); slot++) {
				ItemStack citem = inventory.getItem(slot);
				if (citem == null) {
					availablespace += maxstack;
				} else if (citem.getTypeId() == id && calc.getDamageValue(citem) == data) {
					availablespace += (maxstack - citem.getAmount());
				}
			}
			return availablespace;
		} catch (Exception e) {
			String info = "Transaction getAvailableSpace() passed values inventory='" + inventory.getName() + "', id='" + id + "', data='" + data + "'";
			new HyperError(e, info);
			int availablespace = 0;
			return availablespace;
		}
	}
	



	/**
	 * 
	 * 
	 * This function adds purchased items to a player's inventory.
	 * 
	 */
	@SuppressWarnings("deprecation")
	public void addItems(int amount, int id, int data, Inventory inventory) {
		Calculation calc = hc.getCalculation();
		try {
			MaterialData md = new MaterialData(id, (byte) data);
			ItemStack stack = md.toItemStack();
			int maxstack = stack.getMaxStackSize();
			for (int slot = 0; slot < inventory.getSize(); slot++) {
				int pamount = 0;
				ItemStack citem = inventory.getItem(slot);
				if (citem != null && citem.getTypeId() == id && data == calc.getDamageValue(citem)) {
					int currentamount = citem.getAmount();
					if ((maxstack - currentamount) >= amount) {
						pamount = amount;
						citem.setAmount(pamount + currentamount);
					} else {
						pamount = maxstack - currentamount;
						citem.setAmount(maxstack);
					}
				} else if (inventory.getItem(slot) == null) {
					if (id == 373 && data != 0) {
						Potion pot = Potion.fromDamage(data);
						stack = pot.toItemStack(amount);
					} else {
						stack = md.toItemStack();
					}
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
				String info = "Error adding items to inventory; + '" + amount + "' remaining. Transaction addBoughtItems() passed values inventory='" + inventory.getName() + "', id='" + id + "', data='" + data + "', amount='" + amount + "'";
				new HyperError(info);
			}
			if (inventory.getType() == InventoryType.PLAYER) {
				Player p = (Player) inventory.getHolder();
				p.updateInventory();
			}
		} catch (Exception e) {
			String info = "Transaction addItems() passed values inventory='" + inventory.getName() + "', id='" + id + "', data='" + data + "', amount='" + amount + "'";
			new HyperError(e, info);
		}
	}

	/**
	 * 
	 * 
	 * This function removes the items that a player has sold from their
	 * inventory. The amount is the number of items sold, and it requires the
	 * player and the item's id and data.
	 * 
	 */
	public boolean removeItems(int id, int data, int amount, Inventory inventory) {
		try {
			int oamount = amount;
			Calculation calc = hc.getCalculation();
			data = calc.newData(id, data);
			if (inventory.getType() == InventoryType.PLAYER) {
				Player p = (Player) inventory.getHolder();
				ItemStack hstack = p.getItemInHand();
				if (hstack != null && !hasenchants(hstack)) {
					int stackid = hstack.getTypeId();
					int stackdata = calc.getDamageValue(hstack);
					if (stackid == id && stackdata == data) {
						if (amount >= hstack.getAmount()) {
							amount -= hstack.getAmount();
							inventory.clear(p.getInventory().getHeldItemSlot());
						} else {
							hstack.setAmount(hstack.getAmount() - amount);
							return true;
						}
					}
				}
			}
			for (int i = 0; i < inventory.getSize(); i++) {
				ItemStack stack = inventory.getItem(i);
				if (stack != null && !hasenchants(stack)) {
					int stackid = stack.getTypeId();
					int stackdata = calc.getDamageValue(stack);
					if (stackid == id && stackdata == data) {
						if (amount >= stack.getAmount()) {
							amount -= stack.getAmount();
							inventory.clear(i);
						} else {
							stack.setAmount(stack.getAmount() - amount);
							return true;
						}
					}
				}
			}
			if (amount != 0) {
				new HyperError("removesoldItems() failure.  Items not successfully removed.  Passed id = '" + id + "', data = '" + data + "', amount = '" + oamount + "'");
				return false;	
			} else {
				return true;
			}
		} catch (Exception e) {
			String info = "Transaction removeSoldItems() passed values inventory='" + inventory.getName() + "', id='" + id + "', data='" + data + "', amount='" + amount + "'";
			new HyperError(e, info);
			return false;
		}
	}
	
	
	
	public int getbarxpPoints(Player player) {
		int lvl = player.getLevel();
		int exppoints = (int) Math.floor(((3.5 * lvl) + 6.7) * player.getExp() + .5);
		return exppoints;
	}

	public int getxpfornextLvl(int lvl) {
		int exppoints = (int) Math.floor(((3.5 * lvl) + 6.7) + .5);
		return exppoints;
	}

	public int getlvlxpPoints(int lvl) {
		int exppoints = (int) Math.floor((1.75 * Math.pow(lvl, 2)) + (5 * lvl) + .5);
		return exppoints;
	}

	public int gettotalxpPoints(Player player) {
		int lvl = player.getLevel();
		int lvlxp = getlvlxpPoints(lvl);
		int barxp = getbarxpPoints(player);
		int totalxp = lvlxp + barxp;
		return totalxp;
	}

	public int getlvlfromXP(int exp) {
		double lvlraw = (Math.sqrt((exp * 7.0) + 25.0) - 5.0) * (2.0 / 7.0);
		int lvl = (int) Math.floor(lvlraw);
		if ((double) lvl > lvlraw) {
			lvl = lvl - 1;
		}
		return lvl;
	}
	
	
	/**
	 * 
	 * 
	 * This function checks if an item is enchanted.
	 * 
	 */
	public boolean hasenchants(ItemStack stack) {
		try {
			boolean hasenchants = false;
			if (stack != null && !stack.getType().equals(Material.AIR)) {
				if (stack.getType().equals(Material.ENCHANTED_BOOK)) {
					EnchantmentStorageMeta emeta = (EnchantmentStorageMeta)stack.getItemMeta();
					hasenchants = emeta.hasStoredEnchants();
				} else {
					hasenchants = stack.getItemMeta().hasEnchants();
				}
			}
			return hasenchants;
		} catch (Exception e) {
			new HyperError(e, "Passed stack: type = '" + stack.getType().toString() + "', amount = '" + stack.getAmount() + "', id = '" + stack.getTypeId() + "'");
			return false;
		}
	}

	/**
	 * 
	 * 
	 * This function returns the class value (diamond, stone, etc.) of the given
	 * material.
	 * 
	 */
	public double getclassValue(EnchantmentClass eclass) {
		try {
			double value;
			if (eclass.equals(EnchantmentClass.LEATHER)) {
				value = (hc.getYaml().getConfig().getDouble("config.enchantment.classvalue.leather"));
			} else if (eclass.equals(EnchantmentClass.WOOD)) {
				value = (hc.getYaml().getConfig().getDouble("config.enchantment.classvalue.wood"));
			} else if (eclass.equals(EnchantmentClass.STONE)) {
				value = (hc.getYaml().getConfig().getDouble("config.enchantment.classvalue.stone"));
			} else if (eclass.equals(EnchantmentClass.CHAINMAIL)) {
				value = (hc.getYaml().getConfig().getDouble("config.enchantment.classvalue.chainmail"));
			} else if (eclass.equals(EnchantmentClass.IRON)) {
				value = (hc.getYaml().getConfig().getDouble("config.enchantment.classvalue.iron"));
			} else if (eclass.equals(EnchantmentClass.GOLD)) {
				value = (hc.getYaml().getConfig().getDouble("config.enchantment.classvalue.gold"));
			} else if (eclass.equals(EnchantmentClass.DIAMOND)) {
				value = (hc.getYaml().getConfig().getDouble("config.enchantment.classvalue.diamond"));
			} else if (eclass.equals(EnchantmentClass.BOOK)) {
				value = (hc.getYaml().getConfig().getDouble("config.enchantment.classvalue.book"));
			} else if (eclass.equals(EnchantmentClass.BOW)) {
				value = (hc.getYaml().getConfig().getDouble("config.enchantment.classvalue.bow"));
			} else {
				value = 0;
			}
			return value;
		} catch (Exception e) {
			String info = "ETransaction getclassValue() passed values eclass='" + eclass.toString() + "'";
			new HyperError(e, info);
			return 0;
		}
	}
	
	public boolean canEnchantItem(ItemStack stack) {
		if (stack == null || stack.getType().equals(Material.AIR)) {
			return false;
		}
		if (stack.getType().equals(Material.BOOK)) {
			return true;
		} else {
			boolean enchantable = false;
			for (Enchantment enchant:Enchantment.values()) {
				if (enchant.canEnchantItem(stack)) {
					enchantable = true;
				}
			}
			return enchantable;
		}
	}
	
	public boolean canAcceptEnchantment(ItemStack stack, Enchantment e) {
		if (e == null || stack == null || stack.getType().equals(Material.AIR)) {
			return false;
		}
		if (stack.getType().equals(Material.BOOK)) {
			return true;
		} else if (stack.getType().equals(Material.ENCHANTED_BOOK)) {
			return false;
		} else {
			ArrayList<Enchantment> enchants = listEnchantments(stack);
			for (Enchantment en:enchants) {
				if (en.conflictsWith(e)) {
					return false;
				}
			}
			return e.canEnchantItem(stack);
		}
	}
	
	/**
	 * @param stack An ItemStack
	 * @return ArrayList of all enchantments as String on the ItemStack
	 */
	public ArrayList<String> getEnchantments (ItemStack stack) {
		return convertEnchantmentMapToNames(getEnchantmentMap(stack));
	}
	
	public ArrayList<String> convertEnchantmentMapToNames(Map<Enchantment, Integer> enchants) {
		ArrayList<String> enchantments = new ArrayList<String>();
		if (enchants.isEmpty()) {
			return enchantments;
		}
		Iterator<Enchantment> ite = enchants.keySet().iterator();
		while (ite.hasNext()) {
			Enchantment e = ite.next();
			enchantments.add(hc.getDataFunctions().getEnchantNameWithoutLevel(e.getName()) + enchants.get(e));
		}
		return enchantments;
	}
	
	/**
	 * @param stack An ItemStack
	 * @return ArrayList of all enchantments as Enchantment on the ItemStack
	 */
	public ArrayList<Enchantment> listEnchantments (ItemStack stack) {
		ArrayList<Enchantment> enchantments = new ArrayList<Enchantment>();
		for (Enchantment ench:getEnchantmentMap(stack).keySet()) {
			enchantments.add(ench);
		}
		return enchantments;
	}
	
	public Map<Enchantment, Integer> getEnchantmentMap(ItemStack stack) {
		if (stack != null) {
			if (stack.getType().equals(Material.ENCHANTED_BOOK)) {
				EnchantmentStorageMeta emeta = (EnchantmentStorageMeta)stack.getItemMeta();
				return emeta.getStoredEnchants();
			} else {
				return stack.getEnchantments();
			}
		} else {
			return null;
		}
	}
	
	public int getEnchantmentLevel(ItemStack stack, Enchantment e) {
		if (e == null || stack == null) {
			return 0;
		}
		if (stack.getType().equals(Material.ENCHANTED_BOOK)) {
			EnchantmentStorageMeta emeta = (EnchantmentStorageMeta)stack.getItemMeta();
			return emeta.getStoredEnchantLevel(e);
		} else {
			return stack.getEnchantmentLevel(e);
		}
	}
	
	
	public boolean containsEnchantment(ItemStack stack, Enchantment e) {
		if (e == null || stack == null) {
			return false;
		}
		if (stack.getType().equals(Material.ENCHANTED_BOOK)) {
			EnchantmentStorageMeta emeta = (EnchantmentStorageMeta)stack.getItemMeta();
			return emeta.hasStoredEnchant(e);
		} else {
			return stack.containsEnchantment(e);
		}
	}
	
	public void removeEnchantment(ItemStack stack, Enchantment e) {
		if (e == null || stack == null) {
			return;
		}
		if (stack.getType().equals(Material.ENCHANTED_BOOK)) {
			stack.setType(Material.BOOK);
		} else {
			stack.removeEnchantment(e);
		}
	}
	
	public void addEnchantment(ItemStack stack, Enchantment e, int lvl) {
		if (e == null || stack == null) {
			return;
		}
		if (stack.getType().equals(Material.BOOK)) {
			stack.setType(Material.ENCHANTED_BOOK);
			EnchantmentStorageMeta emeta = (EnchantmentStorageMeta)stack.getItemMeta();
			emeta.addStoredEnchant(e, lvl, true);
			stack.setItemMeta(emeta);
		} else {
			stack.addEnchantment(e, lvl);
		}
	}
	
	
	/**
	 * 
	 * 
	 * This function returns the durability multiplier for an item.
	 * 
	 */
	
	public double getDuramult(Player p) {
		try {
			double dura = p.getItemInHand().getDurability();
			double maxdura = p.getItemInHand().getType().getMaxDurability();
			double duramult = (1 - dura / maxdura);
			return duramult;
		} catch (Exception e) {
			String info = "ETransaction getDuramult() passed values player='" + p.getName() + "'";
			new HyperError(e, info);
			return 0;
		}
	}
	
	
	
	
}
