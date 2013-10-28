package regalowl.hyperconomy;

import java.util.ArrayList;

import org.bukkit.GameMode;
import org.bukkit.entity.Player;

public class Esell {
	Esell(Player player, String[] args) {
		HyperConomy hc = HyperConomy.hc;
		LanguageFile L = hc.getLanguageFile();
		EconomyManager em = hc.getEconomyManager();
		try {
			if (player.getGameMode() == GameMode.CREATIVE && hc.gYH().gQFC("config").gB("block-selling-in-creative-mode")) {
				player.sendMessage(L.get("CANT_SELL_CREATIVE"));
				return;
			}
			HyperPlayer hp = em.getHyperPlayer(player.getName());
			HyperEconomy he = hp.getHyperEconomy();
			if (em.inAnyShop(player)) {
				Shop s = em.getShop(player);
				if (hp.hasSellPermission(s)) {
					String name = args[0];
					if (args[0].equalsIgnoreCase("max")) {
						if (!new HyperItemStack(player.getItemInHand()).hasenchants()) {
							player.sendMessage(L.get("HAS_NO_ENCHANTMENTS"));
						}
						ArrayList<String> enchants = new HyperItemStack(player.getItemInHand()).getEnchants();
						for (String e:enchants) {
							if (s.has(e)) {
								PlayerTransaction pt = new PlayerTransaction(TransactionType.SELL);
								pt.setHyperObject(he.getHyperObject(e, s));
								pt.setTradePartner(s.getOwner());
								TransactionResponse response = hp.processTransaction(pt);
								response.sendMessages();
							} else {
								player.sendMessage(L.get("CANT_BE_TRADED"));
							}
						}

					} else {
						if (he.enchantTest(name)) {
							if (s.has(name)) {
								PlayerTransaction pt = new PlayerTransaction(TransactionType.SELL);
								pt.setHyperObject(he.getHyperObject(name, s));
								pt.setTradePartner(s.getOwner());
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
