package regalowl.hyperconomy;

import org.bukkit.command.CommandSender;

public class Deleteeconomy {
	Deleteeconomy(String args[], CommandSender sender) {
		HyperConomy hc = HyperConomy.hc;
		LanguageFile L = hc.getLanguageFile();
		HyperEconomy sf = hc.getShopFactory();
		try {
			if (args.length == 1) {
				String economy = args[0];
				if (economy.equalsIgnoreCase("default")) {
					sender.sendMessage(L.get("CANT_DELETE_DEFAULT_ECONOMY"));
					return;
				}
				if (hc.getDataFunctions().testEconomy(economy)) {
					if (hc.getYaml().getConfig().getBoolean("config.run-automatic-backups")) {
						new Backup();
					}
					for (Shop shop:sf.getShops()) {
						if (shop.getEconomy().equalsIgnoreCase(economy)) {
							shop.setEconomy("default");
						}
					}
					for (HyperPlayer hp:hc.getDataFunctions().getHyperPlayers()) {
						if (hp.getEconomy().equalsIgnoreCase(economy)) {
							hp.setEconomy("default");
						}
					}
					hc.getSQLEconomy().deleteEconomy(economy);
					sender.sendMessage(L.get("ECONOMY_DELETED"));
				} else {
					sender.sendMessage(L.get("ECONOMY_DOESNT_EXIST"));
				}
			} else {
				sender.sendMessage(L.get("DELETEECONOMY_INVALID"));
			}
		} catch (Exception e) {
			sender.sendMessage(L.get("DELETEECONOMY_INVALID"));
		}
	}
}
