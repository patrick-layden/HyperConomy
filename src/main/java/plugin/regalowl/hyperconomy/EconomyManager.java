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
	private boolean playersLoaded;
	private BukkitTask economyWait;
	private boolean loadActive;
	private boolean economiesLoaded;
	
	private ConcurrentHashMap<String, HyperEconomy> economies = new ConcurrentHashMap<String, HyperEconomy>();
	private ConcurrentHashMap<String, HyperPlayer> hyperPlayers = new ConcurrentHashMap<String, HyperPlayer>();
	private ConcurrentHashMap<String, HyperBank> hyperBanks = new ConcurrentHashMap<String, HyperBank>();
	private ConcurrentHashMap<String, Shop> shops = new ConcurrentHashMap<String, Shop>();
	
	
	
	private long shopinterval;
	private BukkitTask shopCheckTask;
	private boolean useShops;
	private ArrayList<Double> updateAfterLoad = new ArrayList<Double>();
	public final double version = 1.27;
	
	
	private ArrayList<String> tables = new ArrayList<String>();
	
	public EconomyManager() {
		hc = HyperConomy.hc;
		economiesLoaded = false;
		dataLoaded = false;
		playersLoaded = false;
		loadActive = false;
		useShops = hc.gYH().gFC("config").getBoolean("config.use-shops");
		shopinterval = hc.gYH().gFC("config").getLong("config.shopcheckinterval");
		hc.getServer().getPluginManager().registerEvents(this, hc);
		tables.add("settings");
		tables.add("objects");
		tables.add("players");
		tables.add("log");
		tables.add("history");
		tables.add("audit_log");
		tables.add("shop_objects");
		tables.add("frame_shops");
		tables.add("banks");
		tables.add("shops");
		tables.add("info_signs");
		tables.add("item_displays");
		tables.add("economies");
	}
	
	public double getVersion() {
		return version;
	}
	
	public ArrayList<String> getTablesList() {
		return tables;
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
				//adds banks
				hc.getLogger().info("[HyperConomy]Updating HyperConomy database to version 1.26.");
				hc.getSQLWrite().convertExecuteSynchronously("CREATE TABLE IF NOT EXISTS hyperconomy_banks (NAME VARCHAR(255) NOT NULL PRIMARY KEY, BALANCE DOUBLE NOT NULL DEFAULT '0', OWNERS VARCHAR(255), MEMBERS VARCHAR(255))");
				hc.getSQLWrite().executeSynchronously("UPDATE hyperconomy_settings SET VALUE = '1.26' WHERE SETTING = 'version'");
			}
			if (version < 1.27) {
				hc.getLogger().info("[HyperConomy]Updating HyperConomy database to version 1.27.");
				hc.getSQLWrite().convertExecuteSynchronously("CREATE TABLE IF NOT EXISTS hyperconomy_shops (NAME VARCHAR(255) NOT NULL PRIMARY KEY, TYPE VARCHAR(255) NOT NULL, ECONOMY VARCHAR(255) NOT NULL, OWNER VARCHAR(255) NOT NULL, WORLD VARCHAR(255) NOT NULL, MESSAGE TEXT NOT NULL, BANNED_OBJECTS TEXT NOT NULL, ALLOWED_PLAYERS TEXT NOT NULL, P1X DOUBLE NOT NULL, P1Y DOUBLE NOT NULL, P1Z DOUBLE NOT NULL, P2X DOUBLE NOT NULL, P2Y DOUBLE NOT NULL, P2Z DOUBLE NOT NULL)");
				hc.getSQLWrite().convertExecuteSynchronously("CREATE TABLE IF NOT EXISTS hyperconomy_info_signs (WORLD VARCHAR(255) NOT NULL, X INTEGER NOT NULL, Y INTEGER NOT NULL, Z INTEGER NOT NULL, HYPEROBJECT VARCHAR(255) NOT NULL, TYPE VARCHAR(255) NOT NULL, MULTIPLIER INTEGER NOT NULL, ECONOMY VARCHAR(255) NOT NULL, ECLASS VARCHAR(255) NOT NULL, PRIMARY KEY(WORLD, X, Y, Z))");
				hc.getSQLWrite().convertExecuteSynchronously("CREATE TABLE IF NOT EXISTS hyperconomy_item_displays (WORLD VARCHAR(255) NOT NULL, X DOUBLE NOT NULL, Y DOUBLE NOT NULL, Z DOUBLE NOT NULL, HYPEROBJECT VARCHAR(255) NOT NULL, PRIMARY KEY(WORLD, X, Y, Z))");	
				hc.getSQLWrite().convertExecuteSynchronously("CREATE TABLE IF NOT EXISTS hyperconomy_economies (NAME VARCHAR(255) NOT NULL PRIMARY KEY, HYPERACCOUNT VARCHAR(255) NOT NULL)");
				hc.getEconomyManager().addUpdateAfterLoad(1.27);
				hc.getSQLWrite().executeSynchronously("UPDATE hyperconomy_settings SET VALUE = '1.27' WHERE SETTING = 'version'");
			}
		} else {
			createTables(hc.getSQLWrite(), false);
		}
		String query = "SELECT * FROM hyperconomy_objects WHERE economy = 'default'";
		hc.getSQLRead().syncRead(this, "load3", query, null);
	}
	public void createTables(SQLWrite sw, boolean copydatabase) {
		if (copydatabase) {
			for (String table:tables) {
				sw.convertExecuteSynchronously("DROP TABLE IF EXISTS hyperconomy_"+table);
			}
		}
		sw.convertExecuteSynchronously("CREATE TABLE IF NOT EXISTS hyperconomy_settings (SETTING VARCHAR(255) NOT NULL, VALUE TEXT, TIME DATETIME NOT NULL, PRIMARY KEY (SETTING))");
		sw.convertExecuteSynchronously("CREATE TABLE IF NOT EXISTS hyperconomy_objects (NAME VARCHAR(255) NOT NULL, ECONOMY VARCHAR(255) NOT NULL, DISPLAY_NAME VARCHAR(255), ALIASES VARCHAR(1000), TYPE TINYTEXT, MATERIAL TINYTEXT, DATA INTEGER, DURABILITY INTEGER, VALUE DOUBLE, STATIC TINYTEXT, STATICPRICE DOUBLE, STOCK DOUBLE, MEDIAN DOUBLE, INITIATION TINYTEXT, STARTPRICE DOUBLE, CEILING DOUBLE, FLOOR DOUBLE, MAXSTOCK DOUBLE NOT NULL DEFAULT '1000000', PRIMARY KEY (NAME, ECONOMY))");
		sw.convertExecuteSynchronously("CREATE TABLE IF NOT EXISTS hyperconomy_players (PLAYER VARCHAR(255) NOT NULL PRIMARY KEY, ECONOMY TINYTEXT, BALANCE DOUBLE NOT NULL DEFAULT '0', X DOUBLE NOT NULL DEFAULT '0', Y DOUBLE NOT NULL DEFAULT '0', Z DOUBLE NOT NULL DEFAULT '0', WORLD TINYTEXT NOT NULL, HASH VARCHAR(255) NOT NULL DEFAULT '', SALT VARCHAR(255) NOT NULL DEFAULT '')");
		sw.convertExecuteSynchronously("CREATE TABLE IF NOT EXISTS hyperconomy_log (ID INTEGER NOT NULL PRIMARY KEY AUTO_INCREMENT, TIME DATETIME, CUSTOMER TINYTEXT, ACTION TINYTEXT, OBJECT TINYTEXT, AMOUNT DOUBLE, MONEY DOUBLE, TAX DOUBLE, STORE TINYTEXT, TYPE TINYTEXT)");
		sw.convertExecuteSynchronously("CREATE TABLE IF NOT EXISTS hyperconomy_history (ID INTEGER NOT NULL PRIMARY KEY AUTO_INCREMENT, OBJECT TINYTEXT, ECONOMY TINYTEXT, TIME DATETIME, PRICE DOUBLE)");
		sw.convertExecuteSynchronously("CREATE TABLE IF NOT EXISTS hyperconomy_audit_log (ID INTEGER NOT NULL PRIMARY KEY AUTO_INCREMENT, TIME DATETIME NOT NULL, ACCOUNT TINYTEXT NOT NULL, ACTION TINYTEXT NOT NULL, AMOUNT DOUBLE NOT NULL, ECONOMY TINYTEXT NOT NULL)");
		sw.convertExecuteSynchronously("CREATE TABLE IF NOT EXISTS hyperconomy_shop_objects (ID INTEGER NOT NULL PRIMARY KEY AUTO_INCREMENT, SHOP VARCHAR(255) NOT NULL, HYPEROBJECT VARCHAR(255) NOT NULL, QUANTITY DOUBLE NOT NULL, SELL_PRICE DOUBLE NOT NULL, BUY_PRICE DOUBLE NOT NULL, MAX_STOCK INTEGER NOT NULL DEFAULT '1000000', STATUS VARCHAR(255) NOT NULL)");
		sw.convertExecuteSynchronously("CREATE TABLE IF NOT EXISTS hyperconomy_frame_shops (ID INTEGER NOT NULL PRIMARY KEY, HYPEROBJECT VARCHAR(255) NOT NULL, ECONOMY TINYTEXT, SHOP VARCHAR(255), TRADE_AMOUNT INTEGER NOT NULL, X DOUBLE NOT NULL DEFAULT '0', Y DOUBLE NOT NULL DEFAULT '0', Z DOUBLE NOT NULL DEFAULT '0', WORLD TINYTEXT NOT NULL)");
		sw.convertExecuteSynchronously("CREATE TABLE IF NOT EXISTS hyperconomy_banks (NAME VARCHAR(255) NOT NULL PRIMARY KEY, BALANCE DOUBLE NOT NULL DEFAULT '0', OWNERS VARCHAR(255), MEMBERS VARCHAR(255))");
		sw.convertExecuteSynchronously("CREATE TABLE IF NOT EXISTS hyperconomy_shops (NAME VARCHAR(255) NOT NULL PRIMARY KEY, TYPE VARCHAR(255) NOT NULL, ECONOMY VARCHAR(255) NOT NULL, OWNER VARCHAR(255) NOT NULL, WORLD VARCHAR(255) NOT NULL, MESSAGE TEXT NOT NULL, BANNED_OBJECTS TEXT NOT NULL, ALLOWED_PLAYERS TEXT NOT NULL, P1X DOUBLE NOT NULL, P1Y DOUBLE NOT NULL, P1Z DOUBLE NOT NULL, P2X DOUBLE NOT NULL, P2Y DOUBLE NOT NULL, P2Z DOUBLE NOT NULL)");
		sw.convertExecuteSynchronously("CREATE TABLE IF NOT EXISTS hyperconomy_info_signs (WORLD VARCHAR(255) NOT NULL, X INTEGER NOT NULL, Y INTEGER NOT NULL, Z INTEGER NOT NULL, HYPEROBJECT VARCHAR(255) NOT NULL, TYPE VARCHAR(255) NOT NULL, MULTIPLIER INTEGER NOT NULL, ECONOMY VARCHAR(255) NOT NULL, ECLASS VARCHAR(255) NOT NULL, PRIMARY KEY(WORLD, X, Y, Z))");
		sw.convertExecuteSynchronously("CREATE TABLE IF NOT EXISTS hyperconomy_item_displays (WORLD VARCHAR(255) NOT NULL, X DOUBLE NOT NULL, Y DOUBLE NOT NULL, Z DOUBLE NOT NULL, HYPEROBJECT VARCHAR(255) NOT NULL, PRIMARY KEY(WORLD, X, Y, Z))");
		sw.convertExecuteSynchronously("CREATE TABLE IF NOT EXISTS hyperconomy_economies (NAME VARCHAR(255) NOT NULL PRIMARY KEY, HYPERACCOUNT VARCHAR(255) NOT NULL)");
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
				//ArrayList<String> econs = sr.getStringList("hyperconomy_objects", "DISTINCT ECONOMY", null);
				ArrayList<String> econs = sr.getStringList("hyperconomy_economies", "NAME", null);
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
				QueryResult result = sr.select("SELECT * FROM hyperconomy_players");
				while (result.next()) {
					HyperPlayer hplayer = new HyperPlayer(result.getString("PLAYER"), result.getString("ECONOMY"), result.getDouble("BALANCE"), result.getDouble("X"), result.getDouble("Y"), result.getDouble("Z"), result.getString("WORLD"), result.getString("HASH"), result.getString("SALT"));
					hyperPlayers.put(hplayer.getName().toLowerCase(), hplayer);
				}
				result.close();
				playersLoaded = true;
				hyperBanks.clear();
				result = sr.select("SELECT * FROM hyperconomy_banks");
				while (result.next()) {
					HyperBank hBank = new HyperBank(result.getString("NAME"), result.getDouble("BALANCE"), result.getString("OWNERS"), result.getString("MEMBERS"));
					hyperBanks.put(hBank.getName().toLowerCase(), hBank);
				}
				result.close();
				
				shops.clear();
				if (useShops) {
					result = sr.select("SELECT * FROM hyperconomy_shops");
					while (result.next()) {
						String type = result.getString("TYPE");
						if (type.equalsIgnoreCase("server")) {
							String name = result.getString("NAME");
							Location p1 = new Location(Bukkit.getWorld(result.getString("WORLD")), result.getInt("P1X"), result.getInt("P1Y"), result.getInt("P1Z"));
							Location p2 = new Location(Bukkit.getWorld(result.getString("WORLD")), result.getInt("P2X"), result.getInt("P2Y"), result.getInt("P2Z"));
							Shop shop = new ServerShop(name, result.getString("ECONOMY"), getHyperPlayer(result.getString("OWNER")), 
									result.getString("MESSAGE"), p1, p2, result.getString("BANNED_OBJECTS"));
							shops.put(name, shop);
						} else if (type.equalsIgnoreCase("player")) {
							if (!hc.gYH().gFC("config").getBoolean("config.use-player-shops")) {continue;}
							String name = result.getString("NAME");
							Location p1 = new Location(Bukkit.getWorld(result.getString("WORLD")), result.getInt("P1X"), result.getInt("P1Y"), result.getInt("P1Z"));
							Location p2 = new Location(Bukkit.getWorld(result.getString("WORLD")), result.getInt("P2X"), result.getInt("P2Y"), result.getInt("P2Z"));
							Shop shop = new PlayerShop(name, result.getString("ECONOMY"), getHyperPlayer(result.getString("OWNER")), 
									result.getString("MESSAGE"), p1, p2, result.getString("BANNED_OBJECTS"), result.getString("ALLOWED_PLAYERS"));
							shops.put(name, shop);
						}
					}
					result.close();
				} else {
					Shop shop = new ServerShop("GlobalShop", getGlobalShopAccount().getEconomy(), getGlobalShopAccount());
					shops.put("GlobalShop", shop);
				}
				stopShopCheck();
				startShopCheck();

				hc.getServer().getScheduler().runTask(hc, new Runnable() {
					public void run() {
						addOnlinePlayers();
						createGlobalShopAccount();
						waitForDataLoad();
					}
				});
			}
		});
	}
	
	private void waitForDataLoad() {
		hc.getServer().getScheduler().runTaskLater(hc, new Runnable() {
			public void run() {
				if (dataLoaded) {return;}
				for (Shop s : getShops()) {
					if (!s.isLoaded()) {
						waitForDataLoad();
						return;
					}
				}
				dataLoaded = true;
				if (updateAfterLoad()) {
					hc.restart();
					return;
				}
				hc.getHyperEventHandler().fireDataLoadEvent();
				loadActive = false;
				hc.getHyperLock().setLoadLock(false);
			}
		}, 1L);
	}
	

	private boolean updateAfterLoad() {
		boolean restart = false;
		for (Double d:updateAfterLoad) {
			if (d.doubleValue() == 1.23) {
				hc.getLogger().info("[HyperConomy]Updating object names for version 1.23.");
				for (HyperEconomy he : getEconomies()) {
					he.updateNamesFromYml();
				}
				restart = true;
			} else if (d.doubleValue() == 1.27) {
				hc.getLogger().info("[HyperConomy]Importing YML shops for version 1.27.");
				hc.gYH().registerFileConfiguration("shops");
				FileConfiguration sh = hc.gYH().gFC("shops");
				LanguageFile L = hc.getLanguageFile();
				Iterator<String> it = sh.getKeys(false).iterator();
				while (it.hasNext()) {
					HashMap<String,String> values = new HashMap<String,String>();
					Object element = it.next();
					String name = element.toString(); 
					String owner = sh.getString(name + ".owner");
					if (owner == null || owner == "") {
						owner = getGlobalShopAccount().getName();
					}
					String type = "player";
					if (owner.equalsIgnoreCase(getGlobalShopAccount().getName())) {
						type = "server";
					}
					values.put("NAME", name);
					values.put("TYPE", type);
					String economy = sh.getString(name + ".economy");
					if (economy == null || economy == "") {
						economy = "default";
					}
					values.put("ECONOMY", economy);
					values.put("OWNER", owner);
					String world = sh.getString(name + ".world");
					if (world == null || world == "") {
						world = "world";
					}
					values.put("WORLD", world);

					String message1 = sh.getString(name + ".shopmessage1");
					if (message1 == null || message1 == "") {
						message1 = "&aWelcome to "+name+"";
					}
					message1 = message1.replace("%n", name);
					String message2 = sh.getString(name + ".shopmessage2");
					if (message2 == null || message2 == "") {
						message2 = "&9Type &b/hc &9for help.";
					}
					message2 = message2.replace("%n", name);
					String message = L.get("SHOP_LINE_BREAK")+"%n"+message1+"%n"+message2+"%n"+L.get("SHOP_LINE_BREAK");
					values.put("MESSAGE", message);
					values.put("P1X", sh.getString(name + ".p1.x"));
					values.put("P1Y", sh.getString(name + ".p1.y"));
					values.put("P1Z", sh.getString(name + ".p1.z"));
					values.put("P2X", sh.getString(name + ".p2.x"));
					values.put("P2Y", sh.getString(name + ".p2.y"));
					values.put("P2Z", sh.getString(name + ".p2.z"));
					String banned = sh.getString(name + ".unavailable");
					if (banned == null) {
						banned = "";
					}
					values.put("BANNED_OBJECTS", banned);
					String allowed = sh.getString(name + ".allowed");
					if (allowed == null) {
						allowed = "";
					}
					values.put("ALLOWED_PLAYERS", allowed);
					hc.getSQLWrite().performInsert("hyperconomy_shops", values);
				}
				
				hc.gYH().registerFileConfiguration("signs");
				FileConfiguration sns = hc.gYH().gFC("signs");
				hc.getLogger().info("[HyperConomy]Importing YML info signs for version 1.27.");
				Iterator<String> iterat = sns.getKeys(false).iterator();
				while (iterat.hasNext()) {
					String signKey = iterat.next().toString();
					String key = signKey;
					String world = signKey.substring(0, signKey.indexOf("|"));
					signKey = signKey.substring(signKey.indexOf("|") + 1, signKey.length());
					int x = Integer.parseInt(signKey.substring(0, signKey.indexOf("|")));
					signKey = signKey.substring(signKey.indexOf("|") + 1, signKey.length());
					int y = Integer.parseInt(signKey.substring(0, signKey.indexOf("|")));
					signKey = signKey.substring(signKey.indexOf("|") + 1, signKey.length());
					int z = Integer.parseInt(signKey);
					
					String name = sns.getString(key + ".itemname");
					SignType type = SignType.fromString(sns.getString(key + ".type"));
					String economy = sns.getString(key + ".economy");
					EnchantmentClass enchantClass = EnchantmentClass.fromString(sns.getString(key + ".enchantclass"));
					int multiplier = sns.getInt(key + ".multiplier");
					if (multiplier < 1) {
						multiplier = 1;
					}
					HashMap<String,String> values = new HashMap<String,String>();
					values.put("WORLD", world);
					values.put("X", x+"");
					values.put("Y", y+"");
					values.put("Z", z+"");
					values.put("HYPEROBJECT", name);
					values.put("TYPE", type.toString());
					values.put("MULTIPLIER", multiplier+"");
					values.put("ECONOMY", economy);
					values.put("ECLASS", enchantClass.toString());
					hc.getSQLWrite().performInsert("hyperconomy_info_signs", values);
				}
				
				
				
				hc.gYH().registerFileConfiguration("displays");
				FileConfiguration displays = hc.gYH().gFC("displays");
				hc.getLogger().info("[HyperConomy]Importing YML item displays for version 1.27.");
				iterat = displays.getKeys(false).iterator();
				while (iterat.hasNext()) {
					String key = iterat.next().toString();
					String name = displays.getString(key + ".name");
					String world = displays.getString(key + ".world");
					String x = displays.getString(key + ".x");
					String y = displays.getString(key + ".y");
					String z = displays.getString(key + ".z");
					HashMap<String,String> values = new HashMap<String,String>();
					values.put("WORLD", world);
					values.put("X", x);
					values.put("Y", y);
					values.put("Z", z);
					values.put("HYPEROBJECT", name);
					hc.getSQLWrite().performInsert("hyperconomy_item_displays", values);
				}
				
				
				ArrayList<String> econs = sr.getStringList("hyperconomy_objects", "DISTINCT ECONOMY", null);
				for (String econ:econs) {
					HashMap<String,String> values = new HashMap<String,String>();
					values.put("NAME", econ);
					values.put("HYPERACCOUNT", "hyperconomy");
					hc.getSQLWrite().performInsert("hyperconomy_economies", values);
				}
				
				
				
				restart = true;
			} else if (d.doubleValue() == 1.28) {
				hc.getLogger().info("[HyperConomy]Updating for version 1.28.");
			}
		}
		return restart;
	}
	
	public boolean dataLoaded() {
		return dataLoaded;
	}
	public HyperEconomy getEconomy(String name) {
		for (Map.Entry<String,HyperEconomy> entry : economies.entrySet()) {
			HyperEconomy he = entry.getValue();
			if (he.getName().equalsIgnoreCase(name)) {
				return he;
			}
		}
		return null;
	}
	public HyperEconomy getDefaultEconomy() {
		return getEconomy("default");
	}
	
	/*
	public void addEconomy(String name) {
		if (!economies.containsKey(name)) {
			economies.put(name, new HyperEconomy(name));
		}
	}
	*/
	
	public boolean economyExists(String economy) {
		for (Map.Entry<String,HyperEconomy> entry : economies.entrySet()) {
			HyperEconomy he = entry.getValue();
			if (he.getName().equalsIgnoreCase(economy)) {
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
			econs.add(he.getName());
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
		HashMap<String,String> values = new HashMap<String,String>();
		values.put("NAME", economy);
		values.put("HYPERACCOUNT", hc.gYH().gFC("config").getString("config.global-shop-account"));
		sw.performInsert("hyperconomy_economies", values);
		for (HyperObject ho:defaultEconomy.getHyperObjects()) {
			values = new HashMap<String,String>();
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
		HashMap<String,String> conditions = new HashMap<String,String>();
		conditions.put("ECONOMY", economy);
		hc.getSQLWrite().performDelete("hyperconomy_objects", conditions);
		conditions = new HashMap<String,String>();
		conditions.put("NAME", economy);
		hc.getSQLWrite().performDelete("hyperconomy_economies", conditions);
		hc.restart();
	}

	
	
	public void createEconomyFromYml(String econ, boolean restart) {
		if (hc.gYH().gFC("config").getBoolean("config.run-automatic-backups")) {
			new Backup();
		}
		SQLWrite sw = hc.getSQLWrite();
		HashMap<String,String> values = new HashMap<String,String>();
		values.put("NAME", econ);
		values.put("HYPERACCOUNT", hc.gYH().gFC("config").getString("config.global-shop-account"));
		sw.performInsert("hyperconomy_economies", values);
		FileConfiguration objects = hc.gYH().gFC("objects");
		Iterator<String> it = objects.getKeys(false).iterator();
		while (it.hasNext()) {
			String itemname = it.next().toString();
			String category = objects.getString(itemname + ".information.category");
			if (category == null) {
				category = "unknown";
			}
			
			values = new HashMap<String,String>();
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
	
	

	
	

	
	
	

	
	
	public HyperBank getHyperBank(String name) {
		if (name == null) {return null;}
		String bankName = name.toLowerCase();
		if (hyperBanks.containsKey(bankName)) {
			return hyperBanks.get(bankName);
		}
		return null;
	}

	public void addHyperBank(HyperBank hb) {
		if (hb == null) {return;}
		if (!hyperBanks.contains(hb)) {
			hyperBanks.put(hb.getName().toLowerCase(), hb);
		}
	}
	
	public void removeHyperBank(HyperBank hb) {
		if (hb == null) {return;}
		if (hyperBanks.contains(hb)) {
			hyperBanks.remove(hb.getName().toLowerCase());
		}
	}
	
	public boolean hasBank(String name) {
		if (name == null) {return false;}
		return hyperBanks.containsKey(name.toLowerCase());
	}
	
	public ArrayList<HyperBank> getHyperBanks() {
		ArrayList<HyperBank> hbs = new ArrayList<HyperBank>();
		for (HyperBank hb:hyperBanks.values()) {
			hbs.add(hb);
		}
		return hbs;
	}
	
	public ArrayList<String> getHyperBankNames() {
		ArrayList<String> hbs = new ArrayList<String>();
		for (HyperBank hb:hyperBanks.values()) {
			hbs.add(hb.getName());
		}
		return hbs;
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
			if (!hyperPlayerExists(name)) {
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
			if (!hyperPlayerExists(name)) {
				addPlayer(name);
			}
			if (hyperPlayers.containsKey(name.toLowerCase())) {
				HyperPlayer hp = hyperPlayers.get(name.toLowerCase());
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
			if (!hyperPlayerExists(p.getName())) {
				addPlayer(p.getName());
			}
		}
	}
	
	
	public boolean accountExists(String name) {
		if (hyperPlayerExists(name) || hasBank(name)) {
			return true;
		}
		return false;
	}
	
	public HyperAccount getAccount(String name) {
		if (hyperPlayerExists(name)) {
			return getHyperPlayer(name);
		}
		if (hasBank(name)) {
			return getHyperBank(name);
		}
		return null;
	}
	
	
	public boolean hyperPlayerExists(String name) {
		String playerName = name.toLowerCase();
		if (hc.useExternalEconomy()) {
			if (hc.getEconomy().hasAccount(name)) {
				if (!hyperPlayers.containsKey(playerName)) {
					addPlayer(name);
				}
				return true;
			} else {
				return false;
			}
		} else {
			return hyperPlayers.containsKey(playerName);
		}
	}
	

	public HyperPlayer getHyperPlayer(String player) {
		String playerName = player.toLowerCase();
		if (hyperPlayers.containsKey(playerName) && hyperPlayers.get(playerName) != null) {
			return hyperPlayers.get(playerName);
		} else {
			if (hyperPlayers.get(playerName) == null) {
				hyperPlayers.remove(playerName);
			}
			return addPlayer(player);
		}
	}
	public HyperPlayer getHyperPlayer(Player player) {
		return getHyperPlayer(player.getName());
	}
	public ArrayList<HyperPlayer> getHyperPlayers() {
		ArrayList<HyperPlayer> hps = new ArrayList<HyperPlayer>();
		for (HyperPlayer hp:hyperPlayers.values()) {
			hps.add(hp);
		}
		return hps;
	}
	
	
	public void addHyperPlayer(HyperPlayer hp) {
		if (!hyperPlayers.contains(hp)) {
			hyperPlayers.put(hp.getName().toLowerCase(), hp);
		}
	}
	public void removeHyperPlayer(HyperPlayer hp) {
		if (hyperPlayers.contains(hp)) {
			hyperPlayers.remove(hp.getName().toLowerCase());
		}
	}
	
	

	
	

	public HyperPlayer addPlayer(String player) {
		if (!playersLoaded) {return null;}
		String playerName = player.toLowerCase();
		if (!hyperPlayers.containsKey(playerName)) {
			HyperPlayer newHp = new HyperPlayer(player);
			hyperPlayers.put(playerName, newHp);
			return newHp;
		} else {
			HyperPlayer hp = hyperPlayers.get(playerName);
			if (hp != null) {
				return hp;
			} else {
				hyperPlayers.remove(playerName);
				HyperPlayer newHp = new HyperPlayer(player);
				hyperPlayers.put(playerName, newHp);
				return newHp;
			}
		}
	}




/*
	public boolean createPlayerAccount(String player) {
		if (!hasAccount(player)) {
			addPlayer(player);
			return true;
		} else {
			return false;
		}
	}
*/
	
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
		if (!accountExists(globalAccount)) {
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
