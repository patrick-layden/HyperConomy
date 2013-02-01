package regalowl.hyperconomy;


import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;

public class Buy {
	HyperConomy hc;
	Buy(String args[], Player player, String playerecon) {
		hc = HyperConomy.hc;
		DataHandler sf = hc.getDataFunctions();
		Transaction tran = hc.getTransaction();
		Calculation calc = hc.getCalculation();
		LanguageFile L = hc.getLanguageFile();
		ShopFactory s = hc.getShopFactory();
		try {
			if (s.getShop(player) != null) {
				if (!hc.getYaml().getConfig().getBoolean("config.use-shop-permissions") || player.hasPermission("hyperconomy.shop.*") || player.hasPermission("hyperconomy.shop." + s.getShop(player)) || player.hasPermission("hyperconomy.shop." + s.getShop(player) + ".buy")) {
					String name = args[0];
					boolean xp = false;
					int id = 0;
					int data = 0;
					int amount = 0;
					if (hc.itemTest(name)) {
						HyperObject ho = sf.getHyperObject(name, playerecon);
						int txpid = ho.getId();
						int txpdata = ho.getData();
						if (txpid == -1 && txpdata == -1) {
							xp = true;
						}


						id = ho.getId();
						data = ho.getData();
						if (args.length == 1) {
							amount = 1;
						} else {
							try {
								amount = Integer.parseInt(args[1]);
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
											space = tran.getavailableSpace(id, calc.getDamageValue(damagestack), player);
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
					if (hc.itemTest(name)) {
						if (s.getShop(player).has(name)) {
							if (xp) {
								tran.buyXP(name, amount, player);
							} else {
								tran.buy(name, amount, id, data, player);		
							}
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
