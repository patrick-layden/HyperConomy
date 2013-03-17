package regalowl.hyperconomy;

import org.bukkit.entity.Player;

public class Sell {
	HyperConomy hc;

	Sell(String args[], Player player, String playerecon) {
		hc = HyperConomy.hc;
		DataHandler sf = hc.getDataFunctions();
		Transaction tran = hc.getTransaction();
		LanguageFile L = hc.getLanguageFile();
		Calculation calc = hc.getCalculation();
		ShopFactory s = hc.getShopFactory();
		try {
			if (s.inAnyShop(player)) {
				if (!hc.getYaml().getConfig().getBoolean("config.use-shop-permissions") || player.hasPermission("hyperconomy.shop.*") || player.hasPermission("hyperconomy.shop." + s.getShop(player)) || player.hasPermission("hyperconomy.shop." + s.getShop(player) + ".sell")) {
					String name = sf.fixName(args[0]);
					int amount = 0;
					boolean xp = false;

					if (sf.itemTest(name)) {
						HyperObject ho = sf.getHyperObject(name, playerecon);
						int txpid = ho.getId();
						int txpdata = ho.getData();
						if (txpid == -1 && txpdata == -1) {
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
										amount = calc.gettotalxpPoints(player);
									} else {
										amount = tran.countItems(ho.getId(), ho.getData(), player.getInventory());	
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
							if (xp) {
								tran.sellXP(name, amount, player);
							} else {
								TransactionResponse response = tran.sell(ho, amount, player, null);
								response.sendMessages();
							}
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
