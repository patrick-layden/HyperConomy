package regalowl.hyperconomy;

import java.io.File;
import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

public class Setlanguage {

	Setlanguage(String args[], CommandSender sender) {
		HyperConomy hc = HyperConomy.hc;
		LanguageFile L = hc.getLanguageFile();
		FileTools ft = new FileTools();
		String folderpath = Bukkit.getServer().getPluginManager().getPlugin("HyperConomy").getDataFolder() + File.separator + "Languages";
		ft.makeFolder(folderpath);
		try {
			if (args.length == 1) {
				String language = args[0].toLowerCase();
				ArrayList<String> sl = L.getSupportedLanguages();
				String filepath = folderpath + File.separator + language + ".txt";
				if (sl.contains(language) || ft.fileExists(filepath)) {
					hc.getYaml().getConfig().set("config.language", language);
					language = L.buildLanguageFile(false);
					//sender.sendMessage(ChatColor.GOLD + language + " loaded.");
					sender.sendMessage(L.f(L.get("LANGUAGE_LOADED"), language));
				} else {
					sender.sendMessage(L.get("LANGUAGE_NOT_FOUND"));
				}
			} else if (args.length == 2 && args[1].equalsIgnoreCase("o")) {
				String language = args[0].toLowerCase();
				ArrayList<String> sl = L.getSupportedLanguages();
				if (sl.contains(language)) {
					hc.getYaml().getConfig().set("config.language", language);
					language = L.buildLanguageFile(true);
					//sender.sendMessage(ChatColor.GOLD + language + " loaded.");
					sender.sendMessage(L.f(L.get("LANGUAGE_LOADED"), language));
				} else {
					sender.sendMessage(L.get("LANGUAGE_NOT_FOUND"));
				}
			} else {
				sender.sendMessage(L.get("SETLANGUAGE_INVALID"));
			}
		} catch (Exception e) {
			sender.sendMessage(L.get("SETLANGUAGE_INVALID"));
		}
	}
	
	
}
