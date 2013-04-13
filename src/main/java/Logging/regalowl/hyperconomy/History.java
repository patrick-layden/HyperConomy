package regalowl.hyperconomy;

import java.util.ArrayList;
import java.util.HashMap;


/**
 * 
 * 
 * This class stores item price history in history.yml  (Value/Purchase price.)
 * 
 */
public class History {
	
	private HyperConomy hc;
	private InfoSignHandler isign;
	private DataHandler df;
	private SQLWrite sw;
	private SQLRead sr;

	private int historylogtaskid;

	private int daysToSaveHistory;
	
	private long lastTime;
	private long timeCounter;
	
	History() {
		hc = HyperConomy.hc;
		isign = hc.getInfoSignHandler();
		sw = hc.getSQLWrite();
		df = hc.getDataFunctions();
		sr = hc.getSQLRead();
		daysToSaveHistory = hc.getYaml().getConfig().getInt("config.daystosavehistory");
		lastTime = System.currentTimeMillis();
		String tc = sr.getSettingValue("history_time_counter");
		if (tc == null) {
			sw.addSetting("history_time_counter", "0");
			timeCounter = 0;
		} else {
			try {
				timeCounter = Long.parseLong(tc);
			} catch (Exception e) {
				new HyperError(e);
			}
		}
		startTimer();
	}
	



	
    private void startTimer() {
    	if (hc.getYaml().getConfig().getBoolean("config.store-price-history")) {
			historylogtaskid = hc.getServer().getScheduler().scheduleSyncRepeatingTask(hc, new Runnable() {
			    public void run() {
			    	long currentTime = System.currentTimeMillis();
			    	timeCounter += (currentTime - lastTime);
			    	lastTime = currentTime;
			    	if (timeCounter >= 3600000) {
			    	//if (timeCounter >= 120000) {
			    		timeCounter = 0;
			    		writeHistoryThread();
						hc.getServer().getScheduler().scheduleSyncDelayedTask(hc, new Runnable() {
						    public void run() {
						    	isign.updateSigns();
						    }
						}, 1200L);
			    	}
			    	sw.updateSetting("history_time_counter", timeCounter + "");
			    }
			}, 600, 600);
    	}
    }
	

	
	private void writeHistoryThread() {
		ArrayList<HyperObject> objects = df.getHyperObjects();
		for (HyperObject object : objects) {
			if (object.getType() == HyperObjectType.ENCHANTMENT) {
				writeHistoryData(object.getName(), object.getEconomy(), object.getValue(EnchantmentClass.DIAMOND));
			} else {
				writeHistoryData(object.getName(), object.getEconomy(), object.getValue(1));
			}

		}
	}
  	
  	
  	
	private void writeHistoryData(String object, String economy, double price) {
		String statement = "";
		if (hc.s().useMySQL()) {
			statement = "Insert Into hyperconomy_history (OBJECT, ECONOMY, TIME, PRICE)" + " Values ('" + object + "','" + economy + "', NOW() ,'" + price + "')";
		} else {
			statement = "Insert Into hyperconomy_history (OBJECT, ECONOMY, TIME, PRICE)" + " Values ('" + object + "','" + economy + "', datetime('NOW', 'localtime') ,'" + price + "')";
		}
		sw.executeSQL(statement);
		if (hc.s().useMySQL()) {
			statement = "DELETE FROM hyperconomy_history WHERE TIME < DATE_SUB(NOW(), INTERVAL " + daysToSaveHistory + " DAY)";
		} else {
			statement = "DELETE FROM hyperconomy_history WHERE TIME < date('now','" + df.formatSQLiteTime(daysToSaveHistory * -1) + " day')";
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
			QueryResult result = sr.getDatabaseConnection().read("SELECT PRICE FROM hyperconomy_history WHERE OBJECT = '" + name + "' AND ECONOMY = '" + economy + "' ORDER BY TIME DESC");
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
			new HyperError(e, "getHistoricValue() passed arguments: name = '" + name + "', economy = '" + economy + "', count = '" + count + "'");
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
	public String getPercentChange(HyperObject ho, int timevalue) {
		if (ho == null || sr == null) {
			return "?";
		}
		Calculation calc = hc.getCalculation();
		double percentChange = 0.0;
		double historicvalue = getHistoricValue(ho.getName(), ho.getEconomy(), timevalue);
		if (historicvalue == -1.0) {
			return "?";
		}
		double currentvalue = 0.0;
		if (ho.getType() == HyperObjectType.ENCHANTMENT) {
			currentvalue = ho.getValue(EnchantmentClass.DIAMOND);
		} else {
			currentvalue = ho.getValue(1);
		}
		percentChange = ((currentvalue - historicvalue) / historicvalue) * 100;
		percentChange = calc.round(percentChange, 3);
		return percentChange + "";
	}
	
	
	/**
	 * This function must be called from an asynchronous thread!
	 * @param object
	 * @param timevalue
	 * @param economy
	 * @return The percentage change in theoretical price for the given object and timevalue in hours
	 */
	public HashMap<HyperObject, String> getPercentChange(String economy, int timevalue) {
		if (sr == null) {
			return null;
		}

		HashMap<HyperObject, ArrayList<Double>> allValues = new HashMap<HyperObject, ArrayList<Double>>();
		QueryResult result = sr.getDatabaseConnection().read("SELECT OBJECT, PRICE FROM hyperconomy_history WHERE ECONOMY = '" + economy + "' ORDER BY TIME DESC");
		while (result.next()) {
			HyperObject ho = hc.getDataFunctions().getHyperObject(result.getString("OBJECT"), economy);
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
		
		ArrayList<HyperObject> hobjects = hc.getDataFunctions().getHyperObjects(economy);
		HashMap<HyperObject, String> relevantValues = new HashMap<HyperObject, String>();
		for (HyperObject ho:hobjects) {
			if (allValues.containsKey(ho)) {
				ArrayList<Double> historicValues = allValues.get(ho);
				if (historicValues.size() >= timevalue) {
					double historicValue = historicValues.get(timevalue - 1);
					double currentvalue = 0.0;
					if (ho.getType() == HyperObjectType.ENCHANTMENT) {
						currentvalue = ho.getValue(EnchantmentClass.DIAMOND);
					} else {
						currentvalue = ho.getValue(1);
					}
					double percentChange = ((currentvalue - historicValue) / historicValue) * 100;
					percentChange = hc.getCalculation().round(percentChange, 3);
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

  	
}
