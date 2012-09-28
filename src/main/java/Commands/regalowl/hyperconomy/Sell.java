package regalowl.hyperconomy;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class Sell {
	HyperConomy hc;

	Sell(String args[], Player player, String playerecon) {
		hc = HyperConomy.hc;
		SQLFunctions sf = hc.getSQLFunctions();
		Transaction tran = hc.getTransaction();
		Shop s = hc.getShop();
		try {
			s.setinShop(player);
			if (s.inShop() != -1) {
				if (!hc.getYaml().getConfig().getBoolean("config.use-shop-permissions") || player.hasPermission("hyperconomy.shop.*") || player.hasPermission("hyperconomy.shop." + s.getShop(player)) || player.hasPermission("hyperconomy.shop." + s.getShop(player) + ".sell")) {
					String name = args[0];
					int amount = 0;
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
									amount = tran.countInvitems(sf.getId(name, playerecon), sf.getData(name, playerecon), player);
								} else {
									player.sendMessage(ChatColor.DARK_RED + "Invalid parameters. Use /sell [name] (amount or 'max').");
									return;
								}
							}
						}
					}
					if (teststring != null) {
						if (s.has(s.getShop(player), name)) {
							tran.sell(name, sf.getId(name, playerecon), sf.getData(name, playerecon), amount, player);
						} else {
							player.sendMessage(ChatColor.BLUE + "Sorry, that item or enchantment cannot be traded at this shop.");
							return;
						}
					} else {
						player.sendMessage(ChatColor.DARK_RED + "Invalid item name!");
						return;
					}
				} else {
					player.sendMessage(ChatColor.BLUE + "Sorry, you don't have permission to trade here.");
					return;
				}
			} else {
				player.sendMessage(ChatColor.DARK_RED + "You must be in a shop to buy or sell.");
				return;
			}
		} catch (Exception e) {
			player.sendMessage(ChatColor.DARK_RED + "Invalid parameters. Use /sell [name] (amount or 'max').");
			return;
		}
	}
}
