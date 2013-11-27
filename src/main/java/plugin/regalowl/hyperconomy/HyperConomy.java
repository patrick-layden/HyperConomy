package regalowl.hyperconomy;

import java.io.File;
import java.util.logging.Logger;

import net.milkbowl.vault.Vault;
import net.milkbowl.vault.economy.Economy;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;

import regalowl.databukkit.CommonFunctions;
import regalowl.databukkit.DataBukkit;
import regalowl.databukkit.FileTools;
import regalowl.databukkit.SQLRead;
import regalowl.databukkit.SQLWrite;
import regalowl.databukkit.YamlHandler;

public class HyperConomy extends JavaPlugin implements DataLoadListener {
	public static HyperConomy hc;
	public static HyperAPI hyperAPI;
	public static HyperEconAPI hyperEconAPI;
	public static HyperObjectAPI hyperObjectAPI;
	
	private EconomyManager em;
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
	


	private void load() {
		enabled = false;
		hc = this;
		hyperAPI = new HyperAPI();
		hyperEconAPI = new HyperEconAPI();
		hyperObjectAPI = new HyperObjectAPI();
		db = new DataBukkit(this);
		cf = db.getCommonFunctions();
		ft = db.getFileTools();
		yh = db.getYamlHandler();
		yh.copyFromJar("categories");
		yh.copyFromJar("config");
		yh.copyFromJar("objects");
		yh.copyFromJar("composites");
		yh.registerFileConfiguration("categories");
		yh.registerFileConfiguration("composites");
		yh.registerFileConfiguration("config");
		yh.registerFileConfiguration("displays");
		yh.registerFileConfiguration("objects");
		yh.registerFileConfiguration("shops");
		yh.registerFileConfiguration("signs");
		yh.registerFileConfiguration("temp");
		L = new LanguageFile();
		hl = new HyperLock(true, false, false);
		new UpdateYML();
		heh = new HyperEventHandler();
		heh.registerDataLoadListener(this);
		hookVault();
	}
	private void enable() {
		HandlerList.unregisterAll(this);
		em = new EconomyManager();
		FileConfiguration config = yh.gFC("config");
		if (config.getBoolean("config.sql-connection.use-mysql")) {
			String username = config.getString("config.sql-connection.username");
			String password = config.getString("config.sql-connection.password");
			int port = config.getInt("config.sql-connection.port");
			String host = config.getString("config.sql-connection.host");
			String database = config.getString("config.sql-connection.database");
			db.enableMySQL(host, database, username, password, port);
		}
		db.createDatabase();
		sw = db.getSQLWrite();
		sr = db.getSQLRead();
		if (config.getBoolean("config.log-sql-statements")) {
			sw.setLogSQL(true);
		}
		setupExternalEconomy();
		logEconomyState();
		em.load();
		l = new Log(this);
		commandhandler = new _Command();
		not = new Notification();
		new TransactionSign();
		yh.startSaveTask(config.getLong("config.saveinterval"));
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
		Bukkit.getServer().getPluginCommand("addcategory").setExecutor(new Addcategory());
		Bukkit.getServer().getPluginCommand("additem").setExecutor(new Additem());
		Bukkit.getServer().getPluginCommand("manageshop").setExecutor(new Manageshop());
		Bukkit.getServer().getPluginCommand("ymladditem").setExecutor(new Ymladditem());
		Bukkit.getServer().getPluginCommand("hcset").setExecutor(new Hcset());
		Bukkit.getServer().getPluginCommand("hcdelete").setExecutor(new Hcdelete());
		Bukkit.getServer().getPluginCommand("hctest").setExecutor(new Hctest());
		Bukkit.getServer().getPluginCommand("frameshop").setExecutor(new Frameshopcommand());
	}

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (hl.loadLock()) {
			hl.sendLockMessage(sender);
			return true;
		}
		try {
			if (cmd.getName().equalsIgnoreCase("lockshop") && !hl.fullLock()) {
				try {
					if (args.length == 0) {
						if (hl.playerLock()) {
							hl.setPlayerLock(false);
							sender.sendMessage(L.get("SHOP_UNLOCKED"));
							return true;
						} else if (!hl.playerLock()) {
							hl.setPlayerLock(true);
							sender.sendMessage(L.get("SHOP_LOCKED"));
							return true;
						} else {
							sender.sendMessage(L.get("FIX_YML_FILE"));
							return true;
						}
					} else {
						sender.sendMessage(L.get("LOCKSHOP_INVALID"));
						return true;
					}
				} catch (Exception e) {
					sender.sendMessage(L.get("LOCKSHOP_INVALID"));
					return true;
				}
			} else if (cmd.getName().equalsIgnoreCase("hc")) {
				if ((args.length == 0 && !hl.isLocked(sender)) || (args.length >= 1 && !args[0].equalsIgnoreCase("enable") && !args[0].equalsIgnoreCase("disable") && !hl.isLocked(sender))) {
					new Hc(sender, args);
					return true;
				} else {
					if (sender.hasPermission("hyperconomy.admin")) {
						if (args.length == 1 && args[0].equalsIgnoreCase("enable") && hl.fullLock()) {
							load();
							enable();
							sender.sendMessage(L.get("HC_HYPERCONOMY_ENABLED"));
							sender.sendMessage(L.get("FILES_RELOADED"));
							sender.sendMessage(L.get("SHOP_UNLOCKED"));
							return true;
						} else if (args.length == 1 && args[0].equalsIgnoreCase("disable") && !hl.fullLock()) {
							sender.sendMessage(L.get("HC_HYPERCONOMY_DISABLED"));
							sender.sendMessage(L.get("SHOP_LOCKED"));
							hl.setPlayerLock(true);
							hl.setFullLock(true);
							disable(true);
							return true;
						}
					}
				}
			}
			if ((!hl.isLocked(sender))) {
				boolean result = commandhandler.handleCommand(sender, cmd, label, args);
				return result;
			} else {
				sender.sendMessage(L.get("GLOBAL_SHOP_LOCKED"));
				return true;
			}
		} catch (Exception e) {
			if (db != null) {
				db.writeError(e, "Unhandled command exception.");
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
		useExternalEconomy = yh.getFileConfiguration("config").getBoolean("config.use-external-economy-plugin");
		if (!vaultInstalled) {
			useExternalEconomy = false;
		}
		if (vaultInstalled && yh.gFC("config").getBoolean("config.hook-internal-economy-into-vault")) {
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
	
	private void logEconomyState() {
		if (useExternalEconomy) {
			log.info("[HyperConomy]Using external economy plugin via Vault.");
		} else {
			log.info("[HyperConomy]Using internal economy plugin.");
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
	
	public EconomyManager getEconomyManager() {
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
