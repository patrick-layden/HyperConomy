package regalowl.hyperconomy;

import java.util.ArrayList;
import java.util.logging.Logger;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class Notification implements TransactionListener {
	
	private HyperConomy hc;
	private ArrayList<HyperObject> notificationQueue = new ArrayList<HyperObject>();
	private String previousmessage;
	private int notifrequests;
	boolean usenotify;
	
	Notification() {
		hc = HyperConomy.hc;
		usenotify = hc.gYH().gFC("config").getBoolean("config.use-notifications");
		if (!usenotify) {return;}
		previousmessage = "";
		notifrequests = 0;
		hc.getHyperEventHandler().registerTransactionListener(this);
	}
	

	public void onTransaction(PlayerTransaction pt, TransactionResponse response) {
		if (response.successful()) {
			TransactionType tt = pt.getTransactionType();
			if (tt == TransactionType.BUY || tt == TransactionType.SELL || tt == TransactionType.SELL_ALL) {
				if (pt.getHyperObject() != null) {
					notificationQueue.add(pt.getHyperObject());
					sendNotification();
				}
			}
		}
	}
	
	
	private void sendNotification() {
		usenotify = hc.gYH().gFC("config").getBoolean("config.use-notifications");
		notifrequests++;
		hc.getServer().getScheduler().scheduleSyncDelayedTask(hc, new Runnable() {
			public void run() {
				send();
				notifrequests--;
			}
		}, notifrequests * 20);
	}

	private void send() {
		HyperObject ho = notificationQueue.get(0);
		LanguageFile L = hc.getLanguageFile();
		String econ = ho.getEconomy();
		HyperEconomy he = hc.getEconomyManager().getEconomy(econ);
		if (checkNotify(ho.getName())) {
			double cost = 0.0;
			int stock = 0;

			if (he.itemTest(ho.getName())) {
				stock = (int) ho.getStock();
				cost = ho.getCost(1);
				String message = L.f(L.get("SQL_NOTIFICATION"), (double) stock, cost, ho.getName(), econ);
				if (!message.equalsIgnoreCase(previousmessage)) {
					notify(message);
					previousmessage = message;
				}
			} else if (he.enchantTest(ho.getName())) {
				cost = ho.getCost(EnchantmentClass.DIAMOND);
				cost = cost + ho.getPurchaseTax(cost);
				stock = (int) ho.getStock();
				String message = L.f(L.get("SQL_NOTIFICATION"), (double) stock, cost, ho.getName(), econ);
				if (!message.equalsIgnoreCase(previousmessage)) {
					notify(message);
					previousmessage = message;
				}
			} else {
				Logger log = Logger.getLogger("Minecraft");
				log.info("HyperConomy ERROR #32--Notifcation Error");
		    	Bukkit.broadcast(ChatColor.DARK_RED + "HyperConomy ERROR #32--Notifcation Error", "hyperconomy.error");
			}
		}
		notificationQueue.remove(0);
	}
	
	
	
	private boolean checkNotify(String name) {
		boolean note = false;
		String notify = hc.gYH().gFC("config").getString("config.notify-for");
		if (notify != null && name != null) {		
			if (notify.contains("," + name + ",")) {
				note = true;
			}
			if (notify.length() >= name.length() && name.equalsIgnoreCase(notify.substring(0, name.length()))) {
				note = true;
			}
		}
		return note;
	}
	

	private void notify(String message) {
		Player[] players = Bukkit.getOnlinePlayers();
		for (int i = 0; i < players.length; i++) {
			Player p = players[i];
			if (p.hasPermission("hyperconomy.notify")) {
				p.sendMessage(message);
			}
		}
	}


}
