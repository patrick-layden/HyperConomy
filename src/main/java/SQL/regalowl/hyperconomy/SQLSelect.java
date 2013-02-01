package regalowl.hyperconomy;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import org.bukkit.configuration.file.FileConfiguration;

public class SQLSelect {
	
	private HyperConomy hc;
	private String username;
	private String password;
	private int port;
	private String host;
	private String database;
	private Connection connection;

	SQLSelect() {
		hc = HyperConomy.hc;
		FileConfiguration config = hc.getYaml().getConfig();
		username = config.getString("config.sql-connection.username");
		password = config.getString("config.sql-connection.password");
		port = config.getInt("config.sql-connection.port");
		host = config.getString("config.sql-connection.host");
		database = config.getString("config.sql-connection.database");
		FileTools ft = new FileTools();
		String sqlitePath = ft.getJarPath() + File.separator + "plugins" + File.separator + "HyperConomy" + File.separator + "HyperConomy.db";

		try {
			connection = null;
			if (hc.useMySQL()) {
				connection = DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + database, username, password);
			} else {
				Class.forName("org.sqlite.JDBC");
				connection = DriverManager.getConnection("jdbc:sqlite:" + sqlitePath);
			}
		} catch (Exception e) {
			new HyperError(e);
			return;

		}
	}

	
	
	public ArrayList<String> getStringColumn(String statement) {
		ArrayList<String> data = new ArrayList<String>();
		try {
			Statement state = connection.createStatement();
			ResultSet result = state.executeQuery(statement);
			while (result.next()) {
				data.add(result.getString(1));
			}
			result.close();
			state.close();
			return data;
		} catch (SQLException e) {
			new HyperError(e);
			return data;
		}
	}

	public ArrayList<Double> getDoubleColumn(String statement) {
		ArrayList<Double> data = new ArrayList<Double>();
		try {
			Statement state = connection.createStatement();
			ResultSet result = state.executeQuery(statement);
			while (result.next()) {
				data.add(result.getDouble(1));
			}
			result.close();
			state.close();
			return data;
		} catch (SQLException e) {
			new HyperError(e);
			return data;
		}
	}

	public ArrayList<Integer> getIntColumn(String statement) {
		ArrayList<Integer> data = new ArrayList<Integer>();
		try {
			Statement state = connection.createStatement();
			ResultSet result = state.executeQuery(statement);
			while (result.next()) {
				data.add(result.getInt(1));
			}
			result.close();
			state.close();
			return data;
		} catch (SQLException e) {
			new HyperError(e);
			return data;
		}
	}
	
	
	public int getInt(String statement) {
		try {
			Statement state = connection.createStatement();
			ResultSet result = state.executeQuery(statement);
			int data = 0;
			if (result.next()) {
				data = result.getInt(1);
			}
			result.close();
			state.close();
			return data;
		} catch (SQLException e) {
			new HyperError(e);
			return 0;
		}
	}
	
	
	public String getSettingValue(String setting) {
		String value = null;
		try {
			Statement state = connection.createStatement();
			ResultSet result = state.executeQuery("SELECT VALUE FROM hyperconomy_settings WHERE SETTING = '" + setting + "'");
			if (result.next()) {
				value = result.getString("VALUE");
			}
			result.close();
			state.close();
			return value;
		} catch (SQLException e) {
			new HyperError(e);
			return value;
		}
	}
	
	
	public int countTableEntries(String table) {
		try {
			Statement statement = connection.createStatement();
			ResultSet result = statement.executeQuery("SELECT COUNT(*) FROM " + table);
			result.next();
			int rowcount = result.getInt(1);
			result.close();
			statement.close();
			return rowcount;
		} catch (SQLException e) {
			new HyperError(e);
			return 0;
		}
	}
	
	public void closeConnection() {
		try {
			connection.close();
		} catch (SQLException e) {

		}
	}
	
	public Connection getConnection() {
		return connection;
	}
	
	
	public double getHistoricValue(String name, String economy, int count) {
		count -= 1;
		ArrayList<Double> data = new ArrayList<Double>();
		try {
			Statement statement = connection.createStatement();
			ResultSet result = statement.executeQuery("SELECT PRICE FROM hyperconomy_history WHERE OBJECT = '" + name + "' AND ECONOMY = '" + economy + "' ORDER BY TIME DESC");
			while (result.next()) {
				data.add(Double.parseDouble(result.getString("PRICE")));
			}
			result.close();
			statement.close();
			if (count < data.size()) {
				return data.get(count);
			} else {
				return -1.0;
			}
		} catch (SQLException e) {
			new HyperError(e);
			return 0;
		}
	}
	
	
	public boolean fieldExists(String table, String field) {
		
		try {
			Statement state = connection.createStatement();	
			String statement = "SELECT * FROM " + table;
			ResultSet result = state.executeQuery(statement);
			
			ResultSetMetaData meta = result.getMetaData();
			int nCols = meta.getColumnCount();
			for (int i = 1; i < nCols + 1; i++) {
			    if (meta.getColumnName(i).equalsIgnoreCase(field)) {
			        result.close();
			        state.close();
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
