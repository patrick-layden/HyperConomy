package regalowl.hyperconomy;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class Listshops {
	Listshops(CommandSender sender, String[] args) {
		HyperConomy hc = HyperConomy.hc;
		Shop s = hc.getShop();
		try {
			if (args.length == 0) {
				String shoplist = s.listShops().toString().replace("_", " ").replace("[", "").replace("]", "");
				sender.sendMessage(ChatColor.AQUA + shoplist);
			} else {
				sender.sendMessage(ChatColor.DARK_RED + "Invalid Parameters.  Use /listshops");
			}
		} catch (Exception e) {
			sender.sendMessage(ChatColor.DARK_RED + "Invalid Parameters.  Use /listshops");
		}
	}
}
