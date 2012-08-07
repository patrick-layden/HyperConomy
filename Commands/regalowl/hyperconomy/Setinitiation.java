package regalowl.hyperconomy;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class Setinitiation {

	Setinitiation(String args[], CommandSender sender, String playerecon) {
		HyperConomy hc = HyperConomy.hc;
		SQLFunctions sf = hc.getSQLFunctions();
		InfoSign isign = hc.getInfoSign();
		String name = "";
		try {
			if (args.length == 1) {
				name = args[0];
				String teststring = hc.testiString(name);
				if (teststring != null) {
					boolean nstatus;
					boolean istatus = Boolean.parseBoolean(sf.getInitiation(
							name, playerecon));
					if (istatus) {
						nstatus = false;
						sender.sendMessage(ChatColor.GOLD
								+ "Initiation price is set to false for "
								+ name);
					} else {
						nstatus = true;
						sender.sendMessage(ChatColor.GOLD
								+ "Initiation price is set to true for " + name);
					}
					sf.setInitiation(name, playerecon, nstatus + "");
					isign.setrequestsignUpdate(true);
					isign.checksignUpdate();
				} else {
					sender.sendMessage(ChatColor.DARK_RED
							+ "Invalid item name!");
				}
			} else if (args.length == 2) {
				String ench = args[1];
				if (ench.equalsIgnoreCase("e")) {
					name = args[0];
					String teststring = hc.testeString(name);
					if (teststring != null) {
						boolean nstatus;
						boolean istatus = Boolean.parseBoolean(sf
								.getInitiation(name, playerecon));
						if (istatus) {
							nstatus = false;
							sender.sendMessage(ChatColor.GOLD
									+ "Initiation price is set to false for "
									+ name);
						} else {
							nstatus = true;
							sender.sendMessage(ChatColor.GOLD
									+ "Initiation price is set to true for "
									+ name);
						}
						sf.setInitiation(name, playerecon, nstatus + "");
						isign.setrequestsignUpdate(true);
						isign.checksignUpdate();
					} else {
						sender.sendMessage(ChatColor.DARK_RED
								+ "Invalid enchantment name");
					}
				} else {
					sender.sendMessage(ChatColor.DARK_RED
							+ "Invalid parameters. Use /setinitiation [item/enchantment name] ('e')");
				}
			} else {
				sender.sendMessage(ChatColor.DARK_RED
						+ "Invalid parameters. Use /setinitiation [item/enchantment name] ('e')");
			}
		} catch (Exception e) {
			sender.sendMessage(ChatColor.DARK_RED
					+ "Invalid parameters. Use /setinitiation [item/enchantment name] ('e')");
		}
	}
}
