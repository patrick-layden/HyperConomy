package regalowl.hyperconomy;

import org.bukkit.entity.Player;

public class Setshop {
	Setshop(String[] args, Player player) {
		HyperConomy hc = HyperConomy.hc;
		Shop s = hc.getShop();
		LanguageFile L = hc.getLanguageFile();
		if (args.length >= 2) {
			if (args[0].equalsIgnoreCase("p1")) {
				String name = args[1].replace(".", "").replace(":", "");
				s.setPoint1(name, player);
				player.sendMessage(L.get("P1_SET"));
			} else if (args[0].equalsIgnoreCase("p2")) {
				String name = args[1].replace(".", "").replace(":", "");
				s.setPoint2(name, player);
				player.sendMessage(L.get("P2_SET"));
			}
		} else {
			player.sendMessage(L.get("SETSHOP_INVALID"));
		}
	}
}
