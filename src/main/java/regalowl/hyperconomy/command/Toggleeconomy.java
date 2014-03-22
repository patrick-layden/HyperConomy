package regalowl.hyperconomy.command;


import org.bukkit.command.CommandSender;

import regalowl.hyperconomy.HyperConomy;
import regalowl.hyperconomy.util.LanguageFile;

public class Toggleeconomy {
	Toggleeconomy(CommandSender sender) {
		HyperConomy hc = HyperConomy.hc;
		LanguageFile L = hc.getLanguageFile();
		try {
			if (hc.gYH().gFC("config").getBoolean("config.use-external-economy-plugin")) {
				hc.gYH().gFC("config").set("config.use-external-economy-plugin", false);
				sender.sendMessage(L.get("TOGGLEECONOMY_DISABLED"));
			} else {
				hc.gYH().gFC("config").set("config.use-external-economy-plugin", true);
				sender.sendMessage(L.get("TOGGLEECONOMY_ENABLED"));
			}
		} catch (Exception e) {
			sender.sendMessage(L.get("TOGGLEECONOMY_INVALID"));
			return;
		}
	}
}
