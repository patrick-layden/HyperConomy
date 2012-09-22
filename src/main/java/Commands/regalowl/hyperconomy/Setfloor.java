package regalowl.hyperconomy;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class Setfloor {
	Setfloor(String args[], CommandSender sender, String playerecon) {
		HyperConomy hc = HyperConomy.hc;
		SQLFunctions sf = hc.getSQLFunctions();
		InfoSign isign = hc.getInfoSign();
		String name = "";
		try {
			if (args.length == 2) {
				name = args[0];
				double floor = Double.parseDouble(args[1]);
				String teststring1 = hc.testiString(name);
				String teststring2 = hc.testeString(name);
				if (teststring1 != null || teststring2 != null) {
					sf.setFloor(name, playerecon, floor);
					sender.sendMessage(ChatColor.GOLD + "" + name
							+ " floor set!");
					isign.setrequestsignUpdate(true);
					isign.checksignUpdate();
				} else {
					sender.sendMessage(ChatColor.DARK_RED
							+ "Invalid name!");
				}
			} else {
				sender.sendMessage(ChatColor.DARK_RED
						+ "Invalid parameters. Use /setfloor [item/enchantment name] [floor]");
			}
		} catch (Exception e) {
			sender.sendMessage(ChatColor.DARK_RED
					+ "Invalid parameters. Use /setfloor [item/enchantment name] [floor]");
		}
	}
}
