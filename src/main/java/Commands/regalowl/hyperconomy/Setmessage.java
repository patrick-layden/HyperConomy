package regalowl.hyperconomy;

import org.bukkit.command.CommandSender;

public class Setmessage {
	Setmessage(String[] args, CommandSender sender) {
		HyperConomy hc = HyperConomy.hc;
		Shop s = hc.getShop();
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
						name = hc.fixsName(name);
					}
					int i = 0;
					while (i < s.getshopdataSize()) {
						if (name.equalsIgnoreCase(s.getshopData(i))) {
							s.setMessage1(i, message);
							hc.getYaml().getShops().set(s.getshopData(i) + ".shopmessage1", message);
							sender.sendMessage(L.get("MESSAGE1_SET"));
							return;
						}
						i++;
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
						name = hc.fixsName(name);
					}
					int i = 0;
					while (i < s.getshopdataSize()) {
						if (name.equalsIgnoreCase(s.getshopData(i))) {
							s.setMessage2(i, message);
							hc.getYaml().getShops().set(s.getshopData(i) + ".shopmessage2", message);
							sender.sendMessage(L.get("MESSAGE2_SET"));
							return;
						}
						i++;
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
