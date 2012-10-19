package regalowl.hyperconomy;

import org.bukkit.entity.Player;
import static regalowl.hyperconomy.Messages.*;

public class Sell {
	HyperConomy hc;

	Sell(String args[], Player player, String playerecon) {
		hc = HyperConomy.hc;
		SQLFunctions sf = hc.getSQLFunctions();
		Transaction tran = hc.getTransaction();
		Calculation calc = hc.getCalculation();
		Shop s = hc.getShop();
		try {
			s.setinShop(player);
			if (s.inShop() != -1) {
				if (!hc.getYaml().getConfig().getBoolean("config.use-shop-permissions") || player.hasPermission("hyperconomy.shop.*") || player.hasPermission("hyperconomy.shop." + s.getShop(player)) || player.hasPermission("hyperconomy.shop." + s.getShop(player) + ".sell")) {
					String name = args[0];
					int amount = 0;
					boolean xp = false;
					int txpid = sf.getId(name, playerecon);
					int txpdata = sf.getData(name, playerecon);
					if (txpid == -1 && txpdata == -1) {
						xp = true;
					}
					
					
					String teststring = hc.testiString(name);
					if (teststring != null) {
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
										amount = tran.countInvitems(sf.getId(name, playerecon), sf.getData(name, playerecon), player);	
									}
								} else {
									player.sendMessage(SELL_INVALID);
									return;
								}
							}
						}
					}
					if (teststring != null) {
						if (s.has(s.getShop(player), name)) {
							if (xp) {
								tran.sellXP(name, amount, player);
							} else {
								tran.sell(name, sf.getId(name, playerecon), sf.getData(name, playerecon), amount, player);
							}
						} else {
							player.sendMessage(CANT_BE_TRADED);
							return;
						}
					} else {
						player.sendMessage(INVALID_ITEM_NAME);
						return;
					}
				} else {
					player.sendMessage(NO_TRADE_PERMISSION);
					return;
				}
			} else {
				player.sendMessage(MUST_BE_IN_SHOP);
				return;
			}
		} catch (Exception e) {
			player.sendMessage(SELL_INVALID);
			return;
		}
	}
}
