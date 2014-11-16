package regalowl.hyperconomy.command;


import regalowl.simpledatalib.CommonFunctions;
import regalowl.hyperconomy.HyperConomy;
import regalowl.hyperconomy.HyperEconomy;
import regalowl.hyperconomy.tradeobject.EnchantmentClass;
import regalowl.hyperconomy.tradeobject.TradeObject;
import regalowl.hyperconomy.tradeobject.TradeObjectType;


public class Value extends BaseCommand implements HyperCommand {

	public Value(HyperConomy hc) {
		super(hc, false);
	}

	@Override
	public CommandData onCommand(CommandData data) {
		if (!validate(data)) return data;

		try {
			HyperEconomy he = super.getEconomy();
			boolean requireShop = hc.getConf().getBoolean("shop.limit-info-commands-to-shops");
			if (hp != null && requireShop && !dm.getHyperShopManager().inAnyShop(hp) && !hp.hasPermission("hyperconomy.admin")) {
				data.addResponse(L.get("REQUIRE_SHOP_FOR_INFO"));
				return data;
			}
			String name = he.fixName(args[0]);
			TradeObject ho = he.getTradeObject(name, dm.getHyperShopManager().getShop(hp));
			if (ho == null) {
				data.addResponse(L.get("INVALID_ITEM_NAME"));
				return data;
			}
			int amount = 1;
			if (ho.getType() != TradeObjectType.ENCHANTMENT && args.length > 1) {
				amount = Integer.parseInt(args[1]);
				if (amount > 10000) {
					amount = 10000;
				}
			}
			EnchantmentClass eClass = EnchantmentClass.DIAMOND;
			if (ho.getType() == TradeObjectType.ENCHANTMENT && args.length > 1) {
				eClass = EnchantmentClass.fromString(args[1]);
				if (eClass == EnchantmentClass.NONE) {
					eClass = EnchantmentClass.DIAMOND;
				}
			}
			double val = 0;
			double cost = 0;
			if (hp != null) {
				if (ho.getType() == TradeObjectType.ITEM) {
					val = ho.getSellPriceWithTax(amount, hp);
					cost = ho.getBuyPriceWithTax(amount);
				} else if (ho.getType() == TradeObjectType.ENCHANTMENT) {
					val = ho.getSellPrice(eClass, hp);
					val -= hp.getSalesTax(val);
					cost = ho.getBuyPrice(eClass);
					cost += ho.getPurchaseTax(cost);
				} else if (ho.getType() == TradeObjectType.EXPERIENCE) {
					val = ho.getSellPrice(amount);
					val -= hp.getSalesTax(val);
					cost = ho.getBuyPriceWithTax(amount);
				}
			} else {
				if (ho.getType() == TradeObjectType.ITEM) {
					val = ho.getSellPrice(amount);
					val -= ho.getSalesTaxEstimate(val);
					cost = ho.getBuyPriceWithTax(amount);
				} else if (ho.getType() == TradeObjectType.ENCHANTMENT) {
					val = ho.getSellPrice(eClass);
					val -= ho.getSalesTaxEstimate(val);
					cost = ho.getBuyPrice(eClass);
					cost += ho.getPurchaseTax(cost);
				} else if (ho.getType() == TradeObjectType.EXPERIENCE) {
					val = ho.getSellPrice(amount);
					val -= ho.getSalesTaxEstimate(val);
					cost = ho.getBuyPriceWithTax(amount);
				}
			}


			data.addResponse(L.get("LINE_BREAK"));
			data.addResponse(L.f(L.get("CAN_BE_SOLD_FOR"), amount, CommonFunctions.twoDecimals(val), ho.getDisplayName()));
			data.addResponse(L.f(L.get("CAN_BE_PURCHASED_FOR"), amount, CommonFunctions.twoDecimals(cost), ho.getDisplayName()));
			data.addResponse(L.f(L.get("GLOBAL_SHOP_CURRENTLY_HAS"), CommonFunctions.twoDecimals(ho.getStock()), ho.getDisplayName()));
			data.addResponse(L.get("LINE_BREAK"));

		} catch (Exception e) {
			hc.gSDL().getErrorWriter().writeError(e);
		}
		return data;
	}
}
