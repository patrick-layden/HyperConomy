package regalowl.hyperconomy;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public class DataFunctions {
	private HyperConomy hc;
	private String username;
	private String password;
	private int port;
	private String host;
	private String database;
	private boolean sqlloaded;
	private boolean databuilt;
	private HashMap<String, HyperObject> hyperObjects = new HashMap<String, HyperObject>();
	
	
	private ArrayList<String> econplayer = new ArrayList<String>();
	private ArrayList<String> playerecon = new ArrayList<String>();
	private ArrayList<Double> playerbalance = new ArrayList<Double>();
	private ArrayList<String> koec = new ArrayList<String>();
	private ArrayList<String> hobject = new ArrayList<String>();
	private ArrayList<String> heconomy = new ArrayList<String>();
	private ArrayList<Double> hprice = new ArrayList<Double>();
	private ArrayList<Integer> hcount = new ArrayList<Integer>();
	private HashMap<String, Integer> historyDataCount = new HashMap<String, Integer>();
	private int sqllockthreadid;
	private ArrayList<String> economies = new ArrayList<String>();

	DataFunctions() {
		hc = HyperConomy.hc;
		FileConfiguration config = hc.getYaml().getConfig();
		username = config.getString("config.sql-connection.username");
		password = config.getString("config.sql-connection.password");
		port = config.getInt("config.sql-connection.port");
		host = config.getString("config.sql-connection.host");
		database = config.getString("config.sql-connection.database");
		sqlloaded = false;
		databuilt = false;
	}

	
	public HyperObject getHyperObject(String key) {
		if (hyperObjects.containsKey(key)) {
			return hyperObjects.get(key);
		} else {
			return null;
		}
	}
	
	public HyperObject getHyperObject(String name, String economy) {
		String key = name + ":" + economy;
		if (hyperObjects.containsKey(key)) {
			return hyperObjects.get(key);
		} else {
			return null;
		}
	}
	
	
	public String testName(String name, String economy) {
		if (!hyperObjects.containsKey(name + ":" + economy)) {
			return null;
		} else {
			return name;
		}
	}

	public void load() {
		reset();
		hc.sqllockShop();
		sqllockthreadid = hc.getServer().getScheduler().scheduleSyncRepeatingTask(hc, new Runnable() {
			public void run() {
				SQLWrite sw = hc.getSQLWrite();
				if (hc.getSQLWrite().getBuffer().size() == 0 && !sw.initialWrite()) {
					databuilt = hc.buildData();
					sqlloaded = loadSQL();
					hc.sqlunlockShop();
					hc.getServer().getScheduler().cancelTask(sqllockthreadid);
					hc.onDataLoad();
				}
			}
		}, 0L, 10L);
	}

	private boolean loadSQL() {
		hyperObjects.clear();
		
		playerecon.clear();
		playerbalance.clear();
		econplayer.clear();
		hobject.clear();
		heconomy.clear();
		hprice.clear();
		hcount.clear();
		
		try {
			Connection connect = hc.getSQLWrite().getConnectionPool().getConnectionForRead();
			Statement state = connect.createStatement();
			ResultSet result = state.executeQuery("SELECT * FROM hyperobjects");
			while (result.next()) {
				HyperObject hobj = new HyperObject();
				hobj.setName(result.getString("NAME"));
				hobj.setEconomy(result.getString("ECONOMY"));
				hobj.setType(result.getString("TYPE"));
				hobj.setCategory(result.getString("CATEGORY"));
				hobj.setMaterial(result.getString("MATERIAL"));
				hobj.setId(result.getInt("ID"));
				hobj.setData(result.getInt("DATA"));
				hobj.setDurability(result.getInt("DURABILITY"));
				hobj.setValue(result.getDouble("VALUE"));
				hobj.setIsstatic(result.getString("STATIC"));
				hobj.setStaticprice(result.getDouble("STATICPRICE"));
				hobj.setStock(result.getDouble("STOCK"));
				hobj.setMedian(result.getDouble("MEDIAN"));
				hobj.setInitiation(result.getString("INITIATION"));
				hobj.setStartprice(result.getDouble("STARTPRICE"));
				hobj.setCeiling(result.getDouble("CEILING"));
				hobj.setFloor(result.getDouble("FLOOR"));
				hyperObjects.put(hobj.getName() + ":" + hobj.getEconomy(), hobj);
			}
			result.close();
			state.close();
			connect.close();
		} catch (SQLException e) {
			new HyperError(e);
		}
		
		playerecon = getStringColumn("SELECT ECONOMY FROM hyperplayers");
		econplayer = getStringColumn("SELECT PLAYER FROM hyperplayers");
		playerbalance = getDoubleColumn("SELECT BALANCE FROM hyperplayers");
		economies = getStringColumn("SELECT ECONOMY FROM hyperobjects");

		// History
		hobject = getStringColumn("SELECT OBJECT FROM hyperhistory");
		heconomy = getStringColumn("SELECT ECONOMY FROM hyperhistory");
		hprice = getDoubleColumn("SELECT PRICE FROM hyperhistory");
		hcount = getIntColumn("SELECT COUNT FROM hyperhistory");
		for (int c = 0; c < hobject.size(); c++) {
			koec.add(hobject.get(c) + ":" + heconomy.get(c) + ":" + hcount.get(c));
		}
		startHistoryDataCount();
		return true;
	}

	public ArrayList<String> getKeys() {
		ArrayList<String> keys = new ArrayList<String>();
		for (String key:hyperObjects.keySet()) {
			keys.add(key);
		}
		return keys;
	}

	// make next 3 private again later

	public ArrayList<String> getStringColumn(String statement) {
		ArrayList<String> data = new ArrayList<String>();
		try {
			Connection connect = hc.getSQLWrite().getConnectionPool().getConnectionForRead();
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
			Connection connect = hc.getSQLWrite().getConnectionPool().getConnectionForRead();
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
			Connection connect = hc.getSQLWrite().getConnectionPool().getConnectionForRead();
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
		player = fixpN(player);
		try {
			if (player == null) {
				return "default";
			}
			if (econplayer.indexOf(player) == -1) {
				addPlayer(player);
			}
			String econ = playerecon.get(econplayer.indexOf(player));
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
			player = fixpN(player);
			if (econplayer.indexOf(player) == -1) {
				addPlayer(player);
			}
			String econ = playerecon.get(econplayer.indexOf(player));
			return econ;
		} catch (Exception e) {
			e.printStackTrace();
			return "default";
		}
	}

	public void addPlayer(String player) {
		player = fixpN(player);
		if (!econplayer.contains(player)) {
			if (!inDatabase(player)) {
				SQLWrite sw = hc.getSQLWrite();
				sw.writeData("Insert Into hyperplayers (PLAYER, ECONOMY, BALANCE)" + " Values ('" + player + "','" + "default" + "','" + 0.0 + "')");
			}
			playerecon.add("default");
			econplayer.add(player);
			playerbalance.add(0.0);
		}
	}

	public void setPlayerEconomy(String player, String econ) {
		player = fixpN(player);
		try {
			String statement = "UPDATE hyperplayers SET ECONOMY='" + econ + "' WHERE PLAYER = '" + player + "'";
			hc.getSQLWrite().writeData(statement);
			playerecon.set(econplayer.indexOf(player), econ);
		} catch (Exception e) {
			SQLRetry sqr = new SQLRetry();
			sqr.retrySetEconomy(hc, player, econ);
		}
	}

	public boolean hasAccount(String name) {
		name = fixpN(name);
		boolean ha = false;
		if (econplayer.contains(name)) {
			ha = true;
		}
		return ha;
	}

	public Double getPlayerBalance(Player p) {
		try {
			if (p == null) {
				throw new PlayerNotFoundException("Function passed player that cannot be found");
			}
			String player = p.getName();
			player = fixpN(player);
			if (econplayer.indexOf(player) != -1) {
				return playerbalance.get(econplayer.indexOf(player));
			} else {
				throw new PlayerNotFoundException("Function passed player that cannot be found");
			}
		} catch (Exception e) {
			new HyperError(e, "Passed player: " + p.getDisplayName());
			return 0.0;
		}
	}

	public Double getPlayerBalance(String player) {
		player = fixpN(player);
		try {
			if (econplayer.indexOf(player) != -1) {
				return playerbalance.get(econplayer.indexOf(player));
			} else {
				throw new PlayerNotFoundException("Function passed player that cannot be found");
			}
		} catch (Exception e) {
			new HyperError(e, "Passed player: " + player);
			return 0.0;
		}
	}

	public void setPlayerBalance(String player, Double balance) {
		player = fixpN(player);
		Calculation calc = hc.getCalculation();
		balance = calc.twoDecimals(balance);
		try {
			String statement = "UPDATE hyperplayers SET BALANCE='" + balance + "' WHERE PLAYER = '" + player + "'";
			hc.getSQLWrite().writeData(statement);
			playerbalance.set(econplayer.indexOf(player), balance);
		} catch (Exception e) {
			new HyperError(e, "Passed player: " + player + ", balance: " + balance);
		}
	}

	public void setPlayerBalance(Player p, Double balance) {
		Calculation calc = hc.getCalculation();
		balance = calc.twoDecimals(balance);
		String player = p.getName();
		player = fixpN(player);
		try {
			String statement = "UPDATE hyperplayers SET BALANCE='" + balance + "' WHERE PLAYER = '" + player + "'";
			hc.getSQLWrite().writeData(statement);
			playerbalance.set(econplayer.indexOf(player), balance);
		} catch (Exception e) {
			new HyperError(e, "Passed player: " + player + ", balance: " + balance);
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
		return econplayer;
	}

	public ArrayList<Double> getPlayerBalances() {
		return playerbalance;
	}

	public int countTableEntries(String table) {
		try {
			Connection connect = hc.getSQLWrite().getConnectionPool().getConnectionForRead();
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
	}

	public int getHistoryDataCount(String name, String economy) {
		try {
			String match = name + ":" + economy;
			return historyDataCount.get(match);
		} catch (Exception e) {
			String info = "SQLFunctions getHistoryDataCount() passed values name='" + name + "', economy='" + economy + "'";
			new HyperError(e, info);
			return -1;
		}
	}

	public void setHistoryDataCount(String name, String economy, int value) {
		try {
			String match = name + ":" + economy;
			historyDataCount.put(match, value);
		} catch (Exception e) {
			String info = "SQLFunctions setHistoryDataCount() passed values name='" + name + "', economy='" + economy + "', value='" + value + "'";
			new HyperError(e, info);
		}
	}

	public void writeHistoryData(String object, String economy, double price) {
		int count = getHistoryDataCount(object, economy) + 1;
		String statement = "";
		if (hc.useMySQL()) {
			statement = "Insert Into hyperhistory (OBJECT, ECONOMY, TIME, PRICE, COUNT)" + " Values ('" + object + "','" + economy + "', NOW() ,'" + price + "','" + count + "')";
		} else {
			statement = "Insert Into hyperhistory (OBJECT, ECONOMY, TIME, PRICE, COUNT)" + " Values ('" + object + "','" + economy + "', datetime('NOW', 'localtime') ,'" + price + "','" + count + "')";
		}
		hc.getSQLWrite().writeData(statement);
		int daystosavehistory = hc.getYaml().getConfig().getInt("config.daystosavehistory");
		if (hc.useMySQL()) {
			statement = "DELETE FROM hyperhistory WHERE time < DATE_SUB(NOW(), INTERVAL " + daystosavehistory + " DAY)";
		} else {
			statement = "DELETE FROM hyperhistory WHERE time < date('now','" + formatSQLiteTime(daystosavehistory * -1) + " day')";
		}
		hc.getSQLWrite().writeData(statement);
		setHistoryDataCount(object, economy, getHistoryDataCount(object, economy) + 1);
		hobject.add(object);
		heconomy.add(economy);
		hprice.add(price);
		koec.add(object + ":" + economy + ":" + count);
	}
	
	public String formatSQLiteTime(int time) {
		if (time < 0) {
			return "-" + Math.abs(time);
		} else if (time > 0) {
			return "+" + time;
		} else {
			return "0";
		}
	}

	public Double getHistoryData(String object, String economy, int count) {
		try {
			int lcount = getHistoryDataCount(object, economy);
			count = lcount - count + 1;
			String key = object + ":" + economy + ":" + count;
			int keyloc = koec.indexOf(key);
			if (keyloc == -1) {
				return -1.0;
			}
			Double hvalue = hprice.get(keyloc);
			return hvalue;
		} catch (Exception e) {
			String info = "SQLFunctions getHistoryData() passed values object='" + object + "', economy='" + economy + "', count='" + count + "'";
			new HyperError(e, info);
			return -1.0;
		}
	}

	public boolean testEconomy(String economy) {
		if (economies.contains(economy)) {
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

	public ArrayList<String> getEconomyList() {
		ArrayList<String> econs = new ArrayList<String>();
		for (int i = 0; i < economies.size(); i++) {
			if (!econs.contains(economies.get(i))) {
				econs.add(economies.get(i));
			}
		}
		return econs;
	}

	public boolean sqlLoaded() {
		return sqlloaded;
	}

	public boolean dataBuilt() {
		return databuilt;
	}

	public void reset() {
		sqlloaded = false;
		databuilt = false;
	}

	public void clearData() {
		username = null;
		password = null;
		host = null;
		database = null;
		hyperObjects.clear();

		econplayer.clear();
		playerecon.clear();
		koec.clear();
		hobject.clear();
		heconomy.clear();
		hprice.clear();
		hcount.clear();
		historyDataCount.clear();
		economies.clear();
	}

	public void clearHistory() {
		String statement = "TRUNCATE TABLE hyperhistory";
		hc.getSQLWrite().writeData(statement);
		koec.clear();
		hobject.clear();
		heconomy.clear();
		hprice.clear();
		hcount.clear();
		historyDataCount.clear();
	}

	public String fixpN(String player) {
		for (int i = 0; i < econplayer.size(); i++) {
			if (econplayer.get(i).equalsIgnoreCase(player)) {
				return econplayer.get(i);
			}
		}
		return player;
	}

	public boolean inDatabase(String player) {
		player = fixpN(player);
		boolean indatabase = true;
		try {
			Connection connect = hc.getSQLWrite().getConnectionPool().getConnectionForRead();
			Statement state = connect.createStatement();
			ResultSet result = state.executeQuery("SELECT PLAYER FROM hyperplayers WHERE PLAYER = " + "'" + player + "'");
			if (!result.next()) {
				indatabase = false;
			}
			result.close();
			state.close();
			connect.close();
			return indatabase;
		} catch (SQLException e) {
			Bukkit.broadcast(ChatColor.RED + "SQL connection failed.  Check your config settings.", "hyperconomy.admin");
			e.printStackTrace();
			return false;
		}
	}
}
