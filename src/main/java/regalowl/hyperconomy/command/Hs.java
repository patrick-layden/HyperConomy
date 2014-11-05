package regalowl.hyperconomy.command;

import regalowl.hyperconomy.HyperEconomy;
import regalowl.hyperconomy.shop.Shop;
import regalowl.hyperconomy.tradeobject.TradeObject;
import regalowl.hyperconomy.transaction.PlayerTransaction;
import regalowl.hyperconomy.transaction.TransactionResponse;
import regalowl.hyperconomy.transaction.TransactionType;

public class Hs extends BaseCommand implements HyperCommand {
	
	public Hs() {
		super(true);
	}

	@Override
	public CommandData onCommand(CommandData data) {
		if (!validate(data)) return data;
			try {
			int amount;
	
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
						TradeObject hi = he.getHyperObject(hp.getItemInHand());
						amount = hi.count(hp.getInventory());
					} else {
						data.addResponse(L.get("HS_INVALID"));
						return data;
					}
				}
			}
			TradeObject ho = he.getHyperObject(hp.getItemInHand(), dm.getHyperShopManager().getShop(hp));
			if (ho == null) {
				data.addResponse(L.get("CANT_BE_TRADED"));
			} else {
				Shop s = dm.getHyperShopManager().getShop(hp);
				PlayerTransaction pt = new PlayerTransaction(TransactionType.SELL);
				pt.setObeyShops(true);
				pt.setHyperObject(ho);
				pt.setAmount(amount);
				if (s != null) {
					pt.setTradePartner(s.getOwner());
				}
				TransactionResponse response = hp.processTransaction(pt);
				response.sendMessages();
			}
		} catch (Exception e) {
			hc.gDB().writeError(e);
		}
		return data;
	}
}
