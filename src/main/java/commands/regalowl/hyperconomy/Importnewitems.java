package regalowl.hyperconomy;

import java.util.ArrayList;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class Importnewitems {
	Importnewitems(String args[], CommandSender sender) {
		HyperConomy hc = HyperConomy.hc;
		LanguageFile L = hc.getLanguageFile();
		EconomyManager em = hc.getEconomyManager();
		try {
			if (args.length == 1) {
				String economy = args[0];
				if (em.economyExists(economy)) {
					if (hc.gYH().gFC("config").getBoolean("config.run-automatic-backups")) {
						new Backup();
					}
					ArrayList<String> added = em.getEconomy(economy).loadNewItems();
					sender.sendMessage(ChatColor.GOLD + added.toString() + " " + L.get("LOADED_INTO_ECONOMY"));
				} else {
					sender.sendMessage(L.get("ECONOMY_NOT_EXIST"));
				}
			} else {
				sender.sendMessage(L.get("IMPORTNEWITEMS_INVALID"));
			}
		} catch (Exception e) {
			sender.sendMessage(L.get("IMPORTNEWITEMS_INVALID"));
		}
	}
}
