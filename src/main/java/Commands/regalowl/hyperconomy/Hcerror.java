package regalowl.hyperconomy;



import org.bukkit.command.CommandSender;

public class Hcerror {
	Hcerror(CommandSender sender) {
		HyperConomy hc = HyperConomy.hc;
		LanguageFile L = hc.getLanguageFile();
		try {
			boolean logError = hc.getYaml().getConfig().getBoolean("config.log-errors");
			if (logError) {
				hc.getYaml().getConfig().set("config.log-errors", false);
				sender.sendMessage(L.get("HCERROR_DISABLED"));
			} else {
				hc.getYaml().getConfig().set("config.log-errors", true);
				sender.sendMessage(L.get("HCERROR_ENABLED"));
			}
		} catch (Exception e) {
			sender.sendMessage(L.get("HCERROR_INVALID"));
			return;
		}
	}
}
