package regalowl.hyperconomy;


import org.bukkit.entity.Player;

public class Buy {
	HyperConomy hc;
	Buy(String args[], Player player, String playerecon) {
		hc = HyperConomy.hc;
		HyperEconomy he = hc.getEconomyManager().getEconomy(playerecon);
		EconomyManager em = hc.getEconomyManager();
		LanguageFile L = hc.getLanguageFile();
		try {
			if (em.inAnyShop(player)) {
				HyperPlayer hp = em.getHyperPlayer(player);
				if (hp.hasBuyPermission(em.getShop(player))) {
					String name = he.fixName(args[0]);
					int id = 0;
					int amount = 0;
					HyperObject ho = he.getHyperObject(name, em.getShop(player));
					if (ho instanceof HyperItem) {
						HyperItem hi = (HyperItem)ho;
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
									int space = 0;
									if (id >= 0) {
										space = hi.getAvailableSpace(player.getInventory());
									}
									amount = space;
									int shopstock = (int) ho.getStock();
									if (amount > shopstock) {
										amount = shopstock;
									}
								} else {
									player.sendMessage(L.get("BUY_INVALID"));
									return;
								}
							}
						}
						Shop s = em.getShop(player);
						if (s.has(name)) {
							PlayerTransaction pt = new PlayerTransaction(TransactionType.BUY);
							pt.setHyperObject(ho);
							pt.setAmount(amount);
							pt.setTradePartner(s.getOwner());
							TransactionResponse response = hp.processTransaction(pt);
							response.sendMessages();
						} else {
							player.sendMessage(L.get("CANT_BE_TRADED"));
							return;
						}
					} else if (ho instanceof HyperXP) {
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
									int shopstock = (int) ho.getStock();
									if (amount > shopstock) {
										amount = shopstock;
									}
								} else {
									player.sendMessage(L.get("BUY_INVALID"));
									return;
								}
							}
						}
						Shop s = em.getShop(player);
						if (s.has(name)) {
							PlayerTransaction pt = new PlayerTransaction(TransactionType.BUY);
							pt.setHyperObject(ho);
							pt.setAmount(amount);
							pt.setTradePartner(s.getOwner());
							TransactionResponse response = hp.processTransaction(pt);
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
			player.sendMessage(L.get("BUY_INVALID"));
			return;
		}
	}
}
