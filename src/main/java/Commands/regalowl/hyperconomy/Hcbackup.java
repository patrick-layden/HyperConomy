package regalowl.hyperconomy;


import org.bukkit.command.CommandSender;

public class Hcbackup {
	LanguageFile L = HyperConomy.hc.getLanguageFile();
	Hcbackup(CommandSender sender) {
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
