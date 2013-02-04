package regalowl.hyperconomy;

import org.bukkit.command.CommandSender;

public class Renameshop {
	Renameshop(CommandSender sender, String[] args) {
		HyperConomy hc = HyperConomy.hc;
		LanguageFile L = hc.getLanguageFile();
		ShopFactory s = hc.getShopFactory();
		try {
			if (args.length == 2) {
				String name = s.fixShopName(args[0]);
				String newname = args[1];
				if (s.shopExists(name)) {
					s.getShop(name).setName(newname);
					sender.sendMessage(L.get("SHOP_RENAMED"));
				} else {
					sender.sendMessage(L.get("SHOP_NOT_EXIST"));
				}
			} else {
				sender.sendMessage(L.get("RENAMESHOP_INVALID"));
			}
		} catch (Exception e) {
			sender.sendMessage(L.get("RENAMESHOP_INVALID"));
		}
	}
}
