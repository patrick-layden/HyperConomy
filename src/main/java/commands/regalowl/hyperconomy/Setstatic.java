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
				if (he.itemTest(name)) {
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
			} else if (args.length == 2) {
				String ench = args[1];
				if (ench.equalsIgnoreCase("e")) {
					name = args[0];
					if (he.enchantTest(name)) {
						boolean nstatus;
						boolean sstatus = Boolean.parseBoolean(he.getHyperObject(name).getIsstatic());
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
						sender.sendMessage(L.get("INVALID_ENCHANTMENT_NAME"));
					}
				} else {
					sender.sendMessage(L.get("SETSTATIC_INVALID"));
				}
			} else {
				sender.sendMessage(L.get("SETSTATIC_INVALID"));
			}
		} catch (Exception e) {
			sender.sendMessage(L.get("SETSTATIC_INVALID"));
		}
	}
}
