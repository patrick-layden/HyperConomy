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
			isEnchantment = hc.enchantTest(this.objectName);
			Block signblock = Bukkit.getWorld(world).getBlockAt(x, y, z);
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
						double cost = calc.getEnchantCost(objectName, enchantClass.toString(), economy);
						cost = calc.twoDecimals((cost + calc.getEnchantTax(objectName, economy, cost)) * multiplier);
						line3 = ChatColor.WHITE + "Buy:";
						line4 = ChatColor.GREEN + L.get("CURRENCY") + cost;
					} else {
						double pcost = calc.getCost(objectName, 1, economy);
						line3 = ChatColor.WHITE + "Buy:";
						line4 = ChatColor.GREEN + L.get("CURRENCY") + (calc.twoDecimals((pcost + calc.getPurchaseTax(objectName, economy, pcost)) * multiplier));
					}
					break;
				case SELL:
					if (isEnchantment) {
						double value = calc.getEnchantValue(objectName, enchantClass.toString(), economy);
						value = calc.twoDecimals((value - calc.getSalesTax(null, value)) * multiplier);
						line3 = ChatColor.WHITE + "Sell:";
						line4 = ChatColor.GREEN + L.get("CURRENCY") + value;
					} else {
						double value = calc.getTvalue(objectName, 1, economy);
						value = calc.twoDecimals((value - calc.getSalesTax(null, value)) * multiplier);
						line3 = ChatColor.WHITE + "Sell:";
						line4 = ChatColor.GREEN + L.get("CURRENCY") + value;
					}
					break;
				case STOCK:
					line3 = ChatColor.WHITE + "Stock:";
					line4 = ChatColor.GREEN + "" + hc.getSQLFunctions().getStock(objectName, economy);
					break;
				case VALUE:
					line3 = ChatColor.WHITE + "Value:";
					line4 = ChatColor.GREEN + "" + hc.getSQLFunctions().getValue(objectName, economy) * multiplier;
					break;
				case STATUS:
					boolean staticstatus;
					staticstatus = Boolean.parseBoolean(hc.getSQLFunctions().getStatic(objectName, economy));
					line3 = ChatColor.WHITE + "Status:";
					if (staticstatus) {
						line4 = ChatColor.GREEN + "Static";
					} else {
						boolean initialstatus;
						initialstatus = Boolean.parseBoolean(hc.getSQLFunctions().getInitiation(objectName, economy));
						if (initialstatus) {
							line4 = ChatColor.GREEN + "Initial";
						} else {
							line4 = ChatColor.GREEN + "Dynamic";
						}
					}
					break;
				case STATICPRICE:
					line3 = ChatColor.WHITE + "Static Price:";
					line4 = ChatColor.GREEN + "" + hc.getSQLFunctions().getStaticPrice(objectName, economy) * multiplier;
					break;
				case STARTPRICE:
					line3 = ChatColor.WHITE + "Start Price:";
					line4 = ChatColor.GREEN + "" + hc.getSQLFunctions().getStartPrice(objectName, economy) * multiplier;
					break;
				case MEDIAN:
					line3 = ChatColor.WHITE + "Median:";
					line4 = ChatColor.GREEN + "" + hc.getSQLFunctions().getMedian(objectName, economy);
					break;
				case HISTORY:
					String increment = ChatColor.stripColor(line4.replace(" ", "")).toUpperCase().replaceAll("[0-9]", "");
					if (increment.contains("(")) {
						increment = increment.substring(0, increment.indexOf("("));
					}
					String timev = ChatColor.stripColor(line4.replace(" ", "")).toUpperCase().replaceAll("[A-Z]", "");
					int timevalue;
					if (timev.contains("(")) {
						timevalue = Integer.parseInt(timev.substring(0, timev.indexOf("(")));
					} else {
						timevalue = Integer.parseInt(timev);
					}
					String percentchange = "";
					String colorcode = "\u00A71";
					if (increment.equalsIgnoreCase("h")) {
						percentchange = getpercentChange(objectName, timevalue, economy);
						colorcode = getcolorCode(percentchange);
					} else if (increment.equalsIgnoreCase("d")) {
						timevalue = timevalue * 24;
						percentchange = getpercentChange(objectName, timevalue, economy);
						colorcode = getcolorCode(percentchange);
						timevalue = timevalue / 24;
					} else if (increment.equalsIgnoreCase("w")) {
						timevalue = timevalue * 168;
						percentchange = getpercentChange(objectName, timevalue, economy);
						colorcode = getcolorCode(percentchange);
						timevalue = timevalue / 168;
					} else if (increment.equalsIgnoreCase("m")) {
						timevalue = timevalue * 672;
						percentchange = getpercentChange(objectName, timevalue, economy);
						colorcode = getcolorCode(percentchange);
						timevalue = timevalue / 672;
					}
					line3 = ChatColor.WHITE + "History:";
					line4 = ChatColor.WHITE + "" + timevalue + increment.toLowerCase() + colorcode + "(" + percentchange + ")";
					if (line3.length() > 14) {
						line3 = line3.substring(0, 13) + ")";
					}
					break;
				case TAX:
					if (isEnchantment) {
						double price = calc.getEnchantCost(objectName, enchantClass.toString(), economy);
						double taxpaid = calc.twoDecimals(calc.getEnchantTax(objectName, economy, price) * multiplier);
						line3 = ChatColor.WHITE + "Tax:";
						line4 = ChatColor.GREEN + "" + L.get("CURRENCY") + taxpaid;
					} else {
						line3 = ChatColor.WHITE + "Tax:";
						line4 = ChatColor.GREEN + L.get("CURRENCY") + calc.twoDecimals(calc.getPurchaseTax(objectName, economy, calc.getCost(objectName, 1, economy) * multiplier));
					}
					break;
				case SB:
					if (isEnchantment) {
						double cost = calc.getEnchantCost(objectName, enchantClass.toString(), economy);
						cost = calc.twoDecimals((cost + calc.getEnchantTax(objectName, economy, cost)) * multiplier);
						line4 = ChatColor.WHITE + "B:" + "\u00A7a" + L.get("CURRENCY") + cost;
						double value = calc.getEnchantValue(objectName, enchantClass.toString(), economy);
						value = calc.twoDecimals((value - calc.getSalesTax(null, value)) * multiplier);
						line3 = ChatColor.WHITE + "S:" + ChatColor.GREEN + L.get("CURRENCY") + value;
					} else {
						double pcost = calc.getCost(objectName, 1, economy);
						line4 = ChatColor.WHITE + "B:" + ChatColor.GREEN + L.get("CURRENCY") + (calc.twoDecimals((pcost + calc.getPurchaseTax(objectName, economy, pcost)) * multiplier));
						double value = calc.getTvalue(objectName, 1, economy);
						value = calc.twoDecimals((value - calc.getSalesTax(null, value)) * multiplier);
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
		s.setLine(0, line1);
		s.setLine(1, line2);
		s.setLine(2, line3);
		s.setLine(3, line4);
		s.update();
		return true;
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

	private String getpercentChange(String itemn, int timevalue, String economy) {
		Calculation calc = hc.getCalculation();
		String percentchange = "";
		SQLFunctions sf = hc.getSQLFunctions();
		double percentc = 0.0;
		double historicvalue = sf.getHistoryData(itemn, economy, timevalue);
		if (historicvalue == -1.0) {
			return "?";
		}
		if (hc.itemTest(itemn)) {
			Double currentvalue = calc.getTvalue(itemn, 1, economy);
			percentc = ((currentvalue - historicvalue) / historicvalue) * 100;
			percentc = calc.round(percentc, 3);
		} else if (hc.enchantTest(itemn)) {
			Double currentvalue = calc.getEnchantValue(itemn, "diamond", economy);
			percentc = ((currentvalue - historicvalue) / historicvalue) * 100;
			percentc = calc.round(percentc, 3);
		}
		percentchange = percentc + "";
		return percentchange;
	}
}
