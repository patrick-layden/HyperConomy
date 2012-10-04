package regalowl.hyperconomy;

import java.util.ArrayList;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class Notify {
	Notify(String[] args, CommandSender sender) {
		HyperConomy hc = HyperConomy.hc;
		try {
			String itemname = hc.fixName(args[0]);
			if (args.length == 1) {
				if (hc.getYaml().getConfig().getBoolean("config.use-notifications")) {
					if (hc.itemTest(itemname) || hc.enchantTest(itemname) || itemname.equalsIgnoreCase("all")) {
						if (!itemname.equalsIgnoreCase("all")) {
							boolean note = false;
							String notify = hc.getYaml().getConfig().getString("config.notify-for");
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
								hc.getYaml().getConfig().set("config.notify-for", notify);
								sender.sendMessage(ChatColor.GOLD + "You will no longer receive notifications for " + itemname);
							} else {
								notify = notify + itemname + ",";
								hc.getYaml().getConfig().set("config.notify-for", notify);
								sender.sendMessage(ChatColor.GOLD + "You will now receive notifications for " + itemname);
							}
						} else {
							ArrayList<String> items = hc.getNames();
							String namelist = "";
							int i = 0;
							while (i < items.size()) {
								namelist = namelist + items.get(i) + ",";
								i++;
							}
							String notify = hc.getYaml().getConfig().getString("config.notify-for");
							if (notify.equalsIgnoreCase(namelist)) {
								hc.getYaml().getConfig().set("config.notify-for", "");
								sender.sendMessage(ChatColor.GOLD + "You will no longer receive notifications for any item or enchantment.");
							} else {
								hc.getYaml().getConfig().set("config.notify-for", namelist);
								sender.sendMessage(ChatColor.GOLD + "You will now receive notifications for all items and enchantments.");
							}
						}
					} else {
						sender.sendMessage(ChatColor.DARK_RED + "That item or enchantment is not in the database!");
					}
				} else {
					sender.sendMessage(ChatColor.DARK_RED + "Notifications are currently disabled!");
				}
			} else {
				sender.sendMessage(ChatColor.DARK_RED + "Invalid Parameters.  Use /notify [name/'all']");
			}
		} catch (Exception e) {
			sender.sendMessage(ChatColor.DARK_RED + "Invalid Parameters.  Use /notify [name/'all']");
		}
	}
}
