package regalowl.hyperconomy;

import org.bukkit.command.CommandSender;

public class Removeshop {
	Removeshop(CommandSender sender, String[] args) {
		HyperConomy hc = HyperConomy.hc;
		LanguageFile L = hc.getLanguageFile();
		ShopFactory s = hc.getShopFactory();
		try {
			if (args.length == 1) {
				String name = s.fixShopName(args[0]);
				if (s.shopExists(name)) {
					s.removeShop(name);
					sender.sendMessage(L.f(L.get("HAS_BEEN_REMOVED"), name));
				} else {
					sender.sendMessage(L.get("SHOP_NOT_EXIST"));
				}
			} else {
				sender.sendMessage(L.get("REMOVE_SHOP_INVALID"));
			}
		} catch (Exception e) {
			sender.sendMessage(L.get("SHOP_NOT_EXIST"));
		}
	}
}
