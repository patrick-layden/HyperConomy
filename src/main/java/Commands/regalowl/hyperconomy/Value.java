package regalowl.hyperconomy;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Value {
	HyperConomy hc;

	Value(String args[], CommandSender sender, String playerecon) {
		hc = HyperConomy.hc;
		DataHandler sf = hc.getDataFunctions();
		Calculation calc = hc.getCalculation();
		LanguageFile L = hc.getLanguageFile();
		Player player = null;
		ShopFactory s = hc.getShopFactory();
		try {
			if (sender instanceof Player) {
				player = (Player) sender;
			}
			boolean requireShop = hc.getConfig().getBoolean("config.limit-info-commands-to-shops");
			if (player == null || (requireShop && s.getShop(player) != null) || !requireShop || player.hasPermission("hyperconomy.admin")) {
				String name = args[0];
				int amount;
				if (args.length == 2) {
					amount = Integer.parseInt(args[1]);
				} else {
					amount = 1;
				}
				if (sf.itemTest(name)) {
					double val = calc.getTvalue(name, amount, playerecon);
					double salestax = 0;
					if (player != null) {
						salestax = calc.getSalesTax(player, val);
					}
					val = calc.twoDecimals(val - salestax);
					sender.sendMessage(L.get("LINE_BREAK"));
					sender.sendMessage(L.f(L.get("CAN_BE_SOLD_FOR"), amount, val, name));
					double cost = calc.getCost(name, amount, playerecon);
					double taxpaid = calc.getPurchaseTax(name, playerecon, cost);
					cost = calc.twoDecimals(cost + taxpaid);
					if (cost > Math.pow(10, 10)) {
						cost = -1;
					}
					double stock = 0;
					stock = sf.getHyperObject(name, playerecon).getStock();
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
