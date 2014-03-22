package regalowl.hyperconomy.command;

import java.util.ArrayList;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import regalowl.hyperconomy.DataManager;
import regalowl.hyperconomy.HyperConomy;
import regalowl.hyperconomy.util.LanguageFile;

public class Listeconomies {
	Listeconomies(String args[], CommandSender sender) {
		HyperConomy hc = HyperConomy.hc;
		DataManager em = hc.getDataManager();
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
