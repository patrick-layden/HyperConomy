package regalowl.hyperconomy;

import java.util.ArrayList;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;


public class SQLWriteThreadManager {

	private ArrayList<Integer> processed = new ArrayList<Integer>();
	private ArrayList<ArrayList<String>> astatements = new ArrayList<ArrayList<String>>();
	
	private int savecompletetaskid;
	private int total;
	private boolean abort;
	private Player p;
	private HyperConomy hc;
	private SQLWrite sw;
	private int threads;
	
	public void writeData(HyperConomy hyc, SQLWrite sqw, int threadcount, ArrayList<String> statements, Player player) {
		p = player;
		hc = hyc;
		sw = sqw;
		threads = threadcount;
		
		total = statements.size();
		
		int co = 0;
		while (co < threadcount) {
			ArrayList<String> sarray = new ArrayList<String>();
			astatements.add(co, sarray);
			co++;
		}
		
		
		int threadselect = 0;
		for (int c = 0; c < statements.size(); c++) {
			ArrayList<String> cstatement = astatements.get(threadselect);
			cstatement.add(statements.get(c));
			astatements.set(threadselect, cstatement);
			threadselect++;
			if (threadselect == astatements.size()) {
				threadselect = 0;
			}
		}
		
		
		processed.clear();
		int scounter = 0;
		while (scounter < astatements.size()) {		
			SQLWriteThread srt = new SQLWriteThread();
			srt.writeThread(hc, this, astatements.get(scounter), scounter);
			processed.add(0);
			scounter++;
		}
		
		
		
		
		
		savecompletetaskid = hc.getServer().getScheduler().scheduleSyncRepeatingTask(hc, new Runnable() {
			public void run() {
				if (abort) {
					if (p != null) {
						p.sendMessage(ChatColor.RED + "An error has occurred!  Save aborted.");
						p.sendMessage(ChatColor.RED + "Lower the SQL thread count and try again.");
					}
					saveComplete();
					return;
				}
				int totalprocessed = 0;
				for (int i = 0; i < processed.size(); i++) {
					totalprocessed = totalprocessed + processed.get(i);
				}
				int remaining = total - totalprocessed;
				
				if (remaining == 0) {
					saveComplete();
					if (p != null) {
						p.sendMessage(ChatColor.GREEN + "Save complete!");
					}
				} else {
					if (p != null) {
						p.sendMessage(ChatColor.GREEN + "Saving: " + remaining + " entries remaining.");
					}
				}
			}
		}, 0L, 200L);
		
		
		
	}
	

	
	

	
	
	public void saveComplete() {
		sw.returnThreads(threads);
		hc.getServer().getScheduler().cancelTask(savecompletetaskid);
	}

	
	public void abortSave(){
		abort = true;
	}
	
	public void setProcessed(int threadid, int numprocessed) {
		processed.set(threadid, numprocessed);
	}
	
}
