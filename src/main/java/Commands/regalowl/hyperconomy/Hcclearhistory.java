package regalowl.hyperconomy;

import org.bukkit.command.CommandSender;

public class Hcclearhistory {
	
	
	Hcclearhistory(String args[], CommandSender sender) {
		HyperConomy hc = HyperConomy.hc;
		LanguageFile L = hc.getLanguageFile();
		History hist = hc.getHistory();
		try {
			hist.clearHistory();
			sender.sendMessage(L.get("HCCLEARHISTORY_CLEARED"));
		} catch (Exception e) {
			sender.sendMessage(L.get("HCCLEARHISTORY_INVALID"));
		}
	}
}
