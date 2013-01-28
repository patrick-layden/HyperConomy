package regalowl.hyperconomy;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;

public class ConnectionPool {
	
	private int maxConnections;
	private HyperConomy hc;
	private SQLWrite sw;
	private DataFunctions sf;
	
	private ArrayList<Connection> connections = new ArrayList<Connection>();
	private ArrayList<Boolean> inUse = new ArrayList<Boolean>();
	
	private String sqlitePath;
	
	
	@SuppressWarnings("deprecation")
	ConnectionPool(HyperConomy hyc, SQLWrite sqw, int maxconnections) {
		
		sw = sqw;
		hc = hyc;
		maxConnections = maxconnections;
		sf = hc.getDataFunctions();
		FileTools ft = new FileTools();
		sqlitePath = ft.getJarPath() + File.separator + "plugins" + File.separator + "HyperConomy" + File.separator + "HyperConomy.db";
		
		
		openConnections();
		
		
		hc.getServer().getScheduler().scheduleAsyncRepeatingTask(hc, new Runnable() {
			public void run() {
				refreshConnections();
			}
		}, 12000L, 12000L);
		
	}
	

	private void refreshConnections() {
		sw.pauseWrite(1200L);
		closeConnections();
		openConnections();
	}
	
	
	
	public Connection getConnection() {
		int i = 0;
		if (inUse != null && connections != null) {
			while (i < maxConnections) {
				if (!inUse.get(i)) {
					inUse.set(i, true);
					return connections.get(i);
				}
				i++;
			}
		}
		return null;
	}
	
	
	public void returnConnection(Connection connection) {
		if (connections.contains(connection)) {
			inUse.set(connections.indexOf(connection), false);
		}
	}
	
	
	
	@SuppressWarnings("deprecation")
	public void openConnections() {
		for (int i = 0; i < maxConnections; i++) {
				hc.getServer().getScheduler().scheduleAsyncDelayedTask(hc, new Runnable() {
					public void run() {
						try {
							Connection connect = null;
							if (hc.useMySQL()) {
								connect = DriverManager.getConnection("jdbc:mysql://" + sf.getHost() + ":" + sf.getPort() + "/" + sf.getDatabase(), sf.getUserName(), sf.getPassword());
							} else {
								Class.forName("org.sqlite.JDBC");
								connect = DriverManager.getConnection("jdbc:sqlite:" + sqlitePath);
							}
							connections.add(connect);
							inUse.add(false);
						} catch (Exception e) {
							new HyperError(e);
							refreshConnections();
							return;
						}
					}
				}, (i + 1));
		}
	}

	
	@SuppressWarnings("deprecation")
	public void closeConnections() {
		hc.getServer().getScheduler().scheduleAsyncDelayedTask(hc, new Runnable() {
			public void run() {
				for (int i = 0; i < connections.size(); i++) {
					try {
						connections.get(i).close();
					} catch (SQLException e) {
						new HyperError(e);
					}
				}
				connections.clear();
				inUse.clear();
			}
		}, 0);
	}
	
	
	public Connection getConnectionForRead() {
		try {
		if (hc.useMySQL()) {
			return DriverManager.getConnection("jdbc:mysql://" + sf.getHost() + ":" + sf.getPort() + "/" + sf.getDatabase(), sf.getUserName(), sf.getPassword());
		} else {
			Class.forName("org.sqlite.JDBC");
			return DriverManager.getConnection("jdbc:sqlite:" + sqlitePath);
		}
		} catch (Exception e) {
			new HyperError(e);
			return null;
		}
	}
	

}
