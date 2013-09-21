package regalowl.hyperconomy;

import java.util.ArrayList;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class Listeconomies {
	Listeconomies(String args[], CommandSender sender) {
		HyperConomy hc = HyperConomy.hc;
		EconomyManager em = hc.getEconomyManager();
		LanguageFile L = hc.getLanguageFile();
		try {
			if (args.length == 0) {
				ArrayList<String> economies = em.getEconomyList();
				sender.sendMessage(ChatColor.AQUA + economies.toString());
			} else {
				sender.sendMessage(L.get("LISTECONOMIES_INVALID"));
			}
		} catch (Exception e) {
			sender.sendMessage(L.get("LISTECONOMIES_INVALID"));
		}
	}
}
