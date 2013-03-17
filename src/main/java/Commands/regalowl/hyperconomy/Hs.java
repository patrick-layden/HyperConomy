package regalowl.hyperconomy;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class Hs {
	Hs(String args[], Player player) {
		HyperConomy hc = HyperConomy.hc;
		ShopFactory s = hc.getShopFactory();
		Calculation calc = hc.getCalculation();
		Transaction tran = hc.getTransaction();
		ETransaction ench = hc.getETransaction();
		LanguageFile L = hc.getLanguageFile();
		DataHandler dh = hc.getDataFunctions();
		int amount;
		try {
			HyperPlayer hp = dh.getHyperPlayer(player);
			if (s.inAnyShop(player)) {
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
								int da = calc.getDamageValue(player.getItemInHand());
								amount = tran.countItems(itmid, da, player.getInventory());
							} else {
								player.sendMessage(L.get("HS_INVALID"));
								return;
							}
						}
					}
					int itd = player.getItemInHand().getTypeId();
					int da = calc.getDamageValue(player.getItemInHand());
					HyperObject ho = hc.getDataFunctions().getHyperObject(itd, da, hp.getEconomy());
					if (ho == null) {
						player.sendMessage(L.get("CANT_BE_TRADED"));
					} else {
						String nam = ho.getName();
						ItemStack iinhand = player.getItemInHand();
						if (ench.hasenchants(iinhand) == false) {
							if (s.getShop(player).has(nam)) {
								TransactionResponse response = tran.sell(ho, amount, player, null);
								response.sendMessages();
							} else {
								player.sendMessage(L.get("CANT_BE_TRADED"));
							}
						} else {
							player.sendMessage(L.get("CANT_BUY_SELL_ENCHANTED_ITEMS"));
						}
					}
				} else {
					player.sendMessage(L.get("NO_TRADE_PERMISSION"));
				}
			} else {
				player.sendMessage(L.get("MUST_BE_IN_SHOP"));
			}
		} catch (Exception e) {
			player.sendMessage(L.get("HS_INVALID"));
		}
	}
}
