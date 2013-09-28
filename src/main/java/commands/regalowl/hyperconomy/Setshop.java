package regalowl.hyperconomy;

import org.bukkit.entity.Player;

public class Setshop {
	Setshop(String[] args, Player player) {
		HyperConomy hc = HyperConomy.hc;
		HyperEconomy he = hc.getEconomyManager().getEconomy(hc.getEconomyManager().getHyperPlayer(player.getName()).getEconomy());
		LanguageFile L = hc.getLanguageFile();
		if (args.length >= 2) {
			if (args[0].equalsIgnoreCase("p1")) {
				String name = args[1].replace(".", "").replace(":", "");
				if (he.shopExists(name)) {
					he.getShop(name).setPoint1(player);
				} else {
					Shop shop = new Shop(name, hc.getEconomyManager().getHyperPlayer(player.getName()));
					shop.setPoint1(player);
					shop.setPoint2(player);
					shop.setDefaultMessages();
					he.addShop(shop);
				}
				player.sendMessage(L.get("P1_SET"));
			} else if (args[0].equalsIgnoreCase("p2")) {
				String name = args[1].replace(".", "").replace(":", "");
				if (he.shopExists(name)) {
					he.getShop(name).setPoint2(player);
				} else {
					Shop shop = new Shop(name, hc.getEconomyManager().getHyperPlayer(player.getName()));
					shop.setPoint1(player);
					shop.setPoint2(player);
					shop.setDefaultMessages();
					he.addShop(shop);
				}
				player.sendMessage(L.get("P2_SET"));
			}
		} else {
			player.sendMessage(L.get("SETSHOP_INVALID"));
		}
	}
}
