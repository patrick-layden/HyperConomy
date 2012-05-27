package regalowl.hyperconomy;

import java.util.ArrayList;

import org.bukkit.block.Sign;

public class UpdateSign {
	
	private ArrayList<Sign> sign = new ArrayList<Sign>();
	private ArrayList<String> l1 = new ArrayList<String>();
	private ArrayList<String> l2 = new ArrayList<String>();
	private ArrayList<String> l3 = new ArrayList<String>();
	private ArrayList<String> l4 = new ArrayList<String>();
	
	private HyperConomy hc;

	
	public void updateSign(HyperConomy hyc, Sign s, String line1, String line2, String line3, String line4) {
		hc = hyc;

		if (!sign.contains(s)) {
			sign.add(s);
			l1.add(line1);
			l2.add(line2);
			l3.add(line3);
			l4.add(line4);
		
			hc.getServer().getScheduler().scheduleSyncDelayedTask(hc, new Runnable() {
			    public void run() {
			    	sign.get(0).setLine(0, l1.get(0));
			    	sign.get(0).setLine(1, l2.get(0));
			    	sign.get(0).setLine(2, l3.get(0));
			    	sign.get(0).setLine(3, l4.get(0));
			    	sign.get(0).update();
			    	sign.remove(0);
			    	l1.remove(0);
			    	l2.remove(0);
			    	l3.remove(0);
			    	l4.remove(0);
			    }
			}, 100L);
		}
		
		
	}
}
