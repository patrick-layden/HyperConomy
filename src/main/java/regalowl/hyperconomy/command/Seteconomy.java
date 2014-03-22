package regalowl.hyperconomy.command;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import regalowl.hyperconomy.DataManager;
import regalowl.hyperconomy.HyperConomy;
import regalowl.hyperconomy.util.LanguageFile;

public class Seteconomy {
	Seteconomy(_Command command, String args[], CommandSender sender, Player player) {
		HyperConomy hc = HyperConomy.hc;
		DataManager em = hc.getDataManager();
		LanguageFile L = hc.getLanguageFile();
		try {
			if (args.length == 1) {
				String economy = args[0];
				if (em.economyExists(economy)) {
					if (player != null) {
						em.getHyperPlayer(player.getName()).setEconomy(economy);
						sender.sendMessage(L.get("ECONOMY_SET"));
					} else {
						command.setNonPlayerEconomy(economy);
						hc.getConsoleSettings().setConsoleEconomy(economy);
						sender.sendMessage(L.get("ECONOMY_SET"));
					}
				} else {
					sender.sendMessage(L.get("ECONOMY_NOT_EXIST"));
				}

			} else {
				sender.sendMessage(L.get("SETECONOMY_INVALID"));
			}
		} catch (Exception e) {
			sender.sendMessage(L.get("SETECONOMY_INVALID"));
		}
	}
}
