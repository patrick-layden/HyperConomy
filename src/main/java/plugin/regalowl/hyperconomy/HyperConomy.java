package regalowl.hyperconomy;

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
import org.bukkit.plugin.java.JavaPlugin;

import regalowl.databukkit.DataBukkit;
import regalowl.databukkit.SQLRead;
import regalowl.databukkit.SQLWrite;
import regalowl.databukkit.YamlHandler;

public class HyperConomy extends JavaPlugin {
	public static HyperConomy hc;
	public static HyperAPI hyperAPI;
	public static HyperEconAPI hyperEconAPI;
	public static HyperObjectAPI hyperObjectAPI;
	
	private EconomyManager em;
	private DataBukkit db;
	private YamlHandler yh;
	private HyperSettings hs;
	private Calculation calc;
	private Log l;
	private InfoSignHandler isign;
	private _Command commandhandler;
	private History hist;
	private InventoryManipulation im;
	private Notification not;
	private ItemDisplayFactory itdi;
	private SQLWrite sw;
	private SQLRead sr;
	private WebHandler wh;
	private ChestShop cs;
	private HyperLock hl;
	private LanguageFile L;
	private Logger log = Logger.getLogger("Minecraft");
	private Economy economy;
	private SerializeArrayList sal;
	private boolean enabled;

	@Override
	public void onEnable() {
		initialize();
	}

	@Override
	public void onDisable() {
		shutDown(false);
	}
	
	public void onDataLoad() {
		Plugin x = this.getServer().getPluginManager().getPlugin("Vault");
		if (x != null & x instanceof Vault) {
			this.setupEconomy();
		} else if (s().gB("use-external-economy-plugin")) {
			log.warning(L.get("VAULT_NOT_FOUND"));
			s().sB("use-external-economy-plugin", false);
		}
		hist = new History();
		itdi = new ItemDisplayFactory();
		registerCommands();
		if (wh == null) {
			wh = new WebHandler();
			wh.startServer();
		} else {
			wh.endServer();
			wh.startServer();
		}
		isign = new InfoSignHandler();
		isign.updateSigns();
		enabled = true;
		hl.setLoadLock(false);
	}


	public void initialize() {
		enabled = false;
		db = new DataBukkit(this);
		yh = db.getYamlHandler();
		yh.copyFromJar("categories");
		yh.copyFromJar("config");
		yh.copyFromJar("enchants");
		yh.copyFromJar("items");
		yh.copyFromJar("composites");
		yh.registerFileConfiguration("categories");
		yh.registerFileConfiguration("composites");
		yh.registerFileConfiguration("config");
		yh.registerFileConfiguration("displays");
		yh.registerFileConfiguration("enchants");
		yh.registerFileConfiguration("items");
		yh.registerFileConfiguration("shops");
		yh.registerFileConfiguration("signs");
		yh.setSaveInterval(yh.gFC("config").getLong("config.saveinterval"));
		HandlerList.unregisterAll(this);
		hc = this;
		L = new LanguageFile();
		hl = new HyperLock(true, false, false);
		hs = new HyperSettings();
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
		em.load();

		l = new Log(this);
		im = new InventoryManipulation();
		calc = new Calculation();
		sal = new SerializeArrayList();
		commandhandler = new _Command();
		not = new Notification();
		new TransactionSign();
		hs.startSave();
		cs = new ChestShop();
		hyperAPI = new HyperAPI();
		hyperEconAPI = new HyperEconAPI();
		hyperObjectAPI = new HyperObjectAPI();
	}

	public void shutDown(boolean protect) {
		enabled = false;
		HandlerList.unregisterAll(this);
		if (itdi != null) {
			itdi.unloadDisplays();
		}
		if (hs != null) {
			hs.stopSave();
		}
		if (hist != null) {
			hist.stopHistoryLog();
		}
		if (wh != null) {
			wh.endServer();
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
	}
	
	public void restart() {
		shutDown(true);
		initialize();
	}

	private void registerCommands() {
		Bukkit.getServer().getPluginCommand("addcategory").setExecutor(new Addcategory());
		Bukkit.getServer().getPluginCommand("additem").setExecutor(new Additem());
		Bukkit.getServer().getPluginCommand("manageshop").setExecutor(new Manageshop());
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
							initialize();
							sender.sendMessage(L.get("HC_HYPERCONOMY_ENABLED"));
							sender.sendMessage(L.get("FILES_RELOADED"));
							sender.sendMessage(L.get("SHOP_UNLOCKED"));
							return true;
						} else if (args.length == 1 && args[0].equalsIgnoreCase("disable") && !hl.fullLock()) {
							sender.sendMessage(L.get("HC_HYPERCONOMY_DISABLED"));
							sender.sendMessage(L.get("SHOP_LOCKED"));
							hl.setPlayerLock(true);
							hl.setFullLock(true);
							shutDown(true);
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
			db.writeError(e, "Unhandled command exception.");
			return true;
		}
	}

	private Boolean setupEconomy() {
		RegisteredServiceProvider<Economy> economyProvider = getServer().getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);
		if (economyProvider != null) {
			economy = economyProvider.getProvider();
			if (economy.getName().equalsIgnoreCase("HyperConomy")) {
				s().sB("use-external-economy-plugin", false);
			}
		}
		return (economy != null);
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

	public Calculation getCalculation() {
		return calc;
	}

	public Economy getEconomy() {
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

	public InventoryManipulation getInventoryManipulation() {
		return im;
	}

	public LanguageFile getLanguageFile() {
		return L;
	}

	public Logger log() {
		return log;
	}

	public HyperSettings s() {
		return hs;
	}

	public boolean enabled() {
		return enabled;
	}
	
	public WebHandler getWebHandler() {
		return wh;
	}
	
	public ChestShop getChestShop() {
		return cs;
	}
	
	public DataBukkit getDataBukkit() {
		return db;
	}
	public DataBukkit gDB() {
		return db;
	}
	public SerializeArrayList getSerializeArrayList() {
		return sal;
	}
	
}
