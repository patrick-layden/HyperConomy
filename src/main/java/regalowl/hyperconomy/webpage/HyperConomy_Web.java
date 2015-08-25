package regalowl.hyperconomy.webpage;


import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import regalowl.simpledatalib.SimpleDataLib;
import regalowl.simpledatalib.file.FileConfiguration;
import regalowl.simpledatalib.file.YamlHandler;
import regalowl.hyperconomy.HyperConomy;
import regalowl.hyperconomy.api.MineCraftConnector;
import regalowl.hyperconomy.event.HyperEventHandler;


public class HyperConomy_Web extends JavaPlugin {

	private HyperConomy hc;
	private HyperEventHandler heh;
	private SimpleDataLib db;
	private YamlHandler yh;
	private WebHandler wh;
	private Logger log;
	
	
	

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
	
	@Override
	public void onEnable() {
		System.out.println("HyperConomy_Web enabled.");
		enable();
	}
	@Override
	public void onDisable() {
		disable();
	}
	
	

	public void restart() {
		disable();
		enable();
		buildData();
	}
	
	private void enable() {
		Plugin hcPlugin = getServer().getPluginManager().getPlugin("HyperConomy");
		MineCraftConnector mc = (MineCraftConnector)hcPlugin;
		hc = mc.getHC();
		log = Logger.getLogger("Minecraft");
		heh = hc.getHyperEventHandler();
		heh.registerListener(this);
		registerCommands();
		db = new SimpleDataLib("HyperConomy_Web");
		db.initialize();
		yh = db.getYamlHandler();
		yh.copyFromJar("config");
		yh.registerFileConfiguration("config");
		FileConfiguration config = yh.getFileConfiguration("config");
		config.setDefault("enable-web-api", false);
		config.setDefault("web-api-path", "API");
		waitForLoad();
	}


	private void waitForLoad() {
		hc.getMC().runTaskLater(new Runnable() {
			public void run() {
				if (!hc.enabled()) {
					waitForLoad();
					return;
				}
				buildData();
			}
		}, 20L);
	}
	
	private void buildData() {
		FileConfiguration config = yh.gFC("config");
		backgroundColor = "#" + config.getString("background-color");
		fontColor = "#" + config.getString("font-color");
		borderColor = "#" + config.getString("border-color");
		backgroundColor = "#" + config.getString("background-color");
		increaseColor = "#" + config.getString("increase-value-color");
		decreaseColor = "#" + config.getString("decrease-value-color");
		highlightColor = "#" + config.getString("highlight-row-color");
		headerColor = "#" + config.getString("header-color");
		tableDataColor = "#" + config.getString("table-data-color");
		font = config.getString("font");
		fontSize = config.getInt("font-size");
		port = config.getInt("port");
		webAPIPath = config.getString("web-api-path");
		useWebAPI = config.getBoolean("enable-web-api");
		if (wh == null) wh = new WebHandler(this);
		if (!wh.serverStarted()) wh.startServer();
	}

	public void disable() {
		if (wh != null) {
			wh.endServer();
			wh = null;
		}
		if (db != null) {
			db.shutDown();
			db = null;
		}
		getServer().getScheduler().cancelTasks(this);
	}
	



	private void registerCommands() {
		Bukkit.getServer().getPluginCommand("hcweb").setExecutor(new Hcweb(this));
	}

	public WebHandler getWebHandler() {
		return wh;
	}
	
	public SimpleDataLib getSimpleDataLib() {
		return db;
	}
	
	public YamlHandler gYH() {
		return yh;
	}
	
	public Logger getLog() {
		return log;
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

}
