package regalowl.hyperconomy;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class SQLWrite {

	private HyperConomy hc;
	private int activethreads;
	private int threadlimit;
	private Player player;
	private ArrayList<String> buffer = new ArrayList<String>();
	private SQLWrite sqw;
	private Boolean writeactive;
	
	SQLWrite(HyperConomy hyc) {
		hc = hyc;
		activethreads = 0;
		threadlimit = 20;
		sqw = this;
		writeactive = false;
		
	}
	
	
	public void writeData(ArrayList<String> statements) {
		for (int c = 0; c < statements.size(); c++) {
			buffer.add(statements.get(c));
			Write();
			//Bukkit.broadcastMessage("s1");
		}
	}
	
	public void writeData(String statement) {
		buffer.add(statement);
		Write();
		//Bukkit.broadcastMessage("s2");
	}
	
	
	
	
	
    private void Write() {
    	
    	if (!writeactive) {
    		writeactive = true;
    		hc.getServer().getScheduler().scheduleSyncDelayedTask(hc, new Runnable() {
    		    public void run() {
    		    	//Bukkit.broadcastMessage("Running ActiveT: " + activethreads + " size: " + buffer.size());
    		    	
    		    	if (activethreads < threadlimit) {
    		    		
    		    		ArrayList<String> ws = new ArrayList<String>();
    		    		while (buffer.size() > 0) {
    		    			int index = buffer.size() - 1;
    		    			ws.add(buffer.get(index));
    		    			buffer.remove(index);
    		    		}
    		    		
    		    		SQLWriteThreadManager swtm = new SQLWriteThreadManager();
    					int bsize = ws.size();
    					int threadcount = 1;
    					if (bsize < 10) {
    						threadcount = 1;
    					} else if (bsize < 200 && bsize >= 10) {
    						threadcount = (threadlimit - activethreads)/2 - 1;
    					} else {
    						threadcount = threadlimit - activethreads;
    					}
    					swtm.writeData(hc, sqw, threadcount, ws, player);
    					activethreads = activethreads + threadcount;
    					if (!buffer.isEmpty()) {
    						retryWrite();
    					}
    		    	} else {
    		    		retryWrite();
    		    	}
    		    	writeactive = false;
    		    }
    		}, 20L);
    	}

    }

    
    
    private void retryWrite() {
		hc.getServer().getScheduler().scheduleSyncDelayedTask(hc, new Runnable() {
		    public void run() {
		    	Write();
		    }
		}, 60L);
    }
	

	
	public void setPlayer(Player p) {
		player = p;
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
	
	public void shutDown() {
		while (activethreads > 0) {
			//wait for threads to finish.
		}
	}
	
}


