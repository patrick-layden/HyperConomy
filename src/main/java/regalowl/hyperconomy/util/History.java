package regalowl.hyperconomy.util;

import java.util.ArrayList;
import java.util.HashMap;

import regalowl.simpledatalib.CommonFunctions;
import regalowl.simpledatalib.sql.QueryResult;
import regalowl.simpledatalib.sql.SQLRead;
import regalowl.simpledatalib.sql.SQLWrite;
import regalowl.hyperconomy.DataManager;
import regalowl.hyperconomy.HyperConomy;
import regalowl.hyperconomy.display.InfoSignHandler;
import regalowl.hyperconomy.tradeobject.EnchantmentClass;
import regalowl.hyperconomy.tradeobject.TradeObject;
import regalowl.hyperconomy.tradeobject.TradeObjectType;


/**
 * 
 * 
 * This class stores item price history in history.yml  (Value/Purchase price.)
 * 
 */
public class History {
	
	private HyperConomy hc;
	private DataManager em;
	private InfoSignHandler isign;
	private SQLWrite sw;
	private SQLRead sr;

	private long historylogtaskid;

	private int daysToSaveHistory;
	
	private long lastTime;
	private long timeCounter;
	private boolean useHistory;
	private boolean timeCounterAdded;
	
	private final int millisecondsInHour = 3600000;
	//private final int millisecondsInHour = 600;
	
	public History(HyperConomy hc) {
		this.hc = hc;
		useHistory = hc.getConf().getBoolean("enable-feature.price-history-storage");
		if (!useHistory) {return;}
		em = hc.getDataManager();
		isign = hc.getInfoSignHandler();
		sw = hc.getSQLWrite();
		sr = hc.getSQLRead();
		daysToSaveHistory = hc.getConf().getInt("history.days-to-save");
		lastTime = System.currentTimeMillis();
		timeCounter = getTimeCounter();
		startTimer();
	}
	
	public boolean useHistory() {
		return useHistory;
	}
	

	public Long getTimeCounter() {
		Long value = 0L;
		QueryResult result = sr.select("SELECT VALUE FROM hyperconomy_settings WHERE SETTING = 'history_time_counter'");
		if (result.next()) {
			try {
				value = Long.parseLong(result.getString("VALUE"));
			} catch (Exception e) {
				value = 0L;
			}
		} else {
			if (!timeCounterAdded) {
				timeCounterAdded = true;
				addSetting("history_time_counter", "0");
			}
		}
		result.close();
		return value;
	}

	public void addSetting(String setting, String value) {
		sw.addToQueue("INSERT INTO hyperconomy_settings (SETTING, VALUE, TIME) VALUES ('" + setting + "', '" + value + "', NOW() )");
	}

	public void updateSetting(String setting, String value) {
		sw.addToQueue("UPDATE hyperconomy_settings SET VALUE='" + value + "' WHERE SETTING = '" + setting + "'");
	}

	
	private void startTimer() {
		historylogtaskid = hc.getMC().runRepeatingTask(new Runnable() {
			@Override
			public void run() {
				long currentTime = System.currentTimeMillis();
				timeCounter += (currentTime - lastTime);
				lastTime = currentTime;
				if (timeCounter >= millisecondsInHour) {
					timeCounter = 0;
					writeHistoryValues();
					hc.getMC().runTaskLater(new Runnable() {
						@Override
						public void run() {
							if (isign != null) isign.updateSigns();
						}
					}, 1200L);
				}
				updateSetting("history_time_counter", timeCounter + "");
			}
		}, 600L, 600L);
	}
	

	
	private void writeHistoryValues() {
		ArrayList<TradeObject> objects = em.getTradeObjects();
		ArrayList<String> statements = new ArrayList<String>();
		for (TradeObject object : objects) {
			statements.add("INSERT INTO hyperconomy_history (OBJECT, ECONOMY, TIME, PRICE) "
					+ "VALUES ('"+object.getName()+"','"+object.getEconomy()+"', NOW() ,'"+object.getBuyPrice(1)+"')");
		}
		if (hc.getSQLManager().useMySQL()) {
			statements.add("DELETE FROM hyperconomy_history WHERE TIME < DATE_SUB(NOW(), INTERVAL " + daysToSaveHistory + " DAY)");
		} else {
			statements.add("DELETE FROM hyperconomy_history WHERE TIME < date('now','" + formatSQLiteTime(daysToSaveHistory * -1) + " day')");
		}
		sw.addToQueue(statements);
	}

    
    public void stopHistoryLog() {
    	hc.getMC().cancelTask(historylogtaskid);
    }

	public double getHistoricValue(String name, String economy, int count) {
		try {
			count -= 1;
			QueryResult result = sr.select("SELECT PRICE FROM hyperconomy_history WHERE OBJECT = '"+name+"' AND ECONOMY = '"+economy+"' ORDER BY TIME DESC LIMIT "+count+",1");
			if (result.next()) {
				return Double.parseDouble(result.getString("PRICE"));
			}
			result.close();
			return -1.0;
		} catch (Exception e) {
			hc.gSDL().getErrorWriter().writeError(e, "getHistoricValue() passed arguments: name = '" + name + "', economy = '" + economy + "', count = '" + count + "'");
			return -1.0;
		}
	}

	/**
	 * This function must be called from an asynchronous thread!
	 * @param ho
	 * @param timevalue
	 * @return The percentage change in theoretical price for the given object and timevalue in hours
	 */
    
	public synchronized String getPercentChange(TradeObject ho, int timevalue) {
		if (ho == null || sr == null) {
			hc.gSDL().getErrorWriter().writeError("getPercentChange passed null HyperObject or SQLRead");
			return "?";
		}
		double percentChange = 0.0;
		double historicvalue = getHistoricValue(ho.getName(), ho.getEconomy(), timevalue);
		if (historicvalue == -1.0 || historicvalue == 0.0) return "?";
		double currentvalue = ho.getBuyPrice(1);
		percentChange = ((currentvalue - historicvalue) / historicvalue) * 100.0;
		percentChange = CommonFunctions.round(percentChange, 3);
		return percentChange + "";
	}
	
	//TODO improve performance
	/**
	 * This function must be called from an asynchronous thread!
	 * @param timevalue
	 * @param economy
	 * @return The percentage change in theoretical price for the given object and timevalue in hours
	 */
	public synchronized HashMap<TradeObject, String> getPercentChange(String economy, int timevalue) {
		if (sr == null) return null;
		HashMap<TradeObject, ArrayList<Double>> allValues = new HashMap<TradeObject, ArrayList<Double>>();
		QueryResult result = sr.select("SELECT OBJECT, PRICE FROM hyperconomy_history WHERE ECONOMY = '" + economy + "' ORDER BY TIME DESC");
		while (result.next()) {
			TradeObject ho = em.getEconomy(economy).getTradeObject(result.getString("OBJECT"));
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
		
		ArrayList<TradeObject> hobjects =  em.getEconomy(economy).getTradeObjects();
		HashMap<TradeObject, String> relevantValues = new HashMap<TradeObject, String>();
		for (TradeObject ho:hobjects) {
			if (allValues.containsKey(ho)) {
				ArrayList<Double> historicValues = allValues.get(ho);
				if (historicValues.size() >= timevalue) {
					double historicValue = historicValues.get(timevalue - 1);
					double currentvalue = 0.0;
					if (ho.getType() == TradeObjectType.ENCHANTMENT) {
						currentvalue = ho.getSellPrice(EnchantmentClass.DIAMOND);
					} else if (ho.getType() == TradeObjectType.ITEM) {
						currentvalue = ho.getBuyPrice(1);
					} else {
						currentvalue = ho.getBuyPrice(1);
					}
					if (historicValue == 0.0) {
						relevantValues.put(ho, "?");
						continue;
					}
					double percentChange = ((currentvalue - historicValue) / historicValue) * 100.0;
					percentChange = CommonFunctions.round(percentChange, 3);
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
