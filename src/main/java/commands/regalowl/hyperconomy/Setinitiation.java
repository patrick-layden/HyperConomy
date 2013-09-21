package regalowl.hyperconomy;

import org.bukkit.command.CommandSender;

public class Setinitiation {
	Setinitiation(String args[], CommandSender sender, String playerecon) {
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
					boolean istatus = Boolean.parseBoolean(he.getHyperObject(name).getInitiation());
					if (istatus) {
						nstatus = false;
						sender.sendMessage(L.f(L.get("INITIATION_FALSE"), name));
					} else {
						nstatus = true;
						//sender.sendMessage(ChatColor.GOLD + "Initiation price is set to true for " + name);
						sender.sendMessage(L.f(L.get("INITIATION_TRUE"), name));
					}
					he.getHyperObject(name).setInitiation(nstatus + "");
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
						boolean istatus = Boolean.parseBoolean(he.getHyperObject(name).getInitiation());
						if (istatus) {
							nstatus = false;
							sender.sendMessage(L.f(L.get("INITIATION_FALSE"), name));
						} else {
							nstatus = true;
							sender.sendMessage(L.f(L.get("INITIATION_TRUE"), name));
						}
						he.getHyperObject(name).setInitiation(nstatus + "");
						isign.updateSigns();
					} else {
						sender.sendMessage(L.get("INVALID_ENCHANTMENT_NAME"));
					}
				} else {
					sender.sendMessage(L.get("INITIATION_INVALID"));
				}
			} else {
				sender.sendMessage(L.get("INITIATION_INVALID"));
			}
		} catch (Exception e) {
			sender.sendMessage(L.get("INITIATION_INVALID"));
		}
	}
}
