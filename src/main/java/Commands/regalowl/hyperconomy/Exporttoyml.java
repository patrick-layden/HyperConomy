package regalowl.hyperconomy;

import org.bukkit.command.CommandSender;

public class Exporttoyml {
	
	Exporttoyml(CommandSender sender, String[] args) {
		HyperConomy hc = HyperConomy.hc;
		LanguageFile L = hc.getLanguageFile();
		try {
			if (hc.useSQL()) {
				if (args.length == 1 || args.length == 2) {
					String economy = args[0];
					if (hc.getSQLFunctions().testEconomy(economy)) {
						if (args.length == 2 && args[1].equalsIgnoreCase("confirm")) {
							if (hc.getYaml().getConfig().getBoolean("config.run-automatic-backups")) {
								new Backup();
							}
							SQLEconomy sqe = hc.getSQLEconomy();
							sqe.exportToYml(economy);
							sender.sendMessage(L.get("ECONOMY_EXPORTED"));
						} else {
							sender.sendMessage(L.get("EXPORT_PROCEED"));
						}
					} else {
						sender.sendMessage(L.get("ECONOMY_NOT_EXIST"));
					}
				} else {
					sender.sendMessage(L.get("EXPORTTOYML_INVALID"));
				}
			} else {
				sender.sendMessage(L.get("ONLY_AVAILABLE_SQL"));
			}
			return;
		} catch (Exception e) {
			sender.sendMessage(L.get("EXPORTTOYML_INVALID"));
			return;
		}
	}
	
}
