package regalowl.hyperconomy.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import regalowl.hyperconomy.HyperConomy;

import regalowl.hyperconomy.util.HyperLock;
import regalowl.hyperconomy.util.LanguageFile;

public class Lockshop implements CommandExecutor {

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		HyperConomy hc = HyperConomy.hc;
		HyperLock hl = hc.getHyperLock();
		LanguageFile L = hc.getLanguageFile();
		try {
			if (hl.fullLock()) {
				return true;
			}
			if (args.length == 0) {
				if (hl.playerLock()) {
					hl.setPlayerLock(false);
					sender.sendMessage(L.get("SHOP_UNLOCKED"));
					return true;
				} else if (!hl.playerLock()) {
					hl.setPlayerLock(true);
					sender.sendMessage(L.get("SHOP_LOCKED"));
					return true;
				} else {
					sender.sendMessage(L.get("FIX_YML_FILE"));
					return true;
				}
			} else {
				sender.sendMessage(L.get("LOCKSHOP_INVALID"));
				return true;
			}
		} catch (Exception e) {
			sender.sendMessage(L.get("LOCKSHOP_INVALID"));
			return true;
		}
	}
}
