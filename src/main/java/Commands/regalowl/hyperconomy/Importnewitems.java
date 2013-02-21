package regalowl.hyperconomy;

import java.util.ArrayList;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class Importnewitems {
	Importnewitems(String args[], CommandSender sender) {
		HyperConomy hc = HyperConomy.hc;
		LanguageFile L = hc.getLanguageFile();
		try {
			if (args.length == 1) {
				String economy = args[0];
				if (hc.getDataFunctions().testEconomy(economy)) {
					if (hc.getYaml().getConfig().getBoolean("config.run-automatic-backups")) {
						new Backup();
					}
					SQLEconomy se = hc.getSQLEconomy();
					if (!economy.equalsIgnoreCase("default")) {
						se.loadItems("default");
					}
					ArrayList<String> added = se.loadItems(economy);
					sender.sendMessage(ChatColor.GOLD + added.toString() + " " + L.get("LOADED_INTO_ECONOMY"));
				} else {
					sender.sendMessage(L.get("ECONOMY_NOT_EXIST"));
				}
			} else {
				sender.sendMessage(L.get("LOADITEMS_INVALID"));
			}
		} catch (Exception e) {
			sender.sendMessage(L.get("LOADITEMS_INVALID"));
		}
	}
}
