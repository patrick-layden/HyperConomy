package regalowl.hyperconomy;


import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class Hb {
	HyperConomy hc;

	Hb(String args[], Player player, String playerecon) {
		hc = HyperConomy.hc;
		EconomyManager em = hc.getEconomyManager();
		LanguageFile L = hc.getLanguageFile();
		InventoryManipulation im = hc.getInventoryManipulation();
		double amount;
		boolean ma = false;
		try {
			HyperPlayer hp = em.getHyperPlayer(player.getName());
			HyperEconomy he = hp.getHyperEconomy();
			if (he.inAnyShop(player)) {
				if (hp.hasBuyPermission(he.getShop(player))) {
					ItemStack iinhand = player.getItemInHand();
					if (im.hasenchants(iinhand) == false) {
						if (args.length == 0) {
							amount = 1;
						} else {
							try {
								amount = Integer.parseInt(args[0]);
								if (amount > 10000) {
									amount = 10000;
								}
							} catch (Exception e) {
								String max = args[0];
								if (max.equalsIgnoreCase("max")) {
									ma = true;
									int space = im.getAvailableSpace(player.getItemInHand().getTypeId(), im.getDamageValue(player.getItemInHand()), player.getInventory());
									amount = space;
								} else {
									player.sendMessage(L.get("HB_INVALID"));
									return;
								}
							}
						}
						int itd = player.getItemInHand().getTypeId();
						int da = im.getDamageValue(player.getItemInHand());
						HyperObject ho = he.getHyperObject(itd, da);
						if (ho == null) {
							player.sendMessage(L.get("OBJECT_NOT_AVAILABLE"));
						} else {
							String nam = ho.getName();
							double shopstock = 0;
							shopstock = ho.getStock();
							// Buys the most possible from the shop if the
							// amount is more than that for max.
							if (amount > shopstock && ma) {
								amount = shopstock;
							}
							if (he.getShop(player).has(nam)) {
								PlayerTransaction pt = new PlayerTransaction(TransactionType.BUY);
								pt.setHyperObject(ho);
								pt.setAmount((int) Math.rint(amount));
								TransactionResponse response = hp.processTransaction(pt);
								response.sendMessages();
							} else {
								player.sendMessage(L.get("CANT_BE_TRADED"));
							}
						}
					} else {
						player.sendMessage(L.get("CANT_BUY_SELL_ENCHANTED_ITEMS"));
					}
				} else {
					player.sendMessage(L.get("NO_TRADE_PERMISSION"));
				}
			} else {
				player.sendMessage(L.get("MUST_BE_IN_SHOP"));
			}
			return;
		} catch (Exception e) {
			player.sendMessage(L.get("HB_INVALID"));
		}
	}
}
