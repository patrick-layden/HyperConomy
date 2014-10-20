package regalowl.hyperconomy.command;

import java.util.Iterator;


import regalowl.hyperconomy.HyperEconomy;
import regalowl.hyperconomy.hyperobject.EnchantmentClass;
import regalowl.hyperconomy.hyperobject.HyperObject;
import regalowl.hyperconomy.serializable.SerializableEnchantment;
import regalowl.hyperconomy.serializable.SerializableItemStack;

public class Hv extends BaseCommand implements HyperCommand {
	public Hv() {
		super(true);
	}
	
	@Override
	public CommandData onCommand(CommandData data) {
		if (!validate(data)) return data;
		int amount;
		try {
			HyperEconomy he = getEconomy();
			boolean requireShop = hc.getConf().getBoolean("shop.limit-info-commands-to-shops");
			if ((requireShop && dm.getHyperShopManager().inAnyShop(hp)) || !requireShop || hp.hasPermission("hyperconomy.admin")) {
				SerializableItemStack iinhand = hp.getItemInHand();
				if (args.length == 0) {
					amount = 1;
				} else {
					amount = Integer.parseInt(args[0]);
					if (amount > 10000) {
						amount = 10000;
					}
				}
				if (!iinhand.hasEnchantments()) {
					HyperObject ho = he.getHyperObject(iinhand, dm.getHyperShopManager().getShop(hp));
					if (ho == null) {
						data.addResponse(L.get("OBJECT_NOT_AVAILABLE"));
					} else {
						String displayName = ho.getDisplayName();
						double val = ho.getSellPrice(amount, hp);
						if (ho.isDurable() && amount > 1) {
							int numberofitem = ho.count(hp.getInventory());
							if (amount - numberofitem > 0) {
								int addamount = amount - numberofitem;
								val = val + ho.getSellPrice(addamount);
							}
						}
						double salestax = hp.getSalesTax(val);
						val = cf.twoDecimals(val - salestax);
						data.addResponse(L.get("LINE_BREAK"));
						data.addResponse(L.f(L.get("CAN_BE_SOLD_FOR"), amount, val, displayName));
						double cost = ho.getBuyPrice(amount);
						double taxpaid = ho.getPurchaseTax(cost);
						cost = cf.twoDecimals(cost + taxpaid);
						double stock = 0;
						stock = ho.getStock();
						data.addResponse(L.f(L.get("CAN_BE_PURCHASED_FOR"), amount, cost, displayName));
						data.addResponse(L.f(L.get("GLOBAL_SHOP_CURRENTLY_HAS"), cf.twoDecimals(stock), displayName));
						data.addResponse(L.get("LINE_BREAK"));
					}
				} else {
					Iterator<SerializableEnchantment> ite = iinhand.getItemMeta().getEnchantments().iterator();
					data.addResponse(L.get("LINE_BREAK"));
					while (ite.hasNext()) {
						SerializableEnchantment ench = ite.next();
						int lvl = ench.getLvl();
						String enam = ench.getEnchantmentName();
						String fnam = enam + lvl;
						String mater = hp.getItemInHand().getMaterial();
						HyperObject ho = he.getHyperObject(fnam, dm.getHyperShopManager().getShop(hp));
						double value = ho.getSellPrice(EnchantmentClass.fromString(mater), hp);
						double cost = ho.getBuyPrice(EnchantmentClass.fromString(mater));
						cost = cost + ho.getPurchaseTax(cost);
						value = cf.twoDecimals(value);
						cost = cf.twoDecimals(cost);
						double salestax = 0;
						salestax = hp.getSalesTax(value);
						value = cf.twoDecimals(value - salestax);
						data.addResponse(L.f(L.get("EVALUE_SALE"), value, fnam));
						data.addResponse(L.f(L.get("EVALUE_PURCHASE"), cost, fnam));
						data.addResponse(L.f(L.get("EVALUE_STOCK"), cf.twoDecimals(he.getHyperObject(fnam, dm.getHyperShopManager().getShop(hp)).getStock()), fnam));
					}
					data.addResponse(L.get("LINE_BREAK"));
				}
			} else {
				data.addResponse(L.get("REQUIRE_SHOP_FOR_INFO"));
			}
		} catch (Exception e) {
			data.addResponse(L.get("HV_INVALID"));
		}
		return data;
	}
}
