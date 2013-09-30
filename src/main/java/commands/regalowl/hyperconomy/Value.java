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

		
		try {
			if (sender instanceof Player) {
				player = (Player) sender;
			}
			boolean requireShop = hc.getConfig().getBoolean("config.limit-info-commands-to-shops");
			if (player == null || (requireShop && he.inAnyShop(player)) || !requireShop || player.hasPermission("hyperconomy.admin")) {
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
				if (he.itemTest(name)) {
					HyperObject ho = he.getHyperObject(name, he.getShop(player));
					double val = ho.getValue(amount);
					double salestax = 0;
					if (player != null) {
						HyperPlayer hp = he.getHyperPlayer(player);
						salestax = hp.getSalesTax(val);
					}
					val = calc.twoDecimals(val - salestax);
					sender.sendMessage(L.get("LINE_BREAK"));
					sender.sendMessage(L.f(L.get("CAN_BE_SOLD_FOR"), amount, val, name));
					double cost = ho.getCost(amount);
					double taxpaid = ho.getPurchaseTax(cost);
					cost = calc.twoDecimals(cost + taxpaid);
					if (cost > Math.pow(10, 10)) {
						cost = -1;
					}
					double stock = 0;
					stock = calc.twoDecimals(he.getHyperObject(name, he.getShop(player)).getStock());
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
