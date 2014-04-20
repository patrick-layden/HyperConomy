package regalowl.hyperconomy.util;

import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.scheduler.BukkitRunnable;

import regalowl.hyperconomy.HyperConomy;


public class HyperConfig {
	
	private FileConfiguration fc;
	private ConcurrentHashMap<String, String> hConfig = new ConcurrentHashMap<String, String>();

	public HyperConfig(FileConfiguration fc) {
		this.fc = fc;
		Iterator<String> it = fc.getKeys(true).iterator();
		while (it.hasNext()) {
			String signKey = it.next().toString();
			String o = fc.getString(signKey);
			if (o != null) {
				hConfig.put(signKey, o);
			}
		}
	}
	
	public String getString(String key) {
		if (hConfig.containsKey(key)) {
			String o = hConfig.get(key);
			try {
				return o;
			} catch (Exception e) {
				return null;
			}
		}
		return null;
	}
	
	public Boolean getBoolean(String key) {
		if (hConfig.containsKey(key)) {
			String o = hConfig.get(key);
			try {
				return Boolean.parseBoolean(o);
			} catch (Exception e) {
				return null;
			}
		}
		return null;
	}
	
	public Integer getInt(String key) {
		if (hConfig.containsKey(key)) {
			String o = hConfig.get(key);
			try {
				return Integer.parseInt(o);
			} catch (Exception e) {
				return null;
			}
		}
		return null;
	}
	
	public Long getLong(String key) {
		if (hConfig.containsKey(key)) {
			String o = hConfig.get(key);
			try {
				return Long.parseLong(o);
			} catch (Exception e) {
				return null;
			}
		}
		return null;
	}
	
	public Double getDouble(String key) {
		if (hConfig.containsKey(key)) {
			String o = hConfig.get(key);
			try {
				return Double.parseDouble(o);
			} catch (Exception e) {
				return null;
			}
		}
		return null;
	}
	
	public void set(String path, Object o) {
		new SetTask(path, o).runTask(HyperConomy.hc);
	}
	
	private class SetTask extends BukkitRunnable {
		private String path;
		private Object obj;
		
    	public SetTask(String path, Object obj) {
    		this.path = path;
    		this.obj = obj;
    	}
    	
		public void run() {
			fc.set(path, obj);
		}
    }
	
}
