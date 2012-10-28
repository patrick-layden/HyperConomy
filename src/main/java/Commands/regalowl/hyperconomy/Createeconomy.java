package regalowl.hyperconomy;

import org.bukkit.command.CommandSender;

public class Createeconomy {
	Createeconomy(String args[], CommandSender sender) {
		HyperConomy hc = HyperConomy.hc;
		LanguageFile L = hc.getLanguageFile();
		try {
			if (hc.useSQL()) {
    			if (args.length == 1) {
    				String economy = args[0];
    				if (!hc.getSQLFunctions().testEconomy(economy)) {
    					hc.getSQLEconomy().createNewEconomy(economy);
    					sender.sendMessage(L.get("NEW_ECONOMY_CREATED"));
    				} else {
    					sender.sendMessage(L.get("ECONOMY_ALREADY_EXISTS"));
    				}
    			} else {
    				sender.sendMessage(L.get("CREATEECONOMY_INVALID"));
    			}
			} else {
				sender.sendMessage(L.get("ONLY_AVAILABLE_SQL"));
			}
		} catch (Exception e) {
			sender.sendMessage(L.get("CREATEECONOMY_INVALID"));
		}
	}
}
