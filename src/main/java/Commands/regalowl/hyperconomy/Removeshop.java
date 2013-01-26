package regalowl.hyperconomy;

import org.bukkit.command.CommandSender;

public class Removeshop {
	Removeshop(CommandSender sender, String[] args) {
		HyperConomy hc = HyperConomy.hc;
		LanguageFile L = hc.getLanguageFile();
		ShopFactory s = hc.getShopFactory();
		try {
			if (args.length > 0) {
				String name = "";
				String teststring = hc.getYaml().getShops().getString(name);
				if (teststring == null) {
					name = hc.fixsName(name);
				}
				s.removeShop(name);
				sender.sendMessage(L.f(L.get("HAS_BEEN_REMOVED"), name.replace("_", " ")));
			} else {
				sender.sendMessage(L.get("REMOVE_SHOP_INVALID"));
			}
		} catch (Exception e) {
			sender.sendMessage(L.get("SHOP_NOT_EXIST"));
		}
	}
}
