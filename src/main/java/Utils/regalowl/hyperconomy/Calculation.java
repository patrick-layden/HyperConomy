package regalowl.hyperconomy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.Potion;

/**
 * 
 * 
 * This class handles various calculations, such as how much a purchase or sale
 * is worth.
 * 
 */
public class Calculation {
	private HyperConomy hc;
	private ArrayList<Integer> durableIds;

	/**
	 * 
	 * 
	 * Calculation Constructor.
	 * 
	 */
	Calculation() {
		hc = HyperConomy.hc;
		durableIds = new ArrayList<Integer>();
		durableIds.add(268);
		durableIds.add(272);
		durableIds.add(267);
		durableIds.add(283);
		durableIds.add(276);
		durableIds.add(290);
		durableIds.add(291);
		durableIds.add(292);
		durableIds.add(294);
		durableIds.add(293);
		durableIds.add(270);
		durableIds.add(274);
		durableIds.add(257);
		durableIds.add(285);
		durableIds.add(278);
		durableIds.add(269);
		durableIds.add(273);
		durableIds.add(256);
		durableIds.add(284);
		durableIds.add(277);
		durableIds.add(271);
		durableIds.add(275);
		durableIds.add(258);
		durableIds.add(286);
		durableIds.add(279);
		durableIds.add(298);
		durableIds.add(299);
		durableIds.add(300);
		durableIds.add(301);
		durableIds.add(302);
		durableIds.add(303);
		durableIds.add(304);
		durableIds.add(305);
		durableIds.add(306);
		durableIds.add(307);
		durableIds.add(308);
		durableIds.add(309);
		durableIds.add(310);
		durableIds.add(311);
		durableIds.add(312);
		durableIds.add(313);
		durableIds.add(314);
		durableIds.add(315);
		durableIds.add(316);
		durableIds.add(317);
		durableIds.add(261);
		durableIds.add(346);
		durableIds.add(359);
		durableIds.add(259);
	}

	/**
	 * 
	 * 
	 * This function determines the sale value for one or more of the item.
	 * 
	 */
	public double getValue(String name, int amount, Player p) {
		SQLFunctions sf = hc.getSQLFunctions();
		String playerecon = sf.getPlayerEconomy(p.getName());
		try {
			double totalvalue = 0;
			int itemid = 0;
			itemid = sf.getId(name, playerecon);
			double damage = 0;
			boolean isstatic = false;
			isstatic = Boolean.parseBoolean(sf.getStatic(name, playerecon));
			if (!isstatic) {
				damage = getDamage(itemid, amount, p);
				double shopstock = 0;
				double value = 0;
				double median = 0;
				double icost = 0;
				shopstock = sf.getStock(name, playerecon);
				value = sf.getValue(name, playerecon);
				median = sf.getMedian(name, playerecon);
				icost = sf.getStartPrice(name, playerecon);
				if (icost >= ((median * value) / shopstock) && shopstock > 0) {
					sf.setInitiation(name, playerecon, "false");
				}
				int counter = 0;
				while (counter < amount) {
					double price = ((median * value) / shopstock);
					price = applyCeilingFloor(name, playerecon, price);
					shopstock = shopstock + 1;
					totalvalue = totalvalue + price;
					counter++;
				}
				totalvalue = totalvalue * damage;
				Boolean initial = false;
				initial = Boolean.parseBoolean(sf.getInitiation(name, playerecon));
				if (initial == true) {
					double ivalue = applyCeilingFloor(name, playerecon, icost);
					totalvalue = ivalue * damage * amount;
				}
				if (totalvalue < Math.pow(10, 10)) {
					totalvalue = twoDecimals(totalvalue);
				} else {
					totalvalue = 3235624645000.7;
				}
			} else {
				damage = getDamage(itemid, amount, p);
				double statprice = sf.getStaticPrice(name, playerecon);
				double svalue = applyCeilingFloor(name, playerecon, statprice);
				totalvalue = svalue * amount * damage;
			}
			return totalvalue;
		} catch (Exception e) {
			e.printStackTrace();
			Logger log = Logger.getLogger("Minecraft");
			log.info("HyperConomy ERROR #16");
			Bukkit.broadcast(ChatColor.DARK_RED + "HyperConomy ERROR #16", "hyperconomy.error");
			double totalvalue = 0;
			return totalvalue;
		}
	}

	/**
	 * 
	 * 
	 * This function determines the purchase price for one or more of the item.
	 * 
	 */
	public double getCost(String name, int amount, String playerecon) {
		SQLFunctions sf = hc.getSQLFunctions();
		try {
			sf = hc.getSQLFunctions();
			double cost = 0;
			boolean isstatic = Boolean.parseBoolean(sf.getStatic(name, playerecon));
			if (isstatic == false) {
				double shopstock = 0;
				double oshopstock = 0;
				double value = 0;
				double median = 0;
				shopstock = sf.getStock(name, playerecon);
				oshopstock = shopstock;
				value = sf.getValue(name, playerecon);
				median = sf.getMedian(name, playerecon);
				shopstock = shopstock - 1;
				int counter = 0;
				while (counter < amount) {
					double price = ((median * value) / shopstock);
					price = applyCeilingFloor(name, playerecon, price);
					shopstock = shopstock - 1;
					cost = cost + price;
					counter++;
				}
				boolean initial = Boolean.parseBoolean(sf.getInitiation(name, playerecon));
				if (initial == true) {
					double icost = 0;
					icost = sf.getStartPrice(name, playerecon);
					if (cost < (icost * amount) && oshopstock > 0) {
						sf.setInitiation(name, playerecon, "false");
					} else {
						double price = applyCeilingFloor(name, playerecon, icost);
						cost = price * amount;
					}
				}
				if (cost < Math.pow(10, 10)) {
					cost = twoDecimals(cost);
				} else {
					cost = 3235624645000.7;
				}
			} else {
				double staticcost = sf.getStaticPrice(name, playerecon);
				double price = applyCeilingFloor(name, playerecon, staticcost);
				cost = price * amount;
				cost = twoDecimals(cost);
			}
			return cost;
		} catch (Exception e) {
			e.printStackTrace();
			Logger log = Logger.getLogger("Minecraft");
			log.info("HyperConomy ERROR #15");
			Bukkit.broadcast(ChatColor.DARK_RED + "HyperConomy ERROR #15", "hyperconomy.error");
			double cost = 99999999;
			return cost;
		}
	}

	/**
	 * 
	 * 
	 * This function calculates the value for the given enchantment.
	 * 
	 */
	public double getEnchantValue(String name, String mater, String playerecon) {
		SQLFunctions sf = hc.getSQLFunctions();
		Calculation calc = hc.getCalculation();
		ETransaction etran = hc.getETransaction();
		try {
			double cost = 0;
			double classvalue = etran.getclassValue(mater);
			boolean stax;
			stax = Boolean.parseBoolean(sf.getStatic(name, playerecon));
			if (!stax) {
				double shopstock;
				double value;
				double median;
				double icost;
				shopstock = sf.getStock(name, playerecon);
				value = sf.getValue(name, playerecon);
				median = sf.getMedian(name, playerecon);
				icost = sf.getStartPrice(name, playerecon);
				if (icost >= ((median * value) / shopstock) && shopstock > 0) {
					sf.setInitiation(name, playerecon, "false");
				}
				double price = (median * value) / shopstock;
				cost = cost + price;
				cost = cost * classvalue;
				cost = applyCeilingFloor(name, playerecon, cost);
				Boolean initial;
				initial = Boolean.parseBoolean(sf.getInitiation(name, playerecon));
				if (initial == true) {
					cost = icost * classvalue;
					cost = applyCeilingFloor(name, playerecon, cost);
				}
				if (cost < Math.pow(10, 10)) {
					cost = calc.twoDecimals(cost);
				} else {
					cost = 3235624645000.7;
				}
			} else {
				double statprice;
				statprice = sf.getStaticPrice(name, playerecon);
				cost = statprice * classvalue;
				cost = applyCeilingFloor(name, playerecon, cost);
			}
			return cost;
		} catch (Exception e) {
			e.printStackTrace();
			Logger l = Logger.getLogger("Minecraft");
			l.info("HyperConomy ERROR #19");
			Bukkit.broadcast(ChatColor.DARK_RED + "HyperConomy ERROR #19", "hyperconomy.error");
			double value = 0;
			return value;
		}
	}

	/**
	 * 
	 * 
	 * This function calculates the cost for the given enchantment.
	 * 
	 */
	public double getEnchantCost(String name, String mater, String playerecon) {
		SQLFunctions sf = hc.getSQLFunctions();
		Calculation calc = hc.getCalculation();
		ETransaction etran = hc.getETransaction();
		try {
			double cost = 0;
			double classvalue = etran.getclassValue(mater);
			if (classvalue != 123456789) {
				boolean stax;
				stax = Boolean.parseBoolean(sf.getStatic(name, playerecon));
				if (!stax) {
					double shopstock;
					double value;
					double median;
					shopstock = sf.getStock(name, playerecon);
					value = sf.getValue(name, playerecon);
					median = sf.getMedian(name, playerecon);
					double oshopstock = shopstock;
					shopstock = shopstock - 1;
					double price = ((median * value) / shopstock);
					cost = price * classvalue;
					cost = applyCeilingFloor(name, playerecon, cost);
					boolean initial;
					initial = Boolean.parseBoolean(sf.getInitiation(name, playerecon));
					if (initial == true) {
						double icost;
						icost = sf.getStartPrice(name, playerecon);
						if (price < icost && oshopstock > 0) {
							sf.setInitiation(name, playerecon, "false");
						} else {
							cost = icost * classvalue;
							cost = applyCeilingFloor(name, playerecon, cost);
						}
					}
					if (cost < Math.pow(10, 10)) {
						cost = calc.twoDecimals(cost);
					} else {
						cost = 3235624645000.7;
					}
				} else {
					double staticcost;
					staticcost = sf.getStaticPrice(name, playerecon);
					cost = staticcost * classvalue;
					cost = applyCeilingFloor(name, playerecon, cost);
				}
			} else {
				cost = 123456789;
			}
			return cost;
		} catch (Exception e) {
			e.printStackTrace();
			Logger l = Logger.getLogger("Minecraft");
			l.info("HyperConomy ERROR #20");
			Bukkit.broadcast(ChatColor.DARK_RED + "HyperConomy ERROR #20", "hyperconomy.error");
			double cost = 99999999;
			return cost;
		}
	}

	/**
	 * 
	 * 
	 * This function calculates the total damage multiplier for a cost/value
	 * calculation.
	 * 
	 */
	private double getDamage(int itemid, int amount, Player p) {
		ETransaction ench = hc.getETransaction();
		try {
			double damage = 0;
			if (testId(itemid)) {
				Inventory pinv = p.getInventory();
				HashMap<Integer, ? extends ItemStack> stacks = pinv.all(itemid);
				int heldslot = p.getInventory().getHeldItemSlot();
				boolean hasenchants2 = ench.hasenchants(stacks.get(heldslot));
				int totalitems = 0;
				if (p.getItemInHand().getTypeId() == itemid && hasenchants2 == false) {
					damage = getdurabilityPercent(stacks.get(heldslot));
					totalitems++;
				}
				int slot = 0;
				while (slot < 36) {
					boolean hasenchants = ench.hasenchants(stacks.get(slot));
					if (stacks.get(slot) != null && totalitems < amount && slot != heldslot && hasenchants == false) {
						damage = getdurabilityPercent(stacks.get(slot)) + damage;
						totalitems++;
					}
					slot++;
				}
				damage = damage / amount;
			} else {
				damage = 1;
			}
			return damage;
		} catch (Exception e) {
			e.printStackTrace();
			Logger log = Logger.getLogger("Minecraft");
			log.info("HyperConomy ERROR #14");
			Bukkit.broadcast(ChatColor.DARK_RED + "HyperConomy ERROR #14", "hyperconomy.error");
			double damage = 0;
			return damage;
		}
	}

	/**
	 * 
	 * 
	 * This function returns the percentage of remaining durability in an
	 * itemstack. (0 = 0% durability, 1 = 100%) It returns as 1 if the item
	 * cannot be damaged.
	 * 
	 */
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
			e.printStackTrace();
			Logger log = Logger.getLogger("Minecraft");
			log.info("HyperConomy ERROR #13");
			Bukkit.broadcast(ChatColor.DARK_RED + "HyperConomy ERROR #13", "hyperconomy.error");
			double durabilitypercent = 1;
			return durabilitypercent;
		}
	}

	/**
	 * 
	 * 
	 * This function uses the testId function to determine if the item can be
	 * damaged. If it can be damaged it sets the damage value to 0 (to represent
	 * an undamaged item). If the item cannot be damaged it returns the original
	 * damage value.
	 */
	public int newData(int id, int data) {
		try {
			int newData;
			if (testId(id)) {
				newData = 0;
			} else {
				newData = data;
			}
			return newData;
		} catch (Exception e) {
			e.printStackTrace();
			Logger log = Logger.getLogger("Minecraft");
			log.info("HyperConomy ERROR #12");
			Bukkit.broadcast(ChatColor.DARK_RED + "HyperConomy ERROR #12", "hyperconomy.error");
			int newData = data;
			return newData;
		}
	}

	/**
	 * 
	 * 
	 * This function compares the given id with its list to determine if the
	 * item can be damaged. If it can be damaged it returns true, false if not.
	 * 
	 */
	public boolean testId(int id) {
		try {
			boolean datatest = false;
			if (durableIds.contains(id)) {
				datatest = true;
			}
			return datatest;
		} catch (Exception e) {
			e.printStackTrace();
			Logger log = Logger.getLogger("Minecraft");
			log.info("HyperConomy ERROR #11");
			Bukkit.broadcast(ChatColor.DARK_RED + "HyperConomy ERROR #11", "hyperconomy.error");
			boolean datatest = false;
			return datatest;
		}
	}

	/**
	 * 
	 * 
	 * This function calculates the theoretical value for items, ignoring
	 * durability.
	 * 
	 */
	public double getTvalue(String name, int amount, String playerecon) {
		SQLFunctions sf = hc.getSQLFunctions();
		try {
			double cost = 0;
			int counter = 0;
			Boolean initial = false;
			initial = Boolean.parseBoolean(sf.getInitiation(name, playerecon));
			boolean isstatic = false;
			isstatic = Boolean.parseBoolean(sf.getStatic(name, playerecon));
			if (!isstatic) {
				double shopstock = 0;
				double value = 0;
				double median = 0;
				double icost = 0;
				shopstock = sf.getStock(name, playerecon);
				value = sf.getValue(name, playerecon);
				median = sf.getMedian(name, playerecon);
				icost = sf.getStartPrice(name, playerecon);
				while (counter < amount) {
					double price = ((median * value) / shopstock);
					price = applyCeilingFloor(name, playerecon, price);
					shopstock = shopstock + 1;
					cost = cost + price;
					counter++;
				}
				if (initial == true) {
					double price = applyCeilingFloor(name, playerecon, icost);
					cost = price * amount;
				}
				if (cost < Math.pow(10, 10)) {
					cost = twoDecimals(cost);
				} else {
					cost = 3235624645000.7;
				}
			} else {
				double statprice = sf.getStaticPrice(name, playerecon);
				double price = applyCeilingFloor(name, playerecon, statprice);
				cost = price * amount;
			}
			return cost;
		} catch (Exception e) {
			e.printStackTrace();
			Logger log = Logger.getLogger("Minecraft");
			log.info("HyperConomy ERROR #10");
			Bukkit.broadcast(ChatColor.DARK_RED + "HyperConomy ERROR #10", "hyperconomy.error");
			double cost = 99999999;
			return cost;
		}
	}

	/**
	 * 
	 * 
	 * This function returns the correct damage value for potions.
	 * 
	 */
	public int getpotionDV(ItemStack item) {
		try {
			int da;
			if (item != null) {
				if (item.getTypeId() == 373) {
					try {
						Potion p = Potion.fromItemStack(item);
						da = p.toDamageValue();
					} catch (Exception IllegalArgumentException) {
						da = item.getData().getData();
					}
				} else {
					da = item.getData().getData();
				}
			} else {
				da = 0;
			}
			return da;
		} catch (Exception e) {
			e.printStackTrace();
			Logger log = Logger.getLogger("Minecraft");
			log.info("HyperConomy ERROR #9");
			Bukkit.broadcast(ChatColor.DARK_RED + "HyperConomy ERROR #9", "hyperconomy.error");
			int da = 0;
			return da;
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

	public double round(double input, int decimals) {
		double factor = Math.pow(10, decimals);
		int changedecimals = (int) Math.ceil((input * factor) - .5);
		return (double) changedecimals / factor;
	}

	public double twoDecimals(double input) {
		int nodecimals = (int) Math.ceil((input * 100) - .5);
		double twodecimals = (double) nodecimals / 100.0;
		return twodecimals;
	}

	public int getdamageValue(ItemStack item) {
		int itd = item.getTypeId();
		int da = getpotionDV(item);
		int newdat = newData(itd, da);
		return newdat;
	}

	public double getSalesTax(Player p, Double fprice) {
		Account acc = hc.getAccount();
		double salestax = 0;
		if (hc.getYaml().getConfig().getBoolean("config.dynamic-tax.use-dynamic-tax")) {
			if (p == null) {
				return 0.0;
			}
			double moneyfloor = hc.getYaml().getConfig().getDouble("config.dynamic-tax.money-floor");
			double moneycap = hc.getYaml().getConfig().getDouble("config.dynamic-tax.money-cap");
			double cbal = acc.getBalance(p.getName());
			double maxtaxrate = hc.getYaml().getConfig().getDouble("config.dynamic-tax.max-tax-percent") / 100.0;
			if (cbal >= moneycap) {
				salestax = fprice * maxtaxrate;
			} else if (cbal <= moneyfloor) {
				salestax = 0;
			} else {
				double taxrate = ((cbal - moneyfloor) / (moneycap - moneyfloor));
				if (taxrate > maxtaxrate) {
					taxrate = maxtaxrate;
				}
				salestax = fprice * taxrate;
			}
		} else {
			double salestaxpercent = hc.getYaml().getConfig().getDouble("config.sales-tax-percent");
			salestax = (salestaxpercent / 100) * fprice;
		}
		return salestax;
	}

	public double getPurchaseTax(String name, String economy, double cost) {
		SQLFunctions sf = hc.getSQLFunctions();
		boolean isinitial = Boolean.parseBoolean(sf.getInitiation(name, economy));
		boolean isstatic = Boolean.parseBoolean(sf.getStatic(name, economy));
		double tax = 0.0;
		if (isstatic) {
			tax = hc.getYaml().getConfig().getDouble("config.statictaxpercent") / 100.0;
		} else if (isinitial) {
			tax = hc.getYaml().getConfig().getDouble("config.initialpurchasetaxpercent") / 100.0;
		} else {
			tax = hc.getYaml().getConfig().getDouble("config.purchasetaxpercent") / 100.0;
		}
		return twoDecimals(cost * tax);
	}

	public double getEnchantTax(String name, String economy, double cost) {
		SQLFunctions sf = hc.getSQLFunctions();
		boolean isstatic = Boolean.parseBoolean(sf.getStatic(name, economy));
		double tax = 0.0;
		if (isstatic) {
			tax = hc.getYaml().getConfig().getDouble("config.statictaxpercent") / 100.0;
		} else {
			tax = hc.getYaml().getConfig().getDouble("config.enchanttaxpercent") / 100.0;
		}
		return twoDecimals(cost * tax);
	}

	public double getCeiling(String name, String economy) {
		SQLFunctions sf = hc.getSQLFunctions();
		double ceiling = sf.getCeiling(name, economy);
		double floor = sf.getFloor(name, economy);
		if (ceiling <= 0 || floor > ceiling) {
			ceiling = 9999999999999.99;
		}
		return ceiling;
	}

	public double getFloor(String name, String economy) {
		SQLFunctions sf = hc.getSQLFunctions();
		double floor = sf.getFloor(name, economy);
		double ceiling = sf.getCeiling(name, economy);
		if (floor < 0 || ceiling < floor) {
			floor = 0.0;
		}
		return floor;
	}

	public double applyCeilingFloor(String name, String playerecon, double price) {
		double floor = getFloor(name, playerecon);
		double ceiling = getCeiling(name, playerecon);
		if (price > ceiling) {
			price = ceiling;
		} else if (price < floor) {
			price = floor;
		}
		return price;
	}
}
