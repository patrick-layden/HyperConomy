package regalowl.hyperconomy;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class Setstaticprice {
	Setstaticprice(String args[], CommandSender sender, String playerecon) {
		HyperConomy hc = HyperConomy.hc;
		SQLFunctions sf = hc.getSQLFunctions();
		InfoSign isign = hc.getInfoSign();
		String name = "";
		try {
			if (args.length == 2) {
				name = args[0];
				Double staticprice = Double.parseDouble(args[1]);
				String teststring = hc.testiString(name);
				if (teststring != null) {
					sf.setStaticPrice(name, playerecon, staticprice);
					sender.sendMessage(ChatColor.GOLD + "" + name
							+ " static price set!");
					isign.setrequestsignUpdate(true);
					isign.checksignUpdate();
				} else {
					sender.sendMessage(ChatColor.DARK_RED
							+ "Invalid item name!");
				}
			} else if (args.length == 3) {
				String ench = args[2];
				if (ench.equalsIgnoreCase("e")) {
					name = args[0];
					Double staticprice = Double.parseDouble(args[1]);
					String teststring = hc.testeString(name);
					if (teststring != null) {
						sf.setStaticPrice(name, playerecon, staticprice);
						sender.sendMessage(ChatColor.GOLD + "" + name
								+ " static price set!");
						isign.setrequestsignUpdate(true);
						isign.checksignUpdate();
					} else {
						sender.sendMessage(ChatColor.DARK_RED
								+ "Invalid enchantment name");
					}
				} else {
					sender.sendMessage(ChatColor.DARK_RED
							+ "Invalid parameters. Use /setstaticprice [item/enchantment name] [staticprice] ('e')");
				}
			} else {
				sender.sendMessage(ChatColor.DARK_RED
						+ "Invalid parameters. Use /setstaticprice [item/enchantment name] [staticprice] ('e')");
			}
		} catch (Exception e) {
			sender.sendMessage(ChatColor.DARK_RED
					+ "Invalid parameters. Use /setstaticprice [item/enchantment name] [staticprice] ('e')");
		}
	}
}
