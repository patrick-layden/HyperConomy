package regalowl.hyperconomy.command;


import regalowl.hyperconomy.HyperConomy;
import regalowl.hyperconomy.HyperEconomy;
import regalowl.hyperconomy.shop.Shop;
import regalowl.hyperconomy.tradeobject.TradeObject;
import regalowl.hyperconomy.tradeobject.TradeObjectType;
import regalowl.hyperconomy.transaction.PlayerTransaction;
import regalowl.hyperconomy.transaction.TransactionResponse;
import regalowl.hyperconomy.transaction.TransactionType;


public class Buy extends BaseCommand implements HyperCommand {

	public Buy(HyperConomy hc) {
		super(hc, true);
	}

	@Override
	public CommandData onCommand(CommandData data) {
		if (!validate(data)) return data;
		HyperEconomy he = hp.getHyperEconomy();
		try {
			Shop s = hc.getHyperShopManager().getShop(hp);
			TradeObject ho = he.getTradeObject(args[0], hc.getHyperShopManager().getShop(hp));
			if (ho == null) {
				data.addResponse(L.get("OBJECT_NOT_IN_DATABASE"));
				return data;
			}
			int amount = 1;
			if (args.length > 1) {
				if (args[1].equalsIgnoreCase("max")) {
					if (ho.getType() == TradeObjectType.ITEM) {
						amount = ho.getAvailableSpace(hp.getInventory());
						if (amount > ho.getStock()) amount = (int)Math.floor(ho.getStock());
					} else if (ho.getType() == TradeObjectType.EXPERIENCE) {
						amount = (int) ho.getStock();
					} else if (ho.getType() == TradeObjectType.ENCHANTMENT) {
						amount = 1;
					}
				} else {
					try {
						amount = Integer.parseInt(args[1]);
						if (amount > 10000) amount = 10000;
					} catch (Exception e) {
						data.addResponse(L.get("BUY_INVALID"));
						return data;
					}
				}
			}
			PlayerTransaction pt = new PlayerTransaction(TransactionType.BUY);
			pt.setObeyShops(true);
			pt.setHyperObject(ho);
			pt.setAmount(amount);
			if (s != null) pt.setTradePartner(s.getOwner());
			TransactionResponse response = hp.processTransaction(pt);
			response.sendMessages();
		} catch (Exception e) {
			hc.gSDL().getErrorWriter().writeError(e);
		}
		return data;
	}
}
