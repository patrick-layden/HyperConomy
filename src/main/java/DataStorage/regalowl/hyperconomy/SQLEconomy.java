package regalowl.hyperconomy;

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
	SQLEconomy(HyperConomy hyc) {
		hc = hyc;
		FileConfiguration config = hc.getYaml().getConfig();
		username = config.getString("config.sql-connection.username");
		password = config.getString("config.sql-connection.password");
		port = config.getInt("config.sql-connection.port");
		host = config.getString("config.sql-connection.host");
		database = config.getString("config.sql-connection.database");
	}
	public boolean checkTables() {
		try {
			Connection connect = DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + database, username, password);
			Statement state = connect.createStatement();
			state.execute("CREATE TABLE IF NOT EXISTS hyperobjects (NAME TINYTEXT, ECONOMY TINYTEXT, TYPE TINYTEXT, CATEGORY TINYTEXT, MATERIAL TINYTEXT, ID INT, DATA INT, " + "DURABILITY INT, VALUE DOUBLE, STATIC TINYTEXT, STATICPRICE DOUBLE, STOCK DOUBLE, MEDIAN DOUBLE, INITIATION TINYTEXT, STARTPRICE DOUBLE, CEILING DOUBLE, FLOOR DOUBLE)");
			state.execute("CREATE TABLE IF NOT EXISTS hyperplayers (PLAYER TINYTEXT, ECONOMY TINYTEXT)");
			state.execute("CREATE TABLE IF NOT EXISTS hyperlog (ID INT NOT NULL AUTO_INCREMENT, TIME DATETIME, CUSTOMER TINYTEXT, ACTION TINYTEXT, OBJECT TINYTEXT, AMOUNT DOUBLE, MONEY DOUBLE, TAX DOUBLE, STORE TINYTEXT, TYPE TINYTEXT, PRIMARY KEY (ID))");
			state.execute("CREATE TABLE IF NOT EXISTS hyperhistory (ID INT NOT NULL AUTO_INCREMENT, OBJECT TINYTEXT, ECONOMY TINYTEXT, TIME DATETIME, PRICE DOUBLE, COUNT INT, PRIMARY KEY (ID))");
			ResultSet rs = state.executeQuery("SELECT * FROM hyperhistory");
			ResultSetMetaData rsmd = rs.getMetaData();
			int numcolumns = rsmd.getColumnCount();
			if (numcolumns != 6) {
				state.execute("DROP TABLE hyperhistory");
			}
			state.execute("CREATE TABLE IF NOT EXISTS hyperhistory (ID INT NOT NULL AUTO_INCREMENT, OBJECT TINYTEXT, ECONOMY TINYTEXT, TIME DATETIME, PRICE DOUBLE, COUNT INT, PRIMARY KEY (ID))");
			state.close();
			connect.close();		
    		SQLUtils su = new SQLUtils();
    		boolean exists = su.fieldExists(host, port, database, username, password, "hyperobjects", "ceiling");
    		if (!exists) {
    			String statement = "ALTER TABLE hyperobjects ADD CEILING DOUBLE AFTER STARTPRICE";
    			su.executeSQL(host, port, database, username, password, statement);
    			statement = "ALTER TABLE hyperobjects ADD FLOOR DOUBLE AFTER CEILING";
    			su.executeSQL(host, port, database, username, password, statement);
    		}			
			return true;
		} catch (SQLException e) {
			return false;
		}
	}
	public void deleteTables() {
		try {
			Connection connect = DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + database, username, password);
			Statement state = connect.createStatement();
			state.execute("DROP TABLE IF EXISTS hyperobjects");
			state.execute("DROP TABLE IF EXISTS hyperhistory");
			state.execute("DROP TABLE IF EXISTS hyperplayers");
			state.execute("DROP TABLE IF EXISTS hyperlog");
			state.close();
			connect.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	public boolean checkData() {
		boolean migrate = false;
		ArrayList<String> testdata = new ArrayList<String>();
		testdata = hc.getSQLFunctions().getStringColumn("SELECT NAME FROM hyperobjects WHERE ECONOMY='default'");
		if (testdata.size() == 0) {
			migrate = true;
			new Backup();
			migrate();
		}
		return migrate;
	}
	public void migrate() {
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
				statements.add("Insert Into hyperobjects (NAME, ECONOMY, TYPE, CATEGORY, MATERIAL, ID, DATA, DURABILITY, VALUE, STATIC, STATICPRICE, STOCK, MEDIAN, INITIATION, STARTPRICE, CEILING, FLOOR)" + " Values ('" + itemname + "','" + "default" + "','" + "item" + "','" + category + "','" + itemsyaml.getString(itemname + ".information.material") + "','" + itemsyaml.getInt(itemname + ".information.id") + "','" + itemsyaml.getInt(itemname + ".information.data") + "','"
						+ itemsyaml.getInt(itemname + ".information.data") + "','" + itemsyaml.getDouble(itemname + ".value") + "','" + itemsyaml.getString(itemname + ".price.static") + "','" + itemsyaml.getDouble(itemname + ".price.staticprice") + "','" + itemsyaml.getDouble(itemname + ".stock.stock") + "','" + itemsyaml.getDouble(itemname + ".stock.median") + "','" + itemsyaml.getString(itemname + ".initiation.initiation") + "','" + itemsyaml.getDouble(itemname + ".initiation.startprice")
						+ "','" + itemsyaml.getDouble(itemname + ".price.ceiling") + "','" + itemsyaml.getDouble(itemname + ".price.floor") + "')");
			} else {
				statements.add("Insert Into hyperobjects (NAME, ECONOMY, TYPE, CATEGORY, MATERIAL, ID, DATA, DURABILITY, VALUE, STATIC, STATICPRICE, STOCK, MEDIAN, INITIATION, STARTPRICE, CEILING, FLOOR)" + " Values ('" + itemname + "','" + "default" + "','" + "experience" + "','" + category + "','" + "none" + "','" + itemsyaml.getInt(itemname + ".information.id") + "','" + itemsyaml.getInt(itemname + ".information.data") + "','" + itemsyaml.getInt(itemname + ".information.data") + "','"
						+ itemsyaml.getDouble(itemname + ".value") + "','" + itemsyaml.getString(itemname + ".price.static") + "','" + itemsyaml.getDouble(itemname + ".price.staticprice") + "','" + itemsyaml.getDouble(itemname + ".stock.stock") + "','" + itemsyaml.getDouble(itemname + ".stock.median") + "','" + itemsyaml.getString(itemname + ".initiation.initiation") + "','" + itemsyaml.getDouble(itemname + ".initiation.startprice") + "','" + itemsyaml.getDouble(itemname + ".price.ceiling") + "','"
						+ itemsyaml.getDouble(itemname + ".price.floor") + "')");
			}
		}
		Iterator<String> it2 = enchantsyaml.getKeys(false).iterator();
		while (it2.hasNext()) {
			String ename = it2.next().toString();
			String category = enchantsyaml.getString(ename + ".information.category");
			if (category == null) {
				category = "unknown";
			}
			statements.add("Insert Into hyperobjects (NAME, ECONOMY, TYPE, CATEGORY, MATERIAL, ID, DATA, DURABILITY, VALUE, STATIC, STATICPRICE, STOCK, MEDIAN, INITIATION, STARTPRICE, CEILING, FLOOR)" + " Values ('" + ename + "','" + "default" + "','" + "enchantment" + "','" + category + "','" + enchantsyaml.getString(ename + ".information.name") + "','" + enchantsyaml.getInt(ename + ".information.id") + "','" + "-1" + "','" + "-1" + "','" + enchantsyaml.getDouble(ename + ".value") + "','"
					+ enchantsyaml.getString(ename + ".price.static") + "','" + enchantsyaml.getDouble(ename + ".price.staticprice") + "','" + enchantsyaml.getDouble(ename + ".stock.stock") + "','" + enchantsyaml.getDouble(ename + ".stock.median") + "','" + enchantsyaml.getString(ename + ".initiation.initiation") + "','" + enchantsyaml.getDouble(ename + ".initiation.startprice") + "','" + enchantsyaml.getDouble(ename + ".price.ceiling") + "','" + enchantsyaml.getDouble(ename + ".price.floor") + "')");
		}
		SQLWrite sw = hc.getSQLWrite();
		sw.writeData(statements);
		hc.getSQLFunctions().load();
	}
	public void createNewEconomy(String economy) {
		SQLFunctions sf = hc.getSQLFunctions();
		ArrayList<String> items = hc.getInames();
		ArrayList<String> enchants = hc.getEnames();
		ArrayList<String> statements = new ArrayList<String>();
		for (int i = 0; i < items.size(); i++) {
			String type = "item";
			if (items.get(i).equalsIgnoreCase("xp")) {
				type = "experience";
			}
			String c = items.get(i);
			statements.add("Insert Into hyperobjects (NAME, ECONOMY, TYPE, CATEGORY, MATERIAL, ID, DATA, DURABILITY, VALUE, STATIC, STATICPRICE, STOCK, MEDIAN, INITIATION, STARTPRICE, CEILING, FLOOR)" + " Values ('" + c + "','" + economy + "','" + type + "','" + sf.getCategory(c, "default") + "','" + sf.getMaterial(c, "default") + "','" + sf.getId(c, "default") + "','" + sf.getData(c, "default") + "','" + sf.getDurability(c, "default") + "','" + sf.getValue(c, "default") + "','"
					+ sf.getStatic(c, "default") + "','" + sf.getStaticPrice(c, "default") + "','" + 0.0 + "','" + sf.getMedian(c, "default") + "','" + "true" + "','" + sf.getStartPrice(c, "default") + "','" + sf.getCeiling(c, "default") + "','" + sf.getFloor(c, "default") + "')");
		}
		for (int i = 0; i < enchants.size(); i++) {
			String type = "enchantment";
			String c = enchants.get(i);
			statements.add("Insert Into hyperobjects (NAME, ECONOMY, TYPE, CATEGORY, MATERIAL, ID, DATA, DURABILITY, VALUE, STATIC, STATICPRICE, STOCK, MEDIAN, INITIATION, STARTPRICE, CEILING, FLOOR)" + " Values ('" + c + "','" + economy + "','" + type + "','" + sf.getCategory(c, "default") + "','" + sf.getMaterial(c, "default") + "','" + sf.getId(c, "default") + "','" + sf.getData(c, "default") + "','" + sf.getDurability(c, "default") + "','" + sf.getValue(c, "default") + "','"
					+ sf.getStatic(c, "default") + "','" + sf.getStaticPrice(c, "default") + "','" + 0.0 + "','" + sf.getMedian(c, "default") + "','" + "true" + "','" + sf.getStartPrice(c, "default") + "','" + sf.getCeiling(c, "default") + "','" + sf.getFloor(c, "default") + "')");
		}
		SQLWrite sw = hc.getSQLWrite();
		sw.writeData(statements);
		hc.getServer().getScheduler().scheduleSyncDelayedTask(hc, new Runnable() {
			public void run() {
				SQLFunctions sf = hc.getSQLFunctions();
				sf.load();
			}
		}, 100L);
	}
	public void deleteEconomy(String economy) {
		hc.getSQLWrite().writeData("DELETE FROM hyperobjects WHERE ECONOMY='" + economy + "'");
	}
	public ArrayList<String> loadItems(String economy) {
		FileConfiguration itemsyaml = hc.getYaml().getItems();
		FileConfiguration enchantsyaml = hc.getYaml().getEnchants();
		ArrayList<String> statements = new ArrayList<String>();
		ArrayList<String> objectsAdded = new ArrayList<String>();
		Iterator<String> it = itemsyaml.getKeys(false).iterator();
		SQLFunctions sf = hc.getSQLFunctions();
		ArrayList<String> keys = sf.getKeys();
		while (it.hasNext()) {
			String itemname = it.next().toString();
			String key = itemname + ":" + economy;
			if (!keys.contains(key)) {
				objectsAdded.add(itemname);
				if (!itemname.equalsIgnoreCase("xp")) {
					statements.add("Insert Into hyperobjects (NAME, ECONOMY, TYPE, CATEGORY, MATERIAL, ID, DATA, DURABILITY, VALUE, STATIC, STATICPRICE, STOCK, MEDIAN, INITIATION, STARTPRICE, CEILING, FLOOR)" + " Values ('" + itemname + "','" + economy + "','" + "item" + "','" + "unknown" + "','" + itemsyaml.getString(itemname + ".information.material") + "','" + itemsyaml.getInt(itemname + ".information.id") + "','" + itemsyaml.getInt(itemname + ".information.data") + "','"
							+ itemsyaml.getInt(itemname + ".information.data") + "','" + itemsyaml.getDouble(itemname + ".value") + "','" + itemsyaml.getString(itemname + ".price.static") + "','" + itemsyaml.getDouble(itemname + ".price.staticprice") + "','" + itemsyaml.getDouble(itemname + ".stock.stock") + "','" + itemsyaml.getDouble(itemname + ".stock.median") + "','" + itemsyaml.getString(itemname + ".initiation.initiation") + "','" + itemsyaml.getDouble(itemname + ".initiation.startprice")
							+ "','"+ itemsyaml.getDouble(itemname + ".price.ceiling") + "','" + itemsyaml.getDouble(itemname + ".price.floor") + "')");
				} else {
					statements.add("Insert Into hyperobjects (NAME, ECONOMY, TYPE, CATEGORY, MATERIAL, ID, DATA, DURABILITY, VALUE, STATIC, STATICPRICE, STOCK, MEDIAN, INITIATION, STARTPRICE, CEILING, FLOOR)" + " Values ('" + itemname + "','" + economy + "','" + "experience" + "','" + "unknown" + "','" + "none" + "','" + itemsyaml.getInt(itemname + ".information.id") + "','" + itemsyaml.getInt(itemname + ".information.data") + "','" + itemsyaml.getInt(itemname + ".information.data") + "','"
							+ itemsyaml.getDouble(itemname + ".value") + "','" + itemsyaml.getString(itemname + ".price.static") + "','" + itemsyaml.getDouble(itemname + ".price.staticprice") + "','" + itemsyaml.getDouble(itemname + ".stock.stock") + "','" + itemsyaml.getDouble(itemname + ".stock.median") + "','" + itemsyaml.getString(itemname + ".initiation.initiation") + "','" + itemsyaml.getDouble(itemname + ".initiation.startprice") + "','" + itemsyaml.getDouble(itemname + ".price.ceiling") + "','"
							+ itemsyaml.getDouble(itemname + ".price.floor") + "')");
				}
			}
		}
		Iterator<String> it2 = enchantsyaml.getKeys(false).iterator();
		while (it2.hasNext()) {
			String ename = it2.next().toString();
			String key = ename + ":" + economy;
			if (!keys.contains(key)) {
				objectsAdded.add(ename);
				statements.add("Insert Into hyperobjects (NAME, ECONOMY, TYPE, CATEGORY, MATERIAL, ID, DATA, DURABILITY, VALUE, STATIC, STATICPRICE, STOCK, MEDIAN, INITIATION, STARTPRICE, CEILING, FLOOR)" + " Values ('" + ename + "','" + economy + "','" + "enchantment" + "','" + "unknown" + "','" + enchantsyaml.getString(ename + ".information.name") + "','" + enchantsyaml.getInt(ename + ".information.id") + "','" + "-1" + "','" + "-1" + "','" + enchantsyaml.getDouble(ename + ".value") + "','"
						+ enchantsyaml.getString(ename + ".price.static") + "','" + enchantsyaml.getDouble(ename + ".price.staticprice") + "','" + enchantsyaml.getDouble(ename + ".stock.stock") + "','" + enchantsyaml.getDouble(ename + ".stock.median") + "','" + enchantsyaml.getString(ename + ".initiation.initiation") + "','" + enchantsyaml.getDouble(ename + ".initiation.startprice") + "','" + enchantsyaml.getDouble(ename + ".price.ceiling") + "','" + enchantsyaml.getDouble(ename + ".price.floor")
						+ "')");
			}
		}
		SQLWrite sw = hc.getSQLWrite();
		sw.writeData(statements);
		hc.getSQLFunctions().load();
		return objectsAdded;
	}
	public void exportToYml(String economy) {
		FileConfiguration items = hc.getYaml().getItems();
		FileConfiguration enchants = hc.getYaml().getEnchants();
		SQLFunctions sf = hc.getSQLFunctions();
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
			String newtype = sf.getType(name, economy);
			String newcategory = sf.getCategory(name, economy);
			String newmaterial = sf.getMaterial(name, economy);
			int newid = sf.getId(name, economy);
			int newdata = sf.getData(name, economy);
			int newdurability = sf.getDurability(name, economy);
			double newvalue = sf.getValue(name, economy);
			String newstatic = sf.getStatic(name, economy);
			double newstaticprice = sf.getStaticPrice(name, economy);
			double newstock = sf.getStock(name, economy);
			double newmedian = sf.getMedian(name, economy);
			String newinitiation = sf.getInitiation(name, economy);
			double newstartprice = sf.getStartPrice(name, economy);
			double newceiling = sf.getCeiling(name, economy);
			double newfloor = sf.getFloor(name, economy);
			if (hc.itemTest(name)) {
				items.set(name + ".information.type", newtype);
			} else if (hc.enchantTest(name)) {
				enchants.set(name + ".information.type", newtype);
			}
			if (hc.itemTest(name)) {
				items.set(name + ".information.category", newcategory);
			} else if (hc.enchantTest(name)) {
				enchants.set(name + ".information.category", newcategory);
			}
			if (hc.itemTest(name)) {
				items.set(name + ".information.material", newmaterial);
			} else if (hc.enchantTest(name)) {
				enchants.set(name + ".information.name", newmaterial);
			}
			if (hc.itemTest(name)) {
				items.set(name + ".information.id", newid);
			} else if (hc.enchantTest(name)) {
				enchants.set(name + ".information.id", newid);
			}
			if (hc.itemTest(name)) {
				items.set(name + ".information.data", newdata);
			}
			if (hc.itemTest(name)) {
				items.set(name + ".information.data", newdurability);
			}
			if (hc.itemTest(name)) {
				items.set(name + ".value", newvalue);
			} else if (hc.enchantTest(name)) {
				enchants.set(name + ".value", newvalue);
			}
			if (hc.itemTest(name)) {
				items.set(name + ".price.static", Boolean.parseBoolean(newstatic));
			} else if (hc.enchantTest(name)) {
				enchants.set(name + ".price.static", Boolean.parseBoolean(newstatic));
			}
			if (hc.itemTest(name)) {
				items.set(name + ".price.staticprice", newstaticprice);
			} else if (hc.enchantTest(name)) {
				enchants.set(name + ".price.staticprice", newstaticprice);
			}
			if (hc.itemTest(name)) {
				items.set(name + ".stock.stock", newstock);
			} else if (hc.enchantTest(name)) {
				enchants.set(name + ".stock.stock", newstock);
			}
			if (hc.itemTest(name)) {
				items.set(name + ".stock.median", newmedian);
			} else if (hc.enchantTest(name)) {
				enchants.set(name + ".stock.median", newmedian);
			}
			if (hc.itemTest(name)) {
				items.set(name + ".initiation.initiation", Boolean.parseBoolean(newinitiation));
			} else if (hc.enchantTest(name)) {
				enchants.set(name + ".initiation.initiation", Boolean.parseBoolean(newinitiation));
			}
			if (hc.itemTest(name)) {
				items.set(name + ".initiation.startprice", newstartprice);
			} else if (hc.enchantTest(name)) {
				enchants.set(name + ".initiation.startprice", newstartprice);
			}
			if (hc.itemTest(name)) {
				items.set(name + ".price.ceiling", newceiling);
			} else if (hc.enchantTest(name)) {
				enchants.set(name + ".price.ceiling", newceiling);
			}
			if (hc.itemTest(name)) {
				items.set(name + ".price.floor", newfloor);
			} else if (hc.enchantTest(name)) {
				enchants.set(name + ".price.floor", newfloor);
			}
		}
		hc.getYaml().saveYamls();
	}
}
