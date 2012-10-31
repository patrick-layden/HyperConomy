package regalowl.hyperconomy;

import org.bukkit.entity.Player;

public class Setshop {
	Setshop(String[] args, Player player) {
		HyperConomy hc = HyperConomy.hc;
		Shop s = hc.getShop();
		LanguageFile L = hc.getLanguageFile();
		if (args.length >= 2) {
			if (args[0].equalsIgnoreCase("p1")) {
				int counter = 1;
				String name = "";
				while (counter < args.length) {
					if (counter == 1) {
						name = args[1];
					} else {
						name = name + "_" + args[counter];
					}
					counter++;
				}
				String teststring = hc.getYaml().getShops().getString(name);
				if (teststring == null) {
					name = hc.fixsName(name);
				}
				name = name.replace(".", "").replace(":", "");
				s.setsShop(name, player);
				s.setShop1();
				player.sendMessage(L.get("P1_SET"));
			} else if (args[0].equalsIgnoreCase("p2")) {
				int counter = 1;
				String name = "";
				while (counter < args.length) {
					if (counter == 1) {
						name = args[1];
					} else {
						name = name + "_" + args[counter];
					}
					counter++;
				}
				name = name.replace(".", "").replace(":", "");
				String teststring = hc.getYaml().getShops().getString(name);
				if (teststring == null) {
					name = hc.fixsName(name);
				}
				s.setsShop(name, player);
				s.setShop2();
				player.sendMessage(L.get("P2_SET"));
			}
		} else {
			player.sendMessage(L.get("SETSHOP_INVALID"));
		}
	}
}
