package regalowl.hyperconomy;

import java.util.ArrayList;

import org.bukkit.command.CommandSender;

public class Removeitem {
	Removeitem(String args[], CommandSender sender) {
		HyperConomy hc = HyperConomy.hc;
		ShopFactory s = hc.getShopFactory();
		LanguageFile L = hc.getLanguageFile();
		DataHandler dh = hc.getDataFunctions();
		try {
			if (args.length == 2) {
				String itemname = dh.fixName(args[0]);
				String shopname = s.fixShopName(args[1]);
				if (dh.objectTest(itemname) || itemname.equalsIgnoreCase("all")) {
    				if (s.shopExists(shopname)) {
    					Shop shop = s.getShop(shopname);
	    				if (shop.has(itemname) || itemname.equalsIgnoreCase("all")) {
	    					if (!itemname.equalsIgnoreCase("all")) {
	    						ArrayList<String> remove = new ArrayList<String>();
	    						remove.add(itemname);
	    						shop.removeObjects(remove);
		    					sender.sendMessage(L.f(L.get("REMOVED_FROM"), itemname, shopname.replace("_", " ")));
	    					} else {
	    						shop.removeAllObjects();
		    					sender.sendMessage(L.f(L.get("ALL_REMOVED_FROM"), shopname.replace("_", " ")));
	    					}
	    				} else {
	    					sender.sendMessage(L.get("ALREADY_BEEN_REMOVED"));
	    				}
    				} else {
    					sender.sendMessage(L.get("SHOP_NOT_EXIST"));
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
