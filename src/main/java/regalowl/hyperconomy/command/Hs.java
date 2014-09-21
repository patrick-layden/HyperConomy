package regalowl.hyperconomy.command;

import org.bukkit.entity.Player;
import regalowl.hyperconomy.DataManager;
import regalowl.hyperconomy.HyperConomy;
import regalowl.hyperconomy.HyperEconomy;
import regalowl.hyperconomy.account.HyperPlayer;
import regalowl.hyperconomy.hyperobject.HyperObject;
import regalowl.hyperconomy.shop.Shop;
import regalowl.hyperconomy.transaction.PlayerTransaction;
import regalowl.hyperconomy.transaction.TransactionResponse;
import regalowl.hyperconomy.transaction.TransactionType;
import regalowl.hyperconomy.util.LanguageFile;

public class Hs {
	Hs(String args[], Player player) {
		HyperConomy hc = HyperConomy.hc;
		if (hc.getHyperLock().isLocked(player)) {
			hc.getHyperLock().sendLockMessage(player);
			return;
		}
		LanguageFile L = hc.getLanguageFile();
		DataManager em = hc.getDataManager();
		int amount;
		try {
			HyperPlayer hp = em.getHyperPlayer(player);
			HyperEconomy he = hp.getHyperEconomy();
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
						HyperObject hi = he.getHyperObject(player.getItemInHand());
						amount = hi.count(player.getInventory());
					} else {
						player.sendMessage(L.get("HS_INVALID"));
						return;
					}
				}
			}
			HyperObject ho = he.getHyperObject(player.getItemInHand(), em.getHyperShopManager().getShop(player));
			if (ho == null) {
				player.sendMessage(L.get("CANT_BE_TRADED"));
			} else {
				Shop s = em.getHyperShopManager().getShop(player);
				PlayerTransaction pt = new PlayerTransaction(TransactionType.SELL);
				pt.setObeyShops(true);
				pt.setHyperObject(ho);
				pt.setAmount(amount);
				pt.setTradePartner(s.getOwner());
				TransactionResponse response = hp.processTransaction(pt);
				response.sendMessages();
			}
		} catch (Exception e) {
			HyperConomy.hc.getDebugMode().debugWriteError(e);
			player.sendMessage(L.get("HS_INVALID"));
		}
	}
}
