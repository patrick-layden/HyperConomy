package regalowl.hyperconomy.timeeffects;

import java.util.HashMap;
import java.util.Random;

import regalowl.hyperconomy.HyperConomy;
import regalowl.hyperconomy.HyperEconomy;
import regalowl.hyperconomy.account.HyperAccount;
import regalowl.hyperconomy.tradeobject.TradeObject;


public class TimeEffect {

	private HyperConomy hc;
	private TimeEffectType type; //type of effect
	private String name; //name of TradeObject or HyperAccount
	private String economy; //name of TradeObject's economy, blank for accounts
	private double value; //value to balance on or random range
	private int seconds; //how frequently effect runs
	private double increment; //how much the affect changes the stock or balance by
	private int timeRemaining; //time remaining until effect runs again
	
	private HashMap<String,String> updateConditions = new HashMap<String,String>();
	
	public TimeEffect(HyperConomy hc, TimeEffectType type, String name, String economy, double value, int seconds, double increment, int timeRemaining) {
		this.hc = hc;
		this.type = type;
		this.name = name;
		this.economy = economy;
		this.value = value;
		this.seconds = seconds;
		this.increment = increment;
		this.timeRemaining = timeRemaining; 
		updateConditions.put("NAME", this.name);
		updateConditions.put("ECONOMY", this.economy);
		updateConditions.put("TYPE", this.type.toString());
	}
	
	public void save() {
		HashMap<String,String> values = new HashMap<String,String>();
		values.put("TYPE", type.toString());
		values.put("NAME", name);
		values.put("ECONOMY", economy);
		values.put("VALUE", value+"");
		values.put("SECONDS", seconds+"");
		values.put("INCREMENT", increment+"");
		values.put("TIME_REMAINING", timeRemaining+"");
		hc.getSQLWrite().performInsert("hyperconomy_time_effects", values);
	}
	
	public void delete() {
		hc.getSQLWrite().performDelete("hyperconomy_time_effects", updateConditions);
	}

	
	public void subtractTime(int seconds) {
		this.timeRemaining -= seconds;
		if (timeRemaining <= 0) {
			runEffect();
			int newTime = this.timeRemaining + this.seconds;
			this.timeRemaining = newTime;
			if (this.timeRemaining < 0) this.timeRemaining = 0; //in case subtract interval is larger than time effect seconds
		}
		HashMap<String,String> values = new HashMap<String,String>();
		values.put("TIME_REMAINING", timeRemaining+"");
		hc.getSQLWrite().performUpdate("hyperconomy_time_effects", values, updateConditions);
	}
	
	private void runEffect() {
		if (type == TimeEffectType.BALANCE_STOCK) {
			HyperEconomy he = hc.getDataManager().getEconomy(economy);
			if (he == null) return;
			TradeObject to = he.getTradeObject(name);
			if (to == null) return;
			double currentStock = to.getStock();
			if (currentStock < value) {
				if (currentStock + increment > value) {
					to.setStock(value);
				} else {
					to.setStock(currentStock + increment);
				}
			} else if (currentStock > value) {
				if (currentStock - increment < value) {
					to.setStock(value);
				} else {
					to.setStock(currentStock - increment);
				}
			}
		} else if (type == TimeEffectType.INCREASE_STOCK) {
			HyperEconomy he = hc.getDataManager().getEconomy(economy);
			if (he == null) return;
			TradeObject to = he.getTradeObject(name);
			if (to == null) return;
			double currentStock = to.getStock();
			to.setStock(currentStock + increment);
		} else if (type == TimeEffectType.DECREASE_STOCK) {
			HyperEconomy he = hc.getDataManager().getEconomy(economy);
			if (he == null) return;
			TradeObject to = he.getTradeObject(name);
			if (to == null) return;
			double newStock = to.getStock() - increment;
			if (newStock < 0) newStock = 0;
			to.setStock(newStock);
		} else if (type == TimeEffectType.BALANCE_BALANCE) {
			HyperAccount ha = hc.getDataManager().getAccount(name);
			if (ha == null) return;
			double currentBalance = ha.getBalance();
			if (currentBalance < value) {
				if (currentBalance + increment > value) {
					ha.setBalance(value);
				} else {
					ha.setBalance(currentBalance + increment);
				}
			} else if (currentBalance > value) {
				if (currentBalance - increment < value) {
					ha.setBalance(value);
				} else {
					ha.setBalance(currentBalance - increment);
				}
			}
		} else if (type == TimeEffectType.INCREASE_BALANCE) {
			HyperAccount ha = hc.getDataManager().getAccount(name);
			if (ha == null) return;
			ha.setBalance(ha.getBalance() + increment);
		} else if (type == TimeEffectType.DECREASE_BALANCE) {
			HyperAccount ha = hc.getDataManager().getAccount(name);
			if (ha == null) return;
			double newBalance = ha.getBalance() - increment;
			if (newBalance < 0) newBalance = 0;
			ha.setBalance(newBalance);
		} else if (type == TimeEffectType.RANDOM_STOCK) {
			HyperEconomy he = hc.getDataManager().getEconomy(economy);
			if (he == null) return;
			TradeObject to = he.getTradeObject(name);
			if (to == null) return;
			int max = (int) ((to.getStock() + increment) * 100);
			int min = (int) ((to.getStock() - increment) * 100);
			Random rand = new Random();
			int randomNum = (rand.nextInt((max - min) + 1) + min)/100;
			if (randomNum < 0) randomNum = 0;
			to.setStock(randomNum);
		} else if (type == TimeEffectType.RANDOM_BALANCE) {
			HyperAccount ha = hc.getDataManager().getAccount(name);
			if (ha == null) return;
			double currentBalance = ha.getBalance();
			int max = (int) ((currentBalance + increment) * 100);
			int min = (int) ((currentBalance - increment) * 100);
			Random rand = new Random();
			double randomNum = (rand.nextInt((max - min) + 1) + min)/100.0;
			if (randomNum < 0) randomNum = 0;
			ha.setBalance(randomNum);
		}
	}
	
	
	public void setType(TimeEffectType type) {
		this.type = type;
		HashMap<String,String> values = new HashMap<String,String>();
		values.put("TYPE", type.toString());
		hc.getSQLWrite().performUpdate("hyperconomy_time_effects", values, updateConditions);
	}
	public void setName(String name) {
		this.name = name;
		HashMap<String,String> values = new HashMap<String,String>();
		values.put("NAME", name);
		hc.getSQLWrite().performUpdate("hyperconomy_time_effects", values, updateConditions);
	}
	public void setEconomy(String economy) {
		this.economy = economy;
		HashMap<String,String> values = new HashMap<String,String>();
		values.put("ECONOMY", economy);
		hc.getSQLWrite().performUpdate("hyperconomy_time_effects", values, updateConditions);
	}
	public void setValue(double value) {
		this.value = value;
		HashMap<String,String> values = new HashMap<String,String>();
		values.put("VALUE", value+"");
		hc.getSQLWrite().performUpdate("hyperconomy_time_effects", values, updateConditions);
	}
	public void setSeconds(int seconds) {
		this.seconds = seconds;
		HashMap<String,String> values = new HashMap<String,String>();
		values.put("SECONDS", seconds+"");
		hc.getSQLWrite().performUpdate("hyperconomy_time_effects", values, updateConditions);
	}
	public void setIncrement(double increment) {
		this.increment = increment;
		HashMap<String,String> values = new HashMap<String,String>();
		values.put("INCREMENT", increment+"");
		hc.getSQLWrite().performUpdate("hyperconomy_time_effects", values, updateConditions);
	}
	public void setTimeRemaining(int timeRemaining) {
		this.timeRemaining = timeRemaining;
		HashMap<String,String> values = new HashMap<String,String>();
		values.put("TIME_REMAINING", timeRemaining+"");
		hc.getSQLWrite().performUpdate("hyperconomy_time_effects", values, updateConditions);
	}
	
	
	public TimeEffectType getType() {
		return type;
	}
	public String getName() {
		return name;
	}
	public String getEconomy() {
		return economy;
	}
	public double getValue() {
		return value;
	}
	public int getSeconds() {
		return seconds;
	}
	public double getIncrement() {
		return increment;
	}
	public int getTimeRemaining() {
		return timeRemaining;
	}
}
