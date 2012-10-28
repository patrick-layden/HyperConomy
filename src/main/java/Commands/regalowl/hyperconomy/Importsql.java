package regalowl.hyperconomy;


import org.bukkit.command.CommandSender;

public class Importsql {
	
	
	Importsql(CommandSender sender, String args[]) {
		HyperConomy hc = HyperConomy.hc;
		LanguageFile L = hc.getLanguageFile();
		try {
			if (hc.useSQL()) {
				if (args.length == 1 || args.length == 0) {
					if (args.length == 1 && args[0].equalsIgnoreCase("confirm")) {
						if (hc.getYaml().getConfig().getBoolean("config.run-automatic-backups")) {
							new Backup();
						}
						RestoreSQL rs = new RestoreSQL();
						rs.restore(sender);
						sender.sendMessage(L.get("IMPORTING_TABLES"));
					} else {
						sender.sendMessage(L.get("IMPORTSQL_WARNING"));
					}
				} else {
					sender.sendMessage(L.get("IMPORTSQL_INVALID"));
				}
			} else {
				sender.sendMessage(L.get("ONLY_AVAILABLE_SQL"));
			}
			return;
		} catch (Exception e) {
			sender.sendMessage(L.get("IMPORTSQL_INVALID"));
			return;
		}
	}
}
