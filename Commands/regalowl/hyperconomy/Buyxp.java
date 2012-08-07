package regalowl.hyperconomy;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class Buyxp {


	Buyxp(String args[], Player player) {
		HyperConomy hc = HyperConomy.hc;
		Shop s = hc.getShop();
		Transaction tran = hc.getTransaction();
		try {
			s.setinShop(player);
			if (s.inShop() != -1) {
				if (!hc.getYaml().getConfig()
						.getBoolean("config.use-shop-permissions")
						|| player.hasPermission("hyperconomy.shop.*")
						|| player.hasPermission("hyperconomy.shop."
								+ s.getShop(player))
						|| player.hasPermission("hyperconomy.shop."
								+ s.getShop(player) + ".buy")) {

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
		} catch (Exception e) {
			player.sendMessage(ChatColor.DARK_RED
					+ "Invalid Parameters.  Use /buyxp (amount)");
		}

	}
	
	
}
