package regalowl.hyperconomy;

import java.util.ArrayList;

import org.bukkit.command.CommandSender;

public class Removeitem {
	Removeitem(String args[], CommandSender sender) {
		HyperConomy hc = HyperConomy.hc;
		LanguageFile L = hc.getLanguageFile();
		EconomyManager em = hc.getEconomyManager();
		try {
			if (args.length == 2) {
				String itemname = args[0];
				String shopname = args[1];
				if (!em.shopExists(shopname)) {
					sender.sendMessage(L.get("SHOP_NOT_EXIST"));
					return;
				}
				Shop shop = em.getShop(shopname);
				HyperEconomy he = shop.getHyperEconomy();
				if (he.objectTest(itemname) || itemname.equalsIgnoreCase("all")) {
	    				if (!shop.isBanned(itemname) || itemname.equalsIgnoreCase("all")) {
	    					if (!itemname.equalsIgnoreCase("all")) {
	    						ArrayList<HyperObject> remove = new ArrayList<HyperObject>();
	    						HyperObject ho = shop.getHyperEconomy().getHyperObject(itemname);
	    						remove.add(ho);
	    						shop.banObjects(remove);
		    					sender.sendMessage(L.f(L.get("REMOVED_FROM"), itemname, shopname.replace("_", " ")));
	    					} else {
	    						shop.banAllObjects();
		    					sender.sendMessage(L.f(L.get("ALL_REMOVED_FROM"), shopname.replace("_", " ")));
	    					}
	    				} else {
	    					sender.sendMessage(L.get("ALREADY_BEEN_REMOVED"));
	    				}
    			} else {
    				sender.sendMessage(L.get("OBJECT_NOT_IN_DATABASE"));
    			}
			} else {
				sender.sendMessage(L.get("REMOVEITEM_INVALID"));
			}
		} catch (Exception e) {
			sender.sendMessage(L.get("REMOVEITEM_INVALID"));
		}
	}
}
