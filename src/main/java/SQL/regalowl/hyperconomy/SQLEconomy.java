package regalowl.hyperconomy;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

import org.bukkit.configuration.file.FileConfiguration;


public class SQLEconomy {
	private String username;
	private String password;
	private int port;
	private String host;
	private String database;
	private HyperConomy hc;
	SQLEconomy() {
		hc = HyperConomy.hc;
		FileConfiguration config = hc.getYaml().getConfig();
		username = config.getString("config.sql-connection.username");
		password = config.getString("config.sql-connection.password");
		port = config.getInt("config.sql-connection.port");
		host = config.getString("config.sql-connection.host");
		database = config.getString("config.sql-connection.database");
	}
	
	
	private Connection getConnection() {
		try {
			if (hc.useMySQL()) {
				return DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + database, username, password);
			} else {
				FileTools ft = new FileTools();
				String path = ft.getJarPath() + File.separator + "plugins" + File.separator + "HyperConomy" + File.separator + "HyperConomy.db";
				return DriverManager.getConnection("jdbc:sqlite:" + path);
			}
		} catch (Exception e) {
			return null;
		}
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
	
	
	public boolean checkData() {
		boolean migrate = false;
		ArrayList<String> testdata = new ArrayList<String>();
		testdata = getStringColumn("SELECT NAME FROM hyperconomy_objects WHERE ECONOMY='default'");
		if (testdata.size() == 0) {
			migrate = true;
			new Backup();
			migrate("default");
		}
		return migrate;
	}
	
	
	/**
	 * 
	 */
	public void migrate(String economy) {
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
				statements.add("Insert Into hyperconomy_objects (NAME, ECONOMY, TYPE, CATEGORY, MATERIAL, ID, DATA, DURABILITY, VALUE, STATIC, STATICPRICE, STOCK, MEDIAN, INITIATION, STARTPRICE, CEILING, FLOOR, MAXSTOCK)" + " Values ('" + itemname + "','" + economy + "','" + "item" + "','" + category + "','" + itemsyaml.getString(itemname + ".information.material") + "','" + itemsyaml.getInt(itemname + ".information.id") + "','" + itemsyaml.getInt(itemname + ".information.data") + "','"
						+ itemsyaml.getInt(itemname + ".information.data") + "','" + itemsyaml.getDouble(itemname + ".value") + "','" + itemsyaml.getString(itemname + ".price.static") + "','" + itemsyaml.getDouble(itemname + ".price.staticprice") + "','" + itemsyaml.getDouble(itemname + ".stock.stock") + "','" + itemsyaml.getDouble(itemname + ".stock.median") + "','" + itemsyaml.getString(itemname + ".initiation.initiation") + "','" + itemsyaml.getDouble(itemname + ".initiation.startprice")
						+ "','" + itemsyaml.getDouble(itemname + ".price.ceiling") + "','" + itemsyaml.getDouble(itemname + ".price.floor") + "','" + itemsyaml.getDouble(itemname + ".stock.maxstock") + "')");
			} else {
				statements.add("Insert Into hyperconomy_objects (NAME, ECONOMY, TYPE, CATEGORY, MATERIAL, ID, DATA, DURABILITY, VALUE, STATIC, STATICPRICE, STOCK, MEDIAN, INITIATION, STARTPRICE, CEILING, FLOOR, MAXSTOCK)" + " Values ('" + itemname + "','" + economy + "','" + "experience" + "','" + category + "','" + "none" + "','" + itemsyaml.getInt(itemname + ".information.id") + "','" + itemsyaml.getInt(itemname + ".information.data") + "','" + itemsyaml.getInt(itemname + ".information.data") + "','"
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
			statements.add("Insert Into hyperconomy_objects (NAME, ECONOMY, TYPE, CATEGORY, MATERIAL, ID, DATA, DURABILITY, VALUE, STATIC, STATICPRICE, STOCK, MEDIAN, INITIATION, STARTPRICE, CEILING, FLOOR, MAXSTOCK)" + " Values ('" + ename + "','" + economy + "','" + "enchantment" + "','" + category + "','" + enchantsyaml.getString(ename + ".information.name") + "','" + enchantsyaml.getInt(ename + ".information.id") + "','" + "-2" + "','" + "-2" + "','" + enchantsyaml.getDouble(ename + ".value") + "','"
					+ enchantsyaml.getString(ename + ".price.static") + "','" + enchantsyaml.getDouble(ename + ".price.staticprice") + "','" + enchantsyaml.getDouble(ename + ".stock.stock") + "','" + enchantsyaml.getDouble(ename + ".stock.median") + "','" + enchantsyaml.getString(ename + ".initiation.initiation") + "','" + enchantsyaml.getDouble(ename + ".initiation.startprice") + "','" + enchantsyaml.getDouble(ename + ".price.ceiling") + "','" + enchantsyaml.getDouble(ename + ".price.floor") + "','" + enchantsyaml.getDouble(ename + ".stock.maxstock") + "')");
		}
		SQLWrite sw = hc.getSQLWrite();
		sw.executeSQL(statements);
		hc.getDataFunctions().load();
	}
	
	
	
	
	
	public void createNewEconomy(String economy) {
		DataHandler sf = hc.getDataFunctions();
		ArrayList<String> items = sf.getItemNames();
		ArrayList<String> enchants = sf.getEnchantNames();
		ArrayList<String> statements = new ArrayList<String>();
		for (int i = 0; i < items.size(); i++) {
			String type = "item";
			if (items.get(i).equalsIgnoreCase("xp")) {
				type = "experience";
			}
			String c = items.get(i);
			HyperObject ho = sf.getHyperObject(c, "default");
			statements.add("Insert Into hyperconomy_objects (NAME, ECONOMY, TYPE, CATEGORY, MATERIAL, ID, DATA, DURABILITY, VALUE, STATIC, STATICPRICE, STOCK, MEDIAN, INITIATION, STARTPRICE, CEILING, FLOOR, MAXSTOCK)" + " Values ('" + c + "','" + economy + "','" + type + "','" + ho.getCategory() + "','" + ho.getMaterial() + "','" + ho.getId() + "','" + ho.getData() + "','" + ho.getDurability() + "','" + ho.getValue() + "','"
					+ ho.getIsstatic() + "','" + ho.getStaticprice() + "','" + 0.0 + "','" + ho.getMedian() + "','" + "true" + "','" + ho.getStartprice() + "','" + ho.getCeiling() + "','" + ho.getFloor() + "','" + ho.getMaxstock() + "')");
		}
		for (int i = 0; i < enchants.size(); i++) {
			String type = "enchantment";
			String c = enchants.get(i);
			HyperObject ho = sf.getHyperObject(c, "default");
			statements.add("Insert Into hyperconomy_objects (NAME, ECONOMY, TYPE, CATEGORY, MATERIAL, ID, DATA, DURABILITY, VALUE, STATIC, STATICPRICE, STOCK, MEDIAN, INITIATION, STARTPRICE, CEILING, FLOOR, MAXSTOCK)" + " Values ('" + c + "','" + economy + "','" + type + "','" + ho.getCategory() + "','" + ho.getMaterial() + "','" + ho.getId() + "','" + ho.getData() + "','" + ho.getDurability() + "','" + ho.getValue() + "','"
					+ ho.getIsstatic() + "','" + ho.getStaticprice() + "','" + 0.0 + "','" + ho.getMedian() + "','" + "true" + "','" + ho.getStartprice() + "','" + ho.getCeiling() + "','" + ho.getFloor() + "','" + ho.getMaxstock() + "')");
		}
		SQLWrite sw = hc.getSQLWrite();
		sw.executeSQL(statements);
		hc.getDataFunctions().load();
		sf.load();
	}
	public void deleteEconomy(String economy) {
		hc.getSQLWrite().executeSQL("DELETE FROM hyperconomy_objects WHERE ECONOMY='" + economy + "'");
	}
	public ArrayList<String> loadNewItems(String economy) {
		FileConfiguration itemsyaml = hc.getYaml().getItems();
		FileConfiguration enchantsyaml = hc.getYaml().getEnchants();
		ArrayList<String> statements = new ArrayList<String>();
		ArrayList<String> objectsAdded = new ArrayList<String>();
		Iterator<String> it = itemsyaml.getKeys(false).iterator();
		DataHandler sf = hc.getDataFunctions();
		ArrayList<String> keys = sf.getObjectKeys();
		while (it.hasNext()) {
			String itemname = it.next().toString();
			String key = itemname + ":" + economy;
			if (!keys.contains(key)) {
				objectsAdded.add(itemname);
				if (!itemname.equalsIgnoreCase("xp")) {
					statements.add("Insert Into hyperconomy_objects (NAME, ECONOMY, TYPE, CATEGORY, MATERIAL, ID, DATA, DURABILITY, VALUE, STATIC, STATICPRICE, STOCK, MEDIAN, INITIATION, STARTPRICE, CEILING, FLOOR, MAXSTOCK)" + " Values ('" + itemname + "','" + economy + "','" + "item" + "','" + "unknown" + "','" + itemsyaml.getString(itemname + ".information.material") + "','" + itemsyaml.getInt(itemname + ".information.id") + "','" + itemsyaml.getInt(itemname + ".information.data") + "','"
							+ itemsyaml.getInt(itemname + ".information.data") + "','" + itemsyaml.getDouble(itemname + ".value") + "','" + itemsyaml.getString(itemname + ".price.static") + "','" + itemsyaml.getDouble(itemname + ".price.staticprice") + "','" + itemsyaml.getDouble(itemname + ".stock.stock") + "','" + itemsyaml.getDouble(itemname + ".stock.median") + "','" + itemsyaml.getString(itemname + ".initiation.initiation") + "','" + itemsyaml.getDouble(itemname + ".initiation.startprice")
							+ "','"+ itemsyaml.getDouble(itemname + ".price.ceiling") + "','" + itemsyaml.getDouble(itemname + ".price.floor") + "','" + itemsyaml.getDouble(itemname + ".stock.maxstock") + "')");
				} else {
					statements.add("Insert Into hyperconomy_objects (NAME, ECONOMY, TYPE, CATEGORY, MATERIAL, ID, DATA, DURABILITY, VALUE, STATIC, STATICPRICE, STOCK, MEDIAN, INITIATION, STARTPRICE, CEILING, FLOOR, MAXSTOCK)" + " Values ('" + itemname + "','" + economy + "','" + "experience" + "','" + "unknown" + "','" + "none" + "','" + itemsyaml.getInt(itemname + ".information.id") + "','" + itemsyaml.getInt(itemname + ".information.data") + "','" + itemsyaml.getInt(itemname + ".information.data") + "','"
							+ itemsyaml.getDouble(itemname + ".value") + "','" + itemsyaml.getString(itemname + ".price.static") + "','" + itemsyaml.getDouble(itemname + ".price.staticprice") + "','" + itemsyaml.getDouble(itemname + ".stock.stock") + "','" + itemsyaml.getDouble(itemname + ".stock.median") + "','" + itemsyaml.getString(itemname + ".initiation.initiation") + "','" + itemsyaml.getDouble(itemname + ".initiation.startprice") + "','" + itemsyaml.getDouble(itemname + ".price.ceiling") + "','"
							+ itemsyaml.getDouble(itemname + ".price.floor") + "','" + itemsyaml.getDouble(itemname + ".stock.maxstock") + "')");
				}
			}
		}
		Iterator<String> it2 = enchantsyaml.getKeys(false).iterator();
		while (it2.hasNext()) {
			String ename = it2.next().toString();
			String key = ename + ":" + economy;
			if (!keys.contains(key)) {
				objectsAdded.add(ename);
				statements.add("Insert Into hyperconomy_objects (NAME, ECONOMY, TYPE, CATEGORY, MATERIAL, ID, DATA, DURABILITY, VALUE, STATIC, STATICPRICE, STOCK, MEDIAN, INITIATION, STARTPRICE, CEILING, FLOOR)" + " Values ('" + ename + "','" + economy + "','" + "enchantment" + "','" + "unknown" + "','" + enchantsyaml.getString(ename + ".information.name") + "','" + enchantsyaml.getInt(ename + ".information.id") + "','" + "-2" + "','" + "-2" + "','" + enchantsyaml.getDouble(ename + ".value") + "','"
						+ enchantsyaml.getString(ename + ".price.static") + "','" + enchantsyaml.getDouble(ename + ".price.staticprice") + "','" + enchantsyaml.getDouble(ename + ".stock.stock") + "','" + enchantsyaml.getDouble(ename + ".stock.median") + "','" + enchantsyaml.getString(ename + ".initiation.initiation") + "','" + enchantsyaml.getDouble(ename + ".initiation.startprice") + "','" + enchantsyaml.getDouble(ename + ".price.ceiling") + "','" + enchantsyaml.getDouble(ename + ".price.floor")
						+ "','" + enchantsyaml.getDouble(ename + ".stock.maxstock") + "')");
			}
		}
		SQLWrite sw = hc.getSQLWrite();
		sw.executeSQL(statements);
		hc.getDataFunctions().load();
		return objectsAdded;
	}
	public void exportToYml(String economy) {
		FileConfiguration items = hc.getYaml().getItems();
		FileConfiguration enchants = hc.getYaml().getEnchants();
		DataHandler sf = hc.getDataFunctions();
		ArrayList<String> names = new ArrayList<String>();
		Iterator<String> it = items.getKeys(false).iterator();
		while (it.hasNext()) {
			String cname = it.next().toString();
			names.add(cname);
		}
		Iterator<String> it2 = enchants.getKeys(false).iterator();
		while (it2.hasNext()) {
			String cname = it2.next().toString();
			names.add(cname);
		}
		Collections.sort(names, String.CASE_INSENSITIVE_ORDER);
		for (int i = 0; i < names.size(); i++) {
			String name = names.get(i);
			items.set(name, null);
			enchants.set(name, null);
			HyperObject ho = sf.getHyperObject(name, economy);
			String newtype = HyperObjectType.getString(ho.getType());
			String newcategory = ho.getCategory();
			String newmaterial = ho.getMaterial();
			int newid = ho.getId();
			int newdata = ho.getData();
			int newdurability = ho.getDurability();
			double newvalue = ho.getValue();
			String newstatic = ho.getIsstatic();
			double newstaticprice = ho.getStaticprice();
			double newstock = ho.getStock();
			double newmedian = ho.getMedian();
			String newinitiation = ho.getInitiation();
			double newstartprice = ho.getStartprice();
			double newceiling = ho.getCeiling();
			double newfloor = ho.getFloor();
			double newmaxstock = ho.getMaxstock();
			if (sf.itemTest(name)) {
				items.set(name + ".information.type", newtype);
			} else if (sf.enchantTest(name)) {
				enchants.set(name + ".information.type", newtype);
			}
			if (sf.itemTest(name)) {
				items.set(name + ".information.category", newcategory);
			} else if (sf.enchantTest(name)) {
				enchants.set(name + ".information.category", newcategory);
			}
			if (sf.itemTest(name)) {
				items.set(name + ".information.material", newmaterial);
			} else if (sf.enchantTest(name)) {
				enchants.set(name + ".information.name", newmaterial);
			}
			if (sf.itemTest(name)) {
				items.set(name + ".information.id", newid);
			} else if (sf.enchantTest(name)) {
				enchants.set(name + ".information.id", newid);
			}
			if (sf.itemTest(name)) {
				items.set(name + ".information.data", newdata);
			}
			if (sf.itemTest(name)) {
				items.set(name + ".information.data", newdurability);
			}
			if (sf.itemTest(name)) {
				items.set(name + ".value", newvalue);
			} else if (sf.enchantTest(name)) {
				enchants.set(name + ".value", newvalue);
			}
			if (sf.itemTest(name)) {
				items.set(name + ".price.static", Boolean.parseBoolean(newstatic));
			} else if (sf.enchantTest(name)) {
				enchants.set(name + ".price.static", Boolean.parseBoolean(newstatic));
			}
			if (sf.itemTest(name)) {
				items.set(name + ".price.staticprice", newstaticprice);
			} else if (sf.enchantTest(name)) {
				enchants.set(name + ".price.staticprice", newstaticprice);
			}
			if (sf.itemTest(name)) {
				items.set(name + ".stock.stock", newstock);
			} else if (sf.enchantTest(name)) {
				enchants.set(name + ".stock.stock", newstock);
			}
			if (sf.itemTest(name)) {
				items.set(name + ".stock.median", newmedian);
			} else if (sf.enchantTest(name)) {
				enchants.set(name + ".stock.median", newmedian);
			}
			if (sf.itemTest(name)) {
				items.set(name + ".initiation.initiation", Boolean.parseBoolean(newinitiation));
			} else if (sf.enchantTest(name)) {
				enchants.set(name + ".initiation.initiation", Boolean.parseBoolean(newinitiation));
			}
			if (sf.itemTest(name)) {
				items.set(name + ".initiation.startprice", newstartprice);
			} else if (sf.enchantTest(name)) {
				enchants.set(name + ".initiation.startprice", newstartprice);
			}
			if (sf.itemTest(name)) {
				items.set(name + ".price.ceiling", newceiling);
			} else if (sf.enchantTest(name)) {
				enchants.set(name + ".price.ceiling", newceiling);
			}
			if (sf.itemTest(name)) {
				items.set(name + ".price.floor", newfloor);
			} else if (sf.enchantTest(name)) {
				enchants.set(name + ".price.floor", newfloor);
			}
			if (sf.itemTest(name)) {
				items.set(name + ".stock.maxstock", newmaxstock);
			} else if (sf.enchantTest(name)) {
				enchants.set(name + ".stock.maxstock", newmaxstock);
			}
		}
		hc.getYaml().saveYamls();
	}
	
	
	private ArrayList<String> getStringColumn(String statement) {
		ArrayList<String> data = new ArrayList<String>();
		try {
			Connection connect = getConnection();
			Statement state = connect.createStatement();
			ResultSet result = state.executeQuery(statement);
			while (result.next()) {
				data.add(result.getString(1));
			}
			result.close();
			return data;
		} catch (SQLException e) {
			return data;
		}
	}
	
}
