package regalowl.hyperconomy.display;

import java.util.HashMap;







import regalowl.simpledatalib.CommonFunctions;
import regalowl.hyperconomy.HyperConomy;
import regalowl.hyperconomy.HyperEconomy;
import regalowl.hyperconomy.minecraft.HBlock;
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
	private TradeObject to;
	private HLocation loc;
	private HyperConomy hc;
	private LanguageFile L;
	private String line1;
	private String line2;
	private String line3;
	private String line4;
	
	private int timeValueHours;
	private int timeValue;
	private String increment;

	public InfoSign(HyperConomy hc, HLocation signLoc, SignType type, String objectName, double multiplier, String economy, EnchantmentClass enchantClass) {
		this.multiplier = multiplier;
		if (enchantClass == null) {
			this.enchantClass = EnchantmentClass.DIAMOND;
		} else {
			this.enchantClass = enchantClass;
		}
		this.hc = hc;
		HyperEconomy he = hc.getDataManager().getEconomy(economy);
		L = hc.getLanguageFile();
		this.economy = "default";
		if (economy != null) {
			this.economy = economy;
		}
		this.loc = signLoc;
		this.type = type;
		to = he.getTradeObject(objectName);
		if (to == null) {
			deleteSign();
			return;
		}
		this.objectName = to.getDisplayName();
		HSign s = getSign();
		if (s == null) {
			deleteSign();
			return;
		}
		line1 = hc.getMC().removeColor(s.getLine(0).trim());
		line2 = hc.getMC().removeColor(s.getLine(1).trim());
		if (line1.length() > 13) {
			line2 = "&1" + line1.substring(13, line1.length()) + line2;
			line1 = "&1" + line1.substring(0, 13);
		} else {
			line1 = "&1" + line1;
			line2 = "&1" + line2;
		}
		line3 = s.getLine(2);
		line4 = s.getLine(3);
	}

	
	public InfoSign(HyperConomy hc, HLocation signLoc, SignType type, String objectName, double multiplier, String economy, EnchantmentClass enchantClass, String[] lines) {
		this.multiplier = multiplier;
		if (enchantClass == null) {
			this.enchantClass = EnchantmentClass.DIAMOND;
		} else {
			this.enchantClass = enchantClass;
		}
		this.hc = hc;
		HyperEconomy he = hc.getDataManager().getEconomy(economy);
		L = hc.getLanguageFile();
		this.economy = "default";
		this.loc = signLoc;
		this.type = type;
		if (economy != null) {
			this.economy = economy;
		}
		to = he.getTradeObject(objectName);
		if (to == null) {
			deleteSign();
			return;
		}
		this.objectName = to.getDisplayName();
		line1 = hc.getMC().removeColor(lines[0].trim());
		line2 = hc.getMC().removeColor(lines[1].trim());
		if (line1.length() > 13) {
			line2 = "&1" + line1.substring(13, line1.length()) + line2;
			line1 = "&1" + line1.substring(0, 13);
		} else {
			line1 = "&1" + line1;
			line2 = "&1" + line2;
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
	
	public TradeObject getTradeObject() {
		return to;
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
					if (to.getType() == TradeObjectType.ENCHANTMENT) {
						double cost = to.getBuyPrice(enchantClass);
						cost = CommonFunctions.twoDecimals((cost + to.getPurchaseTax(cost)) * multiplier);
						line3 = "&f" + "Buy:";
						line4 = "&a" + L.fCS(cost);
					} else if (to.getType() == TradeObjectType.ITEM) {
						double pcost = to.getBuyPrice(1);
						line3 = "&f" + "Buy:";
						line4 = "&a" + L.fCS(CommonFunctions.twoDecimals((pcost + to.getPurchaseTax(pcost)) * multiplier));
					} else {
						double pcost = to.getBuyPrice(1);
						line3 = "&f" + "Buy:";
						line4 = "&a" + L.fCS(CommonFunctions.twoDecimals((pcost + to.getPurchaseTax(pcost)) * multiplier));
					}
					break;
				case SELL:
					if (to.getType() == TradeObjectType.ENCHANTMENT) {
						double value = to.getSellPrice(enchantClass);
						value = CommonFunctions.twoDecimals((value - to.getSalesTaxEstimate(value)) * multiplier);
						line3 = "&f" + "Sell:";
						line4 = "&a" + L.fCS(value);
					} else if (to.getType() == TradeObjectType.ITEM) {
						double value = to.getSellPrice(1);
						value = CommonFunctions.twoDecimals((value - to.getSalesTaxEstimate(value)) * multiplier);
						line3 = "&f" + "Sell:";
						line4 = "&a" + L.fCS(value);
					} else {
						double value = to.getSellPrice(1);
						value = CommonFunctions.twoDecimals((value - to.getSalesTaxEstimate(value)) * multiplier);
						line3 = "&f" + "Sell:";
						line4 = "&a" + L.fCS(value);
					}
					break;
				case STOCK:
					line3 = "&f" + "Stock:";
					line4 = "&a" + "" + CommonFunctions.twoDecimals(to.getStock());
					break;
				case TOTALSTOCK:
					line3 = "&f" + "Total Stock:";
					line4 = "&a" + "" + CommonFunctions.twoDecimals(to.getTotalStock());
					break;
				case VALUE:
					line3 = "&f" + "Value:";
					line4 = "&a" + "" + to.getValue() * multiplier;
					break;
				case STATUS:
					boolean staticstatus;
					staticstatus = to.isStatic();
					line3 = "&f" + "Status:";
					if (staticstatus) {
						line4 = "&a" + "Static";
					} else {
						boolean initialstatus;
						initialstatus = to.useInitialPricing();
						if (initialstatus) {
							line4 = "&a" + "Initial";
						} else {
							line4 = "&a" + "Dynamic";
						}
					}
					break;
				case STATICPRICE:
					line3 = "&f" + "Static Price:";
					line4 = "&a" + "" + to.getStaticPrice() * multiplier;
					break;
				case STARTPRICE:
					line3 = "&f" + "Start Price:";
					line4 = "&a" + "" + to.getStartPrice() * multiplier;
					break;
				case MEDIAN:
					line3 = "&f" + "Median:";
					line4 = "&a" + "" + to.getMedian();
					break;
				case HISTORY:
					String timeIncrement = hc.getMC().removeColor(line4);
					if (timeIncrement.contains("(")) timeIncrement = timeIncrement.substring(0, timeIncrement.indexOf("("));
					timeIncrement = timeIncrement.toUpperCase().replaceAll("[^A-Z]", "");
					String timeValueString = hc.getMC().removeColor(line4);
					if (timeValueString.contains("(")) timeValueString = timeValueString.substring(0, timeValueString.indexOf("("));
					timeValueString = timeValueString.toUpperCase().replaceAll("[^0-9]", "");
					int timeValue = Integer.parseInt(timeValueString);
					int timeValueHours = timeValue;
					if (timeIncrement.equals("H")) {
						timeValueHours *= 1;
					} else if (timeIncrement.equals("D")) {
						timeValueHours *= 24;
					} else if (timeIncrement.equals("W")) {
						timeValueHours *= 168;
					} else if (timeIncrement.equals("M")) {
						timeValueHours *= 672;
					}
					updateHistorySign(timeValueHours, timeValue, timeIncrement);
					break;
				case TAX:
					if (to.getType() == TradeObjectType.ENCHANTMENT) {
						double price = to.getBuyPrice(enchantClass);
						double taxpaid = CommonFunctions.twoDecimals(to.getPurchaseTax(price) * multiplier);
						line3 = "&f" + "Tax:";
						line4 = "&a" + "" + L.fCS(taxpaid);
					} else if (to.getType() == TradeObjectType.ITEM) {
						line3 = "&f" + "Tax:";
						line4 = "&a" + L.fCS(CommonFunctions.twoDecimals(to.getPurchaseTax(to.getBuyPrice(1) * multiplier)));
					} else {
						BasicTradeObject bo = (BasicTradeObject)to;
						line3 = "&f" + "Tax:";
						line4 = "&a" + L.fCS(CommonFunctions.twoDecimals(bo.getPurchaseTax(bo.getBuyPrice(1) * multiplier)));
					}
					break;
				case SB:
					if (to.getType() == TradeObjectType.ENCHANTMENT) {
						double cost = to.getBuyPrice(enchantClass);
						cost = CommonFunctions.twoDecimals((cost + to.getPurchaseTax(cost)) * multiplier);
						line4 = "&f" + "B:" + "&a" + L.fCS(cost);
						double value = to.getSellPrice(enchantClass);
						value = CommonFunctions.twoDecimals((value - to.getSalesTaxEstimate(value)) * multiplier);
						line3 = "&f" + "S:" + "&a" + L.fCS(value);
					} else if (to.getType() == TradeObjectType.ITEM) {
						double pcost = to.getBuyPrice(1);
						line4 = "&f" + "B:" + "&a" + L.fCS(CommonFunctions.twoDecimals((pcost + to.getPurchaseTax(pcost)) * multiplier));
						double value = to.getSellPrice(1);
						value = CommonFunctions.twoDecimals((value - to.getSalesTaxEstimate(value)) * multiplier);
						line3 = "&f" + "S:" + "&a" + L.fCS(value);
					} else {
						double pcost = to.getBuyPrice(1);
						line4 = "&f" + "B:" + "&a" + L.fCS(CommonFunctions.twoDecimals((pcost + to.getPurchaseTax(pcost)) * multiplier));
						double value = to.getSellPrice(1);
						value = CommonFunctions.twoDecimals((value - to.getSalesTaxEstimate(value)) * multiplier);
						line3 = "&f" + "S:" + "&a" + L.fCS(value);
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
			hc.gSDL().getErrorWriter().writeError(e);
		}
	}

	private void updateHistorySign(int timevalueHours, int timevalue, String inc) {
		try {
			this.timeValueHours = timevalueHours;
			this.timeValue = timevalue;
			this.increment = inc;
			new Thread(new Runnable() {
				public void run() {
					String percentchange = hc.getHistory().getPercentChange(to, timeValueHours);
					String colorcode = getcolorCode(percentchange);
					line3 = "&f" + "History:";
					line4 = "&f" + "" + timeValue + increment.toLowerCase() + colorcode + "(" + percentchange + ")";
					if (line3.length() > 14) {
						line3 = line3.substring(0, 13) + ")";
					}
					hc.getMC().runTask(new Runnable() {
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
			hc.gSDL().getErrorWriter().writeError(e);
		}
	}
	

	private String getcolorCode(String percentchange) {
		String colorcode = "&1";
		if (percentchange.equalsIgnoreCase("?")) {
			colorcode = "&1";
		} else {
			Double percentc = Double.parseDouble(percentchange);
			if (percentc > 0) {
				colorcode = "&a";
			} else if (percentc < 0) {
				colorcode = "&4";
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
		HBlock sb = new HBlock(hc, loc);
		if (!sb.isLoaded()) sb.load();
		return hc.getMC().getSign(loc);
	}
	
	
}
