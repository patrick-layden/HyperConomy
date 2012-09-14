package regalowl.hyperconomy;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;

public class InfoSign implements Listener {
	private int activesign;
	private boolean stoprequested;
	private FileConfiguration sns;
	private HyperConomy hc;
	private Calculation calc;
	private Set<String> names;
	private ArrayList<String> signkeys = new ArrayList<String>();
	private long signupdateinterval;
	private boolean requestsignupdate;
	private boolean signupdateactive;
	private boolean signupdaterepeat;
	private int signupdatetaskid;
	private ArrayList<String> signtypes = new ArrayList<String>();

	public void setinfoSign(HyperConomy hyperc, Calculation clc, ETransaction enchant, Transaction tran) {
		activesign = 0;
		stoprequested = false;
		hc = hyperc;
		calc = clc;
		sns = hc.getYaml().getSigns();
		signupdateinterval = hc.getYaml().getConfig().getLong("config.signupdateinterval");
		requestsignupdate = false;
		signupdateactive = false;
		signupdaterepeat = false;
		if (hc.getYaml().getConfig().getBoolean("config.use-info-signs")) {
			hc.getServer().getPluginManager().registerEvents(this, hc);
		}
		names = new HashSet<String>();
		ArrayList<String> anames = hc.getNames();
		for (int i = 0; i < anames.size(); i++) {
			names.add(anames.get(i));
		}
		signkeys.clear();
		Iterator<String> iterat = sns.getKeys(false).iterator();
		while (iterat.hasNext()) {
			signkeys.add(iterat.next().toString());
		}
		signtypes.add("buy");
		signtypes.add("sell");
		signtypes.add("stock");
		signtypes.add("value");
		signtypes.add("status");
		signtypes.add("static price");
		signtypes.add("start price");
		signtypes.add("median");
		signtypes.add("history");
		signtypes.add("tax");
		signtypes.add("sb");
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onSignChangeEvent(SignChangeEvent scevent) {
		Player p = scevent.getPlayer();
		if (p.hasPermission("hyperconomy.createsign")) {
			String line12 = scevent.getLine(0).trim() + scevent.getLine(1).trim();
			line12 = hc.fixName(line12);
			if (names.contains(line12.toLowerCase())) {
				String type = getsignType(scevent.getLine(2));
				if (type != null) {
					String locat = scevent.getBlock().getWorld().getName() + "|" + scevent.getBlock().getX() + "|" + scevent.getBlock().getY() + "|" + scevent.getBlock().getZ();
					if (sns.get(locat) == null) {
						sns.set(locat + ".itemname", line12);
						sns.set(locat + ".type", type);
						if (hc.useSQL()) {
							sns.set(locat + ".economy", hc.getSQLFunctions().getPlayerEconomy(p.getName()));
						} else {
							sns.set(locat + ".economy", "default");
						}
						signkeys.clear();
						Iterator<String> iterat = sns.getKeys(false).iterator();
						while (iterat.hasNext()) {
							signkeys.add(iterat.next().toString());
						}
					}
					setrequestsignUpdate(true);
					checksignUpdate();
				}
			}
		}
	}

	public boolean updatesignsThread() {
		String signkey = "";
		String itemn = "";
		String type = "";
		String economy = "";
		if (hc.getYaml().getConfig().getBoolean("config.use-info-signs")) {
			if (activesign < signkeys.size()) {
				try {
					signkey = signkeys.get(activesign);
					itemn = sns.getString(signkey + ".itemname");
					type = sns.getString(signkey + ".type");
					economy = sns.getString(signkey + ".economy");
					if (hc.useSQL()) {
						if (!hc.getSQLFunctions().testEconomy(economy)) {
							sns.set(signkey + ".economy", "default");
							economy = "default";
						}
					}
					if (economy == null) {
						sns.set(signkey + ".economy", "default");
						economy = "default";
					}
					String world = signkey.substring(0, signkey.indexOf("|"));
					signkey = signkey.substring(signkey.indexOf("|") + 1, signkey.length());
					int x = Integer.parseInt(signkey.substring(0, signkey.indexOf("|")));
					signkey = signkey.substring(signkey.indexOf("|") + 1, signkey.length());
					int y = Integer.parseInt(signkey.substring(0, signkey.indexOf("|")));
					signkey = signkey.substring(signkey.indexOf("|") + 1, signkey.length());
					int z = Integer.parseInt(signkey);
					signkey = world + "|" + x + "|" + y + "|" + z;
					Block signblock = Bukkit.getWorld(world).getBlockAt(x, y, z);
					if (signblock.getType().equals(Material.SIGN_POST) || signblock.getType().equals(Material.WALL_SIGN)) {
						Sign s = (Sign) signblock.getState();
						if (checkSign(s)) {
							itemn = sns.getString(signkey + ".itemname");
							type = sns.getString(signkey + ".type");
						}
						String line23 = "";
						String SB3 = "";
						String SB4 = "";
						boolean item = hc.itemTest(itemn);
						boolean enchant = hc.enchantTest(itemn);
						if (type.equalsIgnoreCase("sell")) {
							if (item) {
								double value = calc.getTvalue(itemn, 1, economy);
								value = calc.twoDecimals(value - calc.getSalesTax(null, value));
								line23 = "§fSell: " + "§a" + hc.getYaml().getConfig().getString("config.currency-symbol") + value;
							} else if (enchant) {
								if (sns.getString(signkey + ".enchantclass") == null) {
									sns.set(signkey + ".enchantclass", "diamond");
								}
								double value = calc.getEnchantValue(itemn, sns.getString(signkey + ".enchantclass"), economy);
								value = calc.twoDecimals(value - calc.getSalesTax(null, value));
								line23 = "§fSell: " + "§a" + hc.getYaml().getConfig().getString("config.currency-symbol") + value;
								
							}
						} else if (type.equalsIgnoreCase("buy")) {
							if (item) {
								double pcost = calc.getCost(itemn, 1, economy);
								line23 = "§fBuy: " + "§a" + hc.getYaml().getConfig().getString("config.currency-symbol") + (calc.twoDecimals(pcost + calc.getPurchaseTax(itemn, economy, pcost)));
							} else if (enchant) {
								if (sns.getString(signkey + ".enchantclass") == null) {
									sns.set(signkey + ".enchantclass", "diamond");
								}
								double cost = calc.getEnchantCost(itemn, sns.getString(signkey + ".enchantclass"), economy);
								cost = calc.twoDecimals(cost + calc.getEnchantTax(itemn, economy, cost));
								line23 = "§fBuy: " + "§a" + hc.getYaml().getConfig().getString("config.currency-symbol") + cost;
							}
						} else if (type.equalsIgnoreCase("sb")) {
							if (item) {
								line23 = null;
								double pcost = calc.getCost(itemn, 1, economy);
								SB4 = "§fB:" + "§a" + hc.getYaml().getConfig().getString("config.currency-symbol") + (calc.twoDecimals(pcost + calc.getPurchaseTax(itemn, economy, pcost)));
								double value = calc.getTvalue(itemn, 1, economy);
								value = calc.twoDecimals(value - calc.getSalesTax(null, value));
								SB3 = "§fS:" + "§a" + hc.getYaml().getConfig().getString("config.currency-symbol") + value;
							} else if (enchant) {
								if (sns.getString(signkey + ".enchantclass") == null) {
									sns.set(signkey + ".enchantclass", "diamond");
								}
								line23 = null;
								double cost = calc.getEnchantCost(itemn, sns.getString(signkey + ".enchantclass"), economy);
								cost = calc.twoDecimals(cost + calc.getEnchantTax(itemn, economy, cost));
								SB4 = "§fB:" + "§a" + hc.getYaml().getConfig().getString("config.currency-symbol") + cost;
								double value = calc.getEnchantValue(itemn, sns.getString(signkey + ".enchantclass"), economy);
								value = calc.twoDecimals(value - calc.getSalesTax(null, value));
								SB3 = "§fS:" + "§a" + hc.getYaml().getConfig().getString("config.currency-symbol") + value;
							}
						} else if (type.equalsIgnoreCase("stock")) {
							if (item) {
								line23 = "§fStock: " + "§a" + hc.getSQLFunctions().getStock(itemn, economy);
							} else if (enchant) {
								line23 = "§fStock: " + "§a" + hc.getSQLFunctions().getStock(itemn, economy);
							}
						} else if (type.equalsIgnoreCase("Status")) {
							if (item) {
								boolean staticstatus;
								staticstatus = Boolean.parseBoolean(hc.getSQLFunctions().getStatic(itemn, economy));
								if (staticstatus) {
									line23 = "§fStatus: " + "§a" + "Static";
								} else {
									boolean initialstatus;
									initialstatus = Boolean.parseBoolean(hc.getSQLFunctions().getInitiation(itemn, economy));
									if (initialstatus) {
										line23 = "§fStatus: " + "§a" + "Initial";
									} else {
										line23 = "§fStatus: " + "§a" + "Dynamic";
									}
								}
							} else if (enchant) {
								boolean staticstatus;
								staticstatus = Boolean.parseBoolean(hc.getSQLFunctions().getStatic(itemn, economy));
								if (staticstatus) {
									line23 = "§fStatus: " + "§a" + "Static";
								} else {
									boolean initialstatus;
									initialstatus = Boolean.parseBoolean(hc.getSQLFunctions().getInitiation(itemn, economy));
									if (initialstatus) {
										line23 = "§fStatus: " + "§a" + "Initial";
									} else {
										line23 = "§fStatus: " + "§a" + "Dynamic";
									}
								}
							}
						} else if (type.equalsIgnoreCase("value")) {
							if (item) {
								line23 = "§fValue: " + "§a" + hc.getSQLFunctions().getValue(itemn, economy);
							} else if (enchant) {
								line23 = "§fValue: " + "§a" + hc.getSQLFunctions().getValue(itemn, economy);
								;
							}
						} else if (type.equalsIgnoreCase("static price")) {
							if (item) {
								line23 = "§fStatic Price: " + "§a" + hc.getSQLFunctions().getStaticPrice(itemn, economy);
							} else if (enchant) {
								line23 = "§fStatic Price: " + "§a" + hc.getSQLFunctions().getStaticPrice(itemn, economy);
							}
						} else if (type.equalsIgnoreCase("start price")) {
							if (item) {
								line23 = "§fStart Price: " + "§a" + hc.getSQLFunctions().getStartPrice(itemn, economy);
							} else if (enchant) {
								line23 = "§fStart Price: " + "§a" + hc.getSQLFunctions().getStartPrice(itemn, economy);
							}
						} else if (type.equalsIgnoreCase("median")) {
							if (item) {
								line23 = "§fMedian: " + "§a" + hc.getSQLFunctions().getMedian(itemn, economy);
							} else if (enchant) {
								line23 = "§fMedian: " + "§a" + hc.getSQLFunctions().getMedian(itemn, economy);
							}
						} else if (type.equalsIgnoreCase("history")) {
							String increment = ChatColor.stripColor(s.getLine(3).replace(" ", "")).toUpperCase().replaceAll("[0-9]", "");
							if (increment.contains("(")) {
								increment = increment.substring(0, increment.indexOf("("));
							}
							String timev = ChatColor.stripColor(s.getLine(3).replace(" ", "")).toUpperCase().replaceAll("[A-Z]", "");
							int timevalue;
							if (timev.contains("(")) {
								timevalue = Integer.parseInt(timev.substring(0, timev.indexOf("(")));
							} else {
								timevalue = Integer.parseInt(timev);
							}
							String percentchange = "";
							String colorcode = "§1";
							if (increment.equalsIgnoreCase("h")) {
								percentchange = getpercentChange(itemn, timevalue, economy);
								colorcode = getcolorCode(percentchange);
							} else if (increment.equalsIgnoreCase("d")) {
								timevalue = timevalue * 24;
								percentchange = getpercentChange(itemn, timevalue, economy);
								colorcode = getcolorCode(percentchange);
								timevalue = timevalue / 24;
							} else if (increment.equalsIgnoreCase("w")) {
								timevalue = timevalue * 168;
								percentchange = getpercentChange(itemn, timevalue, economy);
								colorcode = getcolorCode(percentchange);
								timevalue = timevalue / 168;
							} else if (increment.equalsIgnoreCase("m")) {
								timevalue = timevalue * 672;
								percentchange = getpercentChange(itemn, timevalue, economy);
								colorcode = getcolorCode(percentchange);
								timevalue = timevalue / 672;
							}
							String line2 = "§fHistory: ";
							String line3 = "§f" + timevalue + increment.toLowerCase() + colorcode + "(" + percentchange + ")";
							if (line3.length() > 14) {
								line3 = line3.substring(0, 13) + ")";
							}
							line23 = line2 + line3;
						} else if (type.equalsIgnoreCase("Tax")) {
							if (item) {
								line23 = "§fTax: " + "§a" + hc.getYaml().getConfig().getString("config.currency-symbol") + calc.twoDecimals(calc.getPurchaseTax(itemn, economy, calc.getCost(itemn, 1, economy)));
							} else if (enchant) {
								String line3 = ChatColor.stripColor(s.getLine(3).replace(" ", "")).toLowerCase().replaceAll("[0-9]", "");
								if (sns.getString(signkey + ".enchantclass") == null) {
									sns.set(signkey + ".enchantclass", "diamond");
								}
								double price = calc.getEnchantCost(itemn, sns.getString(signkey + ".enchantclass"), economy);
								double taxpaid = calc.twoDecimals(calc.getEnchantTax(itemn, economy, price));
								line23 = "§fTax: " + "§a" + hc.getYaml().getConfig().getString("config.currency-symbol") + taxpaid;
							}
						}
						if (!s.getLine(0).startsWith("§")) {
							s.setLine(0, "§1" + s.getLine(0));
						}
						if (!s.getLine(1).startsWith("§") && !s.getLine(1).isEmpty()) {
							s.setLine(1, "§1" + s.getLine(1));
						}
						if (line23 != null) {
							s.setLine(2, line23.substring(0, line23.indexOf(":") + 1));
							s.setLine(3, line23.substring(line23.indexOf(":") + 1, line23.length()));
						} else {
							s.setLine(2, SB3);
							s.setLine(3, SB4);
						}
						s.update();
					} else {
						sns.set(signkey, null);
					}
					activesign++;
				} catch (Exception e) {
					activesign++;
					String info = "InfoSign error: signkey='" + signkey + "', type='" + type + "', object='" + itemn + "', economy='" + economy + "'";
					new Error(e, info);
					return false;
				}
			} else {
				if (!getsignupdateRepeat()) {
					setrequestsignUpdate(false);
					stoprequested = true;
					activesign = 0;
				} else {
					stopsignupdateRepeat();
					activesign = 0;
				}
			}
		}
		return true;
	}

	public int getremainingSigns() {
		int remainingsigns = 0;
		if (!stoprequested && getsignupdateActive()) {
			remainingsigns = signkeys.size() - activesign;
			if (getsignupdateRepeat()) {
				remainingsigns = remainingsigns + signkeys.size();
			}
		}
		return remainingsigns;
	}

	private String getsignType(String line3) {
		String type = null;
		int counter = 0;
		while (counter < signtypes.size()) {
			if (line3.equalsIgnoreCase(signtypes.get(counter))) {
				type = signtypes.get(counter);
				break;
			}
			counter++;
		}
		return type;
	}

	public boolean checkSign(Sign s) {
		boolean resetsign = false;
		String signkey = signkeys.get(activesign);
		String itemn = sns.getString(signkey + ".itemname");
		String type = sns.getString(signkey + ".type").replace(" ", "").toLowerCase();
		String types = ChatColor.stripColor(s.getLine(2).replace(hc.getYaml().getConfig().getString("config.currency-symbol"), "").replace(":", "").replace(".", "").replaceAll("[0-9]", "")).trim();
		//Bukkit.broadcastMessage(s.getLine(2));
		//Bukkit.broadcastMessage(types);
		String line12 = ChatColor.stripColor(s.getLine(0) + s.getLine(1)).trim();
		line12 = hc.fixName(line12);
		types = types.toLowerCase();
		//Bukkit.broadcastMessage("[" + types + "]");
		if (types.equalsIgnoreCase("s") || types.equalsIgnoreCase("se")) {
			types = "sb";
		}
		
		if (!line12.equalsIgnoreCase(itemn) || !type.equalsIgnoreCase(types)) {
			resetsign = true;
			sns.set(signkey + ".itemname", line12);
			sns.set(signkey + ".type", types);
		}
		return resetsign;
	}

	public void setstoprequested(boolean stoprequest) {
		stoprequested = stoprequest;
	}

	private String getcolorCode(String percentchange) {
		String colorcode = "§1";
		if (percentchange.equalsIgnoreCase("?")) {
			colorcode = "§1";
		} else {
			Double percentc = Double.parseDouble(percentchange);
			if (percentc > 0) {
				colorcode = "§a";
			} else if (percentc < 0) {
				colorcode = "§4";
			}
		}
		return colorcode;
	}

	private String getpercentChange(String itemn, int timevalue, String economy) {
		String percentchange = "";
		SQLFunctions sf = hc.getSQLFunctions();
		double percentc = 0.0;
		String teststring = hc.testiString(itemn);
		String teststring2 = hc.testeString(itemn);
		double historicvalue = sf.getHistoryData(itemn, economy, timevalue);
		if (historicvalue == -1.0) {
			return "?";
		}
		if (teststring != null) {
			Double currentvalue = calc.getTvalue(itemn, 1, economy);
			percentc = ((currentvalue - historicvalue) / historicvalue) * 100;
			percentc = calc.round(percentc, 3);
		} else if (teststring2 != null) {
			Double currentvalue = calc.getEnchantValue(itemn, "diamond", economy);
			percentc = ((currentvalue - historicvalue) / historicvalue) * 100;
			percentc = calc.round(percentc, 3);
		}
		percentchange = percentc + "";
		return percentchange;
	}

	public void resetAll() {
		activesign = 0;
		stoprequested = false;
		signkeys.clear();
		Iterator<String> iterat = sns.getKeys(false).iterator();
		while (iterat.hasNext()) {
			signkeys.add(iterat.next().toString());
		}
	}

	public void setrequestsignUpdate(boolean updatestate) {
		if (signupdateactive && updatestate) {
			signupdaterepeat = true;
		}
		requestsignupdate = updatestate;
	}

	public void stopsignupdateRepeat() {
		signupdaterepeat = false;
	}

	public boolean getsignupdateActive() {
		return signupdateactive;
	}

	public boolean getsignupdateRepeat() {
		return signupdaterepeat;
	}

	public void setsignupdateInterval(long interval) {
		signupdateinterval = interval;
	}

	public long getsignupdateInterval() {
		return signupdateinterval;
	}

	public void startsignUpdate() {
		signupdateactive = true;
		signupdatetaskid = hc.getServer().getScheduler().scheduleSyncRepeatingTask(hc, new Runnable() {
			public void run() {
				if (!requestsignupdate) {
					setstoprequested(false);
					stopsignUpdate();
				} else {
					try {
						updatesignsThread();
					} catch (Exception e) {
						e.printStackTrace();
						Logger log = Logger.getLogger("Minecraft");
						log.info("HyperConomy ERROR #31--Sign update issue.");
						Bukkit.broadcast(ChatColor.DARK_RED + "HyperConomy ERROR #31--Sign update issue.", "hyperconomy.error");
					}
				}
			}
		}, signupdateinterval, signupdateinterval);
	}

	public void stopsignUpdate() {
		hc.getServer().getScheduler().cancelTask(signupdatetaskid);
		signupdateactive = false;
	}

	public void checksignUpdate() {
		if (requestsignupdate && !signupdateactive) {
			startsignUpdate();
		}
	}
}
