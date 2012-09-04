package regalowl.hyperconomy;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public class SQLFunctions {
	private HyperConomy hc;
	private String statement;
	private String username;
	private String password;
	private int port;
	private String host;
	private String database;
	private ArrayList<String> tne = new ArrayList<String>();
	private ArrayList<String> tname = new ArrayList<String>();
	private ArrayList<String> teconomy = new ArrayList<String>();
	private ArrayList<String> ttype = new ArrayList<String>();
	private ArrayList<String> tcategory = new ArrayList<String>();
	private ArrayList<String> tmaterial = new ArrayList<String>();
	private ArrayList<Integer> tid = new ArrayList<Integer>();
	private ArrayList<Integer> tdata = new ArrayList<Integer>();
	private ArrayList<Integer> tdurability = new ArrayList<Integer>();
	private ArrayList<Double> tvalue = new ArrayList<Double>();
	private ArrayList<String> tstatic = new ArrayList<String>();
	private ArrayList<Double> tstaticprice = new ArrayList<Double>();
	private ArrayList<Double> tstock = new ArrayList<Double>();
	private ArrayList<Double> tmedian = new ArrayList<Double>();
	private ArrayList<String> tinitiation = new ArrayList<String>();
	private ArrayList<Double> tstartprice = new ArrayList<Double>();
	private ArrayList<Double> tceiling = new ArrayList<Double>();
	private ArrayList<Double> tfloor = new ArrayList<Double>();
	private ArrayList<String> econplayer = new ArrayList<String>();
	private ArrayList<String> playerecon = new ArrayList<String>();
	private ArrayList<String> koec = new ArrayList<String>();
	private ArrayList<String> hobject = new ArrayList<String>();
	private ArrayList<String> heconomy = new ArrayList<String>();
	private ArrayList<Double> hprice = new ArrayList<Double>();
	private ArrayList<Integer> hcount = new ArrayList<Integer>();
	private HashMap<String, Integer> historyDataCount = new HashMap<String, Integer>();
	private int sqllockthreadid;
	private FileConfiguration items;
	private FileConfiguration enchants;
	private ArrayList<String> economies = new ArrayList<String>();

	SQLFunctions() {
		hc = HyperConomy.hc;
		if (hc.useSQL()) {
			FileConfiguration config = hc.getYaml().getConfig();
			username = config.getString("config.sql-connection.username");
			password = config.getString("config.sql-connection.password");
			port = config.getInt("config.sql-connection.port");
			host = config.getString("config.sql-connection.host");
			database = config.getString("config.sql-connection.database");
		} else {
			items = hc.getYaml().getItems();
			enchants = hc.getYaml().getEnchants();
			economies.add("default");
		}
	}

	public void setName(String name, String economy, String newname) {
		if (hc.useSQL()) {
			statement = "UPDATE hyperobjects SET NAME='" + newname + "' WHERE NAME = '" + name + "' AND ECONOMY = '" + economy + "'";
			write();
			int keyloc = tne.indexOf(name + ":" + economy);
			tname.set(keyloc, newname);
		} else {
			// not implemented
		}
	}

	public void setEconomy(String name, String economy, String neweconomy) {
		if (hc.useSQL()) {
			statement = "UPDATE hyperobjects SET ECONOMY='" + neweconomy + "' WHERE NAME = '" + name + "' AND ECONOMY = '" + economy + "'";
			write();
			int keyloc = tne.indexOf(name + ":" + economy);
			teconomy.set(keyloc, neweconomy);
		} else {
			// irrelevant
		}
	}

	public void setType(String name, String economy, String newtype) {
		int keyloc = tne.indexOf(name + ":" + economy);
		ttype.set(keyloc, newtype);
		if (hc.useSQL()) {
			statement = "UPDATE hyperobjects SET TYPE='" + newtype + "' WHERE NAME = '" + name + "' AND ECONOMY = '" + economy + "'";
			write();
		} else {
			if (hc.itemTest(name)) {
				items.set(name + ".information.type", newtype);
			} else if (hc.enchantTest(name)) {
				enchants.set(name + ".information.type", newtype);
			}
		}
	}

	public void setCategory(String name, String economy, String newcategory) {
		int keyloc = tne.indexOf(name + ":" + economy);
		tcategory.set(keyloc, newcategory);
		if (hc.useSQL()) {
			statement = "UPDATE hyperobjects SET CATEGORY='" + newcategory + "' WHERE NAME = '" + name + "' AND ECONOMY = '" + economy + "'";
			write();
		} else {
			if (hc.itemTest(name)) {
				items.set(name + ".information.category", newcategory);
			} else if (hc.enchantTest(name)) {
				enchants.set(name + ".information.category", newcategory);
			}
		}
	}

	public void setMaterial(String name, String economy, String newmaterial) {
		int keyloc = tne.indexOf(name + ":" + economy);
		tmaterial.set(keyloc, newmaterial);
		if (hc.useSQL()) {
			statement = "UPDATE hyperobjects SET MATERIAL='" + newmaterial + "' WHERE NAME = '" + name + "' AND ECONOMY = '" + economy + "'";
			write();
		} else {
			if (hc.itemTest(name)) {
				items.set(name + ".information.material", newmaterial);
			} else if (hc.enchantTest(name)) {
				enchants.set(name + ".information.name", newmaterial);
			}
		}
	}

	public void setId(String name, String economy, int newid) {
		int keyloc = tne.indexOf(name + ":" + economy);
		tid.set(keyloc, newid);
		if (hc.useSQL()) {
			statement = "UPDATE hyperobjects SET ID='" + newid + "' WHERE NAME = '" + name + "' AND ECONOMY = '" + economy + "'";
			write();
		} else {
			if (hc.itemTest(name)) {
				items.set(name + ".information.id", newid);
			} else if (hc.enchantTest(name)) {
				enchants.set(name + ".information.id", newid);
			}
		}
	}

	public void setData(String name, String economy, int newdata) {
		int keyloc = tne.indexOf(name + ":" + economy);
		tdata.set(keyloc, newdata);
		if (hc.useSQL()) {
			statement = "UPDATE hyperobjects SET DATA='" + newdata + "' WHERE NAME = '" + name + "' AND ECONOMY = '" + economy + "'";
			write();
		} else {
			if (hc.itemTest(name)) {
				items.set(name + ".information.data", newdata);
			} else if (hc.enchantTest(name)) {
				// do nothing
			}
		}
	}

	public void setDurability(String name, String economy, int newdurability) {
		int keyloc = tne.indexOf(name + ":" + economy);
		tdurability.set(keyloc, newdurability);
		if (hc.useSQL()) {
			statement = "UPDATE hyperobjects SET DURABILITY='" + newdurability + "' WHERE NAME = '" + name + "' AND ECONOMY = '" + economy + "'";
			write();
		} else {
			if (hc.itemTest(name)) {
				items.set(name + ".information.data", newdurability);
			} else if (hc.enchantTest(name)) {
				// do nothing
			}
		}
	}

	public void setValue(String name, String economy, double newvalue) {
		int keyloc = tne.indexOf(name + ":" + economy);
		tvalue.set(keyloc, newvalue);
		if (hc.useSQL()) {
			statement = "UPDATE hyperobjects SET VALUE='" + newvalue + "' WHERE NAME = '" + name + "' AND ECONOMY = '" + economy + "'";
			write();
		} else {
			if (hc.itemTest(name)) {
				items.set(name + ".value", newvalue);
			} else if (hc.enchantTest(name)) {
				enchants.set(name + ".value", newvalue);
			}
		}
	}

	public void setStatic(String name, String economy, String newstatic) {
		int keyloc = tne.indexOf(name + ":" + economy);
		tstatic.set(keyloc, newstatic);
		if (hc.useSQL()) {
			statement = "UPDATE hyperobjects SET STATIC='" + newstatic + "' WHERE NAME = '" + name + "' AND ECONOMY = '" + economy + "'";
			write();
		} else {
			if (hc.itemTest(name)) {
				items.set(name + ".price.static", Boolean.parseBoolean(newstatic));
			} else if (hc.enchantTest(name)) {
				enchants.set(name + ".price.static", Boolean.parseBoolean(newstatic));
			}
		}
	}

	public void setStaticPrice(String name, String economy, double newstaticprice) {
		int keyloc = tne.indexOf(name + ":" + economy);
		tstaticprice.set(keyloc, newstaticprice);
		if (hc.useSQL()) {
			statement = "UPDATE hyperobjects SET STATICPRICE='" + newstaticprice + "' WHERE NAME = '" + name + "' AND ECONOMY = '" + economy + "'";
			write();
		} else {
			if (hc.itemTest(name)) {
				items.set(name + ".price.staticprice", newstaticprice);
			} else if (hc.enchantTest(name)) {
				enchants.set(name + ".price.staticprice", newstaticprice);
			}
		}
	}

	public void setStock(String name, String economy, double newstock) {
		int keyloc = tne.indexOf(name + ":" + economy);
		tstock.set(keyloc, newstock);
		if (hc.useSQL()) {
			statement = "UPDATE hyperobjects SET STOCK='" + newstock + "' WHERE NAME = '" + name + "' AND ECONOMY = '" + economy + "'";
			write();
		} else {
			if (hc.itemTest(name)) {
				items.set(name + ".stock.stock", newstock);
			} else if (hc.enchantTest(name)) {
				enchants.set(name + ".stock.stock", newstock);
			}
		}
	}

	public void setMedian(String name, String economy, double newmedian) {
		int keyloc = tne.indexOf(name + ":" + economy);
		tmedian.set(keyloc, newmedian);
		if (hc.useSQL()) {
			statement = "UPDATE hyperobjects SET MEDIAN='" + newmedian + "' WHERE NAME = '" + name + "' AND ECONOMY = '" + economy + "'";
			write();
		} else {
			if (hc.itemTest(name)) {
				items.set(name + ".stock.median", newmedian);
			} else if (hc.enchantTest(name)) {
				enchants.set(name + ".stock.median", newmedian);
			}
		}
	}

	public void setInitiation(String name, String economy, String newinitiation) {
		int keyloc = tne.indexOf(name + ":" + economy);
		tinitiation.set(keyloc, newinitiation);
		if (hc.useSQL()) {
			statement = "UPDATE hyperobjects SET INITIATION='" + newinitiation + "' WHERE NAME = '" + name + "' AND ECONOMY = '" + economy + "'";
			write();
		} else {
			if (hc.itemTest(name)) {
				items.set(name + ".initiation.initiation", Boolean.parseBoolean(newinitiation));
			} else if (hc.enchantTest(name)) {
				enchants.set(name + ".initiation.initiation", Boolean.parseBoolean(newinitiation));
			}
		}
	}

	public void setStartPrice(String name, String economy, double newstartprice) {
		int keyloc = tne.indexOf(name + ":" + economy);
		tstartprice.set(keyloc, newstartprice);
		if (hc.useSQL()) {
			statement = "UPDATE hyperobjects SET STARTPRICE='" + newstartprice + "' WHERE NAME = '" + name + "' AND ECONOMY = '" + economy + "'";
			write();
		} else {
			if (hc.itemTest(name)) {
				items.set(name + ".initiation.startprice", newstartprice);
			} else if (hc.enchantTest(name)) {
				enchants.set(name + ".initiation.startprice", newstartprice);
			}
		}
	}

	public void setCeiling(String name, String economy, double newceiling) {
		int keyloc = tne.indexOf(name + ":" + economy);
		tceiling.set(keyloc, newceiling);
		if (hc.useSQL()) {
			statement = "UPDATE hyperobjects SET CEILING='" + newceiling + "' WHERE NAME = '" + name + "' AND ECONOMY = '" + economy + "'";
			write();
		} else {
			if (hc.itemTest(name)) {
				items.set(name + ".price.ceiling", newceiling);
			} else if (hc.enchantTest(name)) {
				enchants.set(name + ".price.ceiling", newceiling);
			}
		}
	}

	public void setFloor(String name, String economy, double newfloor) {
		int keyloc = tne.indexOf(name + ":" + economy);
		tfloor.set(keyloc, newfloor);
		if (hc.useSQL()) {
			statement = "UPDATE hyperobjects SET FLOOR='" + newfloor + "' WHERE NAME = '" + name + "' AND ECONOMY = '" + economy + "'";
			write();
		} else {
			if (hc.itemTest(name)) {
				items.set(name + ".price.floor", newfloor);
			} else if (hc.enchantTest(name)) {
				enchants.set(name + ".price.floor", newfloor);
			}
		}
	}

	public String getName(String name, String economy) {
		int keyloc = tne.indexOf(name + ":" + economy);
		return tname.get(keyloc);
	}

	public String getEconomy(String name, String economy) {
		int keyloc = tne.indexOf(name + ":" + economy);
		return teconomy.get(keyloc);
	}

	public String getType(String name, String economy) {
		int keyloc = tne.indexOf(name + ":" + economy);
		return ttype.get(keyloc);
	}

	public String getCategory(String name, String economy) {
		int keyloc = tne.indexOf(name + ":" + economy);
		return tcategory.get(keyloc);
	}

	public String getMaterial(String name, String economy) {
		int keyloc = tne.indexOf(name + ":" + economy);
		return tmaterial.get(keyloc);
	}

	public int getId(String name, String economy) {
		int keyloc = tne.indexOf(name + ":" + economy);
		return tid.get(keyloc);
	}

	public int getData(String name, String economy) {
		int keyloc = tne.indexOf(name + ":" + economy);
		return tdata.get(keyloc);
	}

	public int getDurability(String name, String economy) {
		int keyloc = tne.indexOf(name + ":" + economy);
		return tdurability.get(keyloc);
	}

	public double getValue(String name, String economy) {
		int keyloc = tne.indexOf(name + ":" + economy);
		return tvalue.get(keyloc);
	}

	public String getStatic(String name, String economy) {
		int keyloc = tne.indexOf(name + ":" + economy);
		return tstatic.get(keyloc);
	}

	public double getStaticPrice(String name, String economy) {
		int keyloc = tne.indexOf(name + ":" + economy);
		return tstaticprice.get(keyloc);
	}

	public double getStock(String name, String economy) {
		int keyloc = tne.indexOf(name + ":" + economy);
		return tstock.get(keyloc);
	}

	public double getMedian(String name, String economy) {
		int keyloc = tne.indexOf(name + ":" + economy);
		return tmedian.get(keyloc);
	}

	public String getInitiation(String name, String economy) {
		int keyloc = tne.indexOf(name + ":" + economy);
		return tinitiation.get(keyloc);
	}

	public double getStartPrice(String name, String economy) {
		int keyloc = tne.indexOf(name + ":" + economy);
		return tstartprice.get(keyloc);
	}

	public double getCeiling(String name, String economy) {
		int keyloc = tne.indexOf(name + ":" + economy);
		return tceiling.get(keyloc);
	}

	public double getFloor(String name, String economy) {
		int keyloc = tne.indexOf(name + ":" + economy);
		return tfloor.get(keyloc);
	}

	public String testName(String name, String economy) {
		if (!tne.contains(name + ":" + economy)) {
			return null;
		} else {
			return name;
		}
	}

	private void write() {
		SQLWrite sw = hc.getSQLWrite();
		sw.writeData(statement);
	}

	public void load() {
		if (!hc.sqlLock()) {
			hc.sqllockShop();
			sqllockthreadid = hc.getServer().getScheduler().scheduleSyncRepeatingTask(hc, new Runnable() {
				public void run() {
					int activethreads = hc.getSQLWrite().getActiveThreads();
					if (activethreads == 0) {
						cancelLock();
					}
				}
			}, 200L, 10L);
		}
	}

	private void cancelLock() {
		hc.getServer().getScheduler().cancelTask(sqllockthreadid);
		hc.getServer().getScheduler().scheduleSyncDelayedTask(hc, new Runnable() {
			public void run() {
				hc.buildData();
				loadSQL();
				hc.sqlunlockShop();
			}
		}, 40L);
	}

	private void loadSQL() {
		tne.clear();
		tname.clear();
		teconomy.clear();
		ttype.clear();
		tcategory.clear();
		tmaterial.clear();
		tid.clear();
		tdata.clear();
		tdurability.clear();
		tvalue.clear();
		tstatic.clear();
		tstaticprice.clear();
		tstock.clear();
		tmedian.clear();
		tinitiation.clear();
		tstartprice.clear();
		tceiling.clear();
		tfloor.clear();
		playerecon.clear();
		econplayer.clear();
		hobject.clear();
		heconomy.clear();
		hprice.clear();
		hcount.clear();
		tname = getStringColumn("SELECT NAME FROM hyperobjects");
		teconomy = getStringColumn("SELECT ECONOMY FROM hyperobjects");
		ttype = getStringColumn("SELECT TYPE FROM hyperobjects");
		tcategory = getStringColumn("SELECT CATEGORY FROM hyperobjects");
		tmaterial = getStringColumn("SELECT MATERIAL FROM hyperobjects");
		tid = getIntColumn("SELECT ID FROM hyperobjects");
		tdata = getIntColumn("SELECT DATA FROM hyperobjects");
		tdurability = getIntColumn("SELECT DURABILITY FROM hyperobjects");
		tvalue = getDoubleColumn("SELECT VALUE FROM hyperobjects");
		tstatic = getStringColumn("SELECT STATIC FROM hyperobjects");
		tstaticprice = getDoubleColumn("SELECT STATICPRICE FROM hyperobjects");
		tstock = getDoubleColumn("SELECT STOCK FROM hyperobjects");
		tmedian = getDoubleColumn("SELECT MEDIAN FROM hyperobjects");
		tinitiation = getStringColumn("SELECT INITIATION FROM hyperobjects");
		tstartprice = getDoubleColumn("SELECT STARTPRICE FROM hyperobjects");
		tceiling = getDoubleColumn("SELECT CEILING FROM hyperobjects");
		tfloor = getDoubleColumn("SELECT FLOOR FROM hyperobjects");
		playerecon = getStringColumn("SELECT ECONOMY FROM hyperplayers");
		econplayer = getStringColumn("SELECT PLAYER FROM hyperplayers");
		economies = getStringColumn("SELECT ECONOMY FROM hyperobjects");
		for (int c = 0; c < tname.size(); c++) {
			tne.add(tname.get(c) + ":" + teconomy.get(c));
		}
		// History
		hobject = getStringColumn("SELECT OBJECT FROM hyperhistory");
		heconomy = getStringColumn("SELECT ECONOMY FROM hyperhistory");
		hprice = getDoubleColumn("SELECT PRICE FROM hyperhistory");
		hcount = getIntColumn("SELECT COUNT FROM hyperhistory");
		for (int c = 0; c < hobject.size(); c++) {
			koec.add(hobject.get(c) + ":" + heconomy.get(c) + ":" + hcount.get(c));
		}
		startHistoryDataCount();
	}

	public void loadYML() {
		items = hc.getYaml().getItems();
		enchants = hc.getYaml().getEnchants();
		tne.clear();
		tname.clear();
		teconomy.clear();
		ttype.clear();
		tcategory.clear();
		tmaterial.clear();
		tid.clear();
		tdata.clear();
		tdurability.clear();
		tvalue.clear();
		tstatic.clear();
		tstaticprice.clear();
		tstock.clear();
		tmedian.clear();
		tinitiation.clear();
		tstartprice.clear();
		tceiling.clear();
		tfloor.clear();
		playerecon.clear();
		econplayer.clear();
		hobject.clear();
		heconomy.clear();
		hprice.clear();
		hcount.clear();
		koec.clear();
		ArrayList<String> names = hc.getNames();
		// Bukkit.broadcastMessage(names.toString());
		for (int i = 0; i < names.size(); i++) {
			String cname = names.get(i);
			if (hc.testiString(cname) != null) {
				tname.add(cname);
				teconomy.add("default");
				if (cname != "xp") {
					ttype.add("item");
				} else {
					ttype.add("xp");
				}
				String testcat = items.getString(cname + ".information.category");
				if (testcat == null) {
					tcategory.add("unknown");
				} else {
					tcategory.add(testcat);
				}
				tmaterial.add(items.getString(cname + ".information.material"));
				tid.add(items.getInt(cname + ".information.id"));
				tdata.add(items.getInt(cname + ".information.data"));
				tdurability.add(items.getInt(cname + ".information.data"));
				tvalue.add(items.getDouble(cname + ".value"));
				tstatic.add(items.getString(cname + ".price.static"));
				tstaticprice.add(items.getDouble(cname + ".price.staticprice"));
				tstock.add(items.getDouble(cname + ".stock.stock"));
				tmedian.add(items.getDouble(cname + ".stock.median"));
				tinitiation.add(items.getString(cname + ".initiation.initiation"));
				tstartprice.add(items.getDouble(cname + ".initiation.startprice"));
				tceiling.add(items.getDouble(cname + ".price.ceiling"));
				tfloor.add(items.getDouble(cname + ".price.floor"));
				playerecon.add("default");
			} else {
				tname.add(cname);
				teconomy.add("default");
				ttype.add("enchantment");
				String testcat = enchants.getString(cname + ".information.category");
				if (testcat == null) {
					tcategory.add("unknown");
				} else {
					tcategory.add(testcat);
				}
				tmaterial.add(enchants.getString(cname + ".information.name"));
				tid.add(enchants.getInt(cname + ".information.id"));
				tdata.add(-1);
				tdurability.add(-1);
				tvalue.add(enchants.getDouble(cname + ".value"));
				tstatic.add(enchants.getString(cname + ".price.static"));
				tstaticprice.add(enchants.getDouble(cname + ".price.staticprice"));
				tstock.add(enchants.getDouble(cname + ".stock.stock"));
				tmedian.add(enchants.getDouble(cname + ".stock.median"));
				tinitiation.add(enchants.getString(cname + ".initiation.initiation"));
				tstartprice.add(enchants.getDouble(cname + ".initiation.startprice"));
				tceiling.add(enchants.getDouble(cname + ".price.ceiling"));
				tfloor.add(enchants.getDouble(cname + ".price.floor"));
				playerecon.add("default");
			}
		}
		for (int c = 0; c < tname.size(); c++) {
			tne.add(tname.get(c) + ":" + "default");
		}
		FileConfiguration history = hc.getYaml().getHistory();
		for (int l = 0; l < names.size(); l++) {
			String object = history.getString(names.get(l));
			ArrayList<Double> data = new ArrayList<Double>();
			if (object != null) {
				while (object.contains(",")) {
					if (object.length() > 1) {
						double cdata = Double.parseDouble(object.substring(0, object.indexOf(",")));
						object = object.substring(object.indexOf(",") + 1, object.length());
						data.add(cdata);
						if (names.get(l).equalsIgnoreCase("ice")) {
							// Logger log = Logger.getLogger("Minecraft");
							// log.info(cdata + "");
						}
					} else {
						object = "";
					}
				}
			}
			for (int m = 0; m < data.size(); m++) {
				hobject.add(names.get(l));
				heconomy.add("default");
				hprice.add(data.get(m));
				// hcount.add(m + 1);
				koec.add(names.get(l) + ":" + "default" + ":" + (m + 1));
			}
		}
		startHistoryDataCount();
	}

	public ArrayList<String> getKeys() {
		return tne;
	}

	// make next 3 private again later
	public ArrayList<String> getStringColumn(String statement) {
		ArrayList<String> data = new ArrayList<String>();
		try {
			Connection connect = DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + database, username, password);
			Statement state = connect.createStatement();
			ResultSet result = state.executeQuery(statement);
			while (result.next()) {
				data.add(result.getString(1));
			}
			result.close();
			state.close();
			connect.close();
			return data;
		} catch (SQLException e) {
			Bukkit.broadcast(ChatColor.RED + "SQL connection failed.  Check your config settings.", "hyperconomy.error");
			e.printStackTrace();
			return data;
		}
	}

	public ArrayList<Double> getDoubleColumn(String statement) {
		ArrayList<Double> data = new ArrayList<Double>();
		try {
			Connection connect = DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + database, username, password);
			Statement state = connect.createStatement();
			ResultSet result = state.executeQuery(statement);
			while (result.next()) {
				data.add(result.getDouble(1));
			}
			result.close();
			state.close();
			connect.close();
			return data;
		} catch (SQLException e) {
			Bukkit.broadcast(ChatColor.RED + "SQL connection failed.  Check your config settings.", "hyperconomy.error");
			e.printStackTrace();
			return data;
		}
	}

	public ArrayList<Integer> getIntColumn(String statement) {
		ArrayList<Integer> data = new ArrayList<Integer>();
		try {
			Connection connect = DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + database, username, password);
			Statement state = connect.createStatement();
			ResultSet result = state.executeQuery(statement);
			while (result.next()) {
				data.add(result.getInt(1));
			}
			result.close();
			state.close();
			connect.close();
			return data;
		} catch (SQLException e) {
			Bukkit.broadcast(ChatColor.RED + "SQL connection failed.  Check your config settings.", "hyperconomy.error");
			e.printStackTrace();
			return data;
		}
	}

	public String getPlayerEconomy(String player) {
		try {
			if (player == null) {
				return "default";
			}
			player = player.toLowerCase();
			if (econplayer.indexOf(player) == -1) {
				addPlayerEconomy(player, "default");
				if (hc.useSQL()) {
					setPlayerEconomy(player, "default");
				}
			}
			String econ = playerecon.get(econplayer.indexOf(player.toLowerCase()));
			return econ;
		} catch (Exception e) {
			e.printStackTrace();
			return "default";
		}
	}

	public String getPlayerEconomy(Player p) {
		try {
			if (p == null) {
				return "default";
			}
			String player = p.getName();
			player = player.toLowerCase();
			if (econplayer.indexOf(player) == -1) {
				addPlayerEconomy(player, "default");
				if (hc.useSQL()) {
					setPlayerEconomy(player, "default");
				}
			}
			String econ = playerecon.get(econplayer.indexOf(player.toLowerCase()));
			return econ;
		} catch (Exception e) {
			e.printStackTrace();
			return "default";
		}
	}

	public void addPlayerEconomy(String player, String economy) {
		if (!econplayer.contains(player)) {
			playerecon.add(economy);
			econplayer.add(player);
		}
	}

	public void setPlayerEconomy(String player, String econ) {
		statement = "UPDATE hyperplayers SET ECONOMY='" + econ + "' WHERE PLAYER = '" + player.toLowerCase() + "'";
		write();
		try {
			playerecon.set(econplayer.indexOf(player.toLowerCase()), econ);
		} catch (Exception e) {
			SQLRetry sqr = new SQLRetry();
			sqr.retrySetEconomy(hc, player, econ);
		}
	}

	public int countTableEntries(String table) {
		try {
			Connection connect = DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + database, username, password);
			Statement statement = connect.createStatement();
			ResultSet result = statement.executeQuery("SELECT COUNT(*) FROM " + table);
			result.next();
			int rowcount = result.getInt(1);
			result.close();
			statement.close();
			connect.close();
			return rowcount;
		} catch (SQLException e) {
			Bukkit.broadcast(ChatColor.RED + "SQL connection failed.  Check your config settings.", "hyperconomy.error");
			e.printStackTrace();
			return 0;
		}
	}

	private void startHistoryDataCount() {
		if (hc.useSQL()) {
			ArrayList<String> names = hc.getNames();
			ArrayList<String> ecns = new ArrayList<String>();
			ArrayList<String> economies = getStringColumn("SELECT ECONOMY FROM hyperobjects");
			HashMap<String, String> uecons = new HashMap<String, String>();
			for (int c = 0; c < economies.size(); c++) {
				uecons.put(economies.get(c), "irrelevant");
			}
			Set<String> econs = uecons.keySet();
			Iterator<String> it = econs.iterator();
			while (it.hasNext()) {
				ecns.add(it.next());
			}
			for (int j = 0; j < ecns.size(); j++) {
				String economy = ecns.get(j);
				for (int k = 0; k < names.size(); k++) {
					String name = names.get(k);
					String match = name + ":" + economy;
					int count = 0;
					for (int i = 0; i < koec.size(); i++) {
						String subkey = koec.get(i);
						subkey = subkey.substring(0, subkey.lastIndexOf(":"));
						if (match.equalsIgnoreCase(subkey)) {
							count++;
						}
					}
					historyDataCount.put(match, count);
				}
			}
		} else {
			ArrayList<String> names = hc.getNames();
			for (int k = 0; k < names.size(); k++) {
				String name = names.get(k);
				String match = name + ":" + "default";
				int count = 0;
				for (int i = 0; i < koec.size(); i++) {
					String subkey = koec.get(i);
					subkey = subkey.substring(0, subkey.lastIndexOf(":"));
					if (match.equalsIgnoreCase(subkey)) {
						count++;
					}
				}
				historyDataCount.put(match, count);
			}
		}
	}

	public int getHistoryDataCount(String name, String economy) {
		String match = name + ":" + economy;
		return historyDataCount.get(match);
	}

	public void setHistoryDataCount(String name, String economy, int value) {
		String match = name + ":" + economy;
		historyDataCount.put(match, value);
	}

	public void writeHistoryData(String object, String economy, double price) {
		int count = getHistoryDataCount(object, economy) + 1;
		if (hc.useSQL()) {
			String statement = "Insert Into hyperhistory (OBJECT, ECONOMY, TIME, PRICE, COUNT)" + " Values ('" + object + "','" + economy + "', NOW() ,'" + price + "','" + count + "')";
			hc.getSQLWrite().writeData(statement);
			int daystosavehistory = hc.getYaml().getConfig().getInt("config.daystosavehistory");
			statement = "DELETE FROM hyperhistory WHERE time < DATE_SUB(NOW(), INTERVAL " + daystosavehistory + " DAY)";
			hc.getSQLWrite().writeData(statement);
			setHistoryDataCount(object, economy, getHistoryDataCount(object, economy) + 1);
		} else {
			FileConfiguration history = hc.getYaml().getHistory();
			String testhistory = history.getString(object);
			if (testhistory == null) {
				history.set(object, price + ",");
				setHistoryDataCount(object, economy, 1);
			} else {
				String historylist = history.getString(object);
				historylist = historylist + price + ",";
				// Stops the history file from growing larger than 2 weeks of
				// entries.
				int daystosavehistory = hc.getYaml().getConfig().getInt("config.daystosavehistory");
				int historylength = historylist.replaceAll("[\\d]", "").replace(".", "").length();
				if (historylength > (daystosavehistory * 24)) {
					historylist = historylist.substring(historylist.indexOf(",") + 1, historylist.length());
					historylength = historylength - 1;
				}
				history.set(object, historylist);
				setHistoryDataCount(object, economy, getHistoryDataCount(object, economy) + 1);
			}
		}
		hobject.add(object);
		heconomy.add(economy);
		hprice.add(price);
		// hcount.add(count);
		koec.add(object + ":" + economy + ":" + count);
	}

	public Double getHistoryData(String object, String economy, int count) {
		int lcount = getHistoryDataCount(object, economy);
		count = lcount - count + 1;
		String key = object + ":" + economy + ":" + count;
		int keyloc = koec.indexOf(key);
		if (keyloc == -1) {
			return -1.0;
		}
		Double hvalue = hprice.get(keyloc);
		return hvalue;
	}

	public boolean testEconomy(String economy) {
		if (teconomy.contains(economy)) {
			return true;
		} else {
			return false;
		}
	}

	public String getUserName() {
		return username;
	}

	public String getPassword() {
		return password;
	}

	public String getDatabase() {
		return database;
	}

	public String getHost() {
		return host;
	}

	public int getPort() {
		return port;
	}

	public ArrayList<String> getEconomies() {
		return economies;
	}
}
