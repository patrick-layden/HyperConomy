package regalowl.hyperconomy;

import java.io.File;
import java.util.ArrayList;
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
	public static String currency;
	private Transaction tran;
	private Calculation calc;
	private ETransaction ench;
	private Log l;
	private ShopFactory s;
	private Account acc;
	private InfoSignHandler isign;
	private _Command commandhandler;
	private History hist;
	private Notification not;
	private ItemDisplayFactory itdi;
	private DataHandler df;
	private SQLWrite sw;
	private SQLRead sr;
	private SQLEconomy sqe;
	private HyperWebStart hws;
	private boolean usemysql;
	private long saveinterval;
	private int savetaskid;
	private YamlFile yaml;
	private boolean playerLock;
	private boolean fullLock;
	private boolean brokenfile;
	private LanguageFile L;
	private Logger log = Logger.getLogger("Minecraft");
	private Economy economy;
	private int errorCount;
	private boolean errorResetActive;
	private boolean shuttingDown;
	private boolean useExternalEconomy;
	private boolean logerrors;
	private String serverVersion;
	private int errorcount;
	private double apiVersion;

	@Override
	public void onEnable() {
		initialize();
	}
	
	@Override
	public void onDisable() {
		shutDown(false);
	}

	public void onDataLoad() {
		getServer().getScheduler().scheduleSyncDelayedTask(this, new Runnable() {
			public void run() {
				onLateStart();
			}
		}, 60L);
	}
	
	private void onLateStart() {
		Plugin x = this.getServer().getPluginManager().getPlugin("Vault");
		if (x != null & x instanceof Vault) {
			this.setupEconomy();
		} else if (useExternalEconomy) {
			log.warning(L.get("VAULT_NOT_FOUND"));
			useExternalEconomy = false;
		}
		acc.checkshopAccount();
		hist = new History();
		itdi = new ItemDisplayFactory();
		hws = new HyperWebStart();
		isign.updateSigns();
		//log.info("HyperConomy " + getDescription().getVersion() + " has been enabled.");
	}

	public void initialize() {
		HandlerList.unregisterAll(this);
		hc = this;
		playerLock = false;
		fullLock = false;
		brokenfile = false;
		boolean migrate = false;
		YamlFile yam = new YamlFile(this);
		yam.YamlEnable();
		yaml = yam;
		loadErrorCount();
		errorResetActive = false;
		shuttingDown = true;
		L = new LanguageFile();
		if (!brokenfile) {
			new Update();
			saveinterval = yaml.getConfig().getLong("config.saveinterval");
			usemysql = yaml.getConfig().getBoolean("config.sql-connection.use-mysql");
			apiVersion = yaml.getConfig().getDouble("api-version");
			currency = L.get("CURRENCY");
			useExternalEconomy = yaml.getConfig().getBoolean("config.use-external-economy-plugin");
			logerrors = this.getYaml().getConfig().getBoolean("config.log-errors");
			serverVersion = this.getServer().getPluginManager().getPlugin("HyperConomy").getDescription().getVersion();
			new Update();
			
			sqe = new SQLEconomy();
			boolean databaseOk = false;
			if (usemysql) {
				databaseOk = sqe.checkMySQL();
				if (!databaseOk) {
					usemysql = false;
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
			tran = new Transaction();
			calc = new Calculation();
			ench = new ETransaction();
			acc = new Account();
			commandhandler = new _Command();
			not = new Notification();
			isign = new InfoSignHandler();
			new TransactionSign();
			if (!migrate) {
				df.load();
			}
			s.startshopCheck();
			startSave();
			new ChestShop();
		}
	}

	public void shutDown(boolean protect) {
		if (df != null) {
			df.shutDown();
		}
		HandlerList.unregisterAll(this);
		if (itdi != null) {
			itdi.unloadDisplays();
		}
		if (s != null) {
			s.stopshopCheck();
			stopSave();
		}
		if (hist != null) {
			hist.stopHistoryLog();
		}
		if (hws != null) {
			hws.endServer();
		}
		if (sw != null) {
			sw.shutDown();
		}
		if (yaml != null) {
			yaml.saveYamls();
		}
		getServer().getScheduler().cancelTasks(this);
		clearData();
		if (protect) {
			new DisabledProtection();
		}
	}
	
	public void clearData() {
		if (df != null) {
			df.clearData();
		}
		tran= null;
		calc= null;
		ench= null;
		l= null;
		s= null;
		acc= null;
		isign= null;
		commandhandler= null;
		hist= null;
		not= null;
		itdi= null;
		df= null;
		sw = null;
		sr = null;
		sqe= null;
		hws= null;
		yaml= null;
		economy= null;
	}
	
	public void disableWebPage() {
		hws.endServer();
		hws = null;
	}
	
	public void enableWebPage() {
		hws = null;
		hws = new HyperWebStart();	
	}

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		try {
			if (cmd.getName().equalsIgnoreCase("lockshop") && !fullLock) {
				try {
					if (args.length == 0) {
						if (playerLock && !brokenfile) {
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
				hc.setUseExternalEconomy(false);
			}
		}
		return (economy != null);
	}

	public void startSave() {
		savetaskid = this.getServer().getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
			public void run() {
				if (!brokenfile) {
					yaml.saveYamls();
				}
			}
		}, saveinterval, saveinterval);
	}

	public void stopSave() {
		this.getServer().getScheduler().cancelTask(savetaskid);
	}

	public void ymlCheck(int failcount) {
		if (failcount == 0) {
			brokenfile = false;
		} else {
			brokenfile = true;
			log.info(L.get("BAD_YMLFILE_DETECTED"));
			playerLock = true;
			fullLock = true;
			shutDown(true);
		}
	}


	public void incrementErrorCount() {
		errorCount++;
		if (errorCount > 20) {
			getServer().getScheduler().cancelTasks(this);
			if (!shuttingDown) {
				shuttingDown = true;
				log.severe("HyperConomy is experiencing a massive amount of errors...shutting down....");
				shutDown(true);
				getPluginLoader().disablePlugin(this);
			}
		}
		if (!errorResetActive) {
			errorResetActive = true;
			getServer().getScheduler().scheduleSyncDelayedTask(this, new Runnable() {
			    public void run() {
			    	errorCount = 0;
	    		    errorResetActive = false;
			    }
			}, 20L);
		}
	}



	public boolean isLocked() {
		return playerLock;
	}

	/*
	public void sqllockShop() {
		sqllock = true;
	}

	
	public void sqlunlockShop() {
		sqllock = false;
	}

	public boolean sqlLock() {
		return sqllock;
	}
	*/
	
	public void lockHyperConomy(boolean lockState) {
		fullLock = lockState;
	}
	
	public boolean fullLock() {
		return fullLock;
	}

	public boolean useMySQL() {
		return usemysql;
	}

	public long getsaveInterval() {
		return saveinterval;
	}

	public void setSaveInterval(long interval) {
		saveinterval = interval;
	}

	public YamlFile getYaml() {
		return yaml;
	}

	public DataHandler getDataFunctions() {
		return df;
	}

	public Transaction getTransaction() {
		return tran;
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

	public ETransaction getETransaction() {
		return ench;
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

	public LanguageFile getLanguageFile() {
		return L;
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
	
	public int getErrorCount() {
		return errorcount;
	}
	
	public void raiseErrorCount() {
		errorcount++;
	}
	
	public void setUseMySQL(boolean usemysql) {
		this.usemysql = usemysql;
	}
	
	public void loadErrorCount() {
		FileTools ft = new FileTools();
		String path = ft.getJarPath() + File.separator + "plugins" + File.separator + "HyperConomy" + File.separator + "errors";
		ft.makeFolder(path);
		ArrayList<String> contents = ft.getFolderContents(path);
		if (contents.size() == 0) {
			errorcount = 0;
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
			errorcount = max + 1;
		}
	}
	
	public double getApiVersion() {
		return apiVersion;
	}

}
