package regalowl.hyperconomy;

import org.bukkit.command.CommandSender;

public class Makeaccount {
	Makeaccount(String args[], CommandSender sender) {
		HyperConomy hc = HyperConomy.hc;
		LanguageFile L = hc.getLanguageFile();
		Account acc = hc.getAccount();
		try {
			if (args.length == 1) {
				String account = args[0];
				if (!acc.checkAccount(account)) {
					boolean success = acc.createAccount(account);
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
