package regalowl.hyperconomy;

import org.bukkit.command.CommandSender;

public class Renameeconomyaccount {
	Renameeconomyaccount(CommandSender sender, String[] args) {
		HyperConomy hc = HyperConomy.hc;
		LanguageFile L = hc.getLanguageFile();
		EconomyManager em = hc.getEconomyManager();
		try {
			if (args.length == 1) {
				String newaccount = args[0];
				String oldaccount = hc.gYH().gFC("config").getString("config.global-shop-account");
				em.getHyperPlayer(newaccount);
				em.getHyperPlayer(newaccount).setBalance(em.getHyperPlayer(oldaccount).getBalance());
				em.getHyperPlayer(oldaccount).setBalance(0);
				hc.gYH().gFC("config").set("config.global-shop-account", newaccount);
				sender.sendMessage(L.get("GLOBAL_SHOP_RENAMED"));
			} else {
				sender.sendMessage(L.get("RENAMEECONOMYACCOUNT_INVALID"));
			}
		} catch (Exception e) {
			sender.sendMessage(L.get("RENAMEECONOMYACCOUNT_INVALID"));
		}
	}
}
