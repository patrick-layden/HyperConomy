package regalowl.hyperconomy;

import org.bukkit.entity.Player;
import static regalowl.hyperconomy.Messages.*;

public class Buyid {
	Buyid(String args[], Player player) {
		HyperConomy hc = HyperConomy.hc;
		Shop s = hc.getShop();
		Transaction tran = hc.getTransaction();
		int amount;
		int itd;
		int da = 0;
		try {
			s.setinShop(player);
			if (s.inShop() != -1) {
				if (!hc.getYaml().getConfig().getBoolean("config.use-shop-permissions") || player.hasPermission("hyperconomy.shop.*") || player.hasPermission("hyperconomy.shop." + s.getShop(player)) || player.hasPermission("hyperconomy.shop." + s.getShop(player) + ".buy")) {
					if (args.length == 2) {
						amount = Integer.parseInt(args[0]);
						itd = Integer.parseInt(args[1]);
					} else if (args.length == 3) {
						amount = Integer.parseInt(args[0]);
						itd = Integer.parseInt(args[1]);
						da = Integer.parseInt(args[2]);
					} else {
						player.sendMessage(BUYID_INVALID);
						return;
					}
					String ke = itd + ":" + da;
					String nam = hc.getnameData(ke);
					if (nam == null) {
						player.sendMessage(OBJECT_NOT_AVAILABLE);
					} else {
						if (s.has(s.getShop(player), nam)) {
							tran.buy(nam, amount, itd, da, player);
						} else {
							player.sendMessage(CANT_BE_TRADED);
						}
					}
				} else {
					player.sendMessage(NO_TRADE_PERMISSION);
				}
			} else {
				player.sendMessage(MUST_BE_IN_SHOP);
			}
		} catch (Exception e) {
			player.sendMessage(BUYID_INVALID);
		}
	}
}
