package regalowl.hyperconomy;

import static regalowl.hyperconomy.Messages.ECONOMY_EXPORTED;
import static regalowl.hyperconomy.Messages.ECONOMY_NOT_EXIST;
import static regalowl.hyperconomy.Messages.EXPORTTOYML_INVALID;
import static regalowl.hyperconomy.Messages.EXPORT_PROCEED;
import static regalowl.hyperconomy.Messages.ONLY_AVAILABLE_SQL;

import org.bukkit.command.CommandSender;

public class Exporttoyml {
	
	Exporttoyml(CommandSender sender, String[] args) {
		HyperConomy hc = HyperConomy.hc;
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
							sender.sendMessage(ECONOMY_EXPORTED);
						} else {
							sender.sendMessage(EXPORT_PROCEED);
						}
					} else {
						sender.sendMessage(ECONOMY_NOT_EXIST);
					}
				} else {
					sender.sendMessage(EXPORTTOYML_INVALID);
				}
			} else {
				sender.sendMessage(ONLY_AVAILABLE_SQL);
			}
			return;
		} catch (Exception e) {
			sender.sendMessage(EXPORTTOYML_INVALID);
			return;
		}
	}
	
}
