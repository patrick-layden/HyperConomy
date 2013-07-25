package regalowl.hyperconomy;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.scheduler.BukkitTask;

public class MySQLConnection implements DatabaseConnection {



	
	private HyperConomy hc;
	private String username;
	private String password;
	private int port;
	private String host;
	private String database;

	private Connection connection;
	private CopyOnWriteArrayList<String> statements = new CopyOnWriteArrayList<String>();
	private String currentStatement;
	private BukkitTask writeTask;
	private PreparedStatement preparedStatement;
	
	private boolean inUse;
	
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
	
	
	public void write(List<String> sql) {
		inUse = true;
		statements.clear();
		for (String csql:sql) {
			statements.add(csql);
		}
		writeTask = hc.getServer().getScheduler().runTaskAsynchronously(hc, new Runnable() {
			public void run() {
				try {
					if (connection == null || connection.isClosed()) {
						openConnection();
					}
					connection.setAutoCommit(false);
					for (String statement:statements) {
						currentStatement = statement;
						preparedStatement = connection.prepareStatement(currentStatement);
						preparedStatement.executeUpdate();
					}
					connection.commit();
					statements.clear();
					inUse = false;
				} catch (SQLException e) {
					try {
						connection.rollback();
						new HyperError(e, "SQL write failed.  The failing SQL statement is in the following brackets: [" + currentStatement + "]");
					} catch (SQLException e1) {
						new HyperError(e, "Rollback failed.  Cannot recover.");
						return;
					}
					statements.remove(currentStatement);
					hc.getSQLWrite().executeSQL(statements);
					statements.clear();
					inUse = false;
				}
			}
		});
	}
	
	public void syncWrite(List<String> sql) {
		inUse = true;
		statements.clear();
		for (String csql:sql) {
			statements.add(csql);
		}
		try {
			if (connection == null || connection.isClosed()) {
				openConnection();
			}
			connection.setAutoCommit(false);
			for (String statement:statements) {
				currentStatement = statement;
				preparedStatement = connection.prepareStatement(currentStatement);
				preparedStatement.executeUpdate();
			}
			connection.commit();
			statements.clear();
			inUse = false;
		} catch (SQLException e) {
			try {
				connection.rollback();
				new HyperError(e, "SQL write failed.  The failing SQL statement is in the following brackets: [" + currentStatement + "]");
			} catch (SQLException e1) {
				new HyperError(e, "Rollback failed.  Cannot recover.");
				return;
			}
			statements.remove(currentStatement);
			hc.getSQLWrite().executeSQL(statements);
			statements.clear();
			inUse = false;
		}
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
	
	
	
	private void openConnection() {
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

	
	public List<String> closeConnection() {
		if (writeTask != null) {
			writeTask.cancel();
			if (inUse) {
				try {
					connection.rollback();
				} catch (SQLException e) {
					new HyperError(e);
				}
			}
		}
		try {
			connection.close();
		} catch (SQLException e) {}
		if (!inUse) {
			statements.clear();
		}
		return statements;
	}
	
	public boolean inUse() {
		return inUse;
	}
	
}