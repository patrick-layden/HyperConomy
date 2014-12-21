package regalowl.hyperconomy;


import regalowl.simpledatalib.SimpleDataLib;
import regalowl.simpledatalib.event.EventHandler;
import regalowl.simpledatalib.events.LogEvent;
import regalowl.simpledatalib.events.LogLevel;
import regalowl.simpledatalib.events.ShutdownEvent;
import regalowl.simpledatalib.file.FileConfiguration;
import regalowl.simpledatalib.file.FileTools;
import regalowl.simpledatalib.file.YamlHandler;
import regalowl.simpledatalib.sql.SQLManager;
import regalowl.simpledatalib.sql.SQLRead;
import regalowl.simpledatalib.sql.SQLWrite;
import regalowl.hyperconomy.account.HyperBankManager;
import regalowl.hyperconomy.account.HyperPlayerManager;
import regalowl.hyperconomy.api.API;
import regalowl.hyperconomy.api.HEconomyProvider;
import regalowl.hyperconomy.api.MineCraftConnector;
import regalowl.hyperconomy.command.Additem;
import regalowl.hyperconomy.command.Audit;
import regalowl.hyperconomy.command.Browseshop;
import regalowl.hyperconomy.command.Buy;
import regalowl.hyperconomy.command.Economyinfo;
import regalowl.hyperconomy.command.Frameshopcommand;
import regalowl.hyperconomy.command.Hb;
import regalowl.hyperconomy.command.HcCommand;
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
import regalowl.hyperconomy.command.Lowstock;
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
import regalowl.hyperconomy.display.FrameShopHandler;
import regalowl.hyperconomy.display.InfoSignHandler;
import regalowl.hyperconomy.display.ItemDisplayHandler;
import regalowl.hyperconomy.display.TransactionSignHandler;
import regalowl.hyperconomy.event.DataLoadEvent;
import regalowl.hyperconomy.event.DisableEvent;
import regalowl.hyperconomy.event.HyperEventHandler;
import regalowl.hyperconomy.event.DataLoadEvent.DataLoadType;
import regalowl.hyperconomy.multiserver.HyperModificationServer;
import regalowl.hyperconomy.shop.ChestShopHandler;
import regalowl.hyperconomy.shop.HyperShopManager;
import regalowl.hyperconomy.util.DebugMode;
import regalowl.hyperconomy.util.DisabledProtection;
import regalowl.hyperconomy.util.History;
import regalowl.hyperconomy.util.HyperLock;
import regalowl.hyperconomy.util.LanguageFile;
import regalowl.hyperconomy.util.Log;
import regalowl.hyperconomy.util.UpdateYML;

public class HyperConomy {

	private transient MineCraftConnector mc;
	private transient API api;
	private transient DataManager dm;
	private transient SimpleDataLib sdl;
	private transient Log l;
	private transient InfoSignHandler isign;
	private transient History hist;
	private transient ItemDisplayHandler itdi;
	private transient ChestShopHandler cs;
	private transient FrameShopHandler fsh;
	private transient HyperLock hl;
	private transient LanguageFile L;
	private transient HyperEventHandler heh;
	private transient FileTools ft;
	private transient FileConfiguration hConfig;
	private transient DebugMode dMode;
	private final int saveInterval = 1200000;
	private boolean enabled;
	private String consoleEconomy;

	public HyperConomy(MineCraftConnector mc) {
		this.mc = mc;
		this.consoleEconomy = "default";
	}
	
	@EventHandler
	public void onLogMessage(LogEvent event) {
		if (event.getException() != null) event.getException().printStackTrace();
		if (event.getLevel() == LogLevel.SEVERE || event.getLevel() == LogLevel.ERROR) mc.logSevere(event.getMessage());
		if (event.getLevel() == LogLevel.INFO) mc.logInfo(event.getMessage());
	}
	
	@EventHandler
	public void onSimpleDataLibShutdownRequest(ShutdownEvent event) {
		disable(false);
	}
	
	@EventHandler
	public void onDataLoad(DataLoadEvent event) {
		if (!(event.loadType == DataLoadType.COMPLETE)) return;
		hist = new History(this);
		itdi = new ItemDisplayHandler(this);
		isign = new InfoSignHandler(this);
		fsh = mc.getFrameShopHandler();
		registerCommands();
		enabled = true;
		hl.setLoadLock(false);
		mc.registerListeners();
		dMode.syncDebugConsoleMessage("Data loading completed.");
	}


	public void load() {
		enabled = false;
		if (sdl != null) sdl.shutDown();
		sdl = new SimpleDataLib("HyperConomy");
		sdl.initialize();
		sdl.registerListener(this);
		ft = sdl.getFileTools();
		YamlHandler yh = sdl.getYamlHandler();
		yh.copyFromJar("config");
		yh.registerFileConfiguration("config");
		new UpdateYML(this);
		hConfig = yh.gFC("config");
		dMode = new DebugMode(this);
		dMode.syncDebugConsoleMessage("HyperConomy loaded with Debug Mode enabled.  Configuration files created and loaded.");
		L = new LanguageFile(this);
		hl = new HyperLock(this, true, false, false);
		if (heh != null) {
			heh.clearListeners();
		}
		heh = new HyperEventHandler(this);
		heh.registerListener(this);
		mc.checkExternalEconomyRegistration();
	}
	
	public void enable() {
		mc.unregisterAllListeners();
		if (hConfig.getBoolean("sql.use-mysql")) {
			String username = hConfig.getString("sql.mysql-connection.username");
			String password = hConfig.getString("sql.mysql-connection.password");
			int port = hConfig.getInt("sql.mysql-connection.port");
			String host = hConfig.getString("sql.mysql-connection.host");
			String database = hConfig.getString("sql.mysql-connection.database");
			sdl.getSQLManager().enableMySQL(host, database, username, password, port);
		}
		dMode.syncDebugConsoleMessage("Expected plugin folder path: [" + sdl.getStoragePath() + "]");
		sdl.getSQLManager().createDatabase();
		dMode.syncDebugConsoleMessage("Database created.");
		sdl.getSQLManager().getSQLWrite().setLogSQL(hConfig.getBoolean("sql.log-sql-statements"));
		mc.setupHEconomyProvider();
		l = new Log(this);
		dm = new DataManager(this);
		new TransactionSignHandler(this);
		sdl.getYamlHandler().startSaveTask(saveInterval);
		cs = new ChestShopHandler(this);
		new HyperModificationServer(this);
		api = new HyperAPI(this);
		dMode.syncDebugConsoleMessage("Data loading started.");
		heh.fireEvent(new DataLoadEvent(DataLoadType.START));
	}

	public void disable(boolean protect) {
		heh.fireEvent(new DisableEvent());
		mc.unRegisterAsExternalEconomy();
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
		mc.cancelAllTasks();
		if (heh != null && !protect) {
			heh.clearListeners();
		}
		if (sdl != null) sdl.shutDown();
		if (protect) {
			new DisabledProtection(this);
		}
	}
	
	public void restart() {
		disable(true);
		load();
		enable();
	}

	private void registerCommands() {
		mc.registerCommand("additem", new Additem(this));
		mc.registerCommand("audit", new Audit(this));
		mc.registerCommand("browseshop", new Browseshop(this));
		mc.registerCommand("buy", new Buy(this));
		mc.registerCommand("economyinfo", new Economyinfo(this));
		mc.registerCommand("frameshop", new Frameshopcommand(this));
		mc.registerCommand("heldbuy", new Hb(this));
		mc.registerCommand("hc", new HcCommand(this));
		mc.registerCommand("hcbalance", new Hcbalance(this));
		mc.registerCommand("hcbank", new Hcbank(this));
		mc.registerCommand("hcdata", new Hcdata(this));
		mc.registerCommand("hcdelete", new Hcdelete(this));
		mc.registerCommand("hceconomy", new Hceconomy(this));
		mc.registerCommand("hcpay", new Hcpay(this));
		mc.registerCommand("hcset", new Hcset(this));
		mc.registerCommand("hctest", new Hctest(this));
		mc.registerCommand("hctop", new Hctop(this));
		mc.registerCommand("heldsell", new Hs(this));
		mc.registerCommand("heldvalue", new Hv(this));
		mc.registerCommand("hyperlog", new Hyperlog(this));
		mc.registerCommand("importbalance", new Importbalance(this));
		mc.registerCommand("intervals", new Intervals(this));
		mc.registerCommand("iteminfo", new Iteminfo(this));
		mc.registerCommand("listcategories", new Listcategories(this));
		mc.registerCommand("lockshop", new Lockshop(this));
		mc.registerCommand("lowstock", new Lowstock(this));
		mc.registerCommand("makeaccount", new Makeaccount(this));
		mc.registerCommand("makedisplay", new Makedisplay(this));
		mc.registerCommand("manageshop", new Manageshop(this));
		mc.registerCommand("notify", new Notify(this));
		mc.registerCommand("objectsettings", new Objectsettings(this));
		mc.registerCommand("removedisplay", new Removedisplay(this));
		mc.registerCommand("repairsigns", new Repairsigns(this));
		mc.registerCommand("scalebypercent", new Scalebypercent(this));
		mc.registerCommand("sell", new Sell(this));
		mc.registerCommand("sellall", new Sellall(this));
		mc.registerCommand("servershop", new Servershopcommand(this));
		mc.registerCommand("setchestowner", new Setchestowner(this));
		mc.registerCommand("seteconomy", new Seteconomy(this));
		mc.registerCommand("setlanguage", new Setlanguage(this));
		mc.registerCommand("setpassword", new Setpassword(this));
		mc.registerCommand("settax", new Settax(this));
		mc.registerCommand("taxsettings", new Taxsettings(this));
		mc.registerCommand("toggleeconomy", new Toggleeconomy(this));
		mc.registerCommand("topenchants", new Topenchants(this));
		mc.registerCommand("topitems", new Topitems(this));
		mc.registerCommand("value", new Value(this));
		mc.registerCommand("xpinfo", new Xpinfo(this));
	}

	
	
	public HyperLock getHyperLock() {
		return hl;
	}
	public YamlHandler getYamlHandler() {
		return sdl.getYamlHandler();
	}
	public YamlHandler gYH() {
		return sdl.getYamlHandler();
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
		return sdl.getSQLManager().getSQLWrite();
	}
	public SQLRead getSQLRead() {
		return sdl.getSQLManager().getSQLRead();
	}
	public ItemDisplayHandler getItemDisplay() {
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
	public SimpleDataLib getSimpleDataLib() {
		return sdl;
	}
	public SimpleDataLib gSDL() {
		return sdl;
	}
	public SQLManager getSQLManager() {
		return sdl.getSQLManager();
	}
	public FileTools getFileTools() {
		return ft;
	}
	public String getConsoleEconomy() {
		return consoleEconomy;
	}
	public void setConsoleEconomy(String economy) {
		this.consoleEconomy = economy;
	}
	public HyperEventHandler getHyperEventHandler() {
		return heh;
	}
	public String getFolderPath() {
		return sdl.getStoragePath();
	}
	public DebugMode getDebugMode() {
		return dMode;
	}
	public API getAPI() {
		return api;
	}
	public HEconomyProvider getEconomyAPI() {
		return mc.getEconomyProvider();
	}
	public MineCraftConnector getMC() {
		return mc;
	}


}
