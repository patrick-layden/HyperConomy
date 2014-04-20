package regalowl.hyperconomy;

import java.io.File;
import java.util.logging.Logger;

import net.milkbowl.vault.Vault;
import net.milkbowl.vault.economy.Economy;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;

import regalowl.databukkit.CommonFunctions;
import regalowl.databukkit.DataBukkit;
import regalowl.databukkit.file.FileTools;
import regalowl.databukkit.file.YamlHandler;
import regalowl.databukkit.sql.SQLRead;
import regalowl.databukkit.sql.SQLWrite;
import regalowl.hyperconomy.api.API;
import regalowl.hyperconomy.api.EconomyAPI;
import regalowl.hyperconomy.api.HyperAPI;
import regalowl.hyperconomy.api.HyperEconAPI;
import regalowl.hyperconomy.command.Additem;
import regalowl.hyperconomy.command.Buy;
import regalowl.hyperconomy.command.Frameshopcommand;
import regalowl.hyperconomy.command.Hc;
import regalowl.hyperconomy.command.Hcbank;
import regalowl.hyperconomy.command.Hcdata;
import regalowl.hyperconomy.command.Hcdelete;
import regalowl.hyperconomy.command.Hceconomy;
import regalowl.hyperconomy.command.Hcset;
import regalowl.hyperconomy.command.Hctest;
import regalowl.hyperconomy.command.Lockshop;
import regalowl.hyperconomy.command.Manageshop;
import regalowl.hyperconomy.command.Sell;
import regalowl.hyperconomy.command.Sellall;
import regalowl.hyperconomy.command.Servershopcommand;
import regalowl.hyperconomy.command.Value;
import regalowl.hyperconomy.command._Command;
import regalowl.hyperconomy.display.InfoSignHandler;
import regalowl.hyperconomy.display.ItemDisplayFactory;
import regalowl.hyperconomy.display.TransactionSign;
import regalowl.hyperconomy.event.DataLoadListener;
import regalowl.hyperconomy.event.HyperEventHandler;
import regalowl.hyperconomy.shop.ChestShop;
import regalowl.hyperconomy.shop.FrameShopHandler;
import regalowl.hyperconomy.util.ConsoleSettings;
import regalowl.hyperconomy.util.DisabledProtection;
import regalowl.hyperconomy.util.Economy_HyperConomy;
import regalowl.hyperconomy.util.History;
import regalowl.hyperconomy.util.HyperConfig;
import regalowl.hyperconomy.util.HyperLock;
import regalowl.hyperconomy.util.LanguageFile;
import regalowl.hyperconomy.util.Log;
import regalowl.hyperconomy.util.Notification;
import regalowl.hyperconomy.util.UpdateYML;

public class HyperConomy extends JavaPlugin implements DataLoadListener {
	public static HyperConomy hc;
	public static API api;
	public static EconomyAPI economyApi;
	
	private DataManager em;
	private DataBukkit db;
	private YamlHandler yh;
	private Log l;
	private InfoSignHandler isign;
	private _Command commandhandler;
	private History hist;
	private Notification not;
	private ItemDisplayFactory itdi;
	private SQLWrite sw;
	private SQLRead sr;
	private ChestShop cs;
	private FrameShopHandler fsh;
	private HyperLock hl;
	private LanguageFile L;
	private Logger log = Logger.getLogger("Minecraft");
	private Economy economy;
	private HyperEventHandler heh;
	private boolean enabled;
	private boolean useExternalEconomy;
	private boolean vaultInstalled;
	private CommonFunctions cf;
	private FileTools ft;
	private ConsoleSettings cos;
	private HyperConfig hConfig;

	@Override
	public void onLoad() {
		load();
	}
	@Override
	public void onEnable() {
		enable();
	}
	@Override
	public void onDisable() {
		disable(false);
	}
	


	public void load() {
		enabled = false;
		hc = this;
		api = new HyperAPI();
		economyApi = new HyperEconAPI();
		db = new DataBukkit(this);
		cf = db.getCommonFunctions();
		ft = db.getFileTools();
		yh = db.getYamlHandler();
		yh.copyFromJar("categories");
		yh.copyFromJar("config");
		yh.registerFileConfiguration("categories");
		yh.registerFileConfiguration("config");
		new UpdateYML();
		hConfig = new HyperConfig(yh.gFC("config"));
		L = new LanguageFile();
		hl = new HyperLock(true, false, false);
		heh = new HyperEventHandler();
		heh.registerDataLoadListener(this);
		hookVault();
	}
	public void enable() {
		HandlerList.unregisterAll(this);
		em = new DataManager();
		if (hConfig.getBoolean("sql.use-mysql")) {
			String username = hConfig.getString("sql.mysql-connection.username");
			String password = hConfig.getString("sql.mysql-connection.password");
			int port = hConfig.getInt("sql.mysql-connection.port");
			String host = hConfig.getString("sql.mysql-connection.host");
			String database = hConfig.getString("sql.mysql-connection.database");
			db.enableMySQL(host, database, username, password, port);
		}
		db.createDatabase();
		sw = db.getSQLWrite();
		sr = db.getSQLRead();
		sw.setLogSQL(hConfig.getBoolean("sql.log-sql-statements"));
		setupExternalEconomy();
		if (useExternalEconomy) {
			log.info("[HyperConomy]Using external economy plugin ("+economy.getName()+") via Vault.");
		} else {
			log.info("[HyperConomy]Using internal economy plugin.");
		}
		em.load();
		l = new Log(this);
		commandhandler = new _Command();
		not = new Notification();
		new TransactionSign();
		yh.startSaveTask(hConfig.getLong("intervals.save"));
		cs = new ChestShop();
		cos = new ConsoleSettings("default");
	}
	
	public void onDataLoad() {
		hist = new History();
		itdi = new ItemDisplayFactory();
		registerCommands();
		isign = new InfoSignHandler();
		isign.updateSigns();
		fsh = new FrameShopHandler();
		enabled = true;
		hl.setLoadLock(false);
	}
	

	public void disable(boolean protect) {
		unHookVault();
		enabled = false;
		HandlerList.unregisterAll(this);
		if (itdi != null) {
			itdi.unloadDisplays();
		}
		if (hist != null) {
			hist.stopHistoryLog();
		}
		if (db != null) {
			db.shutDown();
			db = null;
		}
		getServer().getScheduler().cancelTasks(this);
		if (em != null) {
			em.clearData();
		}
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
		Bukkit.getServer().getPluginCommand("manageshop").setExecutor(new Manageshop());
		Bukkit.getServer().getPluginCommand("additem").setExecutor(new Additem());
		Bukkit.getServer().getPluginCommand("hcset").setExecutor(new Hcset());
		Bukkit.getServer().getPluginCommand("hcdelete").setExecutor(new Hcdelete());
		Bukkit.getServer().getPluginCommand("hctest").setExecutor(new Hctest());
		Bukkit.getServer().getPluginCommand("frameshop").setExecutor(new Frameshopcommand());
		Bukkit.getServer().getPluginCommand("hcbank").setExecutor(new Hcbank());
		Bukkit.getServer().getPluginCommand("servershop").setExecutor(new Servershopcommand());
		Bukkit.getServer().getPluginCommand("hcdata").setExecutor(new Hcdata());
		Bukkit.getServer().getPluginCommand("sellall").setExecutor(new Sellall());
		Bukkit.getServer().getPluginCommand("sell").setExecutor(new Sell());
		Bukkit.getServer().getPluginCommand("buy").setExecutor(new Buy());
		Bukkit.getServer().getPluginCommand("value").setExecutor(new Value());
		Bukkit.getServer().getPluginCommand("lockshop").setExecutor(new Lockshop());
		Bukkit.getServer().getPluginCommand("hc").setExecutor(new Hc());
		Bukkit.getServer().getPluginCommand("hceconomy").setExecutor(new Hceconomy());
	}

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (hl.loadLock()) {
			hl.sendLockMessage(sender);
			return true;
		}
		try {
			if ((!hl.isLocked(sender))) {
				boolean result = commandhandler.handleCommand(sender, cmd, label, args);
				return result;
			} else {
				sender.sendMessage(L.get("GLOBAL_SHOP_LOCKED"));
				return true;
			}
		} catch (Exception e) {
			if (db != null) {
				db.writeError(e);
				return true;
			} else {
				e.printStackTrace();
				return true;
			}
		}
	}
	
	
	private void hookVault() {
		Plugin vault = this.getServer().getPluginManager().getPlugin("Vault");
		if (vault != null & vault instanceof Vault) {
			vaultInstalled = true;
		} else {
			vaultInstalled = false;
		}
		useExternalEconomy = yh.getFileConfiguration("config").getBoolean("economy-plugin.use-external");
		if (!vaultInstalled) {
			useExternalEconomy = false;
		}
		if (vaultInstalled && yh.gFC("config").getBoolean("economy-plugin.hook-internal-economy-into-vault")) {
			getServer().getServicesManager().register(Economy.class, new Economy_HyperConomy(), this, ServicePriority.Highest);
			log.info("[HyperConomy]Internal economy hooked into Vault.");
		}
	}

	private void unHookVault() {
		if (!vaultInstalled) {
			return;
		}
	    RegisteredServiceProvider<Economy> eco = getServer().getServicesManager().getRegistration(Economy.class);
	    if (eco != null) {
	    	Economy registeredEconomy = eco.getProvider();
	    	if (registeredEconomy != null && registeredEconomy.getName().equalsIgnoreCase("HyperConomy")) {
		        getServer().getServicesManager().unregister(eco.getProvider());
		        log.info("[HyperConomy]Internal economy unhooked from Vault.");
	    	}
	    }
	}
	

	public void setupExternalEconomy() {
		if (!useExternalEconomy || !vaultInstalled) {return;}
		RegisteredServiceProvider<Economy> economyProvider = getServer().getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);
		if (economyProvider == null) {
			useExternalEconomy = false;
			return;
		}
		economy = economyProvider.getProvider();
		if (economy == null) {
			useExternalEconomy = false;
			return;
		}
		if (economy.getName().equalsIgnoreCase("HyperConomy")) {
			useExternalEconomy = false;
			return;
		}
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
	
	public HyperConfig getConf() {
		return hConfig;
	}
	
	public DataManager getDataManager() {
		return em;
	}

	public Economy getEconomy() {
		if (economy == null) {
			setupExternalEconomy();
		}
		return economy;
	}

	public Log getLog() {
		return l;
	}

	public Notification getNotify() {
		return not;
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

	public Logger log() {
		return log;
	}
	
	public boolean enabled() {
		return enabled;
	}
	
	public ChestShop getChestShop() {
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
	public boolean useExternalEconomy() {
		return useExternalEconomy;
	}
	public String getFolderPath() {
		String folderpath = ft.getJarPath() + File.separator + "plugins" + File.separator + "HyperConomy";
		return folderpath;
	}

}
