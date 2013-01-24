package regalowl.hyperconomy;

import org.bukkit.command.CommandSender;

public class Removeshop {
	Removeshop(CommandSender sender, String[] args) {
		HyperConomy hc = HyperConomy.hc;
		LanguageFile L = hc.getLanguageFile();
		Shop s = hc.getShop();
		try {
			if (args.length > 0) {
				int counter = 0;
				String name = "";
				while (counter < args.length) {
					if (counter == 0) {
						name = args[0];
					} else {
						name = name + "_" + args[counter];
					}
					counter++;
				}
				String teststring = hc.getYaml().getShops().getString(name);
				if (teststring == null) {
					name = hc.fixsName(name);
				}
				s.removeShop(name);
				//sender.sendMessage(ChatColor.GOLD + name.replace("_", " ") + " has been removed!");
				sender.sendMessage(L.f(L.get("HAS_BEEN_REMOVED"), name.replace("_", " ")));
			} else {
				sender.sendMessage(L.get("REMOVE_SHOP_INVALID"));
			}
		} catch (Exception e) {
			sender.sendMessage(L.get("SHOP_NOT_EXIST"));
		}
	}
}
