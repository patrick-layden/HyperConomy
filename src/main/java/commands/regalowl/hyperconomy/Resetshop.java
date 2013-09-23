package regalowl.hyperconomy;

import java.util.ArrayList;

import org.bukkit.command.CommandSender;

public class Resetshop {
	
	Resetshop(CommandSender sender, String[] args, String playerecon) {
		HyperConomy hc = HyperConomy.hc;
		HyperEconomy he = hc.getEconomyManager().getEconomy(playerecon);
		InfoSignHandler isign = hc.getInfoSignHandler();
		LanguageFile L = hc.getLanguageFile();
		try {
			if (args.length == 0) {
				sender.sendMessage(L.get("RESETSHOP_CONFIRM"));
			} else if (args[0].equalsIgnoreCase("confirm")) {
				if (hc.gYH().gFC("config").getBoolean("config.run-automatic-backups")) {
					new Backup();
				}
				ArrayList<String> names = he.getNames();
				for (int c = 0; c < names.size(); c++) {
					String cname = names.get(c);
					HyperObject ho = he.getHyperObject(cname);
					ho.setStock(0);
					ho.setIsstatic("false");
					ho.setInitiation("true");
				}
				sender.sendMessage(L.get("RESETSHOP_SUCCESS"));
				isign.updateSigns();
			} else {
				sender.sendMessage(L.get("RESETSHOP_INVALID"));
			}
		} catch (Exception e) {
			sender.sendMessage(L.get("RESETSHOP_INVALID"));
		}
	}
}
