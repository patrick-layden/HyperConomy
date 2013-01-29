package regalowl.hyperconomy;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Seteconomy {
	Seteconomy(_Command command, String args[], CommandSender sender, Player player) {
		HyperConomy hc = HyperConomy.hc;
		DataFunctions sf = hc.getDataFunctions();
		LanguageFile L = hc.getLanguageFile();
		try {
			if (args.length == 1) {
				String economy = args[0];
				if (hc.getDataFunctions().testEconomy(economy)) {
					if (player != null) {
						sf.getHyperPlayer(player).setEconomy(economy);
						sender.sendMessage(L.get("ECONOMY_SET"));
					} else {
						command.setNonPlayerEconomy(economy);
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
