package regalowl.hyperconomy;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class Createeconomy {
	Createeconomy(String args[], CommandSender sender) {
		HyperConomy hc = HyperConomy.hc;
		try {
			if (hc.useSQL()) {
    			if (args.length == 1) {
    				String economy = args[0];
    				if (!hc.getSQLFunctions().testEconomy(economy)) {
    					hc.getSQLEconomy().createNewEconomy(economy);
    					sender.sendMessage(ChatColor.GOLD + "New economy created!");
    				} else {
    					sender.sendMessage(ChatColor.RED + "That economy already exists!");
    				}
    			} else {
    				sender.sendMessage(ChatColor.RED + "Invalid Parameters.  Use /createeconomy [name]");
    			}
			} else {
				sender.sendMessage(ChatColor.RED + "This command is only available when SQL is enabled!");
			}
		} catch (Exception e) {
			sender.sendMessage(ChatColor.RED + "Invalid Parameters.  Use /createeconomy [name]");
		}
	}
}
