package regalowl.hyperconomy;


import org.bukkit.command.CommandSender;

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
