package regalowl.hyperconomy.command;

import java.io.File;
import java.util.ArrayList;
import java.util.UUID;


import regalowl.databukkit.file.FileTools;
import regalowl.hyperconomy.account.HyperPlayer;

public class Importbalance extends BaseCommand implements HyperCommand {

	public Importbalance() {
		super(false);
	}

	@Override
	public CommandData onCommand(CommandData data) {
		if (!validate(data)) return data;
		if (!hc.mc.useExternalEconomy()) {
			data.addResponse(L.get("MUST_USE_EXTERNAL_ECONOMY"));
			return data;
		}
		if (args.length == 0) {
			data.addResponse(L.get("IMPORTBALANCES_INVALID"));
			return data;
		}
		String world = args[0];
		if (Bukkit.getWorld(world) == null) {
			data.addResponse(L.get("WORLD_NOT_FOUND"));
			return data;
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
			if (hc.mc.getEconomy().hasAccount(name)) {
				HyperPlayer hp = hc.getHyperPlayerManager().getHyperPlayer(name);
				hp.setInternalBalance(hc.mc.getEconomy().getBalance(name));
				hp.setUUID(puid.toString());
			}
			importedPlayers.add(name);
		}
		//data.addResponse("[" + hc.getCommonFunctions().implode(importedPlayers, ",") + "]");
		data.addResponse(L.get("PLAYERS_IMPORTED"));
		hc.getHyperPlayerManager().purgeDeadAccounts();
		return data;
	}
}
