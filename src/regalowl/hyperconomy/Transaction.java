package regalowl.hyperconomy;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.logging.Logger;

import net.milkbowl.vault.economy.Economy;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
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
	

	
	/**
	 * 
	 * 
	 * This function handles the purchase of items.
	 * 
	 */
	public void buy() {
		
		//Handles buy function errors.
		try {

			//Makes sure that the player is buying at least 1 item.
			if (amount > 0){
		
				//Makes sure that the shop has enough of the item.
				FileConfiguration items = hc.getYaml().getItems();
				int shopstock = items.getInt(name + ".stock.stock");
				if (shopstock >= amount) {
					
					//Makes sure that its a real item.  (Not experience.)
					if (id >= 0) {
			
						//Calculates the cost of the purchase for the player.
						calc.setVC(hc, p, amount, name, ench);
						double price = calc.getCost();
					
						//Makes sure that the player has enough money for the transaction.
						acc.setAccount(hc, p, economy);
						if (acc.checkFunds(price)) {
					
							//Calculates the maximum stack size of the item being purchased.
							MaterialData md = new MaterialData(id, (byte) data);
							ItemStack stack = md.toItemStack();				
							int maxstack = stack.getMaxStackSize();
					
								//Makes sure their inventory has enough room for the purchase.  It only counts empty inventory slots and factors in the maximum stack size.
								int space = getavailableSlots();
								if ((maxstack * space) >= amount) {
						
									//ramount holds the number of items remaining to be placed in the player's inventory.
									int ramount = amount;		
						
									//While the player still has items to be placed into their inventory this places the max stack size of that item into
									//the first available slot until there are no remaining items to be placed.
									while (ramount > 0) {
							
										//Checks if the item it a potion and not a water bottle and then creates an ItemStack of that potion.
										ItemStack stack2;
										if (id == 373 && data != 0) {
											Potion pot = Potion.fromDamage(data);
											stack2 = pot.toItemStack(amount);			
										} else {
											stack2 = md.toItemStack();
										}
							
										//Checks if the items remaining to be put in a player's inventory exceed the maximum stack size.  If they do it sets pamount (the amount to be placed into
										//the player's inventory to the max amount, otherwise it sets pamount to the remaining number of items.
										int pamount;
										if (ramount > maxstack) {
											pamount = maxstack;
										} else {
											pamount = ramount;
										}
									
										//Places the determined amount in the first empty inventory slot that a player has and then subtracts the amount of items placed in their inventory from
										//the remaining items.
										stack2.setAmount(pamount);	
										int fe = p.getInventory().firstEmpty();
										p.getInventory().setItem(fe, stack2);
										ramount = ramount - pamount;
									}
					
									//Removes the number of items purchased from the shop's stock and saves the HyperConomy.yaml.
									items.set((name + ".stock.stock"), (shopstock - amount));
									//HyperConomy.yaml.saveYamls();
								
									//Withdraws the price of the transaction from the player's account.
									acc.withdraw(price);
									
									//Deposits the money spent by the player into the server account.
									acc.depositShop(price);
									
									//Reverts any changes to the global shop account if the account is set to unlimited.
									if (hc.getYaml().getConfig().getBoolean("config.shop-has-unlimited-money")) {
										acc.setBalance("hyperconomy", 0);
									}
										
									//Calculates the amount of money spent on taxes during the purchase.
									double taxpaid = gettaxPaid(price);							
					
									//Informs the player about their purchase including how much was bought, how much was spent, and how much was spent on taxes.
									p.sendMessage(ChatColor.BLACK + "-----------------------------------------------------");
									p.sendMessage(ChatColor.BLUE + "" + ChatColor.ITALIC + "You bought " + ChatColor.GREEN + "" + ChatColor.ITALIC + "" + amount + ChatColor.AQUA + "" + ChatColor.ITALIC + " " + name + " for " + ChatColor.GREEN + "" + ChatColor.ITALIC + "$" + price + ChatColor.BLUE + "" + ChatColor.ITALIC + " and paid " + ChatColor.GREEN + "" + ChatColor.ITALIC + "$" + taxpaid + " in taxes!" );
									//p.sendMessage(ChatColor.BLUE + "" + "You bought " + ChatColor.GREEN + "" + "" + amount + ChatColor.AQUA + "" + " " + name + " for " + ChatColor.GREEN + "" + "$" + price + ChatColor.BLUE + "" + " and paid " + ChatColor.GREEN + "" + "$" + taxpaid + " in taxes!" );
									p.sendMessage(ChatColor.BLACK + "-----------------------------------------------------");	
	
									//This writes a log entry for the transaction in the HyperConomy log.txt file.
									
									String logentry = p.getName() + " bought " + amount + " " + name + " for $" + price + ". [Static Price=" + items.getBoolean(name + ".price.static") + "][Initial Price=" + items.getBoolean(name + ".initiation.initiation") + "]";
									log.setEntry(logentry);
									log.writeBuffer();
									
									//Updates all information signs.
									isign.setrequestsignUpdate(true);
									isign.checksignUpdate();
									
									//Sends price update notifications.
									not.setNotify(hc, calc, ench, name, null);
									not.sendNotification();
									
									//This informs the player of how many of an item they can buy if they don't have enough space for the requested amount.
								} else {
									p.sendMessage(ChatColor.BLUE + "You only have room to buy " + (space * maxstack) + " " + name + "!");
								}
								//This informs the player that they don't have enough money for a transaction.
						} else {
							p.sendMessage(ChatColor.BLUE + "Insufficient Funds!");
						}
					//Informs the player if it's not a real item.
					} else {
						p.sendMessage(ChatColor.BLUE + "Sorry, " + name + " cannot be purchased with this command.");
					}
					//This informs the player that the shop doesn't have enough of the chosen item.	
				} else {
					p.sendMessage(ChatColor.BLUE + "The shop doesn't have enough " + name + "!");
				}
				//This informs the player that they can't try to buy negative values or 0 of an item.
			} else {
				p.sendMessage(ChatColor.BLUE + "You can't buy less than 1 " + name + "!");
			}
		
		//Reports the error to the player making the transaction and prints the stack trace for analysis.
		} catch (Exception e) {
			e.printStackTrace();
	    	Logger log = Logger.getLogger("Minecraft");
	    	log.info("HyperConomy ERROR #2");
	    	Bukkit.broadcast(ChatColor.DARK_RED + "HyperConomy ERROR #2", "hyperconomy.error");
		}
	}
	
	

	/**
	 * 
	 * 
	 * This function handles the sale of items.
	 * 
	 */
	public void sell() {
		
		//Handles sell function errors.
		try {

			//Makes sure that they aren't selling negative items, or 0.
			if (amount > 0){
				
				//Makes sure that its a real item.  (Not experience.)
				if (id >= 0) {
				
					//Counts the number of the chosen item that the player has in their inventory and makes sure that they have enough of the item for the transaction.		
					int totalitems = countInvitems();
					if (totalitems >= amount) {
						
						//Determines the sale value of the items being sold.
						calc.setVC(hc, p, amount, name, ench);
						double price = calc.getValue();
						
						//If the cost is greater than 10^10 the getValue calculation sets the value to this arbitrary number to indicate this.  This makes sure no many infinite values exist
						//and limits the number of items that can be sold at a time.
						Boolean toomuch = false;
						if (price == 3235624645000.7) {
							toomuch = true;
						}
						
						//Gets the items file for the following check.
						FileConfiguration items = hc.getYaml().getItems();
						
						//Makes sure that the value of the items is not extremely high or infinite.			
						if (!toomuch){
							
							//Calculates how many items can be sold before hitting the hyperbolic curve so as to not allow it to be bypassed temporarily.
							int maxi = getmaxInitial();
							
							//EXPERIMENTAL  Sets the amount to the transition point between Dynamic and Initial pricing if the requested amount is greater than the transition point.
							if (amount > (maxi) && (items.getBoolean(name + ".price.static")) == false && (items.getBoolean(name + ".initiation.initiation") == true)) {
								amount = maxi;
								calc.setVC(hc, p, amount, name, ench);
								price = calc.getValue();
							}
							
							boolean sunlimited = hc.getYaml().getConfig().getBoolean("config.shop-has-unlimited-money");
							//Makes sure the global shop has enough money for the transaction.					
							if (acc.checkshopBalance(price) || sunlimited) {
								
								//This recalculates the value of the sale if the getValue function sets the initiation period to false (Not sure if this is necessary).
								if (maxi == 0) {
									calc.setVC(hc, p, amount, name, ench);
									price = calc.getValue();
								}
								
								//Removes the sold items from the player's inventory.
								removesoldItems();
							
								//Adds the sold items to the shopstock and saves the yaml file.
								int shopstock = items.getInt(name + ".stock.stock");
								items.set((name + ".stock.stock"), (shopstock + amount));	
								
								//Sets the initiation period to false if the item has reached the hyperbolic pricing curve 
								//after the transaction is complete so that the value is calculated correctly for future value inquiries.
								int maxi2 = getmaxInitial();
								if (maxi2 == 0) {
									items.set(name + ".initiation.initiation", false);
								}
								
							
								//Deposits money in players account and tells them of transaction success and details about the transaction.
								acc.setAccount(hc, p, economy);
								acc.deposit(price);
								
								//Withdraws money from the global shop's account.
								acc.withdrawShop(price);
								
								//Reverts any changes to the global shop account if the account is set to unlimited.
								if (sunlimited) {
									acc.setBalance("hyperconomy", 0);
								}
								
								p.sendMessage(ChatColor.BLACK + "-----------------------------------------------------");
								p.sendMessage(ChatColor.BLUE + "" + ChatColor.ITALIC + "You sold " + ChatColor.GREEN + "" + "" + ChatColor.ITALIC + amount + ChatColor.AQUA + "" + ChatColor.ITALIC + " " + name + " for " + ChatColor.GREEN + "" + ChatColor.ITALIC + "$" + price + ChatColor.BLUE + "" + ChatColor.ITALIC + "!");
								//p.sendMessage(ChatColor.BLUE + "" + "You sold " + ChatColor.GREEN + "" + "" + amount + ChatColor.AQUA + "" + " " + name + " for " + ChatColor.GREEN + "" + "$" + price + ChatColor.BLUE + "" + "!");
								p.sendMessage(ChatColor.BLACK + "-----------------------------------------------------");
								
								//Plays smoke effects.  (Pointless but funny.)
								World w = p.getWorld();
								w.playEffect(p.getLocation(), Effect.SMOKE, 4);									
								
								//Writes a log entry in the HyperConomy log.txt file.
								
								String logentry = p.getName() + " sold " + amount + " " + name + " for $" + price + ". [Static Price=" + items.getBoolean(name + ".price.static") + "][Initial Price=" + items.getBoolean(name + ".initiation.initiation") + "]";
								log.setEntry(logentry);
								log.writeBuffer();			
								
								//Updates all information signs.
								isign.setrequestsignUpdate(true);
								isign.checksignUpdate();
								
								//Sends price update notifications.
								not.setNotify(hc, calc, ench, name, null);
								not.sendNotification();
							
							//Informs the player if the shop doesn't have enough money.
							} else {
								p.sendMessage(ChatColor.BLUE + "Sorry, the shop currently does not have enough money.");
							}
						
						//Informs the player if they're trying to sell too many items at once, resulting in an infinite value.
						} else {
							p.sendMessage(ChatColor.BLUE + "Currently, you can't sell more than " + items.get(name + ".stock.stock") + " " + name + "!");
						}
						
					//Informs the player that they don't have enough of the item to complete the transaction.	
					} else {
						p.sendMessage(ChatColor.BLUE + "You don't have enough " + name + "!");
					}
			
				//Informs the player if it's not a real item.
				} else {
					p.sendMessage(ChatColor.BLUE + "Sorry, " + name + " cannot be sold with this command.");
				}
				
		//Informs the player that they can't sell negative or 0 items.
			} else {
				p.sendMessage(ChatColor.BLUE + "You can't sell less than 1 " + name + "!");
			}
		
		//Reports the error to the player making the transaction and prints the stack trace for analysis.
		} catch (Exception e) {
			e.printStackTrace();
	    	Logger log = Logger.getLogger("Minecraft");
	    	log.info("HyperConomy ERROR #3");
	    	Bukkit.broadcast(ChatColor.DARK_RED + "HyperConomy ERROR #3", "hyperconomy.error");
		}
	
	}
	
	
	
	/**
	 * 
	 * 
	 * This function counts the number of the specified item in a player's inventory.  It ignores durability.
	 * 
	 */
	public int countInvitems(){
		
		//Handles errors for the function.
		try {
			//Gets the player's inventory
			Inventory pinv = p.getInventory();
			
			//Puts all of the specified item in a HashMap containing the item slot and the ItemStack.
			HashMap <Integer,?extends ItemStack> stacks1 = pinv.all(id);
			
			//Converts any item susceptible to damage's damage value back to 0.
			calc.setNdata(id, data);
			int newdata = calc.newData();
			int slot = 0;
			int totalitems = 0;
			if (p.getInventory().contains(id)) {
				
				String allstacks = "{" + stacks1.toString();
				while (allstacks.contains(" x ")) {
					int a = allstacks.indexOf(" x ") + 3;
					int b = allstacks.indexOf("}", a);
					slot = Integer.parseInt(allstacks.substring(2, allstacks.indexOf("=")));
	
					//Gets the correct damage value if the item in the player's hand is a potion.
					calc.setPDV(stacks1.get(slot));
					int da = calc.getpotionDV();
	
					//Checks whether or not the item in the player's hand has enchantments.
	    			ench.setHE(stacks1.get(slot));
	    			boolean hasenchants = ench.hasenchants();
	    			
	    			//Extracts the number of items from the HashMap data pair by converting it to a string and removing the excess parts.
	    			calc.setNdata(id, da);
					if (stacks1.get(slot) != null && calc.newData() == newdata && hasenchants == false) {
						int num = Integer.parseInt(allstacks.substring(a, b));
						totalitems = num + totalitems;
					}
					allstacks = allstacks.substring(b + 1, allstacks.length());
				}
			}
			return totalitems;
			
		//Reports the error to the player making the transaction and prints the stack trace for analysis.
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
	public int getavailableSlots(){
		
		try {
			int availablespace = 0;
			int slot = 0;
			while (slot < 36) {
				if (p.getInventory().getItem(slot) == null) {
					availablespace++;
				}
			slot++;
			}
			return availablespace;
		} catch (Exception e) {
			e.printStackTrace();
	    	Logger log = Logger.getLogger("Minecraft");
	    	log.info("HyperConomy ERROR #6");
	    	Bukkit.broadcast(ChatColor.DARK_RED + "HyperConomy ERROR #6", "hyperconomy.error");
			int availablespace = 0;
			return availablespace;
		}
	}
	
	
	
	
	/**
	 * 
	 * 
	 * This function removes the items that a player has sold from their inventory.  The amount is the number of items sold, and it requires the player and the item's id and data.
	 * 
	 */
	private void removesoldItems(){
		
		//Handles errors for the function.
		try {
		
			//Converts any item susceptible to damage's damage value back to 0.
			calc.setNdata(id, data);
			int newdata = calc.newData();
					
			//HashMap stacks1 stores all slot ids and ItemStacks of a given id.
			Inventory pinv = p.getInventory();
			HashMap <Integer,?extends ItemStack> stacks1 = pinv.all(id);
			
			//Makes sure that the player's inventory actually contains the item being sold.  (This should have already been verified.)
			if (p.getInventory().contains(id)) {
				
				//Searches for the first slot containing the ItemStack and then determines the maximum stacksize for the item from that ItemStack.
				String stringstacks = stacks1.toString();
				int maxstack = stacks1.get(Integer.parseInt(stringstacks.substring(1, stringstacks.indexOf("=")))).getMaxStackSize();
				
				/*
				This section prioritizes the item in the player's hand before moving on to other inventory slots.
				*/
				
				//Gets the correct damage value if the item in the player's hand is a potion.
				ItemStack inhand = p.getItemInHand();
				calc.setPDV(inhand);
				int dv = calc.getpotionDV();
	
				//Checks whether or not the item in the player's hand has enchantments.
				ench.setHE(inhand);
				boolean hasenchants = ench.hasenchants();
				
				//Makes sure the item in the player's hand hs the same data value and id as the item being sold and also makes sure it is not enchanted.  Checks for nulls just in case.
				//If the itemstack in the players hand is larger than the amount being sold, it only removes part of it, otherwise it deletes the entire stack.
				int ritems = amount;
				calc.setNdata(id, dv);
				if (inhand != null && calc.newData() == newdata && inhand.getTypeId() == id && hasenchants == false) {
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
	
				//Makes sure the remaining items in the player's inventory have the same data value and id as the item being sold and also makes sure the items are not enchanted.
				//If an ItemStack is larger than the amount being sold, it only removes part of it, otherwise it deletes the entire stack.
				int slot;
				String allstacks = "{" + stringstacks;
				while(allstacks.contains(" x ")){
					int a = allstacks.indexOf(" x ") + 3;
					int b = allstacks.indexOf("}", a);			
					slot = Integer.parseInt(allstacks.substring(2, allstacks.indexOf("=")));
	
					//Gets the correct damage value if the item is a potion.	
					calc.setPDV(pinv.getItem(slot));
					int damv = calc.getpotionDV();
					
	    			//Checks whether or not the item has enchantments.
	    			ench.setHE(stacks1.get(slot));
	    			boolean hasenchants2 = ench.hasenchants();
	    			calc.setNdata(id, damv);
					 if (pinv.getItem(slot) != null && calc.newData() == newdata && pinv.getItem(slot).getTypeId() == id && hasenchants2 == false) {
						 if (ritems > 0) {
							 if (ritems >= maxstack && pinv.getItem(slot).getAmount() == maxstack) {
								 pinv.clear(slot);
								 ritems = ritems - maxstack;
							 } else {
								 int stackamount = pinv.getItem(slot).getAmount();
								 if (stackamount <= ritems){
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
	
				//Sends an error message.  If seen this indicates the sell function isn't working properly, as the players inventory doesn't actually contain the item being sold.
			} else {
		    	Logger log = Logger.getLogger("Minecraft");
		    	log.info("HyperConomy ERROR #1");
		    	Bukkit.broadcast(ChatColor.DARK_RED + "HyperConomy ERROR #1", "hyperconomy.error");
			}
		
		//Reports errors in the removesoldItems function and reports them to the player and prints the stack trace.
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
	 * This function returns the maximum number of items that can be sold before reaching the hyperbolic pricing curve.
	 * 
	 */
	private int getmaxInitial(){
		try {
			FileConfiguration items = hc.getYaml().getItems();	
			int maxinitialitems = 0;
			double shopstock = items.getDouble(name + ".stock.stock");
			double value = items.getDouble(name + ".value");
			double median = items.getDouble(name + ".stock.median");
			double icost = items.getDouble(name + ".initiation.startprice");
			double totalstock = ((median * value)/icost);
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
	 * This function returns the amount of money spent on taxes during a purchase.
	 * 
	 */
	public double gettaxPaid(Double price) {
		try {
			FileConfiguration items = hc.getYaml().getItems();
			Boolean itax = items.getBoolean(name + ".initiation.initiation");
			Boolean stax = items.getBoolean(name + ".price.static");
			double taxrate;
			if (!stax) {
				if (itax) {
					taxrate = hc.getYaml().getConfig().getDouble("config.initialpurchasetaxpercent");
				} else {
					taxrate = hc.getYaml().getConfig().getDouble("config.purchasetaxpercent");
				}
			} else {
				taxrate = hc.getYaml().getConfig().getDouble("config.statictaxpercent");
			}
			double taxpaid = price - (price/(1 + taxrate/100));
			DecimalFormat twodigits = new DecimalFormat("#.##");
			taxpaid = Double.valueOf(twodigits.format(taxpaid));
			return taxpaid;
		} catch (Exception e) {
			e.printStackTrace();
	    	Logger log = Logger.getLogger("Minecraft");
	    	log.info("HyperConomy ERROR #9");
	    	Bukkit.broadcast(ChatColor.DARK_RED + "HyperConomy ERROR #8", "hyperconomy.error");
			int taxpaid = -1;
			return taxpaid;
		}
	}
	
	
	
	
	
	/**
	 * 
	 * 
	 * This function handles the purchase of experience.
	 * 
	 */
	public void buyXP() {
		
		//Handles buy function errors.
		try {

			//Makes sure that the player is buying at least 1 item.
			if (amount > 0){
		
				//Makes sure that the shop has enough of the item.
				FileConfiguration items = hc.getYaml().getItems();
				int shopstock = items.getInt(name + ".stock.stock");
				if (shopstock >= amount) {
			
					//Calculates the cost of the purchase for the player.
					calc.setVC(hc, p, amount, name, ench);
					double price = calc.getCost();
				
					//Makes sure that the player has enough money for the transaction.
					acc.setAccount(hc, p, economy);
					if (acc.checkFunds(price)) {
						
						//Calculates and sets the new level and experience bar.
						int totalxp = calc.gettotalxpPoints(p);
						int newxp = totalxp + amount;
						int newlvl = calc.getlvlfromXP(newxp);
						newxp = newxp - calc.getlvlxpPoints(newlvl);
						float xpbarxp = (float)newxp/(float)calc.getxpfornextLvl(newlvl);
						p.setLevel(newlvl);
						p.setExp(xpbarxp);
						
						//Removes the number of items purchased from the shop's stock and saves the HyperConomy.yaml.
						items.set((name + ".stock.stock"), (shopstock - amount));
						//HyperConomy.yaml.saveYamls();
							
						//Withdraws the price of the transaction from the player's account.
						acc.withdraw(price);
								
						//Deposits the money spent by the player into the server account.
						acc.depositShop(price);
								
						//Reverts any changes to the global shop account if the account is set to unlimited.
						if (hc.getYaml().getConfig().getBoolean("config.shop-has-unlimited-money")) {
							acc.setBalance("hyperconomy", 0);
						}
									
						//Calculates the amount of money spent on taxes during the purchase.
						double taxpaid = gettaxPaid(price);							
				
						//Informs the player about their purchase including how much was bought, how much was spent, and how much was spent on taxes.
						p.sendMessage(ChatColor.BLACK + "-----------------------------------------------------");
						p.sendMessage(ChatColor.BLUE + "" + ChatColor.ITALIC + "You bought " + ChatColor.GREEN + "" + ChatColor.ITALIC + "" + amount + ChatColor.AQUA + "" + ChatColor.ITALIC + " " + name + " for " + ChatColor.GREEN + "" + ChatColor.ITALIC + "$" + price + ChatColor.BLUE + "" + ChatColor.ITALIC + " and paid " + ChatColor.GREEN + "" + ChatColor.ITALIC + "$" + taxpaid + " in taxes!" );
						//p.sendMessage(ChatColor.BLUE + "" + "You bought " + ChatColor.GREEN + "" + "" + amount + ChatColor.AQUA + "" + " " + name + " for " + ChatColor.GREEN + "" + "$" + price + ChatColor.BLUE + "" + " and paid " + ChatColor.GREEN + "" + "$" + taxpaid + " in taxes!" );
						p.sendMessage(ChatColor.BLACK + "-----------------------------------------------------");	

						//This writes a log entry for the transaction in the HyperConomy log.txt file.
								
						String logentry = p.getName() + " bought " + amount + " " + name + " for $" + price + ". [Static Price=" + items.getBoolean(name + ".price.static") + "][Initial Price=" + items.getBoolean(name + ".initiation.initiation") + "]";
						log.setEntry(logentry);
						log.writeBuffer();
								
						//Updates all information signs.
						isign.setrequestsignUpdate(true);
						isign.checksignUpdate();
								
						//Sends price update notifications.
						not.setNotify(hc, calc, ench, name, null);
						not.sendNotification();

							//This informs the player that they don't have enough money for a transaction.
					} else {
						p.sendMessage(ChatColor.BLUE + "Insufficient Funds!");
					}
					//This informs the player that the shop doesn't have enough of the chosen item.	
				} else {
					p.sendMessage(ChatColor.BLUE + "The shop doesn't have enough " + name + "!");
				}
				//This informs the player that they can't try to buy negative values or 0 of an item.
			} else {
				p.sendMessage(ChatColor.BLUE + "You can't buy less than 1 " + name + "!");
			}
		
		//Reports the error to the player making the transaction and prints the stack trace for analysis.
		} catch (Exception e) {
			e.printStackTrace();
	    	Logger log = Logger.getLogger("Minecraft");
	    	log.info("HyperConomy ERROR #2");
	    	Bukkit.broadcast(ChatColor.DARK_RED + "HyperConomy ERROR #2", "hyperconomy.error");
		}
	}
	
	

	
	
	
	
	
	
	
	
	
	
	
	/**
	 * 
	 * 
	 * This function handles the sale of experience.
	 * 
	 */
	public void sellXP() {
		
		//Handles sell function errors.
		try {

			//Makes sure that they aren't selling negative items, or 0.
			if (amount > 0){
				
				//Counts the number of the chosen item that the player has in their inventory and makes sure that they have enough of the item for the transaction.		
				int totalxp = calc.gettotalxpPoints(p);
				if (totalxp >= amount) {
					
					//Determines the sale value of the items being sold.
					calc.setVC(hc, p, amount, name, ench);
					double price = calc.getValue();
					
					//If the cost is greater than 10^10 the getValue calculation sets the value to this arbitrary number to indicate this.  This makes sure no infinite values exist
					//and limits the number of items that can be sold at a time.
					Boolean toomuch = false;
					if (price == 3235624645000.7) {
						toomuch = true;
					}
					
					//Gets the items file for the following check.
					FileConfiguration items = hc.getYaml().getItems();
					
					//Makes sure that the value of the items is not extremely high or infinite.			
					if (!toomuch){
						
						//Calculates how many items can be sold before hitting the hyperbolic curve so as to not allow it to be bypassed temporarily.
						int maxi = getmaxInitial();
						
						//EXPERIMENTAL  Sets the amount to the transition point between Dynamic and Initial pricing if the requested amount is greater than the transition point.
						if (amount > (maxi) && (items.getBoolean(name + ".price.static")) == false && (items.getBoolean(name + ".initiation.initiation") == true)) {
							amount = maxi;
							calc.setVC(hc, p, amount, name, ench);
							price = calc.getValue();
						}
						
						boolean sunlimited = hc.getYaml().getConfig().getBoolean("config.shop-has-unlimited-money");
						//Makes sure the global shop has enough money for the transaction.					
						if (acc.checkshopBalance(price) || sunlimited) {
							
							//This recalculates the value of the sale if the getValue function sets the initiation period to false (Not sure if this is necessary).
							if (maxi == 0) {
								calc.setVC(hc, p, amount, name, ench);
								price = calc.getValue();
							}
							
							//Calculates and sets the new level and experience bar.
							int newxp = totalxp - amount;
							int newlvl = calc.getlvlfromXP(newxp);
							newxp = newxp - calc.getlvlxpPoints(newlvl);
							float xpbarxp = (float)newxp/(float)calc.getxpfornextLvl(newlvl);
							p.setLevel(newlvl);
							p.setExp(xpbarxp);
						
							//Adds the sold items to the shopstock and saves the yaml file.
							int shopstock = items.getInt(name + ".stock.stock");
							items.set((name + ".stock.stock"), (shopstock + amount));	
							
							//Sets the initiation period to false if the item has reached the hyperbolic pricing curve 
							//after the transaction is complete so that the value is calculated correctly for future value inquiries.
							int maxi2 = getmaxInitial();
							if (maxi2 == 0) {
								items.set(name + ".initiation.initiation", false);
							}
							
						
							//Deposits money in players account and tells them of transaction success and details about the transaction.
							acc.setAccount(hc, p, economy);
							acc.deposit(price);
							
							//Withdraws money from the global shop's account.
							acc.withdrawShop(price);
							
							//Reverts any changes to the global shop account if the account is set to unlimited.
							if (sunlimited) {
								acc.setBalance("hyperconomy", 0);
							}
							
							p.sendMessage(ChatColor.BLACK + "-----------------------------------------------------");
							p.sendMessage(ChatColor.BLUE + "" + ChatColor.ITALIC + "You sold " + ChatColor.GREEN + "" + "" + ChatColor.ITALIC + amount + ChatColor.AQUA + "" + ChatColor.ITALIC + " " + name + " for " + ChatColor.GREEN + "" + ChatColor.ITALIC + "$" + price + ChatColor.BLUE + "" + ChatColor.ITALIC + "!");
							//p.sendMessage(ChatColor.BLUE + "" + "You sold " + ChatColor.GREEN + "" + "" + amount + ChatColor.AQUA + "" + " " + name + " for " + ChatColor.GREEN + "" + "$" + price + ChatColor.BLUE + "" + "!");
							p.sendMessage(ChatColor.BLACK + "-----------------------------------------------------");
							
							//Plays smoke effects.  (Pointless but funny.)
							World w = p.getWorld();
							w.playEffect(p.getLocation(), Effect.SMOKE, 4);									
							
							//Writes a log entry in the HyperConomy log.txt file.
							
							String logentry = p.getName() + " sold " + amount + " " + name + " for $" + price + ". [Static Price=" + items.getBoolean(name + ".price.static") + "][Initial Price=" + items.getBoolean(name + ".initiation.initiation") + "]";
							log.setEntry(logentry);
							log.writeBuffer();			
							
							//Updates all information signs.
							isign.setrequestsignUpdate(true);
							isign.checksignUpdate();
							
							//Sends price update notifications.
							not.setNotify(hc, calc, ench, name, null);
							not.sendNotification();
						
						//Informs the player if the shop doesn't have enough money.
						} else {
							p.sendMessage(ChatColor.BLUE + "Sorry, the shop currently does not have enough money.");
						}
					
					//Informs the player if they're trying to sell too many items at once, resulting in an infinite value.
					} else {
						p.sendMessage(ChatColor.BLUE + "Currently, you can't sell more than " + items.get(name + ".stock.stock") + " " + name + "!");
					}
					
				//Informs the player that they don't have enough of the item to complete the transaction.	
				} else {
					p.sendMessage(ChatColor.BLUE + "You don't have enough " + name + "!");
				}
		
		//Informs the player that they can't sell negative or 0 items.
			} else {
				p.sendMessage(ChatColor.BLUE + "You can't sell less than 1 " + name + "!");
			}
		
		//Reports the error to the player making the transaction and prints the stack trace for analysis.
		} catch (Exception e) {
			e.printStackTrace();
	    	Logger log = Logger.getLogger("Minecraft");
	    	log.info("HyperConomy ERROR #3");
	    	Bukkit.broadcast(ChatColor.DARK_RED + "HyperConomy ERROR #3", "hyperconomy.error");
		}
	
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/**
	 * 
	 * 
	 * Setter for buy() and sell() functions.
	 * 
	 */
	public void setAll(HyperConomy hyperc, int itemid, int itemdata, int itemamount, String itemname, Player player, Economy econ, Calculation cal, Enchant enc, Log lo, Account account, Notify no, InfoSign infosign) {
		hc = hyperc;
		id = itemid;
		data = itemdata;
		amount = itemamount;
		name = itemname;
		p = player;
		economy = econ;
		calc = cal;
		ench = enc;
		log = lo;
		acc = account;
		not = no;
		isign = infosign;
	}
	
	
	/**
	 * 
	 * 
	 * Setter forcountInvitems() function.
	 * 
	 */	
	public void setCount(Player player, int itemid, int itemdata, Calculation cal, Enchant enc) {
		id = itemid;
		data = itemdata;
		p = player;
		ench = enc;
		calc = cal;
	}
	
	
	/**
	 * 
	 * 
	 * Setter for getavailableSlots() function.
	 * 
	 */	
	public void setSlots(Player player) {
		p = player;
	}
	
	
	/**
	 * 
	 * 
	 * Setter gettaxPaid() function.
	 * 
	 */	
	public void settaxPaid(HyperConomy hyperc, String itemname) {
		hc = hyperc;
		name = itemname;
	}
	

	//Transaction fields.
	private HyperConomy hc;
	private int id;
	private int data;
	private int amount;
	private String name;
	private Player p;
	private Economy economy;
	private Log log;
	private Account acc;
	private Notify not;
	private InfoSign isign;
	
	
	Calculation calc;
	Enchant ench;
	
}
