package regalowl.hyperconomy;


import java.sql.DriverManager;
import org.bukkit.configuration.file.FileConfiguration;

public class MySQLConnection extends DatabaseConnection {
	
	private String username;
	private String password;
	private int port;
	private String host;
	private String database;
	
	MySQLConnection() {
		hc = HyperConomy.hc;
		FileConfiguration config = hc.getYaml().getConfig();
		username = config.getString("config.sql-connection.username");
		password = config.getString("config.sql-connection.password");
		port = config.getInt("config.sql-connection.port");
		host = config.getString("config.sql-connection.host");
		database = config.getString("config.sql-connection.database");
		openConnection();
		inUse = false;
	}

	protected void openConnection() {
		try {
			connection = DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + database, username, password);
		} catch (Exception e) {
			try {
				connection = DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + database, username, password);
			} catch (Exception e2) {
				new HyperError(e, "Fatal database connection error.");
				return;
			}
		}
	}

	
}