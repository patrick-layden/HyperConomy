package regalowl.hyperconomy;

import org.bukkit.GameMode;
import org.bukkit.entity.Player;

public class Sell {
	HyperConomy hc;

	Sell(String args[], Player player, String playerecon) {
		hc = HyperConomy.hc;
		DataHandler sf = hc.getDataFunctions();
		LanguageFile L = hc.getLanguageFile();
		ShopFactory s = hc.getShopFactory();
		InventoryManipulation im = hc.getInventoryManipulation();
		try {
			if (player.getGameMode() == GameMode.CREATIVE && hc.s().blockCreative()) {
				player.sendMessage(L.get("CANT_SELL_CREATIVE"));
				return;
			}
			if (s.inAnyShop(player)) {
				if (sf.getHyperPlayer(player).hasSellPermission(s.getShop(player))) {
					String name = sf.fixName(args[0]);
					int amount = 0;
					boolean xp = false;

					if (sf.itemTest(name)) {
						HyperObject ho = sf.getHyperObject(name, playerecon);
						if (ho.getType() == HyperObjectType.EXPERIENCE) {
							xp = true;
						}
						
						if (args.length == 1) {
							amount = 1;
						} else {
							try {
								amount = Integer.parseInt(args[1]);
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
					if (sf.itemTest(name)) {
						HyperObject ho = sf.getHyperObject(name, playerecon);
						if (s.getShop(player).has(name)) {
							PlayerTransaction pt = new PlayerTransaction(TransactionType.SELL);
							pt.setHyperObject(ho);
							pt.setAmount(amount);
							TransactionResponse response = sf.getHyperPlayer(player).processTransaction(pt);
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
