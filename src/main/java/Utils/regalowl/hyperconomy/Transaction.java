package regalowl.hyperconomy;

import java.util.HashMap;
import java.util.logging.Logger;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;
import org.bukkit.potion.Potion;

/**
 * 
 * 
 * This class handles the purchase and sale of items.
 * 
 */
public class Transaction {
	private HyperConomy hc;

	/**
	 * 
	 * 
	 * Transaction constructor.
	 * 
	 */
	Transaction() {
		hc = HyperConomy.hc;
	}

	/**
	 * 
	 * 
	 * This function handles the purchase of items.
	 * 
	 */
	
	public void buy(String name, int amount, int id, int data, Player p) {
		try {
			DataFunctions sf = hc.getSQLFunctions();
			Calculation calc = hc.getCalculation();
			ETransaction ench = hc.getETransaction();
			LanguageFile L = hc.getLanguageFile();
			Account acc = hc.getAccount();
			Log log = hc.getLog();
			Notification not = hc.getNotify();
			InfoSignHandler isign = hc.getInfoSignHandler();
			String playerecon = sf.getPlayerEconomy(p.getName());
			if (amount > 0) {
				double shopstock = sf.getStock(name, playerecon);
				if (shopstock >= amount) {
					if (id >= 0) {
						double price = calc.getCost(name, amount, playerecon);
						double taxpaid = calc.getPurchaseTax(name, playerecon, price);
						price = calc.twoDecimals(price + taxpaid);
						if (acc.checkFunds(price, p)) {
							int space = getavailableSpace(id, data, p);
							if (space >= amount) {
								addboughtItems(amount, id, data, p);
								if (!Boolean.parseBoolean(sf.getStatic(name, playerecon)) || !hc.getConfig().getBoolean("config.unlimited-stock-for-static-items")) {
									sf.setStock(name, playerecon, shopstock - amount);
								}
								acc.withdraw(price, p);
								acc.depositShop(price);
								if (hc.getYaml().getConfig().getBoolean("config.shop-has-unlimited-money")) {
									String globalaccount = hc.getYaml().getConfig().getString("config.global-shop-account");
									acc.setBalance(0, globalaccount);
								}
								p.sendMessage(L.get("LINE_BREAK"));
								p.sendMessage(L.f(L.get("PURCHASE_MESSAGE"), amount, price, name, calc.twoDecimals(taxpaid)));
								p.sendMessage(L.get("LINE_BREAK"));
								if (hc.useSQL()) {
									String type = "dynamic";
									if (Boolean.parseBoolean(sf.getInitiation(name, playerecon))) {
										type = "initial";
									} else if (Boolean.parseBoolean(sf.getStatic(name, playerecon))) {
										type = "static";
									}
									log.writeSQLLog(p.getName(), "purchase", name, (double) amount, calc.twoDecimals(price - taxpaid), calc.twoDecimals(taxpaid), playerecon, type);
								} else {
									String logentry = L.f(L.get("LOG_BUY"), amount, calc.twoDecimals(price), name, sf.getStatic(name, playerecon), sf.getInitiation(name, playerecon), p);
									log.writeLog(logentry);
								}
								isign.updateSigns();
								not.setNotify(hc, calc, ench, name, null, playerecon);
								not.sendNotification();
							} else {
								p.sendMessage(L.f(L.get("ONLY_ROOM_TO_BUY"), space, name));
							}
						} else {
							p.sendMessage(L.get("INSUFFICIENT_FUNDS"));
						}
					} else {
						p.sendMessage(L.f(L.get("CANNOT_BE_PURCHASED_WITH"), name));
					}
				} else {
					p.sendMessage(L.f(L.get("THE_SHOP_DOESNT_HAVE_ENOUGH"), name));
				}
			} else {
				p.sendMessage(L.f(L.get("CANT_BUY_LESS_THAN_ONE"), name));
			}
		} catch (Exception e) {
			String info = "Transaction buy() passed values name='" + name + "', player='" + p.getName() + "', id='" + id + "', data='" + data + "', amount='" + amount + "'";
			new HyperError(e, info);
		}
	}

	/**
	 * 
	 * 
	 * This function handles the sale of items.
	 * 
	 */
	public void sell(String name, int id, int data, int amount, Player p) {
		try {
			DataFunctions sf = hc.getSQLFunctions();
			Calculation calc = hc.getCalculation();
			ETransaction ench = hc.getETransaction();
			Account acc = hc.getAccount();
			LanguageFile L = hc.getLanguageFile();
			Log log = hc.getLog();
			Notification not = hc.getNotify();
			InfoSignHandler isign = hc.getInfoSignHandler();
			String playerecon = sf.getPlayerEconomy(p.getName());
			if (amount > 0) {
				if (id >= 0) {
					int totalitems = countInvitems(id, data, p);
					if (totalitems < amount) {
						amount = totalitems;
					}
					if (amount > 0) {
						double price = calc.getValue(name, amount, p);
						Boolean toomuch = false;
						if (price == 3235624645000.7) {
							toomuch = true;
						}
						if (!toomuch) {
							int maxi = getmaxInitial(name, p);
							boolean isstatic = false;
							boolean isinitial = false;
							isinitial = Boolean.parseBoolean(sf.getInitiation(name, playerecon));
							isstatic = Boolean.parseBoolean(sf.getStatic(name, playerecon));
							if ((amount > maxi) && !isstatic && isinitial) {
								amount = maxi;
								price = calc.getValue(name, amount, p);
							}
							boolean sunlimited = hc.getYaml().getConfig().getBoolean("config.shop-has-unlimited-money");
							if (acc.checkshopBalance(price) || sunlimited) {
								if (maxi == 0) {
									price = calc.getValue(name, amount, p);
								}
								double trueAmount = removesoldItems(id, data, amount, p);
								double shopstock = 0;
								shopstock = sf.getStock(name, playerecon);
								if (!Boolean.parseBoolean(sf.getStatic(name, playerecon)) || !hc.getConfig().getBoolean("config.unlimited-stock-for-static-items")) {
									sf.setStock(name, playerecon, (shopstock + trueAmount));
								}
								int maxi2 = getmaxInitial(name, p);
								if (maxi2 == 0) {
									sf.setInitiation(name, playerecon, "false");
								}
								double salestax = calc.getSalesTax(p, price);
								acc.deposit(price - salestax, p);
								acc.withdrawShop(price - salestax);
								if (sunlimited) {
									String globalaccount = hc.getYaml().getConfig().getString("config.global-shop-account");
									acc.setBalance(0, globalaccount);
								}
								p.sendMessage(L.get("LINE_BREAK"));
								p.sendMessage(L.f(L.get("SELL_MESSAGE"), amount, calc.twoDecimals(price), name, calc.twoDecimals(salestax)));
								p.sendMessage(L.get("LINE_BREAK"));
								World w = p.getWorld();
								w.playEffect(p.getLocation(), Effect.SMOKE, 4);
								String logentry = "";
								if (hc.useSQL()) {
									String type = "dynamic";
									if (Boolean.parseBoolean(sf.getInitiation(name, playerecon))) {
										type = "initial";
									} else if (Boolean.parseBoolean(sf.getStatic(name, playerecon))) {
										type = "static";
									}
									log.writeSQLLog(p.getName(), "sale", name, (double) amount, calc.twoDecimals(price - salestax), calc.twoDecimals(salestax), playerecon, type);
								} else {
									logentry = L.f(L.get("LOG_SELL"), amount, calc.twoDecimals(price), name, sf.getStatic(name, playerecon), sf.getInitiation(name, playerecon), p);
									log.writeLog(logentry);
								}
								isign.updateSigns();
								not.setNotify(hc, calc, ench, name, null, playerecon);
								not.sendNotification();
							} else {
								p.sendMessage(L.get("SHOP_NOT_ENOUGH_MONEY"));
							}
						} else {
							p.sendMessage(L.f(L.get("CURRENTLY_CANT_SELL_MORE_THAN"), sf.getStock(name, playerecon), name));
						}
					} else {
						p.sendMessage(L.f(L.get("YOU_DONT_HAVE_ENOUGH"), name));
					}
				} else {
					p.sendMessage(L.f(L.get("CANNOT_BE_SOLD_WITH"), name));
				}
			} else {
				p.sendMessage(L.f(L.get("CANT_SELL_LESS_THAN_ONE"), name));
			}
		} catch (Exception e) {
			String info = "Transaction sell() passed values name='" + name + "', player='" + p.getName() + "', id='" + id + "', data='" + data + "', amount='" + amount + "'";
			new HyperError(e, info);
		}
	}

	/**
	 * 
	 * 
	 * This function counts the number of the specified item in a player's
	 * inventory. It ignores durability.
	 * 
	 */
	public int countInvitems(int id, int data, Player p) {
		try {
			int totalitems = 0;
			Calculation calc = hc.getCalculation();
			ETransaction ench = hc.getETransaction();
			data = calc.newData(id, data);
			Inventory pinv = p.getInventory();
			ItemStack[] stacks = pinv.getContents();
			for (ItemStack stack:stacks) {
				if (stack != null && !ench.hasenchants(stack)) {
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
			String info = "Transaction countInvItems() passed values player='" + p.getName() + "', id='" + id + "', data='" + data + "'";
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
	public int getavailableSpace(int itd, int idata, Player p) {
		Calculation calc = hc.getCalculation();
		int id = itd;
		int data = idata;
		MaterialData md = new MaterialData(id, (byte) data);
		ItemStack stack = md.toItemStack();
		int maxstack = stack.getMaxStackSize();
		try {
			int availablespace = 0;
			int slot = 0;
			while (slot < 36) {
				ItemStack citem = p.getInventory().getItem(slot);
				if (p.getInventory().getItem(slot) == null) {
					availablespace = availablespace + maxstack;
				} else if (citem != null && citem.getTypeId() == id && idata == calc.getDamageValue(citem)) {
					availablespace = availablespace + (maxstack - citem.getAmount());
				}
				slot++;
			}
			return availablespace;
		} catch (Exception e) {
			String info = "Transaction getAvailableSpace() passed values player='" + p.getName() + "', id='" + id + "', data='" + data + "'";
			new HyperError(e, info);
			int availablespace = 0;
			return availablespace;
		}
	}

	/**
	 * 
	 * 
	 * This function determines how much more of an item an inventory can hold.
	 * 
	 */
	public int getInventoryAvailableSpace(int itd, int idata, Inventory inv, int slots) {
		Calculation calc = hc.getCalculation();
		int id = itd;
		int data = idata;
		MaterialData md = new MaterialData(id, (byte) data);
		ItemStack stack = md.toItemStack();
		int maxstack = stack.getMaxStackSize();
		try {
			int availablespace = 0;
			int slot = 0;
			while (slot < slots) {
				ItemStack citem = inv.getItem(slot);
				if (inv.getItem(slot) == null) {
					availablespace = availablespace + maxstack;
				} else if (citem != null && citem.getTypeId() == id && idata == calc.getDamageValue(citem)) {
					availablespace = availablespace + (maxstack - citem.getAmount());
				}
				slot++;
			}
			return availablespace;
		} catch (Exception e) {
			String info = "Transaction getInventoryAvailableSpace() passed values id='" + id + "', data='" + data + "'";
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
	private void addboughtItems(int amount, int itd, int idata, Player p) {
		Calculation calc = hc.getCalculation();
		try {
			int id = itd;
			int data = idata;
			int ramount = amount;
			MaterialData md = new MaterialData(id, (byte) data);
			ItemStack stack = md.toItemStack();
			int maxstack = stack.getMaxStackSize();
			int slot = 0;
			while (ramount > 0) {
				int pamount = 0;
				ItemStack citem = p.getInventory().getItem(slot);
				if (citem != null && citem.getTypeId() == id && data == calc.getDamageValue(citem)) {
					int currentamount = citem.getAmount();
					if ((maxstack - currentamount) >= ramount) {
						pamount = ramount;
						citem.setAmount(pamount + currentamount);
					} else {
						pamount = maxstack - currentamount;
						citem.setAmount(maxstack);
					}
				} else if (p.getInventory().getItem(slot) == null) {
					ItemStack stack2;
					if (id == 373 && data != 0) {
						Potion pot = Potion.fromDamage(data);
						stack2 = pot.toItemStack(amount);
					} else {
						stack2 = md.toItemStack();
					}
					if (ramount > maxstack) {
						pamount = maxstack;
					} else {
						pamount = ramount;
					}
					stack2.setAmount(pamount);
					p.getInventory().setItem(slot, stack2);
				}
				ramount = ramount - pamount;
				slot++;
			}
			p.updateInventory();
		} catch (Exception e) {
			String info = "Transaction addBoughtItems() passed values player='" + p.getName() + "', id='" + itd + "', data='" + idata + "', amount='" + amount + "'";
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
	private double removesoldItems(int id, int data, int amount, Player p) {
		Calculation calc = hc.getCalculation();
		ETransaction ench = hc.getETransaction();
		double trueAmount = 0;
		if (!calc.isDurable(id)) {
			trueAmount = amount;
		}
		try {
			int newdata = calc.newData(id, data);
			Inventory pinv = p.getInventory();
			HashMap<Integer, ? extends ItemStack> stacks1 = pinv.all(id);
			if (p.getInventory().contains(id)) {
				String stringstacks = stacks1.toString();
				int maxstack = stacks1.get(Integer.parseInt(stringstacks.substring(1, stringstacks.indexOf("=")))).getMaxStackSize();
				ItemStack inhand = p.getItemInHand();
				int dv = calc.getDamageValue(inhand);
				boolean hasenchants = ench.hasenchants(inhand);
				int ritems = amount;
				if (inhand != null && calc.newData(id, dv) == newdata && inhand.getTypeId() == id && hasenchants == false) {
					int amountinhand = inhand.getAmount();
					if (amountinhand > ritems) {
						if (calc.isDurable(inhand.getTypeId())) {
							trueAmount += calc.getdurabilityPercent(inhand);
						}
						inhand.setAmount(amountinhand - ritems);
						ritems = 0;
					} else if (amountinhand <= ritems) {
						Inventory invent = p.getInventory();
						int heldslot = p.getInventory().getHeldItemSlot();
						if (calc.isDurable(pinv.getItem(heldslot).getTypeId())) {
							trueAmount += calc.getdurabilityPercent(pinv.getItem(heldslot));
						}
						invent.clear(heldslot);
						ritems = ritems - amountinhand;
					}
				}
				int slot;
				String allstacks = "{" + stringstacks;
				while (allstacks.contains(" x ")) {
					int a = allstacks.indexOf(" x ") + 3;
					int b = allstacks.indexOf("}", a);
					slot = Integer.parseInt(allstacks.substring(2, allstacks.indexOf("=")));
					int damv = calc.getDamageValue(pinv.getItem(slot));
					boolean hasenchants2 = ench.hasenchants(stacks1.get(slot));
					if (pinv.getItem(slot) != null && calc.newData(id, damv) == newdata && pinv.getItem(slot).getTypeId() == id && hasenchants2 == false) {
						if (ritems > 0) {
							if (ritems >= maxstack && pinv.getItem(slot).getAmount() == maxstack) {
								if (calc.isDurable(pinv.getItem(slot).getTypeId())) {
									trueAmount += calc.getdurabilityPercent(pinv.getItem(slot));
								}
								pinv.clear(slot);
								ritems = ritems - maxstack;
							} else {
								int stackamount = pinv.getItem(slot).getAmount();
								if (stackamount <= ritems) {
									if (calc.isDurable(pinv.getItem(slot).getTypeId())) {
										trueAmount += calc.getdurabilityPercent(pinv.getItem(slot));
									}
									pinv.clear(slot);
									ritems = ritems - stackamount;
								} else {
									if (calc.isDurable(pinv.getItem(slot).getTypeId())) {
										trueAmount += calc.getdurabilityPercent(pinv.getItem(slot));
									}
									pinv.getItem(slot).setAmount(stackamount - ritems);
									ritems = 0;
								}
							}
						}
					}
					allstacks = allstacks.substring(b + 1, allstacks.length());
				}
			}
			return trueAmount;
		} catch (Exception e) {
			String info = "Transaction removeSoldItems() passed values player='" + p.getName() + "', id='" + id + "', data='" + data + "', amount='" + amount + "'";
			new HyperError(e, info);
			return amount;
		}
	}

	/**
	 * 
	 * 
	 * This function returns the maximum number of items that can be sold before
	 * reaching the hyperbolic pricing curve.
	 * 
	 */
	private int getmaxInitial(String name, Player p) {
		DataFunctions sf = hc.getSQLFunctions();
		String playerecon = sf.getPlayerEconomy(p.getName());
		try {
			int maxinitialitems = 0;
			double shopstock = sf.getStock(name, playerecon);
			double value = sf.getValue(name, playerecon);
			double median = sf.getMedian(name, playerecon);
			double icost = sf.getStartPrice(name, playerecon);
			double totalstock = ((median * value) / icost);
			maxinitialitems = (int) (Math.ceil(totalstock) - shopstock);
			return maxinitialitems;
		} catch (Exception e) {
			String info = "Transaction getmaxInitial() passed values player='" + p.getName() + "', name='" + name + "'";
			new HyperError(e, info);
			int maxinitialitems = 0;
			return maxinitialitems;
		}
	}

	/**
	 * 
	 * 
	 * This function handles the purchase of experience.
	 * 
	 */
	public void buyXP(String name, int amount, Player p) {
		try {
			DataFunctions sf = hc.getSQLFunctions();
			Calculation calc = hc.getCalculation();
			ETransaction ench = hc.getETransaction();
			Account acc = hc.getAccount();
			LanguageFile L = hc.getLanguageFile();
			Log log = hc.getLog();
			Notification not = hc.getNotify();
			InfoSignHandler isign = hc.getInfoSignHandler();
			String playerecon = sf.getPlayerEconomy(p.getName());
			if (amount > 0) {
				int shopstock = 0;
				shopstock = (int) sf.getStock(name, playerecon);
				if (shopstock >= amount) {
					double price = calc.getCost(name, amount, playerecon);
					double taxpaid = calc.getPurchaseTax(name, playerecon, price);
					price = calc.twoDecimals(price + taxpaid);
					if (acc.checkFunds(price, p)) {
						int totalxp = calc.gettotalxpPoints(p);
						int newxp = totalxp + amount;
						int newlvl = calc.getlvlfromXP(newxp);
						newxp = newxp - calc.getlvlxpPoints(newlvl);
						float xpbarxp = (float) newxp / (float) calc.getxpfornextLvl(newlvl);
						p.setLevel(newlvl);
						p.setExp(xpbarxp);
						if (!Boolean.parseBoolean(sf.getStatic(name, playerecon)) || !hc.getConfig().getBoolean("config.unlimited-stock-for-static-items")) {
							sf.setStock(name, playerecon, (shopstock - amount));
						}
						acc.withdraw(price, p);
						acc.depositShop(price);
						if (hc.getYaml().getConfig().getBoolean("config.shop-has-unlimited-money")) {
							String globalaccount = hc.getYaml().getConfig().getString("config.global-shop-account");
							acc.setBalance(0, globalaccount);
						}
						p.sendMessage(L.get("LINE_BREAK"));
						p.sendMessage(L.f(L.get("PURCHASE_MESSAGE"), amount, calc.twoDecimals(price), name, calc.twoDecimals(taxpaid)));
						p.sendMessage(L.get("LINE_BREAK"));
						if (hc.useSQL()) {
							String type = "dynamic";
							if (Boolean.parseBoolean(sf.getInitiation(name, playerecon))) {
								type = "initial";
							} else if (Boolean.parseBoolean(sf.getStatic(name, playerecon))) {
								type = "static";
							}
							log.writeSQLLog(p.getName(), "purchase", name, (double) amount, calc.twoDecimals(price), calc.twoDecimals(taxpaid), playerecon, type);
						} else {
							String logentry = L.f(L.get("LOG_BUY"), amount, calc.twoDecimals(price), name, sf.getStatic(name, playerecon), sf.getInitiation(name, playerecon), p);
							log.writeLog(logentry);
						}
						isign.updateSigns();
						not.setNotify(hc, calc, ench, name, null, playerecon);
						not.sendNotification();
					} else {
						p.sendMessage(L.get("INSUFFICIENT_FUNDS"));
					}
				} else {
					p.sendMessage(L.f(L.get("THE_SHOP_DOESNT_HAVE_ENOUGH"), name));
				}
			} else {
				p.sendMessage(L.f(L.get("CANT_BUY_LESS_THAN_ONE"), name));
			}
		} catch (Exception e) {
			String info = "Transaction buyXP() passed values name='" + name + "', player='" + p.getName() + "', amount='" + amount + "'";
			new HyperError(e, info);
		}
	}

	/**
	 * 
	 * 
	 * This function handles the sale of experience.
	 * 
	 */
	public void sellXP(String name, int amount, Player p) {
		try {
			DataFunctions sf = hc.getSQLFunctions();
			Calculation calc = hc.getCalculation();
			ETransaction ench = hc.getETransaction();
			Account acc = hc.getAccount();
			LanguageFile L = hc.getLanguageFile();
			Log log = hc.getLog();
			Notification not = hc.getNotify();
			InfoSignHandler isign = hc.getInfoSignHandler();
			String playerecon = sf.getPlayerEconomy(p.getName());
			if (amount > 0) {
				int totalxp = calc.gettotalxpPoints(p);
				if (totalxp >= amount) {
					double price = calc.getValue(name, amount, p);
					Boolean toomuch = false;
					if (price == 3235624645000.7) {
						toomuch = true;
					}
					if (!toomuch) {
						int maxi = getmaxInitial(name, p);
						boolean itax;
						boolean stax;
						itax = Boolean.parseBoolean(sf.getInitiation(name, playerecon));
						stax = Boolean.parseBoolean(sf.getStatic(name, playerecon));
						if (amount > (maxi) && !stax && itax) {
							amount = maxi;
							price = calc.getValue(name, amount, p);
						}
						boolean sunlimited = hc.getYaml().getConfig().getBoolean("config.shop-has-unlimited-money");
						if (acc.checkshopBalance(price) || sunlimited) {
							if (maxi == 0) {
								price = calc.getValue(name, amount, p);
							}
							int newxp = totalxp - amount;
							int newlvl = calc.getlvlfromXP(newxp);
							newxp = newxp - calc.getlvlxpPoints(newlvl);
							float xpbarxp = (float) newxp / (float) calc.getxpfornextLvl(newlvl);
							p.setLevel(newlvl);
							p.setExp(xpbarxp);
							if (!Boolean.parseBoolean(sf.getStatic(name, playerecon)) || !hc.getConfig().getBoolean("config.unlimited-stock-for-static-items")) {
								sf.setStock(name, playerecon, amount + sf.getStock(name, playerecon));
							}
							int maxi2 = getmaxInitial(name, p);
							if (maxi2 == 0) {
								sf.setInitiation(name, playerecon, "false");
							}
							double salestax = calc.getSalesTax(p, price);
							acc.deposit(price - salestax, p);
							acc.withdrawShop(price - salestax);
							if (sunlimited) {
								String globalaccount = hc.getYaml().getConfig().getString("config.global-shop-account");
								acc.setBalance(0, globalaccount);
							}
							p.sendMessage(L.get("LINE_BREAK"));
							p.sendMessage(L.f(L.get("SELL_MESSAGE"), amount, calc.twoDecimals(price), name, calc.twoDecimals(salestax)));
							p.sendMessage(L.get("LINE_BREAK"));
							World w = p.getWorld();
							w.playEffect(p.getLocation(), Effect.SMOKE, 4);
							if (hc.useSQL()) {
								String type = "dynamic";
								if (Boolean.parseBoolean(sf.getInitiation(name, playerecon))) {
									type = "initial";
								} else if (Boolean.parseBoolean(sf.getStatic(name, playerecon))) {
									type = "static";
								}
								log.writeSQLLog(p.getName(), "sale", name, (double) amount, calc.twoDecimals(price - salestax), calc.twoDecimals(salestax), playerecon, type);
							} else {
								String logentry = L.f(L.get("LOG_SELL"), amount, calc.twoDecimals(price), name, sf.getStatic(name, playerecon), sf.getInitiation(name, playerecon), p);
								log.writeLog(logentry);
							}
							isign.updateSigns();
							not.setNotify(hc, calc, ench, name, null, playerecon);
							not.sendNotification();
						} else {
							p.sendMessage(L.get("SHOP_NOT_ENOUGH_MONEY"));
						}
					} else {
						p.sendMessage(L.f(L.get("CURRENTLY_CANT_SELL_MORE_THAN"), sf.getStock(name, playerecon), name));
					}
				} else {
					p.sendMessage(L.f(L.get("YOU_DONT_HAVE_ENOUGH"), name));
				}
			} else {
				p.sendMessage(L.f(L.get("CANT_SELL_LESS_THAN_ONE"), name));
			}
		} catch (Exception e) {
			String info = "Transaction sellXP() passed values name='" + name + "', player='" + p.getName() + "', amount='" + amount + "'";
			new HyperError(e, info);
		}
	}

	/**
	 * 
	 * 
	 * This function handles the purchase of items bought from HyperChests.
	 * 
	 * @args
	 * 
	 */
	public boolean buyChest(String name, int id, int data, String owner, Player p, int amount, Inventory invent) {
		try {
			DataFunctions sf = hc.getSQLFunctions();
			Calculation calc = hc.getCalculation();
			Account acc = hc.getAccount();
			LanguageFile L = hc.getLanguageFile();
			Log log = hc.getLog();
			String playerecon = sf.getPlayerEconomy(owner);
			double price = calc.getTvalue(name, amount, playerecon);
			if (acc.checkFunds(price, p)) {
				int space = getavailableSpace(id, data, p);
				if (space >= amount) {
					addboughtItems(amount, id, data, p);
					removeItems(id, data, amount, invent);
					acc.withdraw(price, p);
					acc.depositAccount(price, owner);
					p.sendMessage(L.get("LINE_BREAK"));
					p.sendMessage(L.f(L.get("PURCHASE_CHEST_MESSAGE"), amount, calc.twoDecimals(price), name, owner));
					p.sendMessage(L.get("LINE_BREAK"));
					if (hc.useSQL()) {
						log.writeSQLLog(p.getName(), "purchase", name, (double) amount, calc.twoDecimals(price), 0.0, owner, "chestshop");
					} else {
						String logentry = L.f(L.get("LOG_BUY_CHEST"), amount, calc.twoDecimals(price), name, sf.getStatic(name, playerecon), sf.getInitiation(name, playerecon), p, owner);
						log.writeLog(logentry);
					}
					Player o = Bukkit.getPlayer(owner);
					if (o != null) {
						o.sendMessage(L.f(L.get("CHEST_BUY_NOTIFICATION"), amount, calc.twoDecimals(price), name, p));
					}
					return true;
				} else {
					p.sendMessage(L.f(L.get("ONLY_ROOM_TO_BUY"), space, name));
				}
			} else {
				p.sendMessage(L.get("INSUFFICIENT_FUNDS"));
			}
			return false;
		} catch (Exception e) {
			String info = "Transaction buyChest() passed values name='" + name + "', player='" + p.getName() + "', owner='" + owner + "', amount='" + amount + "'";
			new HyperError(e, info);
			return false;
		}
	}

	/**
	 * 
	 * 
	 * This function handles the purchase of items bought from HyperChests with
	 * a set price.
	 * 
	 * @args
	 * 
	 */
	public boolean buyChest(String name, int id, int data, String owner, Player p, int amount, Inventory invent, double price) {
		try {
			DataFunctions sf = hc.getSQLFunctions();
			Account acc = hc.getAccount();
			Log log = hc.getLog();
			LanguageFile L = hc.getLanguageFile();
			Calculation calc = hc.getCalculation();
			String playerecon = sf.getPlayerEconomy(owner);
			if (acc.checkFunds(price, p)) {
				int space = getavailableSpace(id, data, p);
				if (space >= amount) {
					addboughtItems(amount, id, data, p);
					removeItems(id, data, amount, invent);
					acc.withdraw(price, p);
					acc.depositAccount(price, owner);
					p.sendMessage(L.get("LINE_BREAK"));
					p.sendMessage(L.f(L.get("PURCHASE_CHEST_MESSAGE"), amount, calc.twoDecimals(price), name, owner));
					p.sendMessage(L.get("LINE_BREAK"));
					if (hc.useSQL()) {
						log.writeSQLLog(p.getName(), "purchase", name, (double) amount, calc.twoDecimals(price), 0.0, owner, "chestshop");
					} else {
						String logentry = L.f(L.get("LOG_BUY_CHEST"), amount, calc.twoDecimals(price), name, sf.getStatic(name, playerecon), sf.getInitiation(name, playerecon), p, owner);
						log.writeLog(logentry);
					}
					Player o = Bukkit.getPlayer(owner);
					if (o != null) {
						o.sendMessage(L.f(L.get("CHEST_BUY_NOTIFICATION"), amount, calc.twoDecimals(price), name, p));
					}
					return true;
				} else {
					p.sendMessage(L.f(L.get("ONLY_ROOM_TO_BUY"), space, name));
				}
			} else {
				p.sendMessage(L.get("INSUFFICIENT_FUNDS"));
			}
			return false;
		} catch (Exception e) {
			String info = "Transaction buyChest() passed values name='" + name + "', player='" + p.getName() + "', owner='" + owner + "', amount='" + amount + "', price='" + price + "'";
			new HyperError(e, info);
			return false;
		}
	}

	/**
	 * 
	 * 
	 * This function handles the sale of items from HyperChests.
	 * 
	 */
	public boolean sellChest(String name, int id, int data, int amount, String owner, Player p, Inventory invent) {
		try {
			DataFunctions sf = hc.getSQLFunctions();
			Calculation calc = hc.getCalculation();
			Account acc = hc.getAccount();
			Log log = hc.getLog();
			LanguageFile L = hc.getLanguageFile();
			String playerecon = sf.getPlayerEconomy(owner);
			double price = calc.getValue(name, amount, p);
			Boolean toomuch = false;
			if (price == 3235624645000.7) {
				toomuch = true;
			}
			if (!toomuch) {
				removesoldItems(id, data, amount, p);
				addItems(id, data, amount, invent);
				acc.deposit(price, p);
				acc.withdrawAccount(price, owner);
				p.sendMessage(L.get("LINE_BREAK"));
				p.sendMessage(L.f(L.get("SELL_CHEST_MESSAGE"), amount, calc.twoDecimals(price), name, owner));
				p.sendMessage(L.get("LINE_BREAK"));
				World w = p.getWorld();
				w.playEffect(p.getLocation(), Effect.SMOKE, 4);
				if (hc.useSQL()) {
					log.writeSQLLog(p.getName(), "sale", name, (double) amount, calc.twoDecimals(price), 0.0, owner, "chestshop");
				} else {
					String logentry = L.f(L.get("LOG_SELL_CHEST"), amount, calc.twoDecimals(price), name, sf.getStatic(name, playerecon), sf.getInitiation(name, playerecon), p, owner);
					log.writeLog(logentry);
				}
				Player o = Bukkit.getPlayer(owner);
				if (o != null) {
					o.sendMessage(L.f(L.get("CHEST_SELL_NOTIFICATION"), amount, calc.twoDecimals(price), name, p));
				}
				return true;
			} else {
				p.sendMessage(L.f(L.get("CURRENTLY_CANT_SELL_MORE_THAN"), sf.getStock(name, playerecon), name));
			}
			return false;
		} catch (Exception e) {
			String info = "Transaction sellChest() passed values name='" + name + "', player='" + p.getName() + "', owner='" + owner + "', amount='" + amount + "'";
			new HyperError(e, info);
			return false;
		}
	}

	/**
	 * 
	 * 
	 * This function handles the sale of items from HyperChests with a set
	 * price.
	 * 
	 */
	public boolean sellChest(String name, int id, int data, int amount, String owner, Player p, Inventory invent, double price) {
		try {
			DataFunctions sf = hc.getSQLFunctions();
			Account acc = hc.getAccount();
			Log log = hc.getLog();
			LanguageFile L = hc.getLanguageFile();
			Calculation calc = hc.getCalculation();
			String playerecon = sf.getPlayerEconomy(owner);
			removesoldItems(id, data, amount, p);
			addItems(id, data, amount, invent);
			acc.deposit(price, p);
			acc.withdrawAccount(price, owner);
			p.sendMessage(L.get("LINE_BREAK"));
			p.sendMessage(L.f(L.get("SELL_CHEST_MESSAGE"), amount, calc.twoDecimals(price), name, owner));
			p.sendMessage(L.get("LINE_BREAK"));
			World w = p.getWorld();
			w.playEffect(p.getLocation(), Effect.SMOKE, 4);
			if (hc.useSQL()) {
				log.writeSQLLog(p.getName(), "sale", name, (double) amount, calc.twoDecimals(price), 0.0, owner, "chestshop");
			} else {
				String logentry = L.f(L.get("LOG_SELL_CHEST"), amount, calc.twoDecimals(price), name, sf.getStatic(name, playerecon), sf.getInitiation(name, playerecon), p, owner);
				log.writeLog(logentry);
			}
			Player o = Bukkit.getPlayer(owner);
			if (o != null) {
				o.sendMessage(L.f(L.get("CHEST_SELL_NOTIFICATION"), amount, calc.twoDecimals(price), name, p));
			}
			return true;
		} catch (Exception e) {
			String info = "Transaction buyChest() passed values name='" + name + "', player='" + p.getName() + "', owner='" + owner + "', amount='" + amount + "', price='" + price + "'";
			new HyperError(e, info);
			return false;
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
	public void removeItems(int id, int data, int amount, Inventory invent) {
		try {
			Calculation calc = hc.getCalculation();
			ETransaction ench = hc.getETransaction();
			int newdata = calc.newData(id, data);
			HashMap<Integer, ? extends ItemStack> stacks1 = invent.all(id);
			if (invent.contains(id)) {
				String stringstacks = stacks1.toString();
				int maxstack = stacks1.get(Integer.parseInt(stringstacks.substring(1, stringstacks.indexOf("=")))).getMaxStackSize();
				int ritems = amount;
				int slot;
				String allstacks = "{" + stringstacks;
				while (allstacks.contains(" x ")) {
					int a = allstacks.indexOf(" x ") + 3;
					int b = allstacks.indexOf("}", a);
					slot = Integer.parseInt(allstacks.substring(2, allstacks.indexOf("=")));
					int damv = calc.getDamageValue(invent.getItem(slot));
					boolean hasenchants2 = ench.hasenchants(stacks1.get(slot));
					if (invent.getItem(slot) != null && calc.newData(id, damv) == newdata && invent.getItem(slot).getTypeId() == id && hasenchants2 == false) {
						if (ritems > 0) {
							if (ritems >= maxstack && invent.getItem(slot).getAmount() == maxstack) {
								invent.clear(slot);
								ritems = ritems - maxstack;
							} else {
								int stackamount = invent.getItem(slot).getAmount();
								if (stackamount <= ritems) {
									invent.clear(slot);
									ritems = ritems - stackamount;
								} else {
									invent.getItem(slot).setAmount(stackamount - ritems);
									ritems = 0;
								}
							}
						}
					}
					allstacks = allstacks.substring(b + 1, allstacks.length());
				}
			} else {
				Logger log = Logger.getLogger("Minecraft");
				log.info("HyperConomy ERROR #39");
				Bukkit.broadcast(ChatColor.DARK_RED + "HyperConomy ERROR #39", "hyperconomy.error");
			}
		} catch (Exception e) {
			String info = "Transaction removeItems() passed values id='" + id + "', data='" + data + "', amount='" + amount + "'";
			new HyperError(e, info);
		}
	}

	/**
	 * 
	 * 
	 * This function determines how much more of an item a player's inventory
	 * can hold.
	 * 
	 */
	public int getSpace(int id, int data, Inventory invent) {
		try {
			Calculation calc = hc.getCalculation();
			MaterialData md = new MaterialData(id, (byte) data);
			ItemStack stack = md.toItemStack();
			int maxstack = stack.getMaxStackSize();
			int invsize = invent.getSize();
			int availablespace = 0;
			int slot = 0;
			while (slot < invsize) {
				ItemStack citem = invent.getItem(slot);
				if (invent.getItem(slot) == null) {
					availablespace = availablespace + maxstack;
				} else if (citem != null && citem.getTypeId() == id && data == calc.getDamageValue(citem)) {
					availablespace = availablespace + (maxstack - citem.getAmount());
				}
				slot++;
			}
			return availablespace;
		} catch (Exception e) {
			String info = "Transaction getSpace() passed values id='" + id + "', data='" + data + "'";
			new HyperError(e, info);
			int availablespace = 0;
			return availablespace;
		}
	}

	/**
	 * 
	 * 
	 * This function adds purchased items to an inventory;
	 * 
	 */
	public void addItems(int id, int data, int amount, Inventory invent) {
		try {
			Calculation calc = hc.getCalculation();
			int ramount = amount;
			MaterialData md = new MaterialData(id, (byte) data);
			ItemStack stack = md.toItemStack();
			int maxstack = stack.getMaxStackSize();
			int slot = 0;
			while (ramount > 0) {
				int pamount = 0;
				ItemStack citem = invent.getItem(slot);
				if (citem != null && citem.getTypeId() == id && data == calc.getDamageValue(citem)) {
					int currentamount = citem.getAmount();
					if ((maxstack - currentamount) >= ramount) {
						pamount = ramount;
						citem.setAmount(pamount + currentamount);
					} else {
						pamount = maxstack - currentamount;
						citem.setAmount(maxstack);
					}
				} else if (invent.getItem(slot) == null) {
					ItemStack stack2;
					if (id == 373 && data != 0) {
						Potion pot = Potion.fromDamage(data);
						stack2 = pot.toItemStack(amount);
					} else {
						stack2 = md.toItemStack();
					}
					if (ramount > maxstack) {
						pamount = maxstack;
					} else {
						pamount = ramount;
					}
					stack2.setAmount(pamount);
					invent.setItem(slot, stack2);
				}
				ramount = ramount - pamount;
				slot++;
			}
		} catch (Exception e) {
			String info = "Transaction addItems() passed values id='" + id + "', data='" + data + "', amount='" + amount + "'";
			new HyperError(e, info);
		}
	}

	/**
	 * 
	 * 
	 * This function counts the number of the specified item in the specified
	 * inventory. It ignores durability.
	 * 
	 */
	public int countItems(int id, int data, Inventory invent) {
		try {	
			int totalitems = 0;
			Calculation calc = hc.getCalculation();
			ETransaction ench = hc.getETransaction();
			data = calc.newData(id, data);
			ItemStack[] stacks = invent.getContents();
			for (ItemStack stack:stacks) {
				if (stack != null && !ench.hasenchants(stack)) {
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
			String info = "Transaction countItems() passed values id='" + id + "', data='" + data + "'";
			new HyperError(e, info);
			return totalitems;
		}
	}
}
