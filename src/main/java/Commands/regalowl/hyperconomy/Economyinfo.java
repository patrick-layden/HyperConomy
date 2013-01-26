package regalowl.hyperconomy;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Economyinfo {
	Economyinfo(_Command command, String args[], CommandSender sender, Player player) {
		HyperConomy hc = HyperConomy.hc;
		DataFunctions sf = hc.getDataFunctions();
		LanguageFile L = hc.getLanguageFile();
		try {
			if (hc.useSQL()) {
    			if (args.length == 0) {
    				if (player != null) {
    					//sender.sendMessage(ChatColor.BLUE + "You are currently a part of the " + ChatColor.AQUA + "" + sf.getPlayerEconomy(player.getName()) + ChatColor.BLUE + " economy.");	
    					sender.sendMessage(L.f(L.get("PART_OF_ECONOMY"), sf.getPlayerEconomy(player.getName())));
    	    		} else {
    	    			//sender.sendMessage(ChatColor.BLUE + "You are currently a part of the " + ChatColor.AQUA + "" + command.getNonPlayerEconomy() + ChatColor.BLUE + " economy.");
    	    			sender.sendMessage(L.f(L.get("PART_OF_ECONOMY"), command.getNonPlayerEconomy()));
    	    		}
    			} else {
    				sender.sendMessage(L.get("ECONOMYINFO_INVALID"));
    			}
			} else {
				sender.sendMessage(L.get("ONLY_AVAILABLE_SQL"));
			}
		} catch (Exception e) {
			sender.sendMessage(L.get("ECONOMYINFO_INVALID"));
		}
	}
}
