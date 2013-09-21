package regalowl.hyperconomy;

import org.bukkit.command.CommandSender;

public class Setbalance {
	Setbalance(CommandSender sender, String[] args) {
		HyperConomy hc = HyperConomy.hc;
		LanguageFile L = hc.getLanguageFile();
		EconomyManager em = hc.getEconomyManager();
		try {
			if (args.length == 2) {
				String accountname = args[0];
				if (em.hyperPlayerExists(accountname)) {
					Double balance = Double.parseDouble(args[1]);
					em.getHyperPlayer(accountname).setBalance(balance);
					sender.sendMessage(L.get("BALANCE_SET"));
				} else {
					sender.sendMessage(L.get("ACCOUNT_NOT_EXIST"));
				}
			} else {
				sender.sendMessage(L.get("SETBALANCE_INVALID"));
			}
		} catch (Exception e) {
			sender.sendMessage(L.get("SETBALANCE_INVALID"));
		}
	}
}
