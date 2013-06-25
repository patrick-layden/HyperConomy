package regalowl.hyperconomy;


import net.milkbowl.vault.economy.Economy;

import org.bukkit.command.CommandSender;

public class Toggleeconomy {
	Toggleeconomy(CommandSender sender) {
		HyperConomy hc = HyperConomy.hc;
		LanguageFile L = hc.getLanguageFile();
		Economy economy = hc.getEconomy();
		try {
			if (economy != null && economy.getName().equalsIgnoreCase("HyperConomy")) {
				hc.s().sB("use-external-economy-plugin", false);
				sender.sendMessage(L.get("TOGGLEECONOMY_HYPERCONOMY"));
				return;
			}
			if (hc.s().gB("use-external-economy-plugin")) {
				hc.getYaml().getConfig().set("config.use-external-economy-plugin", false);
				hc.s().sB("use-external-economy-plugin", false);
				hc.getAccount().checkshopAccount();
				sender.sendMessage(L.get("TOGGLEECONOMY_DISABLED"));
			} else {
				hc.getYaml().getConfig().set("config.use-external-economy-plugin", true);
				hc.s().sB("use-external-economy-plugin", true);
				hc.getAccount().checkshopAccount();
				sender.sendMessage(L.get("TOGGLEECONOMY_ENABLED"));
			}
		} catch (Exception e) {
			sender.sendMessage(L.get("TOGGLEECONOMY_INVALID"));
			return;
		}
	}
}
