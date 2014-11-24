package regalowl.hyperconomy.command;


import regalowl.hyperconomy.HyperConomy;
import regalowl.hyperconomy.HyperEconomy;
import regalowl.hyperconomy.shop.Shop;
import regalowl.hyperconomy.tradeobject.TradeObject;
import regalowl.hyperconomy.transaction.PlayerTransaction;
import regalowl.hyperconomy.transaction.TransactionResponse;
import regalowl.hyperconomy.transaction.TransactionType;

public class Hb extends BaseCommand implements HyperCommand{

	public Hb(HyperConomy hc) {
		super(hc, true);
	}

	@Override
	public CommandData onCommand(CommandData data) {
		if (!validate(data)) return data;
		try {
			int amount = 1;
			HyperEconomy he = getEconomy();
			TradeObject ho = he.getTradeObject(hp.getItemInHand(), dm.getHyperShopManager().getShop(hp));
			if (ho == null) {
				data.addResponse(L.get("OBJECT_NOT_AVAILABLE"));
				return data;
			}
			if (args.length > 0) {
				if (args[0].equalsIgnoreCase("max")) {
					amount = ho.getAvailableSpace(hp.getInventory());
					if (amount > ho.getStock())
						amount = (int) Math.floor(ho.getStock());
				} else {
					try {
						amount = Integer.parseInt(args[0]);
						if (amount > 10000) amount = 10000;
					} catch (Exception e) {
						data.addResponse(L.get("HB_INVALID"));
						return data;
					}
				}
			}
			Shop s = hc.getHyperShopManager().getShop(hp);
			PlayerTransaction pt = new PlayerTransaction(TransactionType.BUY);
			pt.setObeyShops(true);
			pt.setHyperObject(ho);
			pt.setAmount((int) Math.rint(amount));
			if (s != null) pt.setTradePartner(s.getOwner());
			TransactionResponse response = hp.processTransaction(pt);
			response.sendMessages();
		} catch (Exception e) {
			hc.gSDL().getErrorWriter().writeError(e);
		}
		return data;
	}
}
