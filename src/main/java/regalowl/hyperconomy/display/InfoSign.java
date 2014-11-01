package regalowl.hyperconomy.display;

import java.util.HashMap;






import regalowl.databukkit.CommonFunctions;
import regalowl.hyperconomy.HC;
import regalowl.hyperconomy.HyperEconomy;
import regalowl.hyperconomy.minecraft.HLocation;
import regalowl.hyperconomy.minecraft.HSign;
import regalowl.hyperconomy.tradeobject.BasicTradeObject;
import regalowl.hyperconomy.tradeobject.EnchantmentClass;
import regalowl.hyperconomy.tradeobject.TradeObject;
import regalowl.hyperconomy.tradeobject.TradeObjectType;
import regalowl.hyperconomy.util.LanguageFile;

public class InfoSign {
	private SignType type;
	private String objectName;
	private double multiplier;
	private String economy;
	private EnchantmentClass enchantClass;
	private TradeObject ho;
	private HLocation loc;
	private HC hc;
	private LanguageFile L;
	private String line1;
	private String line2;
	private String line3;
	private String line4;
	
	private int timeValueHours;
	private int timeValue;
	private String increment;

	InfoSign(HLocation signLoc, SignType type, String objectName, double multiplier, String economy, EnchantmentClass enchantClass) {
		this.multiplier = multiplier;
		if (enchantClass == null) {
			this.enchantClass = EnchantmentClass.DIAMOND;
		} else {
			this.enchantClass = enchantClass;
		}
		hc = HC.hc;
		HyperEconomy he = HC.hc.getDataManager().getEconomy(economy);
		L = hc.getLanguageFile();
		this.economy = "default";
		if (economy != null) {
			this.economy = economy;
		}
		this.loc = signLoc;
		this.type = type;
		this.objectName = he.fixName(objectName);
		ho = he.getHyperObject(this.objectName);
		if (ho == null) {
			deleteSign();
			return;
		}
		HSign s = getSign();
		if (s == null) {
			deleteSign();
			return;
		}
		line1 = HC.mc.removeColor(s.getLine(0).trim());
		line2 = HC.mc.removeColor(s.getLine(1).trim());
		if (line1.length() > 13) {
			line2 = HC.mc.applyColor("&1" + line1.substring(13, line1.length()) + line2);
			line1 = HC.mc.applyColor("&1" + line1.substring(0, 13));
		} else {
			line1 = HC.mc.applyColor("&1" + line1);
			line2 = HC.mc.applyColor("&1" + line2);
		}
		line3 = s.getLine(2);
		line4 = s.getLine(3);
	}

	
	InfoSign(HLocation signLoc, SignType type, String objectName, double multiplier, String economy, EnchantmentClass enchantClass, String[] lines) {
		this.multiplier = multiplier;
		if (enchantClass == null) {
			this.enchantClass = EnchantmentClass.DIAMOND;
		} else {
			this.enchantClass = enchantClass;
		}
		hc = HC.hc;
		HyperEconomy he = HC.hc.getDataManager().getEconomy(economy);
		L = hc.getLanguageFile();
		this.economy = "default";
		this.loc = signLoc;
		this.type = type;
		this.objectName = he.fixName(objectName);
		if (economy != null) {
			this.economy = economy;
		}
		ho = he.getHyperObject(this.objectName);
		if (ho == null) {
			deleteSign();
			return;
		}
		line1 = HC.mc.removeColor(lines[0].trim());
		line2 = HC.mc.removeColor(lines[1].trim());
		if (line1.length() > 13) {
			line2 = HC.mc.applyColor("&1" + line1.substring(13, line1.length()) + line2);
			line1 = HC.mc.applyColor("&1" + line1.substring(0, 13));
		} else {
			line1 = HC.mc.applyColor("&1" + line1);
			line2 = HC.mc.applyColor("&1" + line2);
		}
		line3 = lines[2];
		line4 = lines[3];
		HashMap<String,String> values = new HashMap<String,String>();
		values.put("WORLD", loc.getWorld());
		values.put("X", loc.getBlockX()+"");
		values.put("Y", loc.getBlockY()+"");
		values.put("Z", loc.getBlockZ()+"");
		values.put("HYPEROBJECT", objectName);
		values.put("TYPE", type.toString());
		values.put("MULTIPLIER", multiplier+"");
		values.put("ECONOMY", economy+"");
		values.put("ECLASS", enchantClass.toString());
		hc.getSQLWrite().performInsert("hyperconomy_info_signs", values);
		if (getSign() == null) {
			deleteSign();
			return;
		}
	}
	



	public int getX() {
		return loc.getBlockX();
	}

	public int getY() {
		return loc.getBlockY();
	}

	public int getZ() {
		return loc.getBlockZ();
	}

	public String getWorld() {
		return loc.getWorld();
	}

	public HLocation getLocation() {
		return loc;
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


	public void update() {
		try {
			switch (type) {
				case BUY:
					if (ho.getType() == TradeObjectType.ENCHANTMENT) {
						double cost = ho.getBuyPrice(enchantClass);
						cost = CommonFunctions.twoDecimals((cost + ho.getPurchaseTax(cost)) * multiplier);
						line3 = HC.mc.applyColor("&f" + "Buy:");
						line4 = HC.mc.applyColor("&a" + L.fCS(cost));
					} else if (ho.getType() == TradeObjectType.ITEM) {
						double pcost = ho.getBuyPrice(1);
						line3 = HC.mc.applyColor("&f" + "Buy:");
						line4 = HC.mc.applyColor("&a" + L.fCS(CommonFunctions.twoDecimals((pcost + ho.getPurchaseTax(pcost)) * multiplier)));
					} else {
						double pcost = ho.getBuyPrice(1);
						line3 = HC.mc.applyColor("&f" + "Buy:");
						line4 = HC.mc.applyColor("&a" + L.fCS(CommonFunctions.twoDecimals((pcost + ho.getPurchaseTax(pcost)) * multiplier)));
					}
					break;
				case SELL:
					if (ho.getType() == TradeObjectType.ENCHANTMENT) {
						double value = ho.getSellPrice(enchantClass);
						value = CommonFunctions.twoDecimals((value - ho.getSalesTaxEstimate(value)) * multiplier);
						line3 = HC.mc.applyColor("&f" + "Sell:");
						line4 = HC.mc.applyColor("&a" + L.fCS(value));
					} else if (ho.getType() == TradeObjectType.ITEM) {
						double value = ho.getSellPrice(1);
						value = CommonFunctions.twoDecimals((value - ho.getSalesTaxEstimate(value)) * multiplier);
						line3 = HC.mc.applyColor("&f" + "Sell:");
						line4 = HC.mc.applyColor("&a" + L.fCS(value));
					} else {
						double value = ho.getSellPrice(1);
						value = CommonFunctions.twoDecimals((value - ho.getSalesTaxEstimate(value)) * multiplier);
						line3 = HC.mc.applyColor("&f" + "Sell:");
						line4 = HC.mc.applyColor("&a" + L.fCS(value));
					}
					break;
				case STOCK:
					line3 = HC.mc.applyColor("&f" + "Stock:");
					line4 = HC.mc.applyColor("&a" + "" + CommonFunctions.twoDecimals(ho.getStock()));
					break;
				case TOTALSTOCK:
					line3 = HC.mc.applyColor("&f" + "Total Stock:");
					line4 = HC.mc.applyColor("&a" + "" + CommonFunctions.twoDecimals(ho.getTotalStock()));
					break;
				case VALUE:
					line3 = HC.mc.applyColor("&f" + "Value:");
					line4 = HC.mc.applyColor("&a" + "" + ho.getValue() * multiplier);
					break;
				case STATUS:
					boolean staticstatus;
					staticstatus = Boolean.parseBoolean(ho.getIsstatic());
					line3 = HC.mc.applyColor("&f" + "Status:");
					if (staticstatus) {
						line4 = HC.mc.applyColor("&a" + "Static");
					} else {
						boolean initialstatus;
						initialstatus = Boolean.parseBoolean(ho.getInitiation());
						if (initialstatus) {
							line4 = HC.mc.applyColor("&a" + "Initial");
						} else {
							line4 = HC.mc.applyColor("&a" + "Dynamic");
						}
					}
					break;
				case STATICPRICE:
					line3 = HC.mc.applyColor("&f" + "Static Price:");
					line4 = HC.mc.applyColor("&a" + "" + ho.getStaticprice() * multiplier);
					break;
				case STARTPRICE:
					line3 = HC.mc.applyColor("&f" + "Start Price:");
					line4 = HC.mc.applyColor("&a" + "" + ho.getStartprice() * multiplier);
					break;
				case MEDIAN:
					line3 = HC.mc.applyColor("&f" + "Median:");
					line4 = HC.mc.applyColor("&a" + "" + ho.getMedian());
					break;
				case HISTORY:
					String increment = HC.mc.removeColor(line4.replace(" ", "")).toUpperCase().replaceAll("[0-9]", "");
					if (increment.contains("(")) {
						increment = increment.substring(0, increment.indexOf("("));
					}
					String timev = HC.mc.removeColor(line4.replace(" ", "")).toUpperCase().replaceAll("[A-Z]", "");
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
					if (ho.getType() == TradeObjectType.ENCHANTMENT) {
						double price = ho.getBuyPrice(enchantClass);
						double taxpaid = CommonFunctions.twoDecimals(ho.getPurchaseTax(price) * multiplier);
						line3 = HC.mc.applyColor("&f" + "Tax:");
						line4 = HC.mc.applyColor("&a" + "" + L.fCS(taxpaid));
					} else if (ho.getType() == TradeObjectType.ITEM) {
						line3 = HC.mc.applyColor("&f" + "Tax:");
						line4 = HC.mc.applyColor("&a" + L.fCS(CommonFunctions.twoDecimals(ho.getPurchaseTax(ho.getBuyPrice(1) * multiplier))));
					} else {
						BasicTradeObject bo = (BasicTradeObject)ho;
						line3 = HC.mc.applyColor("&f" + "Tax:");
						line4 = HC.mc.applyColor("&a" + L.fCS(CommonFunctions.twoDecimals(bo.getPurchaseTax(bo.getBuyPrice(1) * multiplier))));
					}
					break;
				case SB:
					if (ho.getType() == TradeObjectType.ENCHANTMENT) {
						double cost = ho.getBuyPrice(enchantClass);
						cost = CommonFunctions.twoDecimals((cost + ho.getPurchaseTax(cost)) * multiplier);
						line4 = HC.mc.applyColor("&f" + "B:" + "\u00A7a" + L.fCS(cost));
						double value = ho.getSellPrice(enchantClass);
						value = CommonFunctions.twoDecimals((value - ho.getSalesTaxEstimate(value)) * multiplier);
						line3 = HC.mc.applyColor("&f" + "S:" + "&a" + L.fCS(value));
					} else if (ho.getType() == TradeObjectType.ITEM) {
						double pcost = ho.getBuyPrice(1);
						line4 = HC.mc.applyColor("&f" + "B:" + "&a" + L.fCS(CommonFunctions.twoDecimals((pcost + ho.getPurchaseTax(pcost)) * multiplier)));
						double value = ho.getSellPrice(1);
						value = CommonFunctions.twoDecimals((value - ho.getSalesTaxEstimate(value)) * multiplier);
						line3 = HC.mc.applyColor("&f" + "S:" + "&a" + L.fCS(value));
					} else {
						double pcost = ho.getBuyPrice(1);
						line4 = HC.mc.applyColor("&f" + "B:" + "&a" + L.fCS(CommonFunctions.twoDecimals((pcost + ho.getPurchaseTax(pcost)) * multiplier)));
						double value = ho.getSellPrice(1);
						value = CommonFunctions.twoDecimals((value - ho.getSalesTaxEstimate(value)) * multiplier);
						line3 = HC.mc.applyColor("&f" + "S:" + "&a" + L.fCS(value));
					}
					break;
				default:
					break;
			}
			if (!type.equals(SignType.HISTORY)) {
				HSign s = getSign();
				if (s != null) {
					s.setLine(0, line1);
					s.setLine(1, line2);
					s.setLine(2, line3);
					s.setLine(3, line4);
					s.update();
				}
			}
		} catch (Exception e) {
			hc.gDB().writeError(e);
		}
	}

	private void updateHistorySign(int timevalueHours, int timevalue, String inc) {
		try {
			this.timeValueHours = timevalueHours;
			this.timeValue = timevalue;
			this.increment = inc;
			new Thread(new Runnable() {
				public void run() {
					String percentchange = hc.getHistory().getPercentChange(ho, timeValueHours);
					String colorcode = getcolorCode(percentchange);
					line3 = HC.mc.applyColor("&f" + "History:");
					line4 = HC.mc.applyColor("&f" + "" + timeValue + increment.toLowerCase() + colorcode + "(" + percentchange + ")");
					if (line3.length() > 14) {
						line3 = line3.substring(0, 13) + ")";
					}
					HC.mc.runTask(new Runnable() {
						public void run() {
							HSign s = getSign();
							if (s != null) {
								s.setLine(0, line1);
								s.setLine(1, line2);
								s.setLine(2, line3);
								s.setLine(3, line4);
								s.update();
							}
						}
					});
				}
			}).start();
		} catch (Exception e) {
			hc.gDB().writeError(e);
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
		hc.getInfoSignHandler().removeSign(this);
		HashMap<String,String> conditions = new HashMap<String,String>();
		conditions.put("WORLD", loc.getWorld());
		conditions.put("X", loc.getBlockX()+"");
		conditions.put("Y", loc.getBlockY()+"");
		conditions.put("Z", loc.getBlockZ()+"");
		hc.getSQLWrite().performDelete("hyperconomy_info_signs", conditions);
	}
	
	/*
	public boolean isValid() {
		Sign s = getSign();
		if (s != null) {
			return true;
		}
		return false;
	}
	*/
	
	public HSign getSign() {
		if (loc == null) return null;
		if (!loc.isLoaded()) loc.load();
		return HC.mc.getSign(loc);
	}
	
	
}
