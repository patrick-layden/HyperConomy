package regalowl.hyperconomy.command;

import java.io.File;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import regalowl.databukkit.file.FileTools;
import regalowl.hyperconomy.HyperConomy;
import regalowl.hyperconomy.util.LanguageFile;

public class Setlanguage {

	Setlanguage(String args[], CommandSender sender) {
		HyperConomy hc = HyperConomy.hc;
		LanguageFile L = hc.getLanguageFile();
		FileTools ft = hc.getFileTools();
		String folderpath = Bukkit.getServer().getPluginManager().getPlugin("HyperConomy").getDataFolder() + File.separator + "Languages";
		ft.makeFolder(folderpath);
		try {
			if (args.length == 1) {
				String language = args[0].toLowerCase();
				String filepath = folderpath + File.separator + language + ".hl";
				if (L.languageSupported(language) || ft.fileExists(filepath)) {
					language = L.fixLanguage(language);
					hc.gYH().gFC("config").set("language", language);
					language = L.buildLanguageFile(false);
					sender.sendMessage(L.f(L.get("LANGUAGE_LOADED"), language));
				} else {
					sender.sendMessage(L.get("LANGUAGE_NOT_FOUND"));
				}
			} else if (args.length == 2 && args[1].equalsIgnoreCase("o")) {
				String language = args[0].toLowerCase();
				if (L.languageSupported(language)) {
					language = L.fixLanguage(language);
					hc.gYH().gFC("config").set("language", language);
					language = L.buildLanguageFile(true);
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
