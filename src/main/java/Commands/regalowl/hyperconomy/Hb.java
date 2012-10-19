package regalowl.hyperconomy;


import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import static regalowl.hyperconomy.Messages.*;

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
				if (!hc.getYaml().getConfig().getBoolean("config.use-shop-permissions") || player.hasPermission("hyperconomy.shop.*") || player.hasPermission("hyperconomy.shop." + s.getShop(player)) || player.hasPermission("hyperconomy.shop." + s.getShop(player) + ".buy")) {
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
									int space = tran.getavailableSpace(player.getItemInHand().getTypeId(), calc.getdamageValue(player.getItemInHand()), player);
									amount = space;
								} else {
									player.sendMessage(HB_INVALID);
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
							player.sendMessage(OBJECT_NOT_AVAILABLE);
						} else {
							double shopstock = 0;
							shopstock = sf.getStock(nam, playerecon);
							// Buys the most possible from the shop if the
							// amount is more than that for max.
							if (amount > shopstock && ma) {
								amount = shopstock;
							}
							if (s.has(s.getShop(player), nam)) {
								tran.buy(nam, (int) Math.rint(amount), itd, newdat, player);
							} else {
								player.sendMessage(CANT_BE_TRADED);
							}
						}
					} else {
						player.sendMessage(CANT_BUY_SELL_ENCHANTED_ITEMS);
					}
				} else {
					player.sendMessage(NO_TRADE_PERMISSION);
				}
			} else {
				player.sendMessage(MUST_BE_IN_SHOP);
			}
			return;
		} catch (Exception e) {
			player.sendMessage(HB_INVALID);
		}
	}
}
