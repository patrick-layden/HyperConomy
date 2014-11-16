package regalowl.hyperconomy.command;

import java.io.File;
import java.util.ArrayList;
import java.util.UUID;



import regalowl.simpledatalib.file.FileTools;
import regalowl.hyperconomy.HyperConomy;
import regalowl.hyperconomy.account.HyperPlayer;

public class Importbalance extends BaseCommand implements HyperCommand {

	public Importbalance(HyperConomy hc) {
		super(hc, false);
	}

	@Override
	public CommandData onCommand(CommandData data) {
		if (!validate(data)) return data;
		if (!hc.getMC().useExternalEconomy()) {
			data.addResponse(L.get("MUST_USE_EXTERNAL_ECONOMY"));
			return data;
		}
		if (args.length == 0) {
			data.addResponse(L.get("IMPORTBALANCES_INVALID"));
			return data;
		}
		String world = args[0];
		if (!hc.getMC().worldExists(world)) {
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
			HyperPlayer hp = null;
			try {
				puid = UUID.fromString(uuidName.substring(0, uuidName.indexOf(".")));
				hp = hc.getMC().getPlayer(puid);
			} catch (Exception e) {
				continue;
			}
			if (hp == null ||hp.getName() == null || hp.getName() == "") {
				continue;
			}
			if (hc.getMC().getEconomy().hasAccount(hp.getName())) {
				hp.setInternalBalance(hc.getMC().getEconomy().getBalance(hp.getName()));
				hp.setUUID(puid.toString());
			}
			importedPlayers.add(hp.getName());
		}
		//data.addResponse("[" + hc.getCommonFunctions().implode(importedPlayers, ",") + "]");
		data.addResponse(L.get("PLAYERS_IMPORTED"));
		hc.getHyperPlayerManager().purgeDeadAccounts();
		return data;
	}
}
