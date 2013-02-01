package regalowl.hyperconomy;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.scheduler.BukkitTask;

public class DatabaseConnection {
	
	private DatabaseConnection dc;
	
	private HyperConomy hc;
	private String username;
	private String password;
	private int port;
	private String host;
	private String database;
	
	
	private String sqlitePath;
	private Connection connection;
	private String statement;
	private boolean logErrors;
	private int sqlRetryCount;
	private int connectionRetryCount;
	private SQLWrite sw;
	
	private BukkitTask writeTask;
	private BukkitTask retryWriteTask;
	private BukkitTask retryConnectTask;
	
	DatabaseConnection(SQLWrite sw) {
		this.sw = sw;
		dc = this;
		hc = HyperConomy.hc;
		sw = hc.getSQLWrite();
		FileConfiguration config = hc.getYaml().getConfig();
		username = config.getString("config.sql-connection.username");
		password = config.getString("config.sql-connection.password");
		port = config.getInt("config.sql-connection.port");
		host = config.getString("config.sql-connection.host");
		database = config.getString("config.sql-connection.database");
		FileTools ft = new FileTools();
		sqlitePath = ft.getJarPath() + File.separator + "plugins" + File.separator + "HyperConomy" + File.separator + "HyperConomy.db";
		openConnection();
		logErrors = hc.getYaml().getConfig().getBoolean("config.log-sqlwrite-errors");
		sqlRetryCount = 0;
		connectionRetryCount = 0;
	}
	
	
	public void write(String statement) {
		this.statement = statement;
		writeThread();
	}
	
	
	
	private void openConnection() {
		try {
			connection = null;
			if (hc.useMySQL()) {
				connection = DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + database, username, password);
			} else {
				Class.forName("org.sqlite.JDBC");
				connection = DriverManager.getConnection("jdbc:sqlite:" + sqlitePath);
			}
		} catch (Exception e) {
			connectionRetryCount++;
			if (connectionRetryCount < 4) {
				retryConnection(100L);
			} else {
				new HyperError(e, "Fatal database connection error.  Attempted to connect to database 5 times unsuccessfully.");
				return;
			}
		}
	}
	
	private void retryConnection(long wait) {
		retryConnectTask = hc.getServer().getScheduler().runTaskLaterAsynchronously(hc, new Runnable() {
			public void run() {
				openConnection();
			}
		}, wait);
	}
	
	private void refreshConnection() {
		openConnection();
	}
	

	private void writeThread() {
		writeTask = hc.getServer().getScheduler().runTaskAsynchronously(hc, new Runnable() {
			public void run() {
				try {
					if (connection == null || connection.isClosed()) {
						refreshConnection();
					}
					Statement state = connection.createStatement();
					state.execute(statement);
					state.close();
					statement = null;
					returnConnection();
				} catch (SQLException e) {
					sqlRetryCount++;
					if (sqlRetryCount == 0) {
						scheduleRetry(20L);
					} else if (sqlRetryCount == 1) {
						scheduleRetry(60L);
					} else {
						if (logErrors) {
							new HyperError(e, "3 attempts have been made to write to the database.  The failing SQL statement is in the following brackets: [" + statement + "]");
						}
						sqlRetryCount = 0;
						statement = null;
						returnConnection();
					}
				}
			}
		});
	}

	private void scheduleRetry(long wait) {
		retryWriteTask = hc.getServer().getScheduler().runTaskLaterAsynchronously(hc, new Runnable() {
			public void run() {
				writeThread();
			}
		}, wait);
	}
	
	private void returnConnection() {
		sw.returnConnection(dc);
	}
	
	
	
	public String closeConnection() {
		writeTask.cancel();
		retryConnectTask.cancel();
		retryWriteTask.cancel();
		try {
			connection.close();
		} catch (SQLException e) {
		}
		return statement;
	}
}
