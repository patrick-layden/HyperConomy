package regalowl.hyperconomy;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class Listshops {
	Listshops(CommandSender sender, String[] args) {
		HyperConomy hc = HyperConomy.hc;
		LanguageFile L = hc.getLanguageFile();
		try {
			if (args.length == 0) {
				String shoplist = HyperConomy.hyperAPI.listShops().toString().replace("_", " ").replace("[", "").replace("]", "");
				sender.sendMessage(ChatColor.AQUA + shoplist);
			} else {
				sender.sendMessage(L.get("LISTSHOPS_INVALID"));
			}
		} catch (Exception e) {
			sender.sendMessage(L.get("LISTSHOPS_INVALID"));
		}
	}
}
