package regalowl.hyperconomy;

import java.util.ArrayList;
import java.util.logging.Logger;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

public class Notify {
	
	private HyperConomy hc;
	private Calculation calc;
	private Enchant ench;
	private ArrayList<String> name = new ArrayList<String>();
	private ArrayList<String> eclass = new ArrayList<String>();
	
	
	private int notifrequests;
	
	boolean usenotify;
	
	Notify() {
		notifrequests = 0;
		usenotify = false;
	}
	
	
	public void setNotify(HyperConomy hyperc, Calculation cal, Enchant enchant, String nam, String ecla) {
		
		hc = hyperc;
		calc = cal;
		ench = enchant;
		usenotify = hc.getYaml().getConfig().getBoolean("config.use-notifications");
		
		if (usenotify) {
			name.add(nam);
			eclass.add(ecla);
		}

	}
	
	
    public void sendNotification() {
    	usenotify = hc.getYaml().getConfig().getBoolean("config.use-notifications");
    	if (usenotify) {
	    	notifrequests++;
	    	hc.getServer().getScheduler().scheduleSyncDelayedTask(hc, new Runnable() {
	    		public void run() {
	    		    send();
	    		    notifrequests--;
	    		}
	    	}, notifrequests * 20);
    	

    	}
		
    }
	
	public void send() {
		if (checkNotify(name.get(0))) {
			double cost = 0.0;
			int stock = 0;
			if (hc.itemTest(name.get(0))) {
				calc.setVC(hc, null, 1, name.get(0), null);
				cost = calc.getCost();
				stock = hc.getYaml().getItems().getInt(name.get(0) + ".stock.stock");
				Bukkit.broadcast("§9The economy now has §a" + stock + " §b" + name.get(0) + " §9priced at §a$" + cost + " §9each.", "hyperconomy.notify");
			} else if (hc.enchantTest(name.get(0))) {
				ench.setVC(hc, name.get(0), eclass.get(0));
				cost = ench.getCost();
				stock = hc.getYaml().getEnchants().getInt(name.get(0) + ".stock.stock");
				Bukkit.broadcast("§9The economy now has §a" + stock + " §b" + name.get(0) + " §9priced at §a$" + cost + " §9each.", "hyperconomy.notify");
			} else {
		    	Logger log = Logger.getLogger("Minecraft");
		    	log.info("HyperConomy ERROR #32--Notifcation Error");
		    	Bukkit.broadcast(ChatColor.DARK_RED + "HyperConomy ERROR #32--Notifcation Error", "hyperconomy.error");
			}
		}

		name.remove(0);
		eclass.remove(0);
	}
	
	
	
	public boolean checkNotify(String name) {
		boolean note = false;
		String notify = hc.getYaml().getConfig().getString("config.notify-for");
		if (notify != null) {		
			//For everything but the first.  (Which lacks a comma.)
			if (notify.contains("," + name + ",")) {
				note = true;
			}
			//For the first/last item.
			if (notify.length() >= name.length() && name.equalsIgnoreCase(notify.substring(0, name.length()))) {
				note = true;
			}
		}
		return note;
	}


}
