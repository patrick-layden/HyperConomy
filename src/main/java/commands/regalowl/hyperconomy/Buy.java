package regalowl.hyperconomy;


import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;

public class Buy {
	HyperConomy hc;
	Buy(String args[], Player player, String playerecon) {
		hc = HyperConomy.hc;
		DataHandler sf = hc.getDataFunctions();
		LanguageFile L = hc.getLanguageFile();
		ShopFactory s = hc.getShopFactory();
		try {
			if (s.inAnyShop(player)) {
				HyperPlayer hp = sf.getHyperPlayer(player);
				if (hp.hasBuyPermission(s.getShop(player))) {
					String name = sf.fixName(args[0]);
					boolean xp = false;
					int id = 0;
					int data = 0;
					int amount = 0;
					if (sf.itemTest(name)) {
						HyperObject ho = sf.getHyperObject(name, playerecon);
						if (ho.getType() == HyperObjectType.EXPERIENCE) {
							xp = true;
						}

						id = ho.getId();
						data = ho.getData();
						if (args.length == 1) {
							amount = 1;
						} else {
							try {
								amount = Integer.parseInt(args[1]);
								if (amount > 10000) {
									amount = 10000;
								}
							} catch (Exception e) {
								String max = args[1];
								if (max.equalsIgnoreCase("max")) {
									if (xp) {
										amount = (int) ho.getStock();
									} else {
										MaterialData damagemd = new MaterialData(id, (byte) data);
										ItemStack damagestack = damagemd.toItemStack();
										int space = 0;
										if (id >= 0) {
											space = hc.getInventoryManipulation().getAvailableSpace(id, hc.getInventoryManipulation().getDamageValue(damagestack), player.getInventory());
										}
										amount = space;
										int shopstock = (int) ho.getStock();
										if (amount > shopstock) {
											amount = shopstock;
										}
									}
								} else {
									player.sendMessage(L.get("BUY_INVALID"));
									return;
								}
							}
						}
					}
					if (sf.itemTest(name)) {
						HyperObject ho = sf.getHyperObject(name, playerecon);
						if (s.getShop(player).has(name)) {
							PlayerTransaction pt = new PlayerTransaction(TransactionType.BUY);
							pt.setHyperObject(ho);
							pt.setAmount(amount);
							TransactionResponse response = hp.processTransaction(pt);
							response.sendMessages();
						} else {
							player.sendMessage(L.get("CANT_BE_TRADED"));
							return;
						}
					} else {
						player.sendMessage(L.get("INVALID_ITEM_NAME"));
						return;
					}
				} else {
					player.sendMessage(L.get("NO_TRADE_PERMISSION"));
					return;
				}
			} else {
				player.sendMessage(L.get("MUST_BE_IN_SHOP"));
				return;
			}
		} catch (Exception e) {
			player.sendMessage(L.get("BUY_INVALID"));
			return;
		}
	}
}
