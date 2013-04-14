package regalowl.hyperconomy;


import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class Hb {
	HyperConomy hc;

	Hb(String args[], Player player, String playerecon) {
		hc = HyperConomy.hc;
		DataHandler sf = hc.getDataFunctions();
		LanguageFile L = hc.getLanguageFile();
		Calculation calc = hc.getCalculation();
		ShopFactory s = hc.getShopFactory();
		InventoryManipulation im = hc.getInventoryManipulation();
		double amount;
		boolean ma = false;
		try {
			if (s.inAnyShop(player)) {
				if (!hc.getYaml().getConfig().getBoolean("config.use-shop-permissions") || player.hasPermission("hyperconomy.shop.*") || player.hasPermission("hyperconomy.shop." + s.getShop(player).getName()) || player.hasPermission("hyperconomy.shop." + s.getShop(player).getName() + ".buy")) {
					ItemStack iinhand = player.getItemInHand();
					if (im.hasenchants(iinhand) == false) {
						if (args.length == 0) {
							amount = 1;
						} else {
							try {
								amount = Integer.parseInt(args[0]);
							} catch (Exception e) {
								String max = args[0];
								if (max.equalsIgnoreCase("max")) {
									ma = true;
									int space = im.getAvailableSpace(player.getItemInHand().getTypeId(), calc.getDamageValue(player.getItemInHand()), player.getInventory());
									amount = space;
								} else {
									player.sendMessage(L.get("HB_INVALID"));
									return;
								}
							}
						}
						int itd = player.getItemInHand().getTypeId();
						int da = calc.getDamageValue(player.getItemInHand());
						HyperObject ho = sf.getHyperObject(itd, da, playerecon);
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
							HyperPlayer hp = sf.getHyperPlayer(player);
							if (s.getShop(player).has(nam)) {
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
