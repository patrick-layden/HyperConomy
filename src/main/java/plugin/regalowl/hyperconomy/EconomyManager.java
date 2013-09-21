package regalowl.hyperconomy;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.scheduler.BukkitTask;

public class EconomyManager {

	private HyperConomy hc;
	private SQLRead sr;
	private boolean economiesLoaded;
	private BukkitTask waitToLoad;
	private BukkitTask waitForLoad;
	private boolean loadActive;
	
	private ConcurrentHashMap<String, HyperEconomy> economies = new ConcurrentHashMap<String, HyperEconomy>();
	
	EconomyManager() {
		hc = HyperConomy.hc;
		loadActive = false;
		economiesLoaded = false;
	}
	
	
	public void load() {
		if (!loadActive) {
			loadActive = true;
			economiesLoaded = false;
			hc.loadLock(true);
			sr = hc.getSQLRead();
			hc.getServer().getScheduler().runTaskAsynchronously(hc, new Runnable() {
				public void run() {
					ArrayList<String> testdata = sr.getStringColumn("SELECT NAME FROM hyperconomy_objects WHERE ECONOMY='default'");
					if (testdata.size() > 0) {
						hc.getServer().getScheduler().runTask(hc, new Runnable() {
							public void run() {
								waitForBuffer();
							}
						});
					} else {
						hc.getServer().getScheduler().runTask(hc, new Runnable() {
							public void run() {
								createEconomyFromYml("default");
								waitForBuffer();
							}
						});
					}
				}
			});
		}
	}
	private void waitForBuffer() {
		waitToLoad = hc.getServer().getScheduler().runTaskTimer(hc, new Runnable() {
			public void run() {
				SQLWrite sw = hc.getSQLWrite();
				if (sw.getBuffer().size() == 0) {
					buildEconomies();
					waitToLoad.cancel();
				}
			}
		}, 0L, 1L);
	}
	private void buildEconomies() {
		hc.getServer().getScheduler().runTaskAsynchronously(hc, new Runnable() {
			public void run() {
				economies.clear();
				ArrayList<String> econs = sr.getStringColumn("SELECT DISTINCT ECONOMY FROM hyperconomy_objects");
				for (String e : econs) {
					economies.put(e, new HyperEconomy(e));
				}
				waitForLoad();
			}
		});
	}
	private void waitForLoad() {
		waitForLoad = hc.getServer().getScheduler().runTaskTimer(hc, new Runnable() {
			public void run() {
				boolean loaded = true;
				for (HyperEconomy he : getEconomies()) {
					if (!he.dataLoaded()) {
						loaded = false;
					}
				}
				if (loaded) {
					hc.loadLock(false);
					loadActive = false;
					economiesLoaded = true;
					waitForLoad.cancel();
					hc.onDataLoad();
				}
			}
		}, 0L, 1L);
	}
	
	
	public boolean economiesLoaded() {
		return economiesLoaded;
	}
	public HyperEconomy getEconomy(String name) {
		if (economies.containsKey(name)) {
			return economies.get(name);
		} else {
			return null;
		}
	}
	
	public void addEconomy(String name) {
		if (!economies.containsKey(name)) {
			economies.put(name, new HyperEconomy(name));
		}
	}
	
	public boolean economyExists(String name) {
		if (economies.containsKey(name)) {
			return true;
		}
		return false;
	}
	
	public boolean testEconomy(String economy) {
		if (economies.contains(economy)) {
			return true;
		} else {
			return false;
		}
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
	}


	public ArrayList<String> getEconomyList() {
		ArrayList<String> econs = new ArrayList<String>();
		for (int i = 0; i < economies.size(); i++) {
			if (!econs.contains(economies.get(i))) {
				econs.add(economies.get(i).getEconomy());
			}
		}
		return econs;
	}
	public ArrayList<String> getShopList() {
		ArrayList<String> shops = new ArrayList<String>();
		for (Map.Entry<String,HyperEconomy> entry : economies.entrySet()) {
			HyperEconomy he = entry.getValue();
			for (Shop s:he.getShops()) {
				shops.add(s.getName());
			}
		}
		return shops;
	}
	public ArrayList<Shop> getShops() {
		ArrayList<Shop> shops = new ArrayList<Shop>();
		for (Map.Entry<String,HyperEconomy> entry : economies.entrySet()) {
			HyperEconomy he = entry.getValue();
			for (Shop s:he.getShops()) {
				shops.add(s);
			}
		}
		return shops;
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
	public ArrayList<HyperPlayer> getHyperPlayers() {
		ArrayList<HyperPlayer> hyperPlayers = new ArrayList<HyperPlayer>();
		for (Map.Entry<String,HyperEconomy> entry : economies.entrySet()) {
			HyperEconomy he = entry.getValue();
			for (HyperPlayer hp:he.getHyperPlayers()) {
				hyperPlayers.add(hp);
			}
		}
		return hyperPlayers;
	}
	
	
	public boolean hyperPlayerExists(String name) {
		for (Map.Entry<String,HyperEconomy> entry : economies.entrySet()) {
			HyperEconomy he = entry.getValue();
			if (he.hasAccount(name)) {
				return true;
			}
		}
		return false;
	}
	
	public boolean shopExists(String name) {
		for (Map.Entry<String,HyperEconomy> entry : economies.entrySet()) {
			HyperEconomy he = entry.getValue();
			if (he.shopExists(name)) {
				return true;
			}
		}
		return false;
	}
	
	public Shop getShop(String name) {
		for (Map.Entry<String,HyperEconomy> entry : economies.entrySet()) {
			HyperEconomy he = entry.getValue();
			if (he.shopExists(name)) {
				return he.getShop(name);
			}
		}
		return null;
	}
	
	public HyperPlayer getHyperPlayer(String name) {
		for (Map.Entry<String,HyperEconomy> entry : economies.entrySet()) {
			HyperEconomy he = entry.getValue();
			if (he.hasAccount(name)) {
				return he.getHyperPlayer(name);
			}
		}
		HyperEconomy he = getEconomy("default");
		return he.addPlayer("name");
	}
	
	public HyperPlayer getGlobalShopAccount() {
		return getHyperPlayer(hc.getYaml().getConfig().getString("config.global-shop-account"));
	}
	

	
	public void createNewEconomy(String economy) {
		HyperEconomy defaultEconomy = getEconomy("default");
		ArrayList<String> items = defaultEconomy.getItemNames();
		ArrayList<String> enchants = defaultEconomy.getEnchantNames();
		ArrayList<String> statements = new ArrayList<String>();
		for (int i = 0; i < items.size(); i++) {
			String type = "item";
			if (items.get(i).equalsIgnoreCase("xp")) {
				type = "experience";
			}
			String c = items.get(i);
			HyperObject ho = defaultEconomy.getHyperObject(c);
			statements.add("Insert Into hyperconomy_objects (NAME, ECONOMY, TYPE, CATEGORY, MATERIAL, ID, DATA, DURABILITY, VALUE, STATIC, STATICPRICE, STOCK, MEDIAN, INITIATION, STARTPRICE, CEILING, FLOOR, MAXSTOCK)" + " Values ('" + c + "','" + economy + "','" + type + "','" + ho.getCategory() + "','" + ho.getMaterial() + "','" + ho.getId() + "','" + ho.getData() + "','" + ho.getDurability() + "','" + ho.getValue() + "','"
					+ ho.getIsstatic() + "','" + ho.getStaticprice() + "','" + 0.0 + "','" + ho.getMedian() + "','" + "true" + "','" + ho.getStartprice() + "','" + ho.getCeiling() + "','" + ho.getFloor() + "','" + ho.getMaxstock() + "')");
		}
		for (int i = 0; i < enchants.size(); i++) {
			String type = "enchantment";
			String c = enchants.get(i);
			HyperObject ho = defaultEconomy.getHyperObject(c);
			statements.add("Insert Into hyperconomy_objects (NAME, ECONOMY, TYPE, CATEGORY, MATERIAL, ID, DATA, DURABILITY, VALUE, STATIC, STATICPRICE, STOCK, MEDIAN, INITIATION, STARTPRICE, CEILING, FLOOR, MAXSTOCK)" + " Values ('" + c + "','" + economy + "','" + type + "','" + ho.getCategory() + "','" + ho.getMaterial() + "','" + ho.getId() + "','" + ho.getData() + "','" + ho.getDurability() + "','" + ho.getValue() + "','"
					+ ho.getIsstatic() + "','" + ho.getStaticprice() + "','" + 0.0 + "','" + ho.getMedian() + "','" + "true" + "','" + ho.getStartprice() + "','" + ho.getCeiling() + "','" + ho.getFloor() + "','" + ho.getMaxstock() + "')");
		}
		SQLWrite sw = hc.getSQLWrite();
		sw.executeSQL(statements);
		buildEconomies();
	}
	public void deleteEconomy(String economy) {
		hc.getSQLWrite().executeSQL("DELETE FROM hyperconomy_objects WHERE ECONOMY='" + economy + "'");
		buildEconomies();
	}

	

	public void createEconomyFromYml(String econ) {
		FileConfiguration itemsyaml = hc.getYaml().getItems();
		FileConfiguration enchantsyaml = hc.getYaml().getEnchants();
		ArrayList<String> statements = new ArrayList<String>();
		Iterator<String> it = itemsyaml.getKeys(false).iterator();
		while (it.hasNext()) {
			String itemname = it.next().toString();
			String category = itemsyaml.getString(itemname + ".information.category");
			if (category == null) {
				category = "unknown";
			}
			if (!itemname.equalsIgnoreCase("xp")) {
				statements.add("Insert Into hyperconomy_objects (NAME, ECONOMY, TYPE, CATEGORY, MATERIAL, ID, DATA, DURABILITY, VALUE, STATIC, STATICPRICE, STOCK, MEDIAN, INITIATION, STARTPRICE, CEILING, FLOOR, MAXSTOCK)" + " Values ('" + itemname + "','" + econ + "','" + "item" + "','" + category + "','" + itemsyaml.getString(itemname + ".information.material") + "','" + itemsyaml.getInt(itemname + ".information.id") + "','" + itemsyaml.getInt(itemname + ".information.data") + "','"
						+ itemsyaml.getInt(itemname + ".information.data") + "','" + itemsyaml.getDouble(itemname + ".value") + "','" + itemsyaml.getString(itemname + ".price.static") + "','" + itemsyaml.getDouble(itemname + ".price.staticprice") + "','" + itemsyaml.getDouble(itemname + ".stock.stock") + "','" + itemsyaml.getDouble(itemname + ".stock.median") + "','" + itemsyaml.getString(itemname + ".initiation.initiation") + "','" + itemsyaml.getDouble(itemname + ".initiation.startprice")
						+ "','" + itemsyaml.getDouble(itemname + ".price.ceiling") + "','" + itemsyaml.getDouble(itemname + ".price.floor") + "','" + itemsyaml.getDouble(itemname + ".stock.maxstock") + "')");
			} else {
				statements.add("Insert Into hyperconomy_objects (NAME, ECONOMY, TYPE, CATEGORY, MATERIAL, ID, DATA, DURABILITY, VALUE, STATIC, STATICPRICE, STOCK, MEDIAN, INITIATION, STARTPRICE, CEILING, FLOOR, MAXSTOCK)" + " Values ('" + itemname + "','" + econ + "','" + "experience" + "','" + category + "','" + "none" + "','" + itemsyaml.getInt(itemname + ".information.id") + "','" + itemsyaml.getInt(itemname + ".information.data") + "','" + itemsyaml.getInt(itemname + ".information.data") + "','"
						+ itemsyaml.getDouble(itemname + ".value") + "','" + itemsyaml.getString(itemname + ".price.static") + "','" + itemsyaml.getDouble(itemname + ".price.staticprice") + "','" + itemsyaml.getDouble(itemname + ".stock.stock") + "','" + itemsyaml.getDouble(itemname + ".stock.median") + "','" + itemsyaml.getString(itemname + ".initiation.initiation") + "','" + itemsyaml.getDouble(itemname + ".initiation.startprice") + "','" + itemsyaml.getDouble(itemname + ".price.ceiling") + "','"
						+ itemsyaml.getDouble(itemname + ".price.floor") + "','" + itemsyaml.getDouble(itemname + ".stock.maxstock") + "')");
			}
		}
		Iterator<String> it2 = enchantsyaml.getKeys(false).iterator();
		while (it2.hasNext()) {
			String ename = it2.next().toString();
			String category = enchantsyaml.getString(ename + ".information.category");
			if (category == null) {
				category = "unknown";
			}
			statements.add("Insert Into hyperconomy_objects (NAME, ECONOMY, TYPE, CATEGORY, MATERIAL, ID, DATA, DURABILITY, VALUE, STATIC, STATICPRICE, STOCK, MEDIAN, INITIATION, STARTPRICE, CEILING, FLOOR, MAXSTOCK)" + " Values ('" + ename + "','" + econ + "','" + "enchantment" + "','" + category + "','" + enchantsyaml.getString(ename + ".information.name") + "','" + enchantsyaml.getInt(ename + ".information.id") + "','" + "-2" + "','" + "-2" + "','" + enchantsyaml.getDouble(ename + ".value") + "','"
					+ enchantsyaml.getString(ename + ".price.static") + "','" + enchantsyaml.getDouble(ename + ".price.staticprice") + "','" + enchantsyaml.getDouble(ename + ".stock.stock") + "','" + enchantsyaml.getDouble(ename + ".stock.median") + "','" + enchantsyaml.getString(ename + ".initiation.initiation") + "','" + enchantsyaml.getDouble(ename + ".initiation.startprice") + "','" + enchantsyaml.getDouble(ename + ".price.ceiling") + "','" + enchantsyaml.getDouble(ename + ".price.floor") + "','" + enchantsyaml.getDouble(ename + ".stock.maxstock") + "')");
		}
		SQLWrite sw = hc.getSQLWrite();
		sw.executeSQL(statements);
	}
	
	

	
	
	
	
	
	
	
	
	
	public boolean checkSQLLite() {
		FileTools ft = new FileTools();
		String path = ft.getJarPath() + "plugins" + File.separator + "HyperConomy" + File.separator + "HyperConomy.db";
		try {
			Class.forName("org.sqlite.JDBC");
			Connection connect = DriverManager.getConnection("jdbc:sqlite:" + path);
			try {
				Statement state = connect.createStatement();
				ResultSet result = state.executeQuery("SELECT VALUE FROM hyperconomy_settings WHERE SETTING = 'version'");
				if (result.next()) {
					double version = Double.parseDouble(result.getString("VALUE"));
					if (version == 1.0) {
						state.execute("ALTER TABLE hyperconomy_players RENAME TO hyperconomy_players_temp");
						state.execute("CREATE TABLE IF NOT EXISTS hyperconomy_players (PLAYER VARCHAR(255) NOT NULL PRIMARY KEY, ECONOMY TINYTEXT, BALANCE DOUBLE NOT NULL DEFAULT '0', X DOUBLE NOT NULL DEFAULT '0', Y DOUBLE NOT NULL DEFAULT '0', Z DOUBLE NOT NULL DEFAULT '0', WORLD TINYTEXT NOT NULL, HASH VARCHAR(255) NOT NULL DEFAULT '', SALT VARCHAR(255) NOT NULL DEFAULT '')");
						state.execute("INSERT INTO hyperconomy_players (PLAYER, ECONOMY, BALANCE, X, Y, Z, WORLD, HASH) SELECT * FROM hyperconomy_players_temp");
						state.execute("DROP TABLE hyperconomy_players_temp");
						state.execute("DROP TABLE IF EXISTS hyperconomy_settings");
						state.execute("CREATE TABLE IF NOT EXISTS hyperconomy_settings (SETTING VARCHAR(255) NOT NULL PRIMARY KEY, VALUE STRING, TIME DATETIME NOT NULL)");
						state.execute("INSERT INTO hyperconomy_settings (SETTING, VALUE, TIME)" + " VALUES ('version', '1.1', datetime('NOW', 'localtime'))");
					} 
					if (version == 1.1) {
						//for next version
					}
				}
				result.close();
				state.close();
			} catch (Exception e) {
				//e.printStackTrace();
				connect.close();
				connect = DriverManager.getConnection("jdbc:sqlite:" + path);
				Statement state = connect.createStatement();
				state.execute("CREATE TABLE IF NOT EXISTS hyperconomy_settings (SETTING VARCHAR(255) NOT NULL PRIMARY KEY, VALUE STRING, TIME DATETIME NOT NULL)");
				state.execute("DELETE FROM hyperconomy_settings");
				state.execute("INSERT INTO hyperconomy_settings (SETTING, VALUE, TIME) VALUES ('version', '1.1', datetime('NOW', 'localtime'))");
				state.close();
			}
			connect.close();
			connect = DriverManager.getConnection("jdbc:sqlite:" + path);
			Statement state = connect.createStatement();
			state.execute("CREATE TABLE IF NOT EXISTS hyperconomy_objects (NAME VARCHAR(255) NOT NULL, ECONOMY VARCHAR(255) NOT NULL, TYPE TINYTEXT, CATEGORY TINYTEXT, MATERIAL TINYTEXT, ID INT, DATA INT, DURABILITY INT, VALUE DOUBLE, STATIC TINYTEXT, STATICPRICE DOUBLE, STOCK DOUBLE, MEDIAN DOUBLE, INITIATION TINYTEXT, STARTPRICE DOUBLE, CEILING DOUBLE, FLOOR DOUBLE, MAXSTOCK DOUBLE NOT NULL DEFAULT '1000000', PRIMARY KEY (NAME, ECONOMY))");
			state.execute("CREATE TABLE IF NOT EXISTS hyperconomy_players (PLAYER VARCHAR(255) NOT NULL PRIMARY KEY, ECONOMY TINYTEXT, BALANCE DOUBLE NOT NULL DEFAULT '0', X DOUBLE NOT NULL DEFAULT '0', Y DOUBLE NOT NULL DEFAULT '0', Z DOUBLE NOT NULL DEFAULT '0', WORLD TINYTEXT NOT NULL, HASH VARCHAR(255) NOT NULL DEFAULT '', SALT VARCHAR(255) NOT NULL DEFAULT '')");
			state.execute("CREATE TABLE IF NOT EXISTS hyperconomy_log (ID INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, TIME DATETIME, CUSTOMER TINYTEXT, ACTION TINYTEXT, OBJECT TINYTEXT, AMOUNT DOUBLE, MONEY DOUBLE, TAX DOUBLE, STORE TINYTEXT, TYPE TINYTEXT)");
			state.execute("CREATE TABLE IF NOT EXISTS hyperconomy_history (ID INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, OBJECT TINYTEXT, ECONOMY TINYTEXT, TIME DATETIME, PRICE DOUBLE)");
			state.execute("CREATE TABLE IF NOT EXISTS hyperconomy_audit_log (ID INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, TIME DATETIME NOT NULL, ACCOUNT TINYTEXT NOT NULL, ACTION TINYTEXT NOT NULL, AMOUNT DOUBLE NOT NULL, ECONOMY TINYTEXT NOT NULL)");
			state.close();
			connect.close();
			return true;
		} catch (Exception e) {
			//e.printStackTrace();
			new HyperError(e);
			return false;
		}
	}
	public boolean checkMySQL() {
		try {
			double version = 0;
			hc = HyperConomy.hc;
			FileConfiguration config = hc.getYaml().getConfig();
			String username = config.getString("config.sql-connection.username");
			String password = config.getString("config.sql-connection.password");
			int port = config.getInt("config.sql-connection.port");
			String host = config.getString("config.sql-connection.host");
			String database = config.getString("config.sql-connection.database");
			Connection connect = DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + database, username, password);
			
			try {
				Statement state = connect.createStatement();
				ResultSet result = state.executeQuery("SELECT VALUE FROM hyperconomy_settings WHERE SETTING = 'version'");
				if (result.next()) {
					version = Double.parseDouble(result.getString("VALUE"));
					if (version == 1.0) {
						state.execute("ALTER TABLE hyperconomy_players CHANGE HASH HASH VARCHAR(255) NOT NULL DEFAULT ''");
						state.execute("ALTER TABLE hyperconomy_players ADD SALT VARCHAR(255) NOT NULL DEFAULT '' AFTER HASH");
						state.execute("DROP TABLE IF EXISTS hyperconomy_settings");
						state.execute("CREATE TABLE IF NOT EXISTS hyperconomy_settings (SETTING VARCHAR(255) NOT NULL, VALUE TEXT, TIME DATETIME NOT NULL, PRIMARY KEY (SETTING))");
						state.execute("INSERT INTO hyperconomy_settings (SETTING, VALUE, TIME) VALUES ('version', '1.1', datetime('NOW', 'localtime'))");
					} 
					if (version == 1.1) {
						//for next version
					}
				}
				result.close();
				state.close();
			} catch (Exception e) {
				connect.close();
				connect = DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + database, username, password);
				Statement state = connect.createStatement();
				state.execute("CREATE TABLE IF NOT EXISTS hyperconomy_settings (SETTING VARCHAR(255) NOT NULL, VALUE TEXT, TIME DATETIME NOT NULL, PRIMARY KEY (SETTING))");
				state.execute("DELETE FROM hyperconomy_settings");
				state.execute("INSERT INTO hyperconomy_settings (SETTING, VALUE, TIME) VALUES ('version', '1.1', NOW() )");
				updateMySQL1(connect);
				state.close();
			}
			connect.close();
			connect = DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + database, username, password);
			Statement state = connect.createStatement();
			state.execute("CREATE TABLE IF NOT EXISTS hyperconomy_objects (NAME VARCHAR(255) NOT NULL, ECONOMY VARCHAR(255) NOT NULL, TYPE TINYTEXT, CATEGORY TINYTEXT, MATERIAL TINYTEXT, ID INT, DATA INT, DURABILITY INT, VALUE DOUBLE, STATIC TINYTEXT, STATICPRICE DOUBLE, STOCK DOUBLE, MEDIAN DOUBLE, INITIATION TINYTEXT, STARTPRICE DOUBLE, CEILING DOUBLE, FLOOR DOUBLE, MAXSTOCK DOUBLE NOT NULL DEFAULT '1000000', PRIMARY KEY (NAME, ECONOMY))");
			state.execute("CREATE TABLE IF NOT EXISTS hyperconomy_players (PLAYER VARCHAR(255) NOT NULL, ECONOMY TINYTEXT, BALANCE DOUBLE NOT NULL DEFAULT '0', X DOUBLE NOT NULL DEFAULT '0', Y DOUBLE NOT NULL DEFAULT '0', Z DOUBLE NOT NULL DEFAULT '0', WORLD TINYTEXT NOT NULL, HASH VARCHAR(255) NOT NULL DEFAULT '', SALT VARCHAR(255) NOT NULL DEFAULT '', PRIMARY KEY (PLAYER))");
			state.execute("CREATE TABLE IF NOT EXISTS hyperconomy_log (ID INT NOT NULL AUTO_INCREMENT, TIME DATETIME, CUSTOMER TINYTEXT, ACTION TINYTEXT, OBJECT TINYTEXT, AMOUNT DOUBLE, MONEY DOUBLE, TAX DOUBLE, STORE TINYTEXT, TYPE TINYTEXT, PRIMARY KEY (ID))");
			state.execute("CREATE TABLE IF NOT EXISTS hyperconomy_history (ID INT NOT NULL AUTO_INCREMENT, OBJECT TINYTEXT, ECONOMY TINYTEXT, TIME DATETIME, PRICE DOUBLE, PRIMARY KEY (ID))");
			state.execute("CREATE TABLE IF NOT EXISTS hyperconomy_audit_log (ID INT NOT NULL AUTO_INCREMENT, TIME DATETIME NOT NULL, ACCOUNT TINYTEXT NOT NULL, ACTION TINYTEXT NOT NULL, AMOUNT DOUBLE NOT NULL, ECONOMY TINYTEXT NOT NULL, PRIMARY KEY (ID))");
			state.close();
			connect.close();
			return true;
		} catch (Exception e) {
			//new HyperError(e);
			return false;
		}
	}
	
	
	private void updateMySQL1(Connection connect) {
		try {
			Statement state = connect.createStatement();
			state.execute("CREATE TABLE IF NOT EXISTS hyperobjects (NAME VARCHAR(255) NOT NULL, ECONOMY VARCHAR(255) NOT NULL, TYPE TINYTEXT, CATEGORY TINYTEXT, MATERIAL TINYTEXT, ID INT, DATA INT, DURABILITY INT, VALUE DOUBLE, STATIC TINYTEXT, STATICPRICE DOUBLE, STOCK DOUBLE, MEDIAN DOUBLE, INITIATION TINYTEXT, STARTPRICE DOUBLE, CEILING DOUBLE, FLOOR DOUBLE)");
			state.execute("CREATE TABLE IF NOT EXISTS hyperplayers (PLAYER VARCHAR(255) NOT NULL, ECONOMY TINYTEXT, BALANCE DOUBLE NOT NULL DEFAULT '0')");
			state.execute("CREATE TABLE IF NOT EXISTS hyperlog (ID INT NOT NULL AUTO_INCREMENT, TIME DATETIME, CUSTOMER TINYTEXT, ACTION TINYTEXT, OBJECT TINYTEXT, AMOUNT DOUBLE, MONEY DOUBLE, TAX DOUBLE, STORE TINYTEXT, TYPE TINYTEXT, PRIMARY KEY (ID))");
			state.execute("CREATE TABLE IF NOT EXISTS hyperhistory (ID INT NOT NULL AUTO_INCREMENT, OBJECT TINYTEXT, ECONOMY TINYTEXT, TIME DATETIME, PRICE DOUBLE, COUNT INT, PRIMARY KEY (ID))");
			state.execute("CREATE TABLE IF NOT EXISTS hyperauditlog (ID INT NOT NULL AUTO_INCREMENT, TIME DATETIME NOT NULL, ACCOUNT TINYTEXT NOT NULL, ACTION TINYTEXT NOT NULL, AMOUNT DOUBLE NOT NULL, ECONOMY TINYTEXT NOT NULL, PRIMARY KEY (ID))");
			ResultSet rs = state.executeQuery("SELECT * FROM hyperhistory");
			ResultSetMetaData rsmd = rs.getMetaData();
			int numcolumns = rsmd.getColumnCount();
			if (numcolumns != 6) {
				state.execute("DROP TABLE hyperhistory");
				state.execute("CREATE TABLE hyperhistory (ID INT NOT NULL AUTO_INCREMENT, OBJECT TINYTEXT, ECONOMY TINYTEXT, TIME DATETIME, PRICE DOUBLE, COUNT INT, PRIMARY KEY (ID))");
			}
	
			rs = state.executeQuery("SELECT * FROM hyperplayers");
			rsmd = rs.getMetaData();
			numcolumns = rsmd.getColumnCount();
			if (numcolumns != 3) {
				state.execute("DROP TABLE hyperplayers");
				state.execute("CREATE TABLE hyperplayers (PLAYER TINYTEXT, ECONOMY TINYTEXT, BALANCE DOUBLE NOT NULL DEFAULT '0')");
			}	
			boolean exists = fieldExists(connect, "hyperobjects", "ceiling");
			if (!exists) {
				state.execute("ALTER TABLE hyperobjects ADD CEILING DOUBLE AFTER STARTPRICE");
				state.execute("ALTER TABLE hyperobjects ADD FLOOR DOUBLE AFTER CEILING");
			}		
			state.execute("ALTER TABLE hyperobjects CHANGE NAME NAME VARCHAR(255) NOT NULL");
			state.execute("ALTER TABLE hyperobjects CHANGE ECONOMY ECONOMY VARCHAR(255) NOT NULL");
			state.execute("ALTER TABLE hyperplayers CHANGE PLAYER PLAYER VARCHAR(255) NOT NULL");
			state.execute("ALTER TABLE hyperobjects ADD PRIMARY KEY(NAME, ECONOMY)");
			state.execute("ALTER TABLE hyperplayers ADD PRIMARY KEY(PLAYER)");
			state.execute("ALTER TABLE hyperplayers ADD X DOUBLE NOT NULL DEFAULT '0' AFTER BALANCE");
			state.execute("ALTER TABLE hyperplayers ADD Y DOUBLE NOT NULL DEFAULT '0' AFTER X");
			state.execute("ALTER TABLE hyperplayers ADD Z DOUBLE NOT NULL DEFAULT '0' AFTER Y");
			state.execute("ALTER TABLE hyperplayers ADD WORLD TINYTEXT NOT NULL AFTER Z");
			state.execute("ALTER TABLE hyperplayers ADD HASH TEXT AFTER WORLD");
			state.execute("ALTER TABLE hyperobjects ADD MAXSTOCK DOUBLE NOT NULL DEFAULT '1000000' AFTER FLOOR");
			state.execute("ALTER TABLE hyperhistory DROP COUNT");
			state.execute("ALTER TABLE hyperobjects RENAME TO hyperconomy_objects");
			state.execute("ALTER TABLE hyperplayers RENAME TO hyperconomy_players");
			state.execute("ALTER TABLE hyperlog RENAME TO hyperconomy_log");
			state.execute("ALTER TABLE hyperhistory RENAME TO hyperconomy_history");
			state.execute("ALTER TABLE hyperauditlog RENAME TO hyperconomy_audit_log");
			state.execute("ALTER TABLE hyperconomy_players CHANGE HASH HASH VARCHAR(255) NOT NULL DEFAULT ''");
			state.execute("ALTER TABLE hyperconomy_players ADD SALT VARCHAR(255) NOT NULL DEFAULT '' AFTER HASH");
			
			state.close();
			connect.close();
		} catch (SQLException e) {
			//do nothing and continue
		}
	}
	
	
	public boolean fieldExists(Connection connect, String table, String field) {
		
		try {
			Statement state = connect.createStatement();
			ResultSet result = state.executeQuery("SELECT * FROM " + table);
			ResultSetMetaData meta = result.getMetaData();
			int nCols = meta.getColumnCount();
			for (int i = 1; i < nCols + 1; i++) {
			    if (meta.getColumnName(i).equalsIgnoreCase(field)) {
			        result.close();
			    	return true;
			    }
			}
	        result.close();
	        state.close();
			return false;
		} catch (Exception e) {
			new HyperError(e);
			return false;
		}

	}
	
	
}
