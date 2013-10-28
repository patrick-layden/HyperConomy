package regalowl.hyperconomy;

import java.util.ArrayList;
import java.util.HashMap;

import regalowl.databukkit.CommonFunctions;
import regalowl.databukkit.QueryResult;
import regalowl.databukkit.SQLRead;
import regalowl.databukkit.SQLWrite;


/**
 * 
 * 
 * This class stores item price history in history.yml  (Value/Purchase price.)
 * 
 */
public class History {
	
	private HyperConomy hc;
	private EconomyManager em;
	private InfoSignHandler isign;
	private SQLWrite sw;
	private SQLRead sr;

	private int historylogtaskid;

	private int daysToSaveHistory;
	
	private long lastTime;
	private long timeCounter;
	private boolean useHistory;
	
	History() {
		hc = HyperConomy.hc;
		useHistory = hc.gYH().gFC("config").getBoolean("config.store-price-history");
		if (!useHistory) {return;}
		em = hc.getEconomyManager();
		isign = hc.getInfoSignHandler();
		sw = hc.getSQLWrite();
		sr = hc.getSQLRead();
		daysToSaveHistory = hc.gYH().gFC("config").getInt("config.daystosavehistory");
		lastTime = System.currentTimeMillis();
		String tc = getSettingValue("history_time_counter");
		if (tc == null) {
			addSetting("history_time_counter", "0");
			timeCounter = 0;
		} else {
			try {
				timeCounter = Long.parseLong(tc);
			} catch (Exception e) {
				hc.gDB().writeError(e);
			}
		}
		startTimer();
	}
	
	public boolean useHistory() {
		return useHistory;
	}
	

	public String getSettingValue(String setting) {
		String value = null;
		QueryResult result = sr.aSyncSelect("SELECT VALUE FROM hyperconomy_settings WHERE SETTING = '" + setting + "'");
		if (result.next()) {
			value = result.getString("VALUE");
		}
		result.close();
		return value;
	}

	public void addSetting(String setting, String value) {
		sw.convertExecuteSQL("INSERT INTO hyperconomy_settings (SETTING, VALUE, TIME) VALUES ('" + setting + "', '" + value + "', NOW() )");
	}

	public void updateSetting(String setting, String value) {
		sw.executeSQL("UPDATE hyperconomy_settings SET VALUE='" + value + "' WHERE SETTING = '" + setting + "'");
	}

	
	private void startTimer() {
		historylogtaskid = hc.getServer().getScheduler().scheduleSyncRepeatingTask(hc, new Runnable() {
			public void run() {
				long currentTime = System.currentTimeMillis();
				timeCounter += (currentTime - lastTime);
				lastTime = currentTime;
				if (timeCounter >= 3600000) {
					// if (timeCounter >= 120000) {
					timeCounter = 0;
					writeHistoryThread();
					hc.getServer().getScheduler().scheduleSyncDelayedTask(hc, new Runnable() {
						public void run() {
							if (isign != null) {
								isign.updateSigns();
							}
						}
					}, 1200L);
				}
				updateSetting("history_time_counter", timeCounter + "");
			}
		}, 600, 600);
	}
	

	
	private void writeHistoryThread() {
		ArrayList<HyperObject> objects = em.getHyperObjects();
		for (HyperObject object : objects) {
			if (object instanceof HyperEnchant) {
				HyperEnchant he = (HyperEnchant)object;
				writeHistoryData(object.getName(), object.getEconomy(), he.getValue(EnchantmentClass.DIAMOND));
			} else if (object instanceof HyperItem) {
				HyperItem hi = (HyperItem)object;
				writeHistoryData(object.getName(), object.getEconomy(), hi.getValue(1));
			} else if (object instanceof BasicObject) {
				BasicObject bo = (BasicObject)object;
				writeHistoryData(object.getName(), object.getEconomy(), bo.getValue(1));
			}
		}
	}
  	
  	
  	
	private void writeHistoryData(String object, String economy, double price) {
		String statement = "Insert Into hyperconomy_history (OBJECT, ECONOMY, TIME, PRICE)" + " Values ('" + object + "','" + economy + "', NOW() ,'" + price + "')";
		sw.convertExecuteSQL(statement);
		if (hc.getDataBukkit().useMySQL()) {
			statement = "DELETE FROM hyperconomy_history WHERE TIME < DATE_SUB(NOW(), INTERVAL " + daysToSaveHistory + " DAY)";
		} else {
			statement = "DELETE FROM hyperconomy_history WHERE TIME < date('now','" + formatSQLiteTime(daysToSaveHistory * -1) + " day')";
		}
		sw.executeSQL(statement);
	}
  	
  	
    
    public void stopHistoryLog() {
    	hc.getServer().getScheduler().cancelTask(historylogtaskid);
    }
    
	public void clearHistory() {
		String statement = "DELETE FROM hyperconomy_history";
		hc.getSQLWrite().executeSQL(statement);
	}
	
	
	
	public double getHistoricValue(String name, String economy, int count) {
		try {
			count -= 1;
			QueryResult result = sr.aSyncSelect("SELECT PRICE FROM hyperconomy_history WHERE OBJECT = '" + name + "' AND ECONOMY = '" + economy + "' ORDER BY TIME DESC");
			int c = 0;
			while (result.next()) {
				if (c == count) {
					return Double.parseDouble(result.getString("PRICE"));
				}
				c++;
			}
			result.close();
			return -1.0;
		} catch (Exception e) {
			hc.gDB().writeError(e, "getHistoricValue() passed arguments: name = '" + name + "', economy = '" + economy + "', count = '" + count + "'");
			return -999999.0;
		}
	}
	
	/**
	 * This function must be called from an asynchronous thread!
	 * @param object
	 * @param timevalue
	 * @param economy
	 * @return The percentage change in theoretical price for the given object and timevalue in hours
	 */
	public synchronized String getPercentChange(HyperObject ho, int timevalue) {
		if (ho == null || sr == null) {
			return "?";
		}
		CommonFunctions cf = hc.getCommonFunctions();
		double percentChange = 0.0;
		double historicvalue = getHistoricValue(ho.getName(), ho.getEconomy(), timevalue);
		if (historicvalue == -1.0) {
			return "?";
		}
		double currentvalue = 0.0;
		
		if (ho instanceof HyperEnchant) {
			HyperEnchant he = (HyperEnchant)ho;
			currentvalue = he.getValue(EnchantmentClass.DIAMOND);
		} else if (ho instanceof HyperItem) {
			HyperItem hi = (HyperItem)ho;
			currentvalue = hi.getValue(1);
		} else if (ho instanceof BasicObject) {
			BasicObject bo = (BasicObject)ho;
			currentvalue = bo.getValue(1);
		}

		percentChange = ((currentvalue - historicvalue) / historicvalue) * 100;
		percentChange = cf.round(percentChange, 3);
		return percentChange + "";
	}
	
	
	/**
	 * This function must be called from an asynchronous thread!
	 * @param object
	 * @param timevalue
	 * @param economy
	 * @return The percentage change in theoretical price for the given object and timevalue in hours
	 */
	public synchronized HashMap<HyperObject, String> getPercentChange(String economy, int timevalue) {
		if (sr == null) {
			return null;
		}

		HashMap<HyperObject, ArrayList<Double>> allValues = new HashMap<HyperObject, ArrayList<Double>>();
		QueryResult result = sr.aSyncSelect("SELECT OBJECT, PRICE FROM hyperconomy_history WHERE ECONOMY = '" + economy + "' ORDER BY TIME DESC");
		while (result.next()) {
			HyperObject ho = em.getEconomy(economy).getHyperObject(result.getString("OBJECT"));
			double price = result.getDouble("PRICE");
			if (!allValues.containsKey(ho)) {
				ArrayList<Double> values = new ArrayList<Double>();
				values.add(price);
				allValues.put(ho, values);
			} else {
				ArrayList<Double> values = allValues.get(ho);
				values.add(price);
				allValues.put(ho, values);
			}
		}
		result.close();
		
		ArrayList<HyperObject> hobjects =  em.getEconomy(economy).getHyperObjects();
		HashMap<HyperObject, String> relevantValues = new HashMap<HyperObject, String>();
		for (HyperObject ho:hobjects) {
			if (allValues.containsKey(ho)) {
				ArrayList<Double> historicValues = allValues.get(ho);
				if (historicValues.size() >= timevalue) {
					double historicValue = historicValues.get(timevalue - 1);
					double currentvalue = 0.0;
					if (ho instanceof HyperEnchant) {
						HyperEnchant he = (HyperEnchant)ho;
						currentvalue = he.getValue(EnchantmentClass.DIAMOND);
					} else if (ho instanceof HyperItem) {
						HyperItem hi = (HyperItem)ho;
						currentvalue = hi.getValue(1);
					} else if (ho instanceof HyperXP) {
						HyperXP bo = (HyperXP)ho;
						currentvalue = bo.getValue(1);
					}
					double percentChange = ((currentvalue - historicValue) / historicValue) * 100;
					percentChange = hc.getCommonFunctions().round(percentChange, 3);
					String stringValue = percentChange + "";
					relevantValues.put(ho, stringValue);
				} else {
					relevantValues.put(ho, "?");
				}
			} else {
				relevantValues.put(ho, "?");
			}
		}
		return relevantValues;
	}

	public String formatSQLiteTime(int time) {
		if (time < 0) {
			return "-" + Math.abs(time);
		} else if (time > 0) {
			return "+" + time;
		} else {
			return "0";
		}
	}
  	
}
