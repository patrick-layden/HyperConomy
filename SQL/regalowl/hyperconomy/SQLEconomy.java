package regalowl.hyperconomy;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Iterator;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

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
	
	public void checkDatabase() {
        try {
			Connection connect = DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + database, username, password);
			Statement state = connect.createStatement();
			
			
			state.execute("CREATE TABLE IF NOT EXISTS hyperobjects (NAME TINYTEXT, ECONOMY TINYTEXT, TYPE TINYTEXT, CATEGORY TINYTEXT, MATERIAL TINYTEXT, ID INT, DATA INT, " +
					"DURABILITY INT, VALUE DOUBLE, STATIC TINYTEXT, STATICPRICE DOUBLE, STOCK DOUBLE, MEDIAN DOUBLE, INITIATION TINYTEXT, STARTPRICE DOUBLE)");
			state.execute("CREATE TABLE IF NOT EXISTS hyperplayers (PLAYER TINYTEXT, ECONOMY TINYTEXT)");
			state.execute("CREATE TABLE IF NOT EXISTS hyperlog (ID INT NOT NULL AUTO_INCREMENT, TIME DATETIME, CUSTOMER TINYTEXT, ACTION TINYTEXT, OBJECT TINYTEXT, AMOUNT DOUBLE, MONEY DOUBLE, TAX DOUBLE, STORE TINYTEXT, TYPE TINYTEXT, PRIMARY KEY (ID))");
			state.execute("CREATE TABLE IF NOT EXISTS hyperhistory (ID INT NOT NULL AUTO_INCREMENT, TIME DATETIME, CUSTOMER TINYTEXT, ACTION TINYTEXT, OBJECT TINYTEXT, AMOUNT DOUBLE, MONEY DOUBLE, TAX DOUBLE, STORE TINYTEXT, TYPE TINYTEXT, PRIMARY KEY (ID))");
			ArrayList<String> testdata = new ArrayList<String>();
			testdata = hc.getSQLFunctions().getStringColumn("SELECT NAME FROM hyperobjects WHERE ECONOMY='default'");
			if (testdata.size() == 0) {
				migrate();
			}
			

            state.close();
            connect.close();
		} catch (SQLException e) {
			Bukkit.broadcast(ChatColor.RED + "SQL connection failed.  Check your config settings.", "actionzones.admin");
			e.printStackTrace();
			return;
		}
	}
	
	public void migrate() {

		FileConfiguration itemsyaml = hc.getYaml().getItems();
		FileConfiguration enchantsyaml = hc.getYaml().getEnchants();
		ArrayList<String> statements = new ArrayList<String>();
		Iterator<String> it = itemsyaml.getKeys(false).iterator();
		while (it.hasNext()) {  
			String itemname = it.next().toString();
			if (!itemname.equalsIgnoreCase("xp")) {
				statements.add("Insert Into hyperobjects (NAME, ECONOMY, TYPE, CATEGORY, MATERIAL, ID, DATA, DURABILITY, VALUE, STATIC, STATICPRICE, STOCK, MEDIAN, INITIATION, STARTPRICE)"
			            + " Values ('" + itemname + "','" + "default" + "','"
			        + "item" + "','" + "unknown" + "','" + itemsyaml.getString(itemname + ".information.material") + "','" + itemsyaml.getInt(itemname + ".information.id") + "','" + itemsyaml.getInt(itemname + ".information.data") + 
			        "','" + itemsyaml.getInt(itemname + ".information.data") + "','" + itemsyaml.getDouble(itemname + ".value") + "','" + 
			        itemsyaml.getString(itemname + ".price.static") + "','" + itemsyaml.getDouble(itemname + ".price.staticprice") + "','" + 
			        itemsyaml.getDouble(itemname + ".stock.stock") + "','" + itemsyaml.getDouble(itemname + ".stock.median") + "','" + 
			        itemsyaml.getString(itemname + ".initiation.initiation") + "','" + itemsyaml.getDouble(itemname + ".initiation.startprice") + "')");
			} else {
				statements.add("Insert Into hyperobjects (NAME, ECONOMY, TYPE, CATEGORY, MATERIAL, ID, DATA, DURABILITY, VALUE, STATIC, STATICPRICE, STOCK, MEDIAN, INITIATION, STARTPRICE)"
			            + " Values ('" + itemname + "','" + "default" + "','"
			        + "experience" + "','" + "unknown" + "','" + "none" + "','" + itemsyaml.getInt(itemname + ".information.id") + "','" + itemsyaml.getInt(itemname + ".information.data") + 
			        "','" + itemsyaml.getInt(itemname + ".information.data") + "','" + itemsyaml.getDouble(itemname + ".value") + "','" + 
			        itemsyaml.getString(itemname + ".price.static") + "','" + itemsyaml.getDouble(itemname + ".price.staticprice") + "','" + 
			        itemsyaml.getDouble(itemname + ".stock.stock") + "','" + itemsyaml.getDouble(itemname + ".stock.median") + "','" + 
			        itemsyaml.getString(itemname + ".initiation.initiation") + "','" + itemsyaml.getDouble(itemname + ".initiation.startprice") + "')");
			}

		}  
		Iterator<String> it2 = enchantsyaml.getKeys(false).iterator();
		while (it2.hasNext()) {  
			String ename = it2.next().toString();
			statements.add("Insert Into hyperobjects (NAME, ECONOMY, TYPE, CATEGORY, MATERIAL, ID, DATA, DURABILITY, VALUE, STATIC, STATICPRICE, STOCK, MEDIAN, INITIATION, STARTPRICE)"
		            + " Values ('" + ename + "','" + "default" + "','"
		        + "enchantment" + "','" + "unknown" + "','" + enchantsyaml.getString(ename + ".information.name") + "','" + enchantsyaml.getInt(ename + ".information.id") + "','" + "-1" + 
		        "','" + "-1" + "','" + enchantsyaml.getDouble(ename + ".value") + "','" + 
		        enchantsyaml.getString(ename + ".price.static") + "','" + enchantsyaml.getDouble(ename + ".price.staticprice") + "','" + 
		        enchantsyaml.getDouble(ename + ".stock.stock") + "','" + enchantsyaml.getDouble(ename + ".stock.median") + "','" + 
		        enchantsyaml.getString(ename + ".initiation.initiation") + "','" + enchantsyaml.getDouble(ename + ".initiation.startprice") + "')");
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
			statements.add("Insert Into hyperobjects (NAME, ECONOMY, TYPE, CATEGORY, MATERIAL, ID, DATA, DURABILITY, VALUE, STATIC, STATICPRICE, STOCK, MEDIAN, INITIATION, STARTPRICE)"
		            + " Values ('" + c + "','" + economy + "','"
		        + type + "','" + sf.getCategory(c, "default") + "','" + sf.getMaterial(c, "default") + "','" + sf.getId(c, "default") + "','" + sf.getData(c, "default") + 
		        "','" + sf.getDurability(c, "default") + "','" + sf.getValue(c, "default") + "','" + 
		        sf.getStatic(c, "default") + "','" + sf.getStaticPrice(c, "default") + "','" + 
		        0.0 + "','" + sf.getMedian(c, "default") + "','" + 
		        "true" + "','" + sf.getStartPrice(c, "default") + "')");
		}
		
		for (int i = 0; i < enchants.size(); i++) {
			String type = "enchantment";
			String c = enchants.get(i);
			statements.add("Insert Into hyperobjects (NAME, ECONOMY, TYPE, CATEGORY, MATERIAL, ID, DATA, DURABILITY, VALUE, STATIC, STATICPRICE, STOCK, MEDIAN, INITIATION, STARTPRICE)"
		            + " Values ('" + c + "','" + economy + "','"
		        + type + "','" + sf.getCategory(c, "default") + "','" + sf.getMaterial(c, "default") + "','" + sf.getId(c, "default") + "','" + sf.getData(c, "default") + 
		        "','" + sf.getDurability(c, "default") + "','" + sf.getValue(c, "default") + "','" + 
		        sf.getStatic(c, "default") + "','" + sf.getStaticPrice(c, "default") + "','" + 
		        0.0 + "','" + sf.getMedian(c, "default") + "','" + 
		        "true" + "','" + sf.getStartPrice(c, "default") + "')");
		}
		
		
		SQLWrite sw = hc.getSQLWrite();
		sw.writeData(statements);
		sf.load();
	}
	
	public void deleteEconomy(String economy) {
		hc.getSQLWrite().writeData("DELETE FROM hyperobjects WHERE ECONOMY='" + economy + "'");
	}
	
	public boolean exists(String economy) {
		SQLFunctions sf = hc.getSQLFunctions();
		ArrayList<String> test = sf.getStringColumn("SELECT NAME FROM hyperobjects WHERE ECONOMY='" + economy + "'" );
		if (test.size() == 0) {
			return false;
		} else {
			return true;
		}
	}
	
}
