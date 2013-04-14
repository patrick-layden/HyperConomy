package regalowl.hyperconomy;

import java.util.ArrayList;

import org.bukkit.GameMode;
import org.bukkit.entity.Player;

public class Esell {
	Esell(Player player, String[] args) {
		HyperConomy hc = HyperConomy.hc;
		LanguageFile L = hc.getLanguageFile();
		ShopFactory s = hc.getShopFactory();
		DataHandler dh = hc.getDataFunctions();
		InventoryManipulation im = hc.getInventoryManipulation();
		try {
			if (player.getGameMode() == GameMode.CREATIVE && hc.s().blockCreative()) {
				player.sendMessage(L.get("CANT_SELL_CREATIVE"));
				return;
			}
			if (s.inAnyShop(player)) {
				HyperPlayer hp = dh.getHyperPlayer(player);
				if (hp.hasSellPermission(s.getShop(player))) {
					String name = args[0];
					if (args[0].equalsIgnoreCase("max")) {
						if (!im.hasenchants(player.getItemInHand())) {
							player.sendMessage(L.get("HAS_NO_ENCHANTMENTS"));
						}
						ArrayList<String> enchants = im.getEnchantments(player.getItemInHand());
						for (String e:enchants) {
							if (s.getShop(player).has(e)) {
								PlayerTransaction pt = new PlayerTransaction(TransactionType.SELL);
								pt.setHyperObject(dh.getHyperObject(e, hp.getEconomy()));
								TransactionResponse response = hp.processTransaction(pt);
								response.sendMessages();
							} else {
								player.sendMessage(L.get("CANT_BE_TRADED"));
							}
						}

					} else {
						if (hc.getDataFunctions().enchantTest(name)) {
							if (s.getShop(player).has(name)) {
								PlayerTransaction pt = new PlayerTransaction(TransactionType.SELL);
								pt.setHyperObject(dh.getHyperObject(name, hp.getEconomy()));
								TransactionResponse response = hp.processTransaction(pt);
								response.sendMessages();
							} else {
								player.sendMessage(L.get("CANT_BE_TRADED"));
							}
						} else {
							player.sendMessage(L.get("ENCHANTMENT_NOT_IN_DATABASE"));
						}
					}
				} else {
					player.sendMessage(L.get("NO_TRADE_PERMISSION"));
				}
			} else {
				player.sendMessage(L.get("MUST_BE_IN_SHOP"));
			}
		} catch (Exception e) {
			player.sendMessage(L.get("ESELL_INVALID"));
		}
	}
}
