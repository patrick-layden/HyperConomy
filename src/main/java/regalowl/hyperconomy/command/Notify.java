package regalowl.hyperconomy.command;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import regalowl.databukkit.CommonFunctions;
import regalowl.hyperconomy.HyperConomy;
import regalowl.hyperconomy.HyperEconomy;
import regalowl.hyperconomy.event.TransactionListener;
import regalowl.hyperconomy.hyperobject.HyperObject;
import regalowl.hyperconomy.transaction.PlayerTransaction;
import regalowl.hyperconomy.transaction.TransactionResponse;
import regalowl.hyperconomy.transaction.TransactionType;
import regalowl.hyperconomy.util.LanguageFile;
public class Notify implements CommandExecutor, TransactionListener {

	private HyperConomy hc;
	private CommonFunctions cf;
	private LanguageFile L;
	private ArrayList<String> notifyNames = new ArrayList<String>();
	private boolean enabled;

	
	public Notify() {
		hc = HyperConomy.hc;
		L = hc.getLanguageFile();
		cf = hc.getCommonFunctions();
		enabled = hc.getConf().getBoolean("enable-feature.price-change-notifications");
		notifyNames = cf.explode(hc.getConf().getString("shop.send-price-change-notifications-for"), ",");
		hc.getHyperEventHandler().registerListener(this);
	}
	
	public String getNotifyString() {
		return cf.implode(notifyNames, ",");
	}
	
	public void saveNotifyNames() {
		hc.getConf().set("shop.send-price-change-notifications-for", getNotifyString());
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		try {
			HyperEconomy he = hc.getDataManager().getEconomy("default");
			HyperObject ho = he.getHyperObject(args[0]);
			if (!enabled) {
				sender.sendMessage(L.get("NOTIFICATIONS_DISABLED"));
				return true;
			}
			if (args[0].equalsIgnoreCase("all")) {
				notifyNames.clear();
				for (String cName:he.getNames()) {
					notifyNames.add(cName);
				}
				saveNotifyNames();
				sender.sendMessage(L.get("RECEIVE_NOTIFICATIONS"));
				return true;
			}
			if (args[0].equalsIgnoreCase("none")) {
				notifyNames.clear();
				saveNotifyNames();
				sender.sendMessage(L.get("NOT_RECEIVE_NOTIFICATIONS"));
				return true;
			}
			if (ho == null) {
				sender.sendMessage(L.get("OBJECT_NOT_IN_DATABASE"));
				return true;
			}
			if (notifyNames.contains(ho.getName())) {
				notifyNames.remove(ho.getName());
				saveNotifyNames();
				sender.sendMessage(L.f(L.get("NOT_RECEIVE_NOTIFICATIONS_S"), ho.getDisplayName()));
			} else {
				notifyNames.add(ho.getName());
				saveNotifyNames();
				sender.sendMessage(L.f(L.get("RECEIVE_NOTIFICATIONS_S"), ho.getDisplayName()));
			}
		} catch (Exception e) {
			sender.sendMessage(L.get("NOTIFY_INVALID"));
		}
		return true;
	}
	
	
	public void onTransaction(PlayerTransaction pt, TransactionResponse response) {
		if (response.successful()) {
			TransactionType tt = pt.getTransactionType();
			if (tt == TransactionType.BUY || tt == TransactionType.SELL) {
				if (pt.getHyperObject() != null) {
					HyperObject ho = pt.getHyperObject();
					if (notifyNames.contains(ho.getName())) {
						String message = L.f(L.get("SQL_NOTIFICATION"), ho.getStock(), ho.getBuyPriceWithTax(1), ho.getDisplayName(), ho.getEconomy());
						sendNotification(message);
					}
				}
			}
		}
	}

	private void sendNotification(String message) {
		for (Player p:Bukkit.getOnlinePlayers()) {
			if (p.hasPermission("hyperconomy.notify")) {
				p.sendMessage(message);
			}
		}
	}
	
	
}
