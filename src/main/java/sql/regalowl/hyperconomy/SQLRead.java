package regalowl.hyperconomy;


import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;


public class SQLRead {

	private HyperConomy hc;
	private int threadlimit;

    private Queue<DatabaseConnection> connections = new LinkedList<DatabaseConnection>();
    private Lock lock = new ReentrantLock();
    private Condition notFull = lock.newCondition();
    private Condition notEmpty = lock.newCondition();

    
	SQLRead() {
		hc = HyperConomy.hc;
		if (hc.s().gB("sql-connection.use-mysql")) {
			threadlimit = 3;
		} else {
			threadlimit = 1;
		}
		for (int i = 0; i < threadlimit; i++) {
			hc.getServer().getScheduler().runTaskLaterAsynchronously(hc, new Runnable() {
	    		public void run() {
	    			DatabaseConnection dc = null;
	    			if (hc.s().gB("sql-connection.use-mysql")) {
	    				dc = new MySQLConnection();
	    			} else {
		    			dc = new SQLiteConnection();
	    			}
	    			returnConnection(dc);
	    		}
	    	}, i);
		}
	}

	public void returnConnection(DatabaseConnection connection) {
		lock.lock();
		try {
			while (connections.size() == threadlimit) {
				try {
					notFull.await();
				} catch (InterruptedException e) {
					new HyperError(e);
				}
			}
			connections.add(connection);
			notEmpty.signal();
		} finally {
			lock.unlock();
		}
	}

	/**
	 * This method must be called from an asynchronous thread!
	 * @return DatabaseConnection
	 */
	public DatabaseConnection getDatabaseConnection() {
		lock.lock();
		try {
			while (connections.isEmpty()) {
				try {
					notEmpty.await();
				} catch (InterruptedException e) {
					new HyperError(e);
				}
			}
			DatabaseConnection connect = connections.remove();
			notFull.signal();
			return connect;
		} finally {
			lock.unlock();
		}
	}
    
	
	public ArrayList<String> getStringColumn(String statement) {
		ArrayList<String> data = new ArrayList<String>();
		QueryResult result = getDatabaseConnection().read(statement);
		while (result.next()) {
			data.add(result.getString(1));
		}
		result.close();
		return data;
	}

	public ArrayList<Double> getDoubleColumn(String statement) {
		ArrayList<Double> data = new ArrayList<Double>();
		QueryResult result = getDatabaseConnection().read(statement);
		while (result.next()) {
			data.add(result.getDouble(1));
		}
		result.close();
		return data;
	}

	public ArrayList<Integer> getIntColumn(String statement) {
		ArrayList<Integer> data = new ArrayList<Integer>();
		QueryResult result = getDatabaseConnection().read(statement);
		while (result.next()) {
			data.add(result.getInt(1));
		}
		result.close();
		return data;
	}
	
	
	public int getInt(String statement) {
		QueryResult result = getDatabaseConnection().read(statement);
		int data = 0;
		if (result.next()) {
			data = result.getInt(1);
		}
		result.close();
		return data;
	}
	
	
	public String getSettingValue(String setting) {
		String value = null;
		QueryResult result = getDatabaseConnection().read("SELECT VALUE FROM hyperconomy_settings WHERE SETTING = '" + setting + "'");
		if (result.next()) {
			value = result.getString("VALUE");
		}
		result.close();
		return value;
	}
	
	
	public int countTableEntries(String table) {
		QueryResult result = getDatabaseConnection().read("SELECT COUNT(*) FROM " + table);
		result.next();
		int rowcount = result.getInt(1);
		result.close();
		return rowcount;
	}
	
	

	
	public int getActiveReadConnections() {
		return (threadlimit - connections.size());
	}
	
	
	public void shutDown() {
		for (DatabaseConnection dc:connections) {
			dc.closeConnection();
		}
	}

}


