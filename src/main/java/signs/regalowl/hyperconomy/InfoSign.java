package regalowl.hyperconomy;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
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
	private Sign s;
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
		if (s != null) {
			line1 = ChatColor.DARK_BLUE + ChatColor.stripColor(s.getLine(0).trim());
			line2 = ChatColor.DARK_BLUE + ChatColor.stripColor(s.getLine(1).trim());
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
		if (s != null) {
			line1 = ChatColor.DARK_BLUE + ChatColor.stripColor(lines[0].trim());
			line2 = ChatColor.DARK_BLUE + ChatColor.stripColor(lines[1].trim());
			line3 = lines[2];
			line4 = lines[3];
		}
	}

	public boolean setData(String signKey, SignType type, String objectName, String economy) {
		try {
			hc = HyperConomy.hc;
			DataHandler dh = hc.getDataFunctions();
			L = hc.getLanguageFile();
			if (signKey == null || type == null || objectName == null) {
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
			this.objectName = objectName;
			if (economy != null) {
				this.economy = economy;
			}
			isEnchantment = dh.enchantTest(this.objectName);
			Block signblock = Bukkit.getWorld(world).getBlockAt(x, y, z);
			ho = hc.getDataFunctions().getHyperObject(objectName, economy);
			if (signblock.getType().equals(Material.SIGN_POST) || signblock.getType().equals(Material.WALL_SIGN)) {
				s = (Sign) signblock.getState();
				return true;
			}
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

	public Sign getBlockSign() {
		return s;
	}

	public boolean testData() {
		return dataOk;
	}

	public boolean update() {
		if (!dataOk) {
			return false;
		}
		Calculation calc = hc.getCalculation();
		try {
			switch (type) {
				case BUY:
					if (isEnchantment) {
						double cost = ho.getCost(enchantClass);
						cost = calc.twoDecimals((cost + ho.getPurchaseTax(cost)) * multiplier);
						line3 = ChatColor.WHITE + "Buy:";
						line4 = ChatColor.GREEN + L.get("CURRENCY") + cost;
					} else {
						double pcost = ho.getCost(1);
						line3 = ChatColor.WHITE + "Buy:";
						line4 = ChatColor.GREEN + L.get("CURRENCY") + (calc.twoDecimals((pcost + ho.getPurchaseTax(pcost)) * multiplier));
					}
					break;
				case SELL:
					if (isEnchantment) {
						double value = ho.getValue(enchantClass);
						value = calc.twoDecimals((value - ho.getSalesTaxEstimate(value)) * multiplier);
						line3 = ChatColor.WHITE + "Sell:";
						line4 = ChatColor.GREEN + L.get("CURRENCY") + value;
					} else {
						double value = ho.getValue(1);
						value = calc.twoDecimals((value - ho.getSalesTaxEstimate(value)) * multiplier);
						line3 = ChatColor.WHITE + "Sell:";
						line4 = ChatColor.GREEN + L.get("CURRENCY") + value;
					}
					break;
				case STOCK:
					line3 = ChatColor.WHITE + "Stock:";
					line4 = ChatColor.GREEN + "" + hc.getDataFunctions().getHyperObject(objectName, economy).getStock();
					break;
				case VALUE:
					line3 = ChatColor.WHITE + "Value:";
					line4 = ChatColor.GREEN + "" + hc.getDataFunctions().getHyperObject(objectName, economy).getValue() * multiplier;
					break;
				case STATUS:
					boolean staticstatus;
					staticstatus = Boolean.parseBoolean(hc.getDataFunctions().getHyperObject(objectName, economy).getIsstatic());
					line3 = ChatColor.WHITE + "Status:";
					if (staticstatus) {
						line4 = ChatColor.GREEN + "Static";
					} else {
						boolean initialstatus;
						initialstatus = Boolean.parseBoolean(hc.getDataFunctions().getHyperObject(objectName, economy).getInitiation());
						if (initialstatus) {
							line4 = ChatColor.GREEN + "Initial";
						} else {
							line4 = ChatColor.GREEN + "Dynamic";
						}
					}
					break;
				case STATICPRICE:
					line3 = ChatColor.WHITE + "Static Price:";
					line4 = ChatColor.GREEN + "" + hc.getDataFunctions().getHyperObject(objectName, economy).getStaticprice() * multiplier;
					break;
				case STARTPRICE:
					line3 = ChatColor.WHITE + "Start Price:";
					line4 = ChatColor.GREEN + "" + hc.getDataFunctions().getHyperObject(objectName, economy).getStartprice() * multiplier;
					break;
				case MEDIAN:
					line3 = ChatColor.WHITE + "Median:";
					line4 = ChatColor.GREEN + "" + hc.getDataFunctions().getHyperObject(objectName, economy).getMedian();
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
						line4 = ChatColor.GREEN + "" + L.get("CURRENCY") + taxpaid;
					} else {
						line3 = ChatColor.WHITE + "Tax:";
						line4 = ChatColor.GREEN + L.get("CURRENCY") + calc.twoDecimals(ho.getPurchaseTax(ho.getCost(1) * multiplier));
					}
					break;
				case SB:
					if (isEnchantment) {
						double cost = ho.getCost(enchantClass);
						cost = calc.twoDecimals((cost + ho.getPurchaseTax(cost)) * multiplier);
						line4 = ChatColor.WHITE + "B:" + "\u00A7a" + L.get("CURRENCY") + cost;
						double value = ho.getValue(enchantClass);
						value = calc.twoDecimals((value - ho.getSalesTaxEstimate(value)) * multiplier);
						line3 = ChatColor.WHITE + "S:" + ChatColor.GREEN + L.get("CURRENCY") + value;
					} else {
						double pcost = ho.getCost(1);
						line4 = ChatColor.WHITE + "B:" + ChatColor.GREEN + L.get("CURRENCY") + (calc.twoDecimals((pcost + ho.getPurchaseTax(pcost)) * multiplier));
						double value = ho.getValue(1);
						value = calc.twoDecimals((value - ho.getSalesTaxEstimate(value)) * multiplier);
						line3 = ChatColor.WHITE + "S:" + ChatColor.GREEN + L.get("CURRENCY") + value;
					}
					break;
				default:
					break;
			}
		} catch (Exception e) {
			dataOk = false;
			return false;
		}
		if (!type.equals(SignType.HISTORY)) {
			s.setLine(0, line1);
			s.setLine(1, line2);
			s.setLine(2, line3);
			s.setLine(3, line4);
			s.update();
		}
		return true;
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
							s.setLine(0, line1);
							s.setLine(1, line2);
							s.setLine(2, line3);
							s.setLine(3, line4);
							s.update();
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
	
}
