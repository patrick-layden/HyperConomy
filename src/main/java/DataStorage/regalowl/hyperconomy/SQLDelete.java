package regalowl.hyperconomy;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;

public class SQLDelete {


	
	public void removeTable(HyperConomy hc, String table) {

		
		FileConfiguration config = hc.getYaml().getConfig();
		String username = config.getString("config.sql-connection.username");
		String password = config.getString("config.sql-connection.password");
		int port = config.getInt("config.sql-connection.port");
		String host = config.getString("config.sql-connection.host");
		String database = config.getString("config.sql-connection.database");
		
		try {
			Connection connect = DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + database, username, password);
			Statement state = connect.createStatement();	
			state.executeUpdate("DROP TABLE " + table);
            state.close();
            connect.close();
		} catch (SQLException e) {
			Bukkit.broadcast(ChatColor.RED + "SQL connection failed.  Check your config settings.", "hyperconomy.admin");
			e.printStackTrace();
			return;
		}
	}
	
}

