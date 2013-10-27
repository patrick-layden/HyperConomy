package regalowl.hyperconomy;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Value {
	HyperConomy hc;

	Value(String args[], CommandSender sender, String playerecon) {
		hc = HyperConomy.hc;
		HyperEconomy he = hc.getEconomyManager().getEconomy(playerecon);
		Calculation calc = hc.getCalculation();
		LanguageFile L = hc.getLanguageFile();
		Player player = null;
		EconomyManager em = hc.getEconomyManager();
		
		try {
			if (sender instanceof Player) {
				player = (Player) sender;
			}
			boolean requireShop = hc.getConfig().getBoolean("config.limit-info-commands-to-shops");
			if (player == null || (requireShop && em.inAnyShop(player)) || !requireShop || player.hasPermission("hyperconomy.admin")) {
				String name = he.fixName(args[0]);
				int amount;
				if (args.length == 2) {
					amount = Integer.parseInt(args[1]);
					if (amount > 10000) {
						amount = 10000;
					}
				} else {
					amount = 1;
				}
				BasicObject bo = he.getBasicObject(name, em.getShop(player));
				HyperItem hi = he.getHyperItem(name, em.getShop(player));
				if (hi != null) {
					double val = hi.getValue(amount);
					double salestax = 0;
					if (player != null) {
						HyperPlayer hp = em.getHyperPlayer(player);
						salestax = hp.getSalesTax(val);
					}
					val = calc.twoDecimals(val - salestax);
					sender.sendMessage(L.get("LINE_BREAK"));
					sender.sendMessage(L.f(L.get("CAN_BE_SOLD_FOR"), amount, val, name));
					double cost = hi.getCost(amount);
					double taxpaid = hi.getPurchaseTax(cost);
					cost = calc.twoDecimals(cost + taxpaid);
					if (cost > Math.pow(10, 10)) {
						cost = -1;
					}
					double stock = 0;
					stock = calc.twoDecimals(he.getHyperObject(name, em.getShop(player)).getStock());
					sender.sendMessage(L.f(L.get("CAN_BE_PURCHASED_FOR"), amount, cost, name));
					sender.sendMessage(L.f(L.get("GLOBAL_SHOP_CURRENTLY_HAS"), stock, name));
					sender.sendMessage(L.get("LINE_BREAK"));
				} else if (bo != null) {
					double val = bo.getValue(amount);
					double salestax = 0;
					if (player != null) {
						HyperPlayer hp = em.getHyperPlayer(player);
						salestax = hp.getSalesTax(val);
					}
					val = calc.twoDecimals(val - salestax);
					sender.sendMessage(L.get("LINE_BREAK"));
					sender.sendMessage(L.f(L.get("CAN_BE_SOLD_FOR"), amount, val, name));
					double cost = bo.getCost(amount);
					double taxpaid = bo.getPurchaseTax(cost);
					cost = calc.twoDecimals(cost + taxpaid);
					if (cost > Math.pow(10, 10)) {
						cost = -1;
					}
					double stock = 0;
					stock = calc.twoDecimals(he.getHyperObject(name, em.getShop(player)).getStock());
					sender.sendMessage(L.f(L.get("CAN_BE_PURCHASED_FOR"), amount, cost, name));
					sender.sendMessage(L.f(L.get("GLOBAL_SHOP_CURRENTLY_HAS"), stock, name));
					sender.sendMessage(L.get("LINE_BREAK"));
				} else {
					sender.sendMessage(L.get("INVALID_ITEM_NAME"));
					return;
				}
			} else {
				sender.sendMessage(L.get("REQUIRE_SHOP_FOR_INFO"));
				return;
			}
		} catch (Exception e) {
			sender.sendMessage(L.get("VALUE_INVALID"));
			return;
		}
	}
}
