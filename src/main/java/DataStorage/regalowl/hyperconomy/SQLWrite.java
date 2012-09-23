package regalowl.hyperconomy;

import java.io.File;
import java.util.ArrayList;


public class SQLWrite {

	private HyperConomy hc;
	private int activethreads;
	private int threadlimit;
	private int writeTaskID;
	private ArrayList<String> buffer = new ArrayList<String>();
	private ArrayList<String> workingBuffer = new ArrayList<String>();
	private SQLWrite sqw;
	private boolean writePaused;
	private boolean writeActive;
	private ConnectionPool cp;
	private boolean initialWrite;
	
	SQLWrite(HyperConomy hyc) {
		hc = hyc;
		activethreads = 0;
		threadlimit = hc.getYaml().getConfig().getInt("config.sql-connection.max-sql-threads");
		sqw = this;
		cp = new ConnectionPool(hc, this, threadlimit);
		writePaused = false;
		initialWrite = false;
		
		ArrayList<String> sstatements = loadStatements();
		if (sstatements.size() > 0) {
			initialWrite = true;
			hc.sqllockShop();
			writeData(sstatements);
			
			//SQLFunctions sf = hc.getSQLFunctions();
			//sf.load();
		}
		
	}

	public void writeData(ArrayList<String> statements) {
		for (int c = 0; c < statements.size(); c++) {
			workingBuffer.add(statements.get(c));
			buffer.add(statements.get(c));
			startWrite();
		}
	}
	public void writeData(String statement) {
		workingBuffer.add(statement);
		buffer.add(statement);
		startWrite();
	}
	
	
	
	
	
    private void startWrite() {
    	if (!writeActive && !writePaused) {
        	writeActive = true;
    		writeTaskID = hc.getServer().getScheduler().scheduleSyncRepeatingTask(hc, new Runnable() {
    		    public void run() {
        		    Write();
    		    }
    		}, 5L, 5L);
    	}
    }
    private void stopWrite() {
		hc.getServer().getScheduler().cancelTask(writeTaskID);
		writeActive = false;
    }
	
	
    
    
    
	
	
    private void Write() {
    	if (workingBuffer.size() == 0) {
    		stopWrite();
    		if (initialWrite) {
    			initialWrite = false;
    		}
    	}
    	while (activethreads < threadlimit && workingBuffer.size() > 0) {
			activethreads++;
    		int index = workingBuffer.size() - 1;
    		String statement = workingBuffer.get(index);
    		workingBuffer.remove(index);
    		SQLWriteThread swt = new SQLWriteThread();
    		swt.writeThread(hc, sqw, cp, statement);
    	}
    }

    
    public void pauseWrite(long wait) {
    	writePaused = true;
    	stopWrite();
    	hc.getServer().getScheduler().scheduleSyncDelayedTask(hc, new Runnable() {
		    public void run() {
		    	writePaused = false;
		    	startWrite();
		    }
		}, wait);
    }
    
    
    public void writeSuccess(String statement) {
    	buffer.remove(statement);
    	activethreads--;
    }
    
    public void writeFailed(String statement) {
    	workingBuffer.add(statement);
    	activethreads--;
		//Logger l = Logger.getLogger("Minecraft");
		//l.severe("Failed: " + statement);
    }


	public void returnThreads(int threads) {
		activethreads = activethreads - threads;
	}
	
	public int getBufferSize() {
		return buffer.size();
	}
	
	public int getActiveThreads() {
		return activethreads;
	}
	
	public ArrayList<String> getBuffer() {
		return buffer;
	}
	
	public void closeConnections() {
		cp.closeConnections();
	}
	
	public boolean initialWrite() {
		return initialWrite;
	}
	

	public ArrayList<String> loadStatements() {
		FileTools ft = new FileTools();
		SerializeArrayList sal =  new SerializeArrayList();
		String path = ft.getJarPath() + File.separator + "plugins" + File.separator + "HyperConomy" + File.separator + "temp" + File.separator + "buffer.txt";
		if (ft.fileExists(path)) {
			String statements = ft.getStringFromFile(path);
			ft.deleteFile(path);
			return sal.stringToArray(statements);
		} else {
			ArrayList<String> empty = new ArrayList<String>();
			return empty;
		}

	}
}
