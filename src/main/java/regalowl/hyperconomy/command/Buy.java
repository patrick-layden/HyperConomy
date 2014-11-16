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
			String name = he.fixName(args[0]);
			TradeObject ho = he.getTradeObject(name, hc.getHyperShopManager().getShop(hp));
			int amount = 1;
			if (args.length > 1) {
				if (args[1].equalsIgnoreCase("max")) {
					if (ho.getType() == TradeObjectType.ITEM) {
						amount = ho.getAvailableSpace(hp.getInventory());
					} else if (ho.getType() == TradeObjectType.EXPERIENCE) {
						amount = (int) ho.getStock();
					} else if (ho.getType() == TradeObjectType.ENCHANTMENT) {
						amount = 1;
					}
				} else {
					amount = Integer.parseInt(args[1]);
				}
			}
			PlayerTransaction pt = new PlayerTransaction(TransactionType.BUY);
			pt.setObeyShops(true);
			pt.setHyperObject(ho);
			pt.setAmount(amount);
			pt.setTradePartner(s.getOwner());
			TransactionResponse response = hp.processTransaction(pt);
			response.sendMessages();
		} catch (Exception e) {
			hc.gSDL().getErrorWriter().writeError(e);
		}
		return data;
	}
}
