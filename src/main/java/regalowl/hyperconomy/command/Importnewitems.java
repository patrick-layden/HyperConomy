package regalowl.hyperconomy.command;

import java.util.ArrayList;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import regalowl.databukkit.YamlHandler;
import regalowl.hyperconomy.DataManager;
import regalowl.hyperconomy.HyperConomy;
import regalowl.hyperconomy.util.Backup;
import regalowl.hyperconomy.util.LanguageFile;

public class Importnewitems {
	
	Importnewitems(String args[], CommandSender sender) {
		HyperConomy hc = HyperConomy.hc;
		LanguageFile L = hc.getLanguageFile();
		DataManager em = hc.getDataManager();
		YamlHandler yh = hc.getYamlHandler();
		try {
			String economy = "default";
			if (args.length > 0) {
				economy = args[0];
			}
			if (em.economyExists(economy) || args[0].equalsIgnoreCase("update")) {
				if (hc.gYH().gFC("config").getBoolean("config.run-automatic-backups")) {
					new Backup();
				}
				yh.unRegisterFileConfiguration("objects");
				yh.deleteConfigFile("objects");
				yh.copyFromJar("objects");
				yh.registerFileConfiguration("objects");
				ArrayList<String> added = em.getEconomy(economy).loadNewItems();
				sender.sendMessage(ChatColor.GOLD + added.toString() + " " + L.get("LOADED_INTO_ECONOMY"));
			} else {
				sender.sendMessage(L.get("ECONOMY_NOT_EXIST"));
			}

		} catch (Exception e) {
			sender.sendMessage(L.get("IMPORTNEWITEMS_INVALID"));
		}
	}

}
