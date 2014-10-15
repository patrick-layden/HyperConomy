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

public class Hs extends BaseCommand implements HyperCommand {
	
	public Hs() {
		super(true);
	}

	@Override
	public CommandData onCommand(CommandData data) {
		if (!validate(data)) return data;
		int amount;
		try {
			HyperEconomy he = getEconomy();
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
						HyperObject hi = hp.getObjectInHand();
						amount = hi.count(player.getInventory());
					} else {
						data.addResponse(L.get("HS_INVALID"));
						return;
					}
				}
			}
			HyperObject ho = he.getHyperObject(player.getItemInHand(), em.getHyperShopManager().getShop(player));
			if (ho == null) {
				data.addResponse(L.get("CANT_BE_TRADED"));
			} else {
				Shop s = dm.getHyperShopManager().getShop(player);
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
			data.addResponse(L.get("HS_INVALID"));
		}
	}
}
