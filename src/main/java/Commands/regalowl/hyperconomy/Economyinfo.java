package regalowl.hyperconomy;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Economyinfo {
	Economyinfo(_Command command, String args[], CommandSender sender, Player player) {
		HyperConomy hc = HyperConomy.hc;
		SQLFunctions sf = hc.getSQLFunctions();
		try {
			if (hc.useSQL()) {
    			if (args.length == 0) {
    				if (player != null) {
    					sender.sendMessage(ChatColor.BLUE + "You are currently a part of the " + ChatColor.AQUA + "" + sf.getPlayerEconomy(player.getName()) + ChatColor.BLUE + " economy.");	
    	    		} else {
    	    			sender.sendMessage(ChatColor.BLUE + "You are currently a part of the " + ChatColor.AQUA + "" + command.getNonPlayerEconomy() + ChatColor.BLUE + " economy.");
    	    		}
    			} else {
    				sender.sendMessage(ChatColor.RED + "Invalid Parameters.  Use /economyinfo");
    			}
			} else {
				sender.sendMessage(ChatColor.RED + "This command is only available when SQL is enabled!");
			}
		} catch (Exception e) {
			sender.sendMessage(ChatColor.RED + "Invalid Parameters.  Use /economyinfo");
		}
	}
}
