package regalowl.hyperconomy;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;

import org.bukkit.scheduler.BukkitTask;

public class SQLiteConnection extends DatabaseConnection {


	
	private HyperConomy hc;
	private String sqlitePath;
	private Connection connection;
	private String statement;
	private boolean logErrors;
	private int sqlRetryCount;
	
	private BukkitTask writeTask;
	private BukkitTask retryWriteTask;
	
	private boolean inUse;
	
	
	SQLiteConnection() {
		hc = HyperConomy.hc;
		FileTools ft = new FileTools();
		sqlitePath = ft.getJarPath() + File.separator + "plugins" + File.separator + "HyperConomy" + File.separator + "HyperConomy.db";
		openConnection();
		logErrors = hc.getYaml().getConfig().getBoolean("config.log-sqlwrite-errors");
		sqlRetryCount = 0;
		inUse = false;
	}
	
	
	public void write(String statement) {
		inUse = true;
		this.statement = statement;
		writeThread();
	}
	
	
	/**
	 * This function should be run asynchronously to prevent slowing the main thread.
	 * @param statement
	 * @return QueryResult
	 */
	public QueryResult read(String statement) {
		QueryResult qr = new QueryResult();
		try {
			if (connection == null || connection.isClosed()) {
				openConnection();
			}
			Statement state = connection.createStatement();
			ResultSet resultSet = state.executeQuery(statement);
			ResultSetMetaData rsmd = resultSet.getMetaData();
			int columnCount = rsmd.getColumnCount();
			for (int i = 1; i <= columnCount; i++) {
				qr.addColumnName(rsmd.getColumnLabel(i));
			}
			while (resultSet.next()) {
				for (int i = 1; i <= columnCount; i++) {
					qr.addData(i, resultSet.getString(i));
				}
			}
			resultSet.close();
			state.close();
			statement = null;
			if (hc.getSQLRead() != null) {
				hc.getSQLRead().returnConnection(this);
			}
			return qr;
		} catch (SQLException e) {
			new HyperError(e, "The failed SQL statement is in the following brackets: [" + statement + "]");
			if (hc.getSQLRead() != null) {
				hc.getSQLRead().returnConnection(this);
			}
			return qr;
		}
	}
	
	
	protected void openConnection() {
		try {
			Class.forName("org.sqlite.JDBC");
			connection = DriverManager.getConnection("jdbc:sqlite:" + sqlitePath);
		} catch (Exception e) {
			try {
				Class.forName("org.sqlite.JDBC");
				connection = DriverManager.getConnection("jdbc:sqlite:" + sqlitePath);
			} catch (Exception e2) {
				new HyperError(e, "Fatal database connection error.");
				return;
			}
		}
	}
	
	
	


	protected void writeThread() {
		writeTask = hc.getServer().getScheduler().runTaskAsynchronously(hc, new Runnable() {
			public void run() {
				try {
					if (connection == null || connection.isClosed()) {
						openConnection();
					}
					Statement state = connection.createStatement();
					state.execute(statement);
					state.close();
					statement = null;
					inUse = false;
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
						inUse = false;
					}
				}
			}
		});
	}

	protected void scheduleRetry(long wait) {
		retryWriteTask = hc.getServer().getScheduler().runTaskLaterAsynchronously(hc, new Runnable() {
			public void run() {
				writeThread();
			}
		}, wait);
	}
	

	public String closeConnection() {
		inUse = true;
		if (writeTask != null) {
			writeTask.cancel();
		}
		if (retryWriteTask != null) {
			retryWriteTask.cancel();
		}
		try {
			connection.close();
		} catch (SQLException e) {
		}
		return statement;
	}
	
	public boolean inUse() {
		return inUse;
	}
}
