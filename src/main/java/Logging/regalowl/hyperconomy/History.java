package regalowl.hyperconomy;

import java.util.ArrayList;


/**
 * 
 * 
 * This class stores item price history in history.yml  (Value/Purchase price.)
 * 
 */
public class History {
	
	private HyperConomy hc;
	private Calculation calc;
	private InfoSignHandler isign;
	
	private ArrayList<String> historykeys = new ArrayList<String>();

	
	private long historyloginterval;
	private int historylogtaskid;
	private SQLFunctions sf;
	
	//For server start.
	History(HyperConomy hyperc, Calculation cal, ETransaction enchant, InfoSignHandler infosign) {
		hc = hyperc;
		calc = cal;
		isign = infosign;

		historyloginterval = 72000;
		//historyloginterval = 800;
	}



  	
  	public void writehistoryThread() {
  			sf = hc.getSQLFunctions();
  			historykeys = sf.getKeys();
  			
  			ArrayList<String> inames = hc.getInames();
  			ArrayList<String> enames = hc.getEnames();
  			
  			for (int i = 0; i < historykeys.size(); i++) {
  				String key = historykeys.get(i);
  				String name = key.substring(0, key.indexOf(":"));
  				String economy = key.substring(key.indexOf(":") + 1, key.length());
  				Double price = 0.0;
  				if (inames.contains(name)) {
  					price = calc.getTvalue(name, 1, economy);
  				} else if (enames.contains(name)) {
  					price = calc.getEnchantValue(name, "diamond", economy);
  				}
  				sf.writeHistoryData(name, economy, price);
  			}
  	}
  	
  	
  	
  	
    public void starthistoryLog() {
    	if (hc.getYaml().getConfig().getBoolean("config.store-price-history")) {
			historylogtaskid = hc.getServer().getScheduler().scheduleSyncRepeatingTask(hc, new Runnable() {
			    public void run() {
			    	writehistoryThread();
			    	
					//Updates all information signs.
					historylogtaskid = hc.getServer().getScheduler().scheduleSyncDelayedTask(hc, new Runnable() {
					    public void run() {
					    	isign.updateSigns();
					    }
					}, 200L);
			    }
			}, (historyloginterval/2), historyloginterval);
    	}
    }
    
    
    public void stophistoryLog() {
    	hc.getServer().getScheduler().cancelTask(historylogtaskid);
    }

  	
}