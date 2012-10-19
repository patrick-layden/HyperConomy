package regalowl.hyperconomy;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import static regalowl.hyperconomy.Messages.*;

public class Hs {
	Hs(String args[], Player player) {
		HyperConomy hc = HyperConomy.hc;
		Shop s = hc.getShop();
		Calculation calc = hc.getCalculation();
		Transaction tran = hc.getTransaction();
		ETransaction ench = hc.getETransaction();
		int amount;
		try {
			s.setinShop(player);
			if (s.inShop() != -1) {
				if (!hc.getYaml().getConfig().getBoolean("config.use-shop-permissions") || player.hasPermission("hyperconomy.shop.*") || player.hasPermission("hyperconomy.shop." + s.getShop(player)) || player.hasPermission("hyperconomy.shop." + s.getShop(player) + ".sell")) {
					if (args.length == 0) {
						amount = 1;
					} else {
						try {
							amount = Integer.parseInt(args[0]);
						} catch (Exception e) {
							String max = args[0];
							if (max.equalsIgnoreCase("max")) {
								int itmid = player.getItemInHand().getTypeId();
								int da = calc.getpotionDV(player.getItemInHand());
								int newdat = calc.newData(itmid, da);
								amount = tran.countInvitems(itmid, newdat, player);
							} else {
								player.sendMessage(HS_INVALID);
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
						player.sendMessage(CANT_BE_TRADED);
					} else {
						ItemStack iinhand = player.getItemInHand();
						if (ench.hasenchants(iinhand) == false) {
							if (s.has(s.getShop(player), nam)) {
								tran.sell(nam, itd, newdat, amount, player);
							} else {
								player.sendMessage(CANT_BE_TRADED);
							}
						} else {
							player.sendMessage(CANT_BUY_SELL_ENCHANTED_ITEMS);
						}
					}
				} else {
					player.sendMessage(NO_TRADE_PERMISSION);
				}
			} else {
				player.sendMessage(MUST_BE_IN_SHOP);
			}
		} catch (Exception e) {
			player.sendMessage(HS_INVALID);
		}
	}
}
