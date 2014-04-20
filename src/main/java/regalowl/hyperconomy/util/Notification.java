package regalowl.hyperconomy.util;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import regalowl.hyperconomy.HyperConomy;
import regalowl.hyperconomy.event.TransactionListener;
import regalowl.hyperconomy.hyperobject.EnchantmentClass;
import regalowl.hyperconomy.hyperobject.HyperObject;
import regalowl.hyperconomy.hyperobject.HyperObjectType;
import regalowl.hyperconomy.transaction.PlayerTransaction;
import regalowl.hyperconomy.transaction.TransactionResponse;
import regalowl.hyperconomy.transaction.TransactionType;

public class Notification implements TransactionListener {
	
	private HyperConomy hc;
	private ArrayList<HyperObject> notificationQueue = new ArrayList<HyperObject>();
	private String previousmessage;
	private int notifrequests;
	boolean usenotify;
	
	public Notification() {
		hc = HyperConomy.hc;
		usenotify = hc.getConf().getBoolean("enable-feature.price-change-notifications");
		if (!usenotify) {return;}
		previousmessage = "";
		notifrequests = 0;
		hc.getHyperEventHandler().registerTransactionListener(this);
	}
	

	public void onTransaction(PlayerTransaction pt, TransactionResponse response) {
		if (response.successful()) {
			TransactionType tt = pt.getTransactionType();
			if (tt == TransactionType.BUY || tt == TransactionType.SELL) {
				if (pt.getHyperObject() != null) {
					notificationQueue.add(pt.getHyperObject());
					sendNotification();
				}
			}
		}
	}
	
	
	private void sendNotification() {
		usenotify = hc.getConf().getBoolean("enable-feature.price-change-notifications");
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
		if (checkNotify(ho.getName())) {
			double cost = 0.0;
			int stock = 0;

			if (ho.getType() == HyperObjectType.ITEM) {
				stock = (int) ho.getStock();
				cost = ho.getBuyPrice(1);
				String message = L.f(L.get("SQL_NOTIFICATION"), (double) stock, cost, ho.getDisplayName(), econ);
				if (!message.equalsIgnoreCase(previousmessage)) {
					notify(message);
					previousmessage = message;
				}
			} else if (ho.getType() == HyperObjectType.ENCHANTMENT) {
				cost = ho.getBuyPrice(EnchantmentClass.DIAMOND);
				cost = cost + ho.getPurchaseTax(cost);
				stock = (int) ho.getStock();
				String message = L.f(L.get("SQL_NOTIFICATION"), (double) stock, cost, ho.getDisplayName(), econ);
				if (!message.equalsIgnoreCase(previousmessage)) {
					notify(message);
					previousmessage = message;
				}
			} else {
				stock = (int) ho.getStock();
				cost = ho.getBuyPrice(1);
				String message = L.f(L.get("SQL_NOTIFICATION"), (double) stock, cost, ho.getDisplayName(), econ);
				if (!message.equalsIgnoreCase(previousmessage)) {
					notify(message);
					previousmessage = message;
				}
			}
		}
		notificationQueue.remove(0);
	}
	
	
	
	private boolean checkNotify(String name) {
		boolean note = false;
		String notify = hc.getConf().getString("shop.send-price-change-notifications-for");
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
