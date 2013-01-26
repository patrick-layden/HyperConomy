package regalowl.hyperconomy;

import java.util.ArrayList;

import org.bukkit.command.CommandSender;

public class Resetshop {
	
	Resetshop(CommandSender sender, String[] args, String playerecon) {
		HyperConomy hc = HyperConomy.hc;
		DataFunctions sf = hc.getDataFunctions();
		InfoSignHandler isign = hc.getInfoSignHandler();
		LanguageFile L = hc.getLanguageFile();
		try {
			if (args.length == 0) {
				sender.sendMessage(L.get("RESETSHOP_CONFIRM"));
			} else if (args[0].equalsIgnoreCase("confirm")) {
				if (hc.getYaml().getConfig().getBoolean("config.run-automatic-backups")) {
					new Backup();
				}
				ArrayList<String> names = hc.getNames();
				for (int c = 0; c < names.size(); c++) {
					String cname = names.get(c);
					sf.setStock(cname, playerecon, 0);
					sf.setStatic(cname, playerecon, "false");
					sf.setInitiation(cname, playerecon, "true");
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
