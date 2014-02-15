package regalowl.hyperconomy;

import org.bukkit.entity.Player;

public class Setshop {
	Setshop(String[] args, Player player) {
		HyperConomy hc = HyperConomy.hc;
		LanguageFile L = hc.getLanguageFile();
		EconomyManager em = hc.getEconomyManager();
		if (args.length >= 2) {
			if (args[0].equalsIgnoreCase("p1")) {
				String name = args[1].replace(".", "").replace(":", "");
				if (em.shopExists(name)) {
					em.getShop(name).setPoint1(player.getLocation());
				} else {
					HyperPlayer hp = hc.getEconomyManager().getHyperPlayer(player);
					Shop shop = new ServerShop(name, hp.getEconomy(), hc.getEconomyManager().getGlobalShopAccount());
					shop.setPoint1(player.getLocation());
					shop.setPoint2(player.getLocation());
					shop.setDefaultMessages();
					em.addShop(shop);
				}
				player.sendMessage(L.get("P1_SET"));
			} else if (args[0].equalsIgnoreCase("p2")) {
				String name = args[1].replace(".", "").replace(":", "");
				if (em.shopExists(name)) {
					em.getShop(name).setPoint2(player.getLocation());
				} else {
					HyperPlayer hp = hc.getEconomyManager().getHyperPlayer(player);
					Shop shop = new ServerShop(name, hp.getEconomy(), hc.getEconomyManager().getGlobalShopAccount());
					shop.setPoint1(player.getLocation());
					shop.setPoint2(player.getLocation());
					shop.setDefaultMessages();
					em.addShop(shop);
				}
				player.sendMessage(L.get("P2_SET"));
			} else if (args[0].equalsIgnoreCase("owner")) {
				HyperAccount owner = null;
				if (em.hasAccount(args[1])) {
					owner = em.getHyperPlayer(args[1]);
				} else {
					if (em.hasBank(args[1])) {
						owner = em.getHyperBank(args[1]);
					} else {
						player.sendMessage(L.get("ACCOUNT_NOT_EXIST"));
					}
				}
				String name = args[2].replace(".", "").replace(":", "");
				if (em.shopExists(name)) {
					em.getShop(name).setOwner(owner);
				} else {
					player.sendMessage(L.get("SHOP_NOT_EXIST"));
					
				}
				player.sendMessage(L.get("OWNER_SET"));
			}
		} else {
			player.sendMessage(L.get("SETSHOP_INVALID"));
		}
	}
}
