package regalowl.hyperconomy;

import org.bukkit.command.CommandSender;

public class Setstaticprice {
	Setstaticprice(String args[], CommandSender sender, String playerecon) {
		HyperConomy hc = HyperConomy.hc;
		DataHandler sf = hc.getDataFunctions();
		InfoSignHandler isign = hc.getInfoSignHandler();
		LanguageFile L = hc.getLanguageFile();
		String name = "";
		try {
			if (args.length == 2) {
				name = args[0];
				Double staticprice = Double.parseDouble(args[1]);
				if (hc.itemTest(name)) {
					sf.getHyperObject(name, playerecon).setStaticprice(staticprice);
					sender.sendMessage(L.f(L.get("STATIC_PRICE_SET"), name));
					isign.updateSigns();
				} else {
					sender.sendMessage(L.get("INVALID_ITEM_NAME"));
				}
			} else if (args.length == 3) {
				String ench = args[2];
				if (ench.equalsIgnoreCase("e")) {
					name = args[0];
					Double staticprice = Double.parseDouble(args[1]);
					if (hc.enchantTest(name)) {
						sf.getHyperObject(name, playerecon).setStaticprice(staticprice);
						sender.sendMessage(L.f(L.get("STATIC_PRICE_SET"), name));
						isign.updateSigns();
					} else {
						sender.sendMessage(L.get("INVALID_ENCHANTMENT_NAME"));
					}
				} else {
					sender.sendMessage(L.get("SETSTATICPRICE_INVALID"));
				}
			} else {
				sender.sendMessage(L.get("SETSTATICPRICE_INVALID"));
			}
		} catch (Exception e) {
			sender.sendMessage(L.get("SETSTATICPRICE_INVALID"));
		}
	}
}
