package regalowl.hyperconomy;

import org.bukkit.entity.Player;
import static regalowl.hyperconomy.Messages.*;

public class Buyxp {
	Buyxp(String args[], Player player) {
		HyperConomy hc = HyperConomy.hc;
		Shop s = hc.getShop();
		Transaction tran = hc.getTransaction();
		try {
			s.setinShop(player);
			if (s.inShop() != -1) {
				if (!hc.getYaml().getConfig().getBoolean("config.use-shop-permissions") || player.hasPermission("hyperconomy.shop.*") || player.hasPermission("hyperconomy.shop." + s.getShop(player)) || player.hasPermission("hyperconomy.shop." + s.getShop(player) + ".buy")) {
					if (args.length <= 1) {
						int amount;
						if (args.length == 0) {
							amount = 1;
						} else {
							amount = Integer.parseInt(args[0]);
						}
						String ke = -1 + ":" + -1;
						String nam = hc.getnameData(ke);
						tran.buyXP(nam, amount, player);
					} else {
						player.sendMessage(BUYXP_INVALID);
					}
				} else {
					player.sendMessage(NO_TRADE_PERMISSION);
				}
			} else {
				player.sendMessage(MUST_BE_IN_SHOP);
			}
		} catch (Exception e) {
			player.sendMessage(BUYXP_INVALID);
		}
	}
}
