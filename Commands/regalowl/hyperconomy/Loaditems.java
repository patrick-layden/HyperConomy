package regalowl.hyperconomy;

import java.util.ArrayList;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class Loaditems {
	Loaditems(String args[], CommandSender sender) {
		HyperConomy hc = HyperConomy.hc;
		try {
			if (hc.useSQL()) {
    			if (args.length == 1) {
    				String economy = args[0];
    				if (hc.getSQLFunctions().testEconomy(economy)) {
    					if (hc.getYaml().getConfig().getBoolean("config.run-automatic-backups")) {
    						Backup back = new Backup();
    						back.BackupData();
    					}
        				SQLEconomy se = hc.getSQLEconomy();
        				ArrayList<String> added = se.loadItems(economy);
        				sender.sendMessage(ChatColor.GOLD + added.toString() + " loaded into economy!");
    				} else {
    					sender.sendMessage(ChatColor.RED + "That economy doesn't exist!");
    				}
    			} else {
    				sender.sendMessage(ChatColor.RED + "Invalid Parameters.  Use /loaditems [economy]");
    			}
			} else {
				sender.sendMessage(ChatColor.RED + "This command is only available when SQL is enabled!");
			}
		} catch (Exception e) {
			sender.sendMessage(ChatColor.RED + "Invalid Parameters.  Use /loaditems [economy]");
		}
	}
}
