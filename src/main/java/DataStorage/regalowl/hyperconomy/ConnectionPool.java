package regalowl.hyperconomy;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;

public class ConnectionPool {
	
	private int maxConnections;
	private HyperConomy hc;
	private SQLWrite sw;
	private SQLFunctions sf;
	
	private ArrayList<Connection> connections = new ArrayList<Connection>();
	private ArrayList<Boolean> inUse = new ArrayList<Boolean>();
	
	
	ConnectionPool(HyperConomy hyc, SQLWrite sqw, int maxconnections) {
		
		sw = sqw;
		hc = hyc;
		maxConnections = maxconnections;
		sf = hc.getSQLFunctions();
		openConnections();
		
		hc.getServer().getScheduler().scheduleAsyncRepeatingTask(hc, new Runnable() {
			public void run() {
				refreshConnections();
			}
		}, 12000L, 12000L);
		
	}
	
	
	private void refreshConnections() {
		sw.pauseWrite(1200L);
		hc.getServer().getScheduler().scheduleAsyncDelayedTask(hc, new Runnable() {
			public void run() {
				closeConnections();
				openConnections();
			}
		}, 1000L);
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
	
	
	
	public void openConnections() {
		String username = sf.getUserName();
		String password = sf.getPassword();
		int port = sf.getPort();
		String host = sf.getHost();
		String database = sf.getDatabase();
		
		for (int i = 0; i < maxConnections; i++) {
			try {
				Connection connect = DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + database, username, password);
				connections.add(connect);
				inUse.add(false);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	
	public void closeConnections() {
		for (int i = 0; i < connections.size(); i++) {
			try {
				connections.get(i).close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		connections.clear();
		inUse.clear();
	}
	

}