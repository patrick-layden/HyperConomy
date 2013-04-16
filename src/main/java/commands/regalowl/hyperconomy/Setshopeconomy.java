package regalowl.hyperconomy;

import org.bukkit.command.CommandSender;

public class Setshopeconomy {
	Setshopeconomy(String args[], CommandSender sender) {
		HyperConomy hc = HyperConomy.hc;
		LanguageFile L = hc.getLanguageFile();
		ShopFactory s = hc.getShopFactory();
		try {
			if (args.length == 2) {
				String name = args[0];
				String teststring = hc.getYaml().getShops().getString(name);
				if (teststring == null) {
					name = s.fixShopName(name);
				}
				if (name == null) {
					sender.sendMessage(L.get("SHOP_NOT_EXIST"));
					return;
				}
				String economy = args[1];
				if (hc.getDataFunctions().testEconomy(economy)) {
					s.getShop(name).setEconomy(economy);
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
