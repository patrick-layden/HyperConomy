package regalowl.hyperconomy;

import org.bukkit.command.CommandSender;

public class Hcclearhistory {
	
	
	Hcclearhistory(String args[], CommandSender sender) {
		HyperConomy hc = HyperConomy.hc;
		LanguageFile L = hc.getLanguageFile();
		DataFunctions sf = hc.getDataFunctions();

		try {
			sf.clearHistory();
			sender.sendMessage(L.get("HCCLEARHISTORY_CLEARED"));
		} catch (Exception e) {
			sender.sendMessage(L.get("HCCLEARHISTORY_INVALID"));
		}
	}
}
