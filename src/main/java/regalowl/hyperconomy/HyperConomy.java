package regalowl.hyperconomy;


import regalowl.databukkit.CommonFunctions;
import regalowl.databukkit.DataBukkit;
import regalowl.databukkit.event.EventHandler;
import regalowl.databukkit.events.LogEvent;
import regalowl.databukkit.events.LogLevel;
import regalowl.databukkit.events.ShutdownEvent;
import regalowl.databukkit.file.FileConfiguration;
import regalowl.databukkit.file.FileTools;
import regalowl.databukkit.file.YamlHandler;
import regalowl.databukkit.sql.SQLManager;
import regalowl.databukkit.sql.SQLRead;
import regalowl.databukkit.sql.SQLWrite;
import regalowl.hyperconomy.api.API;
import regalowl.hyperconomy.api.EconomyAPI;
import regalowl.hyperconomy.api.HyperAPI;
import regalowl.hyperconomy.api.HyperEconAPI;
import regalowl.hyperconomy.bukkit.FrameShopHandler;
import regalowl.hyperconomy.command.Additem;
import regalowl.hyperconomy.command.Audit;
import regalowl.hyperconomy.command.Browseshop;
import regalowl.hyperconomy.command.Buy;
import regalowl.hyperconomy.command.Economyinfo;
import regalowl.hyperconomy.command.Frameshopcommand;
import regalowl.hyperconomy.command.Hb;
import regalowl.hyperconomy.command.Hc;
import regalowl.hyperconomy.command.Hcbalance;
import regalowl.hyperconomy.command.Hcbank;
import regalowl.hyperconomy.command.Hcdata;
import regalowl.hyperconomy.command.Hcdelete;
import regalowl.hyperconomy.command.Hceconomy;
import regalowl.hyperconomy.command.Hcpay;
import regalowl.hyperconomy.command.Hcset;
import regalowl.hyperconomy.command.Hctest;
import regalowl.hyperconomy.command.Hctop;
import regalowl.hyperconomy.command.Hs;
import regalowl.hyperconomy.command.Hv;
import regalowl.hyperconomy.command.Hyperlog;
import regalowl.hyperconomy.command.Importbalance;
import regalowl.hyperconomy.command.Intervals;
import regalowl.hyperconomy.command.Iteminfo;
import regalowl.hyperconomy.command.Listcategories;
import regalowl.hyperconomy.command.Lockshop;
import regalowl.hyperconomy.command.Makeaccount;
import regalowl.hyperconomy.command.Makedisplay;
import regalowl.hyperconomy.command.Manageshop;
import regalowl.hyperconomy.command.Notify;
import regalowl.hyperconomy.command.Objectsettings;
import regalowl.hyperconomy.command.Removedisplay;
import regalowl.hyperconomy.command.Repairsigns;
import regalowl.hyperconomy.command.Scalebypercent;
import regalowl.hyperconomy.command.Sell;
import regalowl.hyperconomy.command.Sellall;
import regalowl.hyperconomy.command.Servershopcommand;
import regalowl.hyperconomy.command.Setchestowner;
import regalowl.hyperconomy.command.Seteconomy;
import regalowl.hyperconomy.command.Setlanguage;
import regalowl.hyperconomy.command.Setpassword;
import regalowl.hyperconomy.command.Settax;
import regalowl.hyperconomy.command.Taxsettings;
import regalowl.hyperconomy.command.Toggleeconomy;
import regalowl.hyperconomy.command.Topenchants;
import regalowl.hyperconomy.command.Topitems;
import regalowl.hyperconomy.command.Value;
import regalowl.hyperconomy.command.Xpinfo;
import regalowl.hyperconomy.display.InfoSignHandler;
import regalowl.hyperconomy.display.ItemDisplayFactory;
import regalowl.hyperconomy.display.TransactionSign;
import regalowl.hyperconomy.event.DataLoadEvent;
import regalowl.hyperconomy.event.DisableEvent;
import regalowl.hyperconomy.event.HyperEventHandler;
import regalowl.hyperconomy.server.HyperModificationServer;
import regalowl.hyperconomy.shop.ChestShopHandler;
import regalowl.hyperconomy.util.ConsoleSettings;
import regalowl.hyperconomy.util.DebugMode;
import regalowl.hyperconomy.util.DisabledProtection;
import regalowl.hyperconomy.util.History;
import regalowl.hyperconomy.util.HyperLock;
import regalowl.hyperconomy.util.LanguageFile;
import regalowl.hyperconomy.util.Log;
import regalowl.hyperconomy.util.UpdateYML;

public class HyperConomy {
	public static HyperConomy hc;
	public static API api;
	public static EconomyAPI economyApi;
	public static MineCraftConnector mc;
	private DataManager dm;
	private DataBukkit db;
	private YamlHandler yh;
	private Log l;
	private InfoSignHandler isign;
	private History hist;
	private ItemDisplayFactory itdi;
	private SQLWrite sw;
	private SQLRead sr;
	private ChestShopHandler cs;
	private FrameShopHandler fsh;
	private HyperLock hl;
	private LanguageFile L;
	private HyperEventHandler heh;
	private boolean enabled;
	private CommonFunctions cf;
	private FileTools ft;
	private ConsoleSettings cos;
	private FileConfiguration hConfig;
	private DebugMode dMode;
	private final int saveInterval = 1200000;

	public HyperConomy(MineCraftConnector mc) {
		HyperConomy.mc = mc;
	}
	
	@EventHandler
	public void onLogMessage(LogEvent event) {
		if (event.getException() != null) event.getException().printStackTrace();
		if (event.getLevel() == LogLevel.SEVERE) mc.logSevere(event.getMessage());
		if (event.getLevel() == LogLevel.INFO) mc.logInfo(event.getMessage());
	}
	
	@EventHandler
	public void onDataBukkitShutdownRequest(ShutdownEvent event) {
		disable(false);
	}
	
	@EventHandler
	public void onDataLoad(DataLoadEvent event) {
		hist = new History();
		itdi = new ItemDisplayFactory();
		isign = new InfoSignHandler();
		fsh = new FrameShopHandler();
		registerCommands();
		enabled = true;
		hl.setLoadLock(false);
		mc.registerListeners();
		dMode.syncDebugConsoleMessage("Data loading completed.");
	}


	public void load() {
		enabled = false;
		hc = this;
		api = new HyperAPI();
		economyApi = new HyperEconAPI();
		db = new DataBukkit("HyperConomy");
		db.initialize();
		db.registerListener(this);
		cf = db.getCommonFunctions();
		ft = db.getFileTools();
		yh = db.getYamlHandler();
		yh.copyFromJar("categories");
		yh.copyFromJar("config");
		yh.registerFileConfiguration("categories");
		yh.registerFileConfiguration("config");
		new UpdateYML();
		hConfig = yh.gFC("config");
		dMode = new DebugMode();
		dMode.syncDebugConsoleMessage("HyperConomy loaded with Debug Mode enabled.  Configuration files created and loaded.");
		L = new LanguageFile();
		hl = new HyperLock(true, false, false);
		heh = new HyperEventHandler();
		heh.registerListener(this);
		mc.hookExternalEconomy();
		
	}
	
	public void enable() {
		mc.unregisterAllListeners();
		dm = new DataManager();
		if (hConfig.getBoolean("sql.use-mysql")) {
			String username = hConfig.getString("sql.mysql-connection.username");
			String password = hConfig.getString("sql.mysql-connection.password");
			int port = hConfig.getInt("sql.mysql-connection.port");
			String host = hConfig.getString("sql.mysql-connection.host");
			String database = hConfig.getString("sql.mysql-connection.database");
			db.getSQLManager().enableMySQL(host, database, username, password, port);
		}
		dMode.syncDebugConsoleMessage("Expected plugin folder path: [" + db.getStoragePath() + "]");
		db.getSQLManager().createDatabase();
		dMode.syncDebugConsoleMessage("Database created.");
		sw = db.getSQLManager().getSQLWrite();
		sr = db.getSQLManager().getSQLRead();
		sw.setLogSQL(hConfig.getBoolean("sql.log-sql-statements"));
		mc.setupExternalEconomy();
		if (mc.useExternalEconomy()) {
			mc.logInfo("[HyperConomy]Using external economy plugin ("+mc.getEconomyName()+") via Vault.");
		} else {
			mc.logInfo("[HyperConomy]Using internal economy plugin.");
		}
		dMode.syncDebugConsoleMessage("Data loading started.");
		dm.load();
		l = new Log();
		new TransactionSign();
		yh.startSaveTask(saveInterval);
		cs = new ChestShopHandler();
		cos = new ConsoleSettings("default");
		new HyperModificationServer();
	}

	public void disable(boolean protect) {
		heh.fireEvent(new DisableEvent());
		mc.unhookExternalEconomy();
		enabled = false;
		mc.unregisterAllListeners();
		if (itdi != null) {
			itdi.unloadDisplays();
		}
		if (hist != null) {
			hist.stopHistoryLog();
		}
		if (dm != null) {
			dm.shutDown();
		}
		if (db != null) {
			db.shutDown();
			db = null;
		}
		mc.cancelAllTasks();
		if (protect) {
			new DisabledProtection();
		}
		if (heh != null) {
			heh.clearListeners();
		}
	}
	
	public void restart() {
		disable(true);
		load();
		enable();
	}

	private void registerCommands() {
		mc.registerCommand("additem", new Additem());
		mc.registerCommand("audit", new Audit());
		mc.registerCommand("browseshop", new Browseshop());
		mc.registerCommand("buy", new Buy());
		mc.registerCommand("economyinfo", new Economyinfo());
		mc.registerCommand("frameshop", new Frameshopcommand());
		mc.registerCommand("heldbuy", new Hb());
		mc.registerCommand("hc", new Hc());
		mc.registerCommand("hcbalance", new Hcbalance());
		mc.registerCommand("hcbank", new Hcbank());
		mc.registerCommand("hcdata", new Hcdata());
		mc.registerCommand("hcdelete", new Hcdelete());
		mc.registerCommand("hceconomy", new Hceconomy());
		mc.registerCommand("hcpay", new Hcpay());
		mc.registerCommand("hcset", new Hcset());
		mc.registerCommand("hctest", new Hctest());
		mc.registerCommand("hctop", new Hctop());
		mc.registerCommand("heldsell", new Hs());
		mc.registerCommand("heldvalue", new Hv());
		mc.registerCommand("hyperlog", new Hyperlog());
		mc.registerCommand("importbalance", new Importbalance());
		mc.registerCommand("intervals", new Intervals());
		mc.registerCommand("iteminfo", new Iteminfo());
		mc.registerCommand("listcategories", new Listcategories());
		mc.registerCommand("lockshop", new Lockshop());
		mc.registerCommand("makeaccount", new Makeaccount());
		mc.registerCommand("makedisplay", new Makedisplay());
		mc.registerCommand("manageshop", new Manageshop());
		mc.registerCommand("notify", new Notify());
		mc.registerCommand("objectsettings", new Objectsettings());
		mc.registerCommand("removedisplay", new Removedisplay());
		mc.registerCommand("repairsigns", new Repairsigns());
		mc.registerCommand("scalebypercent", new Scalebypercent());
		mc.registerCommand("sell", new Sell());
		mc.registerCommand("sellall", new Sellall());
		mc.registerCommand("servershop", new Servershopcommand());
		mc.registerCommand("setchestowner", new Setchestowner());
		mc.registerCommand("seteconomy", new Seteconomy());
		mc.registerCommand("setlanguage", new Setlanguage());
		mc.registerCommand("setpassword", new Setpassword());
		mc.registerCommand("settax", new Settax());
		mc.registerCommand("taxsettings", new Taxsettings());
		mc.registerCommand("toggleeconomy", new Toggleeconomy());
		mc.registerCommand("topenchants", new Topenchants());
		mc.registerCommand("topitems", new Topitems());
		mc.registerCommand("value", new Value());
		mc.registerCommand("xpinfo", new Xpinfo());
	}

	
	
	public HyperLock getHyperLock() {
		return hl;
	}

	
	public YamlHandler getYamlHandler() {
		return yh;
	}
	public YamlHandler gYH() {
		return yh;
	}
	
	public FileConfiguration getConf() {
		return hConfig;
	}
	
	public DataManager getDataManager() {
		return dm;
	}
	
	public HyperPlayerManager getHyperPlayerManager() {
		return dm.getHyperPlayerManager();
	}
	
	public HyperBankManager getHyperBankManager() {
		return dm.getHyperBankManager();
	}
	
	public HyperShopManager getHyperShopManager() {
		return dm.getHyperShopManager();
	}



	public Log getLog() {
		return l;
	}

	public InfoSignHandler getInfoSignHandler() {
		return isign;
	}

	public SQLWrite getSQLWrite() {
		return sw;
	}

	public SQLRead getSQLRead() {
		return sr;
	}

	public ItemDisplayFactory getItemDisplay() {
		return itdi;
	}

	public History getHistory() {
		return hist;
	}

	public LanguageFile getLanguageFile() {
		return L;
	}
	
	public boolean enabled() {
		return enabled;
	}
	
	public ChestShopHandler getChestShop() {
		return cs;
	}
	
	public FrameShopHandler getFrameShopHandler() {
		return fsh;
	}
	public DataBukkit getDataBukkit() {
		return db;
	}
	public DataBukkit gDB() {
		return db;
	}
	public SQLManager getSQLManager() {
		return db.getSQLManager();
	}
	public CommonFunctions getCommonFunctions() {
		return cf;
	}
	public CommonFunctions gCF() {
		return cf;
	}
	public FileTools getFileTools() {
		return ft;
	}
	public ConsoleSettings getConsoleSettings() {
		return cos;
	}
	public HyperEventHandler getHyperEventHandler() {
		return heh;
	}
	
	public String getFolderPath() {
		return db.getStoragePath();
	}
	
	public DebugMode getDebugMode() {
		return dMode;
	}





}
