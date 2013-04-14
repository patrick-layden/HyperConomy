package regalowl.hyperconomy;

import java.io.File;
import java.util.ArrayList;

import org.bukkit.configuration.file.FileConfiguration;

public class HyperSettings {

	private HyperConomy hc;
	private FileConfiguration config;
	
	private int errorCount;
	private boolean blockCreativeSales;
	private double apiVersion;
	private boolean useExternalEconomy;
	private boolean logerrors;
	private String serverVersion;
	private long saveinterval;
	private boolean usemysql;
	private boolean useShopPermissions;

	
	private int savetaskid;
	private int tempErrorCounter;
	private boolean errorResetActive;
	
	
	HyperSettings() {
		new Update();
		hc = HyperConomy.hc;
		config = HyperConomy.hc.getYaml().getConfig();
		blockCreativeSales = config.getBoolean("config.block-selling-in-creative-mode");
		apiVersion = config.getDouble("api-version");
		useExternalEconomy = config.getBoolean("config.use-external-economy-plugin");
		logerrors = config.getBoolean("config.log-errors");
		serverVersion = HyperConomy.hc.getServer().getPluginManager().getPlugin("HyperConomy").getDescription().getVersion();
		saveinterval = config.getLong("config.saveinterval");
		usemysql = config.getBoolean("config.sql-connection.use-mysql");
		useShopPermissions = config.getBoolean("config.use-shop-permissions");
		
		errorResetActive = false;
		loadErrorCount();
	}
	
	
	public boolean blockCreative() {
		return blockCreativeSales;
	}
	
	public double getApiVersion() {
		return apiVersion;
	}
	
	public boolean useExternalEconomy() {
		return useExternalEconomy;
	}
	
	public void setUseExternalEconomy(boolean state) {
		useExternalEconomy = state;
	}
	
	public boolean logErrors() {
		return logerrors;
	}
	
	public String getServerVersion() {
		return serverVersion;
	}
	
	public boolean useMySQL() {
		return usemysql;
	}
	
	public void setUseMySQL(boolean usemysql) {
		this.usemysql = usemysql;
	}

	public long getsaveInterval() {
		return saveinterval;
	}

	public void setSaveInterval(long interval) {
		saveinterval = interval;
	}
	
	public boolean useShopPermissions() {
		return useShopPermissions;
	}
	
	
	
	
	
	
	
	public void startSave() {
		savetaskid = hc.getServer().getScheduler().scheduleSyncRepeatingTask(hc, new Runnable() {
			public void run() {
				if (!hc.getYaml().broken()) {
					hc.getYaml().saveYamls();
				}
			}
		}, saveinterval, saveinterval);
	}

	public void stopSave() {
		hc.getServer().getScheduler().cancelTask(savetaskid);
	}
	
	
	public int getErrorCount() {
		return errorCount;
	}
	
	public void raiseErrorCount() {
		errorCount++;
	}
	

	
	private void loadErrorCount() {
		FileTools ft = new FileTools();
		String path = ft.getJarPath() + File.separator + "plugins" + File.separator + "HyperConomy" + File.separator + "errors";
		ft.makeFolder(path);
		ArrayList<String> contents = ft.getFolderContents(path);
		if (contents.size() == 0) {
			errorCount = 0;
		} else {
			int max = 0;
			for (String folder:contents) {
				try {
					int cnum = Integer.parseInt(folder);
					if (cnum > max) {
						max = cnum;
					}
				} catch (Exception e) {
					continue;
				}
			}
			errorCount = max + 1;
		}
	}
	
	
	public void incrementErrorCount() {
		tempErrorCounter++;
		if (tempErrorCounter > 20) {
			hc.getServer().getScheduler().cancelTasks(hc);
			hc.log().severe("HyperConomy is experiencing a massive amount of errors...shutting down....");
			hc.shutDown(true);
			hc.getPluginLoader().disablePlugin(hc);
		}
		if (!errorResetActive) {
			errorResetActive = true;
			hc.getServer().getScheduler().scheduleSyncDelayedTask(hc, new Runnable() {
			    public void run() {
			    	tempErrorCounter = 0;
	    		    errorResetActive = false;
			    }
			}, 20L);
		}
	}
	
}
