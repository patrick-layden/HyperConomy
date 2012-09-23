package regalowl.hyperconomy;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;

public class Buy {
	HyperConomy hc;

	Buy(String args[], Player player, String playerecon) {
		hc = HyperConomy.hc;
		SQLFunctions sf = hc.getSQLFunctions();
		Transaction tran = hc.getTransaction();
		Calculation calc = hc.getCalculation();
		Shop s = hc.getShop();
		try {
			s.setinShop(player);
			if (s.inShop() != -1) {
				if (!hc.getYaml().getConfig().getBoolean("config.use-shop-permissions") || player.hasPermission("hyperconomy.shop.*") || player.hasPermission("hyperconomy.shop." + s.getShop(player)) || player.hasPermission("hyperconomy.shop." + s.getShop(player) + ".buy")) {
					String name = args[0];
					int amount = 0;
					String teststring = hc.testiString(name);
					int id = 0;
					int data = 0;
					id = sf.getId(name, playerecon);
					data = sf.getData(name, playerecon);
					if (teststring != null) {
						if (args.length == 1) {
							amount = 1;
						} else {
							try {
								amount = Integer.parseInt(args[1]);
							} catch (Exception e) {
								String max = args[1];
								if (max.equalsIgnoreCase("max")) {
									MaterialData damagemd = new MaterialData(id, (byte) data);
									ItemStack damagestack = damagemd.toItemStack();
									int space = 0;
									if (id >= 0) {
										space = tran.getavailableSpace(id, calc.getdamageValue(damagestack), player);
									}
									amount = space;
									int shopstock = (int) sf.getStock(name, playerecon);
									if (amount > shopstock) {
										amount = shopstock;
									}
								} else {
									player.sendMessage(ChatColor.DARK_RED + "Invalid Parameters. Use /buy [name] (amount or 'max')");
									return;
								}
							}
						}
					}
					if (teststring != null) {
						if (s.has(s.getShop(player), name)) {
							tran.buy(name, amount, id, data, player);
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
			player.sendMessage(ChatColor.DARK_RED + "Invalid Parameters. Use /buy [name] (amount or 'max')");
			return;
		}
	}
}
