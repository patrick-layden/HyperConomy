package regalowl.hyperconomy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.logging.Logger;
import net.milkbowl.vault.Vault;
import net.milkbowl.vault.economy.Economy;

import org.bukkit.Bukkit;
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
	private Shop s;
	private Account acc;
	private InfoSign isign;
	private _Command commandhandler;
	private History hist;
	private Notification not;
	private TransactionSign tsign;
	private ItemDisplay itdi;
	private SQLFunctions sf;
	private SQLWrite sw;
	private SQLEconomy sqe;
	private HyperWebStart hws;
	private boolean usesql;
	private long saveinterval;
	private int savetaskid;
	private YamlFile yaml;
	private boolean lock;
	private boolean mlock;
	private boolean sqllock;
	private boolean brokenfile;
	private LanguageFile L;
	private Logger log = Logger.getLogger("Minecraft");
	private Economy economy;
	private HashMap<String, String> namedata = new HashMap<String, String>();
	private HashMap<String, String> enchantdata = new HashMap<String, String>();
	private ArrayList<String> names = new ArrayList<String>();
	private ArrayList<String> inames = new ArrayList<String>();
	private ArrayList<String> enames = new ArrayList<String>();
	private int errorCount;
	private boolean errorResetActive;
	private boolean shuttingDown;
	private boolean useExternalEconomy;

	@Override
	public void onEnable() {
		initialize();
	}

	public void onDataLoad() {
		itdi = new ItemDisplay();
	}

	@Override
	public void onDisable() {
		shutDown();
	}

	public void initialize() {
		hc = this;
		lock = false;
		mlock = false;
		brokenfile = false;
		enames.clear();
		inames.clear();
		names.clear();
		enchantdata.clear();
		namedata.clear();
		boolean migrate = false;
		YamlFile yam = new YamlFile(this);
		yam.YamlEnable();
		yaml = yam;
		errorCount = 0;
		errorResetActive = false;
		shuttingDown = true;
		L = new LanguageFile();
		if (!brokenfile) {
			new Update();
			saveinterval = yaml.getConfig().getLong("config.saveinterval");
			usesql = yaml.getConfig().getBoolean("config.sql-connection.use-sql");
			currency = yaml.getConfig().getString("config.currency-symbol");
			useExternalEconomy = yaml.getConfig().getBoolean("config.use-vault");
			sf = new SQLFunctions();
			new Update();
			if (usesql) {
				sqe = new SQLEconomy(this);
				boolean databaseOk = sqe.checkTables();
				if (databaseOk) {
					sw = new SQLWrite(this);
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
			}
			s = new Shop(this);
			l = new Log(this);
			tran = new Transaction();
			calc = new Calculation();
			ench = new ETransaction();
			acc = new Account();
			commandhandler = new _Command();
			not = new Notification();
			isign = new InfoSign();
			tsign = new TransactionSign();
			Plugin x = this.getServer().getPluginManager().getPlugin("Vault");
			if (x != null & x instanceof Vault) {
				this.setupEconomy();
			} else {
				log.warning(L.get("VAULT_NOT_FOUND"));
				getPluginLoader().disablePlugin(this);
				return;
			}
			buildData();
			if (useSQL() && !migrate) {
				sf.load();
			} else {
				sf.loadYML();
			}
			s.startshopCheck();
			startSave();
			this.getServer().getScheduler().scheduleSyncDelayedTask(this, new Runnable() {
				public void run() {
					acc.checkshopAccount();
				}
			}, 300L);
			isign.setinfoSign(this, calc, ench, tran);
			hist = new History(this, calc, ench, isign);
			hist.starthistoryLog();
			tsign.setTransactionSign(this, tran, calc, ench, l, acc, isign, not);
			new ChestShop();
			if (usesql) {
				new SQLPlayers(this);
			}
			hws = new HyperWebStart();
			log.info("HyperConomy " + getDescription().getVersion() + " has been enabled.");
		}
	}

	public void shutDown() {
		HandlerList.unregisterAll(this);
		if (itdi != null) {
			itdi.shutDown();
		}
		if (s != null) {
			s.stopshopCheck();
			stopSave();
		}
		if (hist != null) {
			hist.stophistoryLog();
		}
		getServer().getScheduler().cancelTasks(this);
		if (hws != null) {
			hws.endServer();
		}
		if (useSQL() && sw != null) {
			sw.closeConnections();
			new SQLShutdown(this, sw);
		}
		if (yaml != null) {
			yaml.saveYamls();
		}
		clearData();
	}
	
	public void clearData() {
		if (sf != null) {
			sf.clearData();
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
		tsign= null;
		itdi= null;
		sf= null;
		sw= null;
		sqe= null;
		hws= null;
		yaml= null;
		L= null;
		economy= null;
		namedata.clear();
		enchantdata.clear();
		names.clear();
		inames.clear();
		enames.clear();
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
		if (cmd.getName().equalsIgnoreCase("lockshop") && !mlock) {
			try {
				if (args.length == 0) {
					if (lock && !brokenfile) {
						lock = false;
						sender.sendMessage(L.get("SHOP_UNLOCKED"));
						return true;
					} else if (!lock) {
						lock = true;
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
			if (args.length == 0 || (!args[0].equalsIgnoreCase("enable") && !args[0].equalsIgnoreCase("disable")) && !lock && !sqllock && !mlock) {
				new Hc(sender, args);
				return true;
			} else {
				if (sender.hasPermission("hyperconomy.admin")) {
					if (args[0].equalsIgnoreCase("enable") && mlock) {
						initialize();
						sqllock = false;
						sender.sendMessage(L.get("HC_HYPERCONOMY_ENABLED"));
						sender.sendMessage(L.get("FILES_RELOADED"));
						sender.sendMessage(L.get("SHOP_UNLOCKED"));
						return true;
					} else if (args[0].equalsIgnoreCase("disable") && !mlock) {
						sender.sendMessage(L.get("HC_HYPERCONOMY_DISABLED"));
						sender.sendMessage(L.get("SHOP_LOCKED"));
						lock = true;
						mlock = true;
						shutDown();
						return true;
					}
				}
			}
		} 
		if (((!lock && !sqllock) || sender.hasPermission("hyperconomy.admin")) && !mlock) {
			boolean result = commandhandler.handleCommand(sender, cmd, label, args);
			return result;
		} else {
			sender.sendMessage(L.get("GLOBAL_SHOP_LOCKED"));
			return true;
		}
	}

	private Boolean setupEconomy() {
		RegisteredServiceProvider<Economy> economyProvider = getServer().getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);
		if (economyProvider != null) {
			economy = economyProvider.getProvider();
		}
		return (economy != null);
	}

	public boolean buildData() {
		inames.clear();
		namedata.clear();
		enames.clear();
		enchantdata.clear();
		names.clear();
		if (usesql) {
			inames = sf.getStringColumn("SELECT NAME FROM hyperobjects WHERE (TYPE='experience' OR TYPE = 'item') AND ECONOMY='default'");
			ArrayList<String> iids = sf.getStringColumn("SELECT ID FROM hyperobjects WHERE (TYPE='experience' OR TYPE = 'item') AND ECONOMY='default'");
			ArrayList<String> idatas = sf.getStringColumn("SELECT DATA FROM hyperobjects WHERE (TYPE='experience' OR TYPE = 'item') AND ECONOMY='default'");
			for (int c = 0; c < inames.size(); c++) {
				namedata.put(iids.get(c) + ":" + idatas.get(c), inames.get(c));
			}
			enames = sf.getStringColumn("SELECT NAME FROM hyperobjects WHERE TYPE='enchantment' AND ECONOMY='default'");
			ArrayList<String> eids = sf.getStringColumn("SELECT MATERIAL FROM hyperobjects WHERE TYPE='enchantment' AND ECONOMY='default'");
			for (int c = 0; c < enames.size(); c++) {
				String enchantname = enames.get(c);
				enchantdata.put(eids.get(c), enchantname.substring(0, enchantname.length() - 1));
			}
			names = sf.getStringColumn("SELECT NAME FROM hyperobjects WHERE ECONOMY='default'");
		} else {
			Iterator<String> it = yaml.getItems().getKeys(false).iterator();
			while (it.hasNext()) {
				String elst = it.next().toString();
				String ikey = yaml.getItems().getString(elst + ".information.id") + ":" + yaml.getItems().getString(elst + ".information.data");
				namedata.put(ikey, elst);
			}
			Iterator<String> it2 = yaml.getEnchants().getKeys(false).iterator();
			while (it2.hasNext()) {
				String elst2 = it2.next().toString();
				enchantdata.put(yaml.getEnchants().getString(elst2 + ".information.name"), elst2.substring(0, elst2.length() - 1));
			}
			Iterator<String> it3 = yaml.getItems().getKeys(false).iterator();
			while (it3.hasNext()) {
				String cname = it3.next().toString();
				names.add(cname);
				inames.add(cname);
			}
			Iterator<String> it4 = yaml.getEnchants().getKeys(false).iterator();
			while (it4.hasNext()) {
				String cname = it4.next().toString();
				names.add(cname);
				enames.add(cname);
			}
		}
		return true;
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
			if (!lock) {
				if (s == null) {
					log.info(L.get("BAD_YMLFILE_DETECTED"));
					Bukkit.getPluginManager().disablePlugin(Bukkit.getServer().getPluginManager().getPlugin("HyperConomy"));
					return;
				}
				lock = true;
				s.stopshopCheck();
				//l.stopBuffer();
				hist.stophistoryLog();
				isign.stopsignUpdate();
				isign.resetAll();
				//l.saveBuffer();
				stopSave();
			}
		}
	}

	public String fixName(String nam) {
		for (int i = 0; i < names.size(); i++) {
			if (names.get(i).equalsIgnoreCase(nam)) {
				return names.get(i);
			}
		}
		return nam;
	}

	public String fixsName(String nam) {
		String name = nam;
		int c = 0;
		int l = getYaml().getShops().getKeys(false).size();
		Object names[] = getYaml().getShops().getKeys(false).toArray();
		while (c < l) {
			if (names[c].toString().equalsIgnoreCase(name)) {
				name = names[c].toString();
				return name;
			}
			c++;
		}
		return name;
	}

	public boolean itemTest(String name) {
		boolean item = false;
		if (inames.contains(name)) {
			item = true;
		}
		return item;
	}

	public boolean enchantTest(String name) {
		boolean enchant = false;
		if (enames.contains(name)) {
			enchant = true;
		}
		return enchant;
	}

	public String testiString(String name) {
		String teststring = null;
		if (inames.contains(name)) {
			teststring = name;
		} else {
			teststring = null;
		}
		if (teststring == null) {
			name = fixName(name);
			if (inames.contains(name)) {
				teststring = name;
			} else {
				teststring = null;
			}
		}
		return teststring;
	}

	public String testeString(String name) {
		String teststring = null;
		if (enames.contains(name)) {
			teststring = name;
		} else {
			teststring = null;
		}
		if (teststring == null) {
			name = fixName(name);
			if (enames.contains(name)) {
				teststring = name;
			} else {
				teststring = null;
			}
		}
		return teststring;
	}
	
	public void incrementErrorCount() {
		errorCount++;
		if (errorCount > 20) {
			getServer().getScheduler().cancelTasks(this);
			if (!shuttingDown) {
				shuttingDown = true;
				log.severe("HyperConomy is experiencing a massive amount of errors...shutting down....");
				shutDown();
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

	public ArrayList<String> getNames() {
		return names;
	}

	public ArrayList<String> getInames() {
		return inames;
	}

	public ArrayList<String> getEnames() {
		return enames;
	}

	public boolean isLocked() {
		return lock;
	}

	public void sqllockShop() {
		sqllock = true;
	}

	public void sqlunlockShop() {
		sqllock = false;
	}

	public boolean sqlLock() {
		return sqllock;
	}

	public boolean useSQL() {
		return usesql;
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

	public String getnameData(String key) {
		return namedata.get(key);
	}

	public String getenchantData(String key) {
		return enchantdata.get(key);
	}

	public SQLFunctions getSQLFunctions() {
		return sf;
	}

	public Transaction getTransaction() {
		return tran;
	}

	public Calculation getCalculation() {
		return calc;
	}

	public Shop getShop() {
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

	public InfoSign getInfoSign() {
		return isign;
	}

	public SQLWrite getSQLWrite() {
		return sw;
	}

	public SQLEconomy getSQLEconomy() {
		return sqe;
	}

	public ItemDisplay getItemDisplay() {
		return itdi;
	}

	public LanguageFile getLanguageFile() {
		return L;
	}
	
	public boolean useExternalEconomy() {
		return useExternalEconomy;
	}
}
