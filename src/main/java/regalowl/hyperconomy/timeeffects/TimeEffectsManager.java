package regalowl.hyperconomy.timeeffects;

import java.util.ArrayList;

import regalowl.hyperconomy.HyperConomy;
import regalowl.simpledatalib.sql.QueryResult;

public class TimeEffectsManager {

	
	private transient HyperConomy hc;
	private ArrayList<TimeEffect> timeEffects = new ArrayList<TimeEffect>();
	private long timerTaskId;
	
	
	public TimeEffectsManager(HyperConomy hc) {
		this.hc = hc;
		if (!hc.getConf().getBoolean("enable-feature.time-effects")) return;
		timeEffects.clear();
		
		QueryResult data = hc.getSQLRead().select("SELECT * FROM hyperconomy_time_effects");
		while (data.next()) {
			TimeEffectType type = TimeEffectType.fromString(data.getString("TYPE"));
			if (type == TimeEffectType.NONE) continue;
			String name = data.getString("NAME");
			String economy = data.getString("ECONOMY");
			double value = data.getDouble("VALUE");
			int seconds = data.getInt("SECONDS");
			double increment = data.getDouble("INCREMENT");
			int timeRemaining = data.getInt("TIME_REMAINING");
			timeEffects.add(new TimeEffect(hc, type, name, economy, value, seconds, increment, timeRemaining));
		}
		data.close();
		startTimer();
	}
	
	public void disable() {
		hc.getMC().cancelTask(timerTaskId);
		timeEffects.clear();
	}
	
	private void startTimer() {
		timerTaskId = hc.getMC().runRepeatingTask(new Runnable() {
			@Override
			public void run() {
				for (TimeEffect te:timeEffects) {
					te.subtractTime(10);
				}
			}
		}, 200L, 200L);
	}
	
	public TimeEffect getTimeEffect(String name, String economy, TimeEffectType type) {
		for (TimeEffect te:timeEffects) {
			if (te.getName().equals(name) && te.getEconomy().equals(economy) && te.getType() == type) {
				return te;
			}
		}
		return null;
	}
	
	public boolean hasTimeEffect(String name, String economy, TimeEffectType type) {
		if (getTimeEffect(name, economy, type) == null) return false;
		return true;
	}
	
	public void addNewTimeEffect(TimeEffect te) {
		if (te == null) return;
		if (!timeEffects.contains(te)) {
			timeEffects.add(te);
			te.save();
		}
	}
	
	public void deleteTimeEffect(TimeEffect te) {
		if (te == null) return;
		timeEffects.remove(te);
		te.delete();
	}
	
	public ArrayList<TimeEffect> getTimeEffects() {
		ArrayList<TimeEffect> effects = new ArrayList<TimeEffect>();
		effects.addAll(timeEffects);
		return effects;
	}

	
}
