package regalowl.hyperconomy;

import org.bukkit.command.CommandSender;

public class Exporttoyml {

	Exporttoyml(CommandSender sender, String[] args) {
		HyperConomy hc = HyperConomy.hc;
		LanguageFile L = hc.getLanguageFile();
		EconomyManager em = hc.getEconomyManager();
		try {
			if (args.length == 1 || args.length == 2) {
				String economy = args[0];
				if (em.economyExists(economy)) {
					if (args.length == 2 && args[1].equalsIgnoreCase("confirm")) {
						if (hc.gYH().gFC("config").getBoolean("config.run-automatic-backups")) {
							new Backup();
						}
						em.getEconomy(economy).exportToYml();
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
			return;
		} catch (Exception e) {
			sender.sendMessage(L.get("EXPORTTOYML_INVALID"));
			return;
		}
	}

}
