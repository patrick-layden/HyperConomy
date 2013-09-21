package regalowl.hyperconomy;

import org.bukkit.command.CommandSender;

public class Renameshop {
	Renameshop(CommandSender sender, String[] args) {
		HyperConomy hc = HyperConomy.hc;
		LanguageFile L = hc.getLanguageFile();
		EconomyManager em = hc.getEconomyManager();
		try {
			if (args.length == 2) {
				String name = args[0];
				String newname = args[1];
				if (em.shopExists(name)) {
					em.getShop(name).setName(newname);
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
