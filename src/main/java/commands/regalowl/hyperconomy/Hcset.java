package regalowl.hyperconomy;


import java.util.ArrayList;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import regalowl.databukkit.CommonFunctions;




public class Hcset implements CommandExecutor {
	
	
	
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		HyperConomy hc = HyperConomy.hc;
		CommonFunctions cf = hc.gCF();
		if (hc.getHyperLock().isLocked(sender)) {
			hc.getHyperLock().sendLockMessage(sender);;
			return true;
		}
		EconomyManager em = hc.getEconomyManager();
		LanguageFile L = hc.getLanguageFile();
		InfoSignHandler ih = hc.getInfoSignHandler();
		try {
			String economy = hc.getConsoleSettings().getEconomy(sender);
			Player p = null;
			if (sender instanceof Player) {
				p = (Player)sender;
			}
			HyperEconomy he = em.getEconomy(economy);
			if (args.length == 0) {
				sender.sendMessage(L.get("HCSET_INVALID"));
				return true;
			}
			
			if (args[0].equalsIgnoreCase("name")) {
				try {
					if (args.length == 3) {
						String name = args[1];
						String newName = args[2];
						if (he.objectTest(name)) {
							he.getHyperObject(name).setName(newName);
							sender.sendMessage(L.get("NAME_SET"));
							hc.restart();
						} else {
							sender.sendMessage(L.get("INVALID_NAME"));
						}
					} else {
						sender.sendMessage(L.get("HCSET_NAME_INVALID"));
					}
				} catch (Exception e) {
					sender.sendMessage(L.get("HCSET_NAME_INVALID"));
				}
			}
			if (args[0].equalsIgnoreCase("ceiling")) {
				try {
					if (args.length == 3) {
						String name = args[1];
						double ceiling = Double.parseDouble(args[2]);
						if (he.objectTest(name)) {
							he.getHyperObject(name).setCeiling(ceiling);
							sender.sendMessage(L.f(L.get("CEILING_SET"), name));
							ih.updateSigns();
						} else {
							sender.sendMessage(L.get("INVALID_NAME"));
						}
					} else {
						sender.sendMessage(L.get("HCSET_CEILING_INVALID"));
					}
				} catch (Exception e) {
					sender.sendMessage(L.get("HCSET_CEILING_INVALID"));
				}
			}
			
			if (args[0].equalsIgnoreCase("displayname")) {
				try {
					String name = args[1];
					String newName = args[2];
					if (he.objectTest(name)) {
						HyperObject to = he.getHyperObject(newName);
						if (to != null) {
							sender.sendMessage(L.get("NAME_IN_USE"));
							return true;
						}
						he.getHyperObject(name).setDisplayName(newName);
						sender.sendMessage(L.f(L.get("DISPLAYNAME_SET"), newName));
					} else {
						sender.sendMessage(L.get("INVALID_NAME"));
					}
				} catch (Exception e) {
					sender.sendMessage(L.get("NOT_IMPLEMENTED"));
				}
			}
			
			if (args[0].equalsIgnoreCase("static")) {
				try {
					String name = args[1];
					HyperObject ho = he.getHyperObject(name);
					if (ho == null) {
						sender.sendMessage(L.get("INVALID_NAME"));
						return true;
					}
					boolean isStatic = Boolean.parseBoolean(ho.getIsstatic());
					if (isStatic) {
						ho.setIsstatic("false");
						sender.sendMessage(L.get("NOT_IMPLEMENTED"));
					} else {
						ho.setIsstatic("true");
						sender.sendMessage(L.get("NOT_IMPLEMENTED"));
					}
					
				} catch (Exception e) {
					sender.sendMessage(L.get("NOT_IMPLEMENTED"));
				}
			}
			
			if (args[0].equalsIgnoreCase("stock")) {
				try {
					String name = args[1];
					HyperObject ho = he.getHyperObject(name);
					if (ho == null) {
						sender.sendMessage(L.get("INVALID_NAME"));
						return true;
					}
					Double stock = Double.parseDouble(args[2]);
					ho.setStock(stock);
					sender.sendMessage(L.f(L.get("STOCK_SET"), ho.getDisplayName()));
				} catch (Exception e) {
					sender.sendMessage(L.get("NOT_IMPLEMENTED"));
				}
			}
			
			
			if (args[0].equalsIgnoreCase("startprice")) {
				try {
					String name = args[1];
					HyperObject ho = he.getHyperObject(name);
					if (ho == null) {
						sender.sendMessage(L.get("INVALID_NAME"));
						return true;
					}
					Double price = Double.parseDouble(args[2]);
					ho.setStartprice(price);
					sender.sendMessage(L.f(L.get("START_PRICE_SET"), ho.getDisplayName()));
				} catch (Exception e) {
					sender.sendMessage(L.get("NOT_IMPLEMENTED"));
				}
			}
			
			if (args[0].equalsIgnoreCase("staticprice")) {
				try {
					String name = args[1];
					HyperObject ho = he.getHyperObject(name);
					if (ho == null) {
						sender.sendMessage(L.get("INVALID_NAME"));
						return true;
					}
					Double price = Double.parseDouble(args[2]);
					ho.setStaticprice(price);
					sender.sendMessage(L.f(L.get("STATIC_PRICE_SET"), ho.getDisplayName()));
				} catch (Exception e) {
					sender.sendMessage(L.get("NOT_IMPLEMENTED"));
				}
			}
			
			if (args[0].equalsIgnoreCase("stockall")) {
				try {
					Double newStock = Double.parseDouble(args[1]);
					ArrayList<HyperObject> hos = he.getHyperObjects();
					for (HyperObject ho:hos) {
						ho.setStock(newStock);
					}
					sender.sendMessage(L.get("ALL_STOCKS_SET"));
				} catch (Exception e) {
					sender.sendMessage(L.get("NOT_IMPLEMENTED"));
				}
			}
			
			if (args[0].equalsIgnoreCase("stockmedianall")) {
				try {
					if (hc.gYH().gFC("config").getBoolean("config.run-automatic-backups")) {
						new Backup();
					}
					ArrayList<HyperObject> hos = he.getHyperObjects();
					for (HyperObject ho:hos) {
						if (!(ho instanceof CompositeItem)) {
							ho.setStock(ho.getMedian());
							ho.setInitiation("false");
						}
					}
					sender.sendMessage(L.get("SETSTOCKMEDIANALL_SUCCESS"));
				} catch (Exception e) {
					sender.sendMessage(L.get("NOT_IMPLEMENTED"));
				}
			}
			
			if (args[0].equalsIgnoreCase("staticall")) {
				try {
					Double newStock = Double.parseDouble(args[1]);
					ArrayList<HyperObject> hos = he.getHyperObjects();
					for (HyperObject ho:hos) {
						ho.setStock(newStock);
					}
					sender.sendMessage(L.get("ALL_STOCKS_SET"));
				} catch (Exception e) {
					sender.sendMessage(L.get("NOT_IMPLEMENTED"));
				}
			}
			
		} catch (Exception e) {
			sender.sendMessage(L.get("HCSET_INVALID"));
		}
		return true;
	}
	
	

}
