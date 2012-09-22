package regalowl.hyperconomy;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class Settax {

	Settax(String args[], CommandSender sender) {
		HyperConomy hc = HyperConomy.hc;
		InfoSign isign = hc.getInfoSign();
		double taxrate = 0.0;
		try {
			if (args.length != 2) {
				sender.sendMessage(ChatColor.DARK_RED + "Use /settax [puchase/sales/static/initial/enchant] [percent]");
				return;
			} else {
				String type = args[0];
				if (type.equalsIgnoreCase("purchase")) {
					taxrate = Double.parseDouble(args[1]);
					hc.getYaml().getConfig().set("config.purchasetaxpercent", taxrate);
					sender.sendMessage(ChatColor.GOLD + "The purchase tax rate has been set!");
				} else if (type.equalsIgnoreCase("sales")) {
					taxrate = Double.parseDouble(args[1]);
					hc.getYaml().getConfig().set("config.sales-tax-percent", taxrate);
					sender.sendMessage(ChatColor.GOLD + "The sales tax rate has been set!");
				} else if (type.equalsIgnoreCase("static")) {
					taxrate = Double.parseDouble(args[1]);
					hc.getYaml().getConfig().set("config.statictaxpercent", taxrate);
					sender.sendMessage(ChatColor.GOLD + "The static tax rate has been set!");
				} else if (type.equalsIgnoreCase("initial")) {
					taxrate = Double.parseDouble(args[1]);
					hc.getYaml().getConfig().set("config.initialpurchasetaxpercent", taxrate);
					sender.sendMessage(ChatColor.GOLD + "The initial tax rate has been set!");
				} else if (type.equalsIgnoreCase("enchant")) {
					taxrate = Double.parseDouble(args[1]);
					hc.getYaml().getConfig().set("config.enchanttaxpercent", taxrate);
					sender.sendMessage(ChatColor.GOLD + "The enchantment tax rate has been set!");
				} else {
					sender.sendMessage(ChatColor.DARK_RED + "Use /settax [puchase/sales/static/initial/enchant] [percent]");
					return;
				}
				isign.setrequestsignUpdate(true);
				isign.checksignUpdate();
			}
		} catch (Exception e) {
			sender.sendMessage(ChatColor.DARK_RED + "Use /settax [puchase/sales/static/initial/enchant] [percent]");
		}
	}
	
	
}
