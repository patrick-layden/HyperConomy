package regalowl.hyperconomy;

import org.bukkit.command.CommandSender;

public class Renameeconomyaccount {
	Renameeconomyaccount(CommandSender sender, String[] args) {
		HyperConomy hc = HyperConomy.hc;
		Account acc = hc.getAccount();
		LanguageFile L = hc.getLanguageFile();
		try {
			if (args.length == 1) {
				String newaccount = args[0];
				String oldaccount = hc.getYaml().getConfig().getString("config.global-shop-account");
				acc.createAccount(newaccount);
				acc.setBalance(acc.getBalance(oldaccount), newaccount);
				acc.setBalance(0, oldaccount);
				hc.getYaml().getConfig().set("config.global-shop-account", newaccount);
				sender.sendMessage(L.get("GLOBAL_SHOP_RENAMED"));
			} else {
				sender.sendMessage(L.get("RENAMEECONOMYACCOUNT_INVALID"));
			}
		} catch (Exception e) {
			sender.sendMessage(L.get("RENAMEECONOMYACCOUNT_INVALID"));
		}
	}
}
