package regalowl.hyperconomy;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class Ebuy {
	Ebuy(Player player, String[] args) {
		HyperConomy hc = HyperConomy.hc;
		LanguageFile L = hc.getLanguageFile();
		ShopFactory s = hc.getShopFactory();
		DataHandler dh = hc.getDataFunctions();
		try {
			if (s.inAnyShop(player)) {
				HyperPlayer hp = dh.getHyperPlayer(player);
				if (!hc.getYaml().getConfig().getBoolean("config.use-shop-permissions") || player.hasPermission("hyperconomy.shop.*") || player.hasPermission("hyperconomy.shop." + s.getShop(player).getName()) || player.hasPermission("hyperconomy.shop." + s.getShop(player).getName() + ".buy")) {
					String name = args[0];
					if (hc.getDataFunctions().enchantTest(name)) {
						if (s.getShop(player).has(name)) {
							PlayerTransaction pt = new PlayerTransaction(TransactionType.BUY);
							pt.setHyperObject(dh.getHyperObject(name, hp.getEconomy()));
							TransactionResponse response = hp.processTransaction(pt);
							response.sendMessages();
						} else {
							player.sendMessage(ChatColor.BLUE + "Sorry, that item or enchantment cannot be traded at this shop.");
						}
					} else {
						player.sendMessage(L.get("ENCHANTMENT_NOT_IN_DATABASE"));
					}
				} else {
					player.sendMessage(L.get("NO_TRADE_PERMISSION"));
				}
			} else {
				player.sendMessage(L.get("MUST_BE_IN_SHOP"));
			}
			return;
		} catch (Exception e) {
			player.sendMessage(L.get("EBUY_INVALID"));
		}
	}
}
