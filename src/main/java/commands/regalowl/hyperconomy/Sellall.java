package regalowl.hyperconomy;

import org.bukkit.GameMode;
import org.bukkit.entity.Player;

public class Sellall {

	Sellall(String args[], Player player) {
		HyperConomy hc = HyperConomy.hc;
		ShopFactory s = hc.getShopFactory();
		LanguageFile L = hc.getLanguageFile();
		DataHandler dh = hc.getDataFunctions();
		if (player.getGameMode() == GameMode.CREATIVE && hc.s().gB("block-selling-in-creative-mode")) {
			player.sendMessage(L.get("CANT_SELL_CREATIVE"));
			return;
		}
		try {
			if (s.inAnyShop(player)) {
				if (dh.getHyperPlayer(player).hasSellPermission(s.getShop(player))) {
					if (args.length == 0) {
						PlayerTransaction pt = new PlayerTransaction(TransactionType.SELL_ALL);
						TransactionResponse response = dh.getHyperPlayer(player).processTransaction(pt);
						response.sendMessages();
						if (response.getFailedObjects().size() == 0) {
							player.sendMessage(L.get("LINE_BREAK"));
							player.sendMessage(L.get("ALL_ITEMS_SOLD"));
							player.sendMessage(L.f(L.get("SOLD_ITEMS_FOR"), response.getTotalPrice()));
							player.sendMessage(L.get("LINE_BREAK"));
						} else {
							player.sendMessage(L.get("LINE_BREAK"));
							player.sendMessage(L.get("ONE_OR_MORE_CANT_BE_TRADED"));
							player.sendMessage(L.f(L.get("SOLD_ITEMS_FOR"), response.getTotalPrice()));
							player.sendMessage(L.get("LINE_BREAK"));
						}
					} else {
						player.sendMessage(L.get("SELLALL_INVALID"));
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
			player.sendMessage(L.get("SELLALL_INVALID"));
			return;
		}
	}
}
