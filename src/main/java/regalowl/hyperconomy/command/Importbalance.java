package regalowl.hyperconomy.command;

import java.io.File;
import java.util.ArrayList;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;

import regalowl.databukkit.file.FileTools;
import regalowl.hyperconomy.HyperConomy;
import regalowl.hyperconomy.account.HyperPlayer;
import regalowl.hyperconomy.util.LanguageFile;

public class Importbalance {
	@SuppressWarnings("deprecation")
	Importbalance(String args[], CommandSender sender) {
		HyperConomy hc = HyperConomy.hc;
		LanguageFile L = hc.getLanguageFile();
		if (!hc.getMC().useExternalEconomy()) {
			sender.sendMessage(L.get("MUST_USE_EXTERNAL_ECONOMY"));
			return;
		}
		if (args.length == 0) {
			sender.sendMessage(L.get("IMPORTBALANCES_INVALID"));
			return;
		}
		String world = args[0];
		if (Bukkit.getWorld(world) == null) {
			sender.sendMessage(L.get("WORLD_NOT_FOUND"));
			return;
		}
		FileTools ft = hc.getFileTools();
		String playerListPath = ft.getJarPath();
		playerListPath = playerListPath.substring(0, playerListPath.lastIndexOf("plugins"));
		playerListPath += File.separator + world + File.separator + "playerdata";
		ArrayList<String> importedPlayers = new ArrayList<String>();
		for (String uuidName : ft.getFolderContents(playerListPath)) {
			UUID puid = null;
			OfflinePlayer op = null;
			try {
				puid = UUID.fromString(uuidName.substring(0, uuidName.indexOf(".")));
				op = Bukkit.getOfflinePlayer(puid);
			} catch (Exception e) {
				continue;
			}
			String name = op.getName();
			if (name == null || name == "") {
				continue;
			}
			if (hc.getMC().getEconomy().hasAccount(name)) {
				HyperPlayer hp = hc.getHyperPlayerManager().getHyperPlayer(name);
				hp.setInternalBalance(hc.getMC().getEconomy().getBalance(name));
				hp.setUUID(puid.toString());
			}
			importedPlayers.add(name);
		}
		//sender.sendMessage("[" + hc.getCommonFunctions().implode(importedPlayers, ",") + "]");
		sender.sendMessage(L.get("PLAYERS_IMPORTED"));
		hc.getHyperPlayerManager().purgeDeadAccounts();
	}
}
