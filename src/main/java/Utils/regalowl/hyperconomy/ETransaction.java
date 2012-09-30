package regalowl.hyperconomy;

import java.util.ArrayList;
import java.util.Map;
import java.util.logging.Logger;

import net.milkbowl.vault.economy.Economy;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * 
 * 
 * This class handles the purchase and sale of enchantments.
 * 
 */
public class ETransaction {
	private HyperConomy hc;
	private ArrayList<Enchantment> enchantments = new ArrayList<Enchantment>();

	ETransaction() {
		hc = HyperConomy.hc;
		enchantments.add(Enchantment.ARROW_DAMAGE);
		enchantments.add(Enchantment.ARROW_FIRE);
		enchantments.add(Enchantment.ARROW_INFINITE);
		enchantments.add(Enchantment.ARROW_KNOCKBACK);
		enchantments.add(Enchantment.DAMAGE_ALL);
		enchantments.add(Enchantment.DAMAGE_ARTHROPODS);
		enchantments.add(Enchantment.DAMAGE_UNDEAD);
		enchantments.add(Enchantment.DIG_SPEED);
		enchantments.add(Enchantment.DURABILITY);
		enchantments.add(Enchantment.FIRE_ASPECT);
		enchantments.add(Enchantment.KNOCKBACK);
		enchantments.add(Enchantment.LOOT_BONUS_BLOCKS);
		enchantments.add(Enchantment.LOOT_BONUS_MOBS);
		enchantments.add(Enchantment.OXYGEN);
		enchantments.add(Enchantment.PROTECTION_ENVIRONMENTAL);
		enchantments.add(Enchantment.PROTECTION_EXPLOSIONS);
		enchantments.add(Enchantment.PROTECTION_FALL);
		enchantments.add(Enchantment.PROTECTION_FIRE);
		enchantments.add(Enchantment.PROTECTION_PROJECTILE);
		enchantments.add(Enchantment.SILK_TOUCH);
		enchantments.add(Enchantment.WATER_WORKER);
	}

	/**
	 * 
	 * 
	 * This function handles the sale of enchantments.
	 * 
	 */
	public void sellEnchant(String name, Player p) {
		SQLFunctions sf = hc.getSQLFunctions();
		Calculation calc = hc.getCalculation();
		Account acc = hc.getAccount();
		Economy economy = hc.getEconomy();
		Log log = hc.getLog();
		Notify not = hc.getNotify();
		InfoSign isign = hc.getInfoSign();
		// Handles sellEnchant errors.
		try {
			String nenchant = "";
			String playerecon = sf.getPlayerEconomy(p.getName());
			nenchant = sf.getMaterial(name, playerecon);
			Enchantment ench = Enchantment.getByName(nenchant);
			// Makes sure the item being held has the correct enchantment and
			// enchantment level.
			int lvl = Integer.parseInt(name.substring(name.length() - 1, name.length()));
			int truelvl = p.getItemInHand().getEnchantmentLevel(ench);
			if (p.getItemInHand().containsEnchantment(ench) && lvl == truelvl) {
				// Gets the actual value of the enchantment and stores it in
				// fprice, factoring in durability, and then
				// adds the value to the player's balance.
				double dura = p.getItemInHand().getDurability();
				double maxdura = p.getItemInHand().getType().getMaxDurability();
				double duramult = (1 - dura / maxdura);
				String mater = p.getItemInHand().getType().toString();
				double price = calc.getEnchantValue(name, mater, playerecon);
				double fprice = duramult * price;
				boolean sunlimited = hc.getYaml().getConfig().getBoolean("config.shop-has-unlimited-money");
				if (acc.checkshopBalance(fprice) || sunlimited) {
					// Removes the sold enchantment from the item.
					p.getItemInHand().removeEnchantment(ench);
					// Adds the sold items to the shopstock and saves the yaml
					// file.
					int shopstock = 0;
					shopstock = (int) sf.getStock(name, playerecon);
					sf.setStock(name, playerecon, shopstock + 1);
					double salestax = calc.getSalesTax(p, fprice);
					acc.setAccount(hc, p, economy);
					acc.deposit(fprice - salestax);
					// Removes the final transaction price from the shop's
					// account.
					acc.withdrawShop(fprice - salestax);
					// Reverts any changes to the global shop account if the
					// account is set to unlimited.
					if (sunlimited) {
						String globalaccount = hc.getYaml().getConfig().getString("config.global-shop-account");
						acc.setBalance(globalaccount, 0);
					}
					// Formats the sale value to two digits for display.
					fprice = calc.twoDecimals(fprice);
					// Informs the player of their sale.
					p.sendMessage(ChatColor.BLACK + "-----------------------------------------------------");
					p.sendMessage(ChatColor.BLUE + "" + ChatColor.ITALIC + "You sold" + ChatColor.AQUA + "" + ChatColor.ITALIC + " " + name + ChatColor.BLUE + "" + ChatColor.ITALIC + " for " + ChatColor.GREEN + "" + ChatColor.ITALIC + hc.getYaml().getConfig().getString("config.currency-symbol") + fprice + ChatColor.BLUE + "" + ChatColor.ITALIC + " of which " + ChatColor.GREEN + "" + ChatColor.ITALIC + hc.getYaml().getConfig().getString("config.currency-symbol") + calc.twoDecimals(salestax)
							+ ChatColor.BLUE + ChatColor.ITALIC + " went to tax!");
					p.sendMessage(ChatColor.BLACK + "-----------------------------------------------------");
					// Writes the transaction to the log.
					if (hc.useSQL()) {
						String type = "dynamic";
						if (Boolean.parseBoolean(sf.getInitiation(name, playerecon))) {
							type = "initial";
						} else if (Boolean.parseBoolean(sf.getStatic(name, playerecon))) {
							type = "static";
						}
						log.writeSQLLog(p.getName(), "sale", name, 1.0, fprice - salestax, salestax, playerecon, type);
					} else {
						String logentry = p.getName() + " sold " + name + " for " + hc.getYaml().getConfig().getString("config.currency-symbol") + fprice + ". [Static Price=" + sf.getStatic(name, playerecon) + "][Initial Price=" + sf.getInitiation(name, playerecon) + "]";
						log.setEntry(logentry);
						log.writeBuffer();
					}
					// Updates all information signs.
					isign.setrequestsignUpdate(true);
					isign.checksignUpdate();
					// Sends price update notifications.
					not.setNotify(hc, calc, this, name, mater, playerecon);
					not.sendNotification();
				} else {
					p.sendMessage(ChatColor.BLUE + "Sorry, the shop currently does not have enough money.");
				}
				// If the item does not have the enchantment that the player is
				// trying to sell, this informs them.
			} else {
				p.sendMessage(ChatColor.BLUE + "The item you're holding doesn't have " + ChatColor.AQUA + "" + name + "!");
			}
		} catch (Exception e) {
			e.printStackTrace();
			Logger l = Logger.getLogger("Minecraft");
			l.info("HyperConomy ERROR #17");
			Bukkit.broadcast(ChatColor.DARK_RED + "HyperConomy ERROR #17", "hyperconomy.error");
		}
	}

	/**
	 * 
	 * 
	 * This function handles the purchase of enchantments.
	 * 
	 */
	public void buyEnchant(String name, Player p) {
		SQLFunctions sf = hc.getSQLFunctions();
		Calculation calc = hc.getCalculation();
		Account acc = hc.getAccount();
		Economy economy = hc.getEconomy();
		Log log = hc.getLog();
		Notify not = hc.getNotify();
		InfoSign isign = hc.getInfoSign();
		// Handles buyEnchant errors.
		try {
			String playerecon = sf.getPlayerEconomy(p.getName());
			String nenchant = sf.getMaterial(name, playerecon);
			Enchantment ench = Enchantment.getByName(nenchant);
			// Makes sure the shop has the given enchantment.
			int shopstock = 0;
			shopstock = (int) sf.getStock(name, playerecon);
			if (shopstock >= 1) {
				String mater = p.getItemInHand().getType().toString();
				double price = calc.getEnchantCost(name, mater, playerecon);
				price = price + calc.getEnchantTax(name, playerecon, price);
				if (price != 123456789) {
					if (!p.getItemInHand().containsEnchantment(ench)) {
						acc.setAccount(hc, p, economy);
						if (acc.checkFunds(price)) {
							boolean enchtest = ench.canEnchantItem(p.getItemInHand());
							if (hasenchants(p.getItemInHand())) {
								String allenchants = p.getItemInHand().getEnchantments().toString();
								allenchants = allenchants.substring(0, allenchants.length() - 1) + ", E";
								while (allenchants.length() > 1) {
									String enchantname = allenchants.substring(allenchants.indexOf(",") + 2, allenchants.indexOf("]"));
									allenchants = allenchants.substring(allenchants.indexOf("]") + 5, allenchants.length());
									Enchantment enchant = Enchantment.getByName(enchantname);
									if (ench.conflictsWith(enchant)) {
										enchtest = false;
									}
								}
							}
							if (enchtest && p.getItemInHand().getAmount() == 1) {
								sf.setStock(name, playerecon, shopstock - 1);
								acc.withdraw(price);
								acc.depositShop(price);
								if (hc.getYaml().getConfig().getBoolean("config.shop-has-unlimited-money")) {
									String globalaccount = hc.getYaml().getConfig().getString("config.global-shop-account");
									acc.setBalance(globalaccount, 0);
								}
								int l = name.length();
								String lev = name.substring(l - 1, l);
								int level = Integer.parseInt(lev);
								p.getItemInHand().addEnchantment(ench, level);
								boolean stax;
								stax = Boolean.parseBoolean(sf.getStatic(name, playerecon));
								double taxrate;
								if (!stax) {
									taxrate = hc.getYaml().getConfig().getDouble("config.enchanttaxpercent");
								} else {
									taxrate = hc.getYaml().getConfig().getDouble("config.statictaxpercent");
								}
								double taxpaid = price - (price / (1 + taxrate / 100));
								taxpaid = calc.twoDecimals(taxpaid);
								// Formats the price to two decimals for
								// display.
								price = calc.twoDecimals(price);
								// Displays purchase information to the player.
								p.sendMessage(ChatColor.BLACK + "-----------------------------------------------------");
								p.sendMessage(ChatColor.BLUE + "" + ChatColor.ITALIC + "You bought" + ChatColor.AQUA + "" + ChatColor.ITALIC + " " + name + ChatColor.BLUE + "" + ChatColor.ITALIC + " for " + ChatColor.GREEN + "" + ChatColor.ITALIC + hc.getYaml().getConfig().getString("config.currency-symbol") + price + ChatColor.BLUE + "" + ChatColor.ITALIC + " of which " + ChatColor.GREEN + "" + ChatColor.ITALIC + hc.getYaml().getConfig().getString("config.currency-symbol") + taxpaid
										+ " was tax!");
								p.sendMessage(ChatColor.BLACK + "-----------------------------------------------------");
								// Logs the transaction.
								String logentry = "";
								if (hc.useSQL()) {
									String type = "dynamic";
									if (Boolean.parseBoolean(sf.getInitiation(name, playerecon))) {
										type = "initial";
									} else if (Boolean.parseBoolean(sf.getStatic(name, playerecon))) {
										type = "static";
									}
									log.writeSQLLog(p.getName(), "purchase", name, 1.0, price, taxpaid, playerecon, type);
								} else {
									logentry = p.getName() + " bought " + name + " for " + hc.getYaml().getConfig().getString("config.currency-symbol") + price + ". [Static Price=" + sf.getStatic(name, playerecon) + "][Initial Price=" + sf.getInitiation(name, playerecon) + "]";
									log.setEntry(logentry);
									log.writeBuffer();
								}
								// Updates all information signs.
								isign.setrequestsignUpdate(true);
								isign.checksignUpdate();
								// Sends price update notifications.
								not.setNotify(hc, calc, this, name, mater, playerecon);
								not.sendNotification();
							} else {
								p.sendMessage(ChatColor.BLUE + "The item you're holding cannot accept that enchantment!");
							}
						} else {
							p.sendMessage(ChatColor.BLUE + "Insufficient Funds!");
						}
					} else {
						p.sendMessage(ChatColor.BLUE + "The item you're holding already has an enchantment of that type!");
					}
				} else {
					p.sendMessage(ChatColor.BLUE + "The item you're holding cannot accept that enchantment!");
				}
			} else {
				p.sendMessage(ChatColor.BLUE + "The shop doesn't have enough " + name + "!");
			}
		} catch (Exception e) {
			e.printStackTrace();
			Logger l = Logger.getLogger("Minecraft");
			l.info("HyperConomy ERROR #18");
			Bukkit.broadcast(ChatColor.DARK_RED + "HyperConomy ERROR #18", "hyperconomy.error");
		}
	}

	/**
	 * 
	 * 
	 * This function handles the purchase of chestshop enchantments.
	 * 
	 */
	public boolean buyChestEnchant(String name, Player p, ItemStack item, String owner) {
		SQLFunctions sf = hc.getSQLFunctions();
		Calculation calc = hc.getCalculation();
		Account acc = hc.getAccount();
		Economy economy = hc.getEconomy();
		Log log = hc.getLog();
		// Handles buyEnchant errors.
		try {
			// Gets the enchantment from the enchants.yml file and creates a new
			// enchantment from the stored name.
			String nenchant = "";
			String playerecon = sf.getPlayerEconomy(owner);
			nenchant = sf.getMaterial(name, playerecon);
			Enchantment ench = Enchantment.getByName(nenchant);
			// Gets the material of the item the player is holding.
			String mater = p.getItemInHand().getType().toString();
			// Calculates the cost to buy the given enchantment for the relevant
			// material class
			double price = calc.getEnchantValue(name, mater, playerecon);
			// Checks for infinite values. (The cost returns as this number if
			// such a value exists.)
			if (price != 123456789) {
				// Makes sure the item the player is holding doesn't have the
				// enchantment they're trying to buy.
				if (!p.getItemInHand().containsEnchantment(ench)) {
					// Makes sure the item can accept the chosen enchantment.
					// (Need to add new bukkit method for this when 1.2 RB comes
					// out.)
					boolean enchtest = ench.canEnchantItem(p.getItemInHand());
					// add later
					if (hasenchants(p.getItemInHand())) {
						String allenchants = p.getItemInHand().getEnchantments().toString();
						allenchants = allenchants.substring(0, allenchants.length() - 1) + ", E";
						while (allenchants.length() > 1) {
							String enchantname = allenchants.substring(allenchants.indexOf(",") + 2, allenchants.indexOf("]"));
							allenchants = allenchants.substring(allenchants.indexOf("]") + 5, allenchants.length());
							Enchantment enchant = Enchantment.getByName(enchantname);
							if (ench.conflictsWith(enchant)) {
								enchtest = false;
							}
						}
					}
					if (enchtest && p.getItemInHand().getAmount() == 1) {
						acc.setAccount(hc, p, economy);
						if (acc.checkFunds(price)) {
							acc.withdraw(price);
							acc.setAccount(hc, Bukkit.getPlayer(owner), economy);
							acc.depositAccount(owner, price);
							int l = name.length();
							String lev = name.substring(l - 1, l);
							int level = Integer.parseInt(lev);
							p.getItemInHand().addEnchantment(ench, level);
							item.removeEnchantment(ench);
							price = calc.twoDecimals(price);
							p.sendMessage(ChatColor.BLACK + "-----------------------------------------------------");
							p.sendMessage(ChatColor.BLUE + "" + ChatColor.ITALIC + "You bought" + ChatColor.AQUA + "" + ChatColor.ITALIC + " " + name + ChatColor.BLUE + "" + ChatColor.ITALIC + " for " + ChatColor.GREEN + "" + ChatColor.ITALIC + hc.getYaml().getConfig().getString("config.currency-symbol") + price + ChatColor.BLUE + "" + ChatColor.ITALIC + " from " + owner);
							p.sendMessage(ChatColor.BLACK + "-----------------------------------------------------");
							String logentry = "";
							if (hc.useSQL()) {
								log.writeSQLLog(p.getName(), "purchase", name, 1.0, price, 0.0, owner, "chestshop");
							} else {
								logentry = p.getName() + " bought " + name + " for " + hc.getYaml().getConfig().getString("config.currency-symbol") + price + " from " + owner + ". [Static Price=" + sf.getStatic(name, playerecon) + "][Initial Price=" + sf.getInitiation(name, playerecon) + "]";
								log.setEntry(logentry);
								log.writeBuffer();
							}
							Player o = Bukkit.getPlayer(owner);
							if (o != null) {
								o.sendMessage("\u00A79" + p.getName() + " bought" + " \u00A7b" + name + " \u00A79from you for \u00A7a" + hc.getYaml().getConfig().getString("config.currency-symbol") + price + "\u00A79.");
							}
							return true;
						} else {
							p.sendMessage(ChatColor.BLUE + "Insufficient Funds!");
						}
					} else {
						p.sendMessage(ChatColor.BLUE + "The item you're holding cannot accept that enchantment!");
					}
				} else {
					p.sendMessage(ChatColor.BLUE + "The item you're holding already has an enchantment of that type!");
				}
			} else {
				p.sendMessage(ChatColor.BLUE + "The item you're holding cannot accept that enchantment!");
			}
			return false;
		} catch (Exception e) {
			e.printStackTrace();
			Logger l = Logger.getLogger("Minecraft");
			l.info("HyperConomy ERROR #18");
			Bukkit.broadcast(ChatColor.DARK_RED + "HyperConomy ERROR #18", "hyperconomy.error");
			return false;
		}
	}

	/**
	 * 
	 * 
	 * This function handles the purchase of chestshop enchantments with a set
	 * price.
	 * 
	 */
	public boolean buyChestEnchant(String name, Player p, ItemStack item, String owner, double price) {
		SQLFunctions sf = hc.getSQLFunctions();
		Calculation calc = hc.getCalculation();
		Account acc = hc.getAccount();
		Economy economy = hc.getEconomy();
		Log log = hc.getLog();
		// Handles buyEnchant errors.
		try {
			// Gets the enchantment from the enchants.yml file and creates a new
			// enchantment from the stored name.
			String nenchant = "";
			String playerecon = sf.getPlayerEconomy(owner);
			nenchant = sf.getMaterial(name, playerecon);
			Enchantment ench = Enchantment.getByName(nenchant);
			// Makes sure the item the player is holding doesn't have the
			// enchantment they're trying to buy.
			if (!p.getItemInHand().containsEnchantment(ench)) {
				boolean enchtest = ench.canEnchantItem(p.getItemInHand());
				if (hasenchants(p.getItemInHand())) {
					String allenchants = p.getItemInHand().getEnchantments().toString();
					allenchants = allenchants.substring(0, allenchants.length() - 1) + ", E";
					while (allenchants.length() > 1) {
						String enchantname = allenchants.substring(allenchants.indexOf(",") + 2, allenchants.indexOf("]"));
						allenchants = allenchants.substring(allenchants.indexOf("]") + 5, allenchants.length());
						Enchantment enchant = Enchantment.getByName(enchantname);
						if (ench.conflictsWith(enchant)) {
							enchtest = false;
						}
					}
				}
				if (enchtest && p.getItemInHand().getAmount() == 1) {
					// Makes sure the player has enough money for the purchase.
					acc.setAccount(hc, p, economy);
					if (acc.checkFunds(price)) {
						// Removes the cost from the player's account.
						acc.withdraw(price);
						// Deposits the money spent by the player into the chest
						// owner's account.
						acc.setAccount(hc, Bukkit.getPlayer(owner), economy);
						acc.depositAccount(owner, price);
						// Gets the enchantment level from the enchantment's
						// name.
						int l = name.length();
						String lev = name.substring(l - 1, l);
						int level = Integer.parseInt(lev);
						// Adds the enchantment to the item the player is
						// holding.
						p.getItemInHand().addEnchantment(ench, level);
						item.removeEnchantment(ench);
						// Formats the price to two decimals for display.
						price = calc.twoDecimals(price);
						// Displays purchase information to the player.
						p.sendMessage(ChatColor.BLACK + "-----------------------------------------------------");
						p.sendMessage(ChatColor.BLUE + "" + ChatColor.ITALIC + "You bought" + ChatColor.AQUA + "" + ChatColor.ITALIC + " " + name + ChatColor.BLUE + "" + ChatColor.ITALIC + " for " + ChatColor.GREEN + "" + ChatColor.ITALIC + hc.getYaml().getConfig().getString("config.currency-symbol") + price + ChatColor.BLUE + "" + ChatColor.ITALIC + " from " + owner);
						p.sendMessage(ChatColor.BLACK + "-----------------------------------------------------");
						String logentry = "";
						if (hc.useSQL()) {
							log.writeSQLLog(p.getName(), "purchase", name, 1.0, price, 0.0, owner, "chestshop");
						} else {
							logentry = p.getName() + " bought " + name + " for " + hc.getYaml().getConfig().getString("config.currency-symbol") + price + " from " + owner + ". [Static Price=" + sf.getStatic(name, playerecon) + "][Initial Price=" + sf.getInitiation(name, playerecon) + "]";
							log.setEntry(logentry);
							log.writeBuffer();
						}
						Player o = Bukkit.getPlayer(owner);
						if (o != null) {
							o.sendMessage("\u00A79" + p.getName() + " bought" + " \u00A7b" + name + " \u00A79from you for \u00A7a" + hc.getYaml().getConfig().getString("config.currency-symbol") + price + "\u00A79.");
						}
						return true;
					} else {
						p.sendMessage(ChatColor.BLUE + "Insufficient Funds!");
					}
				} else {
					p.sendMessage(ChatColor.BLUE + "The item you're holding cannot accept that enchantment!");
				}
			} else {
				p.sendMessage(ChatColor.BLUE + "The item you're holding already has an enchantment of that type!");
			}
			return false;
		} catch (Exception e) {
			e.printStackTrace();
			Logger l = Logger.getLogger("Minecraft");
			l.info("HyperConomy ERROR #18");
			Bukkit.broadcast(ChatColor.DARK_RED + "HyperConomy ERROR #18", "hyperconomy.error");
			return false;
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
			e.printStackTrace();
			Logger log = Logger.getLogger("Minecraft");
			log.info("HyperConomy ERROR #21");
			Bukkit.broadcast(ChatColor.DARK_RED + "HyperConomy ERROR #21", "hyperconomy.error");
			double duramult = 0;
			return duramult;
		}
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
			// If the ItemStack's enchantment list is not empty, the function
			// returns true. It makes sure that the ItemStack is not null.
			if (stack != null) {
				Map<Enchantment, Integer> enchants = stack.getEnchantments();
				hasenchants = !enchants.isEmpty();
			}
			return hasenchants;
		} catch (Exception e) {
			e.printStackTrace();
			Logger log = Logger.getLogger("Minecraft");
			log.info("HyperConomy ERROR #22");
			Bukkit.broadcast(ChatColor.DARK_RED + "HyperConomy ERROR #22", "hyperconomy.error");
			boolean hasenchants = false;
			return hasenchants;
		}
	}

	/**
	 * 
	 * 
	 * This function returns the class value (diamond, stone, etc.) of the given
	 * material.
	 * 
	 */
	public double getclassValue(String matname) {
		try {
			double value;
			if (matname.toLowerCase().indexOf("leather") != -1) {
				value = (hc.getYaml().getConfig().getDouble("config.enchantment.classvalue.leather"));
			} else if (matname.toLowerCase().indexOf("wood") != -1) {
				value = (hc.getYaml().getConfig().getDouble("config.enchantment.classvalue.wood"));
			} else if (matname.toLowerCase().indexOf("stone") != -1) {
				value = (hc.getYaml().getConfig().getDouble("config.enchantment.classvalue.stone"));
			} else if (matname.toLowerCase().indexOf("chainmail") != -1) {
				value = (hc.getYaml().getConfig().getDouble("config.enchantment.classvalue.chainmail"));
			} else if (matname.toLowerCase().indexOf("iron") != -1) {
				value = (hc.getYaml().getConfig().getDouble("config.enchantment.classvalue.iron"));
			} else if (matname.toLowerCase().indexOf("gold") != -1) {
				value = (hc.getYaml().getConfig().getDouble("config.enchantment.classvalue.gold"));
			} else if (matname.toLowerCase().indexOf("diamond") != -1) {
				value = (hc.getYaml().getConfig().getDouble("config.enchantment.classvalue.diamond"));
			} else if (matname.toLowerCase().indexOf("bow") != -1) {
				value = (hc.getYaml().getConfig().getDouble("config.enchantment.classvalue.bow"));
			} else {
				value = 123456789;
			}
			return value;
		} catch (Exception e) {
			e.printStackTrace();
			Logger log = Logger.getLogger("Minecraft");
			log.info("HyperConomy ERROR #23");
			Bukkit.broadcast(ChatColor.DARK_RED + "HyperConomy ERROR #23", "hyperconomy.error");
			double value = 987654321;
			return value;
		}
	}

	public boolean isEnchantable(ItemStack item) {
		boolean enchantable = false;
		int count = 0;
		while (count < enchantments.size()) {
			Enchantment enchant = enchantments.get(count);
			if (enchant.canEnchantItem(item)) {
				enchantable = true;
			}
			count++;
		}
		return enchantable;
	}
}
