package regalowl.hyperconomy.command;

import java.io.File;
import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import regalowl.databukkit.file.FileTools;
import regalowl.hyperconomy.DataManager;
import regalowl.hyperconomy.HyperConomy;
import regalowl.hyperconomy.account.HyperPlayer;
import regalowl.hyperconomy.util.LanguageFile;

public class Importbalance {
	Importbalance(String args[], CommandSender sender) {
		HyperConomy hc = HyperConomy.hc;
		LanguageFile L = hc.getLanguageFile();
		DataManager em = hc.getDataManager();
		try {
			if (hc.useExternalEconomy()) {
				if (args.length == 1) {
					FileTools ft = hc.getFileTools();

					String world = args[0];
					String playerListPath = ft.getJarPath();
					if (Bukkit.getWorld(world) != null) {
						playerListPath += File.separator + args[0] + File.separator + "players";
						for (String datName:ft.getFolderContents(playerListPath)) {
							String playerName = datName.substring(0, datName.indexOf("."));
							if (hc.getEconomy().hasAccount(playerName)) {
								sender.sendMessage(playerName);
								em.getHyperPlayer(playerName).setInternalBalance(hc.getEconomy().getBalance(playerName));
							}
						}
						sender.sendMessage(L.get("PLAYERS_IMPORTED"));
					} else {
						ArrayList<String> players = new ArrayList<String>();
						for (HyperPlayer hp:em.getHyperPlayers()) {
							players.add(hp.getName());
						}
						for (String player:players) {
							if (hc.getEconomy().hasAccount(player)) {
								sender.sendMessage(player);
								em.getHyperPlayer(player).setInternalBalance(hc.getEconomy().getBalance(player));
							}
						}
						sender.sendMessage(L.get("WORLD_NOT_FOUND"));
						return;
					}
				} else if (args.length > 1 && args[0].equalsIgnoreCase("players")) {
					for (int i = 1; i < args.length; i++) {
						String player = args[i];
						if (hc.getEconomy().hasAccount(player)) {
							em.getHyperPlayer(player).setInternalBalance(hc.getEconomy().getBalance(player));
						}
					}
					sender.sendMessage(L.get("PLAYERS_IMPORTED"));
				} else {
					sender.sendMessage(L.get("IMPORTBALANCES_INVALID"));
				}
			} else {
				sender.sendMessage(L.get("MUST_USE_EXTERNAL_ECONOMY"));
			}
		} catch (Exception e) {
			sender.sendMessage(L.get("IMPORTBALANCES_INVALID"));
		}
	}
}
