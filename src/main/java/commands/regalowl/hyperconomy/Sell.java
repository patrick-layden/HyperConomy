package regalowl.hyperconomy;

import org.bukkit.GameMode;
import org.bukkit.entity.Player;

public class Sell {
	HyperConomy hc;

	Sell(String args[], Player player, String playerecon) {
		hc = HyperConomy.hc;
		HyperEconomy he = hc.getEconomyManager().getEconomy(playerecon);
		LanguageFile L = hc.getLanguageFile();
		EconomyManager em = hc.getEconomyManager();
		try {
			if (player.getGameMode() == GameMode.CREATIVE && hc.gYH().gQFC("config").gB("block-selling-in-creative-mode")) {
				player.sendMessage(L.get("CANT_SELL_CREATIVE"));
				return;
			}
			if (em.inAnyShop(player)) {
				if (em.getHyperPlayer(player).hasSellPermission(em.getShop(player))) {
					String name = he.fixName(args[0]);
					HyperObject ho = he.getHyperObject(name, em.getShop(player));
					int amount = 0;

					if (ho instanceof HyperItem) {
						HyperItem hi = he.getHyperItem(name, em.getShop(player));
						if (args.length == 1) {
							amount = 1;
						} else {
							try {
								amount = Integer.parseInt(args[1]);
								if (amount > 10000) {
									amount = 10000;
								}
							} catch (Exception e) {
								String max = args[1];
								if (max.equalsIgnoreCase("max")) {
									amount = hi.count(player.getInventory());	
								} else {
									player.sendMessage(L.get("SELL_INVALID"));
									return;
								}
							}
						}
						Shop s = em.getShop(player);
						if (s.has(name)) {
							PlayerTransaction pt = new PlayerTransaction(TransactionType.SELL);
							pt.setHyperObject(ho);
							pt.setAmount(amount);
							pt.setTradePartner(s.getOwner());
							TransactionResponse response = em.getHyperPlayer(player).processTransaction(pt);
							response.sendMessages();
						} else {
							player.sendMessage(L.get("CANT_BE_TRADED"));
							return;
						}
					} else if (ho instanceof HyperXP) {
						HyperXP xp = he.getHyperXP(em.getShop(player));
						if (args.length == 1) {
							amount = 1;
						} else {
							try {
								amount = Integer.parseInt(args[1]);
								if (amount > 100000) {
									amount = 100000;
								}
							} catch (Exception e) {
								String max = args[1];
								if (max.equalsIgnoreCase("max")) {
									amount = xp.getTotalXpPoints(player);
								} else {
									player.sendMessage(L.get("SELL_INVALID"));
									return;
								}
							}
						}
						Shop s = em.getShop(player);
						if (s.has(name)) {
							PlayerTransaction pt = new PlayerTransaction(TransactionType.SELL);
							pt.setHyperObject(ho);
							pt.setAmount(amount);
							pt.setTradePartner(s.getOwner());
							TransactionResponse response = em.getHyperPlayer(player).processTransaction(pt);
							response.sendMessages();
						} else {
							player.sendMessage(L.get("CANT_BE_TRADED"));
							return;
						}
					} else {
						player.sendMessage(L.get("INVALID_ITEM_NAME"));
						return;
					}
				} else {
					player.sendMessage(L.get("NO_TRADE_PERMISSION"));
					return;
				}
			} else {
				player.sendMessage(L.get("MUST_BE_IN_SHOP"));
				return;
			}
		} catch (Exception e) {
			player.sendMessage(L.get("SELL_INVALID"));
			return;
		}
	}
}
