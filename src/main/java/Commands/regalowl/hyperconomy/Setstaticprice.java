package regalowl.hyperconomy;

import org.bukkit.command.CommandSender;

public class Setstaticprice {
	Setstaticprice(String args[], CommandSender sender, String playerecon) {
		HyperConomy hc = HyperConomy.hc;
		DataFunctions sf = hc.getSQLFunctions();
		InfoSign isign = hc.getInfoSign();
		LanguageFile L = hc.getLanguageFile();
		String name = "";
		try {
			if (args.length == 2) {
				name = args[0];
				Double staticprice = Double.parseDouble(args[1]);
				if (hc.itemTest(name)) {
					sf.setStaticPrice(name, playerecon, staticprice);
					//sender.sendMessage(ChatColor.GOLD + "" + name + " static price set!");
					sender.sendMessage(L.f(L.get("STATIC_PRICE_SET"), name));
					isign.setrequestsignUpdate(true);
					isign.checksignUpdate();
				} else {
					sender.sendMessage(L.get("INVALID_ITEM_NAME"));
				}
			} else if (args.length == 3) {
				String ench = args[2];
				if (ench.equalsIgnoreCase("e")) {
					name = args[0];
					Double staticprice = Double.parseDouble(args[1]);
					if (hc.enchantTest(name)) {
						sf.setStaticPrice(name, playerecon, staticprice);
						//sender.sendMessage(ChatColor.GOLD + "" + name + " static price set!");
						sender.sendMessage(L.f(L.get("STATIC_PRICE_SET"), name));
						isign.setrequestsignUpdate(true);
						isign.checksignUpdate();
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
