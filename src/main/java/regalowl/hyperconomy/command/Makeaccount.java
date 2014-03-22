package regalowl.hyperconomy.command;

import org.bukkit.command.CommandSender;

import regalowl.hyperconomy.DataManager;
import regalowl.hyperconomy.HyperConomy;
import regalowl.hyperconomy.account.HyperPlayer;
import regalowl.hyperconomy.util.LanguageFile;

public class Makeaccount {
	Makeaccount(String args[], CommandSender sender) {
		HyperConomy hc = HyperConomy.hc;
		LanguageFile L = hc.getLanguageFile();
		DataManager em = hc.getDataManager();
		try {
			if (args.length == 1) {
				String account = args[0];
				if (!em.accountExists(account)) {
					HyperPlayer hp = em.addPlayer(account);
					if (hp != null) {
						sender.sendMessage(L.get("MAKEACCOUNT_SUCCESS"));
					} else {
						sender.sendMessage(L.get("MAKEACCOUNT_FAILED"));
					}
				} else {
					sender.sendMessage(L.get("ACCOUNT_ALREADY_EXISTS"));
				}
			} else {
				sender.sendMessage(L.get("MAKEACCOUNT_INVALID"));
			}
		} catch (Exception e) {
			sender.sendMessage(L.get("MAKEACCOUNT_INVALID"));
		}
	}
}