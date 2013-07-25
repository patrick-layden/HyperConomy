package regalowl.hyperconomy;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import org.bukkit.scheduler.BukkitTask;

public class SQLiteConnection implements DatabaseConnection {


	
	private HyperConomy hc;
	private String sqlitePath;
	private Connection connection;
	private CopyOnWriteArrayList<String> statements = new CopyOnWriteArrayList<String>();
	private BukkitTask writeTask;
	private String currentStatement;
	private PreparedStatement preparedStatement;
	
	private boolean inUse;
	
	
	SQLiteConnection() {
		hc = HyperConomy.hc;
		FileTools ft = new FileTools();
		sqlitePath = ft.getJarPath() + File.separator + "plugins" + File.separator + "HyperConomy" + File.separator + "HyperConomy.db";
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
