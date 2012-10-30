package regalowl.hyperconomy;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class Setstatic {
	Setstatic(String args[], CommandSender sender, String playerecon) {
		HyperConomy hc = HyperConomy.hc;
		SQLFunctions sf = hc.getSQLFunctions();
		InfoSign isign = hc.getInfoSign();
		LanguageFile L = hc.getLanguageFile();
		String name = "";
		try {
			if (args.length == 1) {
				name = args[0];
				String teststring = hc.testiString(name);
				if (teststring != null) {
					boolean nstatus;
					boolean sstatus = false;
					sstatus = Boolean.parseBoolean(sf.getStatic(name, playerecon));
					if (sstatus) {
						nstatus = false;
						//sender.sendMessage(ChatColor.GOLD + "" + name + " will now use a dynamic price.");
						sender.sendMessage(L.f(L.get("USE_DYNAMIC_PRICE"), name));
					} else {
						nstatus = true;
						//sender.sendMessage(ChatColor.GOLD + "" + name + " will now use a static price.");
						sender.sendMessage(L.f(L.get("USE_STATIC_PRICE"), name));
					}
					sf.setStatic(name, playerecon, nstatus + "");
					isign.setrequestsignUpdate(true);
					isign.checksignUpdate();
				} else {
					sender.sendMessage(L.get("INVALID_ITEM_NAME"));
				}
			} else if (args.length == 2) {
				String ench = args[1];
				if (ench.equalsIgnoreCase("e")) {
					name = args[0];
					String teststring = hc.testeString(name);
					if (teststring != null) {
						boolean nstatus;
						boolean sstatus = Boolean.parseBoolean(sf.getStatic(name, playerecon));
						if (sstatus) {
							nstatus = false;
							sender.sendMessage(L.f(L.get("USE_DYNAMIC_PRICE"), name));
							//sender.sendMessage(ChatColor.GOLD + "" + name + " will now use a dynamic price.");
						} else {
							nstatus = true;
							//sender.sendMessage(ChatColor.GOLD + "" + name + " will now use a static price.");
							sender.sendMessage(L.f(L.get("USE_STATIC_PRICE"), name));
						}
						sf.setStatic(name, playerecon, nstatus + "");
						isign.setrequestsignUpdate(true);
						isign.checksignUpdate();
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
