package regalowl.hyperconomy;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

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
				if (!hc.getYaml().getConfig()
						.getBoolean("config.use-shop-permissions")
						|| player.hasPermission("hyperconomy.shop.*")
						|| player.hasPermission("hyperconomy.shop."
								+ s.getShop(player))
						|| player.hasPermission("hyperconomy.shop."
								+ s.getShop(player) + ".buy")) {
					if (args.length == 2) {
						amount = Integer.parseInt(args[0]);
						itd = Integer.parseInt(args[1]);
					} else if (args.length == 3) {
						amount = Integer.parseInt(args[0]);
						itd = Integer.parseInt(args[1]);
						da = Integer.parseInt(args[2]);
					} else {
						player.sendMessage(ChatColor.DARK_RED
								+ "Invalid parameters. Use /buyid [amount] [id] (damage value).");
						return;
					}
					String ke = itd + ":" + da;
					String nam = hc.getnameData(ke);
					if (nam == null) {
						player.sendMessage(ChatColor.BLUE
								+ "Sorry, that item is not currently available.");
					} else {
						if (s.has(s.getShop(player), nam)) {
							tran.buy(nam, amount, itd, da, player);
						} else {
							player.sendMessage(ChatColor.BLUE
									+ "Sorry, that item or enchantment cannot be traded at this shop.");
						}
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
					+ "Invalid parameters. Use /buyid [amount] [id] (damage value).");
		}
	}
}
