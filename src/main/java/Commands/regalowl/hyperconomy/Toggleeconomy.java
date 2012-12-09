package regalowl.hyperconomy;



import org.bukkit.command.CommandSender;

public class Toggleeconomy {
	Toggleeconomy(CommandSender sender) {
		HyperConomy hc = HyperConomy.hc;
		LanguageFile L = hc.getLanguageFile();
		try {
			if (hc.getEconomy().getName().equalsIgnoreCase("HyperConomy")) {
				hc.getYaml().getConfig().set("config.use-external-economy-plugin", false);
				hc.setUseExternalEconomy(false);
				sender.sendMessage(L.get("TOGGLEECONOMY_HYPERCONOMY"));
				return;
			}
			if (hc.useExternalEconomy()) {
				hc.getYaml().getConfig().set("config.use-external-economy-plugin", false);
				hc.setUseExternalEconomy(false);
				sender.sendMessage(L.get("TOGGLEECONOMY_DISABLED"));
			} else {
				hc.getYaml().getConfig().set("config.use-external-economy-plugin", true);
				hc.setUseExternalEconomy(true);
				sender.sendMessage(L.get("TOGGLEECONOMY_ENABLED"));
			}
		} catch (Exception e) {
			sender.sendMessage(L.get("TOGGLEECONOMY_INVALID"));
			return;
		}
	}
}
