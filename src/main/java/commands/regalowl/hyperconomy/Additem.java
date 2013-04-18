package regalowl.hyperconomy;

import java.util.ArrayList;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
public class Additem {
	Additem(String args[], CommandSender sender) {
		HyperConomy hc = HyperConomy.hc;
		ShopFactory s = hc.getShopFactory();
		LanguageFile L = hc.getLanguageFile();
		try {
			if (args.length == 2) {
				String itemname = hc.getDataFunctions().fixName(args[0]);
				String shopname = s.fixShopName(args[1]);
				if (hc.getDataFunctions().objectTest(itemname) || itemname.equalsIgnoreCase("all")) {
    				if (s.shopExists(shopname)) {
    					ServerShop shop = s.getShop(shopname);
	    				if (!shop.has(itemname) || itemname.equalsIgnoreCase("all")) {
	    					if (!itemname.equalsIgnoreCase("all")) {
	    						ArrayList<String> add = new ArrayList<String>();
	    						add.add(itemname);
	    						shop.addObjects(add);
		    					sender.sendMessage(ChatColor.GOLD + itemname + " " + L.get("ADDED_TO") + " " + shopname.replace("_", " "));
	    					} else if (itemname.equalsIgnoreCase("all")) {
		    					shop.addAllObjects();
		    					sender.sendMessage(ChatColor.GOLD + L.get("ALL_ITEMS_ADDED") + " " + shopname.replace("_", " "));
	    					}
	    				} else {
	    					sender.sendMessage(L.get("SHOP_ALREADY_HAS"));
	    				}
    				} else {
    					sender.sendMessage(L.get("SHOP_NOT_EXIST"));
    				}
    			} else {
    				sender.sendMessage(L.get("OBJECT_NOT_IN_DATABASE"));
    			}
			} else {
				sender.sendMessage(L.get("ADD_ITEM_INVALID"));
			}
		} catch (Exception e) {
			sender.sendMessage(L.get("ADD_ITEM_INVALID"));
		}
	}
}
