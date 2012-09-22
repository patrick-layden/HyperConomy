package regalowl.hyperconomy;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class Hb {

	HyperConomy hc;

	Hb(String args[], Player player, String playerecon) {

		hc = HyperConomy.hc;
		SQLFunctions sf = hc.getSQLFunctions();
		Transaction tran = hc.getTransaction();
		Calculation calc = hc.getCalculation();
		Shop s = hc.getShop();
		ETransaction ench = hc.getETransaction();

		double amount;
		boolean ma = false;
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
					ItemStack iinhand = player.getItemInHand();
					if (ench.hasenchants(iinhand) == false) {

						if (args.length == 0) {
							amount = 1;
						} else {

							try {
								amount = Integer.parseInt(args[0]);
							} catch (Exception e) {
								String max = args[0];
								if (max.equalsIgnoreCase("max")) {
									ma = true;
									int space = tran.getavailableSpace(player
											.getItemInHand().getTypeId(), calc
											.getdamageValue(player
													.getItemInHand()), player);
									amount = space;
								} else {
									player.sendMessage(ChatColor.DARK_RED
											+ "Invalid parameters. Use /hb (amount or 'max').");
									return;
								}
							}
						}
						int itd = player.getItemInHand().getTypeId();
						int da = calc.getpotionDV(player.getItemInHand());

						int newdat = calc.newData(itd, da);

						String ke = itd + ":" + newdat;
						String nam = hc.getnameData(ke);
						if (nam == null) {
							player.sendMessage(ChatColor.BLUE + "Sorry, that item is not currently available.");
						} else {
							double shopstock = 0;
							shopstock = sf.getStock(nam, playerecon);
							// Buys the most possible from the shop if the
							// amount is more than that for max.
							if (amount > shopstock && ma) {
								amount = shopstock;
							}
							if (s.has(s.getShop(player), nam)) {
								tran.buy(nam, (int) Math.rint(amount), itd,
										newdat, player);
							} else {
								player.sendMessage(ChatColor.BLUE
										+ "Sorry, that item or enchantment cannot be traded at this shop.");
							}
						}
					} else {
						player.sendMessage("You cannot buy or sell enchanted items.");
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
					+ "Invalid parameters. Use /hb (amount or 'max').");
		}
	}
}
