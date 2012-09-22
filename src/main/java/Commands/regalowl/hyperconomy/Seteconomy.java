package regalowl.hyperconomy;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Seteconomy {
	Seteconomy(Cmd command, String args[], CommandSender sender, Player player) {
		HyperConomy hc = HyperConomy.hc;
		SQLFunctions sf = hc.getSQLFunctions();
		try {
			if (hc.useSQL()) {
				if (args.length == 1) {
    				String economy = args[0];
    				if (hc.getSQLFunctions().testEconomy(economy)) {
    		    		if (player != null) {
        					sf.setPlayerEconomy(player.getName(), economy);
        					sender.sendMessage(ChatColor.GOLD + "Economy set!");
    		    		} else {
    		    			command.setNonPlayerEconomy(economy);
    		    			sender.sendMessage(ChatColor.GOLD + "Economy set!");
    		    		}
    				} else {
    					sender.sendMessage(ChatColor.RED + "That economy doesn't exist!");
    				}
    				
    			} else {
    				sender.sendMessage(ChatColor.RED + "Invalid Parameters.  Use /seteconomy [name]");
    			}
			} else {
				sender.sendMessage(ChatColor.RED + "This command is only available when SQL is enabled!");
			}
		} catch (Exception e) {
			sender.sendMessage(ChatColor.RED + "Invalid Parameters.  Use /seteconomy [name]");
		}
	}
}
