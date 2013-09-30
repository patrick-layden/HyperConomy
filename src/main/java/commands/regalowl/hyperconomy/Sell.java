package regalowl.hyperconomy;

import org.bukkit.GameMode;
import org.bukkit.entity.Player;

public class Sell {
	HyperConomy hc;

	Sell(String args[], Player player, String playerecon) {
		hc = HyperConomy.hc;
		HyperEconomy he = hc.getEconomyManager().getEconomy(playerecon);
		LanguageFile L = hc.getLanguageFile();
		InventoryManipulation im = hc.getInventoryManipulation();
		try {
			if (player.getGameMode() == GameMode.CREATIVE && hc.s().gB("block-selling-in-creative-mode")) {
				player.sendMessage(L.get("CANT_SELL_CREATIVE"));
				return;
			}
			if (he.inAnyShop(player)) {
				if (he.getHyperPlayer(player).hasSellPermission(he.getShop(player))) {
					String name = he.fixName(args[0]);
					int amount = 0;
					boolean xp = false;

					if (he.itemTest(name)) {
						HyperObject ho = he.getHyperObject(name, he.getShop(player));
						if (ho.getType() == HyperObjectType.EXPERIENCE) {
							xp = true;
						}
						
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
									if (xp) {
										amount = im.gettotalxpPoints(player);
									} else {
										amount = im.countItems(ho.getId(), ho.getData(), player.getInventory());	
									}
								} else {
									player.sendMessage(L.get("SELL_INVALID"));
									return;
								}
							}
						}
					}
					if (he.itemTest(name)) {
						HyperObject ho = he.getHyperObject(name, he.getShop(player));
						if (he.getShop(player).has(name)) {
							PlayerTransaction pt = new PlayerTransaction(TransactionType.SELL);
							pt.setHyperObject(ho);
							pt.setAmount(amount);
							TransactionResponse response = he.getHyperPlayer(player).processTransaction(pt);
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
