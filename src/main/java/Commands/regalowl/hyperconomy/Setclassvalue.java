package regalowl.hyperconomy;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class Setclassvalue {

	Setclassvalue(String args[], CommandSender sender) {
		HyperConomy hc = HyperConomy.hc;
		InfoSign isign = hc.getInfoSign();
		try {
			if (args.length != 2) {
				sender.sendMessage(ChatColor.DARK_RED + "Invalid Parameters. Use /setclassvalue [item class] [value]");
			} else {
				String classtype = args[0];
				if (hc.getYaml().getConfig().get("config.enchantment.classvalue." + classtype) != null) {
				double value = Double.parseDouble(args[1]);
				hc.getYaml().getConfig().set("config.enchantment.classvalue." + classtype, value);
				sender.sendMessage(ChatColor.BLUE + "The classvalue for " + ChatColor.AQUA + "" + classtype + ChatColor.BLUE + " has been set.");
				isign.setrequestsignUpdate(true);
				isign.checksignUpdate();
				} else {
					sender.sendMessage(ChatColor.DARK_RED + "Invalid item class.");
				}
			}
	} catch (Exception e) {
		sender.sendMessage(ChatColor.DARK_RED + "Invalid Parameters. Use /setclassvalue [item class] [value]");
	}
	}
}
