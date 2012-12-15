package regalowl.hyperconomy;

import java.util.ArrayList;

import org.bukkit.command.CommandSender;

public class Setstockmedianall {
	Setstockmedianall(CommandSender sender, String[] args, String playerecon) {
		HyperConomy hc = HyperConomy.hc;
		SQLFunctions sf = hc.getSQLFunctions();
		InfoSignHandler isign = hc.getInfoSignHandler();
		LanguageFile L = hc.getLanguageFile();
		try {
			if (args.length == 0) {
				sender.sendMessage(L.get("SETSTOCKMEDIANALL_WARNING"));
			} else if (args[0].equalsIgnoreCase("confirm")) {
				if (hc.getYaml().getConfig().getBoolean("config.run-automatic-backups")) {
					new Backup();
				}
				ArrayList<String> names = hc.getNames();
				for (int c = 0; c < names.size(); c++) {
					sf.setStock(names.get(c), playerecon, sf.getMedian(names.get(c), playerecon));
					sf.setInitiation(names.get(c), playerecon, "false");
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
