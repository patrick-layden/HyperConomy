package regalowl.hyperconomy;

import java.util.ArrayList;


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
		if (hc.useMySQL()) {
			statement = "Insert Into hyperconomy_history (OBJECT, ECONOMY, TIME, PRICE)" + " Values ('" + object + "','" + economy + "', NOW() ,'" + price + "')";
		} else {
			statement = "Insert Into hyperconomy_history (OBJECT, ECONOMY, TIME, PRICE)" + " Values ('" + object + "','" + economy + "', datetime('NOW', 'localtime') ,'" + price + "')";
		}
		sw.executeSQL(statement);
		if (hc.useMySQL()) {
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
		String statement = "TRUNCATE TABLE hyperconomy_history";
		hc.getSQLWrite().executeSQL(statement);
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
		double historicvalue = sr.getHistoricValue(ho.getName(), ho.getEconomy(), timevalue);
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

  	
}
