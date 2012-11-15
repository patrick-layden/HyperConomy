package regalowl.hyperconomy;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

public class SQLUtils {
	
	public boolean fieldExists(String host, int port, String database, String username, String password, String table, String field) {
		
		try {
			Connection connect = DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + database, username, password);
			Statement state = connect.createStatement();	
			String statement = "SELECT * FROM " + table;
			ResultSet result = state.executeQuery(statement);
			
			ResultSetMetaData meta = result.getMetaData();
			int nCols = meta.getColumnCount();
			for (int i = 1; i < nCols + 1; i++) {
			    if (meta.getColumnName(i).equalsIgnoreCase(field)) {
			        result.close();
			        state.close();
			        connect.close();
			    	return true;
			    }
	
			}
	        result.close();
	        state.close();
	        connect.close();
			return false;
		} catch (Exception e) {
			Bukkit.broadcast(ChatColor.RED + "SQL connection failed.  Check your config settings.", "hyperconomy.error");
			e.printStackTrace();
			return true;
		}

	}
	
	
	public void executeSQL(String host, int port, String database, String username, String password, String statement) {
		
		try {
			Connection connect = DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + database, username, password);
			Statement state = connect.createStatement();	
			state.execute(statement);
	        state.close();
	        connect.close();
		} catch (Exception e) {
			Bukkit.broadcast(ChatColor.RED + "SQL connection failed.  Check your config settings.", "hyperconomy.error");
			e.printStackTrace();
		}

	}
	
	
	
	
	

}
