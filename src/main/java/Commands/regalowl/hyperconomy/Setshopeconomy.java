package regalowl.hyperconomy;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class Setshopeconomy {
	Setshopeconomy(String args[], CommandSender sender) {
		HyperConomy hc = HyperConomy.hc;
		LanguageFile L = hc.getLanguageFile();
		try {
			if (hc.useSQL()) {
				if (args.length == 2) {
    				String name = args[0];
    				name = name.replace("%s", " ");
    				String teststring = hc.getYaml().getShops().getString(name);
    				if (teststring == null) {
    					name = hc.fixsName(name);
    				}
    				if (name == null) {
    					sender.sendMessage(L.get("SHOP_NOT_EXIST"));
    					return;
    				}
    				String economy = args[1];
    				if (hc.getSQLFunctions().testEconomy(economy)) {
    					hc.getYaml().getShops().set(name + ".economy", economy);
    					sender.sendMessage(L.get("SHOP_ECONOMY_SET"));
    				} else {
    					sender.sendMessage(L.get("ECONOMY_DOESNT_EXIST"));
    				}
    			} else {
    				sender.sendMessage(L.get("SETSHOPECONOMY_INVALID"));
    			}
			} else {
				sender.sendMessage(L.get("ONLY_AVAILABLE_SQL"));
			}
		} catch (Exception e) {
			sender.sendMessage(L.get("SETSHOPECONOMY_INVALID"));
		}
	}
}
