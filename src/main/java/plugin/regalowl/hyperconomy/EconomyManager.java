package regalowl.hyperconomy;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitTask;

import regalowl.databukkit.QueryResult;
import regalowl.databukkit.SQLRead;
import regalowl.databukkit.SQLWrite;
import regalowl.databukkit.YamlHandler;

public class EconomyManager implements Listener {

	private HyperConomy hc;
	private SQLRead sr;
	private boolean dataLoaded;
	private BukkitTask economyWait;
	private BukkitTask dataWait;
	private boolean loadActive;
	private boolean economiesLoaded;
	
	private ConcurrentHashMap<String, HyperEconomy> economies = new ConcurrentHashMap<String, HyperEconomy>();
	private ConcurrentHashMap<String, HyperPlayer> hyperPlayers = new ConcurrentHashMap<String, HyperPlayer>();
	private ConcurrentHashMap<String, Shop> shops = new ConcurrentHashMap<String, Shop>();
	
	
	
	private long shopinterval;
	private BukkitTask shopCheckTask;
	private boolean useShops;
	private ArrayList<Double> updateAfterLoad = new ArrayList<Double>();
	public final double version = 1.25;
	
	
	
	
	public EconomyManager() {
		hc = HyperConomy.hc;
		economiesLoaded = false;
		loadActive = false;
		useShops = hc.gYH().gFC("config").getBoolean("config.use-shops");
		shopinterval = hc.gYH().gFC("config").getLong("config.shopcheckinterval");
		hc.getServer().getPluginManager().registerEvents(this, hc);
	}
	
	public double getVersion() {
		return version;
	}
	
	public void addUpdateAfterLoad(double version) {
		updateAfterLoad.add(version);
	}
	
	public void load() {
		if (loadActive) {return;}
		loadActive = true;
		try {
			hc = HyperConomy.hc;
			sr = hc.getSQLRead();
			String query = "SELECT VALUE FROM hyperconomy_settings WHERE SETTING = 'version'";
			hc.getSQLRead().setErrorLogging(false);
			hc.getSQLRead().syncRead(this, "load1", query, null);
		} catch (Exception e) {
			hc.gDB().writeError(e);
		}
	}

	public void load1(QueryResult qr) {
		hc = HyperConomy.hc;
		hc.getSQLRead().setErrorLogging(true);
		if (qr.next()) {
			double version = Double.parseDouble(qr.getString("VALUE"));
			if (version < 1.2) {
				//update adds hyperconomy_shop_objects table
				hc.getLogger().info("[HyperConomy]Updating HyperConomy database to version 1.2.");
				hc.getSQLWrite().convertExecuteSynchronously("CREATE TABLE IF NOT EXISTS hyperconomy_shop_objects (ID INTEGER NOT NULL PRIMARY KEY AUTO_INCREMENT, SHOP VARCHAR(255) NOT NULL, HYPEROBJECT VARCHAR(255) NOT NULL, QUANTITY DOUBLE NOT NULL, PRICE DOUBLE NOT NULL, STATUS VARCHAR(255) NOT NULL)");
				hc.getSQLWrite().executeSynchronously("UPDATE hyperconomy_settings SET VALUE = '1.2' WHERE SETTING = 'version'");
			}
			if (version < 1.21) {
				//update removes unnecessary fields from hyperconomy_objects (id, category)
				hc.getLogger().info("[HyperConomy]Updating HyperConomy database to version 1.21.");
				hc.getSQLWrite().convertExecuteSynchronously("CREATE TABLE IF NOT EXISTS hyperconomy_objects_temp (NAME VARCHAR(255) NOT NULL, ECONOMY VARCHAR(255) NOT NULL, TYPE TINYTEXT, MATERIAL TINYTEXT, DATA INTEGER, DURABILITY INTEGER, VALUE DOUBLE, STATIC TINYTEXT, STATICPRICE DOUBLE, STOCK DOUBLE, MEDIAN DOUBLE, INITIATION TINYTEXT, STARTPRICE DOUBLE, CEILING DOUBLE, FLOOR DOUBLE, MAXSTOCK DOUBLE NOT NULL DEFAULT '1000000', PRIMARY KEY (NAME, ECONOMY))");
				hc.getSQLWrite().executeSynchronously("INSERT INTO hyperconomy_objects_temp (NAME, ECONOMY, TYPE, MATERIAL, DATA, DURABILITY, VALUE, STATIC, STATICPRICE, STOCK, MEDIAN, INITIATION, STARTPRICE, CEILING, FLOOR, MAXSTOCK) SELECT NAME, ECONOMY, TYPE, MATERIAL, DATA, DURABILITY, VALUE, STATIC, STATICPRICE, STOCK, MEDIAN, INITIATION, STARTPRICE, CEILING, FLOOR, MAXSTOCK FROM hyperconomy_objects");
				hc.getSQLWrite().executeSynchronously("DROP TABLE hyperconomy_objects");
				hc.getSQLWrite().convertExecuteSynchronously("CREATE TABLE IF NOT EXISTS hyperconomy_objects (NAME VARCHAR(255) NOT NULL, ECONOMY VARCHAR(255) NOT NULL, TYPE TINYTEXT, MATERIAL TINYTEXT, DATA INTEGER, DURABILITY INTEGER, VALUE DOUBLE, STATIC TINYTEXT, STATICPRICE DOUBLE, STOCK DOUBLE, MEDIAN DOUBLE, INITIATION TINYTEXT, STARTPRICE DOUBLE, CEILING DOUBLE, FLOOR DOUBLE, MAXSTOCK DOUBLE NOT NULL DEFAULT '1000000', PRIMARY KEY (NAME, ECONOMY))");
				hc.getSQLWrite().executeSynchronously("INSERT INTO hyperconomy_objects (NAME, ECONOMY, TYPE, MATERIAL, DATA, DURABILITY, VALUE, STATIC, STATICPRICE, STOCK, MEDIAN, INITIATION, STARTPRICE, CEILING, FLOOR, MAXSTOCK) SELECT NAME, ECONOMY, TYPE, MATERIAL, DATA, DURABILITY, VALUE, STATIC, STATICPRICE, STOCK, MEDIAN, INITIATION, STARTPRICE, CEILING, FLOOR, MAXSTOCK FROM hyperconomy_objects_temp");
				hc.getSQLWrite().executeSynchronously("DROP TABLE hyperconomy_objects_temp");
				hc.getSQLWrite().executeSynchronously("UPDATE hyperconomy_settings SET VALUE = '1.21' WHERE SETTING = 'version'");
			}
			if (version < 1.22) {
				//update adds frame shop table
				hc.getLogger().info("[HyperConomy]Updating HyperConomy database to version 1.22.");
				hc.getSQLWrite().convertExecuteSynchronously("CREATE TABLE IF NOT EXISTS hyperconomy_frame_shops (ID INTEGER NOT NULL PRIMARY KEY, HYPEROBJECT VARCHAR(255) NOT NULL, ECONOMY TINYTEXT, SHOP VARCHAR(255), X DOUBLE NOT NULL DEFAULT '0', Y DOUBLE NOT NULL DEFAULT '0', Z DOUBLE NOT NULL DEFAULT '0', WORLD TINYTEXT NOT NULL)");
				hc.getSQLWrite().executeSynchronously("UPDATE hyperconomy_settings SET VALUE = '1.22' WHERE SETTING = 'version'");
			}
			if (version < 1.23) {
				//update adds new fields ALIASES and DISPLAY_NAME to hyperconomy_objects, backs up composites.yml and objects.yml and replaces them with the new ones
				//and then calls an after load update to update the names, aliases, and display names in the database
				hc.getLogger().info("[HyperConomy]Updating HyperConomy database to version 1.23.");
				hc.getSQLWrite().convertExecuteSynchronously("CREATE TABLE IF NOT EXISTS hyperconomy_objects_temp (NAME VARCHAR(255) NOT NULL, ECONOMY VARCHAR(255) NOT NULL, DISPLAY_NAME VARCHAR(255), ALIASES VARCHAR(1000), TYPE TINYTEXT, MATERIAL TINYTEXT, DATA INTEGER, DURABILITY INTEGER, VALUE DOUBLE, STATIC TINYTEXT, STATICPRICE DOUBLE, STOCK DOUBLE, MEDIAN DOUBLE, INITIATION TINYTEXT, STARTPRICE DOUBLE, CEILING DOUBLE, FLOOR DOUBLE, MAXSTOCK DOUBLE NOT NULL DEFAULT '1000000', PRIMARY KEY (NAME, ECONOMY))");
				hc.getSQLWrite().executeSynchronously("INSERT INTO hyperconomy_objects_temp (NAME, ECONOMY, TYPE, MATERIAL, DATA, DURABILITY, VALUE, STATIC, STATICPRICE, STOCK, MEDIAN, INITIATION, STARTPRICE, CEILING, FLOOR, MAXSTOCK) SELECT NAME, ECONOMY, TYPE, MATERIAL, DATA, DURABILITY, VALUE, STATIC, STATICPRICE, STOCK, MEDIAN, INITIATION, STARTPRICE, CEILING, FLOOR, MAXSTOCK FROM hyperconomy_objects");
				hc.getSQLWrite().executeSynchronously("DROP TABLE hyperconomy_objects");
				hc.getSQLWrite().convertExecuteSynchronously("CREATE TABLE IF NOT EXISTS hyperconomy_objects (NAME VARCHAR(255) NOT NULL, ECONOMY VARCHAR(255) NOT NULL, DISPLAY_NAME VARCHAR(255), ALIASES VARCHAR(1000), TYPE TINYTEXT, MATERIAL TINYTEXT, DATA INTEGER, DURABILITY INTEGER, VALUE DOUBLE, STATIC TINYTEXT, STATICPRICE DOUBLE, STOCK DOUBLE, MEDIAN DOUBLE, INITIATION TINYTEXT, STARTPRICE DOUBLE, CEILING DOUBLE, FLOOR DOUBLE, MAXSTOCK DOUBLE NOT NULL DEFAULT '1000000', PRIMARY KEY (NAME, ECONOMY))");
				hc.getSQLWrite().executeSynchronously("INSERT INTO hyperconomy_objects (NAME, ECONOMY, DISPLAY_NAME, ALIASES, TYPE, MATERIAL, DATA, DURABILITY, VALUE, STATIC, STATICPRICE, STOCK, MEDIAN, INITIATION, STARTPRICE, CEILING, FLOOR, MAXSTOCK) SELECT NAME, ECONOMY, DISPLAY_NAME, ALIASES, TYPE, MATERIAL, DATA, DURABILITY, VALUE, STATIC, STATICPRICE, STOCK, MEDIAN, INITIATION, STARTPRICE, CEILING, FLOOR, MAXSTOCK FROM hyperconomy_objects_temp");
				hc.getSQLWrite().executeSynchronously("DROP TABLE hyperconomy_objects_temp");
				new Backup();
				YamlHandler yh = hc.getYamlHandler();
				yh.unRegisterFileConfiguration("composites");
				yh.unRegisterFileConfiguration("objects");
				yh.deleteConfigFile("composites");
				yh.deleteConfigFile("objects");
				yh.copyFromJar("composites");
				yh.copyFromJar("objects");
				yh.registerFileConfiguration("composites");
				yh.registerFileConfiguration("objects");
				hc.getEconomyManager().addUpdateAfterLoad(1.23);
				hc.getSQLWrite().executeSynchronously("UPDATE hyperconomy_settings SET VALUE = '1.23' WHERE SETTING = 'version'");
			}
			if (version < 1.24) {
				//update fixes frameshop table
				hc.getLogger().info("[HyperConomy]Updating HyperConomy database to version 1.24.");
				hc.getSQLWrite().executeSynchronously("DROP TABLE hyperconomy_frame_shops");
				hc.getSQLWrite().convertExecuteSynchronously("CREATE TABLE IF NOT EXISTS hyperconomy_frame_shops (ID INTEGER NOT NULL PRIMARY KEY, HYPEROBJECT VARCHAR(255) NOT NULL, ECONOMY TINYTEXT, SHOP VARCHAR(255), TRADE_AMOUNT INTEGER NOT NULL, X DOUBLE NOT NULL DEFAULT '0', Y DOUBLE NOT NULL DEFAULT '0', Z DOUBLE NOT NULL DEFAULT '0', WORLD TINYTEXT NOT NULL)");
				hc.getSQLWrite().executeSynchronously("UPDATE hyperconomy_settings SET VALUE = '1.24' WHERE SETTING = 'version'");
			}
			if (version < 1.25) {
				//update adds buy/sell prices to player shops and a max stock setting
				hc.getLogger().info("[HyperConomy]Updating HyperConomy database to version 1.25.");
				hc.getSQLWrite().convertExecuteSynchronously("CREATE TABLE IF NOT EXISTS hyperconomy_shop_objects_temp (ID INTEGER NOT NULL PRIMARY KEY AUTO_INCREMENT, SHOP VARCHAR(255) NOT NULL, HYPEROBJECT VARCHAR(255) NOT NULL, QUANTITY DOUBLE NOT NULL, SELL_PRICE DOUBLE NOT NULL, BUY_PRICE DOUBLE NOT NULL, MAX_STOCK INTEGER NOT NULL DEFAULT '1000000', STATUS VARCHAR(255) NOT NULL)");
				hc.getSQLWrite().convertExecuteSynchronously("INSERT INTO hyperconomy_shop_objects_temp (SHOP,HYPEROBJECT,QUANTITY,SELL_PRICE,BUY_PRICE,STATUS) SELECT SHOP,HYPEROBJECT,QUANTITY,PRICE,PRICE,STATUS FROM hyperconomy_shop_objects");
				hc.getSQLWrite().executeSynchronously("DROP TABLE hyperconomy_shop_objects");
				hc.getSQLWrite().convertExecuteSynchronously("CREATE TABLE IF NOT EXISTS hyperconomy_shop_objects (ID INTEGER NOT NULL PRIMARY KEY AUTO_INCREMENT, SHOP VARCHAR(255) NOT NULL, HYPEROBJECT VARCHAR(255) NOT NULL, QUANTITY DOUBLE NOT NULL, SELL_PRICE DOUBLE NOT NULL, BUY_PRICE DOUBLE NOT NULL, MAX_STOCK INTEGER NOT NULL DEFAULT '1000000', STATUS VARCHAR(255) NOT NULL)");
				hc.getSQLWrite().convertExecuteSynchronously("INSERT INTO hyperconomy_shop_objects (SHOP,HYPEROBJECT,QUANTITY,SELL_PRICE,BUY_PRICE,MAX_STOCK,STATUS) SELECT SHOP,HYPEROBJECT,QUANTITY,SELL_PRICE,BUY_PRICE,MAX_STOCK,STATUS FROM hyperconomy_shop_objects_temp");
				hc.getSQLWrite().executeSynchronously("DROP TABLE hyperconomy_shop_objects_temp");
				hc.getSQLWrite().executeSynchronously("UPDATE hyperconomy_settings SET VALUE = '1.25' WHERE SETTING = 'version'");
			}
			if (version < 1.26) {
				
			}
		} else {
			createTables(hc.getSQLWrite(), false);
		}
		String query = "SELECT NAME FROM hyperconomy_objects WHERE ECONOMY='default'";
		hc.getSQLRead().syncRead(this, "load3", query, null);
	}
	public void createTables(SQLWrite sw, boolean copydatabase) {
		if (copydatabase) {
			sw.convertExecuteSynchronously("DROP TABLE IF EXISTS hyperconomy_settings");
			sw.convertExecuteSynchronously("DROP TABLE IF EXISTS hyperconomy_objects");
			sw.convertExecuteSynchronously("DROP TABLE IF EXISTS hyperconomy_players");
			sw.convertExecuteSynchronously("DROP TABLE IF EXISTS hyperconomy_log");
			sw.convertExecuteSynchronously("DROP TABLE IF EXISTS hyperconomy_history");
			sw.convertExecuteSynchronously("DROP TABLE IF EXISTS hyperconomy_audit_log");
			sw.convertExecuteSynchronously("DROP TABLE IF EXISTS hyperconomy_shop_objects");
			sw.convertExecuteSynchronously("DROP TABLE IF EXISTS hyperconomy_frame_shops");		
			
		}
		sw.convertExecuteSynchronously("CREATE TABLE IF NOT EXISTS hyperconomy_settings (SETTING VARCHAR(255) NOT NULL, VALUE TEXT, TIME DATETIME NOT NULL, PRIMARY KEY (SETTING))");
		sw.convertExecuteSynchronously("CREATE TABLE IF NOT EXISTS hyperconomy_objects (NAME VARCHAR(255) NOT NULL, ECONOMY VARCHAR(255) NOT NULL, DISPLAY_NAME VARCHAR(255), ALIASES VARCHAR(1000), TYPE TINYTEXT, MATERIAL TINYTEXT, DATA INTEGER, DURABILITY INTEGER, VALUE DOUBLE, STATIC TINYTEXT, STATICPRICE DOUBLE, STOCK DOUBLE, MEDIAN DOUBLE, INITIATION TINYTEXT, STARTPRICE DOUBLE, CEILING DOUBLE, FLOOR DOUBLE, MAXSTOCK DOUBLE NOT NULL DEFAULT '1000000', PRIMARY KEY (NAME, ECONOMY))");
		sw.convertExecuteSynchronously("CREATE TABLE IF NOT EXISTS hyperconomy_players (PLAYER VARCHAR(255) NOT NULL PRIMARY KEY, ECONOMY TINYTEXT, BALANCE DOUBLE NOT NULL DEFAULT '0', X DOUBLE NOT NULL DEFAULT '0', Y DOUBLE NOT NULL DEFAULT '0', Z DOUBLE NOT NULL DEFAULT '0', WORLD TINYTEXT NOT NULL, HASH VARCHAR(255) NOT NULL DEFAULT '', SALT VARCHAR(255) NOT NULL DEFAULT '')");
		sw.convertExecuteSynchronously("CREATE TABLE IF NOT EXISTS hyperconomy_log (ID INTEGER NOT NULL PRIMARY KEY AUTO_INCREMENT, TIME DATETIME, CUSTOMER TINYTEXT, ACTION TINYTEXT, OBJECT TINYTEXT, AMOUNT DOUBLE, MONEY DOUBLE, TAX DOUBLE, STORE TINYTEXT, TYPE TINYTEXT)");
		sw.convertExecuteSynchronously("CREATE TABLE IF NOT EXISTS hyperconomy_history (ID INTEGER NOT NULL PRIMARY KEY AUTO_INCREMENT, OBJECT TINYTEXT, ECONOMY TINYTEXT, TIME DATETIME, PRICE DOUBLE)");
		sw.convertExecuteSynchronously("CREATE TABLE IF NOT EXISTS hyperconomy_audit_log (ID INTEGER NOT NULL PRIMARY KEY AUTO_INCREMENT, TIME DATETIME NOT NULL, ACCOUNT TINYTEXT NOT NULL, ACTION TINYTEXT NOT NULL, AMOUNT DOUBLE NOT NULL, ECONOMY TINYTEXT NOT NULL)");
		sw.convertExecuteSynchronously("CREATE TABLE IF NOT EXISTS hyperconomy_shop_objects (ID INTEGER NOT NULL PRIMARY KEY AUTO_INCREMENT, SHOP VARCHAR(255) NOT NULL, HYPEROBJECT VARCHAR(255) NOT NULL, QUANTITY DOUBLE NOT NULL, SELL_PRICE DOUBLE NOT NULL, BUY_PRICE DOUBLE NOT NULL, MAX_STOCK INTEGER NOT NULL DEFAULT '1000000', STATUS VARCHAR(255) NOT NULL)");
		sw.convertExecuteSynchronously("CREATE TABLE IF NOT EXISTS hyperconomy_frame_shops (ID INTEGER NOT NULL PRIMARY KEY, HYPEROBJECT VARCHAR(255) NOT NULL, ECONOMY TINYTEXT, SHOP VARCHAR(255), TRADE_AMOUNT INTEGER NOT NULL, X DOUBLE NOT NULL DEFAULT '0', Y DOUBLE NOT NULL DEFAULT '0', Z DOUBLE NOT NULL DEFAULT '0', WORLD TINYTEXT NOT NULL)");
		if (!copydatabase) {
			sw.convertExecuteSynchronously("DELETE FROM hyperconomy_settings");
			sw.convertExecuteSynchronously("INSERT INTO hyperconomy_settings (SETTING, VALUE, TIME) VALUES ('version', '"+hc.getEconomyManager().getVersion()+"', NOW() )");
		}
	}
	
	public void load3(QueryResult qr) {
		if (!qr.next()) {
			HyperConomy.hc.getEconomyManager().createEconomyFromYml("default", false);
		}
		hc.getSQLWrite().afterWrite(this, "load4");
	}
	public void load4() {
		hc.getEconomyManager().load5();
	}
	public void load5() {
		hc.getServer().getScheduler().runTaskAsynchronously(hc, new Runnable() {
			public void run() {
				economies.clear();
				ArrayList<String> econs = sr.getStringList("SELECT DISTINCT ECONOMY FROM hyperconomy_objects");
				for (String e : econs) {
					economies.put(e, new HyperEconomy(e));
				}
				waitForEconomyLoad();
			}
		});
	}
	private void waitForEconomyLoad() {
		economyWait = hc.getServer().getScheduler().runTaskTimer(hc, new Runnable() {
			public void run() {
				if (economiesLoaded) {return;}
				boolean loaded = true;
				for (HyperEconomy he : getEconomies()) {
					if (!he.dataLoaded()) {
						loaded = false;
					}
				}
				if (loaded) {
					economiesLoaded = true;
					hc.getHyperEventHandler().fireEconomyLoadEvent();
					loadRemainingData();
					economyWait.cancel();
				}
			}
		}, 1L, 1L);
	}
	
	
	private void loadRemainingData() {
		hc.getServer().getScheduler().runTaskAsynchronously(hc, new Runnable() {
			public void run() {
				hyperPlayers.clear();
				QueryResult result = sr.aSyncSelect("SELECT * FROM hyperconomy_players");
				while (result.next()) {
					HyperPlayer hplayer = new HyperPlayer(result.getString("PLAYER"), result.getString("ECONOMY"), result.getDouble("BALANCE"), result.getDouble("X"), result.getDouble("Y"), result.getDouble("Z"), result.getString("WORLD"), result.getString("HASH"), result.getString("SALT"));
					hyperPlayers.put(hplayer.getName(), hplayer);
				}
				result.close();
				hc.getServer().getScheduler().runTask(hc, new Runnable() {
					public void run() {
						addOnlinePlayers();
						createGlobalShopAccount();
						loadShops();
						waitForDataLoad();
					}
				});
			}
		});
	}
	
	private void waitForDataLoad() {
		dataWait = hc.getServer().getScheduler().runTaskTimer(hc, new Runnable() {
			public void run() {
				if (dataLoaded) {return;}
				boolean loaded = true;
				for (Shop s : getShops()) {
					if (!s.isLoaded()) {
						loaded = false;
					}
				}
				if (loaded) {
					dataLoaded = true;
					updateAfterLoad();
					hc.getHyperEventHandler().fireDataLoadEvent();
					loadActive = false;
					hc.getHyperLock().setLoadLock(false);
					dataWait.cancel();
				}
			}
		}, 1L, 1L);
	}
	

	private void updateAfterLoad() {
		boolean restart = false;
		for (Double d:updateAfterLoad) {
			if (d.doubleValue() == 1.23) {
				hc.getLogger().info("[HyperConomy]Updating object names for version 1.23.");
				for (HyperEconomy he : getEconomies()) {
					he.updateNamesFromYml();
				}
				restart = true;
			} else if (d.doubleValue() == 1.24) {
				hc.getLogger().info("[HyperConomy]Updating for version 1.24.");
			}
		}
		if (restart) {hc.restart();}
	}
	
	public boolean dataLoaded() {
		return dataLoaded;
	}
	public HyperEconomy getEconomy(String name) {
		for (Map.Entry<String,HyperEconomy> entry : economies.entrySet()) {
			HyperEconomy he = entry.getValue();
			if (he.getEconomy().equalsIgnoreCase(name)) {
				return he;
			}
		}
		return null;
	}
	public HyperEconomy getDefaultEconomy() {
		return getEconomy("default");
	}
	
	public void addEconomy(String name) {
		if (!economies.containsKey(name)) {
			economies.put(name, new HyperEconomy(name));
		}
	}
	
	
	public boolean economyExists(String economy) {
		for (Map.Entry<String,HyperEconomy> entry : economies.entrySet()) {
			HyperEconomy he = entry.getValue();
			if (he.getEconomy().equalsIgnoreCase(economy)) {
				return true;
			}
		}
		return false;
	}
	
	public ArrayList<HyperEconomy> getEconomies() {
		ArrayList<HyperEconomy> econs = new ArrayList<HyperEconomy>();
		for (Map.Entry<String,HyperEconomy> entry : economies.entrySet()) {
			econs.add(entry.getValue());
		}
		return econs;
	}
	
	public void clearData() {
		for (HyperEconomy he: economies.values()) {
			he.clearData();
		}
		economies.clear();
	}


	public ArrayList<String> getEconomyList() {
		ArrayList<String> econs = new ArrayList<String>();
		for (Map.Entry<String,HyperEconomy> entry : economies.entrySet()) {
			HyperEconomy he = entry.getValue();
			econs.add(he.getEconomy());
		}
		return econs;
	}


	
	public ArrayList<HyperObject> getHyperObjects() {
		ArrayList<HyperObject> hyperObjects = new ArrayList<HyperObject>();
		for (Map.Entry<String,HyperEconomy> entry : economies.entrySet()) {
			HyperEconomy he = entry.getValue();
			for (HyperObject ho:he.getHyperObjects()) {
				hyperObjects.add(ho);
			}
		}
		return hyperObjects;
	}



	

	
	public void createNewEconomy(String economy) {
		HyperEconomy defaultEconomy = getEconomy("default");
		SQLWrite sw = hc.getSQLWrite();
		for (HyperObject ho:defaultEconomy.getHyperObjects()) {
			HashMap<String,String> values = new HashMap<String,String>();
			values.put("NAME", ho.getName());
			values.put("DISPLAY_NAME", ho.getDisplayName());
			values.put("ALIASES", ho.getAliasesString());
			values.put("ECONOMY", economy);
			values.put("TYPE", ho.getType().toString());
			values.put("VALUE", ho.getValue()+"");
			values.put("STATIC", ho.getIsstatic());
			values.put("STATICPRICE", ho.getStaticprice()+"");
			values.put("STOCK", 0+"");
			values.put("MEDIAN", ho.getMedian()+"");
			values.put("INITIATION", ho.getInitiation());
			values.put("STARTPRICE", ho.getStartprice()+"");
			values.put("CEILING", ho.getCeiling()+"");
			values.put("FLOOR", ho.getFloor()+"");
			values.put("MAXSTOCK", ho.getMaxstock()+"");
			if (ho instanceof HyperItem) {
				HyperItem hi = (HyperItem)ho;
				values.put("MATERIAL", hi.getMaterial());
				values.put("DATA", hi.getData()+"");
				values.put("DURABILITY", hi.getDurability()+"");
			} else if (ho instanceof HyperEnchant) {
				HyperEnchant he = (HyperEnchant)ho;
				values.put("MATERIAL", he.getEnchantmentName());
				values.put("DATA", "-1");
				values.put("DURABILITY", "-1");
			} else {
				values.put("MATERIAL", "none");
				values.put("DATA", "-1");
				values.put("DURABILITY", "-1");
			}
			sw.performInsert("hyperconomy_objects", values);
		}
		hc.restart();
	}
	
	public void deleteEconomy(String economy) {
		hc.getSQLWrite().addToQueue("DELETE FROM hyperconomy_objects WHERE ECONOMY='" + economy + "'");
		hc.restart();
	}

	
	
	public void createEconomyFromYml(String econ, boolean restart) {
		if (hc.gYH().gFC("config").getBoolean("config.run-automatic-backups")) {
			new Backup();
		}
		FileConfiguration objects = hc.gYH().gFC("objects");
		SQLWrite sw = hc.getSQLWrite();
		Iterator<String> it = objects.getKeys(false).iterator();
		while (it.hasNext()) {
			String itemname = it.next().toString();
			String category = objects.getString(itemname + ".information.category");
			if (category == null) {
				category = "unknown";
			}
			
			HashMap<String,String> values = new HashMap<String,String>();
			values.put("NAME", itemname);
			values.put("ECONOMY", econ);
			values.put("DISPLAY_NAME", objects.getString(itemname + ".name.display"));
			values.put("ALIASES", objects.getString(itemname + ".name.aliases"));
			values.put("TYPE", objects.getString(itemname + ".information.type"));
			values.put("VALUE", objects.getDouble(itemname + ".value")+"");
			values.put("STATIC", objects.getString(itemname + ".price.static"));
			values.put("STATICPRICE", objects.getDouble(itemname + ".price.staticprice")+"");
			values.put("STOCK", objects.getDouble(itemname + ".stock.stock")+"");
			values.put("MEDIAN", objects.getDouble(itemname + ".stock.median")+"");
			values.put("INITIATION", objects.getString(itemname + ".initiation.initiation"));
			values.put("STARTPRICE", objects.getDouble(itemname + ".initiation.startprice")+"");
			values.put("CEILING", objects.getDouble(itemname + ".price.ceiling")+"");
			values.put("FLOOR", objects.getDouble(itemname + ".price.floor")+"");
			values.put("MAXSTOCK", objects.getDouble(itemname + ".stock.maxstock")+"");
			if (objects.getString(itemname + ".information.type").equalsIgnoreCase("item")) {
				values.put("MATERIAL", objects.getString(itemname + ".information.material"));
				values.put("DATA", objects.getInt(itemname + ".information.data")+"");
				values.put("DURABILITY", objects.getInt(itemname + ".information.data")+"");
			} else if (objects.getString(itemname + ".information.type").equalsIgnoreCase("enchantment")) {
				values.put("MATERIAL", objects.getString(itemname + ".information.material"));
				values.put("DATA", "-1");
				values.put("DURABILITY", "-1");
			} else if (objects.getString(itemname + ".information.type").equalsIgnoreCase("experience")) {
				values.put("MATERIAL", "none");
				values.put("DATA", "-1");
				values.put("DURABILITY", "-1");
			}
			sw.performInsert("hyperconomy_objects", values);
		}
		if (restart) {
			hc.restart();
		}
	}
	
	

	
	

	
	
	

	
	
	
	
	
	
	
	
	
	
	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerJoin(PlayerJoinEvent event) {
		try {
			if (!dataLoaded()) {return;}
			String name = event.getPlayer().getName();
			if (name.equalsIgnoreCase(hc.gYH().gFC("config").getString("config.global-shop-account"))) {
				if (hc.gYH().gFC("config").getBoolean("config.block-player-with-same-name-as-global-shop-account")) {
					event.getPlayer().kickPlayer(hc.getLanguageFile().get("CANT_USE_ACCOUNT"));
				}
			}
			if (!hasAccount(name)) {
				addPlayer(name);
			}
		} catch (Exception e) {
			hc.gDB().writeError(e);
		}
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerQuit(PlayerQuitEvent event) {
		try {
			if (!dataLoaded()) {return;}
			Location l = event.getPlayer().getLocation();
			String name = event.getPlayer().getName();
			if (!hasAccount(name)) {
				addPlayer(name);
			}
			if (hyperPlayers.containsKey(name)) {
				HyperPlayer hp = hyperPlayers.get(name);
				if (hp == null) {return;}
				hp.setX(l.getX());
				hp.setY(l.getY());
				hp.setZ(l.getZ());
				hp.setWorld(l.getWorld().getName());
			}
		} catch (Exception e) {
			hc.gDB().writeError(e);
		}
	}
	
	private void addOnlinePlayers() {
		for (Player p : Bukkit.getOnlinePlayers()) {
			if (p.getName().equalsIgnoreCase(hc.gYH().gFC("config").getString("config.global-shop-account"))) {
				if (hc.gYH().gFC("config").getBoolean("config.block-player-with-same-name-as-global-shop-account")) {
					p.kickPlayer(hc.getLanguageFile().get("CANT_USE_ACCOUNT"));
				}
			}
			if (!hasAccount(p.getName())) {
				addPlayer(p.getName());
			}
		}
	}
	
	
	public HyperPlayer getHyperPlayer(String player) {
		player = fixpN(player);
		if (hyperPlayers.containsKey(player) && hyperPlayers.get(player) != null) {
			return hyperPlayers.get(player);
		} else {
			if (hyperPlayers.get(player) == null) {
				hyperPlayers.remove(player);
			}
			return addPlayer(player);
		}
	}
	public HyperPlayer getHyperPlayer(Player player) {
		String name = player.getName();
		if (hyperPlayers.containsKey(name) && hyperPlayers.get(name) != null) {
			return hyperPlayers.get(name);
		} else {
			if (hyperPlayers.get(name) == null) {
				hyperPlayers.remove(name);
			}
			return addPlayer(name);
		}
	}
	
	public void addHyperPlayer(HyperPlayer hp) {
		if (!hyperPlayers.contains(hp)) {
			hyperPlayers.put(hp.getName(), hp);
		}
	}
	public void removeHyperPlayer(HyperPlayer hp) {
		if (hyperPlayers.contains(hp)) {
			hyperPlayers.remove(hp.getName());
		}
	}
	
	
	public ArrayList<HyperPlayer> getHyperPlayers() {
		ArrayList<HyperPlayer> hps = new ArrayList<HyperPlayer>();
		for (HyperPlayer hp:hyperPlayers.values()) {
			hps.add(hp);
		}
		return hps;
	}
	
	

	public HyperPlayer addPlayer(String player) {
		player = fixpN(player);
		if (!hyperPlayers.containsKey(player)) {
			HyperPlayer newHp = new HyperPlayer(player);
			hyperPlayers.put(player, newHp);
			return newHp;
		} else {
			HyperPlayer hp = hyperPlayers.get(player);
			if (hp != null) {
				return hp;
			} else {
				hyperPlayers.remove(player);
				HyperPlayer newHp = new HyperPlayer(player);
				hyperPlayers.put(player, newHp);
				return newHp;
			}
		}
	}



	public boolean hasAccount(String name) {
		if (hc.useExternalEconomy()) {
			if (hc.getEconomy().hasAccount(fixpN(name))) {
				if (!hyperPlayers.containsKey(fixpN(name))) {
					addPlayer(name);
				}
				return true;
			} else {
				return false;
			}
		} else {
			return hyperPlayers.containsKey(fixpN(name));
		}
	}
	


	public boolean createPlayerAccount(String player) {
		player = fixpN(player);
		if (!hasAccount(player)) {
			addPlayer(player);
			return true;
		} else {
			return false;
		}
	}

	
	public ArrayList<String> getEconPlayers() {
		ArrayList<String> econplayers = new ArrayList<String>();
		for (String player:hyperPlayers.keySet()) {
			econplayers.add(player);
		}
		return econplayers;
	}

	
	public String fixpN(String player) {
		for (String name:hyperPlayers.keySet()) {
			if (name.equalsIgnoreCase(player)) {
				return name;
			}
		}
		return player;
	}
	public void createGlobalShopAccount(){		
		HyperConomy hc = HyperConomy.hc;
		String globalAccount = hc.gYH().gFC("config").getString("config.global-shop-account");
		if (!hasAccount(globalAccount)) {
			HyperPlayer ga = getHyperPlayer(globalAccount);
			Double initialBalance = hc.gYH().gFC("config").getDouble("config.initialshopbalance");
			ga.setBalance(initialBalance);
			String economyName = "HyperConomy";
			if (hc.useExternalEconomy()) {
				economyName = hc.getEconomy().getName();
			}
			hc.getLog().writeAuditLog(ga.getName(), "setbalance", initialBalance, economyName);
		}
	}
	
	public HyperPlayer getGlobalShopAccount() {
		return getHyperPlayer(hc.gYH().gFC("config").getString("config.global-shop-account"));
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	private void loadShops() {
		stopShopCheck();
		shops.clear();
		FileConfiguration sh = hc.gYH().gFC("shops");
		if (!useShops) {
			Shop shop = new ServerShop("GlobalShop", getGlobalShopAccount().getEconomy(), getGlobalShopAccount());
			shop.setGlobal();
			shops.put("GlobalShop", shop);
			return;
		}
		Iterator<String> it = sh.getKeys(false).iterator();
		while (it.hasNext()) {   			
			Object element = it.next();
			String name = element.toString(); 
			if (name.equalsIgnoreCase("GlobalShop")) {continue;}
			String owner = sh.getString(name + ".owner");
			String economy = sh.getString(name + ".economy");
			if (owner == null) {
				owner = getGlobalShopAccount().getName();
			}
			if (owner.equalsIgnoreCase(getGlobalShopAccount().getName())) {
				Shop shop = new ServerShop(name, economy, getHyperPlayer(owner));
				shop.setPoint1(sh.getString(name + ".world"), sh.getInt(name + ".p1.x"), sh.getInt(name + ".p1.y"), sh.getInt(name + ".p1.z"));
				shop.setPoint2(sh.getString(name + ".world"), sh.getInt(name + ".p2.x"), sh.getInt(name + ".p2.y"), sh.getInt(name + ".p2.z"));
				shop.setMessage1(sh.getString(name + ".shopmessage1"));
				shop.setMessage2(sh.getString(name + ".shopmessage2"));
				shops.put(name, shop);
			} else {
				if (hc.gYH().gFC("config").getBoolean("config.use-player-shops")) {
					Shop shop = new PlayerShop(name, economy, getHyperPlayer(owner));
					shop.setPoint1(sh.getString(name + ".world"), sh.getInt(name + ".p1.x"), sh.getInt(name + ".p1.y"), sh.getInt(name + ".p1.z"));
					shop.setPoint2(sh.getString(name + ".world"), sh.getInt(name + ".p2.x"), sh.getInt(name + ".p2.y"), sh.getInt(name + ".p2.z"));
					shop.setMessage1(sh.getString(name + ".shopmessage1"));
					shop.setMessage2(sh.getString(name + ".shopmessage2"));
					shops.put(name, shop);
				}
			}

		}
		startShopCheck();
	}
	
	Shop getShop(Player player) {
		if (player == null) {
			return null;
		}
		for (Shop shop : shops.values()) {
			if (shop.inShop(player)) {
				return shop;
			}
		}
		return null;
	}
	public Shop getShop(String shop) {
		shop = fixShopName(shop);
		if (shops.containsKey(shop)) {
			return shops.get(shop);
		} else {
			return null;
		}
	}
	public boolean inAnyShop(Player player) {
		for (Shop shop : shops.values()) {
			if (shop.inShop(player)) {
				return true;
			}
		}
		return false;
	}
	public boolean shopExists(String name) {
		return shops.containsKey(fixShopName(name));
	}
	
	public void addShop(Shop shop) {
		shops.put(shop.getName(), shop);
		hc.getHyperEventHandler().fireShopCreationEvent(shop);
	}
	
	public void removeShop(String name) {
		if (shopExists(name)) {
			shops.remove(fixShopName(name));
		}
	}
	
	
	public void renameShop(String name, String newName) {
		Shop shop = shops.get(name);
		shop.setName(newName);
		shops.put(newName, shop);
		shops.remove(name);
	}
    public void startShopCheck() {
		shopCheckTask = hc.getServer().getScheduler().runTaskTimer(hc, new Runnable() {
		    public void run() {
				for (Shop shop:shops.values()) {
					shop.updatePlayerStatus();
				}
		    }
		}, shopinterval, shopinterval);
    }
    public void stopShopCheck() {
    	if (shopCheckTask != null) {
    		shopCheckTask.cancel();
    	}
    }
    public long getShopCheckInterval() {
    	return shopinterval;
    }
    public void setShopCheckInterval(long interval) {
    	shopinterval = interval;
    }
	public String fixShopName(String nam) {
		for (String shop:shops.keySet()) {
			if (shop.equalsIgnoreCase(nam)) {
				return shop;
			}
		}
		return nam;
	}
	public ArrayList<Shop> getShops() {
		ArrayList<Shop> shopList = new ArrayList<Shop>();
		for (Shop shop:shops.values()) {
			shopList.add(shop);
		}
		return shopList;
	}
	public ArrayList<Shop> getShops(HyperPlayer hp) {
		ArrayList<Shop> shopList = new ArrayList<Shop>();
		for (Shop shop:shops.values()) {
			if (shop.getOwner().equals(hp)) {
				shopList.add(shop);
			}
		}
		return shopList;
	}
	public ArrayList<String> listShops() {
		ArrayList<String> names = new ArrayList<String>();
		for (Shop shop : shops.values()) {
			names.add(shop.getName());
		}
		return names;
	}

	
	
}
