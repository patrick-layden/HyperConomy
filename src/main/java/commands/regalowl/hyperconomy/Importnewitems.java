package regalowl.hyperconomy;

import java.io.File;
import java.util.ArrayList;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import regalowl.databukkit.FileTools;

public class Importnewitems {
	
	Importnewitems(String args[], CommandSender sender) {
		HyperConomy hc = HyperConomy.hc;
		LanguageFile L = hc.getLanguageFile();
		EconomyManager em = hc.getEconomyManager();
		
		try {
			String economy = "default";
			if (args.length > 0) {
				economy = args[0];
			}
			if (em.economyExists(economy) || args[0].equalsIgnoreCase("update")) {
				if (args[0].equalsIgnoreCase("update")) {
					new Backup();
					FileTools ft = hc.getFileTools();
					String folderPath = hc.getFolderPath();
					hc.disable(true);
					ft.deleteFile(folderPath + File.separator + "objects.yml");
					hc.restart();
				} else {
					if (hc.gYH().gFC("config").getBoolean("config.run-automatic-backups")) {
						new Backup();
					}
					ArrayList<String> added = em.getEconomy(economy).loadNewItems();
					sender.sendMessage(ChatColor.GOLD + added.toString() + " " + L.get("LOADED_INTO_ECONOMY"));
				}
			} else {
				sender.sendMessage(L.get("ECONOMY_NOT_EXIST"));
			}

		} catch (Exception e) {
			sender.sendMessage(L.get("IMPORTNEWITEMS_INVALID"));
		}
	}

}
