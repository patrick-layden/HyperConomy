package regalowl.hyperconomy;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import regalowl.databukkit.CommonFunctions;

public class Value {
	HyperConomy hc;

	Value(String args[], CommandSender sender, String playerecon) {
		hc = HyperConomy.hc;
		HyperEconomy he = hc.getEconomyManager().getEconomy(playerecon);
		CommonFunctions cf = hc.gCF();
		LanguageFile L = hc.getLanguageFile();
		Player player = null;
		EconomyManager em = hc.getEconomyManager();
		if (sender instanceof Player) {
			player = (Player) sender;
		}
		try {
			boolean requireShop = hc.gYH().gFC("config").getBoolean("config.limit-info-commands-to-shops");
			if (player != null && requireShop && !em.inAnyShop(player) && !player.hasPermission("hyperconomy.admin")) {
				sender.sendMessage(L.get("REQUIRE_SHOP_FOR_INFO"));
				return;
			}
			String name = he.fixName(args[0]);
			HyperObject ho = he.getHyperObject(name, em.getShop(player));
			if (ho == null) {
				sender.sendMessage(L.get("INVALID_ITEM_NAME"));
				return;
			}
			int amount = 1;
			if (args.length > 1) {
				amount = Integer.parseInt(args[1]);
				if (amount > 10000) {
					amount = 10000;
				}
			}
			double val = 0;
			double cost = 0;
			if (player != null) {
				HyperPlayer hp = em.getHyperPlayer(player);
				if (ho.getType() == HyperObjectType.ITEM) {
					val = ho.getSellPriceWithTax(amount, hp);
					cost = ho.getBuyPriceWithTax(amount);
				} else if (ho.getType() == HyperObjectType.ENCHANTMENT) {
					val = ho.getSellPrice(EnchantmentClass.DIAMOND, hp);
					val -= hp.getSalesTax(val);
					cost = ho.getBuyPrice(EnchantmentClass.DIAMOND);
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
					val = ho.getSellPrice(EnchantmentClass.DIAMOND);
					val -= ho.getSalesTaxEstimate(val);
					cost = ho.getBuyPrice(EnchantmentClass.DIAMOND);
					cost += ho.getPurchaseTax(cost);
				} else if (ho.getType() == HyperObjectType.EXPERIENCE) {
					val = ho.getSellPrice(amount);
					val -= ho.getSalesTaxEstimate(val);
					cost = ho.getBuyPriceWithTax(amount);
				}
			}


			sender.sendMessage(L.get("LINE_BREAK"));
			sender.sendMessage(L.f(L.get("CAN_BE_SOLD_FOR"), amount, val, ho.getDisplayName()));
			sender.sendMessage(L.f(L.get("CAN_BE_PURCHASED_FOR"), amount, cost, ho.getDisplayName()));
			sender.sendMessage(L.f(L.get("GLOBAL_SHOP_CURRENTLY_HAS"), cf.twoDecimals(ho.getStock()), ho.getDisplayName()));
			sender.sendMessage(L.get("LINE_BREAK"));

		} catch (Exception e) {
			sender.sendMessage(L.get("VALUE_INVALID"));
			return;
		}
	}
}
