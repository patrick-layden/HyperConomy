package regalowl.hyperconomy;

import java.util.ArrayList;
import java.util.Map;
import org.bukkit.Bukkit;
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
	private LanguageFile L;
	
	ETransaction() {
		hc = HyperConomy.hc;
		L = hc.getLanguageFile();
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
		DataFunctions sf = hc.getSQLFunctions();
		Calculation calc = hc.getCalculation();
		Account acc = hc.getAccount();
		Log log = hc.getLog();
		Notification not = hc.getNotify();
		InfoSign isign = hc.getInfoSign();
		try {
			String nenchant = "";
			String playerecon = sf.getPlayerEconomy(p.getName());
			nenchant = sf.getMaterial(name, playerecon);
			Enchantment ench = Enchantment.getByName(nenchant);
			int lvl = Integer.parseInt(name.substring(name.length() - 1, name.length()));
			int truelvl = p.getItemInHand().getEnchantmentLevel(ench);
			if (p.getItemInHand().containsEnchantment(ench) && lvl == truelvl) {
				double dura = p.getItemInHand().getDurability();
				double maxdura = p.getItemInHand().getType().getMaxDurability();
				double duramult = (1 - dura / maxdura);
				String mater = p.getItemInHand().getType().toString();
				double price = calc.getEnchantValue(name, mater, playerecon);
				double fprice = duramult * price;
				boolean sunlimited = hc.getYaml().getConfig().getBoolean("config.shop-has-unlimited-money");
				if (acc.checkshopBalance(fprice) || sunlimited) {
					p.getItemInHand().removeEnchantment(ench);
					double shopstock = sf.getStock(name, playerecon);
					sf.setStock(name, playerecon, shopstock + duramult);
					double salestax = calc.getSalesTax(p, fprice);
					acc.deposit(fprice - salestax, p);
					acc.withdrawShop(fprice - salestax);
					if (sunlimited) {
						String globalaccount = hc.getYaml().getConfig().getString("config.global-shop-account");
						acc.setBalance(globalaccount, 0);
					}
					fprice = calc.twoDecimals(fprice);
					p.sendMessage(L.get("LINE_BREAK"));
					p.sendMessage(L.f(L.get("ENCHANTMENT_SELL_MESSAGE"), 1, calc.twoDecimals(fprice), name, calc.twoDecimals(salestax)));
					p.sendMessage(L.get("LINE_BREAK"));
					if (hc.useSQL()) {
						String type = "dynamic";
						if (Boolean.parseBoolean(sf.getInitiation(name, playerecon))) {
							type = "initial";
						} else if (Boolean.parseBoolean(sf.getStatic(name, playerecon))) {
							type = "static";
						}
						log.writeSQLLog(p.getName(), "sale", name, 1.0, fprice - salestax, salestax, playerecon, type);
					} else {
						String logentry = L.f(L.get("LOG_SELL_ENCHANTMENT"), 1, calc.twoDecimals(fprice), name, sf.getStatic(name, playerecon), sf.getInitiation(name, playerecon), p);
						log.writeLog(logentry);
					}
					isign.setrequestsignUpdate(true);
					isign.checksignUpdate();
					not.setNotify(hc, calc, this, name, mater, playerecon);
					not.sendNotification();
				} else {
					p.sendMessage(L.get("SHOP_NOT_ENOUGH_MONEY"));
				}
			} else {
				p.sendMessage(L.f(L.get("ITEM_DOESNT_HAVE_ENCHANTMENT"), name));
			}
		} catch (Exception e) {
			String info = "ETransaction sellEnchant() passed values name='" + name + "', player='" + p.getName() + "'";
			new HyperError(e, info);
		}
	}

	/**
	 * 
	 * 
	 * This function handles the purchase of enchantments.
	 * 
	 */
	public void buyEnchant(String name, Player p) {
		DataFunctions sf = hc.getSQLFunctions();
		Calculation calc = hc.getCalculation();
		Account acc = hc.getAccount();
		Log log = hc.getLog();
		Notification not = hc.getNotify();
		InfoSign isign = hc.getInfoSign();
		try {
			String playerecon = sf.getPlayerEconomy(p.getName());
			String nenchant = sf.getMaterial(name, playerecon);
			Enchantment ench = Enchantment.getByName(nenchant);
			int shopstock = 0;
			shopstock = (int) sf.getStock(name, playerecon);
			if (shopstock >= 1) {
				String mater = p.getItemInHand().getType().toString();
				double price = calc.getEnchantCost(name, mater, playerecon);
				price = price + calc.getEnchantTax(name, playerecon, price);
				if (price != 123456789) {
					if (!p.getItemInHand().containsEnchantment(ench)) {
						if (acc.checkFunds(price, p)) {
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
								acc.withdraw(price, p);
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
								price = calc.twoDecimals(price);
								p.sendMessage(L.get("LINE_BREAK"));
								p.sendMessage(L.f(L.get("ENCHANTMENT_PURCHASE_MESSAGE"), 1, price, name, calc.twoDecimals(taxpaid)));
								p.sendMessage(L.get("LINE_BREAK"));
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
									logentry = L.f(L.get("LOG_BUY_ENCHANTMENT"), 1, calc.twoDecimals(price), name, sf.getStatic(name, playerecon), sf.getInitiation(name, playerecon), p);
									log.writeLog(logentry);
								}
								isign.setrequestsignUpdate(true);
								isign.checksignUpdate();
								not.setNotify(hc, calc, this, name, mater, playerecon);
								not.sendNotification();
							} else {
								p.sendMessage(L.get("ITEM_CANT_ACCEPT_ENCHANTMENT"));
							}
						} else {
							p.sendMessage(L.get("INSUFFICIENT_FUNDS"));
						}
					} else {
						p.sendMessage(L.get("ITEM_ALREADY_HAS_ENCHANTMENT"));
					}
				} else {
					p.sendMessage(L.get("ITEM_CANT_ACCEPT_ENCHANTMENT"));
				}
			} else {
				p.sendMessage(L.f(L.get("THE_SHOP_DOESNT_HAVE_ENOUGH"), name));
			}
		} catch (Exception e) {
			String info = "ETransaction buyEnchant() passed values name='" + name + "', player='" + p.getName() + "'";
			new HyperError(e, info);
		}
	}

	/**
	 * 
	 * 
	 * This function handles the purchase of chestshop enchantments.
	 * 
	 */
	public boolean buyChestEnchant(String name, Player p, ItemStack item, String owner) {
		DataFunctions sf = hc.getSQLFunctions();
		Calculation calc = hc.getCalculation();
		Account acc = hc.getAccount();
		Log log = hc.getLog();
		try {
			String nenchant = "";
			String playerecon = sf.getPlayerEconomy(owner);
			nenchant = sf.getMaterial(name, playerecon);
			Enchantment ench = Enchantment.getByName(nenchant);
			String mater = p.getItemInHand().getType().toString();
			double price = calc.getEnchantValue(name, mater, playerecon);
			if (price != 123456789) {
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
						if (acc.checkFunds(price, p)) {
							acc.withdraw(price, p);
							acc.depositAccount(owner, price);
							int l = name.length();
							String lev = name.substring(l - 1, l);
							int level = Integer.parseInt(lev);
							p.getItemInHand().addEnchantment(ench, level);
							item.removeEnchantment(ench);
							price = calc.twoDecimals(price);
							p.sendMessage(L.get("LINE_BREAK"));
							p.sendMessage(L.f(L.get("PURCHASE_ENCHANTMENT_CHEST_MESSAGE"), 1, calc.twoDecimals(price), name, owner));
							p.sendMessage(L.get("LINE_BREAK"));
							String logentry = "";
							if (hc.useSQL()) {
								log.writeSQLLog(p.getName(), "purchase", name, 1.0, price, 0.0, owner, "chestshop");
							} else {
								logentry = L.f(L.get("LOG_BUY_CHEST_ENCHANTMENT"), 1, calc.twoDecimals(price), name, sf.getStatic(name, playerecon), sf.getInitiation(name, playerecon), p, owner);
								log.writeLog(logentry);
							}
							Player o = Bukkit.getPlayer(owner);
							if (o != null) {
								o.sendMessage(L.f(L.get("CHEST_ENCHANTMENT_BUY_NOTIFICATION"), 1, calc.twoDecimals(price), name, p));
							}
							return true;
						} else {
							p.sendMessage(L.get("INSUFFICIENT_FUNDS"));
						}
					} else {
						p.sendMessage(L.get("ITEM_CANT_ACCEPT_ENCHANTMENT"));
					}
				} else {
					p.sendMessage(L.get("ITEM_ALREADY_HAS_ENCHANTMENT"));
				}
			} else {
				p.sendMessage(L.get("ITEM_CANT_ACCEPT_ENCHANTMENT"));
			}
			return false;
		} catch (Exception e) {
			String info = "ETransaction buyChestEnchant() passed values name='" + name + "', player='" + p.getName() + "', owner='" + owner + "'";
			new HyperError(e, info);
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
		DataFunctions sf = hc.getSQLFunctions();
		Calculation calc = hc.getCalculation();
		Account acc = hc.getAccount();
		Log log = hc.getLog();
		try {
			String nenchant = "";
			String playerecon = sf.getPlayerEconomy(owner);
			nenchant = sf.getMaterial(name, playerecon);
			Enchantment ench = Enchantment.getByName(nenchant);
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
					if (acc.checkFunds(price, p)) {
						acc.withdraw(price, p);
						acc.depositAccount(owner, price);
						int l = name.length();
						String lev = name.substring(l - 1, l);
						int level = Integer.parseInt(lev);
						p.getItemInHand().addEnchantment(ench, level);
						item.removeEnchantment(ench);
						price = calc.twoDecimals(price);
						p.sendMessage(L.get("LINE_BREAK"));
						p.sendMessage(L.f(L.get("PURCHASE_ENCHANTMENT_CHEST_MESSAGE"), 1, calc.twoDecimals(price), name, owner));
						p.sendMessage(L.get("LINE_BREAK"));
						String logentry = "";
						if (hc.useSQL()) {
							log.writeSQLLog(p.getName(), "purchase", name, 1.0, price, 0.0, owner, "chestshop");
						} else {
							logentry = L.f(L.get("LOG_BUY_CHEST_ENCHANTMENT"), 1, calc.twoDecimals(price), name, sf.getStatic(name, playerecon), sf.getInitiation(name, playerecon), p, owner);
							log.writeLog(logentry);
						}
						Player o = Bukkit.getPlayer(owner);
						if (o != null) {
							o.sendMessage(L.f(L.get("CHEST_ENCHANTMENT_BUY_NOTIFICATION"), 1, calc.twoDecimals(price), name, p));
						}
						return true;
					} else {
						p.sendMessage(L.get("INSUFFICIENT_FUNDS"));
					}
				} else {
					p.sendMessage(L.get("ITEM_CANT_ACCEPT_ENCHANTMENT"));
				}
			} else {
				p.sendMessage(L.get("ITEM_ALREADY_HAS_ENCHANTMENT"));
			}
			return false;
		} catch (Exception e) {
			String info = "ETransaction buyChestEnchant() passed values name='" + name + "', player='" + p.getName() + "', owner='" + owner + "', price='" + price + "'";
			new HyperError(e, info);
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
			String info = "ETransaction getDuramult() passed values player='" + p.getName() + "'";
			new HyperError(e, info);
			return 0;
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
			if (stack != null) {
				Map<Enchantment, Integer> enchants = stack.getEnchantments();
				hasenchants = !enchants.isEmpty();
			}
			return hasenchants;
		} catch (Exception e) {
			new HyperError(e);
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
			String info = "ETransaction getclassValue() passed values matname='" + matname + "'";
			new HyperError(e, info);
			return 987654321;
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
