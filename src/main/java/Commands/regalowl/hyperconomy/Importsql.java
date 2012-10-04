package regalowl.hyperconomy;

import static regalowl.hyperconomy.Messages.IMPORTING_TABLES;
import static regalowl.hyperconomy.Messages.IMPORTSQL_INVALID;
import static regalowl.hyperconomy.Messages.IMPORTSQL_WARNING;
import static regalowl.hyperconomy.Messages.ONLY_AVAILABLE_SQL;

import org.bukkit.command.CommandSender;

public class Importsql {
	
	
	Importsql(CommandSender sender, String args[]) {
		HyperConomy hc = HyperConomy.hc;
		try {
			if (hc.useSQL()) {
				if (args.length == 1 || args.length == 0) {
					if (args.length == 1 && args[0].equalsIgnoreCase("confirm")) {
						if (hc.getYaml().getConfig().getBoolean("config.run-automatic-backups")) {
							new Backup();
						}
						RestoreSQL rs = new RestoreSQL();
						rs.restore(sender);
						sender.sendMessage(IMPORTING_TABLES);
					} else {
						sender.sendMessage(IMPORTSQL_WARNING);
					}
				} else {
					sender.sendMessage(IMPORTSQL_INVALID);
				}
			} else {
				sender.sendMessage(ONLY_AVAILABLE_SQL);
			}
			return;
		} catch (Exception e) {
			sender.sendMessage(IMPORTSQL_INVALID);
			return;
		}
	}
}
