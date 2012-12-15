package regalowl.hyperconomy;

import org.bukkit.command.CommandSender;

public class Setstock {
	Setstock(String args[], CommandSender sender, String playerecon) {
		HyperConomy hc = HyperConomy.hc;
		SQLFunctions sf = hc.getSQLFunctions();
		InfoSignHandler isign = hc.getInfoSignHandler();
		LanguageFile L = hc.getLanguageFile();
		Calculation calc = hc.getCalculation();
		String name = "";
		try {
			if (args.length == 2) {
				name = args[0];
				double stock = calc.round(Double.parseDouble(args[1]), 2);
				if (hc.itemTest(name)) {
					sf.setStock(name, playerecon, stock);
					sender.sendMessage(L.f(L.get("STOCK_SET"), name));
					isign.updateSigns();
				} else {
					sender.sendMessage(L.get("INVALID_ITEM_NAME"));
				}
			} else if (args.length == 3) {
				String ench = args[2];
				if (ench.equalsIgnoreCase("e")) {
					name = args[0];
					double stock = calc.round(Double.parseDouble(args[1]), 2);
					if (hc.enchantTest(name)) {
						sf.setStock(name, playerecon, stock);
						sender.sendMessage(L.f(L.get("STOCK_SET"), name));
						isign.updateSigns();
					} else {
						sender.sendMessage(L.get("INVALID_ENCHANTMENT_NAME"));
					}
				} else {
					sender.sendMessage(L.get("SETSTOCK_INVALID"));
				}
			} else {
				sender.sendMessage(L.get("SETSTOCK_INVALID"));
			}
		} catch (Exception e) {
			sender.sendMessage(L.get("SETSTOCK_INVALID"));
		}
	}
}
