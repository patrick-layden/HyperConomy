package regalowl.hyperconomy;


import org.bukkit.Bukkit;
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
			DataFunctions sf = hc.getDataFunctions();
			Calculation calc = hc.getCalculation();
			ETransaction ench = hc.getETransaction();
			LanguageFile L = hc.getLanguageFile();
			Account acc = hc.getAccount();
			Log log = hc.getLog();
			Notification not = hc.getNotify();
			InfoSignHandler isign = hc.getInfoSignHandler();
			String playerecon = sf.getHyperPlayer(p).getEconomy();
			if (amount > 0) {
				double shopstock = sf.getHyperObject(name, playerecon).getStock();
				if (shopstock >= amount) {
					if (id >= 0) {
						double price = calc.getCost(name, amount, playerecon);
						double taxpaid = calc.getPurchaseTax(name, playerecon, price);
						price = calc.twoDecimals(price + taxpaid);
						if (acc.checkFunds(price, p)) {
							int space = getavailableSpace(id, data, p);
							if (space >= amount) {
								addboughtItems(amount, id, data, p);
								if (!Boolean.parseBoolean(sf.getHyperObject(name, playerecon).getIsstatic()) || !hc.getConfig().getBoolean("config.unlimited-stock-for-static-items")) {
									sf.getHyperObject(name, playerecon).setStock(shopstock - amount);
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

								String type = "dynamic";
								if (Boolean.parseBoolean(sf.getHyperObject(name, playerecon).getInitiation())) {
									type = "initial";
								} else if (Boolean.parseBoolean(sf.getHyperObject(name, playerecon).getIsstatic())) {
									type = "static";
								}
								log.writeSQLLog(p.getName(), "purchase", name, (double) amount, calc.twoDecimals(price - taxpaid), calc.twoDecimals(taxpaid), playerecon, type);

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
			DataFunctions sf = hc.getDataFunctions();
			Calculation calc = hc.getCalculation();
			ETransaction ench = hc.getETransaction();
			Account acc = hc.getAccount();
			LanguageFile L = hc.getLanguageFile();
			Log log = hc.getLog();
			Notification not = hc.getNotify();
			InfoSignHandler isign = hc.getInfoSignHandler();
			String playerecon = sf.getHyperPlayer(p).getEconomy();
			if (amount > 0) {
				if (id >= 0) {
					int totalitems = countInvitems(id, data, p);
					if (totalitems < amount) {
						boolean sellRemaining = hc.getYaml().getConfig().getBoolean("config.sell-remaining-if-less-than-requested-amount");
						if (sellRemaining) {
							amount = totalitems;
						} else {
							p.sendMessage(L.f(L.get("YOU_DONT_HAVE_ENOUGH"), name));
							return;
						}
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
							isinitial = Boolean.parseBoolean(sf.getHyperObject(name, playerecon).getInitiation());
							isstatic = Boolean.parseBoolean(sf.getHyperObject(name, playerecon).getIsstatic());
							if ((amount > maxi) && !isstatic && isinitial) {
								amount = maxi;
								price = calc.getValue(name, amount, p);
							}
							boolean sunlimited = hc.getYaml().getConfig().getBoolean("config.shop-has-unlimited-money");
							if (acc.checkshopBalance(price) || sunlimited) {
								if (maxi == 0) {
									price = calc.getValue(name, amount, p);
								}
								removesoldItems(id, data, amount, p);
								double shopstock = 0;
								shopstock = sf.getHyperObject(name, playerecon).getStock();
								if (!Boolean.parseBoolean(sf.getHyperObject(name, playerecon).getIsstatic()) || !hc.getConfig().getBoolean("config.unlimited-stock-for-static-items")) {
									sf.getHyperObject(name, playerecon).setStock(shopstock + amount);
								}
								int maxi2 = getmaxInitial(name, p);
								if (maxi2 == 0) {
									sf.getHyperObject(name, playerecon).setInitiation("false");
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

								String type = "dynamic";
								if (Boolean.parseBoolean(sf.getHyperObject(name, playerecon).getInitiation())) {
									type = "initial";
								} else if (Boolean.parseBoolean(sf.getHyperObject(name, playerecon).getIsstatic())) {
									type = "static";
								}
								log.writeSQLLog(p.getName(), "sale", name, (double) amount, calc.twoDecimals(price - salestax), calc.twoDecimals(salestax), playerecon, type);
								isign.updateSigns();
								not.setNotify(hc, calc, ench, name, null, playerecon);
								not.sendNotification();
							} else {
								p.sendMessage(L.get("SHOP_NOT_ENOUGH_MONEY"));
							}
						} else {
							p.sendMessage(L.f(L.get("CURRENTLY_CANT_SELL_MORE_THAN"), sf.getHyperObject(name, playerecon).getStock(), name));
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
	private boolean removesoldItems(int id, int data, int amount, Player p) {
		try {
			int oamount = amount;
			Calculation calc = hc.getCalculation();
			ETransaction ench = hc.getETransaction();
			data = calc.newData(id, data);
			Inventory pinv = p.getInventory();
			ItemStack hstack = p.getItemInHand();
			if (hstack != null && !ench.hasenchants(hstack)) {
				int stackid = hstack.getTypeId();
				int stackdata = calc.getDamageValue(hstack);
				if (stackid == id && stackdata == data) {
					if (amount >= hstack.getAmount()) {
						amount -= hstack.getAmount();
						pinv.clear(p.getInventory().getHeldItemSlot());
					} else {
						hstack.setAmount(hstack.getAmount() - amount);
						return true;
					}
				}
			}
			for (int i = 0; i < pinv.getSize(); i++) {
				ItemStack stack = pinv.getItem(i);
				if (stack != null && !ench.hasenchants(stack)) {
					int stackid = stack.getTypeId();
					int stackdata = calc.getDamageValue(stack);
					if (stackid == id && stackdata == data) {
						if (amount >= stack.getAmount()) {
							amount -= stack.getAmount();
							pinv.clear(i);
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
			String info = "Transaction removeSoldItems() passed values player='" + p.getName() + "', id='" + id + "', data='" + data + "', amount='" + amount + "'";
			new HyperError(e, info);
			return false;
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
		DataFunctions sf = hc.getDataFunctions();
		String playerecon = sf.getHyperPlayer(p).getEconomy();
		try {
			int maxinitialitems = 0;
			HyperObject ho = sf.getHyperObject(name, playerecon);
			double shopstock = ho.getStock();
			double value = ho.getValue();
			double median = ho.getMedian();
			double icost = ho.getStartprice();
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
			DataFunctions sf = hc.getDataFunctions();
			Calculation calc = hc.getCalculation();
			ETransaction ench = hc.getETransaction();
			Account acc = hc.getAccount();
			LanguageFile L = hc.getLanguageFile();
			Log log = hc.getLog();
			Notification not = hc.getNotify();
			InfoSignHandler isign = hc.getInfoSignHandler();
			String playerecon = sf.getHyperPlayer(p).getEconomy();
			if (amount > 0) {
				int shopstock = 0;
				shopstock = (int) sf.getHyperObject(name, playerecon).getStock();
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
						if (!Boolean.parseBoolean(sf.getHyperObject(name, playerecon).getIsstatic()) || !hc.getConfig().getBoolean("config.unlimited-stock-for-static-items")) {
							sf.getHyperObject(name, playerecon).setStock(shopstock - amount);
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

						String type = "dynamic";
						if (Boolean.parseBoolean(sf.getHyperObject(name, playerecon).getInitiation())) {
							type = "initial";
						} else if (Boolean.parseBoolean(sf.getHyperObject(name, playerecon).getIsstatic())) {
							type = "static";
						}
						log.writeSQLLog(p.getName(), "purchase", name, (double) amount, calc.twoDecimals(price), calc.twoDecimals(taxpaid), playerecon, type);
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
			DataFunctions sf = hc.getDataFunctions();
			Calculation calc = hc.getCalculation();
			ETransaction ench = hc.getETransaction();
			Account acc = hc.getAccount();
			LanguageFile L = hc.getLanguageFile();
			Log log = hc.getLog();
			Notification not = hc.getNotify();
			InfoSignHandler isign = hc.getInfoSignHandler();
			String playerecon = sf.getHyperPlayer(p).getEconomy();
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
						itax = Boolean.parseBoolean(sf.getHyperObject(name, playerecon).getInitiation());
						stax = Boolean.parseBoolean(sf.getHyperObject(name, playerecon).getIsstatic());
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
							if (!Boolean.parseBoolean(sf.getHyperObject(name, playerecon).getIsstatic()) || !hc.getConfig().getBoolean("config.unlimited-stock-for-static-items")) {
								sf.getHyperObject(name, playerecon).setStock(amount + sf.getHyperObject(name, playerecon).getStock());
							}
							int maxi2 = getmaxInitial(name, p);
							if (maxi2 == 0) {
								sf.getHyperObject(name, playerecon).setInitiation("false");
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

							String type = "dynamic";
							if (Boolean.parseBoolean(sf.getHyperObject(name, playerecon).getInitiation())) {
								type = "initial";
							} else if (Boolean.parseBoolean(sf.getHyperObject(name, playerecon).getIsstatic())) {
								type = "static";
							}
							log.writeSQLLog(p.getName(), "sale", name, (double) amount, calc.twoDecimals(price - salestax), calc.twoDecimals(salestax), playerecon, type);

							isign.updateSigns();
							not.setNotify(hc, calc, ench, name, null, playerecon);
							not.sendNotification();
						} else {
							p.sendMessage(L.get("SHOP_NOT_ENOUGH_MONEY"));
						}
					} else {
						p.sendMessage(L.f(L.get("CURRENTLY_CANT_SELL_MORE_THAN"), sf.getHyperObject(name, playerecon).getStock(), name));
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
			DataFunctions sf = hc.getDataFunctions();
			Calculation calc = hc.getCalculation();
			Account acc = hc.getAccount();
			LanguageFile L = hc.getLanguageFile();
			Log log = hc.getLog();
			String playerecon = sf.getHyperPlayer(owner).getEconomy();
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

					log.writeSQLLog(p.getName(), "purchase", name, (double) amount, calc.twoDecimals(price), 0.0, owner, "chestshop");

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
			Account acc = hc.getAccount();
			Log log = hc.getLog();
			LanguageFile L = hc.getLanguageFile();
			Calculation calc = hc.getCalculation();
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

					log.writeSQLLog(p.getName(), "purchase", name, (double) amount, calc.twoDecimals(price), 0.0, owner, "chestshop");

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
			DataFunctions sf = hc.getDataFunctions();
			Calculation calc = hc.getCalculation();
			Account acc = hc.getAccount();
			Log log = hc.getLog();
			LanguageFile L = hc.getLanguageFile();
			String playerecon = sf.getHyperPlayer(owner).getEconomy();
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

				log.writeSQLLog(p.getName(), "sale", name, (double) amount, calc.twoDecimals(price), 0.0, owner, "chestshop");

				Player o = Bukkit.getPlayer(owner);
				if (o != null) {
					o.sendMessage(L.f(L.get("CHEST_SELL_NOTIFICATION"), amount, calc.twoDecimals(price), name, p));
				}
				return true;
			} else {
				p.sendMessage(L.f(L.get("CURRENTLY_CANT_SELL_MORE_THAN"), sf.getHyperObject(name, playerecon).getStock(), name));
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
			Account acc = hc.getAccount();
			Log log = hc.getLog();
			LanguageFile L = hc.getLanguageFile();
			Calculation calc = hc.getCalculation();
			removesoldItems(id, data, amount, p);
			addItems(id, data, amount, invent);
			acc.deposit(price, p);
			acc.withdrawAccount(price, owner);
			p.sendMessage(L.get("LINE_BREAK"));
			p.sendMessage(L.f(L.get("SELL_CHEST_MESSAGE"), amount, calc.twoDecimals(price), name, owner));
			p.sendMessage(L.get("LINE_BREAK"));
			World w = p.getWorld();
			w.playEffect(p.getLocation(), Effect.SMOKE, 4);
			log.writeSQLLog(p.getName(), "sale", name, (double) amount, calc.twoDecimals(price), 0.0, owner, "chestshop");
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
	public boolean removeItems(int id, int data, int amount, Inventory invent) {
		try {
			int oamount = amount;
			Calculation calc = hc.getCalculation();
			ETransaction ench = hc.getETransaction();
			data = calc.newData(id, data);
			for (int i = 0; i < invent.getSize(); i++) {
				ItemStack stack = invent.getItem(i);
				if (stack != null && !ench.hasenchants(stack)) {
					int stackid = stack.getTypeId();
					int stackdata = calc.getDamageValue(stack);
					if (stackid == id && stackdata == data) {
						if (amount >= stack.getAmount()) {
							amount -= stack.getAmount();
							invent.clear(i);
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
			String info = "Transaction removeSoldItems() passed values id='" + id + "', data='" + data + "', amount='" + amount + "'";
			new HyperError(e, info);
			return false;
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
