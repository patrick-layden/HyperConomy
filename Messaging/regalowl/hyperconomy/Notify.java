package regalowl.hyperconomy;

import java.util.ArrayList;
import java.util.logging.Logger;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

public class Notify {
	
	private HyperConomy hc;
	private Calculation calc;
	private ETransaction ench;
	private ArrayList<String> name = new ArrayList<String>();
	private ArrayList<String> eclass = new ArrayList<String>();
	
	private String econ;
	
	private String previousmessage;
	
	
	private int notifrequests;
	
	boolean usenotify;
	
	Notify() {
		previousmessage = "";
		notifrequests = 0;
		usenotify = false;
	}
	
	
	public void setNotify(HyperConomy hyperc, Calculation cal, ETransaction enchant, String nam, String ecla, String economy) {
		
		hc = hyperc;
		calc = cal;
		ench = enchant;
		usenotify = hc.getYaml().getConfig().getBoolean("config.use-notifications");
		econ = economy;
		
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
		SQLFunctions sf = hc.getSQLFunctions();
		if (checkNotify(name.get(0))) {
			double cost = 0.0;
			int stock = 0;
			if (hc.itemTest(name.get(0))) {
				calc.setVC(hc, null, 1, name.get(0), null);
				if (hc.useSQL()) {
					stock = (int) sf.getStock(name.get(0), econ);
				} else {
					stock = hc.getYaml().getItems().getInt(name.get(0) + ".stock.stock");
				}
				cost = calc.getCost();
				
				String message = "";
				if (hc.useSQL()) {
					message = "§9The §f" + econ + " §9economy now has §a" + stock + " §b" + name.get(0) + " §9priced at §a" + hc.getYaml().getConfig().getString("config.currency-symbol") + cost + " §9each.";
				} else {
					message = "§9The economy now has §a" + stock + " §b" + name.get(0) + " §9priced at §a" + hc.getYaml().getConfig().getString("config.currency-symbol") + cost + " §9each.";
				}
				
				if (!message.equalsIgnoreCase(previousmessage)) {
					Bukkit.broadcast(message, "hyperconomy.notify");
					previousmessage = message;
				}
			} else if (hc.enchantTest(name.get(0))) {
				ench.setVC(hc, name.get(0), eclass.get(0), calc);
				cost = ench.getCost();
				if (hc.useSQL()) {
					stock = (int) sf.getStock(name.get(0), econ);
				} else {
					stock = hc.getYaml().getEnchants().getInt(name.get(0) + ".stock.stock");
				}
				String message = "";
				if (hc.useSQL()) {
					message = "§9The §f" + econ + " §9economy now has §a" + stock + " §b" + name.get(0) + " §9priced at §a" + hc.getYaml().getConfig().getString("config.currency-symbol") + cost + " §9each.";
				} else {
					message = "§9The economy now has §a" + stock + " §b" + name.get(0) + " §9priced at §a" + hc.getYaml().getConfig().getString("config.currency-symbol") + cost + " §9each.";
				}
				if (!message.equalsIgnoreCase(previousmessage)) {
					Bukkit.broadcast(message, "hyperconomy.notify");
					previousmessage = message;
				}
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
