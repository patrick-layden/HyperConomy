package regalowl.hyperconomy;

import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Date;
import org.bukkit.configuration.file.FileConfiguration;





/**
 * 
 * 
 * This class logs all transactions to log.txt and lists all items/enchantments on command.
 * 
 */
public class Log {
	
	private ArrayList<String> buffer;	
	//private int logsize;
	private HyperConomy hc;
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

	
	//For server start.
	Log(HyperConomy hyperc) {
		hc = hyperc;
		buffer = new ArrayList<String>();	
		
    	requestbuffer = false;
    	bufferactive = false;
    	logsize = hc.getYaml().getLog().getKeys(true).size();
    	loginterval = hc.getYaml().getConfig().getLong("config.logwriteinterval");
	}
	
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
	  		//HyperConomy.yaml.saveYamls();	  		
	  		buffer.remove(0);
  		} else {
  			setrequestBuffer(false);
  		}
  	}
  	

  	//For when the server shuts down/lockshop.
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
  	
    public int getlogSize() {
    	return logsize;
    }
  	
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
  	
  	
  	

  	//Used by the /writeitems command to write a list of all item names to file.
  	public void writeItems() {
  		try{
  			// Create file 
  			FileWriter fstream = new FileWriter("plugins\\HyperConomy\\ItemNames.txt", true);
  			fstream.write(entry);
  			//Close the output stream
  			fstream.close();
  		}catch (Exception e){//Catch exception if any
  			e.printStackTrace();
  		}
  	}
  	public void writeEnchants() {
  		try{
  			// Create file 
  			FileWriter fstream = new FileWriter("plugins\\HyperConomy\\EnchantmentNames.txt", true);
  			fstream.write(entry);
  			//Close the output stream
  			fstream.close();
  		}catch (Exception e){//Catch exception if any
  			e.printStackTrace();
  		}
  	}
  	
  	
  	
  	
  	
}
