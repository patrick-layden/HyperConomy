package regalowl.hyperconomy;

import org.bukkit.command.CommandSender;

public class Setstatic {
	Setstatic(String args[], CommandSender sender, String playerecon) {
		HyperConomy hc = HyperConomy.hc;
		HyperEconomy he = hc.getEconomyManager().getEconomy(playerecon);
		InfoSignHandler isign = hc.getInfoSignHandler();
		LanguageFile L = hc.getLanguageFile();
		String name = "";
		try {
			if (args.length == 1) {
				name = he.fixName(args[0]);
				if (he.objectTest(name)) {
					boolean nstatus;
					boolean sstatus = false;
					sstatus = Boolean.parseBoolean(he.getHyperObject(name).getIsstatic());
					if (sstatus) {
						nstatus = false;
						sender.sendMessage(L.f(L.get("USE_DYNAMIC_PRICE"), name));
					} else {
						nstatus = true;
						sender.sendMessage(L.f(L.get("USE_STATIC_PRICE"), name));
					}
					he.getHyperObject(name).setIsstatic(nstatus + "");
					isign.updateSigns();
				} else {
					sender.sendMessage(L.get("INVALID_ITEM_NAME"));
				}
			} else {
				sender.sendMessage(L.get("SETSTATIC_INVALID"));
			}
		} catch (Exception e) {
			sender.sendMessage(L.get("SETSTATIC_INVALID"));
		}
	}
}
