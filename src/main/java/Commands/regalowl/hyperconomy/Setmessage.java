package regalowl.hyperconomy;

import org.bukkit.command.CommandSender;

public class Setmessage {
	Setmessage(String[] args, CommandSender sender) {
		HyperConomy hc = HyperConomy.hc;
		ShopFactory s = hc.getShopFactory();
		LanguageFile L = hc.getLanguageFile();
		try {
			if (args.length >= 3) {
				if (args[0].equalsIgnoreCase("1")) {
					String message = args[1];
					message = message.replace("%s", " ");
					int counter = 2;
					String name = "";
					while (counter < args.length) {
						if (counter == 2) {
							name = args[2];
						} else {
							name = name + "_" + args[counter];
						}
						counter++;
					}
					String teststring = hc.getYaml().getShops().getString(name);
					if (teststring == null) {
						name = s.fixShopName(name);
					}
					if (s.shopExists(name)) {
						s.getShop(name).setMessage1(message);
						sender.sendMessage(L.get("MESSAGE1_SET"));
						return;
					}
					sender.sendMessage(L.get("SHOP_NOT_EXIST"));
				} else if (args[0].equalsIgnoreCase("2")) {
					String message = args[1];
					message = message.replace("%s", " ");
					int counter = 2;
					String name = "";
					while (counter < args.length) {
						if (counter == 2) {
							name = args[2];
						} else {
							name = name + "_" + args[counter];
						}
						counter++;
					}
					String teststring = hc.getYaml().getShops().getString(name);
					if (teststring == null) {
						name = s.fixShopName(name);
					}
					if (s.shopExists(name)) {
						s.getShop(name).setMessage2(message);
						sender.sendMessage(L.get("MESSAGE2_SET"));
						return;
					}
					sender.sendMessage(L.get("SHOP_NOT_EXIST"));
				}
			} else {
				sender.sendMessage(L.get("SETMESSAGE_INVALID"));
			}
		} catch (Exception e) {
			sender.sendMessage(L.get("SETMESSAGE_INVALID"));
		}
	}
}
