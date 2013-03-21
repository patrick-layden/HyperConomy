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
		DataHandler dh = hc.getDataFunctions();
		try {
			if (sender instanceof Player) {
				player = (Player) sender;
			}
			boolean requireShop = hc.getConfig().getBoolean("config.limit-info-commands-to-shops");
			if (player == null || (requireShop && s.inAnyShop(player)) || !requireShop || player.hasPermission("hyperconomy.admin")) {
				String name = sf.fixName(args[0]);
				int amount;
				if (args.length == 2) {
					amount = Integer.parseInt(args[1]);
				} else {
					amount = 1;
				}
				if (sf.itemTest(name)) {
					HyperPlayer hp = dh.getHyperPlayer(player);
					HyperObject ho = dh.getHyperObject(name, hp.getEconomy());
					double val = ho.getValue(amount);
					double salestax = 0;
					if (player != null) {
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
