package regalowl.hyperconomy.command;

import java.util.ArrayList;
import java.util.Iterator;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import regalowl.hyperconomy.HyperConomy;
import regalowl.hyperconomy.util.LanguageFile;

public class Listcategories {
	Listcategories(CommandSender sender) {
		HyperConomy hc = HyperConomy.hc;
		LanguageFile L = hc.getLanguageFile();
		try {
			Iterator<String> it = hc.gYH().gFC("categories").getKeys(false).iterator();
			ArrayList<String> categories = new ArrayList<String>();
			while (it.hasNext()) {
				categories.add(it.next().toString());
			}
			sender.sendMessage(ChatColor.AQUA + "" + categories.toString());
			return;
		} catch (Exception e) {
			sender.sendMessage(L.get("LISTCATEGORIES_INVALID"));
			return;
		}
	}
}
