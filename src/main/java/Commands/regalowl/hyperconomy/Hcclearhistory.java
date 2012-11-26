package regalowl.hyperconomy;

import org.bukkit.command.CommandSender;

public class Hcclearhistory {
	
	
	Hcclearhistory(String args[], CommandSender sender) {
		HyperConomy hc = HyperConomy.hc;
		LanguageFile L = hc.getLanguageFile();
		DataFunctions sf = hc.getSQLFunctions();
		
		try {
			if (hc.useSQL()) {
				sf.clearHistory();
				sender.sendMessage(L.get("HCCLEARHISTORY_CLEARED"));
			} else {
				sender.sendMessage(L.get("ONLY_AVAILABLE_SQL"));
			}
		} catch (Exception e) {
			sender.sendMessage(L.get("HCCLEARHISTORY_INVALID"));
		}
	}
}
