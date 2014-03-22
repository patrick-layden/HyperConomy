package regalowl.hyperconomy.command;


import org.bukkit.command.CommandSender;

import regalowl.hyperconomy.HyperConomy;
import regalowl.hyperconomy.util.Backup;
import regalowl.hyperconomy.util.LanguageFile;

public class Hcbackup {
	Hcbackup(CommandSender sender) {
		LanguageFile L = HyperConomy.hc.getLanguageFile();
		try {
			new Backup();
			sender.sendMessage(L.get("ALL_BACKED_UP"));
			return;
		} catch (Exception e) {
			sender.sendMessage(L.get("HCBACKUP_INVALID"));
			return;
		}
	}
}
