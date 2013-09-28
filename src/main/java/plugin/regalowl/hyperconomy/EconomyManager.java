package regalowl.hyperconomy;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.scheduler.BukkitTask;

import regalowl.databukkit.QueryResult;
import regalowl.databukkit.SQLRead;
import regalowl.databukkit.SQLWrite;

public class EconomyManager {

	private HyperConomy hc;
	private SQLRead sr;
	private boolean economiesLoaded;
	private BukkitTask wait;
	private boolean loadActive;
	
	private ConcurrentHashMap<String, HyperEconomy> economies = new ConcurrentHashMap<String, HyperEconomy>();
	
	public EconomyManager() {
		hc = HyperConomy.hc;
		loadActive = false;
		economiesLoaded = false;
	}
	
	
	public void load() {
		if (loadActive) {return;}
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
			if (version == 1.1) {
				hc.getSQLWrite().convertExecuteSQL("CREATE TABLE IF NOT EXISTS hyperconomy_shop_objects (ID INT NOT NULL PRIMARY KEY AUTO_INCREMENT, SHOP VARCHAR(255) NOT NULL, HYPEROBJECT VARCHAR(255) NOT NULL, QUANTITY DOUBLE NOT NULL, PRICE DOUBLE NOT NULL, STATUS VARCHAR(255) NOT NULL)");
				hc.getSQLWrite().executeSQL("UPDATE hyperconomy_settings SET VALUE = '1.2' WHERE SETTING = 'version'");
			}
			if (version == 1.2) {
				//for next version
			}
		} else {
			createTables();
		}
		hc.getSQLWrite().afterWrite(this, "load2");
	}
	public void createTables() {
		hc.getSQLWrite().convertExecuteSQL("CREATE TABLE IF NOT EXISTS hyperconomy_settings (SETTING VARCHAR(255) NOT NULL, VALUE TEXT, TIME DATETIME NOT NULL, PRIMARY KEY (SETTING))");
		hc.getSQLWrite().convertExecuteSQL("DELETE FROM hyperconomy_settings");
		hc.getSQLWrite().convertExecuteSQL("INSERT INTO hyperconomy_settings (SETTING, VALUE, TIME) VALUES ('version', '1.1', NOW() )");
		hc.getSQLWrite().convertExecuteSQL("CREATE TABLE IF NOT EXISTS hyperconomy_objects (NAME VARCHAR(255) NOT NULL, ECONOMY VARCHAR(255) NOT NULL, TYPE TINYTEXT, CATEGORY TINYTEXT, MATERIAL TINYTEXT, ID INTEGER, DATA INTEGER, DURABILITY INTEGER, VALUE DOUBLE, STATIC TINYTEXT, STATICPRICE DOUBLE, STOCK DOUBLE, MEDIAN DOUBLE, INITIATION TINYTEXT, STARTPRICE DOUBLE, CEILING DOUBLE, FLOOR DOUBLE, MAXSTOCK DOUBLE NOT NULL DEFAULT '1000000', PRIMARY KEY (NAME, ECONOMY))");
		hc.getSQLWrite().convertExecuteSQL("CREATE TABLE IF NOT EXISTS hyperconomy_players (PLAYER VARCHAR(255) NOT NULL PRIMARY KEY, ECONOMY TINYTEXT, BALANCE DOUBLE NOT NULL DEFAULT '0', X DOUBLE NOT NULL DEFAULT '0', Y DOUBLE NOT NULL DEFAULT '0', Z DOUBLE NOT NULL DEFAULT '0', WORLD TINYTEXT NOT NULL, HASH VARCHAR(255) NOT NULL DEFAULT '', SALT VARCHAR(255) NOT NULL DEFAULT '')");
		hc.getSQLWrite().convertExecuteSQL("CREATE TABLE IF NOT EXISTS hyperconomy_log (ID INTEGER NOT NULL PRIMARY KEY AUTO_INCREMENT, TIME DATETIME, CUSTOMER TINYTEXT, ACTION TINYTEXT, OBJECT TINYTEXT, AMOUNT DOUBLE, MONEY DOUBLE, TAX DOUBLE, STORE TINYTEXT, TYPE TINYTEXT)");
		hc.getSQLWrite().convertExecuteSQL("CREATE TABLE IF NOT EXISTS hyperconomy_history (ID INTEGER NOT NULL PRIMARY KEY AUTO_INCREMENT, OBJECT TINYTEXT, ECONOMY TINYTEXT, TIME DATETIME, PRICE DOUBLE)");
		hc.getSQLWrite().convertExecuteSQL("CREATE TABLE IF NOT EXISTS hyperconomy_audit_log (ID INTEGER NOT NULL PRIMARY KEY AUTO_INCREMENT, TIME DATETIME NOT NULL, ACCOUNT TINYTEXT NOT NULL, ACTION TINYTEXT NOT NULL, AMOUNT DOUBLE NOT NULL, ECONOMY TINYTEXT NOT NULL)");
		hc.getSQLWrite().convertExecuteSQL("CREATE TABLE IF NOT EXISTS hyperconomy_shop_objects (ID INT NOT NULL PRIMARY KEY AUTO_INCREMENT, SHOP VARCHAR(255) NOT NULL, HYPEROBJECT VARCHAR(255) NOT NULL, QUANTITY DOUBLE NOT NULL, PRICE DOUBLE NOT NULL, STATUS VARCHAR(255) NOT NULL)");
	}
	public void load2() {
		String query = "SELECT NAME FROM hyperconomy_objects WHERE ECONOMY='default'";
		hc.getSQLRead().syncRead(this, "load3", query, null);
	}
	public void load3(QueryResult qr) {
		if (!qr.next()) {
			HyperConomy.hc.getEconomyManager().createEconomyFromYml("default");
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
				waitForLoad();
			}
		});
	}
	private void waitForLoad() {
		wait = hc.getServer().getScheduler().runTaskTimer(hc, new Runnable() {
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
					wait.cancel();
					hc.onDataLoad();
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
		return getHyperPlayer(hc.gYH().gFC("config").getString("config.global-shop-account"));
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
		load();
	}
	public void deleteEconomy(String economy) {
		hc.getSQLWrite().executeSQL("DELETE FROM hyperconomy_objects WHERE ECONOMY='" + economy + "'");
		load();
	}

	

	public void createEconomyFromYml(String econ) {
		new Backup();
		FileConfiguration itemsyaml = hc.gYH().gFC("items");
		FileConfiguration enchantsyaml = hc.gYH().gFC("enchants");
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
	
	
	
	
	
	

	

	
}
