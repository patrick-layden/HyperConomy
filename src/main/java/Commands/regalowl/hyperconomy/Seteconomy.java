package regalowl.hyperconomy;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Seteconomy {
	Seteconomy(_Command command, String args[], CommandSender sender, Player player) {
		HyperConomy hc = HyperConomy.hc;
		SQLFunctions sf = hc.getSQLFunctions();
		LanguageFile L = hc.getLanguageFile();
		try {
			if (hc.useSQL()) {
				if (args.length == 1) {
    				String economy = args[0];
    				if (hc.getSQLFunctions().testEconomy(economy)) {
    		    		if (player != null) {
        					sf.setPlayerEconomy(player.getName(), economy);
        					sender.sendMessage(L.get("ECONOMY_SET"));
    		    		} else {
    		    			command.setNonPlayerEconomy(economy);
    		    			sender.sendMessage(L.get("ECONOMY_SET"));
    		    		}
    				} else {
    					sender.sendMessage(L.get("ECONOMY_NOT_EXIST"));
    				}
    				
    			} else {
    				sender.sendMessage(L.get("SETECONOMY_INVALID"));
    			}
			} else {
				sender.sendMessage(L.get("ONLY_AVAILABLE_SQL"));
			}
		} catch (Exception e) {
			sender.sendMessage(L.get("SETECONOMY_INVALID"));
		}
	}
}
