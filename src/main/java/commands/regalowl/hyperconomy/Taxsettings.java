package regalowl.hyperconomy;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;

import regalowl.hyperconomy.HyperConomy;


public class Taxsettings {
	Taxsettings(CommandSender sender) {
		HyperConomy hc = HyperConomy.hc;
		LanguageFile L = hc.getLanguageFile();
		try {
			FileConfiguration conf = hc.gYH().gFC("config");
			Double purchasetaxpercent = conf.getDouble("config.purchasetaxpercent");
			Double initialpurchasetaxpercent = conf.getDouble("config.initialpurchasetaxpercent");
			Double statictaxpercent = conf.getDouble("config.statictaxpercent");
			Double enchanttaxpercent = conf.getDouble("config.enchanttaxpercent");
			Double salestaxpercent = conf.getDouble("config.sales-tax-percent");
			sender.sendMessage(L.get("LINE_BREAK"));
			sender.sendMessage(L.f(L.get("PURCHASE_TAX_PERCENT"), purchasetaxpercent));
			sender.sendMessage(L.f(L.get("INITIAL_TAX_PERCENT"), initialpurchasetaxpercent));
			sender.sendMessage(L.f(L.get("STATIC_TAX_PERCENT"), statictaxpercent));
			sender.sendMessage(L.f(L.get("ENCHANTMENT_TAX_PERCENT"), enchanttaxpercent));
			sender.sendMessage(L.f(L.get("SALES_TAX_PERCENT"), salestaxpercent));
			sender.sendMessage(L.get("LINE_BREAK"));
		} catch (Exception e) {
			sender.sendMessage(L.get("TAXSETTINGS_INVALID"));
		}
	}
}
