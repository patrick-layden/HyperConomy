package regalowl.hyperconomy;

import java.util.ArrayList;

import org.bukkit.command.CommandSender;

public class Notify {
	Notify(String[] args, CommandSender sender) {
		HyperConomy hc = HyperConomy.hc;
		LanguageFile L = hc.getLanguageFile();
		EconomyManager em = hc.getEconomyManager();
		try {
			HyperEconomy he = em.getEconomy("default");
			String itemname = he.fixName(args[0]);
			if (args.length == 1) {
				if (hc.gYH().gFC("config").getBoolean("config.use-notifications")) {
					if (he.objectTest(itemname) || itemname.equalsIgnoreCase("all")) {
						if (!itemname.equalsIgnoreCase("all")) {
							boolean note = false;
							String notify = hc.gYH().gFC("config").getString("config.notify-for");
							if (notify != null) {
								if (notify.contains("," + itemname + ",")) {
									note = true;
								}
								if (notify.length() >= itemname.length() && itemname.equalsIgnoreCase(notify.substring(0, itemname.length()))) {
									note = true;
								}
							}
							if (note) {
								notify = notify.replace("," + itemname + ",", ",");
								if (itemname.equalsIgnoreCase(notify.substring(0, itemname.length()))) {
									notify = notify.substring(itemname.length() + 1, notify.length());
								}
								hc.gYH().gFC("config").set("config.notify-for", notify);
								//sender.sendMessage(ChatColor.GOLD + "You will no longer receive notifications for " + itemname);
								sender.sendMessage(L.f(L.get("NOT_RECEIVE_NOTIFICATIONS_S"), itemname));
							} else {
								notify = notify + itemname + ",";
								hc.gYH().gFC("config").set("config.notify-for", notify);
								//sender.sendMessage(ChatColor.GOLD + "You will now receive notifications for " + itemname);
								sender.sendMessage(L.f(L.get("RECEIVE_NOTIFICATIONS_S"), itemname));
							}
						} else {
							ArrayList<String> items = he.getNames();
							String namelist = "";
							int i = 0;
							while (i < items.size()) {
								namelist = namelist + items.get(i) + ",";
								i++;
							}
							String notify = hc.gYH().gFC("config").getString("config.notify-for");
							if (notify.equalsIgnoreCase(namelist)) {
								hc.gYH().gFC("config").set("config.notify-for", "");
								sender.sendMessage(L.get("NOT_RECEIVE_NOTIFICATIONS"));
							} else {
								hc.gYH().gFC("config").set("config.notify-for", namelist);
								sender.sendMessage(L.get("RECEIVE_NOTIFICATIONS"));
							}
						}
					} else {
						sender.sendMessage(L.get("OBJECT_NOT_IN_DATABASE"));
					}
				} else {
					sender.sendMessage(L.get("NOTIFICATIONS_DISABLED"));
				}
			} else {
				sender.sendMessage(L.get("NOTIFY_INVALID"));
			}
		} catch (Exception e) {
			sender.sendMessage(L.get("NOTIFY_INVALID"));
		}
	}
}
