package regalowl.hyperconomy;

import org.bukkit.entity.Player;

public class Setshop {
	Setshop(String[] args, Player player) {
		HyperConomy hc = HyperConomy.hc;
		ShopFactory s = hc.getShopFactory();
		LanguageFile L = hc.getLanguageFile();
		if (args.length >= 2) {
			if (args[0].equalsIgnoreCase("p1")) {
				String name = args[1].replace(".", "").replace(":", "");
				s.getShop(name).setPoint1(player);
				player.sendMessage(L.get("P1_SET"));
			} else if (args[0].equalsIgnoreCase("p2")) {
				String name = args[1].replace(".", "").replace(":", "");
				s.getShop(name).setPoint2(player);
				player.sendMessage(L.get("P2_SET"));
			}
		} else {
			player.sendMessage(L.get("SETSHOP_INVALID"));
		}
	}
}
