package regalowl.hyperconomy.command;


import regalowl.hyperconomy.HyperEconomy;

import regalowl.hyperconomy.hyperobject.EnchantmentClass;
import regalowl.hyperconomy.hyperobject.HyperObject;
import regalowl.hyperconomy.hyperobject.HyperObjectType;


public class Value extends BaseCommand implements HyperCommand {

	public Value() {
		super(false);
	}

	@Override
	public CommandData onCommand(CommandData data) {
		if (!validate(data)) return data;
		HyperEconomy he = super.getEconomy();
		try {
			boolean requireShop = hc.getConf().getBoolean("shop.limit-info-commands-to-shops");
			if (hp != null && requireShop && !dm.getHyperShopManager().inAnyShop(hp) && !hp.hasPermission("hyperconomy.admin")) {
				data.addResponse(L.get("REQUIRE_SHOP_FOR_INFO"));
				return data;
			}
			String name = he.fixName(args[0]);
			HyperObject ho = he.getHyperObject(name, dm.getHyperShopManager().getShop(hp));
			if (ho == null) {
				data.addResponse(L.get("INVALID_ITEM_NAME"));
				return data;
			}
			int amount = 1;
			if (ho.getType() != HyperObjectType.ENCHANTMENT && args.length > 1) {
				amount = Integer.parseInt(args[1]);
				if (amount > 10000) {
					amount = 10000;
				}
			}
			EnchantmentClass eClass = EnchantmentClass.DIAMOND;
			if (ho.getType() == HyperObjectType.ENCHANTMENT && args.length > 1) {
				eClass = EnchantmentClass.fromString(args[1]);
				if (eClass == EnchantmentClass.NONE) {
					eClass = EnchantmentClass.DIAMOND;
				}
			}
			double val = 0;
			double cost = 0;
			if (hp != null) {
				if (ho.getType() == HyperObjectType.ITEM) {
					val = ho.getSellPriceWithTax(amount, hp);
					cost = ho.getBuyPriceWithTax(amount);
				} else if (ho.getType() == HyperObjectType.ENCHANTMENT) {
					val = ho.getSellPrice(eClass, hp);
					val -= hp.getSalesTax(val);
					cost = ho.getBuyPrice(eClass);
					cost += ho.getPurchaseTax(cost);
				} else if (ho.getType() == HyperObjectType.EXPERIENCE) {
					val = ho.getSellPrice(amount);
					val -= hp.getSalesTax(val);
					cost = ho.getBuyPriceWithTax(amount);
				}
			} else {
				if (ho.getType() == HyperObjectType.ITEM) {
					val = ho.getSellPrice(amount);
					val -= ho.getSalesTaxEstimate(val);
					cost = ho.getBuyPriceWithTax(amount);
				} else if (ho.getType() == HyperObjectType.ENCHANTMENT) {
					val = ho.getSellPrice(eClass);
					val -= ho.getSalesTaxEstimate(val);
					cost = ho.getBuyPrice(eClass);
					cost += ho.getPurchaseTax(cost);
				} else if (ho.getType() == HyperObjectType.EXPERIENCE) {
					val = ho.getSellPrice(amount);
					val -= ho.getSalesTaxEstimate(val);
					cost = ho.getBuyPriceWithTax(amount);
				}
			}


			data.addResponse(L.get("LINE_BREAK"));
			data.addResponse(L.f(L.get("CAN_BE_SOLD_FOR"), amount, cf.twoDecimals(val), ho.getDisplayName()));
			data.addResponse(L.f(L.get("CAN_BE_PURCHASED_FOR"), amount, cf.twoDecimals(cost), ho.getDisplayName()));
			data.addResponse(L.f(L.get("GLOBAL_SHOP_CURRENTLY_HAS"), cf.twoDecimals(ho.getStock()), ho.getDisplayName()));
			data.addResponse(L.get("LINE_BREAK"));

		} catch (Exception e) {
			data.addResponse(L.get("VALUE_INVALID"));
		}
		return data;
	}
}
