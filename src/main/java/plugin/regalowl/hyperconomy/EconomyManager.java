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

public class EconomyManager implements Listener {

	private HyperConomy hc;
	private SQLRead sr;
	private boolean economiesLoaded;
	private BukkitTask wait;
	private boolean loadActive;
	
	
	private ConcurrentHashMap<String, HyperEconomy> economies = new ConcurrentHashMap<String, HyperEconomy>();
	private ConcurrentHashMap<String, HyperPlayer> hyperPlayers = new ConcurrentHashMap<String, HyperPlayer>();
	private ConcurrentHashMap<String, Shop> shops = new ConcurrentHashMap<String, Shop>();
	
	
	
	private long shopinterval;
	private BukkitTask shopCheckTask;
	private boolean useShops;
	private boolean dataLoaded;
	
	
	
	
	
	
	public EconomyManager() {
		hc = HyperConomy.hc;
		dataLoaded = false;
		loadActive = false;
		economiesLoaded = false;
		useShops = hc.gYH().gFC("config").getBoolean("config.use-shops");
		shopinterval = hc.gYH().gFC("config").getLong("config.shopcheckinterval");
		hc.getServer().getPluginManager().registerEvents(this, hc);
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
				hc.getLogger().info("[HyperConomy]Updating HyperConomy database to version 1.2.");
				hc.getSQLWrite().convertExecuteSynchronously("CREATE TABLE IF NOT EXISTS hyperconomy_shop_objects (ID INTEGER NOT NULL PRIMARY KEY AUTO_INCREMENT, SHOP VARCHAR(255) NOT NULL, HYPEROBJECT VARCHAR(255) NOT NULL, QUANTITY DOUBLE NOT NULL, PRICE DOUBLE NOT NULL, STATUS VARCHAR(255) NOT NULL)");
				hc.getSQLWrite().executeSynchronously("UPDATE hyperconomy_settings SET VALUE = '1.2' WHERE SETTING = 'version'");
			}
			if (version < 1.21) {
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
				hc.getLogger().info("[HyperConomy]Updating HyperConomy database to version 1.22.");
				hc.getSQLWrite().convertExecuteSynchronously("CREATE TABLE IF NOT EXISTS hyperconomy_frame_shops (ID INTEGER NOT NULL PRIMARY KEY, HYPEROBJECT VARCHAR(255) NOT NULL, ECONOMY TINYTEXT, SHOP VARCHAR(255), X DOUBLE NOT NULL DEFAULT '0', Y DOUBLE NOT NULL DEFAULT '0', Z DOUBLE NOT NULL DEFAULT '0', WORLD TINYTEXT NOT NULL)");
				hc.getSQLWrite().executeSynchronously("UPDATE hyperconomy_settings SET VALUE = '1.22' WHERE SETTING = 'version'");
			}
			if (version < 1.23) {
				//for next version
			}
		} else {
			createTables();
		}
		String query = "SELECT NAME FROM hyperconomy_objects WHERE ECONOMY='default'";
		hc.getSQLRead().syncRead(this, "load3", query, null);
	}
	public void createTables() {
		hc.getSQLWrite().convertExecuteSynchronously("CREATE TABLE IF NOT EXISTS hyperconomy_settings (SETTING VARCHAR(255) NOT NULL, VALUE TEXT, TIME DATETIME NOT NULL, PRIMARY KEY (SETTING))");
		hc.getSQLWrite().convertExecuteSynchronously("DELETE FROM hyperconomy_settings");
		hc.getSQLWrite().convertExecuteSynchronously("INSERT INTO hyperconomy_settings (SETTING, VALUE, TIME) VALUES ('version', '1.21', NOW() )");
		hc.getSQLWrite().convertExecuteSynchronously("CREATE TABLE IF NOT EXISTS hyperconomy_objects (NAME VARCHAR(255) NOT NULL, ECONOMY VARCHAR(255) NOT NULL, TYPE TINYTEXT, MATERIAL TINYTEXT, DATA INTEGER, DURABILITY INTEGER, VALUE DOUBLE, STATIC TINYTEXT, STATICPRICE DOUBLE, STOCK DOUBLE, MEDIAN DOUBLE, INITIATION TINYTEXT, STARTPRICE DOUBLE, CEILING DOUBLE, FLOOR DOUBLE, MAXSTOCK DOUBLE NOT NULL DEFAULT '1000000', PRIMARY KEY (NAME, ECONOMY))");
		hc.getSQLWrite().convertExecuteSynchronously("CREATE TABLE IF NOT EXISTS hyperconomy_players (PLAYER VARCHAR(255) NOT NULL PRIMARY KEY, ECONOMY TINYTEXT, BALANCE DOUBLE NOT NULL DEFAULT '0', X DOUBLE NOT NULL DEFAULT '0', Y DOUBLE NOT NULL DEFAULT '0', Z DOUBLE NOT NULL DEFAULT '0', WORLD TINYTEXT NOT NULL, HASH VARCHAR(255) NOT NULL DEFAULT '', SALT VARCHAR(255) NOT NULL DEFAULT '')");
		hc.getSQLWrite().convertExecuteSynchronously("CREATE TABLE IF NOT EXISTS hyperconomy_log (ID INTEGER NOT NULL PRIMARY KEY AUTO_INCREMENT, TIME DATETIME, CUSTOMER TINYTEXT, ACTION TINYTEXT, OBJECT TINYTEXT, AMOUNT DOUBLE, MONEY DOUBLE, TAX DOUBLE, STORE TINYTEXT, TYPE TINYTEXT)");
		hc.getSQLWrite().convertExecuteSynchronously("CREATE TABLE IF NOT EXISTS hyperconomy_history (ID INTEGER NOT NULL PRIMARY KEY AUTO_INCREMENT, OBJECT TINYTEXT, ECONOMY TINYTEXT, TIME DATETIME, PRICE DOUBLE)");
		hc.getSQLWrite().convertExecuteSynchronously("CREATE TABLE IF NOT EXISTS hyperconomy_audit_log (ID INTEGER NOT NULL PRIMARY KEY AUTO_INCREMENT, TIME DATETIME NOT NULL, ACCOUNT TINYTEXT NOT NULL, ACTION TINYTEXT NOT NULL, AMOUNT DOUBLE NOT NULL, ECONOMY TINYTEXT NOT NULL)");
		hc.getSQLWrite().convertExecuteSynchronously("CREATE TABLE IF NOT EXISTS hyperconomy_shop_objects (ID INTEGER NOT NULL PRIMARY KEY AUTO_INCREMENT, SHOP VARCHAR(255) NOT NULL, HYPEROBJECT VARCHAR(255) NOT NULL, QUANTITY DOUBLE NOT NULL, PRICE DOUBLE NOT NULL, STATUS VARCHAR(255) NOT NULL)");
		hc.getSQLWrite().convertExecuteSynchronously("CREATE TABLE IF NOT EXISTS hyperconomy_frame_shops (ID INTEGER NOT NULL PRIMARY KEY, HYPEROBJECT VARCHAR(255) NOT NULL, ECONOMY TINYTEXT, SHOP VARCHAR(255), X DOUBLE NOT NULL DEFAULT '0', Y DOUBLE NOT NULL DEFAULT '0', Z DOUBLE NOT NULL DEFAULT '0', WORLD TINYTEXT NOT NULL)");
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
				loadData();
				waitForLoad();
			}
		});
	}
	private void waitForLoad() {
		wait = hc.getServer().getScheduler().runTaskTimer(hc, new Runnable() {
			public void run() {
				boolean loaded = true;
				for (HyperEconomy he : getEconomies()) {
					if (!he.dataLoaded() || !dataLoaded) {
						loaded = false;
					}
				}
				if (loaded) {
					hc.getHyperLock().setLoadLock(false);
					economiesLoaded = true;
					wait.cancel();
					hc.getHyperEventHandler().fireDataLoadEvent();
					loadActive = false;
				}
			}
		}, 1L, 1L);
	}
	
	
	public boolean economiesLoaded() {
		return economiesLoaded;
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
			values.put("ECONOMY", economy);
			values.put("TYPE", ho.getType().toString());
			values.put("VALUE", ho.getValue()+"");
			values.put("STATIC", ho.getIsstatic());
			values.put("STATICPRICE", ho.getStaticprice()+"");
			values.put("STOCK", ho.getStock()+"");
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
	
	

	
	

	
	
	
	private void loadData() {
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
						dataLoaded = true;
					}
				});
			}
		});
	}
	
	
	
	
	
	
	
	
	
	
	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerJoin(PlayerJoinEvent event) {
		try {
			if (!economiesLoaded()) {return;}
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
			if (!economiesLoaded()) {return;}
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
			return hyperPlayers.put(player, new HyperPlayer(player));
		} else {
			HyperPlayer hp = hyperPlayers.get(player);
			if (hp != null) {
				return hp;
			} else {
				hyperPlayers.remove(player);
				return hyperPlayers.put(player, new HyperPlayer(player));
			}
		}
	}



	public boolean hasAccount(String name) {
		if (hc.useExternalEconomy()) {
			if (hc.getEconomy().hasAccount(name)) {
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
		Log l = hc.getLog();
		String globalaccount = hc.gYH().gFC("config").getString("config.global-shop-account");
		if (hc.useExternalEconomy()) {
			if (!hc.getEconomy().hasAccount(globalaccount)) {
				getHyperPlayer(globalaccount).setBalance(hc.gYH().gFC("config").getDouble("config.initialshopbalance"));
				l.writeAuditLog(globalaccount, "setbalance", hc.gYH().gFC("config").getDouble("config.initialshopbalance"), hc.getEconomy().getName());
			}
		} else {
			if (!hasAccount(globalaccount)) {
				createPlayerAccount(globalaccount);
				getHyperPlayer(globalaccount).setBalance(hc.gYH().gFC("config").getDouble("config.initialshopbalance"));
				l.writeAuditLog(globalaccount, "setbalance", hc.gYH().gFC("config").getDouble("config.initialshopbalance"), "HyperConomy");
			}
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
