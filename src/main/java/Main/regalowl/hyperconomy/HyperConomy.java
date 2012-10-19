package regalowl.hyperconomy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.logging.Logger;
import net.milkbowl.vault.Vault;
import net.milkbowl.vault.economy.Economy;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import static regalowl.hyperconomy.Messages.*;

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
	private boolean sqllock;
	private boolean brokenfile;
	private Logger log = Logger.getLogger("Minecraft");
	private Economy economy;
	private HashMap<String, String> namedata = new HashMap<String, String>();
	private HashMap<String, String> enchantdata = new HashMap<String, String>();
	private ArrayList<String> names = new ArrayList<String>();
	private ArrayList<String> inames = new ArrayList<String>();
	private ArrayList<String> enames = new ArrayList<String>();

	@Override
	public void onEnable() {
		hc = this;
		lock = false;
		brokenfile = false;
		boolean migrate = false;
		YamlFile yam = new YamlFile(this);
		yam.YamlEnable();
		yaml = yam;
		new Messages();
		if (!brokenfile) {
			saveinterval = yaml.getConfig().getLong("config.saveinterval");
			usesql = yaml.getConfig().getBoolean("config.sql-connection.use-sql");
			currency = hc.getYaml().getConfig().getString("config.currency-symbol");
			Update cb = new Update();
			cb.checkCompatibility(this);
			sf = new SQLFunctions();
			
			if (usesql) {
				sqe = new SQLEconomy(this);
				boolean databaseOk = sqe.checkTables();
				if (databaseOk) {
					sw = new SQLWrite(this);
					migrate = sqe.checkData();
				} else {
					log.severe(LOG_BREAK);
					log.severe(LOG_BREAK);
					log.severe(DATABASE_CONNECTION_ERROR);
					log.severe(LOG_BREAK);
					log.severe(LOG_BREAK);
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
				log.warning(VAULT_NOT_FOUND);
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
			acc.setAccount(this, null, economy);
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
			FormatString fs = new FormatString();
			log.info(fs.formatString(HYPERCONOMY_ENABLED, getDescription().getVersion()));
		}
	}
	
	
	public void onDataLoad() {
		itdi = new ItemDisplay();
	}

	@Override
	public void onDisable() {
		try {
			itdi.clearDisplays();
			s.stopshopCheck();
			stopSave();
			l.stopBuffer();
			hist.stophistoryLog();
			l.saveBuffer();
			getServer().getScheduler().cancelTasks(this);
			hws.endServer();
			if (useSQL()) {
				sw.closeConnections();
				new SQLShutdown(this, sw);
			}
		} catch (Exception e) {
		}
		yaml.saveYamls();
		log.info(HYPERCONOMY_DISABLED);
	}

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (cmd.getName().equalsIgnoreCase("lockshop")) {
			try {
				if (args.length == 0) {
					if (lock && !brokenfile) {
						lock = false;
						l.checkBuffer();
						isign.checksignUpdate();
						s.startshopCheck();
						hist.starthistoryLog();
						startSave();
						sender.sendMessage(ChatColor.GOLD + "The global shop has been unlocked!");
						return true;
					} else if (!lock) {
						lock = true;
						s.stopshopCheck();
						l.stopBuffer();
						hist.stophistoryLog();
						isign.stopsignUpdate();
						isign.resetAll();
						l.saveBuffer();
						stopSave();
						yaml.saveYamls();
						sender.sendMessage(ChatColor.GOLD + "The global shop has been locked!");
						return true;
					} else {
						sender.sendMessage(ChatColor.DARK_RED + "You must first fix your bad yml file!");
						return true;
					}
				} else {
					sender.sendMessage(ChatColor.RED + "Invalid parameters.  Use /lockshop");
					return true;
				}
			} catch (Exception e) {
				sender.sendMessage(ChatColor.RED + "Invalid Usage.  Use /lockshop");
				return true;
			}
		} else if (cmd.getName().equalsIgnoreCase("reloadfiles")) {
			try {
				if (lock) {
					YamlFile yam = new YamlFile(this);
					yam.YamlEnable();
					yaml = yam;
					usesql = yaml.getConfig().getBoolean("config.sql-connection.use-sql");
					s.setshopInterval(yaml.getConfig().getLong("config.shopcheckinterval"));
					l.setlogInterval(yaml.getConfig().getLong("config.logwriteinterval"));
					saveinterval = yaml.getConfig().getLong("config.saveinterval");
					isign.setsignupdateInterval(yaml.getConfig().getLong("config.signupdateinterval"));
					s.clearAll();
					s = new Shop(this);
					isign.setinfoSign(this, calc, ench, tran);
					namedata.clear();
					enchantdata.clear();
					names.clear();
					inames.clear();
					enames.clear();
					if (useSQL()) {
						sf.load();
					} else {
						buildData();
						sf.loadYML();
					}
					sqllock = false;
					sender.sendMessage(ChatColor.GOLD + "All files have been reloaded.");
				} else {
					sender.sendMessage(ChatColor.DARK_RED + "You must first lock the shop!");
				}
				return true;
			} catch (Exception e) {
				sender.sendMessage(ChatColor.DARK_RED + "Invalid Parameters.  Use /reloadfiles");
				return true;
			}
		}
		if (!lock && !sqllock) {
			boolean result = commandhandler.handleCommand(sender, cmd, label, args);
			l.checkBuffer();
			return result;
		} else {
			sender.sendMessage(ChatColor.RED + "The global shop is currently locked!");
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
					log.info(BAD_YMLFILE_DETECTED);
					Bukkit.getPluginManager().disablePlugin(Bukkit.getServer().getPluginManager().getPlugin("HyperConomy"));
					return;
				}
				lock = true;
				s.stopshopCheck();
				l.stopBuffer();
				hist.stophistoryLog();
				isign.stopsignUpdate();
				isign.resetAll();
				l.saveBuffer();
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
}
