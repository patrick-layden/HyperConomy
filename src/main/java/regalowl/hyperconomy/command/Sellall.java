package regalowl.hyperconomy.command;




import regalowl.hyperconomy.HyperEconomy;
import regalowl.hyperconomy.account.HyperAccount;
import regalowl.hyperconomy.account.HyperPlayer;
import regalowl.hyperconomy.inventory.HInventory;
import regalowl.hyperconomy.inventory.HItemStack;
import regalowl.hyperconomy.shop.Shop;
import regalowl.hyperconomy.tradeobject.TradeObject;
import regalowl.hyperconomy.transaction.PlayerTransaction;
import regalowl.hyperconomy.transaction.TransactionResponse;
import regalowl.hyperconomy.transaction.TransactionType;



public class Sellall extends BaseCommand implements HyperCommand {
	
	public Sellall() {
		super(true);
	}




	@Override
	public CommandData onCommand(CommandData data) {
		if (!validate(data)) return data;
		if (dm.getHyperShopManager().inAnyShop(hp)) {
			Shop s = dm.getHyperShopManager().getShop(hp);
			if (hp.hasSellPermission(dm.getHyperShopManager().getShop(hp))) {
				if (args.length == 0) {
					TransactionResponse response = sellAll(hp, s.getOwner());
					response.sendMessages();
					if (response.getFailedObjects().size() == 0) {
						data.addResponse(L.get("LINE_BREAK"));
						data.addResponse(L.get("ALL_ITEMS_SOLD"));
						data.addResponse(L.f(L.get("SOLD_ITEMS_FOR"), response.getTotalPrice()));
						data.addResponse(L.get("LINE_BREAK"));
					} else {
						data.addResponse(L.get("LINE_BREAK"));
						data.addResponse(L.get("ONE_OR_MORE_CANT_BE_TRADED"));
						data.addResponse(L.f(L.get("SOLD_ITEMS_FOR"), response.getTotalPrice()));
						data.addResponse(L.get("LINE_BREAK"));
					}
				} else {
					data.addResponse(L.get("SELLALL_INVALID"));
				}
			} else {
				data.addResponse(L.get("NO_TRADE_PERMISSION"));
			}
		} else {
			data.addResponse(L.get("MUST_BE_IN_SHOP"));
		}
		return data;
	}
	
	
	
	
	
	
	public TransactionResponse sellAll(HyperPlayer trader, HyperAccount tradePartner) {
		HInventory inventory = trader.getInventory();
		HyperEconomy he = trader.getHyperEconomy();
		TransactionResponse totalResponse = new TransactionResponse(trader);
		for (int slot = 0; slot < inventory.getSize(); slot++) {
			if (inventory.getItem(slot) == null) {continue;}
			HItemStack stack = inventory.getItem(slot);
			TradeObject ho = he.getHyperObject(stack, dm.getHyperShopManager().getShop(trader));
			if (ho == null) {continue;}
			int amount = ho.count(inventory);
			PlayerTransaction pt = new PlayerTransaction(TransactionType.SELL);
			pt.setObeyShops(true);
			pt.setTradePartner(tradePartner);
			pt.setHyperObject(ho);
			pt.setAmount(amount);
			TransactionResponse response = trader.processTransaction(pt);
			if (response.successful()) {
				totalResponse.addSuccess(response.getMessage(), response.getPrice(), response.getSuccessfulObjects().get(0));
			} else {
				totalResponse.addFailed(response.getMessage(), response.getFailedObjects().get(0));
			}
		}
		return totalResponse;
	}











	
	
	
	
	
}
