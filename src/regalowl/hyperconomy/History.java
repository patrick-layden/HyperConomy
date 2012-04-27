package regalowl.hyperconomy;

import java.util.ArrayList;
import java.util.Iterator;

import org.bukkit.configuration.file.FileConfiguration;


/**
 * 
 * 
 * This class stores item price history in history.yml  (Value/Purchase price.)
 * 
 */
public class History {
	
	private HyperConomy hc;
	private Calculation calc;
	private Enchant ench;
	private InfoSign isign;
	
	private ArrayList<String> historykeys = new ArrayList<String>();
	
	private int daystosavehistory;
	
	private long historyloginterval;
	private int historylogtaskid;
	
	
	//For server start.
	public void setHistory(HyperConomy hyperc, Calculation cal, Enchant enchant, InfoSign infosign) {
		hc = hyperc;
		calc = cal;
		ench = enchant;
		isign = infosign;
		
		daystosavehistory = hc.getYaml().getConfig().getInt("config.daystosavehistory");
		historyloginterval = 72000;
		//historyloginterval = 200;
	}



  	
  	public void writehistoryThread() {
  		
  		FileConfiguration items = hc.getYaml().getItems();
  		FileConfiguration enchants = hc.getYaml().getEnchants();
  		FileConfiguration history = hc.getYaml().getHistory();

		//Creates an ArrayList of all history entry keys.
		Iterator<String> iterat = history.getKeys(false).iterator();
		while (iterat.hasNext()) { 
			historykeys.add(iterat.next().toString());
		}
		
		//Adds all items to the history file.
		Iterator<String> iterat2 = items.getKeys(false).iterator();
		while (iterat2.hasNext()) { 
			String currentitem = iterat2.next().toString();
			
			calc.setVC(hc, null, 1, currentitem, null);
			
			if (!historykeys.contains(currentitem)) {
				historykeys.add(currentitem);
				history.set(currentitem, calc.getTvalue() + ",");
			} else {
				String historylist = history.getString(currentitem);
				historylist = historylist + calc.getTvalue() + ",";
				//Stops the history file from growing larger than 2 weeks of entries.
				if (historylist.replaceAll("[\\d]", "").replace(".", "").length() > (daystosavehistory * 24)) {
					historylist = historylist.substring(historylist.indexOf(",") + 1, historylist.length());
				}
				history.set(currentitem, historylist);
				

			}
		}
		
		
		//Adds all enchants to the history file.
		Iterator<String> iterat3 = enchants.getKeys(false).iterator();
		while (iterat3.hasNext()) { 
			String currentenchant = iterat3.next().toString();
			
			ench.setVC(hc, currentenchant, "diamond", calc);
			
			if (!historykeys.contains(currentenchant)) {
				historykeys.add(currentenchant);
				history.set(currentenchant, ench.getValue() + ",");
			} else {
				String historylist = history.getString(currentenchant);
				historylist = historylist + ench.getValue() + ",";
				//Stops the history file from growing larger than 2 weeks of entries.
				if (historylist.replaceAll("[\\d]", "").replace(".", "").length() > (daystosavehistory * 24)) {
					historylist = historylist.substring(historylist.indexOf(",") + 1, historylist.length());
				}
				history.set(currentenchant, historylist);
			}
		}

  	}
  	
  	
  	
  	
    public void starthistoryLog() {
    	if (hc.getYaml().getConfig().getBoolean("config.store-price-history")) {
			historylogtaskid = hc.getServer().getScheduler().scheduleSyncRepeatingTask(hc, new Runnable() {
			    public void run() {
			    	writehistoryThread();
			    	
					//Updates all information signs.
					isign.setrequestsignUpdate(true);
					isign.checksignUpdate();
			    }
			}, (historyloginterval/2), historyloginterval);
    	}
    }
    
    
    public void stophistoryLog() {
    	hc.getServer().getScheduler().cancelTask(historylogtaskid);
    }


  	
}