package regalowl.hyperconomy;

import org.bukkit.command.CommandSender;

public class Makeaccount {
	Makeaccount(String args[], CommandSender sender) {
		HyperConomy hc = HyperConomy.hc;
		LanguageFile L = hc.getLanguageFile();
		EconomyManager em = hc.getEconomyManager();
		try {
			if (args.length == 1) {
				String account = args[0];
				if (!em.hasAccount(account)) {
					boolean success = em.createPlayerAccount(account);
					if (success) {
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
