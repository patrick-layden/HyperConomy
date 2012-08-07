package regalowl.hyperconomy;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import org.bukkit.configuration.file.FileConfiguration;

public class ShutdownThread {

	private SQLShutdown sqs;
	
	ShutdownThread(HyperConomy hc, SQLShutdown sqsh, ArrayList<String> statements) {
		sqs = sqsh;
		FileConfiguration config = hc.getYaml().getConfig();
		String username = config.getString("config.sql-connection.username");
		String password = config.getString("config.sql-connection.password");
		int port = config.getInt("config.sql-connection.port");
		String host = config.getString("config.sql-connection.host");
		String database = config.getString("config.sql-connection.database");

		try {
			Connection connection = DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + database, username, password);
			Statement state = connection.createStatement();
			int counter = 0;
			while (counter < statements.size()) {
				state.execute(statements.get(counter));
				sqs.statementComplete();
				counter++;
			}
		    state.close();
		    connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	
	
	
}
