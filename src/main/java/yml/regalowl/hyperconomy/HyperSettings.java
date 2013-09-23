package regalowl.hyperconomy;


import org.bukkit.configuration.file.FileConfiguration;

public class HyperSettings {

	private HyperConomy hc;
	private FileConfiguration config;

	private int errorCount;
	private double apiVersion;
	private String serverVersion;
	private long saveinterval;
	private int savetaskid;

	private boolean useWebPage;
	private String backgroundColor;
	private String fontColor;
	private String borderColor;
	private String increaseColor;
	private String decreaseColor;
	private String highlightColor;
	private String headerColor;
	private String tableDataColor;
	private String font;
	private int fontSize;
	private int port;
	
	private double salestax;
	private double tax;
	private double statictax;
	private double enchanttax;
	private double initialtax;
	private boolean useHistory;
	

	HyperSettings() {
		new Update();
		hc = HyperConomy.hc;
		config = HyperConomy.hc.gYH().gFC("config");

		loadData();
		//errorResetActive = false;
		//loadErrorCount();
	}
	
	
	public void loadData() {
		apiVersion = config.getDouble("api-version");
		serverVersion = HyperConomy.hc.getServer().getPluginManager().getPlugin("HyperConomy").getDescription().getVersion();
		saveinterval = config.getLong("config.saveinterval");
		useWebPage = config.getBoolean("config.web-page.use-web-page");
		backgroundColor = "#" + config.getString("config.web-page.background-color");
		fontColor = "#" + config.getString("config.web-page.font-color");
		borderColor = "#" + config.getString("config.web-page.border-color");
		backgroundColor = "#" + config.getString("config.web-page.background-color");
		increaseColor = "#" + config.getString("config.web-page.increase-value-color");
		decreaseColor = "#" + config.getString("config.web-page.decrease-value-color");
		highlightColor = "#" + config.getString("config.web-page.highlight-row-color");
		headerColor = "#" + config.getString("config.web-page.header-color");
		tableDataColor = "#" + config.getString("config.web-page.table-data-color");
		font = config.getString("config.web-page.font");
		fontSize = config.getInt("config.web-page.font-size");
		
		tax = config.getDouble("config.purchasetaxpercent");
		enchanttax = config.getDouble("config.enchanttaxpercent");
		salestax = config.getDouble("config.sales-tax-percent");
		initialtax = config.getDouble("config.initialpurchasetaxpercent");
		statictax = config.getDouble("config.statictaxpercent");
		port = config.getInt("config.web-page.port");
		useHistory = config.getBoolean("config.store-price-history");
	}
	
	public String gS(String path) {
		return config.getString("config." + path);
	}
	public int gI(String path) {
		return config.getInt("config." + path);
	}
	public double gD(String path) {
		return config.getDouble("config." + path);
	}
	public boolean gB(String path) {
		return config.getBoolean("config." + path);
	}
	
	
	public void sS(String path, String value) {
		config.set("config." + path, value);
	}
	public void sI(String path, int value) {
		config.set("config." + path, value);
	}
	public void sD(String path, double value) {
		config.set("config." + path, value);
	}
	public void sB(String path, boolean value) {
		config.set("config." + path, value);
	}
	
	
	
	

	public double getApiVersion() {
		return apiVersion;
	}


	public String getServerVersion() {
		return serverVersion;
	}



	public String getBackgroundColor() {
		return backgroundColor;
	}

	public String getFontColor() {
		return fontColor;
	}

	public String getBorderColor() {
		return borderColor;
	}

	public String getIncreaseColor() {
		return increaseColor;
	}

	public String getDecreaseColor() {
		return decreaseColor;
	}

	public String getHighlightColor() {
		return highlightColor;
	}

	public String getHeaderColor() {
		return headerColor;
	}

	public String getTableDataColor() {
		return tableDataColor;
	}

	public String getFont() {
		return font;
	}

	public int getFontSize() {
		return fontSize;
	}

	public double getSalesTax() {
		return salestax;
	}

	public double getTax() {
		return tax;
	}

	public double getStaticTax() {
		return statictax;
	}

	public double getEnchantTax() {
		return enchanttax;
	}

	public double getInitialTax() {
		return initialtax;
	}

	public boolean getUseHistory() {
		return useHistory;
	}

	public int getPort() {
		return port;
	}

	public boolean useWebPage() {
		return useWebPage;
	}
	
	
	
	
	
	
	public long getsaveInterval() {
		return saveinterval;
	}

	public void setSaveInterval(long interval) {
		saveinterval = interval;
	}

	public void startSave() {
		savetaskid = hc.getServer().getScheduler().scheduleSyncRepeatingTask(hc, new Runnable() {
			public void run() {
				if (!hc.gYH().brokenFile()) {
					hc.gYH().saveYamls();
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

}