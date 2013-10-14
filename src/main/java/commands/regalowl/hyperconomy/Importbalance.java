package regalowl.hyperconomy;

import java.util.ArrayList;

import net.milkbowl.vault.economy.Economy;

import org.bukkit.command.CommandSender;

public class Importbalance {
	Importbalance(String args[], CommandSender sender) {
		HyperConomy hc = HyperConomy.hc;
		LanguageFile L = hc.getLanguageFile();
		EconomyManager em = hc.getEconomyManager();
		Economy econ = hc.getEconomy();
		Log l = hc.getLog();
		try {
			if (hc.useExternalEconomy()) {
				
				ArrayList<String> players = new ArrayList<String>();
				for (HyperPlayer hp:em.getHyperPlayers()) {
					players.add(hp.getName());
				}
				if (args.length == 0) {
					
					for (String player:players) {
						if (econ.hasAccount(player)) {
							em.getHyperPlayer(player).setBalance(econ.getBalance(player));
							l.writeAuditLog(player, "setbalance", econ.getBalance(player), "HyperConomy");
						}
					}
					sender.sendMessage(L.get("PLAYERS_IMPORTED"));
				} else if (args.length > 0) {
					for (int i = 0; i < args.length; i++) {
						String player = args[i];
						if (econ.hasAccount(player)) {
							em.getHyperPlayer(player).setBalance(econ.getBalance(player));
						}
					}
					sender.sendMessage(L.get("PLAYERS_IMPORTED"));
				}
			} else {
				sender.sendMessage(L.get("MUST_USE_EXTERNAL_ECONOMY"));
			}
		} catch (Exception e) {
			sender.sendMessage(L.get("IMPORTBALANCES_INVALID"));
		}
	}
}
