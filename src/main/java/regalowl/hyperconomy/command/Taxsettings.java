package regalowl.hyperconomy.command;
import org.bukkit.command.CommandSender;

import regalowl.databukkit.file.FileConfiguration;
import regalowl.hyperconomy.HyperConomy;
import regalowl.hyperconomy.util.LanguageFile;


public class Taxsettings {
	Taxsettings(CommandSender sender) {
		HyperConomy hc = HyperConomy.hc;
		LanguageFile L = hc.getLanguageFile();
		try {
			FileConfiguration conf = hc.getConf();
			Double purchasetaxpercent = conf.getDouble("tax.purchase");
			Double initialpurchasetaxpercent = conf.getDouble("tax.initial");
			Double statictaxpercent = conf.getDouble("tax.static");
			Double enchanttaxpercent = conf.getDouble("tax.enchant");
			Double salestaxpercent = conf.getDouble("tax.sales");
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
