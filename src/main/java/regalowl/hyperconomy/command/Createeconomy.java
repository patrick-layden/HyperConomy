package regalowl.hyperconomy.command;

import org.bukkit.command.CommandSender;

import regalowl.hyperconomy.HyperConomy;
import regalowl.hyperconomy.util.LanguageFile;

public class Createeconomy {
	Createeconomy(String args[], CommandSender sender) {
		HyperConomy hc = HyperConomy.hc;
		LanguageFile L = hc.getLanguageFile();
		try {
			if (args.length == 1) {
				String economy = args[0];
				if (!hc.getDataManager().economyExists(economy)) {
					hc.getDataManager().createNewEconomy(economy);
					sender.sendMessage(L.get("NEW_ECONOMY_CREATED"));
				} else {
					sender.sendMessage(L.get("ECONOMY_ALREADY_EXISTS"));
				}
			} else {
				sender.sendMessage(L.get("CREATEECONOMY_INVALID"));
			}
		} catch (Exception e) {
			sender.sendMessage(L.get("CREATEECONOMY_INVALID"));
		}
	}
}
