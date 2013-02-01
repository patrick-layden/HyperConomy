package regalowl.hyperconomy;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;

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
		Enchantment[] ens = Enchantment.values();
		for (Enchantment e:ens) {
			enchantments.add(e);
		}
	}

	/**
	 * 
	 * 
	 * This function handles the sale of enchantments.
	 * 
	 */
	public void sellEnchant(String name, Player p) {
		DataHandler sf = hc.getDataFunctions();
		Calculation calc = hc.getCalculation();
		Account acc = hc.getAccount();
		Log log = hc.getLog();
		Notification not = hc.getNotify();
		InfoSignHandler isign = hc.getInfoSignHandler();
		try {
			String nenchant = "";
			String playerecon = sf.getHyperPlayer(p.getName()).getEconomy();
			nenchant = sf.getHyperObject(name, playerecon).getMaterial();
			Enchantment ench = Enchantment.getByName(nenchant);
			int lvl = Integer.parseInt(name.substring(name.length() - 1, name.length()));
			int truelvl = getEnchantmentLevel(p.getItemInHand(), ench);
			if (containsEnchantment(p.getItemInHand(), ench) && lvl == truelvl) {
				double dura = p.getItemInHand().getDurability();
				double maxdura = p.getItemInHand().getType().getMaxDurability();
				double duramult = (1 - dura / maxdura);
				if (p.getItemInHand().getType().equals(Material.ENCHANTED_BOOK)) {
					duramult = 1;
				}
				String mater = p.getItemInHand().getType().toString();
				double price = calc.getEnchantValue(name, EnchantmentClass.fromString(mater), playerecon);
				double fprice = duramult * price;
				boolean sunlimited = hc.getYaml().getConfig().getBoolean("config.shop-has-unlimited-money");
				if (acc.checkshopBalance(fprice) || sunlimited) {
					removeEnchantment(p.getItemInHand(), ench);
					double shopstock = sf.getHyperObject(name, playerecon).getStock();
					sf.getHyperObject(name, playerecon).setStock(shopstock + duramult);
					double salestax = calc.getSalesTax(p, fprice);
					acc.deposit(fprice - salestax, p);
					acc.withdrawShop(fprice - salestax);
					if (sunlimited) {
						String globalaccount = hc.getYaml().getConfig().getString("config.global-shop-account");
						acc.setBalance(0, globalaccount);
					}
					fprice = calc.twoDecimals(fprice);
					p.sendMessage(L.get("LINE_BREAK"));
					p.sendMessage(L.f(L.get("ENCHANTMENT_SELL_MESSAGE"), 1, calc.twoDecimals(fprice), name, calc.twoDecimals(salestax)));
					p.sendMessage(L.get("LINE_BREAK"));

					String type = "dynamic";
					if (Boolean.parseBoolean(sf.getHyperObject(name, playerecon).getInitiation())) {
						type = "initial";
					} else if (Boolean.parseBoolean(sf.getHyperObject(name, playerecon).getIsstatic())) {
						type = "static";
					}
					log.writeSQLLog(p.getName(), "sale", name, 1.0, fprice - salestax, salestax, playerecon, type);

					isign.updateSigns();
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
		DataHandler sf = hc.getDataFunctions();
		Calculation calc = hc.getCalculation();
		Account acc = hc.getAccount();
		Log log = hc.getLog();
		Notification not = hc.getNotify();
		InfoSignHandler isign = hc.getInfoSignHandler();
		try {
			String playerecon = sf.getHyperPlayer(p.getName()).getEconomy();
			String nenchant = sf.getHyperObject(name, playerecon).getMaterial();
			Enchantment ench = Enchantment.getByName(nenchant);
			int shopstock = 0;
			shopstock = (int) sf.getHyperObject(name, playerecon).getStock();
			if (shopstock >= 1) {
				String mater = p.getItemInHand().getType().toString();
				double price = calc.getEnchantCost(name, EnchantmentClass.fromString(mater), playerecon);
				price = price + calc.getEnchantTax(name, playerecon, price);
				if (price != 123456789) {
					if (!containsEnchantment(p.getItemInHand(), ench)) {
						if (acc.checkFunds(price, p)) {
							if (canAcceptEnchantment(p.getItemInHand(), ench) && p.getItemInHand().getAmount() == 1) {
								sf.getHyperObject(name, playerecon).setStock(shopstock - 1);
								acc.withdraw(price, p);
								acc.depositShop(price);
								if (hc.getYaml().getConfig().getBoolean("config.shop-has-unlimited-money")) {
									String globalaccount = hc.getYaml().getConfig().getString("config.global-shop-account");
									acc.setBalance(0, globalaccount);
								}
								int l = name.length();
								String lev = name.substring(l - 1, l);
								int level = Integer.parseInt(lev);
								addEnchantment(p.getItemInHand(), ench, level);
								boolean stax;
								stax = Boolean.parseBoolean(sf.getHyperObject(name, playerecon).getIsstatic());
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
								String type = "dynamic";
								if (Boolean.parseBoolean(sf.getHyperObject(name, playerecon).getInitiation())) {
									type = "initial";
								} else if (Boolean.parseBoolean(sf.getHyperObject(name, playerecon).getIsstatic())) {
									type = "static";
								}
								log.writeSQLLog(p.getName(), "purchase", name, 1.0, price, taxpaid, playerecon, type);

								isign.updateSigns();
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
		DataHandler sf = hc.getDataFunctions();
		Calculation calc = hc.getCalculation();
		Account acc = hc.getAccount();
		Log log = hc.getLog();
		try {
			String nenchant = "";
			String playerecon = sf.getHyperPlayer(owner).getEconomy();
			nenchant = sf.getHyperObject(name, playerecon).getMaterial();
			Enchantment ench = Enchantment.getByName(nenchant);
			String mater = p.getItemInHand().getType().toString();
			double price = calc.getEnchantValue(name, EnchantmentClass.fromString(mater), playerecon);
			if (price != 123456789) {
				if (!containsEnchantment(p.getItemInHand(), ench)) {
					if (canAcceptEnchantment(p.getItemInHand(), ench) && p.getItemInHand().getAmount() == 1) {
						if (acc.checkFunds(price, p)) {
							acc.withdraw(price, p);
							acc.depositAccount(price, owner);
							int l = name.length();
							String lev = name.substring(l - 1, l);
							int level = Integer.parseInt(lev);
							addEnchantment(p.getItemInHand(), ench, level);
							removeEnchantment(item, ench);
							price = calc.twoDecimals(price);
							p.sendMessage(L.get("LINE_BREAK"));
							p.sendMessage(L.f(L.get("PURCHASE_ENCHANTMENT_CHEST_MESSAGE"), 1, calc.twoDecimals(price), name, owner));
							p.sendMessage(L.get("LINE_BREAK"));
							log.writeSQLLog(p.getName(), "purchase", name, 1.0, price, 0.0, owner, "chestshop");
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
		DataHandler sf = hc.getDataFunctions();
		Calculation calc = hc.getCalculation();
		Account acc = hc.getAccount();
		Log log = hc.getLog();
		try {
			String nenchant = "";
			String playerecon = sf.getHyperPlayer(owner).getEconomy();
			nenchant = sf.getHyperObject(name, playerecon).getMaterial();
			Enchantment ench = Enchantment.getByName(nenchant);
			if (!containsEnchantment(p.getItemInHand(), ench)) {
				if (canAcceptEnchantment(p.getItemInHand(), ench) && p.getItemInHand().getAmount() == 1) {
					if (acc.checkFunds(price, p)) {
						acc.withdraw(price, p);
						acc.depositAccount(price, owner);
						int l = name.length();
						String lev = name.substring(l - 1, l);
						int level = Integer.parseInt(lev);
						addEnchantment(p.getItemInHand(), ench, level);
						removeEnchantment(item, ench);
						price = calc.twoDecimals(price);
						p.sendMessage(L.get("LINE_BREAK"));
						p.sendMessage(L.f(L.get("PURCHASE_ENCHANTMENT_CHEST_MESSAGE"), 1, calc.twoDecimals(price), name, owner));
						p.sendMessage(L.get("LINE_BREAK"));
						log.writeSQLLog(p.getName(), "purchase", name, 1.0, price, 0.0, owner, "chestshop");
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
			for (Enchantment enchant:enchantments) {
				if (enchant.canEnchantItem(stack)) {
					enchantable = true;
				}
			}
			return enchantable;
		}
	}
	
	public boolean canAcceptEnchantment(ItemStack stack, Enchantment e) {
		if (e == null || stack == null) {
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
			enchantments.add(hc.getEnchantData(e.getName()) + enchants.get(e));
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
}
