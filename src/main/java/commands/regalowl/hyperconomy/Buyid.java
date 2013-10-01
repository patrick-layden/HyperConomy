package regalowl.hyperconomy;

import org.bukkit.entity.Player;

public class Buyid {
	Buyid(String args[], Player player) {
		HyperConomy hc = HyperConomy.hc;
		LanguageFile L = hc.getLanguageFile();
		int amount;
		int itd;
		int da = 0;
		try {
			HyperEconomy he = hc.getEconomyManager().getHyperPlayer(player.getName()).getHyperEconomy();
			if (he.inAnyShop(player)) {
				HyperPlayer hp = he.getHyperPlayer(player);
				if (hp.hasBuyPermission(he.getShop(player))) {
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
					if (amount > 10000) {
						amount = 10000;
					}
					HyperObject ho = he.getHyperObject(itd, da, he.getShop(player));
					if (ho == null) {
						player.sendMessage(L.get("OBJECT_NOT_AVAILABLE"));
					} else {
						String nam = ho.getName();
						Shop s = he.getShop(player);
						if (s.has(nam)) {
							PlayerTransaction pt = new PlayerTransaction(TransactionType.BUY);
							pt.setHyperObject(ho);
							pt.setAmount(amount);
							pt.setTradePartner(s.getOwner());
							TransactionResponse response = hp.processTransaction(pt);
							response.sendMessages();
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
