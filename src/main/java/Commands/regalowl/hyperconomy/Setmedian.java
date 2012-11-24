package regalowl.hyperconomy;

import org.bukkit.command.CommandSender;

public class Setmedian {
	Setmedian(String args[], CommandSender sender, String playerecon) {
		HyperConomy hc = HyperConomy.hc;
		SQLFunctions sf = hc.getSQLFunctions();
		InfoSign isign = hc.getInfoSign();
		LanguageFile L = hc.getLanguageFile();
		String name = "";
		try {
			if (args.length == 2) {
				name = args[0];
				int median = Integer.parseInt(args[1]);
				if (hc.itemTest(name)) {
					sf.setMedian(name, playerecon, median);
					//sender.sendMessage(ChatColor.GOLD + "" + name + " median set!");
					sender.sendMessage(L.f(L.get("MEDIAN_SET"), name));
					isign.setrequestsignUpdate(true);
					isign.checksignUpdate();
				} else {
					sender.sendMessage(L.get("INVALID_ITEM_NAME"));
				}
			} else if (args.length == 3) {
				String ench = args[2];
				if (ench.equalsIgnoreCase("e")) {
					name = args[0];
					int median = Integer.parseInt(args[1]);
					if (hc.enchantTest(name)) {
						sf.setMedian(name, playerecon, median);
						//sender.sendMessage(ChatColor.GOLD + "" + name + " median set!");
						sender.sendMessage(L.f(L.get("MEDIAN_SET"), name));
						isign.setrequestsignUpdate(true);
						isign.checksignUpdate();
					} else {
						sender.sendMessage(L.get("INVALID_ENCHANTMENT_NAME"));
					}
				} else {
					sender.sendMessage(L.get("SETMEDIAN_INVALID"));
				}
			} else {
				sender.sendMessage(L.get("SETMEDIAN_INVALID"));
			}
		} catch (Exception e) {
			sender.sendMessage(L.get("SETMEDIAN_INVALID"));
		}
	}
}
