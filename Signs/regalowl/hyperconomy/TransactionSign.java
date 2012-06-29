package regalowl.hyperconomy;




import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class TransactionSign implements Listener {
	
	private HyperConomy hc;
	private Transaction tran;
	private Calculation calc;
	private ETransaction ench;
	private Log l;
	private Account acc;
	private InfoSign isign;
	private Notify not;
	private Economy economy;
	
	private Set<String> names;
	private UpdateSign us;
	
	
	
	
	
	public void setTransactionSign(HyperConomy hyperc, Transaction trans, Calculation cal, ETransaction enchant, Log lo, Account account, InfoSign infosign, Notify notify, Economy eco, UpdateSign ups) {
		
		hc = hyperc;
		tran = trans;
		calc = cal;
		ench = enchant;
		l = lo;
		acc = account;
		isign = infosign;
		not = notify;
		economy = eco;
		us = ups;
		
		//Adds all enchantment and item names to names Set.
		names = new HashSet<String>();
		Iterator<String> it = hc.getYaml().getItems().getKeys(false).iterator();
		while (it.hasNext()) {   			  				
			names.add(it.next().toString().toLowerCase());
		}  
		Iterator<String> it2 = hc.getYaml().getEnchants().getKeys(false).iterator();
		while (it2.hasNext()) {   			
			names.add(it2.next().toString().toLowerCase());
		}  
		
		
		if (hc.getYaml().getConfig().getBoolean("config.use-transaction-signs")) {
			hc.getServer().getPluginManager().registerEvents(this, hc);
		}
		
		
	}
	
	@EventHandler(priority = EventPriority.NORMAL)
	public void onSignChangeEvent(SignChangeEvent scevent) {
		if (hc.getYaml().getConfig().getBoolean("config.use-transaction-signs")) {
			String line3 = ChatColor.stripColor(scevent.getLine(2)).trim();
	    	if (line3.equalsIgnoreCase("[sell:buy]") || line3.equalsIgnoreCase("[sell]") || line3.equalsIgnoreCase("[buy]")) {
		    	String line4 = ChatColor.stripColor(scevent.getLine(3)).trim();
		    	try {
		    		Integer.parseInt(line4);
		        	String line12 = ChatColor.stripColor(scevent.getLine(0)).trim() + ChatColor.stripColor(scevent.getLine(1)).trim();
		        	line12 = hc.fixName(line12);
		    	    if (names.contains(line12.toLowerCase())) {
		    	    	if (scevent.getPlayer().hasPermission("hyperconomy.createsign")) {
			    			scevent.setLine(0, "§1" + scevent.getLine(0));
			    			scevent.setLine(1, "§1" + scevent.getLine(1));
			    			if (line3.equalsIgnoreCase("[sell:buy]")) {
			    				scevent.setLine(2, "§f[Sell:Buy]");
			    			} else if (line3.equalsIgnoreCase("[sell]")) {
			    				scevent.setLine(2, "§f[Sell]");
			    			} else if (line3.equalsIgnoreCase("[buy]")) {
			    				scevent.setLine(2, "§f[Buy]");
			    			}
 			    			
			    			scevent.setLine(3, "§a" + scevent.getLine(3));
		    	    	} else if (!scevent.getPlayer().hasPermission("hyperconomy.createsign")) {
			    			scevent.setLine(0, "");
			    			scevent.setLine(1, "");
			    			scevent.setLine(2, "");
			    			scevent.setLine(3, "");
		    	    	}			
				
		    	    	if (scevent.getBlock() != null && scevent.getBlock().getType().equals(Material.SIGN_POST) || scevent.getBlock() != null && scevent.getBlock().getType().equals(Material.WALL_SIGN)) {
		    	    		Sign s = (Sign) scevent.getBlock().getState();
		    	    		s.update();
		    	    	}
					
					
	
		    	    	
		    	    }
		    			
		    	} catch (Exception e) {
		    		return;
		    	}
	    	}
    	}
	}
	
	
	
	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerInteractEvent(PlayerInteractEvent ievent) {
		
		if (hc.getYaml().getConfig().getBoolean("config.use-transaction-signs")) {
		
			Player p = ievent.getPlayer();
			boolean sneak = false;
			if (p.isSneaking()) {
				sneak = true;
			}
			if (sneak && p.hasPermission("hyperconomy.admin")) {
				ievent.setCancelled(false);
				return;
			}
			
			Block b = ievent.getClickedBlock();
			
			
			if (b != null && b.getType().equals(Material.SIGN_POST) || b != null && b.getType().equals(Material.WALL_SIGN)) {
				Sign s = (Sign) b.getState();
	
		    	String line3 = ChatColor.stripColor(s.getLine(2)).trim();
		    	if (line3.equalsIgnoreCase("[sell:buy]") || line3.equalsIgnoreCase("[sell]") || line3.equalsIgnoreCase("[buy]")) {
		    		
		    		String line4 = ChatColor.stripColor(s.getLine(3)).trim();
		    		int amount = 0;
		    		try {
		    			amount = Integer.parseInt(line4);
		    		} catch (Exception e) {
		    			return;
		    		}
		    		
		    		
		    		String line12 = ChatColor.stripColor(s.getLine(0)).trim() + ChatColor.stripColor(s.getLine(1)).trim();
		    		line12 = hc.fixName(line12);
			    	if (names.contains(line12.toLowerCase())) {
						//Colors the sign if it isn't already colored.
			    		if (!s.getLine(0).startsWith("§")) {
			    			us.updateSign(hc, s, "§1" + s.getLine(0), "§1" + s.getLine(1), "§f" + s.getLine(2), "§a" + s.getLine(3));
			    			/*
			    			s.setLine(0, "§1" + s.getLine(0));
			    			s.setLine(1, "§1" + s.getLine(1));
			    			s.setLine(2, "§f" + s.getLine(2));
			    			s.setLine(3, "§a" + s.getLine(3));
			    			s.update();
			    			*/
			    		}
						
						String action = ievent.getAction().name();
						if (action.equalsIgnoreCase("RIGHT_CLICK_BLOCK")) {
							if (line3.equalsIgnoreCase("[sell:buy]") || line3.equalsIgnoreCase("[buy]")) {
								String l1 = s.getLine(0);
								String l2 = s.getLine(1);
								String l3 = s.getLine(2);
								String l4 = s.getLine(3);
								
								if (p.hasPermission("hyperconomy.buysign")) {
									if (hc.itemTest(line12)) {
										int id = hc.getYaml().getItems().getInt(line12 + ".information.id");
										if (id >= 0) {
											if (!hc.isLocked()) {
												tran.setAll(hc, id, hc.getYaml().getItems().getInt(line12 + ".information.data"), amount, line12, p, economy, calc, ench, l, acc, not, isign);
												tran.buy();
											} else {
												p.sendMessage(ChatColor.RED + "The global shop is currently locked!");
											}

										} else if (id == -1) {
											if (!hc.isLocked()) {
												tran.setAll(hc, id, hc.getYaml().getItems().getInt(line12 + ".information.data"), amount, line12, p, economy, calc, ench, l, acc, not, isign);
												tran.buyXP();
											} else {
												p.sendMessage(ChatColor.RED + "The global shop is currently locked!");
											}

										}
									} else if (hc.enchantTest(line12)) {
										if (!hc.isLocked()) {
											ench.setSBE(hc, p, line12, economy, l, acc, isign, not, calc);
											ench.buyEnchant();
										} else {
											p.sendMessage(ChatColor.RED + "The global shop is currently locked!");
										}

									}
								} else {
									p.sendMessage("You don't have permission to do this.");
								}
								ievent.setCancelled(true);
								us.updateSign(hc, s, l1, l2, l3, l4);
							}

						} else if (action.equalsIgnoreCase("LEFT_CLICK_BLOCK")) {
							if (line3.equalsIgnoreCase("[sell:buy]") || line3.equalsIgnoreCase("[sell]")) {
								String l1 = s.getLine(0);
								String l2 = s.getLine(1);
								String l3 = s.getLine(2);
								String l4 = s.getLine(3);
								
								if (p.hasPermission("hyperconomy.sellsign")) {
									if (hc.itemTest(line12)) {
										int id = hc.getYaml().getItems().getInt(line12 + ".information.id");
										if (id >= 0) {
											if (!hc.isLocked()) {
												tran.setAll(hc, id, hc.getYaml().getItems().getInt(line12 + ".information.data"), amount, line12, p, economy, calc, ench, l, acc, not, isign);
												tran.sell();
											} else {
												p.sendMessage(ChatColor.RED + "The global shop is currently locked!");
											}

										} else if (id == -1) {
											if (!hc.isLocked()) {
												tran.setAll(hc, id, hc.getYaml().getItems().getInt(line12 + ".information.data"), amount, line12, p, economy, calc, ench, l, acc, not, isign);
												tran.sellXP();
											} else {
												p.sendMessage(ChatColor.RED + "The global shop is currently locked!");
											}

										}
									} else if (hc.enchantTest(line12)) {
										if (!hc.isLocked()) {
											ench.setSBE(hc, p, line12, economy, l, acc, isign, not, calc);
											ench.sellEnchant();
										} else {
											p.sendMessage(ChatColor.RED + "The global shop is currently locked!");
										}

									}
								} else {
									p.sendMessage("You don't have permission to do this.");
								}
								
								ievent.setCancelled(true);
								us.updateSign(hc, s, l1, l2, l3, l4);
							} else if (line3.equalsIgnoreCase("[buy]")) {
								String l1 = s.getLine(0);
								String l2 = s.getLine(1);
								String l3 = s.getLine(2);
								String l4 = s.getLine(3);
								ievent.setCancelled(true);
								us.updateSign(hc, s, l1, l2, l3, l4);
							}
						}  		
			    	}
		    	}  
			}
		}
	}

	
	
	
	
	

}
