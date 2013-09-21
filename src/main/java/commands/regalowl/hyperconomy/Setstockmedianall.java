package regalowl.hyperconomy;

import java.util.ArrayList;

import org.bukkit.command.CommandSender;

public class Setstockmedianall {
	Setstockmedianall(CommandSender sender, String[] args, String playerecon) {
		HyperConomy hc = HyperConomy.hc;
		HyperEconomy he = hc.getEconomyManager().getEconomy(playerecon);
		InfoSignHandler isign = hc.getInfoSignHandler();
		LanguageFile L = hc.getLanguageFile();
		try {
			if (args.length == 0) {
				sender.sendMessage(L.get("SETSTOCKMEDIANALL_WARNING"));
			} else if (args[0].equalsIgnoreCase("confirm")) {
				if (hc.getYaml().getConfig().getBoolean("config.run-automatic-backups")) {
					new Backup();
				}
				ArrayList<String> names = he.getNames();
				for (int c = 0; c < names.size(); c++) {
					he.getHyperObject(names.get(c)).setStock(he.getHyperObject(names.get(c)).getMedian());
					he.getHyperObject(names.get(c)).setInitiation("false");
				}
				sender.sendMessage(L.get("SETSTOCKMEDIANALL_SUCCESS"));
				isign.updateSigns();
			} else {
				sender.sendMessage(L.get("SETSTOCKMEDIANALL_INVALID"));
			}
		} catch (Exception e) {
			sender.sendMessage(L.get("SETSTOCKMEDIANALL_INVALID"));
		}
	}
}
