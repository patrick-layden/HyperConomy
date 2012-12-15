package regalowl.hyperconomy;

import java.util.ArrayList;

import net.milkbowl.vault.economy.Economy;

import org.bukkit.command.CommandSender;

public class Importbalance {
	Importbalance(String args[], CommandSender sender) {
		HyperConomy hc = HyperConomy.hc;
		LanguageFile L = hc.getLanguageFile();
		DataFunctions df = hc.getSQLFunctions();
		Economy econ = hc.getEconomy();
		try {
			if (hc.useExternalEconomy()) {
				ArrayList<String> players = df.getEconPlayers();
				if (args.length == 0) {
					
					for (String player:players) {
						if (econ.hasAccount(player)) {
							df.setPlayerBalance(player, econ.getBalance(player));
						}
					}
					sender.sendMessage(L.get("PLAYERS_IMPORTED"));
				} else if (args.length > 0) {
					for (int i = 0; i < args.length; i++) {
						String player = df.fixpN(args[i]);
						if (econ.hasAccount(player)) {
							if (players.contains(player)) {
								df.setPlayerBalance(player, econ.getBalance(player));
							} else {
								df.addPlayer(player);
								df.setPlayerBalance(player, econ.getBalance(player));
							}
						}
					}
				}
			} else {
				sender.sendMessage(L.get("MUST_USE_EXTERNAL_ECONOMY"));
			}
		} catch (Exception e) {
			sender.sendMessage(L.get("IMPORTBALANCES_INVALID"));
		}
	}
}
