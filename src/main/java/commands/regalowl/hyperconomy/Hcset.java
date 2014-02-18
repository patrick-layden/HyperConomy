package regalowl.hyperconomy;


import java.util.ArrayList;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;




public class Hcset implements CommandExecutor {
	
	
	
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		HyperConomy hc = HyperConomy.hc;
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
				economy = em.getHyperPlayer(p).getEconomy();
			}
			HyperEconomy he = em.getEconomy(economy);
			if (args.length == 0) {
				sender.sendMessage(L.get("HCSET_INVALID"));
				return true;
			}
			
			if (args[0].equalsIgnoreCase("name")) {
				try {
					String name = args[1];
					String newName = args[2];
					if (he.objectTest(name)) {
						he.getHyperObject(name).setName(newName);
						sender.sendMessage(L.get("NAME_SET"));
						hc.restart();
					} else {
						sender.sendMessage(L.get("INVALID_NAME"));
					}
				} catch (Exception e) {
					sender.sendMessage(L.get("HCSET_NAME_INVALID"));
				}
			} else if (args[0].equalsIgnoreCase("ceiling") || args[0].equalsIgnoreCase("c")) {
				try {
					String name = args[1];
					double ceiling = Double.parseDouble(args[2]);
					if (he.objectTest(name)) {
						he.getHyperObject(name).setCeiling(ceiling);
						sender.sendMessage(L.f(L.get("CEILING_SET"), name));
						ih.updateSigns();
					} else {
						sender.sendMessage(L.get("INVALID_NAME"));
					}
				} catch (Exception e) {
					sender.sendMessage(L.get("HCSET_CEILING_INVALID"));
				}
			} else if (args[0].equalsIgnoreCase("displayname") || args[0].equalsIgnoreCase("dn")) {
				try {
					String name = args[1];
					String newName = args[2];
					if (he.objectTest(name)) {
						HyperObject ho = he.getHyperObject(name);
						HyperObject to = he.getHyperObject(newName);
						if (to != null && !(ho.equals(to))) {
							sender.sendMessage(L.get("NAME_IN_USE"));
							return true;
						}
						ho.setDisplayName(newName);
						sender.sendMessage(L.f(L.get("DISPLAYNAME_SET"), newName));
					} else {
						sender.sendMessage(L.get("INVALID_NAME"));
					}
				} catch (Exception e) {
					sender.sendMessage(L.get("HCSET_DISPLAYNAME_INVALID"));
				}
			} else if (args[0].equalsIgnoreCase("static") || args[0].equalsIgnoreCase("stat")) {
				try {
					String name = args[1];
					if (name.equalsIgnoreCase("all:copy") || name.equalsIgnoreCase("all:true") || name.equalsIgnoreCase("all:false")) {
						if (hc.gYH().gFC("config").getBoolean("config.run-automatic-backups")) {new Backup();}
						boolean state = false;
						boolean copy = false;
						String message = "";
						if (name.equalsIgnoreCase("all:copy")) {
							copy = true;
							state = true;
							message = "true + dynamic prices copied";
						} else if (name.equalsIgnoreCase("all:false")) {
							state = false;
							message = "false";
						} else if (name.equalsIgnoreCase("all:true")) {
							state = true;
							message = "true";
						}
						ArrayList<HyperObject> hyperObjects = he.getHyperObjects();
						for (HyperObject ho:hyperObjects) {
							if (ho instanceof CompositeItem) {continue;}
							if (copy) {
								ho.setStaticprice(ho.getStartprice());
							}
							ho.setIsstatic(state+"");
						}
						sender.sendMessage(L.f(L.get("ALL_OBJECTS_SET_TO_STATIC"), message));
						return true;
					}
					HyperObject ho = he.getHyperObject(name);
					if (ho == null) {
						sender.sendMessage(L.get("INVALID_NAME"));
						return true;
					}
					boolean isStatic = Boolean.parseBoolean(ho.getIsstatic());
					if (isStatic) {
						ho.setIsstatic("false");
						sender.sendMessage(L.get("USE_DYNAMIC_PRICE"));
					} else {
						ho.setIsstatic("true");
						sender.sendMessage(L.get("USE_STATIC_PRICE"));
					}
				} catch (Exception e) {
					sender.sendMessage(L.get("HCSET_STATIC_INVALID"));
				}
			} else if (args[0].equalsIgnoreCase("stock") || args[0].equalsIgnoreCase("s")) {
				try {
					String name = args[1];
					Double stock = 0.0;
					if (!name.equalsIgnoreCase("all:median")) {
						stock = Double.parseDouble(args[2]);
					}
					if (name.equalsIgnoreCase("all")) {
						if (hc.gYH().gFC("config").getBoolean("config.run-automatic-backups")) {new Backup();}
						ArrayList<HyperObject> hyperObjects = he.getHyperObjects();
						for (HyperObject ho:hyperObjects) {
							if (ho instanceof CompositeItem) {continue;}
							ho.setStock(stock);
						}
						sender.sendMessage(L.get("ALL_STOCKS_SET"));
						return true;
					} else if (name.equalsIgnoreCase("all:median")) {
						if (hc.gYH().gFC("config").getBoolean("config.run-automatic-backups")) {new Backup();}
						for (HyperObject ho:he.getHyperObjects()) {
							if ((ho instanceof CompositeItem)) {continue;}
							ho.setStock(ho.getMedian());
							ho.setInitiation("false");
						}
						sender.sendMessage(L.get("SETSTOCKMEDIANALL_SUCCESS"));
						return true;
					}
					HyperObject ho = he.getHyperObject(name);
					if (ho == null) {
						sender.sendMessage(L.get("INVALID_NAME"));
						return true;
					}
					ho.setStock(stock);
					sender.sendMessage(L.f(L.get("STOCK_SET"), ho.getDisplayName()));
				} catch (Exception e) {
					sender.sendMessage(L.get("HCSET_STOCK_INVALID"));
				}
			} else if (args[0].equalsIgnoreCase("startprice") || args[0].equalsIgnoreCase("starp")) {
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
					sender.sendMessage(L.get("HCSET_STARTPRICE_INVALID"));
				}
			} else if (args[0].equalsIgnoreCase("staticprice") || args[0].equalsIgnoreCase("statp")) {
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
					sender.sendMessage(L.get("HCSET_STATICPRICE_INVALID"));
				}
			} else if (args[0].equalsIgnoreCase("value") || args[0].equalsIgnoreCase("v")) {
				try {
					String name = args[1];
					HyperObject ho = he.getHyperObject(name);
					if (ho == null) {
						sender.sendMessage(L.get("INVALID_NAME"));
						return true;
					}
					Double value = Double.parseDouble(args[2]);
					ho.setValue(value);
					sender.sendMessage(L.f(L.get("VALUE_SET"), ho.getDisplayName()));
				} catch (Exception e) {
					sender.sendMessage(L.get("HCSET_VALUE_INVALID"));
				}
			} else if (args[0].equalsIgnoreCase("median") || args[0].equalsIgnoreCase("m")) {
				try {
					String name = args[1];
					HyperObject ho = he.getHyperObject(name);
					if (ho == null) {
						sender.sendMessage(L.get("INVALID_NAME"));
						return true;
					}
					Double median = Double.parseDouble(args[2]);
					ho.setMedian(median);
					sender.sendMessage(L.f(L.get("MEDIAN_SET"), ho.getDisplayName()));
				} catch (Exception e) {
					sender.sendMessage(L.get("HCSET_MEDIAN_INVALID"));
				}
			} else if (args[0].equalsIgnoreCase("floor") || args[0].equalsIgnoreCase("f")) {
				try {
					String name = args[1];
					HyperObject ho = he.getHyperObject(name);
					if (ho == null) {
						sender.sendMessage(L.get("INVALID_NAME"));
						return true;
					}
					Double floor = Double.parseDouble(args[2]);
					ho.setFloor(floor);
					sender.sendMessage(L.f(L.get("FLOOR_SET"), ho.getDisplayName()));
				} catch (Exception e) {
					sender.sendMessage(L.get("HCSET_FLOOR_INVALID"));
				}
			} else if (args[0].equalsIgnoreCase("balance") || args[0].equalsIgnoreCase("b")) {
				try {
					String accountName = args[1];
					if (em.hasAccount(accountName)) {
						Double balance = Double.parseDouble(args[2]);
						em.getHyperPlayer(accountName).setBalance(balance);
						sender.sendMessage(L.get("BALANCE_SET"));
					} else {
						sender.sendMessage(L.get("ACCOUNT_NOT_EXIST"));
					}
				} catch (Exception e) {
					sender.sendMessage(L.get("HCSET_BALANCE_INVALID"));
				}
			} else if (args[0].equalsIgnoreCase("initiation") || args[0].equalsIgnoreCase("init")) {
				try {
					String name = args[1];
					if (name.equalsIgnoreCase("all:true") || name.equalsIgnoreCase("all:false")) {
						if (hc.gYH().gFC("config").getBoolean("config.run-automatic-backups")) {new Backup();}
						boolean state = false;
						String message = "";
						if (name.equalsIgnoreCase("all:false")) {
							state = false;
							message = "false";
						} else if (name.equalsIgnoreCase("all:true")) {
							state = true;
							message = "true";
						}
						ArrayList<HyperObject> hyperObjects = he.getHyperObjects();
						for (HyperObject ho:hyperObjects) {
							if (ho instanceof CompositeItem) {continue;}
							ho.setInitiation(state+"");
						}
						sender.sendMessage(L.f(L.get("ALL_OBJECTS_SET_TO"), message));
						return true;
					}
					HyperObject ho = he.getHyperObject(name);
					if (ho == null) {
						sender.sendMessage(L.get("INVALID_NAME"));
						return true;
					}
					boolean isInitial= Boolean.parseBoolean(ho.getInitiation());
					if (isInitial) {
						ho.setInitiation("false");
						sender.sendMessage(L.f(L.get("INITIATION_FALSE"), ho.getDisplayName()));
					} else {
						ho.setInitiation("true");
						sender.sendMessage(L.f(L.get("INITIATION_TRUE"), ho.getDisplayName()));
					}
				} catch (Exception e) {
					sender.sendMessage(L.get("HCSET_INITIATION_INVALID"));
				}
			} else {
				sender.sendMessage(L.get("HCSET_INVALID"));
				return true;
			}
			
			
		} catch (Exception e) {
			sender.sendMessage(L.get("HCSET_INVALID"));
		}
		return true;
	}
	
	

}
