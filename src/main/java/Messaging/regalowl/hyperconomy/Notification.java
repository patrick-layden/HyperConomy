package regalowl.hyperconomy;

import java.util.ArrayList;
import java.util.logging.Logger;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import static regalowl.hyperconomy.Messages.*;

public class Notification {
	
	private HyperConomy hc;
	private Calculation calc;
	private ArrayList<String> name = new ArrayList<String>();
	private ArrayList<String> eclass = new ArrayList<String>();
	private FormatString fs = new FormatString();
	
	private String econ;
	
	private String previousmessage;
	
	
	private int notifrequests;
	
	boolean usenotify;
	
	Notification() {
		previousmessage = "";
		notifrequests = 0;
		usenotify = false;
	}
	
	
	public void setNotify(HyperConomy hyperc, Calculation cal, ETransaction enchant, String nam, String ecla, String economy) {
		
		hc = hyperc;
		calc = cal;
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
				stock = (int) sf.getStock(name.get(0), econ);
				cost = calc.getCost(name.get(0), 1, econ);
				
				String message = "";
				if (hc.useSQL()) {
					message = fs.formatString(SQL_NOTIFICATION, stock, cost, name.get(0), econ);
					//message = "\u00A79The \u00A7f" + econ + " \u00A79economy now has \u00A7a" + stock + " \u00A7b" + name.get(0) + " \u00A79priced at \u00A7a" + hc.getYaml().getConfig().getString("config.currency-symbol") + cost + " \u00A79each.";
				} else {
					message = fs.formatString(SQL_NOTIFICATION, stock, cost, name.get(0), econ);
					//message = "\u00A79The economy now has \u00A7a" + stock + " \u00A7b" + name.get(0) + " \u00A79priced at \u00A7a" + hc.getYaml().getConfig().getString("config.currency-symbol") + cost + " \u00A79each.";
				}
				
				if (!message.equalsIgnoreCase(previousmessage)) {
					//Bukkit.broadcast(message, "hyperconomy.notify");
					manualNotify(message);
					previousmessage = message;
				}
			} else if (hc.enchantTest(name.get(0))) {
				cost = calc.getEnchantCost(name.get(0), eclass.get(0), econ);
				cost = cost + calc.getEnchantTax(name.get(0), econ, cost);
				stock = (int) sf.getStock(name.get(0), econ);
				String message = "";
				if (hc.useSQL()) {
					message = fs.formatString(SQL_NOTIFICATION, stock, cost, name.get(0), econ);
					//message = "\u00A79The \u00A7f" + econ + " \u00A79economy now has \u00A7a" + stock + " \u00A7b" + name.get(0) + " \u00A79priced at \u00A7a" + hc.getYaml().getConfig().getString("config.currency-symbol") + cost + " \u00A79each.";
				} else {
					message = fs.formatString(SQL_NOTIFICATION, stock, cost, name.get(0), econ);
					//message = "\u00A79The economy now has \u00A7a" + stock + " \u00A7b" + name.get(0) + " \u00A79priced at \u00A7a" + hc.getYaml().getConfig().getString("config.currency-symbol") + cost + " \u00A79each.";
				}
				if (!message.equalsIgnoreCase(previousmessage)) {
					//Bukkit.broadcast(message, "hyperconomy.notify");
					manualNotify(message);
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
			if (notify.length() >= name.length() && name.equalsIgnoreCase(notify.substring(0, name.length()))) {
				note = true;
			}
		}
		return note;
	}
	
	//Workaround for lame bug...
	public void manualNotify(String message) {
		Player[] players = Bukkit.getOnlinePlayers();
		for (int i = 0; i < players.length; i++) {
			Player p = players[i];
			if (p.hasPermission("hyperconomy.notify")) {
				p.sendMessage(message);
			}
		}
	}


}
