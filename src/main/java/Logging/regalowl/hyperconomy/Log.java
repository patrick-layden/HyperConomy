package regalowl.hyperconomy;



import java.util.Date;
import org.bukkit.configuration.file.FileConfiguration;


/**
 * 
 * 
 * This class logs all transactions to log.txt and lists all items/enchantments on command.
 * 
 */
public class Log {
	
	
	private HyperConomy hc;
	private int logsize;
	//private ArrayList<String> buffer;	
	/*
	private String entry;
	private boolean requestbuffer;
	private boolean bufferactive;
	private int logsize;
	private long loginterval;
	private int buffertaskid;
	
	
	public void setEntry(String ent) {
		entry = ent;
	}
	public int getbufferSize() {
		return buffer.size();
	}
	*/
	
	
	public void writeSQLLog(String playername, String action, String object, Double amount, Double money, Double tax, String store, String type) {
		String statement = "Insert Into hyperlog (TIME, CUSTOMER, ACTION, OBJECT, AMOUNT, MONEY, TAX, STORE, TYPE)"
	            + " Values (NOW(),'" + playername + "','" + action + "','" + object + "','" + amount + "','" + hc.getCalculation().twoDecimals(money) + "','" + hc.getCalculation().twoDecimals(tax) + "','" + store + 
	        "','" + type + "')";
		hc.getSQLWrite().writeData(statement);
	}

	
	//For server start.
	Log(HyperConomy hyperc) {
		hc = hyperc;
    	logsize = hc.getYaml().getLog().getKeys(false).size();
		//buffer = new ArrayList<String>();	
    	//requestbuffer = false;
    	//bufferactive = false;
    	//loginterval = hc.getYaml().getConfig().getLong("config.logwriteinterval");
	}
	

  	public void writeLog(String entry) {
  		Date currentDate = new Date();
  		FileConfiguration l = hc.getYaml().getLog();
		l.set("["+ logsize + "] " + currentDate.toString(), entry);
		logsize++;  		
  	}
  	
    public int getlogSize() {
    	return logsize;
    }
  	
  	/*

  	public void writeBuffer() {
  		buffer.add(entry);
  		setrequestBuffer(true);
  	}
  	public void writelogThread() {
  		if (!buffer.isEmpty()) {
	  		Date currentDate = new Date();
	  		FileConfiguration l = hc.getYaml().getLog();
			l.set("["+ logsize + "] " + currentDate.toString(), buffer.get(0));
			logsize++;  		
	  		buffer.remove(0);
  		} else {
  			setrequestBuffer(false);
  		}
  	}
  	

  	public void saveBuffer() {
  		while (!buffer.isEmpty()) {
	  		String entry = buffer.get(0);
	  		Date currentDate = new Date();
	  		String date = currentDate.toString();
	  		FileConfiguration l = hc.getYaml().getLog();
	  		int transactionid = l.getKeys(true).size();
	  		String key = "["+ transactionid + "] " + date;
			l.set(key, entry); 		
	  		buffer.remove(0);
  		}
  		hc.getYaml().saveYamls();	
  	}
  	

    public void setrequestBuffer(boolean bufferstate) {
    	requestbuffer = bufferstate;
    }
    
    public void setlogInterval(long interval) {
    	loginterval = interval;
    }

    
    public void setlogSize(int size) {
    	logsize = size;
    }
  	*/
    

  	
    /*
    public long getlogInterval() {
    	return loginterval;
    }
  	

  	
    public void startBuffer() {
    	bufferactive = true;
		buffertaskid = hc.getServer().getScheduler().scheduleSyncRepeatingTask(hc, new Runnable() {
		    public void run() {
		    	if (!requestbuffer) {
		    		stopBuffer();
		    	} else {
		    		writelogThread();
		    	}
		    }
		}, loginterval, loginterval);
    }
    
    public void stopBuffer() {
    	hc.getServer().getScheduler().cancelTask(buffertaskid);
    	bufferactive = false;
    }
    
    

    
    public void checkBuffer() {
    	if (requestbuffer && !bufferactive) {
    		startBuffer();
    	}
    }
	*/
}
