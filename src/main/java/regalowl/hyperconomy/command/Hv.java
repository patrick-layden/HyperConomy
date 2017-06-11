package regalowl.hyperconomy.command;

import java.util.Iterator;

import regalowl.simpledatalib.CommonFunctions;
import regalowl.hyperconomy.HyperConomy;
import regalowl.hyperconomy.HyperEconomy;
import regalowl.hyperconomy.inventory.HEnchantment;
import regalowl.hyperconomy.inventory.HItemStack;
import regalowl.hyperconomy.tradeobject.EnchantmentClass;
import regalowl.hyperconomy.tradeobject.TradeObject;

public class Hv extends BaseCommand implements HyperCommand {
	public Hv(HyperConomy hc) {
		super(hc, true);
	}

	@Override
	public CommandData onCommand(CommandData data) {
		if (!validate(data)) return data;
		try {
			int amount;
			HyperEconomy he = getEconomy();
			boolean requireShop = hc.getConf().getBoolean("shop.limit-info-commands-to-shops");
			if ((requireShop && dm.getHyperShopManager().inAnyShop(hp)) || !requireShop || hp.hasPermission("hyperconomy.admin")) {
				HItemStack iinhand = hp.getItemInHand();
				if (args.length == 0) {
					amount = 1;
				} else {
					try {
						amount = Integer.parseInt(args[0]);
						if (amount > 10000) amount = 10000;
					} catch (Exception e) {
						data.addResponse(L.get("VALUE_INVALID"));
						return data;
					}
				}
				if (!iinhand.hasEnchantments()) {
					TradeObject ho = he.getTradeObject(iinhand, dm.getHyperShopManager().getShop(hp));
					if (ho == null) {
						data.addResponse(L.get("OBJECT_NOT_AVAILABLE"));
					} else {
						String displayName = ho.getDisplayName();
						double val = ho.getSellPrice(amount, hp);
						/*
						if (ho.isDurable() && amount > 1) {
							int numberofitem = ho.count(hp.getInventory());
							if (amount - numberofitem > 0) {
								int addamount = amount - numberofitem;
								val = val + ho.getSellPrice(addamount);
							}
						}
						*/
						double salestax = hp.getSalesTax(val);
						val = CommonFunctions.twoDecimals(val - salestax);
						data.addResponse(L.get("LINE_BREAK"));
						data.addResponse(L.f(L.get("CAN_BE_SOLD_FOR"), amount, val, displayName));
						double cost = ho.getBuyPrice(amount);
						double taxpaid = ho.getPurchaseTax(cost);
						cost = CommonFunctions.twoDecimals(cost + taxpaid);
						double stock = 0;
						stock = ho.getStock();
						data.addResponse(L.f(L.get("CAN_BE_PURCHASED_FOR"), amount, cost, displayName));
						data.addResponse(L.f(L.get("GLOBAL_SHOP_CURRENTLY_HAS"), CommonFunctions.twoDecimals(stock), displayName));
						data.addResponse(L.get("LINE_BREAK"));
					}
				} else {
					Iterator<HEnchantment> ite = iinhand.getItemMeta().getEnchantments().iterator();
					data.addResponse(L.get("LINE_BREAK"));
					while (ite.hasNext()) {
						TradeObject ho = he.getTradeObject(ite.next(), dm.getHyperShopManager().getShop(hp));
						double value = ho.getSellPrice(EnchantmentClass.fromItem(iinhand), hp);
						double cost = ho.getBuyPrice(EnchantmentClass.fromItem(iinhand));
						cost = cost + ho.getPurchaseTax(cost);
						value = CommonFunctions.twoDecimals(value);
						cost = CommonFunctions.twoDecimals(cost);
						double salestax = 0;
						salestax = hp.getSalesTax(value);
						value = CommonFunctions.twoDecimals(value - salestax);
						data.addResponse(L.f(L.get("EVALUE_SALE"), value, ho.getDisplayName()));
						data.addResponse(L.f(L.get("EVALUE_PURCHASE"), cost, ho.getDisplayName()));
						data.addResponse(L.f(L.get("EVALUE_STOCK"), CommonFunctions.twoDecimals(ho.getStock()), ho.getDisplayName()));
					}
					data.addResponse(L.get("LINE_BREAK"));
				}
			} else {
				data.addResponse(L.get("REQUIRE_SHOP_FOR_INFO"));
			}
		} catch (Exception e) {
			hc.gSDL().getErrorWriter().writeError(e);
		}
		return data;
	}
}
