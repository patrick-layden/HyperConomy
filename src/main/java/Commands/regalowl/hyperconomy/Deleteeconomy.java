package regalowl.hyperconomy;

import org.bukkit.command.CommandSender;

public class Deleteeconomy {
	Deleteeconomy(String args[], CommandSender sender) {
		HyperConomy hc = HyperConomy.hc;
		LanguageFile L = hc.getLanguageFile();
		try {
			if (hc.useSQL()) {
    			if (args.length == 1) {
    				String economy = args[0];
    				if (economy.equalsIgnoreCase("default")) {
    					sender.sendMessage(L.get("CANT_DELETE_DEFAULT_ECONOMY"));
    					return;
    				}
    				if (hc.getSQLFunctions().testEconomy(economy)) {
    					if (hc.getYaml().getConfig().getBoolean("config.run-automatic-backups")) {
    						new Backup();
    					}
    					hc.getSQLEconomy().deleteEconomy(economy);
    					sender.sendMessage(L.get("ECONOMY_DELETED"));
    				} else {
    					sender.sendMessage(L.get("ECONOMY_DOESNT_EXIST"));
    				}
				} else {
					sender.sendMessage(L.get("DELETEECONOMY_INVALID"));
				}
			} else {
				sender.sendMessage(L.get("ONLY_AVAILABLE_SQL"));
			}
		} catch (Exception e) {
			sender.sendMessage(L.get("DELETEECONOMY_INVALID"));
		}
	}
}
