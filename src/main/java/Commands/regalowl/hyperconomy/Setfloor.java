package regalowl.hyperconomy;

import org.bukkit.command.CommandSender;

public class Setfloor {
	Setfloor(String args[], CommandSender sender, String playerecon) {
		HyperConomy hc = HyperConomy.hc;
		SQLFunctions sf = hc.getSQLFunctions();
		InfoSign isign = hc.getInfoSign();
		LanguageFile L = hc.getLanguageFile();
		String name = "";
		try {
			if (args.length == 2) {
				name = args[0];
				double floor = Double.parseDouble(args[1]);
				if (hc.itemTest(name) || hc.enchantTest(name)) {
					sf.setFloor(name, playerecon, floor);
					sender.sendMessage(L.f(L.get("FLOOR_SET"), name));
					isign.setrequestsignUpdate(true);
					isign.checksignUpdate();
				} else {
					sender.sendMessage(L.get("INVALID_NAME"));
				}
			} else {
				sender.sendMessage(L.get("SETFLOOR_INVALID"));
			}
		} catch (Exception e) {
			sender.sendMessage(L.get("SETFLOOR_INVALID"));
		}
	}
}
