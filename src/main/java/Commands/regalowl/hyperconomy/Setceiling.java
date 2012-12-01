package regalowl.hyperconomy;

import org.bukkit.command.CommandSender;

public class Setceiling {
	Setceiling(String args[], CommandSender sender, String playerecon) {
		HyperConomy hc = HyperConomy.hc;
		SQLFunctions sf = hc.getSQLFunctions();
		InfoSign isign = hc.getInfoSign();
		LanguageFile L = hc.getLanguageFile();
		String name = "";
		try {
			if (args.length == 2) {
				name = args[0];
				double ceiling = Double.parseDouble(args[1]);
				if (hc.itemTest(name) || hc.enchantTest(name)) {
					sf.setCeiling(name, playerecon, ceiling);
					sender.sendMessage(L.f(L.get("CEILING_SET"), name));
					isign.setrequestsignUpdate(true);
					isign.checksignUpdate();
				} else {
					sender.sendMessage(L.get("INVALID_NAME"));
				}
			} else {
				sender.sendMessage(L.get("SETCEILING_INVALID"));
			}
		} catch (Exception e) {
			sender.sendMessage(L.get("SETCEILING_INVALID"));
		}
	}
}
