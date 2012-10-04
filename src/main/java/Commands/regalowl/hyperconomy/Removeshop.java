package regalowl.hyperconomy;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class Removeshop {
	Removeshop(CommandSender sender, String[] args) {
		HyperConomy hc = HyperConomy.hc;
		Shop s = hc.getShop();
		try {
			if (args.length > 0) {
				int counter = 0;
				String name = "";
				while (counter < args.length) {
					if (counter == 0) {
						name = args[0];
					} else {
						name = name + "_" + args[counter];
					}
					counter++;
				}
				String teststring = hc.getYaml().getShops().getString(name);
				if (teststring == null) {
					name = hc.fixsName(name);
				}
				s.setrShop(name);
				s.removeShop();
				sender.sendMessage(ChatColor.GOLD + name.replace("_", " ") + " has been removed!");
			} else {
				sender.sendMessage(ChatColor.DARK_RED + "Invalid Parameters.  Use /removeshop [name]");
			}
		} catch (Exception e) {
			sender.sendMessage(ChatColor.DARK_RED + "That shop doesn't exist.");
		}
	}
}
