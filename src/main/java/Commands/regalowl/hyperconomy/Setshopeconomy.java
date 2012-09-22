package regalowl.hyperconomy;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class Setshopeconomy {
	Setshopeconomy(String args[], CommandSender sender) {
		HyperConomy hc = HyperConomy.hc;
		try {
			if (hc.useSQL()) {
				if (args.length == 2) {
    				String name = args[0];
    				name = name.replace("%s", " ");
    				String teststring = hc.getYaml().getShops().getString(name);
    				if (teststring == null) {
    					name = hc.fixsName(name);
    				}
    				if (name == null) {
    					sender.sendMessage(ChatColor.RED + "That shop doesn't exist!");
    					return;
    				}
    				String economy = args[1];
    				if (hc.getSQLFunctions().testEconomy(economy)) {
    					hc.getYaml().getShops().set(name + ".economy", economy);
    					sender.sendMessage(ChatColor.GOLD + "Shop economy set!");
    				} else {
    					sender.sendMessage(ChatColor.RED + "That economy doesn't exist!");
    				}
    			} else {
    				sender.sendMessage(ChatColor.RED + "Invalid Parameters.  Use /setshopeconomy [shop name] [economy]");
    			}
			} else {
				sender.sendMessage(ChatColor.RED + "This command is only available when SQL is enabled!");
			}
		} catch (Exception e) {
			sender.sendMessage(ChatColor.RED + "Invalid Parameters.  Use /setshopeconomy [shop name] [economy]");
		}
	}
}
