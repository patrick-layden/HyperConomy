package regalowl.hyperconomy;

import java.io.File;
import java.util.ArrayList;
import org.bukkit.scheduler.BukkitTask;


public class SQLWrite {

	private HyperConomy hc;
	private int threadlimit;
	private ArrayList<String> buffer = new ArrayList<String>();
	private boolean initialWrite;
	private BukkitTask writeTask;
	private boolean writeActive;
	private ArrayList<DatabaseConnection> dbConnections = new ArrayList<DatabaseConnection>();
	
	SQLWrite() {
		hc = HyperConomy.hc;
		if (hc.useMySQL()) {
			threadlimit = hc.getYaml().getConfig().getInt("config.sql-connection.max-sql-threads");
		} else {
			threadlimit = 1;
		}
		
		for (int i = 0; i < threadlimit; i++) {
			hc.getServer().getScheduler().runTaskLaterAsynchronously(hc, new Runnable() {
	    		public void run() {
	    			DatabaseConnection dc = null;
	    			if (hc.useMySQL()) {
	    				dc = new MySQLConnection();
	    			} else {
		    			dc = new SQLiteConnection();
	    			}
	    			dbConnections.add(dc);
	    		}
	    	}, i);
		}

		initialWrite = false;
		writeActive = false;
		
		ArrayList<String> sstatements = loadStatements();
		if (sstatements.size() > 0) {
			initialWrite = true;
			hc.lockHyperConomy(true);
			executeSQL(sstatements);
		}
	}
	
	private ArrayList<String> loadStatements() {
		FileTools ft = new FileTools();
		SerializeArrayList sal =  new SerializeArrayList();
		String path = ft.getJarPath() + File.separator + "plugins" + File.separator + "HyperConomy" + File.separator + "temp" + File.separator + "buffer.txt";
		if (ft.fileExists(path)) {
			String statements = ft.getStringFromFile(path);
			ft.deleteFile(path);
			return sal.stringToArrayA(statements);
		} else {
			ArrayList<String> empty = new ArrayList<String>();
			return empty;
		}
	}
	

	public void executeSQL(ArrayList<String> statements) {
		for (String statement:statements) {
			buffer.add(statement);
		}
		startWrite();
	}
	public void executeSQL(String statement) {
		buffer.add(statement);
		startWrite();
	}
	


	
	private void startWrite() {
		if (writeActive) {
			return;
		}
		writeActive = true;
		writeTask = hc.getServer().getScheduler().runTaskTimerAsynchronously(hc, new Runnable() {
    		public void run() {
    			for (int i = 0; i < threadlimit; i++) {
	    			for (DatabaseConnection dc:dbConnections) {
	    				if (!dc.inUse()) {
	    	    			if (buffer.size() == 0) {
	    	    				cancelWrite();
	    	    				return;
	    	    			}
	    	    			String statement = buffer.get(0);
	    	    			buffer.remove(statement);
	    	    			dc.write(statement);
	    				}
	    			}
    			}
    		}
    	}, 0L, 1L);
	}
	
	private void cancelWrite() {
		writeTask.cancel();
		writeActive = false;
		initialWrite = false;
	}
    
    


	public void addSetting(String setting, String value) {
		if (hc.useMySQL()) {
			executeSQL("INSERT INTO hyperconomy_settings (SETTING, VALUE, TIME) VALUES ('"+setting+"', '"+value+"', NOW() )");
		} else {
			executeSQL("INSERT INTO hyperconomy_settings (SETTING, VALUE, TIME) VALUES ('"+setting+"', '"+value+"', datetime('NOW', 'localtime'))");
		}
	}
	
	
	public void updateSetting(String setting, String value) {
		executeSQL("UPDATE hyperconomy_settings SET VALUE='" + value + "' WHERE SETTING = '" + setting + "'");
	}


	
	public int getBufferSize() {
		return buffer.size();
	}
	
	public int getActiveThreads() {
		int activeThreads = 0;
		for (DatabaseConnection dc:dbConnections) {
			if (dc.inUse()) {
				activeThreads++;
			}
		}
		return activeThreads;
	}
	
	public int getAvailableThreads() {
		int availableThreads = 0;
		for (DatabaseConnection dc:dbConnections) {
			if (!dc.inUse()) {
				availableThreads++;
			}
		}
		return availableThreads;
	}

	
	public ArrayList<String> getBuffer() {
		return buffer;
	}
	
	public void shutDown() {
		cancelWrite();
		writeActive = true;
		for (DatabaseConnection dc:dbConnections) {
			buffer.add(dc.closeConnection());
		}
		dbConnections.clear();
		saveBuffer();
	}
	
	public boolean initialWrite() {
		return initialWrite;
	}
	
	private void saveBuffer() {
		if (buffer.size() > 0) {
			FileTools ft = new FileTools();
			SerializeArrayList sal = new SerializeArrayList();
			String path = ft.getJarPath() + File.separator + "plugins" + File.separator + "HyperConomy" + File.separator + "temp";
			ft.makeFolder(path);
			path += File.separator + "buffer.txt";
			String stringBuffer = sal.stringArrayToStringA(buffer);
			ft.writeStringToFile(stringBuffer, path);
		}
	}
	


}
