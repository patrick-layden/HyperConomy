package regalowl.hyperconomy;

import java.util.HashMap;
import java.util.logging.Logger;

import net.milkbowl.vault.economy.Economy;

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
		SQLFunctions sf = hc.getSQLFunctions();
		Calculation calc = hc.getCalculation();
		Economy economy = hc.getEconomy();
		ETransaction ench = hc.getETransaction();
		Account acc = hc.getAccount();
		Log log = hc.getLog();
		Notify not = hc.getNotify();
		InfoSign isign = hc.getInfoSign();
		String playerecon = sf.getPlayerEconomy(p.getName());
		// Handles buy function errors.
		try {
			// Makes sure that the player is buying at least 1 item.
			if (amount > 0) {
				// Makes sure that the shop has enough of the item.
				double shopstock = sf.getStock(name, playerecon);
				if (shopstock >= amount) {
					// Makes sure that its a real item. (Not experience.)
					if (id >= 0) {
						// Calculates the cost of the purchase for the player.
						double price = calc.getCost(name, amount, playerecon);
						double taxpaid = calc.getPurchaseTax(name, playerecon, price);
						price = calc.twoDecimals(price + taxpaid);
						// Makes sure that the player has enough money for the
						// transaction.
						acc.setAccount(hc, p, economy);
						if (acc.checkFunds(price)) {
							int space = getavailableSpace(id, data, p);
							if (space >= amount) {
								addboughtItems(amount, id, data, p);
								// Removes the number of items purchased from
								// the shop's stock and saves the
								// HyperConomy.yaml.
								if (!Boolean.parseBoolean(sf.getStatic(name, playerecon)) || !hc.getConfig().getBoolean("config.unlimited-stock-for-static-items")) {
									sf.setStock(name, playerecon, shopstock - amount);
								}
								
								// Withdraws the price of the transaction from
								// the player's account.
								acc.withdraw(price);
								// Deposits the money spent by the player into
								// the server account.
								acc.depositShop(price);
								// Reverts any changes to the global shop
								// account if the account is set to unlimited.
								if (hc.getYaml().getConfig().getBoolean("config.shop-has-unlimited-money")) {
									String globalaccount = hc.getYaml().getConfig().getString("config.global-shop-account");
									acc.setBalance(globalaccount, 0);
								}
								// Informs the player about their purchase
								// including how much was bought, how much was
								// spent, and how much was spent on taxes.
								p.sendMessage(ChatColor.BLACK + "-----------------------------------------------------");
								p.sendMessage(ChatColor.BLUE + "" + ChatColor.ITALIC + "You bought " + ChatColor.GREEN + "" + ChatColor.ITALIC + "" + amount + ChatColor.AQUA + "" + ChatColor.ITALIC + " " + name + " for " + ChatColor.GREEN + "" + ChatColor.ITALIC + hc.getYaml().getConfig().getString("config.currency-symbol") + price + ChatColor.BLUE + "" + ChatColor.ITALIC + " of which " + ChatColor.GREEN + "" + ChatColor.ITALIC + hc.getYaml().getConfig().getString("config.currency-symbol")
										+ taxpaid + ChatColor.BLUE + ChatColor.ITALIC + " was tax!");
								p.sendMessage(ChatColor.BLACK + "-----------------------------------------------------");
								// This writes a log entry for the transaction
								// in the HyperConomy log.txt file.
								if (hc.useSQL()) {
									String type = "dynamic";
									if (Boolean.parseBoolean(sf.getInitiation(name, playerecon))) {
										type = "initial";
									} else if (Boolean.parseBoolean(sf.getStatic(name, playerecon))) {
										type = "static";
									}
									log.writeSQLLog(p.getName(), "purchase", name, (double) amount, price - taxpaid, taxpaid, playerecon, type);
								} else {
									String logentry = p.getName() + " bought " + amount + " " + name + " for " + hc.getYaml().getConfig().getString("config.currency-symbol") + price + ". [Static Price=" + sf.getStatic(name, playerecon) + "][Initial Price=" + sf.getInitiation(name, playerecon) + "]";
									log.setEntry(logentry);
									log.writeBuffer();
								}
								// Updates all information signs.
								isign.setrequestsignUpdate(true);
								isign.checksignUpdate();
								// Sends price update notifications.
								not.setNotify(hc, calc, ench, name, null, playerecon);
								not.sendNotification();
								// This informs the player of how many of an
								// item they can buy if they don't have enough
								// space for the requested amount.
							} else {
								p.sendMessage(ChatColor.BLUE + "You only have room to buy " + space + " " + name + "!");
							}
							// This informs the player that they don't have
							// enough money for a transaction.
						} else {
							p.sendMessage(ChatColor.BLUE + "Insufficient Funds!");
						}
						// Informs the player if it's not a real item.
					} else {
						p.sendMessage(ChatColor.BLUE + "Sorry, " + name + " cannot be purchased with this command.");
					}
					// This informs the player that the shop doesn't have enough
					// of the chosen item.
				} else {
					p.sendMessage(ChatColor.BLUE + "The shop doesn't have enough " + name + "!");
				}
				// This informs the player that they can't try to buy negative
				// values or 0 of an item.
			} else {
				p.sendMessage(ChatColor.BLUE + "You can't buy less than 1 " + name + "!");
			}
			// Reports the error to the player making the transaction and prints
			// the stack trace for analysis.
		} catch (Exception e) {
			e.printStackTrace();
			Logger l = Logger.getLogger("Minecraft");
			l.info("HyperConomy ERROR #2");
			Bukkit.broadcast(ChatColor.DARK_RED + "HyperConomy ERROR #2", "hyperconomy.error");
		}
	}

	/**
	 * 
	 * 
	 * This function handles the sale of items.
	 * 
	 */
	public void sell(String name, int id, int data, int amount, Player p) {
		SQLFunctions sf = hc.getSQLFunctions();
		Calculation calc = hc.getCalculation();
		Economy economy = hc.getEconomy();
		ETransaction ench = hc.getETransaction();
		Account acc = hc.getAccount();
		Log log = hc.getLog();
		Notify not = hc.getNotify();
		InfoSign isign = hc.getInfoSign();
		String playerecon = sf.getPlayerEconomy(p.getName());
		// Handles sell function errors.
		try {
			// Makes sure that they aren't selling negative items, or 0.
			if (amount > 0) {
				// Makes sure that its a real item. (Not experience.)
				if (id >= 0) {
					// Counts the number of the chosen item that the player has
					// in their inventory and makes sure that they have enough
					// of the item for the transaction.
					int totalitems = countInvitems(id, data, p);
					// If someone tries to sell more than they have, it will
					// just sell all that they have.
					if (totalitems < amount) {
						amount = totalitems;
					}
					if (amount > 0) {
						// Determines the sale value of the items being sold.
						double price = calc.getValue(name, amount, p);
						// If the cost is greater than 10^10 the getValue
						// calculation sets the value to this arbitrary number
						// to indicate this. This makes sure no many infinite
						// values exist
						// and limits the number of items that can be sold at a
						// time.
						Boolean toomuch = false;
						if (price == 3235624645000.7) {
							toomuch = true;
						}
						// Makes sure that the value of the items is not
						// extremely high or infinite.
						if (!toomuch) {
							// Calculates how many items can be sold before
							// hitting the hyperbolic curve so as to not allow
							// it to be bypassed temporarily.
							int maxi = getmaxInitial(name, p);
							// EXPERIMENTAL Sets the amount to the transition
							// point between Dynamic and Initial pricing if the
							// requested amount is greater than the transition
							// point.
							boolean isstatic = false;
							boolean isinitial = false;
							isinitial = Boolean.parseBoolean(sf.getInitiation(name, playerecon));
							isstatic = Boolean.parseBoolean(sf.getStatic(name, playerecon));
							if ((amount > maxi) && !isstatic && isinitial) {
								amount = maxi;
								price = calc.getValue(name, amount, p);
							}
							boolean sunlimited = hc.getYaml().getConfig().getBoolean("config.shop-has-unlimited-money");
							// Makes sure the global shop has enough money for
							// the transaction.
							if (acc.checkshopBalance(price) || sunlimited) {
								// This recalculates the value of the sale if
								// the getValue function sets the initiation
								// period to false (Not sure if this is
								// necessary).
								if (maxi == 0) {
									price = calc.getValue(name, amount, p);
								}
								// Removes the sold items from the player's
								// inventory.
								removesoldItems(id, data, amount, p);
								// Adds the sold items to the shopstock and
								// saves the yaml file.
								double shopstock = 0;
								shopstock = sf.getStock(name, playerecon);
								if (!Boolean.parseBoolean(sf.getStatic(name, playerecon)) || !hc.getConfig().getBoolean("config.unlimited-stock-for-static-items")) {
									sf.setStock(name, playerecon, (shopstock + amount));
								}
								// Sets the initiation period to false if the
								// item has reached the hyperbolic pricing curve
								// after the transaction is complete so that the
								// value is calculated correctly for future
								// value inquiries.
								int maxi2 = getmaxInitial(name, p);
								if (maxi2 == 0) {
									sf.setInitiation(name, playerecon, "false");
								}
								double salestax = calc.getSalesTax(p, price);
								// Deposits money in players account and tells
								// them of transaction success and details about
								// the transaction.
								acc.setAccount(hc, p, economy);
								acc.deposit(price - salestax);
								// Withdraws money from the global shop's
								// account.
								acc.withdrawShop(price - salestax);
								// Reverts any changes to the global shop
								// account if the account is set to unlimited.
								if (sunlimited) {
									String globalaccount = hc.getYaml().getConfig().getString("config.global-shop-account");
									acc.setBalance(globalaccount, 0);
								}
								p.sendMessage(ChatColor.BLACK + "-----------------------------------------------------");
								p.sendMessage(ChatColor.BLUE + "" + ChatColor.ITALIC + "You sold " + ChatColor.GREEN + "" + "" + ChatColor.ITALIC + amount + ChatColor.AQUA + "" + ChatColor.ITALIC + " " + name + " for " + ChatColor.GREEN + "" + ChatColor.ITALIC + hc.getYaml().getConfig().getString("config.currency-symbol") + price + ChatColor.BLUE + "" + ChatColor.ITALIC + " of which " + ChatColor.GREEN + "" + ChatColor.ITALIC + hc.getYaml().getConfig().getString("config.currency-symbol")
										+ calc.twoDecimals(salestax) + ChatColor.BLUE + ChatColor.ITALIC + " went to tax!");
								// p.sendMessage(ChatColor.BLUE + "" +
								// "You sold " + ChatColor.GREEN + "" + "" +
								// amount + ChatColor.AQUA + "" + " " + name +
								// " for " + ChatColor.GREEN + "" +
								// hc.getYaml().getConfig().getString("config.currency-symbol")
								// + price + ChatColor.BLUE + "" + "!");
								p.sendMessage(ChatColor.BLACK + "-----------------------------------------------------");
								// Plays smoke effects. (Pointless but funny.)
								World w = p.getWorld();
								w.playEffect(p.getLocation(), Effect.SMOKE, 4);
								// Writes a log entry in the HyperConomy log.txt
								// file.
								String logentry = "";
								if (hc.useSQL()) {
									String type = "dynamic";
									if (Boolean.parseBoolean(sf.getInitiation(name, playerecon))) {
										type = "initial";
									} else if (Boolean.parseBoolean(sf.getStatic(name, playerecon))) {
										type = "static";
									}
									log.writeSQLLog(p.getName(), "sale", name, (double) amount, price - salestax, salestax, playerecon, type);
								} else {
									logentry = p.getName() + " sold " + amount + " " + name + " for " + hc.getYaml().getConfig().getString("config.currency-symbol") + price + ". [Static Price=" + sf.getStatic(name, playerecon) + "][Initial Price=" + sf.getInitiation(name, playerecon) + "]";
									log.setEntry(logentry);
									log.writeBuffer();
								}
								// Updates all information signs.
								isign.setrequestsignUpdate(true);
								isign.checksignUpdate();
								// Sends price update notifications.
								not.setNotify(hc, calc, ench, name, null, playerecon);
								not.sendNotification();
								// Informs the player if the shop doesn't have
								// enough money.
							} else {
								p.sendMessage(ChatColor.BLUE + "Sorry, the shop currently does not have enough money.");
							}
							// Informs the player if they're trying to sell too
							// many items at once, resulting in an infinite
							// value.
						} else {
							p.sendMessage(ChatColor.BLUE + "Currently, you can't sell more than " + sf.getStock(name, playerecon) + " " + name + "!");
						}
						// Informs the player that they don't have enough of the
						// item to complete the transaction.
					} else {
						p.sendMessage(ChatColor.BLUE + "You don't have enough " + name + "!");
					}
					// Informs the player if it's not a real item.
				} else {
					p.sendMessage(ChatColor.BLUE + "Sorry, " + name + " cannot be sold with this command.");
				}
				// Informs the player that they can't sell negative or 0 items.
			} else {
				p.sendMessage(ChatColor.BLUE + "You can't sell less than 1 " + name + "!");
			}
			// Reports the error to the player making the transaction and prints
			// the stack trace for analysis.
		} catch (Exception e) {
			e.printStackTrace();
			Logger l = Logger.getLogger("Minecraft");
			l.info("HyperConomy ERROR #3");
			Bukkit.broadcast(ChatColor.DARK_RED + "HyperConomy ERROR #3", "hyperconomy.error");
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
		Calculation calc = hc.getCalculation();
		ETransaction ench = hc.getETransaction();
		// Handles errors for the function.
		try {
			// Gets the player's inventory
			Inventory pinv = p.getInventory();
			// Puts all of the specified item in a HashMap containing the item
			// slot and the ItemStack.
			HashMap<Integer, ? extends ItemStack> stacks1 = pinv.all(id);
			// Converts any item susceptible to damage's damage value back to 0.
			int newdata = calc.newData(id, data);
			int slot = 0;
			int totalitems = 0;
			if (p.getInventory().contains(id)) {
				String allstacks = "{" + stacks1.toString();
				while (allstacks.contains(" x ")) {
					int a = allstacks.indexOf(" x ") + 3;
					int b = allstacks.indexOf("}", a);
					slot = Integer.parseInt(allstacks.substring(2, allstacks.indexOf("=")));
					// Gets the correct damage value if the item in the player's
					// hand is a potion.
					int da = calc.getpotionDV(stacks1.get(slot));
					// Checks whether or not the item in the player's hand has
					// enchantments.
					boolean hasenchants = ench.hasenchants(stacks1.get(slot));
					// Extracts the number of items from the HashMap data pair
					// by converting it to a string and removing the excess
					// parts.
					if (stacks1.get(slot) != null && calc.newData(id, da) == newdata && hasenchants == false) {
						int num = Integer.parseInt(allstacks.substring(a, b));
						totalitems = num + totalitems;
					}
					allstacks = allstacks.substring(b + 1, allstacks.length());
				}
			}
			return totalitems;
			// Reports the error to the player making the transaction and prints
			// the stack trace for analysis.
		} catch (Exception e) {
			int totalitems = 0;
			e.printStackTrace();
			Logger log = Logger.getLogger("Minecraft");
			log.info("HyperConomy ERROR #4");
			Bukkit.broadcast(ChatColor.DARK_RED + "HyperConomy ERROR #4", "hyperconomy.error");
			return totalitems;
		}
	}

	/**
	 * 
	 * 
	 * This function determines how many empty inventory slots a player has.
	 * 
	 */
	/*
	 * public int getavailableSlots(){
	 * 
	 * try { int availablespace = 0; int slot = 0; while (slot < 36) { if
	 * (p.getInventory().getItem(slot) == null) { availablespace++; } slot++; }
	 * return availablespace; } catch (Exception e) { e.printStackTrace();
	 * Logger log = Logger.getLogger("Minecraft");
	 * log.info("HyperConomy ERROR #6"); Bukkit.broadcast(ChatColor.DARK_RED +
	 * "HyperConomy ERROR #6", "hyperconomy.error"); int availablespace = 0;
	 * return availablespace; } }
	 */
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
				} else if (citem != null && citem.getTypeId() == id && idata == calc.getdamageValue(citem)) {
					availablespace = availablespace + (maxstack - citem.getAmount());
				}
				slot++;
			}
			return availablespace;
		} catch (Exception e) {
			e.printStackTrace();
			Logger log = Logger.getLogger("Minecraft");
			log.info("HyperConomy ERROR #33");
			Bukkit.broadcast(ChatColor.DARK_RED + "HyperConomy ERROR #33", "hyperconomy.error");
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
				} else if (citem != null && citem.getTypeId() == id && idata == calc.getdamageValue(citem)) {
					availablespace = availablespace + (maxstack - citem.getAmount());
				}
				slot++;
			}
			return availablespace;
		} catch (Exception e) {
			e.printStackTrace();
			Logger log = Logger.getLogger("Minecraft");
			log.info("HyperConomy ERROR #33");
			Bukkit.broadcast(ChatColor.DARK_RED + "HyperConomy ERROR #33", "hyperconomy.error");
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
			// ramount holds the number of items remaining to be placed in the
			// player's inventory.
			int ramount = amount;
			MaterialData md = new MaterialData(id, (byte) data);
			ItemStack stack = md.toItemStack();
			int maxstack = stack.getMaxStackSize();
			// While the player still has items to be placed into their
			// inventory this places the max stack size of that item into
			// the first available slot until there are no remaining items to be
			// placed.
			int slot = 0;
			while (ramount > 0) {
				// Stores how many items are being placed into the current slot.
				int pamount = 0;
				// Handles slots that already have some of the same item in it.
				ItemStack citem = p.getInventory().getItem(slot);
				if (citem != null && citem.getTypeId() == id && data == calc.getdamageValue(citem)) {
					int currentamount = citem.getAmount();
					if ((maxstack - currentamount) >= ramount) {
						pamount = ramount;
						citem.setAmount(pamount + currentamount);
					} else {
						pamount = maxstack - currentamount;
						citem.setAmount(maxstack);
					}
					// Handles empty slots.
				} else if (p.getInventory().getItem(slot) == null) {
					// Checks if the item is a potion and not a water bottle and
					// then creates an ItemStack of that potion.
					ItemStack stack2;
					if (id == 373 && data != 0) {
						Potion pot = Potion.fromDamage(data);
						stack2 = pot.toItemStack(amount);
					} else {
						stack2 = md.toItemStack();
					}
					// Checks if the items remaining to be put in a player's
					// inventory exceed the maximum stack size. If they do it
					// sets pamount (the amount to be placed into
					// the player's inventory to the max amount, otherwise it
					// sets pamount to the remaining number of items.
					if (ramount > maxstack) {
						pamount = maxstack;
					} else {
						pamount = ramount;
					}
					// Places the determined amount in the first empty inventory
					// slot that a player has and then subtracts the amount of
					// items placed in their inventory from
					// the remaining items.
					stack2.setAmount(pamount);
					p.getInventory().setItem(slot, stack2);
				}
				ramount = ramount - pamount;
				slot++;
			}
			// needed for transaction signs. It creates ghost items otherwise
			// for some reason.
			p.updateInventory();
		} catch (Exception e) {
			e.printStackTrace();
			Logger log = Logger.getLogger("Minecraft");
			log.info("HyperConomy ERROR #32");
			Bukkit.broadcast(ChatColor.DARK_RED + "HyperConomy ERROR #32", "hyperconomy.error");
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
	private void removesoldItems(int id, int data, int amount, Player p) {
		Calculation calc = hc.getCalculation();
		ETransaction ench = hc.getETransaction();
		// Handles errors for the function.
		try {
			// Converts any item susceptible to damage's damage value back to 0.
			int newdata = calc.newData(id, data);
			// HashMap stacks1 stores all slot ids and ItemStacks of a given id.
			Inventory pinv = p.getInventory();
			HashMap<Integer, ? extends ItemStack> stacks1 = pinv.all(id);
			// Makes sure that the player's inventory actually contains the item
			// being sold. (This should have already been verified.)
			if (p.getInventory().contains(id)) {
				// Searches for the first slot containing the ItemStack and then
				// determines the maximum stacksize for the item from that
				// ItemStack.
				String stringstacks = stacks1.toString();
				int maxstack = stacks1.get(Integer.parseInt(stringstacks.substring(1, stringstacks.indexOf("=")))).getMaxStackSize();
				/*
				 * This section prioritizes the item in the player's hand before
				 * moving on to other inventory slots.
				 */
				// Gets the correct damage value if the item in the player's
				// hand is a potion.
				ItemStack inhand = p.getItemInHand();
				int dv = calc.getpotionDV(inhand);
				// Checks whether or not the item in the player's hand has
				// enchantments.
				boolean hasenchants = ench.hasenchants(inhand);
				// Makes sure the item in the player's hand hs the same data
				// value and id as the item being sold and also makes sure it is
				// not enchanted. Checks for nulls just in case.
				// If the itemstack in the players hand is larger than the
				// amount being sold, it only removes part of it, otherwise it
				// deletes the entire stack.
				int ritems = amount;
				if (inhand != null && calc.newData(id, dv) == newdata && inhand.getTypeId() == id && hasenchants == false) {
					int amountinhand = inhand.getAmount();
					if (amountinhand > ritems) {
						inhand.setAmount(amountinhand - ritems);
						ritems = 0;
					} else if (amountinhand <= ritems) {
						Inventory invent = p.getInventory();
						int heldslot = p.getInventory().getHeldItemSlot();
						invent.clear(heldslot);
						ritems = ritems - amountinhand;
					}
				}
				// Makes sure the remaining items in the player's inventory have
				// the same data value and id as the item being sold and also
				// makes sure the items are not enchanted.
				// If an ItemStack is larger than the amount being sold, it only
				// removes part of it, otherwise it deletes the entire stack.
				int slot;
				String allstacks = "{" + stringstacks;
				while (allstacks.contains(" x ")) {
					int a = allstacks.indexOf(" x ") + 3;
					int b = allstacks.indexOf("}", a);
					slot = Integer.parseInt(allstacks.substring(2, allstacks.indexOf("=")));
					// Gets the correct damage value if the item is a potion.
					int damv = calc.getpotionDV(pinv.getItem(slot));
					// Checks whether or not the item has enchantments.
					boolean hasenchants2 = ench.hasenchants(stacks1.get(slot));
					if (pinv.getItem(slot) != null && calc.newData(id, damv) == newdata && pinv.getItem(slot).getTypeId() == id && hasenchants2 == false) {
						if (ritems > 0) {
							if (ritems >= maxstack && pinv.getItem(slot).getAmount() == maxstack) {
								pinv.clear(slot);
								ritems = ritems - maxstack;
							} else {
								int stackamount = pinv.getItem(slot).getAmount();
								if (stackamount <= ritems) {
									pinv.clear(slot);
									ritems = ritems - stackamount;
								} else {
									pinv.getItem(slot).setAmount(stackamount - ritems);
									ritems = 0;
								}
							}
						}
					}
					allstacks = allstacks.substring(b + 1, allstacks.length());
				}
				// Sends an error message. If seen this indicates the sell
				// function isn't working properly, as the players inventory
				// doesn't actually contain the item being sold.
			} else {
				Logger log = Logger.getLogger("Minecraft");
				log.info("HyperConomy ERROR #1");
				Bukkit.broadcast(ChatColor.DARK_RED + "HyperConomy ERROR #1", "hyperconomy.error");
			}
			// Reports errors in the removesoldItems function and reports them
			// to the player and prints the stack trace.
		} catch (Exception e) {
			e.printStackTrace();
			Logger log = Logger.getLogger("Minecraft");
			log.info("HyperConomy ERROR #5");
			Bukkit.broadcast(ChatColor.DARK_RED + "HyperConomy ERROR #5", "hyperconomy.error");
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
		SQLFunctions sf = hc.getSQLFunctions();
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
			e.printStackTrace();
			Logger log = Logger.getLogger("Minecraft");
			log.info("HyperConomy ERROR #7");
			Bukkit.broadcast(ChatColor.DARK_RED + "HyperConomy ERROR #7", "hyperconomy.error");
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
		SQLFunctions sf = hc.getSQLFunctions();
		Calculation calc = hc.getCalculation();
		Economy economy = hc.getEconomy();
		ETransaction ench = hc.getETransaction();
		Account acc = hc.getAccount();
		Log log = hc.getLog();
		Notify not = hc.getNotify();
		InfoSign isign = hc.getInfoSign();
		String playerecon = sf.getPlayerEconomy(p.getName());
		// Handles buy function errors.
		try {
			// Makes sure that the player is buying at least 1 item.
			if (amount > 0) {
				// Makes sure that the shop has enough of the item.
				int shopstock = 0;
				shopstock = (int) sf.getStock(name, playerecon);
				if (shopstock >= amount) {
					// Calculates the cost of the purchase for the player.
					double price = calc.getCost(name, amount, playerecon);
					double taxpaid = calc.getPurchaseTax(name, playerecon, price);
					price = calc.twoDecimals(price + taxpaid);
					// Makes sure that the player has enough money for the
					// transaction.
					acc.setAccount(hc, p, economy);
					if (acc.checkFunds(price)) {
						// Calculates and sets the new level and experience bar.
						int totalxp = calc.gettotalxpPoints(p);
						int newxp = totalxp + amount;
						int newlvl = calc.getlvlfromXP(newxp);
						newxp = newxp - calc.getlvlxpPoints(newlvl);
						float xpbarxp = (float) newxp / (float) calc.getxpfornextLvl(newlvl);
						p.setLevel(newlvl);
						p.setExp(xpbarxp);
						// Removes the number of items purchased from the shop's
						// stock and saves the HyperConomy.yaml.
						if (!Boolean.parseBoolean(sf.getStatic(name, playerecon)) || !hc.getConfig().getBoolean("config.unlimited-stock-for-static-items")) {
							sf.setStock(name, playerecon, (shopstock - amount));
						}
						// Withdraws the price of the transaction from the
						// player's account.
						acc.withdraw(price);
						// Deposits the money spent by the player into the
						// server account.
						acc.depositShop(price);
						// Reverts any changes to the global shop account if the
						// account is set to unlimited.
						if (hc.getYaml().getConfig().getBoolean("config.shop-has-unlimited-money")) {
							String globalaccount = hc.getYaml().getConfig().getString("config.global-shop-account");
							acc.setBalance(globalaccount, 0);
						}
						// Informs the player about their purchase including how
						// much was bought, how much was spent, and how much was
						// spent on taxes.
						p.sendMessage(ChatColor.BLACK + "-----------------------------------------------------");
						p.sendMessage(ChatColor.BLUE + "" + ChatColor.ITALIC + "You bought " + ChatColor.GREEN + "" + ChatColor.ITALIC + "" + amount + ChatColor.AQUA + "" + ChatColor.ITALIC + " " + name + " for " + ChatColor.GREEN + "" + ChatColor.ITALIC + hc.getYaml().getConfig().getString("config.currency-symbol") + price + ChatColor.BLUE + "" + ChatColor.ITALIC + " and paid " + ChatColor.GREEN + "" + ChatColor.ITALIC + hc.getYaml().getConfig().getString("config.currency-symbol") + taxpaid
								+ " in taxes!");
						// p.sendMessage(ChatColor.BLUE + "" + "You bought " +
						// ChatColor.GREEN + "" + "" + amount + ChatColor.AQUA +
						// "" + " " + name + " for " + ChatColor.GREEN + "" +
						// hc.getYaml().getConfig().getString("config.currency-symbol")
						// + price + ChatColor.BLUE + "" + " and paid " +
						// ChatColor.GREEN + "" +
						// hc.getYaml().getConfig().getString("config.currency-symbol")
						// + taxpaid + " in taxes!" );
						p.sendMessage(ChatColor.BLACK + "-----------------------------------------------------");
						// This writes a log entry for the transaction in the
						// HyperConomy log.txt file.
						if (hc.useSQL()) {
							String type = "dynamic";
							if (Boolean.parseBoolean(sf.getInitiation(name, playerecon))) {
								type = "initial";
							} else if (Boolean.parseBoolean(sf.getStatic(name, playerecon))) {
								type = "static";
							}
							log.writeSQLLog(p.getName(), "purchase", name, (double) amount, price, taxpaid, playerecon, type);
						} else {
							String logentry = p.getName() + " bought " + amount + " " + name + " for " + hc.getYaml().getConfig().getString("config.currency-symbol") + price + ". [Static Price=" + sf.getStatic(name, playerecon) + "][Initial Price=" + sf.getInitiation(name, playerecon) + "]";
							log.setEntry(logentry);
							log.writeBuffer();
						}
						// Updates all information signs.
						isign.setrequestsignUpdate(true);
						isign.checksignUpdate();
						// Sends price update notifications.
						not.setNotify(hc, calc, ench, name, null, playerecon);
						not.sendNotification();
						// This informs the player that they don't have enough
						// money for a transaction.
					} else {
						p.sendMessage(ChatColor.BLUE + "Insufficient Funds!");
					}
					// This informs the player that the shop doesn't have enough
					// of the chosen item.
				} else {
					p.sendMessage(ChatColor.BLUE + "The shop doesn't have enough " + name + "!");
				}
				// This informs the player that they can't try to buy negative
				// values or 0 of an item.
			} else {
				p.sendMessage(ChatColor.BLUE + "You can't buy less than 1 " + name + "!");
			}
			// Reports the error to the player making the transaction and prints
			// the stack trace for analysis.
		} catch (Exception e) {
			e.printStackTrace();
			Logger l = Logger.getLogger("Minecraft");
			l.info("HyperConomy ERROR #42");
			Bukkit.broadcast(ChatColor.DARK_RED + "HyperConomy ERROR #42", "hyperconomy.error");
		}
	}

	/**
	 * 
	 * 
	 * This function handles the sale of experience.
	 * 
	 */
	public void sellXP(String name, int amount, Player p) {
		SQLFunctions sf = hc.getSQLFunctions();
		Calculation calc = hc.getCalculation();
		Economy economy = hc.getEconomy();
		ETransaction ench = hc.getETransaction();
		Account acc = hc.getAccount();
		Log log = hc.getLog();
		Notify not = hc.getNotify();
		InfoSign isign = hc.getInfoSign();
		String playerecon = sf.getPlayerEconomy(p.getName());
		// Handles sell function errors.
		try {
			// Makes sure that they aren't selling negative items, or 0.
			if (amount > 0) {
				// Counts the number of the chosen item that the player has in
				// their inventory and makes sure that they have enough of the
				// item for the transaction.
				int totalxp = calc.gettotalxpPoints(p);
				if (totalxp >= amount) {
					// Determines the sale value of the items being sold.
					double price = calc.getValue(name, amount, p);
					// If the cost is greater than 10^10 the getValue
					// calculation sets the value to this arbitrary number to
					// indicate this. This makes sure no infinite values exist
					// and limits the number of items that can be sold at a
					// time.
					Boolean toomuch = false;
					if (price == 3235624645000.7) {
						toomuch = true;
					}
					// Makes sure that the value of the items is not extremely
					// high or infinite.
					if (!toomuch) {
						// Calculates how many items can be sold before hitting
						// the hyperbolic curve so as to not allow it to be
						// bypassed temporarily.
						int maxi = getmaxInitial(name, p);
						// EXPERIMENTAL Sets the amount to the transition point
						// between Dynamic and Initial pricing if the requested
						// amount is greater than the transition point.
						boolean itax;
						boolean stax;
						itax = Boolean.parseBoolean(sf.getInitiation(name, playerecon));
						stax = Boolean.parseBoolean(sf.getStatic(name, playerecon));
						if (amount > (maxi) && !stax && itax) {
							amount = maxi;
							price = calc.getValue(name, amount, p);
						}
						boolean sunlimited = hc.getYaml().getConfig().getBoolean("config.shop-has-unlimited-money");
						// Makes sure the global shop has enough money for the
						// transaction.
						if (acc.checkshopBalance(price) || sunlimited) {
							// This recalculates the value of the sale if the
							// getValue function sets the initiation period to
							// false (Not sure if this is necessary).
							if (maxi == 0) {
								price = calc.getValue(name, amount, p);
							}
							// Calculates and sets the new level and experience
							// bar.
							int newxp = totalxp - amount;
							int newlvl = calc.getlvlfromXP(newxp);
							newxp = newxp - calc.getlvlxpPoints(newlvl);
							float xpbarxp = (float) newxp / (float) calc.getxpfornextLvl(newlvl);
							p.setLevel(newlvl);
							p.setExp(xpbarxp);
							// Adds the sold items to the shopstock and saves
							// the yaml file.
							if (!Boolean.parseBoolean(sf.getStatic(name, playerecon)) || !hc.getConfig().getBoolean("config.unlimited-stock-for-static-items")) {
								sf.setStock(name, playerecon, amount + sf.getStock(name, playerecon));
							}
							// Sets the initiation period to false if the item
							// has reached the hyperbolic pricing curve
							// after the transaction is complete so that the
							// value is calculated correctly for future value
							// inquiries.
							int maxi2 = getmaxInitial(name, p);
							if (maxi2 == 0) {
								sf.setInitiation(name, playerecon, "false");
							}
							double salestax = calc.getSalesTax(p, price);
							// Deposits money in players account and tells them
							// of transaction success and details about the
							// transaction.
							acc.setAccount(hc, p, economy);
							acc.deposit(price - salestax);
							// Withdraws money from the global shop's account.
							acc.withdrawShop(price - salestax);
							// Reverts any changes to the global shop account if
							// the account is set to unlimited.
							if (sunlimited) {
								String globalaccount = hc.getYaml().getConfig().getString("config.global-shop-account");
								acc.setBalance(globalaccount, 0);
							}
							p.sendMessage(ChatColor.BLACK + "-----------------------------------------------------");
							p.sendMessage(ChatColor.BLUE + "" + ChatColor.ITALIC + "You sold " + ChatColor.GREEN + "" + "" + ChatColor.ITALIC + amount + ChatColor.AQUA + "" + ChatColor.ITALIC + " " + name + " for " + ChatColor.GREEN + "" + ChatColor.ITALIC + hc.getYaml().getConfig().getString("config.currency-symbol") + price + ChatColor.BLUE + "" + ChatColor.ITALIC + " of which " + ChatColor.GREEN + "" + ChatColor.ITALIC + hc.getYaml().getConfig().getString("config.currency-symbol")
									+ calc.twoDecimals(salestax) + ChatColor.BLUE + ChatColor.ITALIC + " went to tax!");
							// p.sendMessage(ChatColor.BLUE + "" + "You sold " +
							// ChatColor.GREEN + "" + "" + amount +
							// ChatColor.AQUA + "" + " " + name + " for " +
							// ChatColor.GREEN + "" +
							// hc.getYaml().getConfig().getString("config.currency-symbol")
							// + price + ChatColor.BLUE + "" + "!");
							p.sendMessage(ChatColor.BLACK + "-----------------------------------------------------");
							// Plays smoke effects. (Pointless but funny.)
							World w = p.getWorld();
							w.playEffect(p.getLocation(), Effect.SMOKE, 4);
							// Writes a log entry in the HyperConomy log.txt
							// file.
							if (hc.useSQL()) {
								String type = "dynamic";
								if (Boolean.parseBoolean(sf.getInitiation(name, playerecon))) {
									type = "initial";
								} else if (Boolean.parseBoolean(sf.getStatic(name, playerecon))) {
									type = "static";
								}
								log.writeSQLLog(p.getName(), "sale", name, (double) amount, price - salestax, salestax, playerecon, type);
							} else {
								String logentry = p.getName() + " sold " + amount + " " + name + " for " + hc.getYaml().getConfig().getString("config.currency-symbol") + price + ". [Static Price=" + sf.getStatic(name, playerecon) + "][Initial Price=" + sf.getInitiation(name, playerecon) + "]";
								log.setEntry(logentry);
								log.writeBuffer();
							}
							// Updates all information signs.
							isign.setrequestsignUpdate(true);
							isign.checksignUpdate();
							// Sends price update notifications.
							not.setNotify(hc, calc, ench, name, null, playerecon);
							not.sendNotification();
							// Informs the player if the shop doesn't have
							// enough money.
						} else {
							p.sendMessage(ChatColor.BLUE + "Sorry, the shop currently does not have enough money.");
						}
						// Informs the player if they're trying to sell too many
						// items at once, resulting in an infinite value.
					} else {
						p.sendMessage(ChatColor.BLUE + "Currently, you can't sell more than " + sf.getStock(name, playerecon) + " " + name + "!");
					}
					// Informs the player that they don't have enough of the
					// item to complete the transaction.
				} else {
					p.sendMessage(ChatColor.BLUE + "You don't have enough " + name + "!");
				}
				// Informs the player that they can't sell negative or 0 items.
			} else {
				p.sendMessage(ChatColor.BLUE + "You can't sell less than 1 " + name + "!");
			}
			// Reports the error to the player making the transaction and prints
			// the stack trace for analysis.
		} catch (Exception e) {
			e.printStackTrace();
			Logger l = Logger.getLogger("Minecraft");
			l.info("HyperConomy ERROR #41");
			Bukkit.broadcast(ChatColor.DARK_RED + "HyperConomy ERROR #41", "hyperconomy.error");
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
		// Handles buy function errors.
		try {
			SQLFunctions sf = hc.getSQLFunctions();
			Calculation calc = hc.getCalculation();
			Economy economy = hc.getEconomy();
			Account acc = hc.getAccount();
			Log log = hc.getLog();
			String playerecon = sf.getPlayerEconomy(owner);
			// Calculates the cost of the purchase for the player.
			double price = calc.getTvalue(name, amount, playerecon);
			// Makes sure that the player has enough money for the transaction.
			acc.setAccount(hc, p, economy);
			if (acc.checkFunds(price)) {
				int space = getavailableSpace(id, data, p);
				if (space >= amount) {
					addboughtItems(amount, id, data, p);
					removeItems(id, data, amount, invent);
					// Withdraws the price of the transaction from the player's
					// account.
					acc.withdraw(price);
					// Deposits the money spent by the player into the chest
					// owner's account.
					acc.setAccount(hc, Bukkit.getPlayer(owner), economy);
					acc.depositAccount(owner, price);
					// Calculates the amount of money spent on taxes during the
					// purchase.
					// double taxpaid = gettaxPaid(price);
					// acc.deposit(price);
					// Informs the player about their purchase including how
					// much was bought, how much was spent, and how much was
					// spent on taxes.
					p.sendMessage(ChatColor.BLACK + "-----------------------------------------------------");
					p.sendMessage(ChatColor.BLUE + "" + ChatColor.ITALIC + "You bought " + ChatColor.GREEN + "" + ChatColor.ITALIC + "" + amount + ChatColor.AQUA + "" + ChatColor.ITALIC + " " + name + ChatColor.BLUE + ChatColor.ITALIC + " for " + ChatColor.GREEN + "" + ChatColor.ITALIC + hc.getYaml().getConfig().getString("config.currency-symbol") + price + ChatColor.BLUE + "" + ChatColor.ITALIC + " from " + owner);
					p.sendMessage(ChatColor.BLACK + "-----------------------------------------------------");
					// This writes a log entry for the transaction in the
					// HyperConomy log.txt file.
					if (hc.useSQL()) {
						log.writeSQLLog(p.getName(), "purchase", name, (double) amount, price, 0.0, owner, "chestshop");
					} else {
						String logentry = p.getName() + " bought " + amount + " " + name + " for " + hc.getYaml().getConfig().getString("config.currency-symbol") + price + " from " + owner + ". [Static Price=" + sf.getStatic(name, playerecon) + "][Initial Price=" + sf.getInitiation(name, playerecon) + "]";
						;
						log.setEntry(logentry);
						log.writeBuffer();
					}
					Player o = Bukkit.getPlayer(owner);
					if (o != null) {
						o.sendMessage("§9" + p.getName() + " bought §a" + amount + " §b" + name + " §9from you for §a" + hc.getYaml().getConfig().getString("config.currency-symbol") + price + "§9.");
					}
					return true;
					// This informs the player of how many of an item they can
					// buy if they don't have enough space for the requested
					// amount.
				} else {
					p.sendMessage(ChatColor.BLUE + "You only have room to buy " + space + " " + name + "!");
				}
				// This informs the player that they don't have enough money for
				// a transaction.
			} else {
				p.sendMessage(ChatColor.BLUE + "Insufficient Funds!");
			}
			return false;
			// Reports the error to the player making the transaction and prints
			// the stack trace for analysis.
		} catch (Exception e) {
			e.printStackTrace();
			Logger log = Logger.getLogger("Minecraft");
			log.info("HyperConomy ERROR #40");
			Bukkit.broadcast(ChatColor.DARK_RED + "HyperConomy ERROR #40", "hyperconomy.error");
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
		// Handles buy function errors.
		try {
			SQLFunctions sf = hc.getSQLFunctions();
			Economy economy = hc.getEconomy();
			Account acc = hc.getAccount();
			Log log = hc.getLog();
			String playerecon = sf.getPlayerEconomy(owner);
			// Makes sure that the player has enough money for the transaction.
			acc.setAccount(hc, p, economy);
			if (acc.checkFunds(price)) {
				int space = getavailableSpace(id, data, p);
				if (space >= amount) {
					addboughtItems(amount, id, data, p);
					removeItems(id, data, amount, invent);
					// Withdraws the price of the transaction from the player's
					// account.
					acc.withdraw(price);
					// Deposits the money spent by the player into the chest
					// owner's account.
					acc.setAccount(hc, Bukkit.getPlayer(owner), economy);
					acc.depositAccount(owner, price);
					// Informs the player about their purchase including how
					// much was bought, how much was spent, and how much was
					// spent on taxes.
					p.sendMessage(ChatColor.BLACK + "-----------------------------------------------------");
					p.sendMessage(ChatColor.BLUE + "" + ChatColor.ITALIC + "You bought " + ChatColor.GREEN + "" + ChatColor.ITALIC + "" + amount + ChatColor.AQUA + "" + ChatColor.ITALIC + " " + name + ChatColor.BLUE + ChatColor.ITALIC + " for " + ChatColor.GREEN + "" + ChatColor.ITALIC + hc.getYaml().getConfig().getString("config.currency-symbol") + price + ChatColor.BLUE + "" + ChatColor.ITALIC + " from " + owner);
					p.sendMessage(ChatColor.BLACK + "-----------------------------------------------------");
					// This writes a log entry for the transaction in the
					// HyperConomy log.txt file.
					if (hc.useSQL()) {
						log.writeSQLLog(p.getName(), "purchase", name, (double) amount, price, 0.0, owner, "chestshop");
					} else {
						String logentry = p.getName() + " bought " + amount + " " + name + " for " + hc.getYaml().getConfig().getString("config.currency-symbol") + price + " from " + owner + ". [Static Price=" + sf.getStatic(name, playerecon) + "][Initial Price=" + sf.getInitiation(name, playerecon) + "]";
						;
						log.setEntry(logentry);
						log.writeBuffer();
					}
					Player o = Bukkit.getPlayer(owner);
					if (o != null) {
						o.sendMessage("§9" + p.getName() + " bought §a" + amount + " §b" + name + " §9from you for §a" + hc.getYaml().getConfig().getString("config.currency-symbol") + price + "§9.");
					}
					return true;
					// This informs the player of how many of an item they can
					// buy if they don't have enough space for the requested
					// amount.
				} else {
					p.sendMessage(ChatColor.BLUE + "You only have room to buy " + space + " " + name + "!");
				}
				// This informs the player that they don't have enough money for
				// a transaction.
			} else {
				p.sendMessage(ChatColor.BLUE + "Insufficient Funds!");
			}
			return false;
			// Reports the error to the player making the transaction and prints
			// the stack trace for analysis.
		} catch (Exception e) {
			e.printStackTrace();
			Logger log = Logger.getLogger("Minecraft");
			log.info("HyperConomy ERROR #40");
			Bukkit.broadcast(ChatColor.DARK_RED + "HyperConomy ERROR #40", "hyperconomy.error");
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
		SQLFunctions sf = hc.getSQLFunctions();
		Calculation calc = hc.getCalculation();
		Economy economy = hc.getEconomy();
		Account acc = hc.getAccount();
		Log log = hc.getLog();
		// Handles sell function errors.
		try {
			sf = hc.getSQLFunctions();
			String playerecon = sf.getPlayerEconomy(owner);
			// Determines the sale value of the items being sold.
			double price = calc.getValue(name, amount, p);
			// If the cost is greater than 10^10 the getValue calculation sets
			// the value to this arbitrary number to indicate this. This makes
			// sure no many infinite values exist
			// and limits the number of items that can be sold at a time.
			Boolean toomuch = false;
			if (price == 3235624645000.7) {
				toomuch = true;
			}
			// Makes sure that the value of the items is not extremely high or
			// infinite.
			if (!toomuch) {
				// Removes the sold items from the player's inventory.
				removesoldItems(id, data, amount, p);
				addItems(id, data, amount, invent);
				// Deposits money in players account and tells them of
				// transaction success and details about the transaction.
				acc.setAccount(hc, p, economy);
				acc.deposit(price);
				// Withdraws money from the chest owner's account.
				acc.setAccount(hc, Bukkit.getPlayer(owner), economy);
				acc.withdrawAccount(owner, price);
				p.sendMessage(ChatColor.BLACK + "-----------------------------------------------------");
				p.sendMessage(ChatColor.BLUE + "" + ChatColor.ITALIC + "You sold " + ChatColor.GREEN + "" + "" + ChatColor.ITALIC + amount + ChatColor.AQUA + "" + ChatColor.ITALIC + " " + name + ChatColor.BLUE + ChatColor.ITALIC + " to " + owner + " for " + ChatColor.GREEN + "" + ChatColor.ITALIC + hc.getYaml().getConfig().getString("config.currency-symbol") + price + ChatColor.BLUE + "" + ChatColor.ITALIC + "!");
				p.sendMessage(ChatColor.BLACK + "-----------------------------------------------------");
				// Plays smoke effects.
				World w = p.getWorld();
				w.playEffect(p.getLocation(), Effect.SMOKE, 4);
				if (hc.useSQL()) {
					log.writeSQLLog(p.getName(), "sale", name, (double) amount, price, 0.0, owner, "chestshop");
				} else {
					String logentry = p.getName() + " sold " + amount + " " + name + " for " + hc.getYaml().getConfig().getString("config.currency-symbol") + price + " to " + owner + ". [Static Price=" + sf.getStatic(name, playerecon) + "][Initial Price=" + sf.getInitiation(name, playerecon) + "]";
					log.setEntry(logentry);
					log.writeBuffer();
				}
				Player o = Bukkit.getPlayer(owner);
				if (o != null) {
					o.sendMessage("§9" + p.getName() + " sold §a" + amount + " §b" + name + " §9to you for §a" + hc.getYaml().getConfig().getString("config.currency-symbol") + price + "§9.");
				}
				return true;
				// Informs the player if they're trying to sell too many items
				// at once, resulting in an infinite value.
			} else {
				p.sendMessage(ChatColor.BLUE + "Currently, you can't sell more than " + sf.getStock(name, playerecon) + " " + name + "!");
			}
			return false;
			// Reports the error to the player making the transaction and prints
			// the stack trace for analysis.
		} catch (Exception e) {
			e.printStackTrace();
			Logger l = Logger.getLogger("Minecraft");
			l.info("HyperConomy ERROR #38");
			Bukkit.broadcast(ChatColor.DARK_RED + "HyperConomy ERROR #38", "hyperconomy.error");
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
		SQLFunctions sf = hc.getSQLFunctions();
		Economy economy = hc.getEconomy();
		Account acc = hc.getAccount();
		Log log = hc.getLog();
		// Handles sell function errors.
		try {
			sf = hc.getSQLFunctions();
			String playerecon = sf.getPlayerEconomy(owner);
			// Removes the sold items from the player's inventory.
			removesoldItems(id, data, amount, p);
			addItems(id, data, amount, invent);
			// Deposits money in players account and tells them of transaction
			// success and details about the transaction.
			acc.setAccount(hc, p, economy);
			acc.deposit(price);
			// Withdraws money from the chest owner's account.
			acc.setAccount(hc, Bukkit.getPlayer(owner), economy);
			acc.withdrawAccount(owner, price);
			p.sendMessage(ChatColor.BLACK + "-----------------------------------------------------");
			p.sendMessage(ChatColor.BLUE + "" + ChatColor.ITALIC + "You sold " + ChatColor.GREEN + "" + "" + ChatColor.ITALIC + amount + ChatColor.AQUA + "" + ChatColor.ITALIC + " " + name + ChatColor.BLUE + ChatColor.ITALIC + " to " + owner + " for " + ChatColor.GREEN + "" + ChatColor.ITALIC + hc.getYaml().getConfig().getString("config.currency-symbol") + price + ChatColor.BLUE + "" + ChatColor.ITALIC + "!");
			p.sendMessage(ChatColor.BLACK + "-----------------------------------------------------");
			// Plays smoke effects. (Pointless but funny.)
			World w = p.getWorld();
			w.playEffect(p.getLocation(), Effect.SMOKE, 4);
			// Writes a log entry in the HyperConomy log.txt file.
			if (hc.useSQL()) {
				log.writeSQLLog(p.getName(), "sale", name, (double) amount, price, 0.0, owner, "chestshop");
			} else {
				String logentry = p.getName() + " sold " + amount + " " + name + " for " + hc.getYaml().getConfig().getString("config.currency-symbol") + price + " to " + owner + ". [Static Price=" + sf.getStatic(name, playerecon) + "][Initial Price=" + sf.getInitiation(name, playerecon) + "]";
				log.setEntry(logentry);
				log.writeBuffer();
			}
			Player o = Bukkit.getPlayer(owner);
			if (o != null) {
				o.sendMessage("§9" + p.getName() + " sold §a" + amount + " §b" + name + " §9to you for §a" + hc.getYaml().getConfig().getString("config.currency-symbol") + price + "§9.");
			}
			return true;
			// Reports the error to the player making the transaction and prints
			// the stack trace for analysis.
		} catch (Exception e) {
			e.printStackTrace();
			Logger l = Logger.getLogger("Minecraft");
			l.info("HyperConomy ERROR #38");
			Bukkit.broadcast(ChatColor.DARK_RED + "HyperConomy ERROR #38", "hyperconomy.error");
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
		Calculation calc = hc.getCalculation();
		ETransaction ench = hc.getETransaction();
		// Handles errors for the function.
		try {
			// Converts any item susceptible to damage's damage value back to 0.
			int newdata = calc.newData(id, data);
			// HashMap stacks1 stores all slot ids and ItemStacks of a given id.
			HashMap<Integer, ? extends ItemStack> stacks1 = invent.all(id);
			// Makes sure that the player's inventory actually contains the item
			// being sold. (This should have already been verified.)
			if (invent.contains(id)) {
				// Searches for the first slot containing the ItemStack and then
				// determines the maximum stacksize for the item from that
				// ItemStack.
				String stringstacks = stacks1.toString();
				int maxstack = stacks1.get(Integer.parseInt(stringstacks.substring(1, stringstacks.indexOf("=")))).getMaxStackSize();
				int ritems = amount;
				// Makes sure the remaining items in the player's inventory have
				// the same data value and id as the item being sold and also
				// makes sure the items are not enchanted.
				// If an ItemStack is larger than the amount being sold, it only
				// removes part of it, otherwise it deletes the entire stack.
				int slot;
				String allstacks = "{" + stringstacks;
				while (allstacks.contains(" x ")) {
					int a = allstacks.indexOf(" x ") + 3;
					int b = allstacks.indexOf("}", a);
					slot = Integer.parseInt(allstacks.substring(2, allstacks.indexOf("=")));
					// Gets the correct damage value if the item is a potion.
					int damv = calc.getpotionDV(invent.getItem(slot));
					// Checks whether or not the item has enchantments.
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
				// Sends an error message. If seen this indicates the sell
				// function isn't working properly, as the players inventory
				// doesn't actually contain the item being sold.
			} else {
				Logger log = Logger.getLogger("Minecraft");
				log.info("HyperConomy ERROR #39");
				Bukkit.broadcast(ChatColor.DARK_RED + "HyperConomy ERROR #39", "hyperconomy.error");
			}
			// Reports errors in the removesoldItems function and reports them
			// to the player and prints the stack trace.
		} catch (Exception e) {
			e.printStackTrace();
			Logger log = Logger.getLogger("Minecraft");
			log.info("HyperConomy ERROR #37");
			Bukkit.broadcast(ChatColor.DARK_RED + "HyperConomy ERROR #37", "hyperconomy.error");
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
		Calculation calc = hc.getCalculation();
		MaterialData md = new MaterialData(id, (byte) data);
		ItemStack stack = md.toItemStack();
		int maxstack = stack.getMaxStackSize();
		int invsize = invent.getSize();
		try {
			int availablespace = 0;
			int slot = 0;
			while (slot < invsize) {
				ItemStack citem = invent.getItem(slot);
				if (invent.getItem(slot) == null) {
					availablespace = availablespace + maxstack;
				} else if (citem != null && citem.getTypeId() == id && data == calc.getdamageValue(citem)) {
					availablespace = availablespace + (maxstack - citem.getAmount());
				}
				slot++;
			}
			return availablespace;
		} catch (Exception e) {
			e.printStackTrace();
			Logger log = Logger.getLogger("Minecraft");
			log.info("HyperConomy ERROR #36");
			Bukkit.broadcast(ChatColor.DARK_RED + "HyperConomy ERROR #36", "hyperconomy.error");
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
		Calculation calc = hc.getCalculation();
		try {
			// ramount holds the number of items remaining to be placed in the
			// player's inventory.
			int ramount = amount;
			MaterialData md = new MaterialData(id, (byte) data);
			ItemStack stack = md.toItemStack();
			int maxstack = stack.getMaxStackSize();
			// While the player still has items to be placed into their
			// inventory this places the max stack size of that item into
			// the first available slot until there are no remaining items to be
			// placed.
			int slot = 0;
			while (ramount > 0) {
				// Stores how many items are being placed into the current slot.
				int pamount = 0;
				// Handles slots that already have some of the same item in it.
				ItemStack citem = invent.getItem(slot);
				if (citem != null && citem.getTypeId() == id && data == calc.getdamageValue(citem)) {
					int currentamount = citem.getAmount();
					if ((maxstack - currentamount) >= ramount) {
						pamount = ramount;
						citem.setAmount(pamount + currentamount);
					} else {
						pamount = maxstack - currentamount;
						citem.setAmount(maxstack);
					}
					// Handles empty slots.
				} else if (invent.getItem(slot) == null) {
					// Checks if the item is a potion and not a water bottle and
					// then creates an ItemStack of that potion.
					ItemStack stack2;
					if (id == 373 && data != 0) {
						Potion pot = Potion.fromDamage(data);
						stack2 = pot.toItemStack(amount);
					} else {
						stack2 = md.toItemStack();
					}
					// Checks if the items remaining to be put in a player's
					// inventory exceed the maximum stack size. If they do it
					// sets pamount (the amount to be placed into
					// the player's inventory to the max amount, otherwise it
					// sets pamount to the remaining number of items.
					if (ramount > maxstack) {
						pamount = maxstack;
					} else {
						pamount = ramount;
					}
					// Places the determined amount in the first empty inventory
					// slot that a player has and then subtracts the amount of
					// items placed in their inventory from
					// the remaining items.
					stack2.setAmount(pamount);
					invent.setItem(slot, stack2);
				}
				ramount = ramount - pamount;
				slot++;
			}
		} catch (Exception e) {
			e.printStackTrace();
			Logger log = Logger.getLogger("Minecraft");
			log.info("HyperConomy ERROR #35");
			Bukkit.broadcast(ChatColor.DARK_RED + "HyperConomy ERROR #35", "hyperconomy.error");
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
		Calculation calc = hc.getCalculation();
		ETransaction ench = hc.getETransaction();
		// Handles errors for the function.
		try {
			// Puts all of the specified item in a HashMap containing the item
			// slot and the ItemStack.
			HashMap<Integer, ? extends ItemStack> stacks1 = invent.all(id);
			// Converts any item susceptible to damage's damage value back to 0.
			int newdata = calc.newData(id, data);
			int slot = 0;
			int totalitems = 0;
			if (invent.contains(id)) {
				String allstacks = "{" + stacks1.toString();
				while (allstacks.contains(" x ")) {
					int a = allstacks.indexOf(" x ") + 3;
					int b = allstacks.indexOf("}", a);
					slot = Integer.parseInt(allstacks.substring(2, allstacks.indexOf("=")));
					// Gets the correct damage value if the item in the player's
					// hand is a potion.
					int da = calc.getpotionDV(stacks1.get(slot));
					// Checks whether or not the item in the player's hand has
					// enchantments.
					boolean hasenchants = ench.hasenchants(stacks1.get(slot));
					// Extracts the number of items from the HashMap data pair
					// by converting it to a string and removing the excess
					// parts.
					if (stacks1.get(slot) != null && calc.newData(id, da) == newdata && hasenchants == false) {
						int num = Integer.parseInt(allstacks.substring(a, b));
						totalitems = num + totalitems;
					}
					allstacks = allstacks.substring(b + 1, allstacks.length());
				}
			}
			return totalitems;
			// Reports the error to the player making the transaction and prints
			// the stack trace for analysis.
		} catch (Exception e) {
			int totalitems = 0;
			e.printStackTrace();
			Logger log = Logger.getLogger("Minecraft");
			log.info("HyperConomy ERROR #34");
			Bukkit.broadcast(ChatColor.DARK_RED + "HyperConomy ERROR #34", "hyperconomy.error");
			return totalitems;
		}
	}
}
