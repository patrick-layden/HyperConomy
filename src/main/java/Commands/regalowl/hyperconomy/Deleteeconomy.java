package regalowl.hyperconomy;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class Deleteeconomy {
	Deleteeconomy(String args[], CommandSender sender) {
		HyperConomy hc = HyperConomy.hc;
		try {
			if (hc.useSQL()) {
    			if (args.length == 1) {
    				String economy = args[0];
    				if (economy.equalsIgnoreCase("default")) {
    					sender.sendMessage(ChatColor.RED + "You can't delete the default economy!");
    					return;
    				}
    				if (hc.getSQLFunctions().testEconomy(economy)) {
    					if (hc.getYaml().getConfig().getBoolean("config.run-automatic-backups")) {
    						new Backup();
    					}
    					hc.getSQLEconomy().deleteEconomy(economy);
    					sender.sendMessage(ChatColor.GOLD + "Economy deleted!");
    				} else {
    					sender.sendMessage(ChatColor.RED + "That economy doesn't exist!");
    				}
				} else {
					sender.sendMessage(ChatColor.RED + "Invalid Parameters.  Use /deleteeconomy [name]");
				}
			} else {
				sender.sendMessage(ChatColor.RED + "This command is only available when SQL is enabled!");
			}
		} catch (Exception e) {
			sender.sendMessage(ChatColor.RED + "Invalid Parameters.  Use /deleteeconomy [name]");
		}
	}
}
