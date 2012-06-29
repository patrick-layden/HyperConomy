package regalowl.hyperconomy;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;

public class SQLFunctions {
	
	private HyperConomy hc;
	private String statement;
	
	private String username;
	private String password;
	private int port;
	private String host;
	private String database;
	
	//primary key
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
	
	
	private ArrayList<String> econplayer = new ArrayList<String>();
	private ArrayList<String> playerecon = new ArrayList<String>();
	
	private int sqllockthreadid;

	
	
	SQLFunctions(HyperConomy hyc) {
		hc = hyc;
		
		FileConfiguration config = hc.getYaml().getConfig();
		username = config.getString("config.sql-connection.username");
		password = config.getString("config.sql-connection.password");
		port = config.getInt("config.sql-connection.port");
		host = config.getString("config.sql-connection.host");
		database = config.getString("config.sql-connection.database");
		
		
	}
	
	
	public void setName(String name, String economy, String newname) {
		statement = "UPDATE hyperobjects SET NAME='" + newname + "' WHERE NAME = '" + name + "' AND ECONOMY = '" + economy + "'";
		write();
		int keyloc = tne.indexOf(name + ":" + economy);
		tname.set(keyloc, newname);
	}
	
	public void setEconomy(String name, String economy, String neweconomy) {
		statement = "UPDATE hyperobjects SET ECONOMY='" + neweconomy + "' WHERE NAME = '" + name + "' AND ECONOMY = '" + economy + "'";
		write();
		int keyloc = tne.indexOf(name + ":" + economy);
		teconomy.set(keyloc, neweconomy);
	}
	
	public void setType(String name, String economy, String newtype) {
		statement = "UPDATE hyperobjects SET TYPE='" + newtype + "' WHERE NAME = '" + name + "' AND ECONOMY = '" + economy + "'";
		write();
		int keyloc = tne.indexOf(name + ":" + economy);
		ttype.set(keyloc, newtype);
	}
	
	public void setCategory(String name, String economy, String newcategory) {
		statement = "UPDATE hyperobjects SET CATEGORY='" + newcategory + "' WHERE NAME = '" + name + "' AND ECONOMY = '" + economy + "'";
		write();
		int keyloc = tne.indexOf(name + ":" + economy);
		tcategory.set(keyloc, newcategory);
	}
	
	public void setMaterial(String name, String economy, String newmaterial) {
		statement = "UPDATE hyperobjects SET MATERIAL='" + newmaterial + "' WHERE NAME = '" + name + "' AND ECONOMY = '" + economy + "'";
		write();
		int keyloc = tne.indexOf(name + ":" + economy);
		tmaterial.set(keyloc, newmaterial);
	}
	
	public void setId(String name, String economy, int newid) {
		statement = "UPDATE hyperobjects SET ID='" + newid + "' WHERE NAME = '" + name + "' AND ECONOMY = '" + economy + "'";
		write();
		int keyloc = tne.indexOf(name + ":" + economy);
		tid.set(keyloc, newid);
	}
	
	public void setData(String name, String economy, int newdata) {
		statement = "UPDATE hyperobjects SET DATA='" + newdata + "' WHERE NAME = '" + name + "' AND ECONOMY = '" + economy + "'";
		write();
		int keyloc = tne.indexOf(name + ":" + economy);
		tdata.set(keyloc, newdata);
	}
	
	public void setDurability(String name, String economy, int newdurability) {
		statement = "UPDATE hyperobjects SET DURABILITY='" + newdurability + "' WHERE NAME = '" + name + "' AND ECONOMY = '" + economy + "'";
		write();
		int keyloc = tne.indexOf(name + ":" + economy);
		tdurability.set(keyloc, newdurability);
	}
	
	public void setValue(String name, String economy, double newvalue) {
		statement = "UPDATE hyperobjects SET VALUE='" + newvalue + "' WHERE NAME = '" + name + "' AND ECONOMY = '" + economy + "'";
		write();
		int keyloc = tne.indexOf(name + ":" + economy);
		tvalue.set(keyloc, newvalue);
	}
	
	public void setStatic(String name, String economy, String newstatic) {
		statement = "UPDATE hyperobjects SET STATIC='" + newstatic + "' WHERE NAME = '" + name + "' AND ECONOMY = '" + economy + "'";
		write();
		int keyloc = tne.indexOf(name + ":" + economy);
		tstatic.set(keyloc, newstatic);
	}
	
	public void setStaticPrice(String name, String economy, double newstaticprice) {
		statement = "UPDATE hyperobjects SET STATICPRICE='" + newstaticprice + "' WHERE NAME = '" + name + "' AND ECONOMY = '" + economy + "'";
		write();
		int keyloc = tne.indexOf(name + ":" + economy);
		tstaticprice.set(keyloc, newstaticprice);
	}
	
	public void setStock(String name, String economy, double newstock) {
		statement = "UPDATE hyperobjects SET STOCK='" + newstock + "' WHERE NAME = '" + name + "' AND ECONOMY = '" + economy + "'";
		write();
		int keyloc = tne.indexOf(name + ":" + economy);
		tstock.set(keyloc, newstock);
	}
	
	public void setMedian(String name, String economy, double newmedian) {
		statement = "UPDATE hyperobjects SET MEDIAN='" + newmedian + "' WHERE NAME = '" + name + "' AND ECONOMY = '" + economy + "'";
		write();
		int keyloc = tne.indexOf(name + ":" + economy);
		tmedian.set(keyloc, newmedian);
	}
	
	public void setInitiation(String name, String economy, String newinitiation) {
		statement = "UPDATE hyperobjects SET INITIATION='" + newinitiation + "' WHERE NAME = '" + name + "' AND ECONOMY = '" + economy + "'";
		write();
		int keyloc = tne.indexOf(name + ":" + economy);
		tinitiation.set(keyloc, newinitiation);
	}
	
	public void setStartPrice(String name, String economy, double newstartprice) {
		statement = "UPDATE hyperobjects SET STARTPRICE='" + newstartprice + "' WHERE NAME = '" + name + "' AND ECONOMY = '" + economy + "'";
		write();
		int keyloc = tne.indexOf(name + ":" + economy);
		tstartprice.set(keyloc, newstartprice);
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
						hc.buildData();
						cancelLock();
					}
				}
			}, 40L, 1L);
		}
	}
	
	private void cancelLock() {
		hc.getServer().getScheduler().cancelTask(sqllockthreadid);
		hc.getServer().getScheduler().scheduleSyncDelayedTask(hc, new Runnable() {
			public void run() {
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
		playerecon.clear();
		econplayer.clear();
		
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
		playerecon = getStringColumn("SELECT ECONOMY FROM hyperplayers");
		econplayer = getStringColumn("SELECT PLAYER FROM hyperplayers");
		
		for (int c = 0; c < tname.size(); c++) {
			tne.add(tname.get(c) + ":" + teconomy.get(c));
		}			
	}
	

	
	public ArrayList<String> getKeys() {
		return tne;
	}
	
	
	
	private String getString(String name, String economy, String type) {
		type = type.toUpperCase();
		String data = "";
		try {
			Connection connect = DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + database, username, password);
			Statement state = connect.createStatement();	
			ResultSet result = state.executeQuery("SELECT " + type + " FROM hyperobjects WHERE NAME = " + "'" + name + "'" + " AND ECONOMY = " + "'" + economy + "'");
			int r = 0;
			while (result.next()) {
				data = result.getString(1);
				r++;
			}
			if (r > 1) {
				Bukkit.broadcast(ChatColor.DARK_RED + "HyperConomy ERROR.  SQLFunctions Error.", "hyperconomy.error");
			}
            result.close();
            state.close();
            connect.close();
            return data;
		} catch (SQLException e) {
			Bukkit.broadcast(ChatColor.RED + "SQL connection failed.  Check your config settings.", "actionzones.admin");
			e.printStackTrace();
			return "error";
		}
	}
	
	private Double getDouble(String name, String economy, String type) {
		type = type.toUpperCase();
		Double data = 0.0;
		try {
			Connection connect = DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + database, username, password);
			Statement state = connect.createStatement();	
			ResultSet result = state.executeQuery("SELECT " + type + " FROM hyperobjects WHERE NAME = " + "'" + name + "'" + " AND ECONOMY = " + "'" + economy + "'");
			int r = 0;
			while (result.next()) {
				data = result.getDouble(1);
				r++;
			}
			if (r > 1) {
				Bukkit.broadcast(ChatColor.DARK_RED + "HyperConomy ERROR.  SQLFunctions Error.", "hyperconomy.error");
			}
            result.close();
            state.close();
            connect.close();
            return data;
		} catch (SQLException e) {
			Bukkit.broadcast(ChatColor.RED + "SQL connection failed.  Check your config settings.", "actionzones.admin");
			e.printStackTrace();
			return 0.0;
		}
	}
	
	private int getInt(String name, String economy, String type) {
		type = type.toUpperCase();
		int data = 0;
		try {
			Connection connect = DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + database, username, password);
			Statement state = connect.createStatement();	
			ResultSet result = state.executeQuery("SELECT " + type + " FROM hyperobjects WHERE NAME = " + "'" + name + "'" + " AND ECONOMY = " + "'" + economy + "'");
			int r = 0;
			while (result.next()) {
				data = result.getInt(1);
				r++;
			}
			if (r > 1) {
				Bukkit.broadcast(ChatColor.DARK_RED + "HyperConomy ERROR.  SQLFunctions Error.", "hyperconomy.error");
			}
            result.close();
            state.close();
            connect.close();
            return data;
		} catch (SQLException e) {
			Bukkit.broadcast(ChatColor.RED + "SQL connection failed.  Check your config settings.", "actionzones.admin");
			e.printStackTrace();
			return 0;
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	//make next 3 private again later
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
			Bukkit.broadcast(ChatColor.RED + "SQL connection failed.  Check your config settings.", "actionzones.admin");
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
			Bukkit.broadcast(ChatColor.RED + "SQL connection failed.  Check your config settings.", "actionzones.admin");
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
			Bukkit.broadcast(ChatColor.RED + "SQL connection failed.  Check your config settings.", "actionzones.admin");
			e.printStackTrace();
			return data;
		}
	}
	

	
	public String getPlayerEconomy(String player) {
		try {
			player = player.toLowerCase();
			if (econplayer.indexOf(player) == -1) {
				addPlayerEconomy(player, "default");
				setPlayerEconomy(player, "default");
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
			Bukkit.broadcast(ChatColor.RED + "SQL connection failed.  Check your config settings.", "actionzones.admin");
			e.printStackTrace();
			return 0;
		}
	}

	
	

}
