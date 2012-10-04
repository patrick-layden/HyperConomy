package regalowl.hyperconomy;

import static regalowl.hyperconomy.Messages.ALL_BACKED_UP;
import static regalowl.hyperconomy.Messages.HCBACKUP_INVALID;

import org.bukkit.command.CommandSender;

public class Hcbackup {
	Hcbackup(CommandSender sender) {
		try {
			new Backup();
			sender.sendMessage(ALL_BACKED_UP);
			return;
		} catch (Exception e) {
			sender.sendMessage(HCBACKUP_INVALID);
			return;
		}
	}
}
