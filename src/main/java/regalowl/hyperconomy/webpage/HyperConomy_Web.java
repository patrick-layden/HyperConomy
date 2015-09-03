package regalowl.hyperconomy.webpage;



import regalowl.simpledatalib.file.FileConfiguration;
import regalowl.hyperconomy.HyperConomy;


public class HyperConomy_Web {

	private HyperConomy hc;
	private WebHandler wh;
	
	
	private boolean enabled;
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
	
	private String webAPIPath;
	private boolean useWebAPI;

	
	public HyperConomy_Web(HyperConomy hc) {
		this.hc = hc;
		buildData();
	}

	public void restart() {
		disable();
		buildData();
	}

	
	private void buildData() {
		this.enabled = false;
		FileConfiguration config = hc.getConf();
		boolean enable = config.getBoolean("web-page.enable");
		if (!enable) return;
		this.enabled = true;
		backgroundColor = "#" + config.getString("web-page.background-color");
		fontColor = "#" + config.getString("web-page.font-color");
		borderColor = "#" + config.getString("web-page.border-color");
		backgroundColor = "#" + config.getString("web-page.background-color");
		increaseColor = "#" + config.getString("web-page.increase-value-color");
		decreaseColor = "#" + config.getString("web-page.decrease-value-color");
		highlightColor = "#" + config.getString("web-page.highlight-row-color");
		headerColor = "#" + config.getString("web-page.header-color");
		tableDataColor = "#" + config.getString("web-page.table-data-color");
		font = config.getString("web-page.font");
		fontSize = config.getInt("web-page.font-size");
		port = config.getInt("web-page.port");
		webAPIPath = config.getString("web-page.web-api-path");
		useWebAPI = config.getBoolean("web-page.enable-web-api");
		if (wh == null) wh = new WebHandler(this);
		if (!wh.serverStarted()) wh.startServer();
	}

	public void disable() {
		if (wh != null) {
			wh.endServer();
			wh = null;
		}
	}



	public WebHandler getWebHandler() {
		return wh;
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
	
	public int getPort() {
		return port;
	}
	
	public boolean useWebAPI() {
		return useWebAPI;
	}
	
	public String getWebAPIPath() {
		return webAPIPath;
	}
	
	public HyperConomy getHC() {
		return hc;
	}
	
	public boolean enabled() {
		return enabled;
	}

}
