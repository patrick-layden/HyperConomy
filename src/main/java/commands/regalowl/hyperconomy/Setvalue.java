package regalowl.hyperconomy;

import org.bukkit.command.CommandSender;

public class Setvalue {
	Setvalue(String args[], CommandSender sender, String playerecon) {
		HyperConomy hc = HyperConomy.hc;
		HyperEconomy he = hc.getEconomyManager().getEconomy(playerecon);
		InfoSignHandler isign = hc.getInfoSignHandler();
		LanguageFile L = hc.getLanguageFile();
		String name = "";
		try {
			if (args.length == 2) {
				name = he.fixName(args[0]);
				double value = Double.parseDouble(args[1]);
				if (he.objectTest(name)) {
					he.getHyperObject(name).setValue(value);
					sender.sendMessage(L.f(L.get("VALUE_SET"), name));
					isign.updateSigns();
				} else {
					sender.sendMessage(L.get("INVALID_ITEM_NAME"));
				}
			} else {
				sender.sendMessage(L.get("SETVALUE_INVALID"));
			}
		} catch (Exception e) {
			sender.sendMessage(L.get("SETVALUE_INVALID"));
		}
	}
}
