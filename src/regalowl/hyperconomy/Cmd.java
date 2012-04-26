package regalowl.hyperconomy;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.SortedMap;
import java.util.TreeMap;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;

public class Cmd {

	//Command fields
	private Player player;
	private String name;
	private Economy economy;
	private Message m;
	
	
	
	//Reused objects
	private HyperConomy hc;
	private Transaction tran;
	private Calculation calc;
	private Enchant ench;
	private Log l;
	private Shop s;
	private Account acc;
	private InfoSign isign;
	private Notify not;
	
	private int renameshopint;
	private String renameshopname;
	
	
	//Constructor for server start.
	Cmd() {
		renameshopint = 0;
		renameshopname = "";
	}
	
	
	public void setCmd(HyperConomy hyperc, Economy e, Message message, Transaction transaction, Calculation cal, Enchant en, Log log, Shop sh, Account account, InfoSign isig, Notify no) {
		hc = hyperc;
		economy = e;
		m = message;
		tran = transaction;
		calc = cal;
		ench = en;
		l = log;
		s = sh;
		acc = account;
		isign = isig;
		not = no;
	}
	

	
	public boolean handleCommand(CommandSender sender, Command cmd, String label, String[] args) {
		player = null;
		if (sender instanceof Player) {
    		player = (Player) sender;
    	}

    	if (cmd.getName().equalsIgnoreCase("buy") && (player != null)){
    		try {
    			s.setinShop(player);
    			if (s.inShop() != -1) {	
    				name = args[0];
    				int amount = 0;
    				String teststring = hc.getYaml().getItems().getString(name);
		
    				if (teststring == null) {
    					name = hc.fixName(name);
    					teststring = hc.getYaml().getItems().getString(name);
    				}
    				int id = hc.getYaml().getItems().getInt(name + ".information.id");
    				int data = hc.getYaml().getItems().getInt(name + ".information.data");	

    				if (teststring != null) {
    				if (args.length == 1) {
    					amount = 1;
    				} else {
    					
    					try {
    						amount = Integer.parseInt(args[1]);
    					} catch (Exception e) {
    						String max = args[1];
    						if (max.equalsIgnoreCase("max")) {
    							tran.setSlots(player);
    							int space = tran.getavailableSlots();

    							
    							MaterialData md = new MaterialData(id, (byte) data);
    							ItemStack stack = md.toItemStack();				
    							int maxstack = stack.getMaxStackSize();
    							amount = space * maxstack;
    							
    	    					int shopstock = hc.getYaml().getItems().getInt(name + ".stock.stock");	
    	    					//Buys the most possible from the shop if the amount is more than that for max.
    	    					if (amount > shopstock) {
    	    						amount = shopstock;
    	    					}
    							
    							
    						} else {
    							m.send(player, 0);
    							return true;
    						}
    						
    					}
		
    				}	

    				}
    				
    				
    			if (teststring != null) {
    				if (s.has(s.getShop(player), name)) {	
	    				tran.setAll(hc, id, data, amount, name, player, economy, calc, ench, l, acc, not, isign);
	    				tran.buy();
					} else {
						m.send(player, 95);
					}
    				
    				
    			} else {
    				m.send(player, 1);
    			}
    			} else {
    				m.send(player, 2);
    			}
    		return true;
    		} catch (Exception e) {
    			m.send(player, 0);
    		}
    		
	
    		
    		
    	} else if (cmd.getName().equalsIgnoreCase("sell") && (player != null)) {
    		try {    			
    			s.setinShop(player);
    			if (s.inShop() != -1) {
    			name = args[0];
    			int amount = 0;
				String teststring = hc.getYaml().getItems().getString(name);
				if (teststring == null) {
					name = hc.fixName(name);
					teststring = hc.getYaml().getItems().getString(name);
				}

				if (teststring != null) {
				if (args.length == 1) {
					amount = 1;
				} else {
					
					try {
						amount = Integer.parseInt(args[1]);
					} catch (Exception e) {
						String max = args[1];
						if (max.equalsIgnoreCase("max")) {
							

		    				tran.setCount(player, hc.getYaml().getItems().getInt(name + ".information.id"), hc.getYaml().getItems().getInt(name + ".information.data"), calc, ench);
		    				amount = tran.countInvitems();

		    				
						} else {
							m.send(player, 3);
							return true;
						}
						
					}
	
				}	

				}

    			if (teststring != null) {
    				
    				if (s.has(s.getShop(player), name)) {	
        				tran.setAll(hc, hc.getYaml().getItems().getInt(name + ".information.id"), hc.getYaml().getItems().getInt(name + ".information.data"), amount, name, player, economy, calc, ench, l, acc, not, isign);
        				tran.sell();
					} else {
						m.send(player, 95);
					}
    				
    			} else {
    				m.send(player, 1);
    			}	
    			} else {
    				m.send(player, 2);
    			}
    			return true;
    		} catch (Exception e) {
    			m.send(player, 3);
    		}   
    		
    		
    		
    		
    		
    		
    		
    		
    		

    	} else if (cmd.getName().equalsIgnoreCase("sellall") && (player != null)) {
    		try {    			
    			s.setinShop(player);
    			if (s.inShop() != -1) {
    			if (args.length == 0) {
    				
    				int slotn = 0;
    				Inventory invent = player.getInventory();
    				
    				
    				int heldslot = player.getInventory().getHeldItemSlot();
    				

    				int itd = 0;
    				//Sells the held item slot first.
    				if (invent.getItem(heldslot) != null) {
    					itd = invent.getItem(heldslot).getTypeId();
    				}
    				if (itd != 0) {
    					calc.setPDV(invent.getItem(heldslot));
    					int da = calc.getpotionDV();

    					
    					

    					
    					
    	    			//int amount = invent.getItem(heldslot).getAmount();
    	    			calc.setNdata(itd, da);
    	    			int newdat = calc.newData();
    	    			String ke = itd + ":" + newdat;
    	    			String nam = hc.getnameData(ke);
    	    			
    	    			
    	    			
    	    			//experimental
    					tran.setCount(player, itd, newdat, calc, ench);
	    				int amount = tran.countInvitems();
    	    			
    	    			
    	    			
    	    			
    	    			if (nam != null) {
    	    				
    	    				if (s.has(s.getShop(player), nam)) {	
        	    				tran.setAll(hc, itd, newdat, amount, nam, player, economy, calc, ench, l, acc, not, isign);
        	    				tran.sell();
    						} else {
    							m.send(player, 95);
    						}

    	    			}
    				}
    				

    				
    				//Sells remaining items after the held slot.
    				while (slotn < 36) {
    					if (invent.getItem(slotn) == null) {
    						itd = 0;
    					} else {
    						itd = invent.getItem(slotn).getTypeId();
    					}
        				
        				if (itd != 0) {
        					
        					
        					//Experimental enchantment block
        					ItemStack itemn = invent.getItem(slotn);
        					ench.setHE(itemn);
        					if (ench.hasenchants() == false) {
        					
        						calc.setPDV(invent.getItem(slotn));
        					int da = calc.getpotionDV();

        	    			//int amount = invent.getItem(slotn).getAmount();
        	    			calc.setNdata(itd, da);
        	    			int newdat = calc.newData();
        	    			String ke = itd + ":" + newdat;
        	    			String nam = hc.getnameData(ke);
        	    			if (nam != null) {
        	    				
        	    				
            	    			//experimental
            					tran.setCount(player, itd, newdat, calc, ench);
        	    				int amount = tran.countInvitems();
        	    				

        	    				if (s.has(s.getShop(player), nam)) {	
            	    				tran.setAll(hc, itd, newdat, amount, nam, player, economy, calc, ench, l, acc, not, isign);
            	    				tran.sell();
        						} else {
        							m.send(player, 95);
        						}
        	    			}
        	    			
        	    			
        					} else {
        						m.send(player, 4);
        					}
        	    				    			
        				}
    				slotn++;
    				}
    			} else {
    				m.send(player, 5);
    			}
    			} else {
    				m.send(player, 2);
    			}
    			return true;
    		} catch (Exception e) {
    			m.send(player, 5);
    		}   
    		
    		
    		
    		
    		
    		
    		
    		
    		
    		
    		
    		
    		
    		
    		
    		
    	} else if (cmd.getName().equalsIgnoreCase("value")) {
    		
		    	try {
		    		
	        		name = args[0];
	        		int amount;
	        			
	        		if (args.length == 2) {
	        			amount = Integer.parseInt(args[1]);
	        		} else {
	        			amount = 1;
	        		}
	        				
	    		String teststring = hc.getYaml().getItems().getString(name);
				if (teststring == null) {
					name = hc.fixName(name);
					teststring = hc.getYaml().getItems().getString(name);
				}
	    		if (teststring != null) {
	    			calc.setVC(hc, player, amount, name, ench);
	    			double val = calc.getTvalue();
	    			m.send(sender, 6);		
	    			sender.sendMessage(ChatColor.GREEN + "" + amount + ChatColor.AQUA + " " + name + ChatColor.BLUE + " can be sold for: " + ChatColor.GREEN + "$" + val);    				
	    			calc.setVC(hc, player, amount, name, ench);
	    			double cost = calc.getCost();
	    			
					String scost = "";
					if (cost > Math.pow(10, 10)) {
						scost = "INFINITY";
					} else {
						scost = cost + "";
					}
	    			
	    			sender.sendMessage(ChatColor.GREEN + "" + amount + ChatColor.AQUA + " " + name + ChatColor.BLUE + " can be purchased for: " + ChatColor.GREEN + "$" + scost);
					sender.sendMessage(ChatColor.BLUE + "The global shop currently has " + ChatColor.GREEN + "" + hc.getYaml().getItems().getInt(name + ".stock.stock") + ChatColor.AQUA + " " + name + ChatColor.BLUE + " available.");
					m.send(sender, 6);
	    				
	    		} else {
	    			m.send(sender, 1);
	    		}    			
	    		return true;
	    	} catch (Exception e) {
	    		m.send(sender, 7);
	    	}

    		
    		
    		
    		
    		
    		
    		
    		
    	} else if (cmd.getName().equalsIgnoreCase("hb") && (player != null)) {
    		int amount;
    		boolean ma = false;
    		try {
    			s.setinShop(player);
    			if (s.inShop() != -1) {
    				
    				
					//Experimental enchantment block
					ItemStack iinhand = player.getItemInHand();
					ench.setHE(iinhand);
					if (ench.hasenchants() == false) {
    				
    				
    				if (args.length == 0) {
    					amount = 1;
    				} else {
    					
    					try {
    						amount = Integer.parseInt(args[0]);
    					} catch (Exception e) {
    						String max = args[0];
    						if (max.equalsIgnoreCase("max")) {
    							ma = true;
    							tran.setSlots(player);
    							int space = tran.getavailableSlots();
    							int maxstack = iinhand.getMaxStackSize();
    							
    							
    							amount = space * maxstack;
    							
    							
    						} else {
    							m.send(player, 8);
    							return true;
    						}
    						
    					}
    					
    					
    					
    					
    				}	
    				
    				int itd = player.getItemInHand().getTypeId();
    				
    				calc.setPDV(player.getItemInHand());
    				int da = calc.getpotionDV();

    				calc.setNdata(itd, da);
    				int newdat = calc.newData();
    				
    				String ke = itd + ":" + newdat;
    				String nam = hc.getnameData(ke);
    				if (nam == null) {
    					m.send(player, 9);
    				} else {
    					
    					
    					int shopstock = hc.getYaml().getItems().getInt(nam + ".stock.stock");	
    					//Buys the most possible from the shop if the amount is more than that for max.
    					if (amount > shopstock && ma) {
    						amount = shopstock;
    					}
    					

	    				if (s.has(s.getShop(player), nam)) {	
	    					tran.setAll(hc, itd, newdat, amount, nam, player, economy, calc, ench, l, acc, not, isign);
	        				tran.buy();
						} else {
							m.send(player, 95);
						}
    					

    				}
    				
				} else {
					m.send(player, 4);
				}
    				
    				
    			} else {
    				m.send(player, 2);
    			}
    			return true;
    		} catch (Exception e) {
    			m.send(player, 8);
    		}
    		
    		
    		
    		
    		
    		
    		
    	} else if (cmd.getName().equalsIgnoreCase("buyxp") && (player != null)) {
    		try {
    			s.setinShop(player);
    			if (s.inShop() != -1) {
    				if (args.length <= 1) {
    					int amount;
    					if (args.length == 0) {
    						amount = 1;
    					} else {
    						amount = Integer.parseInt(args[0]);
    					}
        				String ke = -1 + ":" + -1;
        				String nam = hc.getnameData(ke);
        				tran.setAll(hc, -1, -1, amount, nam, player, economy, calc, ench, l, acc, not, isign);
        				tran.buyXP();
        				
    				} else {
    					player.sendMessage(ChatColor.DARK_RED + "Invalid Parameters.  Use /buyxp (amount)");
    				}
    			} else {
    				m.send(player, 2);
    			}
    			return true;
    		} catch (Exception e) {
    			player.sendMessage(ChatColor.DARK_RED + "Invalid Parameters.  Use /buyxp (amount)");
    		}
    		
    		
    		
    	} else if (cmd.getName().equalsIgnoreCase("sellxp") && (player != null)) {
    		try {
    			s.setinShop(player);
    			if (s.inShop() != -1) {
    				if (args.length <= 1) {
    					int amount;
    					if (args.length == 0) {
    						amount = 1;
    					} else {
    						try {
    							amount = Integer.parseInt(args[0]);
    						} catch (Exception e) {
    							if (args[0].equalsIgnoreCase("max")) {
    								amount = calc.gettotalxpPoints(player);
    							} else {
    								player.sendMessage(ChatColor.DARK_RED + "Invalid Parameters.  Use /buyxp (amount)");
    								return true;
    							}
    						}
    					}
        				String ke = -1 + ":" + -1;
        				String nam = hc.getnameData(ke);
        				tran.setAll(hc, -1, -1, amount, nam, player, economy, calc, ench, l, acc, not, isign);
        				tran.sellXP();
        				
    				} else {
    					player.sendMessage(ChatColor.DARK_RED + "Invalid Parameters.  Use /buyxp (amount)");
    				}
    			} else {
    				m.send(player, 2);
    			}
    			return true;
    		} catch (Exception e) {
    			player.sendMessage(ChatColor.DARK_RED + "Invalid Parameters.  Use /buyxp (amount)");
    		}
    		
    		
    		
    		
    		
    	} else if (cmd.getName().equalsIgnoreCase("xpinfo") && (player != null)) {

    		try {
    			
    			if (args.length == 0) {
    				
    				int totalexp = calc.gettotalxpPoints(player);
    				int lvl = player.getLevel();
    				int xpfornextlvl = calc.getxpfornextLvl(lvl) - calc.getbarxpPoints(player);
    				
    				int xpfor50 = calc.getlvlxpPoints(50) - totalexp;
    				
    				m.send(player, 6);
    				player.sendMessage(ChatColor.BLUE + "Total Experience Points: " + ChatColor.GREEN + "" + totalexp);
    				player.sendMessage(ChatColor.BLUE + "Experience Needed For The Next Level: " + ChatColor.GREEN + "" + xpfornextlvl);
    				player.sendMessage(ChatColor.BLUE + "Experience Needed For Level 50: " + ChatColor.GREEN + "" + xpfor50);
    				m.send(player, 6);
    			} else {
    				player.sendMessage(ChatColor.DARK_RED + "Invalid Parameters.  Use /xpinfo");
    			}

    			return true;
    		} catch (Exception e) {
    			player.sendMessage(ChatColor.DARK_RED + "Invalid Parameters.  Use /xpinfo");
    			return true;
    		}
    		
    		
    		
    		
    		
    		
    	} else if (cmd.getName().equalsIgnoreCase("buyid") && (player != null)) {
    		int amount;
    		int itd;
    		int da = 0;
    		try {
    			s.setinShop(player);
    			if (s.inShop() != -1) {
    				
    				if (args.length == 2) {
    					amount = Integer.parseInt(args[0]);
    					itd = Integer.parseInt(args[1]);
    				} else if (args.length == 3) {
    					amount = Integer.parseInt(args[0]);
    					itd = Integer.parseInt(args[1]);
    					da = Integer.parseInt(args[2]);
    				} else {
    					m.send(player, 10);
    					return false;
    				}
	
    				String ke = itd + ":" + da;
    				String nam = hc.getnameData(ke);
    				


    				if (nam == null) {
    					m.send(player, 9);
    				} else {
    					
        				if (s.has(s.getShop(player), nam)) {
        					
	    					tran.setAll(hc, itd, da, amount, nam, player, economy, calc, ench, l, acc, not, isign);
	        				tran.buy();
        				
        				} else {
        					m.send(player, 95);
        				}
    				}
    				
    				
    				
    				
    				
    				
    			} else {
    				m.send(player, 2);
    			}
    			return true;
    		} catch (Exception e) {
    			m.send(player, 10);
    		}
    		
    		
    		

    		
    		
    		
    		
    		
    	} else if (cmd.getName().equalsIgnoreCase("hs") && (player != null)) {
    		int amount;
    		try {
    			s.setinShop(player);
    			if (s.inShop() != -1) {
    				if (args.length == 0) {
    					amount = 1;
    				} else {
    					
    					
    					
    					try {
    					amount = Integer.parseInt(args[0]);
    					
    					
    					
    					} catch (Exception e) { 
    						
    						String max = args[0];
    						if (max.equalsIgnoreCase("max")) {
    							
    							int itmid = player.getItemInHand().getTypeId();
    							
    							calc.setPDV(player.getItemInHand());
    							int da = calc.getpotionDV();
    		    				

    		    				calc.setNdata(itmid, da);
    		    				int newdat = calc.newData();
    							
    							
    							
    							
    							
    		    				tran.setCount(player, itmid, newdat, calc, ench);
    		    				amount = tran.countInvitems();
    						} else {
    							m.send(player, 11);
    							return true;
    						}
    					}
    					
    				}	
    				int itd = player.getItemInHand().getTypeId();

    				calc.setPDV(player.getItemInHand());
    				int da = calc.getpotionDV();
    				//Gets the correct damage value if its a potion.
    				

    				calc.setNdata(itd, da);
    				int newdat = calc.newData();
    				

    				
    				String ke = itd + ":" + newdat;
    				String nam = hc.getnameData(ke);
    				
    				
    				if (nam == null) {
    					m.send(player, 12);
    				} else {
    					
    					//Experimental enchantment block
    					ItemStack iinhand = player.getItemInHand();
    					ench.setHE(iinhand);
    					if (ench.hasenchants() == false) {
    					

        				
	    				if (s.has(s.getShop(player), nam)) {	
	    					tran.setAll(hc, itd, newdat, amount, nam, player, economy, calc, ench, l, acc, not, isign);
	        				tran.sell();
						} else {
							m.send(player, 95);
						}

    					
    					} else {
    						m.send(player, 4);
    					}
        				
        				
        				
    				}
    			} else {
    				m.send(player, 2);
    			}
    			return true;
    		} catch (Exception e) {
    			m.send(player, 11);
    		}
    		
    		
    		
    		
    		
    		
    		
    		
    		
    		
    		
    		
    		
    		
    		
    		
    		
    		
    		
    		
    		
    		
    		
    	} else if (cmd.getName().equalsIgnoreCase("hv") && (player != null)) {
    		int amount;
    		try {
    			
				ItemStack iinhand = player.getItemInHand();
				ench.setHE(iinhand);
				if (ench.hasenchants() == false) {

    			
    			

    				if (args.length == 0) {
    					amount = 1;
    				} else {
    					amount = Integer.parseInt(args[0]);
    				}	
    				int itd = player.getItemInHand().getTypeId();
    				calc.setPDV(player.getItemInHand());
    				int da = calc.getpotionDV();
    

    				
    				calc.setNdata(itd, da);
    				
    				
    				int newdat = calc.newData();
    				String ke = itd + ":" + newdat;
    				String nam = hc.getnameData(ke);
    				if (nam == null) {
    					m.send(player, 9);
    				} else {
    					calc.setVC(hc, player, amount, nam, ench);
    					double val = calc.getValue();
    					

    					calc.setTID(itd);
    					if (calc.testId() && amount > 1) {
    						tran.setCount(player, itd, player.getItemInHand().getData().getData(), calc, ench);
    						int numberofitem = tran.countInvitems();

    						
    						
    						if (amount - numberofitem > 0) {
    						
    						int addamount = amount - numberofitem;
    							
    						calc.setVC(hc, player, addamount, nam, ench);
    						val = val + calc.getTvalue();
    						}
    					}
    					
    					
    					m.send(player, 6);
    					player.sendMessage(ChatColor.GREEN + "" + amount + ChatColor.AQUA + " " + nam + ChatColor.BLUE + " can be sold for: " + ChatColor.GREEN + "$" + val);
    					
    					
    					
    					calc.setVC(hc, player, amount, nam, ench);
    					double cost = calc.getCost();
    					

    					String scost = "";
    					if (cost > Math.pow(10, 10)) {
    						scost = "INFINITY";
    					} else {
    						scost = cost + "";
    					}

    					
    					
    					player.sendMessage(ChatColor.GREEN + "" + amount + ChatColor.AQUA + " " + nam + ChatColor.BLUE + " can be purchased for: " + ChatColor.GREEN + "$" + scost);
    					player.sendMessage(ChatColor.BLUE + "The global shop currently has " + ChatColor.GREEN + "" + hc.getYaml().getItems().getInt(nam + ".stock.stock") + ChatColor.AQUA + " " + nam + ChatColor.BLUE + " available.");
    					m.send(player, 6);
    				}
    				
				} else {
					m.send(player, 4);
				}

    			return true;
    		} catch (Exception e) {
    			m.send(player, 13);
    		}
    		
    		
    		
    		
    		
    		
    		
    		
    		
    		
    		
    		
    		
    		
    		
    	} else if (cmd.getName().equalsIgnoreCase("settax")) {
    		int amount = 0;
    		try {
    				if (args.length == 0) {
    					m.send(sender, 14);
    				} else if (args.length == 1) {
    					amount = Integer.parseInt(args[0]);
    					hc.getYaml().getConfig().set("config.purchasetaxpercent", amount);
    					m.send(sender, 15);
    					
						//Updates all information signs.
						isign.setrequestsignUpdate(true);
						isign.checksignUpdate();
    				} else {
    					m.send(sender, 14);
    				}
    			return true;
    		} catch (Exception e) {
    			m.send(sender, 14);
    		}
    		
    		
    		
    		
    		
    		
    		
    		
    	} else if (cmd.getName().equalsIgnoreCase("setinitialtax")) {
    		int amount = 0;
    		try {
    				if (args.length == 0) {
    					m.send(sender, 16);
    				} else if (args.length == 1) {
    					amount = Integer.parseInt(args[0]);
    					hc.getYaml().getConfig().set("config.initialpurchasetaxpercent", amount);
    					m.send(sender, 17);
    					
						//Updates all information signs.
    					isign.setrequestsignUpdate(true);
    					isign.checksignUpdate();
    				} else {
    					m.send(sender, 16);
    				}
    			return true;
    		} catch (Exception e) {
    			m.send(sender, 16);
    		}
    		
    		
    		
    		
    	} else if (cmd.getName().equalsIgnoreCase("setstatictax")) {
    		int amount = 0;
    		try {
    				if (args.length == 0) {
    					m.send(sender, 18);
    				} else if (args.length == 1) {
    					amount = Integer.parseInt(args[0]);
    					hc.getYaml().getConfig().set("config.statictaxpercent", amount);
    					m.send(sender, 19);
    					
						//Updates all information signs.
    					isign.setrequestsignUpdate(true);
    					isign.checksignUpdate();
    				} else {
    					m.send(sender, 18);
    				}
    			return true;
    		} catch (Exception e) {
    			m.send(sender, 18);
    		}
    		
    		
    		
    	} else if (cmd.getName().equalsIgnoreCase("setenchanttax")) {
    		int amount = 0;
    		try {
    				if (args.length == 0) {
    					m.send(sender, 20);
    				} else if (args.length == 1) {
    					amount = Integer.parseInt(args[0]);
    					hc.getYaml().getConfig().set("config.enchanttaxpercent", amount);
    					m.send(sender, 21);
    					
						//Updates all information signs.
    					isign.setrequestsignUpdate(true);
    					isign.checksignUpdate();
    				} else {
    					m.send(sender, 20);
    				}
    			return true;
    		} catch (Exception e) {
    			m.send(sender, 20);
    		}
    		
    		
    		
    		
    	} else if (cmd.getName().equalsIgnoreCase("setclassvalue")) {
    		try {
    				if (args.length != 2) {
    					m.send(sender, 22);
    				} else {
    					String classtype = args[0];
    					if (hc.getYaml().getConfig().get("config.enchantment.classvalue." + classtype) != null) {
    					double value = Double.parseDouble(args[1]);
    					hc.getYaml().getConfig().set("config.enchantment.classvalue." + classtype, value);
    					sender.sendMessage(ChatColor.BLUE + "The classvalue for " + ChatColor.AQUA + "" + classtype + ChatColor.BLUE + " has been set.");
    					
						//Updates all information signs.
    					isign.setrequestsignUpdate(true);
    					isign.checksignUpdate();
    					} else {
    						m.send(sender, 23);
    					}
    				}
    			return true;
    		} catch (Exception e) {
    			m.send(sender, 22);
    		}
    		
    		
    	} else if (cmd.getName().equalsIgnoreCase("classvalues")) {
    		try {
    			m.send(sender, 6);
				sender.sendMessage(ChatColor.BLUE + "Bow Value: " + ChatColor.GREEN + "" + hc.getYaml().getConfig().getDouble("config.enchantment.classvalue.bow"));
				sender.sendMessage(ChatColor.BLUE + "Wood Value: " + ChatColor.GREEN + "" + hc.getYaml().getConfig().getDouble("config.enchantment.classvalue.wood"));
				sender.sendMessage(ChatColor.BLUE + "Leather Value: " + ChatColor.GREEN + "" + hc.getYaml().getConfig().getDouble("config.enchantment.classvalue.leather"));
				sender.sendMessage(ChatColor.BLUE + "Stone Value: " + ChatColor.GREEN + "" + hc.getYaml().getConfig().getDouble("config.enchantment.classvalue.stone"));
				sender.sendMessage(ChatColor.BLUE + "Chainmail Value: " + ChatColor.GREEN + "" + hc.getYaml().getConfig().getDouble("config.enchantment.classvalue.chainmail"));
				sender.sendMessage(ChatColor.BLUE + "Iron Value: " + ChatColor.GREEN + "" + hc.getYaml().getConfig().getDouble("config.enchantment.classvalue.iron"));
				sender.sendMessage(ChatColor.BLUE + "Gold Value: " + ChatColor.GREEN + "" + hc.getYaml().getConfig().getDouble("config.enchantment.classvalue.gold"));
				sender.sendMessage(ChatColor.BLUE + "Diamond Value: " + ChatColor.GREEN + "" + hc.getYaml().getConfig().getDouble("config.enchantment.classvalue.diamond"));
				m.send(sender, 6);
    		      
    			return true;
    		} catch (Exception e) {
    			m.send(sender, 24);
    		}
    		
    		
	
    		
    	} else if (cmd.getName().equalsIgnoreCase("setvalue")) {
    		try {
    			if (args.length == 2) {
        			name = args[0];
        			double value = Double.parseDouble(args[1]);    		
    			String teststring = hc.getYaml().getItems().getString(name);   
				if (teststring == null) {
					name = hc.fixName(name);
					teststring = hc.getYaml().getItems().getString(name);
				}
    			if (teststring != null) {
    				
    				

    				hc.getYaml().getItems().set(name + ".value", value);
    				sender.sendMessage(ChatColor.GOLD + "" + name + " value set!");
    				
					//Updates all information signs.
    				isign.setrequestsignUpdate(true);
    				isign.checksignUpdate();
    			} else {
    			m.send(sender, 1);
    			}    	
    			} else if (args.length == 3) {
    				String ench = args[2];
					if (ench.equalsIgnoreCase("e")) {
						
						name = args[0];
	        			double value = Double.parseDouble(args[1]);    		
	    			String teststring = hc.getYaml().getEnchants().getString(name);    		
    				if (teststring == null) {
    					name = hc.fixName(name);
    					teststring = hc.getYaml().getEnchants().getString(name);
    				}
	    			if (teststring != null) {
	    				
	    				

	    				hc.getYaml().getEnchants().set(name + ".value", value);
	    				
	    				sender.sendMessage(ChatColor.GOLD + "" + name + " value set!");
	    				
						//Updates all information signs.
	    				isign.setrequestsignUpdate(true);
	    				isign.checksignUpdate();
	    			} else {
	    			m.send(sender, 27);
	    			}    	
						
						
					} else {
						m.send(sender, 28);
					}
    			} else {
    				m.send(sender, 28);
    			}
	
    			return true;
    		} catch (Exception e) {
    			m.send(sender, 28);
    		}
    		
    		
    		
    		
    		
    		
    		
    		
    		
    		
    	} else if (cmd.getName().equalsIgnoreCase("setstock")) {
    		try {
    			
    			if (args.length == 2) {
        			name = args[0];
        			int stock = Integer.parseInt(args[1]);    		
    			String teststring = hc.getYaml().getItems().getString(name);    
				if (teststring == null) {
					name = hc.fixName(name);
					teststring = hc.getYaml().getItems().getString(name);
				}
    			if (teststring != null) {

    				hc.getYaml().getItems().set(name + ".stock.stock", stock);
    				
    				sender.sendMessage(ChatColor.GOLD + "" + name + " stock set!");
    				
					//Updates all information signs.
    				isign.setrequestsignUpdate(true);
    				isign.checksignUpdate();
    			} else {
    				m.send(sender, 1);
    			}   
    			} else if (args.length == 3) {
    				String ench = args[2];
					if (ench.equalsIgnoreCase("e")) {
						
						
						
						name = args[0];
	        			int stock = Integer.parseInt(args[1]);    		
	    			String teststring = hc.getYaml().getEnchants().getString(name); 
    				if (teststring == null) {
    					name = hc.fixName(name);
    					teststring = hc.getYaml().getEnchants().getString(name);
    				}
	    			if (teststring != null) {
	    				
	    				

	    				hc.getYaml().getEnchants().set(name + ".stock.stock", stock);
	    				
	    				sender.sendMessage(ChatColor.GOLD + "" + name + " stock set!");
	    				
						//Updates all information signs.
	    				isign.setrequestsignUpdate(true);
	    				isign.checksignUpdate();
	    			} else {
	    				m.send(sender, 27);
	    			}   
						
						
						
					} else {
						m.send(sender, 29);
					}
    			} else {
    				m.send(sender, 29);
    			}		
    			
    			return true;
    		} catch (Exception e) {
    			m.send(sender, 29);
    		}
    		
    		
    		
    		
    		
    		
    		
    		
    		
    		
    		
    		
    		
    		
    		
    	} else if (cmd.getName().equalsIgnoreCase("setmedian")) {
    		try {
    			
    			if (args.length == 2) {
    			
        			name = args[0];
        			int median = Integer.parseInt(args[1]);    		
    			String teststring = hc.getYaml().getItems().getString(name);    
				if (teststring == null) {
					name = hc.fixName(name);
					teststring = hc.getYaml().getItems().getString(name);
				}
    			if (teststring != null) {
    				
    				

    				hc.getYaml().getItems().set(name + ".stock.median", median);
    				
    				sender.sendMessage(ChatColor.GOLD + "" + name + " median set!");
    				
					//Updates all information signs.
    				isign.setrequestsignUpdate(true);
    				isign.checksignUpdate();
    			} else {
    				m.send(sender, 1);
    			}    
    			} else if (args.length == 3) {
    				String ench = args[2];
					if (ench.equalsIgnoreCase("e")) {
						
						
						
	        			name = args[0];
	        			int median = Integer.parseInt(args[1]);    		
	    			String teststring = hc.getYaml().getEnchants().getString(name); 
    				if (teststring == null) {
    					name = hc.fixName(name);
    					teststring = hc.getYaml().getEnchants().getString(name);
    				}
	    			if (teststring != null) {
	    				
	    				

	    				hc.getYaml().getEnchants().set(name + ".stock.median", median);
	    				
	    				sender.sendMessage(ChatColor.GOLD + "" + name + " median set!");
	    				
						//Updates all information signs.
	    				isign.setrequestsignUpdate(true);
	    				isign.checksignUpdate();
	    			} else {
	    				m.send(sender, 27);
	    			} 
	
						
					} else {
						m.send(sender, 30);
					}
    			} else {
    				m.send(sender, 30);
    			}
		
    			return true;
    		} catch (Exception e) {
    			m.send(sender, 30);
    		}	
    		
    		
    		
    		
    		
    		
    		
    		
    		
    		
    		
    		
    		
    		
    		
    		
    		
    	} else if (cmd.getName().equalsIgnoreCase("setstatic")) {
    		try {
    			
    			if (args.length == 1) {
        			name = args[0];
     		
    			String teststring = hc.getYaml().getItems().getString(name);   
				if (teststring == null) {
					name = hc.fixName(name);
					teststring = hc.getYaml().getItems().getString(name);
				}
    			if (teststring != null) {
    				boolean nstatus;
    				boolean sstatus = hc.getYaml().getItems().getBoolean(name + ".price.static");
    				
    				if (sstatus) {
    					nstatus = false;
    					sender.sendMessage(ChatColor.GOLD + "" + name + " will now use a dynamic price.");
    				} else {
    					nstatus = true;
    					sender.sendMessage(ChatColor.GOLD + "" + name + " will now use a static price.");
    				}

    				hc.getYaml().getItems().set(name + ".price.static", nstatus);
					//Updates all information signs.
    				isign.setrequestsignUpdate(true);
    				isign.checksignUpdate();
    				
    			} else {
    				m.send(sender, 1);
    			}   
    			} else if (args.length == 2) {
    				String ench = args[1];
					if (ench.equalsIgnoreCase("e")) {
						
						
						
	        			name = args[0];
	             		
	        			String teststring = hc.getYaml().getEnchants().getString(name);  
        				if (teststring == null) {
        					name = hc.fixName(name);
        					teststring = hc.getYaml().getEnchants().getString(name);
        				}
	        			if (teststring != null) {
	        				boolean nstatus;
	        				boolean sstatus = hc.getYaml().getEnchants().getBoolean(name + ".price.static");
	        				
	        				if (sstatus) {
	        					nstatus = false;
	        					sender.sendMessage(ChatColor.GOLD + "" + name + " will now use a dynamic price.");
	        				} else {
	        					nstatus = true;
	        					sender.sendMessage(ChatColor.GOLD + "" + name + " will now use a static price.");
	        				}

	        				hc.getYaml().getEnchants().set(name + ".price.static", nstatus);
							//Updates all information signs.
	        				isign.setrequestsignUpdate(true);
	        				isign.checksignUpdate();
	        				
	        			} else {
	        				m.send(sender, 27);
	        			} 
	
					} else {
						m.send(sender, 31);
					}
    			} else {
    				m.send(sender, 31);
    			}

    			return true;
    		} catch (Exception e) {
    			m.send(sender, 31);
    		}	
    		
    		
    		
    		
    		
    		
    		
    		
    	} else if (cmd.getName().equalsIgnoreCase("setinitiation")) {
    		try {
    			
    			
    			if (args.length == 1) {
    				
        			name = args[0];     		
    			String teststring = hc.getYaml().getItems().getString(name); 
				if (teststring == null) {
					name = hc.fixName(name);
					teststring = hc.getYaml().getItems().getString(name);
				}
    			if (teststring != null) {
    				boolean nstatus;
    				boolean istatus = hc.getYaml().getItems().getBoolean(name + ".initiation.initiation");    				
    				if (istatus) {
    					nstatus = false;
    					sender.sendMessage(ChatColor.GOLD + "Initiation price is set to false for " + name);
    				} else {
    					nstatus = true;
    					sender.sendMessage(ChatColor.GOLD + "Initiation price is set to true for " + name);
    				}

    				hc.getYaml().getItems().set(name + ".initiation.initiation", nstatus);
					//Updates all information signs.
    				isign.setrequestsignUpdate(true);
    				isign.checksignUpdate();
					
    			} else {
    				m.send(sender, 1);
    			}    
    			
    			} else if (args.length == 2) {
    				String ench = args[1];
					if (ench.equalsIgnoreCase("e")) {
						
	        			name = args[0];     		
	        			String teststring = hc.getYaml().getEnchants().getString(name);  
        				if (teststring == null) {
        					name = hc.fixName(name);
        					teststring = hc.getYaml().getEnchants().getString(name);
        				}
	        			if (teststring != null) {
	        				boolean nstatus;
	        				boolean istatus = hc.getYaml().getEnchants().getBoolean(name + ".initiation.initiation");    				
	        				if (istatus) {
	        					nstatus = false;
	        					sender.sendMessage(ChatColor.GOLD + "Initiation price is set to false for " + name);
	        				} else {
	        					nstatus = true;
	        					sender.sendMessage(ChatColor.GOLD + "Initiation price is set to true for " + name);
	        				}

	        				hc.getYaml().getEnchants().set(name + ".initiation.initiation", nstatus);
							//Updates all information signs.
	        				isign.setrequestsignUpdate(true);
	        				isign.checksignUpdate();
	        			} else {
	        				m.send(sender, 27);
	        			}

						
					} else {
						m.send(sender, 32);
					}
    			} else {
    				m.send(sender, 32);
    			}
			
    			return true;
    		} catch (Exception e) {
    			m.send(sender, 32);
    		}	
    		
    		
    		
    		
    		
    		
    		
    		
    		
    		
    	} else if (cmd.getName().equalsIgnoreCase("setstaticprice")) {
    		try {
    			if (args.length == 2) {
        			name = args[0];
        			Double staticprice = Double.parseDouble(args[1]);    		
    			String teststring = hc.getYaml().getItems().getString(name);    	
				if (teststring == null) {
					name = hc.fixName(name);
					teststring = hc.getYaml().getItems().getString(name);
				}
    			if (teststring != null) {
    				
    				

    				hc.getYaml().getItems().set(name + ".price.staticprice", staticprice);
    				sender.sendMessage(ChatColor.GOLD + "" + name + " static price set!");
    				
					//Updates all information signs.
    				isign.setrequestsignUpdate(true);
    				isign.checksignUpdate();
    			} else {
    				m.send(sender, 1);
    			}    	
    			} else if (args.length == 3) {
    				String ench = args[2];
					if (ench.equalsIgnoreCase("e")) {
						
	        			name = args[0];
	        			Double staticprice = Double.parseDouble(args[1]);    		
	    			String teststring = hc.getYaml().getEnchants().getString(name);  
    				if (teststring == null) {
    					name = hc.fixName(name);
    					teststring = hc.getYaml().getEnchants().getString(name);
    				}
	    			if (teststring != null) {

	    				hc.getYaml().getEnchants().set(name + ".price.staticprice", staticprice);
	    				sender.sendMessage(ChatColor.GOLD + "" + name + " static price set!");
	    				
						//Updates all information signs.
	    				isign.setrequestsignUpdate(true);
	    				isign.checksignUpdate();
	    			} else {
	    				m.send(sender, 27);
	    			}
						
					}  else {
						m.send(sender, 33);
					}
    				
    			} else {
    				m.send(sender, 33);
    			}

    			return true;
    		} catch (Exception e) {
    			m.send(sender, 33);
    		}	
    		
    		
    		
    		
    
    		
    		
    	} else if (cmd.getName().equalsIgnoreCase("setstartprice")) {
    		try {

    			if (args.length == 2) {
        			name = args[0];
        			double startprice = Double.parseDouble(args[1]);    		
    			String teststring = hc.getYaml().getItems().getString(name);  
				if (teststring == null) {
					name = hc.fixName(name);
					teststring = hc.getYaml().getItems().getString(name);
				}
    			if (teststring != null) {
    				hc.getYaml().getItems().set(name + ".initiation.startprice", startprice);
    				
    				sender.sendMessage(ChatColor.GOLD + "" + name + " start price set!");
    				
					//Updates all information signs.
    				isign.setrequestsignUpdate(true);
    				isign.checksignUpdate();
    			} else {
    			m.send(sender, 1);
    			}   
    			
    			} else if (args.length == 3) {
					String ench = args[2];
					if (ench.equalsIgnoreCase("e")) {
						
	        			name = args[0];
	        			double startprice = Double.parseDouble(args[1]);    		
	    			String teststring = hc.getYaml().getEnchants().getString(name);  
    				if (teststring == null) {
    					name = hc.fixName(name);
    					teststring = hc.getYaml().getEnchants().getString(name);
    				}
	    			if (teststring != null) {
	    				hc.getYaml().getEnchants().set(name + ".initiation.startprice", startprice);
	    				sender.sendMessage(ChatColor.GOLD + "" + name + " start price set!");
	    				
						//Updates all information signs.
	    				isign.setrequestsignUpdate(true);
	    				isign.checksignUpdate();
	    			} else {
	    			m.send(sender, 27);
	    			} 
						
					} else {
						m.send(sender, 34);
					}
    			} else {
    				m.send(sender, 34);
    			}
    			
    			return true;   			
    		} catch (Exception e) {
    			m.send(sender, 34);
    		}	
    		
    		
    		
    		
    		
    	
    	} else if (cmd.getName().equalsIgnoreCase("writeitems")) {
    		try {
        		if (args[0].equalsIgnoreCase("row") || args[0].equalsIgnoreCase("column")) {
					
					if (args.length == 1) {
						
	    			Iterator<String> it2 = hc.getYaml().getItems().getKeys(true).iterator();
	    				while (it2.hasNext()) {   			
	    					Object element2 = it2.next();
	    					String elst2 = element2.toString();    				
	    					if (elst2.indexOf(".") == -1) {
	    						if (args[0].equalsIgnoreCase("column")) {
	    							String itemname = elst2 + "\n";
	    							l.setEntry(itemname);
	        						l.writeItems();
	    						} else if (args[0].equalsIgnoreCase("row")) {
	    							String itemname = elst2 + ",";
	    							l.setEntry(itemname);
	        						l.writeItems();
	    						}
	    					}
	    				}
	    				m.send(sender, 35);
	    				
					} else if (args.length == 2) {
						String ench = args[1];
						if (ench.equalsIgnoreCase("e")) {
						
		    			Iterator<String> it2 = hc.getYaml().getEnchants().getKeys(true).iterator();
	    				while (it2.hasNext()) {   			
	    					Object element2 = it2.next();
	    					String elst2 = element2.toString();    				
	    					if (elst2.indexOf(".") == -1) {
					
	    						if (args[0].equalsIgnoreCase("column")) {
	    							String itemname = elst2 + "\n";
	    							l.setEntry(itemname);
	        						l.writeEnchants();
	    						} else if (args[0].equalsIgnoreCase("row")) {
	    							String itemname = elst2 + ",";
	    							l.setEntry(itemname);
	        						l.writeEnchants();
	    						}
	    					}
	    				}
	    				m.send(sender, 36);
	    				
	    				
						} else {
							m.send(sender, 37);
						}
					} else {
						m.send(sender, 37);
					}

				
        		} else {
        			m.send(sender, 37);
        		}
				
				
				
    			return true;
    		} catch (Exception e) {
    			m.send(sender, 37);
    		}
    		
    		
    		
    		
    		
    		
    		
    		
    		
    		
    		
    		
    		
    		
    		
    		
    	} else if (cmd.getName().equalsIgnoreCase("topitems")) {
	    		try {
	        		String nameshop = "";
	    			if (args.length >= 2) {		
	    				int counter = 1;
	    				String name = "";
	    				while (counter < args.length) {
	    					if (counter == 1) {
	    						name = args[1];
	    					} else {
	    						name = name + "_" + args[counter];
	    					}
	    					counter++;
	    				}
	    				name = name.replace(".", "").replace(":", "");	
	    				String teststring = hc.getYaml().getShops().getString(name);
	    				if (teststring == null) {
	    					name = hc.fixsName(name);
	    				}
	    				
	    				
	    				
	    				String shoplist = s.listShops().toString();
	    				
	    				if (shoplist.contains(name)) {
	    					nameshop = name;
	    				} else {
	    					sender.sendMessage(ChatColor.DARK_RED + "That shop doesn't exist!");
	    					return true;
	    				}
	    			}
	    	    		//hc.getYaml().saveYamls();	
	    			int page;
	        			if (args.length == 0) {
	        				page = 1;
	        			} else {
	        				page = Integer.parseInt(args[0]);
	        			}
	
					SortedMap <Double, String> itemstocks = new TreeMap<Double, String>();
	    			Iterator<String> it2 = hc.getYaml().getItems().getKeys(false).iterator();
	    			while (it2.hasNext()) {   			
	    				Object element = it2.next();
	    				String elst = element.toString();    				
	    					
	    					boolean unavailable = false;
	    					
	    					if (nameshop != "") {
	    												
	    						if (!s.has(nameshop, elst)) {
	    							unavailable = true;
	    						}
	    					}
	    					
	    					if (!unavailable) {
		    					double samount = hc.getYaml().getItems().getDouble(elst + ".stock.stock");
		    					if (samount > 0) {
		    						
		    						while (itemstocks.containsKey(samount)) {
		    							samount = samount + .00001;
		    						}
		    						itemstocks.put(samount, elst);
		    					}
	    					}	
	    			}
	    			int numberpage = page * 10;
	    			int count = 0;
	    			int le = itemstocks.size();
	    			double maxpages = le/10;
	    			maxpages = Math.ceil(maxpages);
	    			int maxpi = (int)maxpages + 1;
	    			sender.sendMessage(ChatColor.RED + "Page " + ChatColor.WHITE + "(" + ChatColor.RED + "" + page + ChatColor.WHITE + "/" + ChatColor.RED + "" + maxpi + ChatColor.WHITE + ")");
	    			
	    			
	    			try {
	    			
	    			while (count < numberpage) {
	    				double lk = itemstocks.lastKey();
	    				if (count > ((page * 10) - 11)) {
	    					sender.sendMessage(ChatColor.WHITE + itemstocks.get(lk) + ChatColor.WHITE + ": " + ChatColor.AQUA + "" + (int)Math.floor(lk));
	    				}
	    				itemstocks.remove(lk);
	    				count++;
	    			}
	    			} catch (Exception e) {
	    				m.send(sender, 38);
	    			}
		
	    			return true;
	    		} catch (Exception e) {
	    			m.send(sender, 39);
	    		}
    		
    		
    		
    		
    		
    	} else if (cmd.getName().equalsIgnoreCase("topenchants")) {
    		try {
    			
    			
    			String nameshop = "";
    			if (args.length >= 2) {		
    				int counter = 1;
    				String name = "";
    				while (counter < args.length) {
    					if (counter == 1) {
    						name = args[1];
    					} else {
    						name = name + "_" + args[counter];
    					}
    					counter++;
    				}
    				name = name.replace(".", "").replace(":", "");	
    				String teststring = hc.getYaml().getShops().getString(name);
    				if (teststring == null) {
    					name = hc.fixsName(name);
    				}
    				String shoplist = s.listShops().toString();
    				
    				if (shoplist.contains(name)) {
    					nameshop = name;
    				} else {
    					sender.sendMessage(ChatColor.DARK_RED + "That shop doesn't exist!");
    					return true;
    				}
    			}
    			
    			
    			
    			int page;
        			if (args.length == 0) {
        				page = 1;
        			} else {
        				page = Integer.parseInt(args[0]);
        			}

				SortedMap <Double, String> enchantstocks = new TreeMap<Double, String>();
    			Iterator<String> it2 = hc.getYaml().getEnchants().getKeys(false).iterator();
    			while (it2.hasNext()) {   			
    				Object element = it2.next();
    				String elst = element.toString();    				
    					
    					boolean unavailable = false;
    					
    					if (nameshop != "") {
    											
    						if (!s.has(nameshop, elst)) {
    							unavailable = true;
    						}
    					}
    					
    					if (!unavailable) {
	    					double samount = hc.getYaml().getEnchants().getDouble(elst + ".stock.stock");
	    					if (samount > 0) {
	    						
	    						while (enchantstocks.containsKey(samount)) {
	    							samount = samount + .00001;
	    						}
	    						enchantstocks.put(samount, elst);
	    					}
    					}
    			}
    			int numberpage = page * 10;
    			int count = 0;
    			int le = enchantstocks.size();
    			double maxpages = le/10;
    			maxpages = Math.ceil(maxpages);
    			int maxpi = (int)maxpages + 1;
    			sender.sendMessage(ChatColor.RED + "Page " + ChatColor.WHITE + "(" + ChatColor.RED + "" + page + ChatColor.WHITE + "/" + ChatColor.RED + "" + maxpi + ChatColor.WHITE + ")");
    			
    			
    			try {
    			
    			while (count < numberpage) {
    				double lk = enchantstocks.lastKey();
    				if (count > ((page * 10) - 11)) {
    					sender.sendMessage(ChatColor.WHITE + enchantstocks.get(lk) + ChatColor.WHITE + ": " + ChatColor.AQUA + "" + (int)Math.floor(lk));
    				}
    				enchantstocks.remove(lk);
    				count++;
    			}
    			} catch (Exception e) {
    				m.send(sender, 38);
    			}
	
    			return true;
    		} catch (Exception e) {
    			m.send(sender, 40);
    		}
    		
    		
    		
    		
    		
    	} else if (cmd.getName().equalsIgnoreCase("browseshop")) {
	    		
    		
    		try {
	    		//Gets the search string.
	    		String input = args[0].toLowerCase();
	    		
	    		//Gets the page number if given.
				int page;
				if (args.length == 1) {
					page = 1;
				} else {
					page = Integer.parseInt(args[1]);
				}
				
				//Gets the shop name if it exists.
	    		String nameshop = null;
				if (args.length >= 3) {		
					int counter = 2;
					String name = "";
					while (counter < args.length) {
						if (counter == 2) {
							name = args[2];
						} else {
							name = name + "_" + args[counter];
						}
						counter++;
					}
					name = name.replace(".", "").replace(":", "");	
					String teststring = hc.getYaml().getShops().getString(name);
					if (teststring == null) {
						name = hc.fixsName(name);
					}
					String shoplist = s.listShops().toString();
					if (shoplist.contains(name)) {
						nameshop = name;
					} else {
						sender.sendMessage(ChatColor.DARK_RED + "That shop doesn't exist!");
						return true;
					}
				}
	    		
	    		
				//Puts all items and enchantments into names.
				ArrayList<String> names = hc.getNames();
				ArrayList<String> rnames = new ArrayList<String>();
				int i = 0;
				while(i < names.size()) {
					String cname = names.get(i);
					if (cname.startsWith(input)) {
						//String itemname = hc.fixName(cname);
						String itemname = cname;
						if (nameshop == null || s.has(nameshop, itemname)) {
							rnames.add(cname);
						}
					}
					i++;
				}
				//Alphabetizes arraylist.
				Collections.sort(rnames, String.CASE_INSENSITIVE_ORDER);
				int numberpage = page * 10;
				int count = 0;
				int rsize = rnames.size();
				double maxpages = rsize/10;
				maxpages = Math.ceil(maxpages);
				int maxpi = (int)maxpages + 1;
				sender.sendMessage(ChatColor.RED + "Page " + ChatColor.WHITE + "(" + ChatColor.RED + "" + page + ChatColor.WHITE + "/" + ChatColor.RED + "" + maxpi + ChatColor.WHITE + ")");

				while (count < numberpage) {
					if (count > ((page * 10) - 11)) {
						if (count < rsize) {
							String iname = rnames.get(count);
							
				            String t = hc.getYaml().getItems().getString(iname + ".stock.stock");
				            String t2 = hc.getYaml().getEnchants().getString(iname + ".stock.stock");
							
				            Double cost = 0.0;
				            int stock = 0;
				            
				            
				            if (t != null) {
								calc.setVC(hc, null, 1, iname, null);
								cost = calc.getCost();
								stock = hc.getYaml().getItems().getInt(iname + ".stock.stock");
							} else if (t2 != null) {
								ench.setVC(hc, iname, "diamond");
								cost = ench.getCost();
								stock = hc.getYaml().getEnchants().getInt(iname + ".stock.stock");
							}
							
							
							sender.sendMessage("§b" + iname + " §9[§a" + stock + " §9available: §a$" + cost + " §9each.]");
						} else {
							sender.sendMessage("You have reached the end.");
							break;
						}
						
					}
					count++;
				}

    		} catch (Exception e) {
    			sender.sendMessage(ChatColor.DARK_RED + "Invalid Parameters.  Use /browseshop [Search string] (page) (shop)");
    			return true;
    		}
    		return true;
    		
    		
    		
    		
    		
    		
    		
    		
    		
    		
    		
    		
    	} else if (cmd.getName().equalsIgnoreCase("iteminfo")) {
    		if (player != null) {
    		try {
    			

    			if (args.length == 1) {
					int givenid = Integer.parseInt(args[0]);
					
					int dv = 0;
					calc.setNdata(givenid, dv);
					int newdat = calc.newData();
					String ke = givenid + ":" + newdat;
					String nam = hc.getnameData(ke);
					
					if (nam == null) {
						nam = "Item not in database.";
					}
					
					m.send(player, 6);
    				player.sendMessage(ChatColor.BLUE + "Name: " + ChatColor.AQUA + "" + nam);
    				m.send(player, 6);
					return true;
				} else if (args.length == 2) {
					int givenid = Integer.parseInt(args[0]);;
					int givendam = Integer.parseInt(args[1]);
					
					
					calc.setNdata(givenid, givendam);
					int newdat = calc.newData();
					String ke = givenid + ":" + newdat;
					String nam = hc.getnameData(ke);
					
					if (nam == null) {
						nam = "Item not in database.";
					}
					m.send(player, 6);
    				player.sendMessage(ChatColor.BLUE + "Name: " + ChatColor.AQUA + "" + nam);
    				m.send(player, 6);
					return true;
				}
    			

        			
    			//Gets the damage value, whether or not its a potion.
    			String mat = player.getItemInHand().getType().toString();
    			int itemid = player.getItemInHand().getTypeId();
    			
    			calc.setPDV(player.getItemInHand());
    			int dv = calc.getpotionDV();
    			
				calc.setNdata(itemid, dv);
				int newdat = calc.newData();
				String ke = itemid + ":" + newdat;
				String nam = hc.getnameData(ke);

				if (nam == null) {
					nam = "Item not in database.";
				}
    			

    			String enchantments = "";
    			ench.setHE(player.getItemInHand());
				if (ench.hasenchants()) {
				Iterator<Enchantment> ite = player.getItemInHand().getEnchantments().keySet().iterator();
				while (ite.hasNext()) {
					String rawstring = ite.next().toString();
					String enchname = rawstring.substring(rawstring.indexOf(",") + 2, rawstring.length() - 1);
					Enchantment en = null;
					en = Enchantment.getByName(enchname);
					int lvl = player.getItemInHand().getEnchantmentLevel(en);
					String na = hc.getenchantData(enchname);
					String fnam = na + lvl;
					if (enchantments.length() == 0) {
						enchantments = fnam;
					} else {
						enchantments = enchantments + ", " + fnam;
					}
				}
				} else {
					enchantments = "None";
				}

    			
    			double dura = player.getItemInHand().getDurability();
				double maxdura = player.getItemInHand().getType().getMaxDurability();
				double durp = (1 - dura/maxdura) * 100;
				calc.setTID(itemid);
    			if  (calc.testId()) {
    				durp = (long)Math.floor(durp + .5);
    			} else {
    				durp = 100;
    			}
    				m.send(player, 6);
    				player.sendMessage(ChatColor.BLUE + "Name: " + ChatColor.AQUA + "" + nam);
    				player.sendMessage(ChatColor.BLUE + "Material: " + ChatColor.AQUA + "" + mat);
    				player.sendMessage(ChatColor.BLUE + "ID: " + ChatColor.GREEN + "" + itemid);
    				player.sendMessage(ChatColor.BLUE + "Damage Value: " + ChatColor.GREEN + "" + dv);
    				player.sendMessage(ChatColor.BLUE + "Durability: " + ChatColor.GREEN + "" + durp + "%");
    				player.sendMessage(ChatColor.BLUE + "Enchantments: " + ChatColor.AQUA + "" + enchantments);
    				m.send(player, 6);
    			return true;
    		} catch (Exception e) {
    			m.send(player, 41);
    		}
    		}
    
    		
    		
    		
    		
    		
    		
    	} else if (cmd.getName().equalsIgnoreCase("itemsettings")) {
    		try {
    			if (args.length == 0 && player != null) {
    				int itd = player.getItemInHand().getTypeId();
    				calc.setPDV(player.getItemInHand());
    				int da = calc.getpotionDV();

    				calc.setNdata(itd, da);
    				int newdat = calc.newData();
    				String ke = itd + ":" + newdat;
    				String nam = hc.getnameData(ke);
    				if (nam == null) {
    					m.send(player, 42);
    				} else {
    					FileConfiguration it = hc.getYaml().getItems();
    
    					Double val = it.getDouble(nam + ".value");
    					Boolean stat = it.getBoolean(nam + ".price.static");
    					Double statprice = it.getDouble(nam + ".price.staticprice");
    					int sto = it.getInt(nam + ".stock.stock");
    					int med = it.getInt(nam + ".stock.median");
    					Boolean init = it.getBoolean(nam + ".initiation.initiation");
    					Double starprice = it.getDouble(nam + ".initiation.startprice");
    					
    					double totalstock = ((med * val)/starprice);
    					int maxinitialitems = 0;
    					
    					double roundedtotalstock = Math.rint( totalstock * 1.0d ) / 1.0d;
    					maxinitialitems = (int) (roundedtotalstock - sto);
    					m.send(player, 6);
    					player.sendMessage(ChatColor.BLUE + "Name: " + ChatColor.AQUA + "" + nam);
        				player.sendMessage(ChatColor.BLUE + "Value: " + ChatColor.AQUA + "" + val);
        				player.sendMessage(ChatColor.BLUE + "Use Start Price: " + ChatColor.AQUA + "" + init + ChatColor.BLUE + ", " + ChatColor.GREEN + starprice);
        				player.sendMessage(ChatColor.BLUE + "Static price: " + ChatColor.AQUA + "" + stat + ChatColor.BLUE + ", " + ChatColor.GREEN + "" + statprice);
        				player.sendMessage(ChatColor.BLUE + "Stock: " + ChatColor.GREEN + "" + sto);
        				player.sendMessage(ChatColor.BLUE + "Median stock: " + ChatColor.GREEN + "" + med);		
        				player.sendMessage(ChatColor.BLUE + "Items Needed To Reach Hyperbolic Curve: " + ChatColor.GREEN + "" + maxinitialitems);
        				m.send(player, 6);
    				}
    			} else if (args.length == 1) {
    				String nam = args[0];
    				String teststring = hc.getYaml().getItems().getString(nam);
    				if (teststring == null) {
    					nam = hc.fixName(nam);
    					teststring = hc.getYaml().getItems().getString(nam);
    				}
    				if (teststring != null) {
    					FileConfiguration it = hc.getYaml().getItems();    
    					Double val = it.getDouble(nam + ".value");
    					Boolean stat = it.getBoolean(nam + ".price.static");
    					Double statprice = it.getDouble(nam + ".price.staticprice");
    					int sto = it.getInt(nam + ".stock.stock");
    					int med = it.getInt(nam + ".stock.median");
    					Boolean init = it.getBoolean(nam + ".initiation.initiation");
    					Double starprice = it.getDouble(nam + ".initiation.startprice");
    					
    					double totalstock = ((med * val)/starprice);
    					int maxinitialitems = 0;
    					
    					double roundedtotalstock = Math.rint( totalstock * 1.0d ) / 1.0d;
    					maxinitialitems = (int) (roundedtotalstock - sto);
    					m.send(sender, 6);
    					sender.sendMessage(ChatColor.BLUE + "Name: " + ChatColor.AQUA + "" + nam);
        				sender.sendMessage(ChatColor.BLUE + "Value: " + ChatColor.AQUA + "" + val);
        				sender.sendMessage(ChatColor.BLUE + "Use Start Price: " + ChatColor.AQUA + "" + init + ChatColor.BLUE + ", " + ChatColor.GREEN + starprice);
        				sender.sendMessage(ChatColor.BLUE + "Static price: " + ChatColor.AQUA + "" + stat + ChatColor.BLUE + ", " + ChatColor.GREEN + "" + statprice);
        				sender.sendMessage(ChatColor.BLUE + "Stock: " + ChatColor.GREEN + "" + sto);
        				sender.sendMessage(ChatColor.BLUE + "Median stock: " + ChatColor.GREEN + "" + med);		
        				sender.sendMessage(ChatColor.BLUE + "Items Needed To Reach Hyperbolic Curve: " + ChatColor.GREEN + "" + maxinitialitems);
        				m.send(sender, 6);
    				} else {
    	    			m.send(sender, 1);
    	    		}  

    			} else {
    				m.send(sender, 44);
    			}
    			return true;
    		} catch (Exception e) {
    			m.send(sender, 44);
    		}
    		
    		
    		
    		
    		
    	
    		
    	} else if (cmd.getName().equalsIgnoreCase("enchantsettings")) {
    		try {

    				String nam = args[0];
    				
    				String teststring = hc.getYaml().getEnchants().getString(nam);
    				if (teststring == null) {
    					nam = hc.fixName(nam);
    				}
    				if (!hc.getYaml().getEnchants().getKeys(false).contains(nam)) {
    					m.send(sender, 43);
    				} else {
    					FileConfiguration it = hc.getYaml().getEnchants();
    
    					Double val = it.getDouble(nam + ".value");
    					Boolean stat = it.getBoolean(nam + ".price.static");
    					Double statprice = it.getDouble(nam + ".price.staticprice");
    					int sto = it.getInt(nam + ".stock.stock");
    					int med = it.getInt(nam + ".stock.median");
    					Boolean init = it.getBoolean(nam + ".initiation.initiation");
    					Double starprice = it.getDouble(nam + ".initiation.startprice");
    					
    					double totalstock = ((med * val)/starprice);
    					int maxinitialitems = 0;
    					
    					double roundedtotalstock = Math.rint( totalstock * 1.0d ) / 1.0d;
    					maxinitialitems = (int) (roundedtotalstock - sto);

    					m.send(sender, 6);
    					sender.sendMessage(ChatColor.BLUE + "Name: " + ChatColor.AQUA + "" + nam);
        				sender.sendMessage(ChatColor.BLUE + "Value: " + ChatColor.AQUA + "" + val);
        				sender.sendMessage(ChatColor.BLUE + "Use Start Price: " + ChatColor.AQUA + "" + init + ChatColor.BLUE + ", " + ChatColor.GREEN + starprice);
        				sender.sendMessage(ChatColor.BLUE + "Static price: " + ChatColor.AQUA + "" + stat + ChatColor.BLUE + ", " + ChatColor.GREEN + "" + statprice);       				
        				sender.sendMessage(ChatColor.BLUE + "Stock: " + ChatColor.GREEN + "" + sto);
        				sender.sendMessage(ChatColor.BLUE + "Median stock: " + ChatColor.GREEN + "" + med);			
        				sender.sendMessage(ChatColor.BLUE + "Items Needed To Reach Hyperbolic Curve: " + ChatColor.GREEN + "" + maxinitialitems);
        				m.send(sender, 6);
    				}

    			return true;
    		} catch (Exception e) {
    			m.send(sender, 94);
    		}
    		
    		
    		
    		
    		
    		
    		
    		
    	} else if (cmd.getName().equalsIgnoreCase("taxsettings")) {
    		try {

    					FileConfiguration conf = hc.getYaml().getConfig();
    
    					Double purchasetaxpercent = conf.getDouble("config.purchasetaxpercent");
    					Double initialpurchasetaxpercent = conf.getDouble("config.initialpurchasetaxpercent");
    					Double statictaxpercent = conf.getDouble("config.statictaxpercent");
    					Double enchanttaxpercent = conf.getDouble("config.enchanttaxpercent");
  
    					m.send(sender, 6);
    					sender.sendMessage(ChatColor.BLUE + "Purchase Tax Percent: " + ChatColor.GREEN + "" + purchasetaxpercent);
    					sender.sendMessage(ChatColor.BLUE + "Initial Purchase Tax Percent: " + ChatColor.GREEN + "" + initialpurchasetaxpercent);
    					sender.sendMessage(ChatColor.BLUE + "Static Tax Percent: " + ChatColor.GREEN + "" + statictaxpercent);
    					sender.sendMessage(ChatColor.BLUE + "Enchantment Tax Percent: " + ChatColor.GREEN + "" + enchanttaxpercent);
    					m.send(sender, 6);
    				

    			return true;
    		} catch (Exception e) {
    			m.send(sender, 45);
    		}

    		
    		
    		
    		
    		
    	} else if (cmd.getName().equalsIgnoreCase("hc")) {
    		try {
    			if (args.length == 0) {
    				m.send(sender, 6);
    				m.send(sender, 53);
    				m.send(sender, 54);
    				m.send(sender, 55);
    				m.send(sender, 56);
					m.send(sender, 6);
    			} else if (args.length == 1) {
    				
    				String type = args[0];
    				
    				if (type.equalsIgnoreCase("sell")) {
    					m.send(sender, 6);
    					m.send(sender, 57);
    					m.send(sender, 58);
    					m.send(sender, 59);
    					m.send(sender, 60);
    					m.send(sender, 100);
    					m.send(sender, 61);
    					m.send(sender, 6);
    				} else if (type.equalsIgnoreCase("buy")) {
    					m.send(sender, 6);
    					m.send(sender, 62);
    					m.send(sender, 63);
    					m.send(sender, 64);
    					m.send(sender, 65);
    					m.send(sender, 99);
    					m.send(sender, 66);
    					m.send(sender, 6);
    					
    				} else if (type.equalsIgnoreCase("info")) {
    					
    					m.send(sender, 67);
    					m.send(sender, 68);
    					m.send(sender, 69);
    					m.send(sender, 70);
    					m.send(sender, 71);
    					m.send(sender, 96);
    					m.send(sender, 72);
    					m.send(sender, 73);
    					m.send(sender, 98);
    					m.send(sender, 74);
    					
    					
    				} else if (type.equalsIgnoreCase("params")) {
    					m.send(sender, 6);
    					m.send(sender, 75);
    					m.send(sender, 76);
    					m.send(sender, 77);
    					m.send(sender, 78);
    					m.send(sender, 79);
    					m.send(sender, 6);
    				}
    				
    			} else if (args.length == 2) {
    				String type = args[0];
    				String subtype = args[1];

    				
    				if (type.equalsIgnoreCase("sell")) {
    					
    					if (subtype.equalsIgnoreCase("sell")) {
    						m.send(sender, 6);
    						m.send(sender, 57);
    						m.send(sender, 80);
    						m.send(sender, 6);
    					} else if(subtype.equalsIgnoreCase("hs")) {
    						m.send(sender, 6);
    						m.send(sender, 58);
    						m.send(sender, 81);
    						m.send(sender, 6);
    					} else if(subtype.equalsIgnoreCase("esell")) {
    						m.send(sender, 6);
    						m.send(sender, 59);
    						m.send(sender, 82);				
    						m.send(sender, 6);
    					} else if(subtype.equalsIgnoreCase("sellall")) {
    						m.send(sender, 6);
    						m.send(sender, 60);
    						m.send(sender, 83);	
    						m.send(sender, 6);
    					} else if(subtype.equalsIgnoreCase("sellxp")) {
    						m.send(sender, 6);
    						m.send(sender, 100);
    						m.send(sender, 102);
    						m.send(sender, 6);
    					}						
    				} else if (type.equalsIgnoreCase("buy")) {					
    					if (subtype.equalsIgnoreCase("buy")) {
    						m.send(sender, 6);
    						m.send(sender, 62);
    						m.send(sender, 84);
    						m.send(sender, 6);
    					} else if(subtype.equalsIgnoreCase("hb")) {
    						m.send(sender, 6);
    						m.send(sender, 63);
    						m.send(sender, 85);
    						m.send(sender, 6);
    					} else if(subtype.equalsIgnoreCase("buyid")) {
    						m.send(sender, 6);
    						m.send(sender, 64);
    						m.send(sender, 86);
    						m.send(sender, 6);
    					} else if(subtype.equalsIgnoreCase("ebuy")) {
    						m.send(sender, 6);
    						m.send(sender, 65);
    						m.send(sender, 87);
    						m.send(sender, 6);
    					} else if(subtype.equalsIgnoreCase("buyxp")) {
    						m.send(sender, 6);
    						m.send(sender, 99);
    						m.send(sender, 101);
    						m.send(sender, 6);
    					}			
    				} else if (type.equalsIgnoreCase("info")) {
    					if (subtype.equalsIgnoreCase("value")) {
    						m.send(sender, 6);
    						m.send(sender, 67);
    						m.send(sender, 88);
    						m.send(sender, 6);
    					} else if(subtype.equalsIgnoreCase("hv")) {
    						m.send(sender, 6);
    						m.send(sender, 68);
    						m.send(sender, 89);
    						m.send(sender, 6);
    					} else if(subtype.equalsIgnoreCase("iteminfo")) {
    						m.send(sender, 6);
    						m.send(sender, 69);
    						m.send(sender, 90);
    						m.send(sender, 6);
    					} else if(subtype.equalsIgnoreCase("ii")) {
    						m.send(sender, 6);
    						m.send(sender, 70);
    						m.send(sender, 90);
    						m.send(sender, 6);
    					} else if(subtype.equalsIgnoreCase("topitems")) {
    						m.send(sender, 6);
    						m.send(sender, 71);
    						m.send(sender, 91);
    						m.send(sender, 6);
    					} else if(subtype.equalsIgnoreCase("topenchants")) {
    						m.send(sender, 6);
    						m.send(sender, 72);
    						m.send(sender, 92);
    						m.send(sender, 6);
    					} else if(subtype.equalsIgnoreCase("browseshop")) {
    						m.send(sender, 6);
    						m.send(sender, 96);
    						m.send(sender, 97);
    						m.send(sender, 6);
    					} else if(subtype.equalsIgnoreCase("evalue")) {
    						m.send(sender, 6);
    						m.send(sender, 73);
    						m.send(sender, 93);
    						m.send(sender, 6);
    					} else if(subtype.equalsIgnoreCase("xpinfo")) {
    						m.send(sender, 6);
    						m.send(sender, 98);
    						m.send(sender, 103);
    						m.send(sender, 6);
    					}
    				}		
    			} else {   
    				//do nothing
    			}
    			return true;
    		} catch (Exception e) {
    			m.send(sender, 46);
    		}

    		
    		
    		
    		
    		
    		
    		
    	} else if (cmd.getName().equalsIgnoreCase("ebuy") && player != null) {
    		try {
    			s.setinShop(player);
    			if (s.inShop() != -1) {
    				
    				String name = args[0];
    				
    				
    				String teststring = hc.getYaml().getEnchants().getString(name);
    				if (teststring == null) {
    					name = hc.fixName(name);
    					teststring = hc.getYaml().getEnchants().getString(name);
    				}

    				if (teststring != null) {
    					
    					
        				if (s.has(s.getShop(player), name)) {	
            				ench.setSBE(hc, player, name, economy, l, acc, isign, not, calc);
            				ench.buyEnchant();
    					} else {
    						m.send(player, 95);
    					}
    					
    					

    				} else {
    					m.send(player, 43);
    				}

    				
    				
    			} else {
    				m.send(player, 49);
    			}
    			return true;
    		} catch (Exception e) {
    			m.send(player, 47);
    		}
    		
    		
    		
    			
    			
    			
    			
    			
    	} else if (cmd.getName().equalsIgnoreCase("esell") && player != null) {
    		try {
    			s.setinShop(player);
    			if (s.inShop() != -1) {
    				
    				String name = args[0];
    				

    				
    				if (args[0].equalsIgnoreCase("max")) {
    					
    					//need to add check for items without enchantments
    					ench.setHE(player.getItemInHand());
    					if (!ench.hasenchants()) {
    						m.send(player, 48);
    					}
    					
    					Iterator<Enchantment> ite = player.getItemInHand().getEnchantments().keySet().iterator();
        				while (ite.hasNext()) {
        					String rawstring = ite.next().toString();
        					String enchname = rawstring.substring(rawstring.indexOf(",") + 2, rawstring.length() - 1);
        					Enchantment en = null;
        					en = Enchantment.getByName(enchname);
        					int lvl = player.getItemInHand().getEnchantmentLevel(en);
        					String nam = hc.getenchantData(enchname);
        					String fnam = nam + lvl;
        					
        					
            				if (s.has(s.getShop(player), fnam)) {	
            					ench.setSBE(hc, player, fnam, economy, l, acc, isign, not, calc);
            					ench.sellEnchant();
        					} else {
        						m.send(player, 95);
        					}
        					
        				}
    				} else {
        				String teststring = hc.getYaml().getEnchants().getString(name);
        				if (teststring == null) {
        					name = hc.fixName(name);
        					teststring = hc.getYaml().getEnchants().getString(name);
        				}
        				if (teststring != null) {

        					
            				if (s.has(s.getShop(player), name)) {	
            					ench.setSBE(hc, player, name, economy, l, acc, isign, not, calc);
            					ench.sellEnchant();
        					} else {
        						m.send(player, 95);
        					}
        					
        					
        				} else {
        					m.send(player, 43);
        				}
    				}

    				
    				
    			} else {
    				m.send(player, 49);
    			}
    			return true;
    		} catch (Exception e) {
    			m.send(player, 50);;
    		}
    		
    		
    			
    			
    			
    			
    	} else if (cmd.getName().equalsIgnoreCase("evalue")) {
    		try {
    		
    			if (args.length == 2) {

    				String nam = args[0];
    				String teststring = hc.getYaml().getEnchants().getString(nam);
    				if (teststring == null) {
    					nam = hc.fixName(nam);
    					teststring = hc.getYaml().getEnchants().getString(nam);
    				}
    				if (teststring != null) {
    				

    					
    					String type = args[1];
    					
    					
    					
    					
    					if (type.equalsIgnoreCase("s")) {
    						
    					
    					
	    		        String[] classtype = new String[8];
	    		        classtype[0] = "leather";
	    		        classtype[1] = "wood";
	    		        classtype[2] = "iron";
	    		        classtype[3] = "chainmail";
	    		        classtype[4] = "stone";
	    		        classtype[5] = "gold";
	    		        classtype[6] = "diamond";
	    		        classtype[7] = "bow";
	    				int n = 0;
	    				m.send(sender, 6);		
	    				while (n < 8) {
	    				ench.setVC(hc, nam, classtype[n]);
	    				double value = ench.getValue();    				
	    				sender.sendMessage(ChatColor.AQUA + "" + nam + ChatColor.BLUE + " on a " + ChatColor.AQUA + "" + classtype[n] + ChatColor.BLUE + " item can be sold for: " + ChatColor.GREEN + "$" + value);
	    				n++;
	    				}
	    				m.send(sender, 6);	
    					} else if (type.equalsIgnoreCase("b")) {
    						
    	    		        String[] classtype = new String[8];
    	    		        classtype[0] = "leather";
    	    		        classtype[1] = "wood";
    	    		        classtype[2] = "iron";
    	    		        classtype[3] = "chainmail";
    	    		        classtype[4] = "stone";
    	    		        classtype[5] = "gold";
    	    		        classtype[6] = "diamond";
    	    		        classtype[7] = "bow";
    	    				int n = 0;
    	    				m.send(sender, 6);
    	    				while (n < 8) {
    	    				ench.setVC(hc, nam, classtype[n]);
    	    				double cost = ench.getCost();    				
    	    				sender.sendMessage(ChatColor.AQUA + "" + nam + ChatColor.BLUE + " on a " + ChatColor.AQUA + "" + classtype[n] + ChatColor.BLUE + " item can be bought for: " + ChatColor.GREEN + "$" + cost);
    	    				n++;
    	    				}
    	    				m.send(sender, 6);	
    	    				
    					} else if (type.equalsIgnoreCase("a")) {
    						m.send(sender, 6);	
    						sender.sendMessage(ChatColor.BLUE + "The global shop has " + ChatColor.GREEN + "" + hc.getYaml().getEnchants().getInt(nam + ".stock.stock") + ChatColor.AQUA + " " + nam + ChatColor.BLUE + " available.");
    						m.send(sender, 6);	
    					} else {
    						m.send(sender, 51);
    					}
    				

        			} else {
        				m.send(sender, 43);
        			}

    				
    			} else if (args.length == 0 && player != null){
    				ench.setHE(player.getItemInHand());
    				if (ench.hasenchants()) {
    				player.getItemInHand().getEnchantments().keySet().toArray();
    				
    				Iterator<Enchantment> ite = player.getItemInHand().getEnchantments().keySet().iterator();
    				m.send(player, 6);	
    				ench.setDM(player);
    				double duramult = ench.getDuramult();
    				while (ite.hasNext()) {
    					String rawstring = ite.next().toString();
    					String enchname = rawstring.substring(rawstring.indexOf(",") + 2, rawstring.length() - 1);
    					Enchantment en = null;
    					en = Enchantment.getByName(enchname);
    					int lvl = player.getItemInHand().getEnchantmentLevel(en);
    					String nam = hc.getenchantData(enchname);
    					String fnam = nam + lvl;
    					String mater = player.getItemInHand().getType().name();
    					ench.setVC(hc, fnam, mater);
    					double value = ench.getValue() * duramult;
    					ench.setVC(hc, fnam, mater);
    					double cost = ench.getCost();
						DecimalFormat twodigits = new DecimalFormat("#.##");
						value = Double.valueOf(twodigits.format(value));
						cost = Double.valueOf(twodigits.format(cost));
    					player.sendMessage(ChatColor.AQUA + "" + fnam + ChatColor.BLUE + " can be sold for: " + ChatColor.GREEN + "$" + value);
    					player.sendMessage(ChatColor.AQUA + "" + fnam + ChatColor.BLUE + " can be purchased for: " + ChatColor.GREEN + "$" + cost);
    					player.sendMessage(ChatColor.BLUE + "The global shop currently has" + ChatColor.GREEN + " " + hc.getYaml().getEnchants().getInt(fnam + ".stock.stock") + ChatColor.AQUA + " " + fnam + ChatColor.BLUE + " available.");
    				}
    				m.send(player, 6);	
    			} else {
    				m.send(player, 48);
    			}
    			} else {
    				m.send(sender, 51);
    			}

    			return true;
    		} catch (Exception e) {
    			m.send(sender, 51);
    		}	
    			
    			
    			
    			
    		
    		
    		
    	} else if (cmd.getName().equalsIgnoreCase("removeshop")) {
    		try {

    			if (args.length > 0) {
    				
    				int counter = 0;
    				String name = "";
    				while (counter < args.length) {
    					if (counter == 0) {
    						name = args[0];
    					} else {
    						name = name + "_" + args[counter];
    					}
    					counter++;
    				}
    				String teststring = hc.getYaml().getShops().getString(name);
    				if (teststring == null) {
    					name = hc.fixsName(name);
    				}
    				//sender.sendMessage("name: " + name);
        			s.setrShop(name);
        			s.removeShop();
        			sender.sendMessage(ChatColor.GOLD + name.replace("_", " ") + " has been removed!");	
    				
    			} else {
    				sender.sendMessage(ChatColor.DARK_RED + "Invalid Parameters.  Use /removeshop [name]");
    			}
    			return true;
    		} catch (Exception e) {
    			sender.sendMessage(ChatColor.DARK_RED + "That shop doesn't exist.");
    		}
    		
    		
    	} else if (cmd.getName().equalsIgnoreCase("listshops")) {
    		try {

    			if (args.length == 0) {
    				
    				String shoplist = s.listShops().toString().replace("_", " ").replace("[", "").replace("]", "");
    				sender.sendMessage(ChatColor.AQUA + shoplist);
    				
    			} else {
    				sender.sendMessage(ChatColor.DARK_RED + "Invalid Parameters.  Use /listshops");
    			}
    			return true;
    		} catch (Exception e) {
    			sender.sendMessage(ChatColor.DARK_RED + "Invalid Parameters.  Use /listshops");
    		}
    		
    		
    		
    		
    		
    	} else if (cmd.getName().equalsIgnoreCase("intervals")) {
    		try {

    			if (args.length == 0) {
    				
    				
    				
    				m.send(sender, 6);
    				sender.sendMessage(ChatColor.BLUE + "Shop Check Interval: " + ChatColor.GREEN + "" + s.getshopInterval() + ChatColor.BLUE + " Ticks/" + ChatColor.GREEN + "" + s.getshopInterval()/20 + ChatColor.BLUE + " Seconds");
    				sender.sendMessage(ChatColor.BLUE + "Save Interval: " + ChatColor.GREEN + "" + hc.getsaveInterval() + ChatColor.BLUE + " Ticks/" + ChatColor.GREEN + "" + hc.getsaveInterval()/20 + ChatColor.BLUE + " Seconds");
    				sender.sendMessage(ChatColor.BLUE + "Log Write Interval: " + ChatColor.GREEN + "" + l.getlogInterval() + ChatColor.BLUE + " Ticks/" + ChatColor.GREEN + "" + l.getlogInterval()/20 + ChatColor.BLUE + " Seconds");
    				sender.sendMessage(ChatColor.BLUE + "The log buffer currently holds " + ChatColor.GREEN + "" + l.getbufferSize() + ChatColor.BLUE + " entries.");
    				sender.sendMessage(ChatColor.BLUE + "The log has " + ChatColor.GREEN + "" + l.getlogSize() + ChatColor.BLUE + " entries.");
    				sender.sendMessage(ChatColor.BLUE + "Sign Update Interval: " + ChatColor.GREEN + "" + isign.getsignupdateInterval() + ChatColor.BLUE + " Ticks/" + ChatColor.GREEN + "" + isign.getsignupdateInterval()/20 + ChatColor.BLUE + " Seconds");
    				sender.sendMessage(ChatColor.BLUE + "There are " + ChatColor.GREEN + "" + isign.getremainingSigns() + ChatColor.BLUE + " signs waiting to update.");
    				m.send(sender, 6);
    				
    			} else {
    				sender.sendMessage(ChatColor.DARK_RED + "Invalid Parameters.  Use /intervals");
    			}
    			return true;
    		} catch (Exception e) {
    			sender.sendMessage(ChatColor.DARK_RED + "Invalid Parameters.  Use /intervals");
    		}
    		
    		
    		
    		
    		
    		
    	} else if (cmd.getName().equalsIgnoreCase("setbalance")) {
    		try {

    			if (args.length == 2) {
    				String accountname = args[0];
    				if (acc.checkAccount(accountname)) {
    					Double balance = Double.parseDouble(args[1]);
    					acc.setBalance(accountname, balance);
    					sender.sendMessage(ChatColor.GOLD + "Balance set!");
    				} else {
    					sender.sendMessage(ChatColor.DARK_RED + "That account doesn't exist!");
    				}
    				
    				
    				
    				

    				
    			} else {
    				sender.sendMessage(ChatColor.DARK_RED + "Invalid Parameters.  Use /setbalance [account] [balance]");
    			}
    			return true;
    		} catch (Exception e) {
    			sender.sendMessage(ChatColor.DARK_RED + "Invalid Parameters.  Use /setbalance [account] [balance]");
    		}
    		
    		
    		
    		
    		
    		
    		
    	} else if (cmd.getName().equalsIgnoreCase("additem")) {
    		try {
    			String itemname = args[0];
    			String teststring2 = hc.getYaml().getEnchants().getString(itemname);
				if (teststring2 == null) {
					itemname = hc.fixName(itemname);
					teststring2 = hc.getYaml().getEnchants().getString(itemname);
				}
    			String teststring = hc.getYaml().getItems().getString(itemname);
				if (teststring == null) {
					itemname = hc.fixName(itemname);
					teststring = hc.getYaml().getItems().getString(itemname);
				}

    			if (args.length >= 2) {
    				if (teststring != null || teststring2 != null || itemname.equalsIgnoreCase("all")) {
	    			
	
	    				int counter = 1;
	    				String shopname = "";
	    				while (counter < args.length) {
	    					if (counter == 1) {
	    						shopname = args[1];
	    					} else {
	    						shopname = shopname + "_" + args[counter];
	    					}
	    					counter++;
	    				}
	    				String teststring3 = hc.getYaml().getShops().getString(shopname);
	    				if (teststring3 == null) {
	    					shopname = hc.fixsName(shopname);
	    					teststring3 = hc.getYaml().getShops().getString(shopname);
	    				}
	    				if (teststring3 != null) {
		    				String unavailable = hc.getYaml().getShops().getString(shopname + ".unavailable");

		    				if (!s.has(shopname, itemname) || itemname.equalsIgnoreCase("all")) {
		    					
		    					if (!itemname.equalsIgnoreCase("all")) {
			    					unavailable = unavailable.replace("," + itemname + ",", ",");
			    					if (itemname.equalsIgnoreCase(unavailable.substring(0, itemname.length()))) {
			    						unavailable = unavailable.substring(itemname.length() + 1, unavailable.length());
			    					}
			    					
			    					hc.getYaml().getShops().set(shopname + ".unavailable", unavailable);
			    					//hc.getYaml().saveYamls();
			    					sender.sendMessage(ChatColor.GOLD + itemname + " added to " + shopname.replace("_", " "));
		    					} else if (itemname.equalsIgnoreCase("all")) {
	
			    					
			    					hc.getYaml().getShops().set(shopname + ".unavailable", null);
			    					//hc.getYaml().saveYamls();
			    					sender.sendMessage(ChatColor.GOLD + "All items have been added to " + shopname.replace("_", " "));
		    					}
		    					
		    					
		    				} else {
		    					sender.sendMessage(ChatColor.DARK_RED + "The shop already has that item.");
		    				}
	    				
	    				} else {
	    					sender.sendMessage(ChatColor.DARK_RED + "That shop doesn't exist!");
	    				}
	    			} else {
	    				m.send(sender, 42);
	    			}
    			} else {
    				sender.sendMessage(ChatColor.DARK_RED + "Invalid Parameters.  Use /removeitem [name/'all'] [shop]");
    			}
    			return true;
    		} catch (Exception e) {
    			sender.sendMessage(ChatColor.DARK_RED + "Invalid Parameters.  Use /additem [name/'all'] [shop]");
    		}
    		
    		
    		
    		
    	} else if (cmd.getName().equalsIgnoreCase("removeitem")) {
    		try {

    			String itemname = args[0];
    			String teststring2 = hc.getYaml().getEnchants().getString(itemname);
				if (teststring2 == null) {
					itemname = hc.fixName(itemname);
					teststring2 = hc.getYaml().getEnchants().getString(itemname);
				}
    			String teststring = hc.getYaml().getItems().getString(itemname);
				if (teststring == null) {
					itemname = hc.fixName(itemname);
					teststring = hc.getYaml().getItems().getString(itemname);
				}
    			if (args.length >= 2) {
    				if (teststring != null || teststring2 != null || itemname.equalsIgnoreCase("all")) {
	    			
	
	    				int counter = 1;
	    				String shopname = "";
	    				while (counter < args.length) {
	    					if (counter == 1) {
	    						shopname = args[1];
	    					} else {
	    						shopname = shopname + "_" + args[counter];
	    					}
	    					counter++;
	    				}
	    				
	    				String teststring3 = hc.getYaml().getShops().getString(shopname);
	    				if (teststring3 == null) {
	    					shopname = hc.fixsName(shopname);
	    					teststring3 = hc.getYaml().getShops().getString(shopname);
	    				}
	    				if (teststring3 != null) {
	    				
		    				String unavailable = hc.getYaml().getShops().getString(shopname + ".unavailable");
		    				if (s.has(shopname, itemname) || itemname.equalsIgnoreCase("all")) {
		    					
		    					
		    					if (!itemname.equalsIgnoreCase("all")) {
		    						if (unavailable == null) {
		    							unavailable = "";
		    						}
			    					unavailable = unavailable + itemname + ",";
			    					hc.getYaml().getShops().set(shopname + ".unavailable", unavailable);
			    					//hc.getYaml().saveYamls();
			    					sender.sendMessage(ChatColor.GOLD + itemname + " removed from " + shopname.replace("_", " "));
		    					} else if (itemname.equalsIgnoreCase("all")) {
		    						
	
		    						String itemlist = "";
		    		    			Iterator<String> it2 = hc.getYaml().getItems().getKeys(true).iterator();
		    	    				while (it2.hasNext()) {   			
		    	    					Object element2 = it2.next();
		    	    					String elst2 = element2.toString();    				
		    	    					if (elst2.indexOf(".") == -1) {
		    	    							itemlist = itemlist + elst2 + ",";	    	    						
		    	    					}
		    	    				}
		    	    				
		    	    				
		    						String enchantlist = "";
		    		    			Iterator<String> it3 = hc.getYaml().getEnchants().getKeys(true).iterator();
		    	    				while (it3.hasNext()) {   			
		    	    					Object element2 = it3.next();
		    	    					String elst2 = element2.toString();    				
		    	    					if (elst2.indexOf(".") == -1) {
		    	    							enchantlist = enchantlist + elst2 + ",";
		    	    					}
		    	    				}
		    	    				
		    						
			    					hc.getYaml().getShops().set(shopname + ".unavailable", itemlist + enchantlist);
			    					//hc.getYaml().saveYamls();
			    					sender.sendMessage(ChatColor.GOLD + "All items and enchantments have been removed from " + shopname.replace("_", " "));
		    					}
		    				} else {
		    					sender.sendMessage(ChatColor.DARK_RED + "That item has already been removed from the shop.");
		    				}
	    				
	    				} else {
	    					sender.sendMessage(ChatColor.DARK_RED + "That shop doesn't exist!");
	    				}
	    				
	    			} else {
	    				m.send(sender, 42);
	    			}
    			} else {
    				sender.sendMessage(ChatColor.DARK_RED + "Invalid Parameters.  Use /removeitem [name/'all'] [shop] ");
    			}
    			return true;
    		} catch (Exception e) {
    			sender.sendMessage(ChatColor.DARK_RED + "Invalid Parameters.  Use /removeitem [name/'all'] [shop]");
    		}
    		
    		
    		
    		
    		
    		
    		
    		
    		
    		
    		
    	} else if (cmd.getName().equalsIgnoreCase("notify")) {
    		try {

    			String itemname = hc.fixName(args[0]);

    			if (args.length == 1) {
    				
    				if (hc.getYaml().getConfig().getBoolean("config.use-notifications")) {
    					
    				
    				
	    				if (hc.itemTest(itemname) || hc.enchantTest(itemname) || itemname.equalsIgnoreCase("all")) {
		    		
		    				
	    					if (!itemname.equalsIgnoreCase("all")) {
			    				boolean note = false;
			    				String notify = hc.getYaml().getConfig().getString("config.notify-for");
			    				if (notify != null) {		
			    					//For everything but the first.  (Which lacks a comma.)
			    					if (notify.contains("," + itemname + ",")) {
			    						note = true;
			    					}
			    					//For the first/last item.
			    					if (notify.length() >= itemname.length() && itemname.equalsIgnoreCase(notify.substring(0, itemname.length()))) {
			    						note = true;
			    					}
			    				}
			    				
			    				//Toggles the notification.
			    				if (note) {
			    					notify = notify.replace("," + itemname + ",", ",");
			    					if (itemname.equalsIgnoreCase(notify.substring(0, itemname.length()))) {
			    						notify = notify.substring(itemname.length() + 1, notify.length());
			    					}
			    					hc.getYaml().getConfig().set("config.notify-for", notify);
			    					sender.sendMessage(ChatColor.GOLD + "You will no longer receive notifications for " + itemname);
			    				} else {
			    					notify = notify + itemname + ",";
			    					hc.getYaml().getConfig().set("config.notify-for", notify);
			    					sender.sendMessage(ChatColor.GOLD + "You will now receive notifications for " + itemname);
			    				}
	    					} else {
	    						
	    						ArrayList<String> items = hc.getNames();
	    						String namelist = "";
	    						int i = 0;
	    						while (i < items.size()) {
	    							namelist = namelist + items.get(i) + ",";
	    							i++;
	    						}
	    						
	    						String notify = hc.getYaml().getConfig().getString("config.notify-for");
	    						if (notify.equalsIgnoreCase(namelist)) {
			    					hc.getYaml().getConfig().set("config.notify-for", "");
			    					sender.sendMessage(ChatColor.GOLD + "You will no longer receive notifications for any item or enchantment.");
	    						} else {
			    					hc.getYaml().getConfig().set("config.notify-for", namelist);
			    					sender.sendMessage(ChatColor.GOLD + "You will now receive notifications for all items and enchantments.");
	    						}
	    					}
	
			  
	
		    				
		    			} else {
		    				sender.sendMessage(ChatColor.DARK_RED + "That item or enchantment is not in the database!");
		    			}
    				
	    			} else {
	    				sender.sendMessage(ChatColor.DARK_RED + "Notifications are currently disabled!");
	    			}
    				
    			} else {
    				sender.sendMessage(ChatColor.DARK_RED + "Invalid Parameters.  Use /notify [name/'all']");
    			}
    			return true;
    		} catch (Exception e) {
    			sender.sendMessage(ChatColor.DARK_RED + "Invalid Parameters.  Use /notify [name/'all']");
    		}
    		
    		
    		
    		
    		
    		
    		
    		
    		
    		
     	} else if (cmd.getName().equalsIgnoreCase("setstockmedianall")) {
    		try {
    			
    			if (args.length == 0){
    				sender.sendMessage(ChatColor.RED + "Are you sure you wish to do this?");
    				sender.sendMessage(ChatColor.RED + "All item and enchantment stocks will be set to their median.");
    				sender.sendMessage(ChatColor.RED + "All item and enchantments will have initial pricing disabled.");
    				sender.sendMessage(ChatColor.RED + "Type /setstockmedianall confirm to proceed.");

    			} else if (args[0].equalsIgnoreCase("confirm")){
	    			FileConfiguration items = hc.getYaml().getItems();
	    			Iterator<String> it = items.getKeys(false).iterator();
	    			while (it.hasNext()) {   			
	    				String elst = it.next().toString();
	    				items.set(elst + ".stock.stock", items.getInt(elst + ".stock.median"));
	    				items.set(elst + ".initiation.initiation", false);
	    			}   
	    			
	    			FileConfiguration enchants = hc.getYaml().getEnchants();
	    			Iterator<String> it2 = enchants.getKeys(false).iterator();
	    			while (it2.hasNext()) {   			;
	    				String elst2 = it2.next().toString();
	    				enchants.set(elst2 + ".stock.stock", enchants.getInt(elst2 + ".stock.median"));
	    				enchants.set(elst2 + ".initiation.initiation", false);
	    			}  
   			
	    			sender.sendMessage(ChatColor.GOLD + "Shop stocks of all items/enchantments have been set to their medians and initial pricing has been disabled.");
	    			
					//Updates all information signs.
	    			isign.setrequestsignUpdate(true);
	    			isign.checksignUpdate();
    			} else {
    				sender.sendMessage(ChatColor.DARK_RED + "Invalid Parameters.  Use /setstockmedianall");
    			}
    			
    			
    		} catch (Exception e) {
    			sender.sendMessage(ChatColor.DARK_RED + "Invalid Parameters.  Use /setstockmedianall");
    		}	

    		
    		return true;
    		
    		
    	} else if (cmd.getName().equalsIgnoreCase("scalebypercent")) {
    		try {
    			
    			if (args.length == 2){
    				
    				String type = args[0];
    				Double percent = Double.parseDouble(args[1]);
    				percent = percent/100;
    				
    				if (percent >= 0) {
    					if (type.equalsIgnoreCase("value") || type.equalsIgnoreCase("staticprice") || type.equalsIgnoreCase("stock") || type.equalsIgnoreCase("median") || type.equalsIgnoreCase("startprice")) {
    						String path = "";
    						if (type.equalsIgnoreCase("value")) {
    							path = ".value";
    						} else if (type.equalsIgnoreCase("staticprice")) {
    							path = ".price.staticprice";
    						} else if (type.equalsIgnoreCase("stock")) {
    							path = ".stock.stock";
    						} else if (type.equalsIgnoreCase("median")) {
    							path = ".stock.median";
    						} else if (type.equalsIgnoreCase("startprice")) {
    							path = ".initiation.startprice";
    						}
    						
    						
    						FileConfiguration items = hc.getYaml().getItems();
        	    			Iterator<String> it = items.getKeys(false).iterator();
        	    			while (it.hasNext()) {   			
        	    				String elst = it.next().toString();
        	    				Double newvalue = items.getDouble(elst + path) * percent;
        	    				DecimalFormat twodigits = new DecimalFormat("#.##");
        	    				newvalue = Double.valueOf(twodigits.format(newvalue));
        	    				items.set(elst + path, newvalue);
        	    			}   
        	    			
        	    			FileConfiguration enchants = hc.getYaml().getEnchants();
        	    			Iterator<String> it2 = enchants.getKeys(false).iterator();
        	    			while (it2.hasNext()) {   			;
        	    				String elst2 = it2.next().toString();
        	    				Double newvalue = enchants.getDouble(elst2 + path) * percent;
        	    				DecimalFormat twodigits = new DecimalFormat("#.##");
        	    				newvalue = Double.valueOf(twodigits.format(newvalue));
        	    				enchants.set(elst2 + path, newvalue);
        	    			}  

        	    			sender.sendMessage(ChatColor.GOLD + "Adjustment successful!");
        	    			
        					//Updates all information signs.
        	    			isign.setrequestsignUpdate(true);
        	    			isign.checksignUpdate();
        				} else {
            				sender.sendMessage(ChatColor.DARK_RED + "The setting must be either value, staticprice, stock, median, or startprice!");
            			}
    						
    					} else {
    						sender.sendMessage(ChatColor.DARK_RED + "Percent must be greater than 0!");
    					}
    			} else {
    				sender.sendMessage(ChatColor.DARK_RED + "Invalid Parameters.  Use /scalebypercent [setting] [percent]");
    			}  			  			
    		} catch (Exception e) {
    			sender.sendMessage(ChatColor.DARK_RED + "Invalid Parameters.  Use /scalebypercent [setting] [percent]");
    		}	

    		
    		return true;
    		
    		
    		
    	} else if (cmd.getName().equalsIgnoreCase("resetshop")) {
    		try {
    			
    			if (args.length == 0){
    				sender.sendMessage(ChatColor.RED + "Are you sure you wish to do this?");
    				sender.sendMessage(ChatColor.RED + "All item and enchantment stocks will be set to 0.");
    				sender.sendMessage(ChatColor.RED + "All items and enchantments will return to initial pricing.");
    				sender.sendMessage(ChatColor.RED + "Static pricing will be disabled for all items and enchantments.");
    				sender.sendMessage(ChatColor.RED + "Type /resetshop confirm to proceed.");

    			} else if (args[0].equalsIgnoreCase("confirm")){
	    			FileConfiguration items = hc.getYaml().getItems();
	    			Iterator<String> it = items.getKeys(false).iterator();
	    			while (it.hasNext()) {   			
	    				Object element = it.next();
	    				String elst = element.toString();
	    				items.set(elst + ".stock.stock", 0);
	    				items.set(elst + ".price.static", false);
	    				items.set(elst + ".initiation.initiation", true);
	    			}   
	    			
	    			FileConfiguration enchants = hc.getYaml().getEnchants();
	    			Iterator<String> it2 = enchants.getKeys(false).iterator();
	    			while (it2.hasNext()) {   			
	    				Object element2 = it2.next();
	    				String elst2 = element2.toString();
	    				enchants.set(elst2 + ".stock.stock", 0);
	    				enchants.set(elst2 + ".price.static", false);
	    				enchants.set(elst2 + ".initiation.initiation", true);
	    			}  
	    			sender.sendMessage(ChatColor.GOLD + "Shop stock, initiation, and static pricing have been reset!");
	    			
					//Updates all information signs.
	    			isign.setrequestsignUpdate(true);
	    			isign.checksignUpdate();
    			} else {
    				sender.sendMessage(ChatColor.DARK_RED + "Invalid Parameters.  Use /resetshop");
    			}
    			
    			
    		} catch (Exception e) {
    			sender.sendMessage(ChatColor.DARK_RED + "Invalid Parameters.  Use /resetshop");
    		}	

    		
    		return true;
    		
    		
    		
    		
    		
    		
    	} else if (cmd.getName().equalsIgnoreCase("renameshop")) {
    		try {
    			
    			if (args.length >= 1){
    				int counter = 0;
    				String name = "";
    				while (counter < args.length) {
    					if (counter == 0) {
    						name = args[0];
    					} else {
    						name = name + "_" + args[counter];
    					}
    					counter++;
    				}
				    name = hc.fixsName(name);
				    
				    if (name.equalsIgnoreCase("reset")) {
    					renameshopname = "";
    					renameshopint = 0;
				    	sender.sendMessage(ChatColor.GOLD + "Command has been reset!");
				    	return true;
				    }
				    
				    
					String teststring = hc.getYaml().getShops().getString(name);
    				
    				
    				
    				if (renameshopint == 0 && teststring != null) {
    					renameshopname = name;
    					renameshopint = 1;
    					sender.sendMessage(ChatColor.GOLD + "Shop to be renamed selected!");
    					sender.sendMessage(ChatColor.GOLD + "Now type /renameshop [new name]");
    					sender.sendMessage(ChatColor.GOLD + "To reset the command and start over type /renameshop reset");
    				} else if (renameshopint == 1) {
    					
    					if (name.equalsIgnoreCase(renameshopname)) {
    						sender.sendMessage(ChatColor.DARK_RED + "You can't give the shop its original name!");
    						return true;
    					}
    					s.setrenShop(renameshopname, name);
    					s.renameShop();
    					renameshopname = "";
    					renameshopint = 0;
    					sender.sendMessage(ChatColor.GOLD + "Shop renamed successfully!");
    				} else {
    					sender.sendMessage(ChatColor.DARK_RED + "That shop doesn't exist!");
    				}
    				

    			
    			} else {
    				sender.sendMessage(ChatColor.DARK_RED + "Invalid Parameters.  Use /renameshop [name]");
    			}
    			
    			
    		} catch (Exception e) {
    			sender.sendMessage(ChatColor.DARK_RED + "Invalid Parameters.  Use /renameshop [name]");
    		}	

    		
    		return true;
    		
    		
    		
    		
    		
    		
    		
    	} else if (cmd.getName().equalsIgnoreCase("setmessage")) {
    		try {
	    		
	    		if (args.length >= 3) {
	    			
	    			if (args[0].equalsIgnoreCase("1")) {

	    				String message = args[1];

	    				message = message.replace("%s", " ");
	    				
	    				int counter = 2;
	    				String name = "";
	    				while (counter < args.length) {
	    					if (counter == 2) {
	    						name = args[2];
	    					} else {
	    						name = name + "_" + args[counter];
	    					}
	    					counter++;
	    				}
	    				String teststring = hc.getYaml().getShops().getString(name);
	    				if (teststring == null) {
	    					name = hc.fixsName(name);
	    				}
	    				
	    				
	    				int i = 0;
	    				
	    				while (i < s.getshopdataSize()) {
	    					//sender.sendMessage("False: " + Shop.shopdata.get(i));
	    				if (name.equalsIgnoreCase(s.getshopData(i))) {
	    					s.setMessage1(i, message);
	    					hc.getYaml().getShops().set(s.getshopData(i) + ".shopmessage1", message);
	    					sender.sendMessage(ChatColor.GOLD + "Message 1 set!");
	    					//sender.sendMessage("True: " + Shop.shopdata.get(i) + "," + Shop.shopmessage1.get(i));
	    					return true;
	    				}
	    					i++;
	    				}
	    				sender.sendMessage(ChatColor.DARK_RED + "That shop doesn't exist!");

		    			
	    			} else if (args[0].equalsIgnoreCase("2")) {
	    				
	    				String message = args[1];

	    				message = message.replace("%s", " ");
	    				
	    				int counter = 2;
	    				String name = "";
	    				while (counter < args.length) {
	    					if (counter == 2) {
	    						name = args[2];
	    					} else {
	    						name = name + "_" + args[counter];
	    					}
	    					counter++;
	    				}
	    				String teststring = hc.getYaml().getShops().getString(name);
	    				if (teststring == null) {
	    					name = hc.fixsName(name);
	    				}
	    				
	    				int i = 0;
	    				while (i < s.getshopdataSize()) {
	    				if (name.equalsIgnoreCase(s.getshopData(i))) {
	    					s.setMessage2(i, message);
	    					hc.getYaml().getShops().set(s.getshopData(i) + ".shopmessage2", message);
	    					sender.sendMessage(ChatColor.GOLD + "Message 2 set!");
	    					return true;
	    				}
	    				i++;
	    				}
	    				
	    				
	    				sender.sendMessage(ChatColor.DARK_RED + "That shop doesn't exist!");
		    			
	    			} 
	    			
	    		} else {
	    			sender.sendMessage(ChatColor.DARK_RED + "Invalid Parameters.  Use /setmessage ['1'/'2'] [message] [shop]");
	    		}
    		} catch (Exception e) {
    			sender.sendMessage(ChatColor.DARK_RED + "Invalid Parameters.  Use /setmessage ['1'/'2'] [message] [shop]");
    		}

    		
    		return true;
    		
    		
    		
    		
    		
    		
    		
    		
    	} else if (cmd.getName().equalsIgnoreCase("setshop") && player != null) {
    		if (args.length >= 2) {
    			
    			if (args[0].equalsIgnoreCase("p1")) {
    				int counter = 1;
    				String name = "";
    				while (counter < args.length) {
    					if (counter == 1) {
    						name = args[1];
    					} else {
    						name = name + "_" + args[counter];
    					}
    					counter++;
    				}
    				String teststring = hc.getYaml().getShops().getString(name);
    				if (teststring == null) {
    					name = hc.fixsName(name);
    				}
    				name = name.replace(".", "").replace(":", "");
        			s.setsShop(name, player);
        			s.setShop1();
        			player.sendMessage(ChatColor.GOLD + "Shop location p1 has been set!");	
    			} else if (args[0].equalsIgnoreCase("p2")) {
    				int counter = 1;
    				String name = "";
    				while (counter < args.length) {
    					if (counter == 1) {
    						name = args[1];
    					} else {
    						name = name + "_" + args[counter];
    					}
    					counter++;
    				}
    				
    				name = name.replace(".", "").replace(":", "");
    				String teststring = hc.getYaml().getShops().getString(name);
    				if (teststring == null) {
    					name = hc.fixsName(name);
    				}
    				s.setsShop(name, player);
        			s.setShop2();
        			player.sendMessage(ChatColor.GOLD + "Shop location p2 has been set!");	
    			}
    		} else {
    			m.send(player, 52);
    		}
    		return true;	
    	}
    	
    	
    	
    	
    	
    	
    	

    	
    	
    	
    	return false;
	}
		

    
    
	}
	
	
	
	
	
	
	

