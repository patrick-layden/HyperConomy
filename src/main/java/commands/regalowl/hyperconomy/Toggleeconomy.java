package regalowl.hyperconomy;


import net.milkbowl.vault.economy.Economy;

import org.bukkit.command.CommandSender;

public class Toggleeconomy {
	Toggleeconomy(CommandSender sender) {
		HyperConomy hc = HyperConomy.hc;
		LanguageFile L = hc.getLanguageFile();
		Economy economy = hc.getEconomy();
		try {
			if (hc.s().gB("use-external-economy-plugin")) {
				hc.gYH().gFC("config").set("config.use-external-economy-plugin", false);
				hc.setUseExternalEconomy(false);
				hc.getEconomyManager().createGlobalShopAccount();
				sender.sendMessage(L.get("TOGGLEECONOMY_DISABLED"));
			} else {
				if (economy != null && economy.getName().equalsIgnoreCase("HyperConomy")) {
					//use internal economy if HyperConomy is hooked to Vault
					hc.setUseExternalEconomy(false);
					sender.sendMessage(L.get("TOGGLEECONOMY_HYPERCONOMY"));
					return;
				}
				hc.gYH().gFC("config").set("config.use-external-economy-plugin", true);
				hc.setupExternalEconomy();
				hc.getEconomyManager().createGlobalShopAccount();
				sender.sendMessage(L.get("TOGGLEECONOMY_ENABLED"));
			}
		} catch (Exception e) {
			sender.sendMessage(L.get("TOGGLEECONOMY_INVALID"));
			return;
		}
	}
}
