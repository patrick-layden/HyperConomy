package regalowl.hyperconomy;

import org.bukkit.entity.Player;

public class Buyid {
	Buyid(String args[], Player player) {
		HyperConomy hc = HyperConomy.hc;
		LanguageFile L = hc.getLanguageFile();
		ShopFactory s = hc.getShopFactory();
		Transaction tran = hc.getTransaction();
		int amount;
		int itd;
		int da = 0;
		try {
			if (s.getShop(player) != null) {
				if (!hc.getYaml().getConfig().getBoolean("config.use-shop-permissions") || player.hasPermission("hyperconomy.shop.*") || player.hasPermission("hyperconomy.shop." + s.getShop(player)) || player.hasPermission("hyperconomy.shop." + s.getShop(player) + ".buy")) {
					if (args.length == 2) {
						amount = Integer.parseInt(args[0]);
						itd = Integer.parseInt(args[1]);
					} else if (args.length == 3) {
						amount = Integer.parseInt(args[0]);
						itd = Integer.parseInt(args[1]);
						da = Integer.parseInt(args[2]);
					} else {
						player.sendMessage(L.get("BUYID_INVALID"));
						return;
					}
					String ke = itd + ":" + da;
					String nam = hc.getnameData(ke);
					if (nam == null) {
						player.sendMessage(L.get("OBJECT_NOT_AVAILABLE"));
					} else {
						if (s.getShop(player).has(nam)) {
							tran.buy(nam, amount, itd, da, player);
						} else {
							player.sendMessage(L.get("CANT_BE_TRADED"));
						}
					}
				} else {
					player.sendMessage(L.get("NO_TRADE_PERMISSION"));
				}
			} else {
				player.sendMessage(L.get("MUST_BE_IN_SHOP"));
			}
		} catch (Exception e) {
			player.sendMessage(L.get("BUYID_INVALID"));
		}
	}
}
