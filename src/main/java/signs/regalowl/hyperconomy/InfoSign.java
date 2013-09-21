package regalowl.hyperconomy;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;

public class InfoSign {
	private String signKey;
	private SignType type;
	private String objectName;
	private double multiplier;
	private String economy;
	private EnchantmentClass enchantClass;
	private HyperObject ho;
	private int x;
	private int y;
	private int z;
	private String world;
	private HyperConomy hc;
	private boolean isEnchantment;
	private LanguageFile L;
	private String line1;
	private String line2;
	private String line3;
	private String line4;
	private boolean dataOk;
	
	private int timeValueHours;
	private int timeValue;
	private String increment;

	InfoSign(String signKey, SignType type, String objectName, double multiplier, String economy, EnchantmentClass enchantClass) {
		this.multiplier = multiplier;
		if (enchantClass == null) {
			this.enchantClass = EnchantmentClass.DIAMOND;
		} else {
			this.enchantClass = enchantClass;
		}
		dataOk = setData(signKey, type, objectName, economy);
		Sign s = getSign();
		if (s != null) {
			line1 = ChatColor.stripColor(s.getLine(0).trim());
			line2 = ChatColor.stripColor(s.getLine(1).trim());
			if (line1.length() > 13) {
				line2 = ChatColor.DARK_BLUE + line1.substring(13, line1.length()) + line2;
				line1 = ChatColor.DARK_BLUE + line1.substring(0, 13);
			} else {
				line1 = ChatColor.DARK_BLUE + line1;
				line2 = ChatColor.DARK_BLUE + line2;
			}
			line3 = s.getLine(2);
			line4 = s.getLine(3);
		}
	}

	InfoSign(String signKey, SignType type, String objectName, double multiplier, String economy, EnchantmentClass enchantClass, String[] lines) {
		this.multiplier = multiplier;
		if (enchantClass == null) {
			this.enchantClass = EnchantmentClass.DIAMOND;
		} else {
			this.enchantClass = enchantClass;
		}
		dataOk = setData(signKey, type, objectName, economy);
		line1 = ChatColor.stripColor(lines[0].trim());
		line2 = ChatColor.stripColor(lines[1].trim());
		if (line1.length() > 13) {
			line2 = ChatColor.DARK_BLUE + line1.substring(13, line1.length()) + line2;
			line1 = ChatColor.DARK_BLUE + line1.substring(0, 13);
		} else {
			line1 = ChatColor.DARK_BLUE + line1;
			line2 = ChatColor.DARK_BLUE + line2;
		}
		line3 = lines[2];
		line4 = lines[3];
	}

	public boolean setData(String signKey, SignType type, String objectName, String economy) {
		try {
			hc = HyperConomy.hc;
			HyperEconomy he = hc.getEconomyManager().getEconomy(economy);
			L = hc.getLanguageFile();
			if (signKey == null || type == null || objectName == null) {
				new HyperError("DEBUG: infosign initialization null: " + signKey + ", " + objectName + ", " + economy);
				return false;
			}
			this.economy = "default";
			this.signKey = signKey;
			this.world = signKey.substring(0, signKey.indexOf("|"));
			signKey = signKey.substring(signKey.indexOf("|") + 1, signKey.length());
			this.x = Integer.parseInt(signKey.substring(0, signKey.indexOf("|")));
			signKey = signKey.substring(signKey.indexOf("|") + 1, signKey.length());
			this.y = Integer.parseInt(signKey.substring(0, signKey.indexOf("|")));
			signKey = signKey.substring(signKey.indexOf("|") + 1, signKey.length());
			this.z = Integer.parseInt(signKey);
			this.type = type;
			this.objectName = he.fixName(objectName);
			if (economy != null) {
				this.economy = economy;
			}
			isEnchantment = he.enchantTest(this.objectName);
			Location l = new Location(Bukkit.getWorld(world), x, y, z);
			Chunk c = l.getChunk();
			if (!c.isLoaded()) {
				c.load();
			}
			Block signblock = Bukkit.getWorld(world).getBlockAt(x, y, z);
			ho = he.getHyperObject(this.objectName);
			if (signblock.getType().equals(Material.SIGN_POST) || signblock.getType().equals(Material.WALL_SIGN)) {
				return true;
			}
			new HyperError("DEBUG: infosign initialization failed: " + x + "," + y + "," + z + "," + world);
			return false;
		} catch (Exception e) {
			new HyperError(e, "InfoSign setData() passed signKey='" + signKey + "', SignType='" + type.toString() + "', objectName='" + objectName + "', economy='" + economy + "'");
			return false;
		}
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public int getZ() {
		return z;
	}

	public String getWorld() {
		return world;
	}

	public String getKey() {
		return signKey;
	}

	public SignType getType() {
		return type;
	}

	public String getObjectName() {
		return objectName;
	}

	public double getMultiplier() {
		return multiplier;
	}

	public String getEconomy() {
		return economy;
	}

	public EnchantmentClass getEnchantmentClass() {
		return enchantClass;
	}

	public boolean testData() {
		getSign();
		return dataOk;
	}

	public void update() {
		if (!dataOk) {
			return;
		}
		if (ho == null) {
			HyperEconomy he = hc.getEconomyManager().getEconomy(economy);
			ho = he.getHyperObject(objectName);
			if (ho == null) {
				new HyperError("InfoSign HyperObject null after retry: " + objectName + "," + economy);
				return;
			}
		}
		Calculation calc = hc.getCalculation();
		try {
			switch (type) {
				case BUY:
					if (isEnchantment) {
						double cost = ho.getCost(enchantClass);
						cost = calc.twoDecimals((cost + ho.getPurchaseTax(cost)) * multiplier);
						line3 = ChatColor.WHITE + "Buy:";
						line4 = ChatColor.GREEN + L.fCS(cost);
					} else {
						double pcost = ho.getCost(1);
						line3 = ChatColor.WHITE + "Buy:";
						line4 = ChatColor.GREEN + L.fCS(calc.twoDecimals((pcost + ho.getPurchaseTax(pcost)) * multiplier));
					}
					break;
				case SELL:
					if (isEnchantment) {
						double value = ho.getValue(enchantClass);
						value = calc.twoDecimals((value - ho.getSalesTaxEstimate(value)) * multiplier);
						line3 = ChatColor.WHITE + "Sell:";
						line4 = ChatColor.GREEN + L.fCS(value);
					} else {
						double value = ho.getValue(1);
						value = calc.twoDecimals((value - ho.getSalesTaxEstimate(value)) * multiplier);
						line3 = ChatColor.WHITE + "Sell:";
						line4 = ChatColor.GREEN + L.fCS(value);
					}
					break;
				case STOCK:
					line3 = ChatColor.WHITE + "Stock:";
					line4 = ChatColor.GREEN + "" + ho.getStock();
					break;
				case VALUE:
					line3 = ChatColor.WHITE + "Value:";
					line4 = ChatColor.GREEN + "" + ho.getValue() * multiplier;
					break;
				case STATUS:
					boolean staticstatus;
					staticstatus = Boolean.parseBoolean(ho.getIsstatic());
					line3 = ChatColor.WHITE + "Status:";
					if (staticstatus) {
						line4 = ChatColor.GREEN + "Static";
					} else {
						boolean initialstatus;
						initialstatus = Boolean.parseBoolean(ho.getInitiation());
						if (initialstatus) {
							line4 = ChatColor.GREEN + "Initial";
						} else {
							line4 = ChatColor.GREEN + "Dynamic";
						}
					}
					break;
				case STATICPRICE:
					line3 = ChatColor.WHITE + "Static Price:";
					line4 = ChatColor.GREEN + "" + ho.getStaticprice() * multiplier;
					break;
				case STARTPRICE:
					line3 = ChatColor.WHITE + "Start Price:";
					line4 = ChatColor.GREEN + "" + ho.getStartprice() * multiplier;
					break;
				case MEDIAN:
					line3 = ChatColor.WHITE + "Median:";
					line4 = ChatColor.GREEN + "" + ho.getMedian();
					break;
				case HISTORY:
					String increment = ChatColor.stripColor(line4.replace(" ", "")).toUpperCase().replaceAll("[0-9]", "");
					if (increment.contains("(")) {
						increment = increment.substring(0, increment.indexOf("("));
					}
					String timev = ChatColor.stripColor(line4.replace(" ", "")).toUpperCase().replaceAll("[A-Z]", "");
					int timevalue;
					int timevalueHours;
					if (timev.contains("(")) {
						timevalue = Integer.parseInt(timev.substring(0, timev.indexOf("(")));
					} else {
						timevalue = Integer.parseInt(timev);
					}
					timevalueHours = timevalue;
					if (increment.equalsIgnoreCase("h")) {
						timevalueHours *= 1;
					} else if (increment.equalsIgnoreCase("d")) {
						timevalueHours *= 24;
					} else if (increment.equalsIgnoreCase("w")) {
						timevalueHours *= 168;
					} else if (increment.equalsIgnoreCase("m")) {
						timevalueHours *= 672;
					}
					updateHistorySign(timevalueHours, timevalue, increment);
					break;
				case TAX:
					if (isEnchantment) {
						double price = ho.getCost(enchantClass);
						double taxpaid = calc.twoDecimals(ho.getPurchaseTax(price) * multiplier);
						line3 = ChatColor.WHITE + "Tax:";
						line4 = ChatColor.GREEN + "" + L.fCS(taxpaid);
					} else {
						line3 = ChatColor.WHITE + "Tax:";
						line4 = ChatColor.GREEN + L.fCS(calc.twoDecimals(ho.getPurchaseTax(ho.getCost(1) * multiplier)));
					}
					break;
				case SB:
					if (isEnchantment) {
						double cost = ho.getCost(enchantClass);
						cost = calc.twoDecimals((cost + ho.getPurchaseTax(cost)) * multiplier);
						line4 = ChatColor.WHITE + "B:" + "\u00A7a" + L.fCS(cost);
						double value = ho.getValue(enchantClass);
						value = calc.twoDecimals((value - ho.getSalesTaxEstimate(value)) * multiplier);
						line3 = ChatColor.WHITE + "S:" + ChatColor.GREEN + L.fCS(value);
					} else {
						double pcost = ho.getCost(1);
						line4 = ChatColor.WHITE + "B:" + ChatColor.GREEN + L.fCS(calc.twoDecimals((pcost + ho.getPurchaseTax(pcost)) * multiplier));
						double value = ho.getValue(1);
						value = calc.twoDecimals((value - ho.getSalesTaxEstimate(value)) * multiplier);
						line3 = ChatColor.WHITE + "S:" + ChatColor.GREEN + L.fCS(value);
					}
					break;
				default:
					break;
			}
			if (!type.equals(SignType.HISTORY)) {
				Sign s = getSign();
				if (s != null) {
					s.setLine(0, line1);
					s.setLine(1, line2);
					s.setLine(2, line3);
					s.setLine(3, line4);
					s.update();
				}
			}
		} catch (Exception e) {
			new HyperError(e);
		}
	}
	
	
	@SuppressWarnings("deprecation")
	private void updateHistorySign(int timevalueHours, int timevalue, String inc) {
		try {
			this.timeValueHours = timevalueHours;
			this.timeValue = timevalue;
			this.increment = inc;
			hc.getServer().getScheduler().scheduleAsyncDelayedTask(hc, new Runnable() {
				public void run() {
					String percentchange = hc.getHistory().getPercentChange(ho, timeValueHours);
					String colorcode = getcolorCode(percentchange);
					line3 = ChatColor.WHITE + "History:";
					line4 = ChatColor.WHITE + "" + timeValue + increment.toLowerCase() + colorcode + "(" + percentchange + ")";
					if (line3.length() > 14) {
						line3 = line3.substring(0, 13) + ")";
					}
					hc.getServer().getScheduler().scheduleSyncDelayedTask(hc, new Runnable() {
						public void run() {
							Sign s = getSign();
							if (s != null) {
								s.setLine(0, line1);
								s.setLine(1, line2);
								s.setLine(2, line3);
								s.setLine(3, line4);
								s.update();
							}
						}
					}, 0L);
				}
			}, 0L);
		} catch (Exception e) {
			new HyperError(e);
		}
	}
	

	private String getcolorCode(String percentchange) {
		String colorcode = "\u00A71";
		if (percentchange.equalsIgnoreCase("?")) {
			colorcode = "\u00A71";
		} else {
			Double percentc = Double.parseDouble(percentchange);
			if (percentc > 0) {
				colorcode = "\u00A7a";
			} else if (percentc < 0) {
				colorcode = "\u00A74";
			}
		}
		return colorcode;
	}
	
	
	public void deleteSign() {
		if (signKey != null && !signKey.equalsIgnoreCase("")) {
			hc.getYaml().getSigns().set(signKey, null);
		}
	}
	
	
	public Sign getSign() {
		if (world == null) {
			return null;
		}
		Location l = new Location(Bukkit.getWorld(world), x, y, z);
		Chunk c = l.getChunk();
		if (!c.isLoaded()) {
			c.load();
		}
		Block signblock = l.getBlock();
		if (signblock.getType().equals(Material.SIGN_POST) || signblock.getType().equals(Material.WALL_SIGN)) {
			Sign s = (Sign) signblock.getState();
			return s;
		} else {
			dataOk = false;
			return null;
		}

	}
	
}
