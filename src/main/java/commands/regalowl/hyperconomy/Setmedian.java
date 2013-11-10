package regalowl.hyperconomy;

import org.bukkit.command.CommandSender;

public class Setmedian {
	Setmedian(String args[], CommandSender sender, String playerecon) {
		HyperConomy hc = HyperConomy.hc;
		HyperEconomy he = hc.getEconomyManager().getEconomy(playerecon);
		InfoSignHandler isign = hc.getInfoSignHandler();
		LanguageFile L = hc.getLanguageFile();
		String name = "";
		try {
			if (args.length == 2) {
				name = he.fixName(args[0]);
				int median = Integer.parseInt(args[1]);
				if (he.objectTest(name)) {
					he.getHyperObject(name).setMedian(median);
					sender.sendMessage(L.f(L.get("MEDIAN_SET"), name));
					isign.updateSigns();
				} else {
					sender.sendMessage(L.get("INVALID_ITEM_NAME"));
				}
			} else {
				sender.sendMessage(L.get("SETMEDIAN_INVALID"));
			}
		} catch (Exception e) {
			sender.sendMessage(L.get("SETMEDIAN_INVALID"));
		}
	}
}
