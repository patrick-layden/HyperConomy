package regalowl.hyperconomy;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class Ebuy {
	Ebuy(Player player, String[] args) {
		HyperConomy hc = HyperConomy.hc;
		LanguageFile L = hc.getLanguageFile();
		EconomyManager em = hc.getEconomyManager();
		try {
			HyperEconomy he = em.getHyperPlayer(player.getName()).getHyperEconomy();
			if (he.inAnyShop(player)) {
				HyperPlayer hp = he.getHyperPlayer(player);
				if (hp.hasBuyPermission(he.getShop(player))) {
					String name = args[0];
					if (he.enchantTest(name)) {
						Shop s = he.getShop(player);
						if (s.has(name)) {
							PlayerTransaction pt = new PlayerTransaction(TransactionType.BUY);
							pt.setHyperObject(he.getHyperObject(name, he.getShop(player)));
							pt.setTradePartner(s.getOwner());
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
