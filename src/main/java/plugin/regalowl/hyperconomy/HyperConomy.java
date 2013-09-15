package regalowl.hyperconomy;

import java.util.logging.Logger;
import net.milkbowl.vault.Vault;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public class HyperConomy extends JavaPlugin {
	public static HyperConomy hc;
	public static HyperAPI hyperAPI;
	public static HyperEconAPI hyperEconAPI;
	public static HyperObjectAPI hyperObjectAPI;
	
	private HyperSettings hs;
	private Calculation calc;
	private Log l;
	private ShopFactory s;
	private Account acc;
	private InfoSignHandler isign;
	private _Command commandhandler;
	private History hist;
	private InventoryManipulation im;
	private Notification not;
	private ItemDisplayFactory itdi;
	private DataHandler df;
	private SQLWrite sw;
	private SQLRead sr;
	private SQLEconomy sqe;
	private WebHandler wh;
	private ChestShop cs;
	private YamlFile yaml;
	private boolean playerLock;
	private boolean fullLock;
	private boolean loadLock;
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
		acc.checkshopAccount();
		hist = new History();
		itdi = new ItemDisplayFactory();
		if (wh == null) {
			wh = new WebHandler();
			wh.startServer();
		} else {
			wh.endServer();
			wh.startServer();
		}
		isign.updateSigns();
		
		enabled = true;
		loadLock = false;
	}


	public void initialize() {
		loadLock = true;
		enabled = false;
		HandlerList.unregisterAll(this);
		hc = this;
		playerLock = false;
		fullLock = false;
		boolean migrate = false;
		yaml = new YamlFile();
		yaml.YamlEnable();
		L = new LanguageFile();
		hs = new HyperSettings();
		sqe = new SQLEconomy();
		boolean databaseOk = false;
		if (hc.s().gB("sql-connection.use-mysql")) {
			databaseOk = sqe.checkMySQL();
			if (!databaseOk) {
				hc.s().sB("sql-connection.use-mysql", false);
				databaseOk = sqe.checkSQLLite();
				log.severe(L.get("SWITCH_TO_SQLITE"));
			}
		} else {
			databaseOk = sqe.checkSQLLite();
		}
		if (databaseOk) {
			sw = new SQLWrite();
			sr = new SQLRead();
			df = new DataHandler();
			migrate = sqe.checkData();
		} else {
			log.severe(L.get("LOG_BREAK"));
			log.severe(L.get("LOG_BREAK"));
			log.severe(L.get("DATABASE_CONNECTION_ERROR"));
			log.severe(L.get("LOG_BREAK"));
			log.severe(L.get("LOG_BREAK"));
			getServer().getScheduler().cancelTasks(this);
			getPluginLoader().disablePlugin(this);
			return;
		}
		s = new ShopFactory();
		l = new Log(this);
		im = new InventoryManipulation();
		calc = new Calculation();
		sal = new SerializeArrayList();
		acc = new Account();
		commandhandler = new _Command();
		not = new Notification();
		isign = new InfoSignHandler();
		new TransactionSign();
		if (!migrate) {
			df.load();
		}
		s.startshopCheck();
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
		if (s != null) {
			s.stopshopCheck();
			hs.stopSave();
		}
		if (hist != null) {
			hist.stopHistoryLog();
		}
		if (wh != null) {
			wh.endServer();
		}
		if (sw != null) {
			sw.shutDown();
		}
		if (sr != null) {
			sr.shutDown();
		}
		if (yaml != null) {
			yaml.saveYamls();
		}
		getServer().getScheduler().cancelTasks(this);
		if (df != null) {
			df.clearData();
		}
		if (protect) {
			new DisabledProtection();
		}
	}


	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (loadLock) {
			sender.sendMessage(L.get("HYPERCONOMY_LOADING"));
			return true;
		}
		try {
			if (cmd.getName().equalsIgnoreCase("lockshop") && !fullLock) {
				try {
					if (args.length == 0) {
						if (playerLock) {
							playerLock = false;
							sender.sendMessage(L.get("SHOP_UNLOCKED"));
							return true;
						} else if (!playerLock) {
							playerLock = true;
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
				if ((args.length == 0 && !fullLock && !playerLock) || (args.length >= 1 && !args[0].equalsIgnoreCase("enable") && !args[0].equalsIgnoreCase("disable") && !playerLock && !fullLock)) {
					new Hc(sender, args);
					return true;
				} else {
					if (sender.hasPermission("hyperconomy.admin")) {
						if (args.length == 1 && args[0].equalsIgnoreCase("enable") && fullLock) {
							initialize();
							sender.sendMessage(L.get("HC_HYPERCONOMY_ENABLED"));
							sender.sendMessage(L.get("FILES_RELOADED"));
							sender.sendMessage(L.get("SHOP_UNLOCKED"));
							return true;
						} else if (args.length == 1 && args[0].equalsIgnoreCase("disable") && !fullLock) {
							sender.sendMessage(L.get("HC_HYPERCONOMY_DISABLED"));
							sender.sendMessage(L.get("SHOP_LOCKED"));
							playerLock = true;
							fullLock = true;
							shutDown(true);
							return true;
						}
					}
				}
			}
			if ((!playerLock || sender.hasPermission("hyperconomy.admin")) && !fullLock) {
				boolean result = commandhandler.handleCommand(sender, cmd, label, args);
				return result;
			} else {
				sender.sendMessage(L.get("GLOBAL_SHOP_LOCKED"));
				return true;
			}
		} catch (Exception e) {
			new HyperError(e, "Unhandled command exception.");
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

	public boolean isLocked() {
		return playerLock;
	}

	public void loadLock(boolean lockState) {
		loadLock = lockState;
	}

	public boolean fullLock() {
		return fullLock;
	}
	
	public boolean loadLock() {
		return loadLock;
	}

	public YamlFile getYaml() {
		return yaml;
	}

	public DataHandler getDataFunctions() {
		return df;
	}

	public Calculation getCalculation() {
		return calc;
	}

	public ShopFactory getShopFactory() {
		return s;
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

	public Account getAccount() {
		return acc;
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

	public SQLEconomy getSQLEconomy() {
		return sqe;
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
	
	public SerializeArrayList getSerializeArrayList() {
		return sal;
	}
}
