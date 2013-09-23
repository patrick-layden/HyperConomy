package regalowl.hyperconomy;

import org.bukkit.command.CommandSender;

public class Setshopeconomy {
	Setshopeconomy(String args[], CommandSender sender) {
		HyperConomy hc = HyperConomy.hc;
		LanguageFile L = hc.getLanguageFile();
		try {
			if (args.length == 2) {
				String name = args[0];
				if (!hc.getEconomyManager().shopExists(name)) {
					sender.sendMessage(L.get("SHOP_NOT_EXIST"));
					return;
				}
				String economy = args[1];
				if (hc.getEconomyManager().economyExists(economy)) {
					hc.getEconomyManager().getShop(name).setEconomy(economy);
					sender.sendMessage(L.get("SHOP_ECONOMY_SET"));
				} else {
					sender.sendMessage(L.get("ECONOMY_DOESNT_EXIST"));
				}
			} else {
				sender.sendMessage(L.get("SETSHOPECONOMY_INVALID"));
			}
		} catch (Exception e) {
			sender.sendMessage(L.get("SETSHOPECONOMY_INVALID"));
		}
	}
}
