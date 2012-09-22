package regalowl.hyperconomy;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class Sellxp {

	Sellxp(String args[], Player player) {
		HyperConomy hc = HyperConomy.hc;
		Shop s = hc.getShop();
		Transaction tran = hc.getTransaction();
		Calculation calc = hc.getCalculation();
		try {
			s.setinShop(player);
			if (s.inShop() != -1) {
				if (!hc.getYaml().getConfig()
						.getBoolean("config.use-shop-permissions")
						|| player.hasPermission("hyperconomy.shop.*")
						|| player.hasPermission("hyperconomy.shop."
								+ s.getShop(player))
						|| player.hasPermission("hyperconomy.shop."
								+ s.getShop(player) + ".sell")) {

					if (args.length <= 1) {
						int amount;
						if (args.length == 0) {
							amount = 1;
						} else {
							try {
								amount = Integer.parseInt(args[0]);
							} catch (Exception e) {
								if (args[0].equalsIgnoreCase("max")) {
									amount = calc.gettotalxpPoints(player);
								} else {
									player.sendMessage(ChatColor.DARK_RED
											+ "Invalid Parameters.  Use /buyxp (amount)");
									return;
								}
							}
						}
						String ke = -1 + ":" + -1;
						String nam = hc.getnameData(ke);
						tran.sellXP(nam, amount, player);

					} else {
						player.sendMessage(ChatColor.DARK_RED
								+ "Invalid Parameters.  Use /buyxp (amount)");
					}
				} else {
					player.sendMessage(ChatColor.BLUE
							+ "Sorry, you don't have permission to trade here.");
				}

			} else {
				player.sendMessage(ChatColor.DARK_RED
						+ "You must be in a shop to buy or sell.");
			}
			return;
		} catch (Exception e) {
			player.sendMessage(ChatColor.DARK_RED
					+ "Invalid Parameters.  Use /buyxp (amount)");
		}

	}

}
