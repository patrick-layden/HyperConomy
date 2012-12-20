package regalowl.hyperconomy;

import java.util.ArrayList;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
public class Additem {
	Additem(String args[], CommandSender sender) {
		HyperConomy hc = HyperConomy.hc;
		Shop s = hc.getShop();
		SerializeArrayList sal = new SerializeArrayList();
		LanguageFile L = hc.getLanguageFile();
		try {
			String itemname = hc.fixName(args[0]);
			if (args.length >= 2) {
				if (hc.objectTest(itemname) || itemname.equalsIgnoreCase("all")) {
					String shopname = args[1].replace("_", " ");
    				String teststring3 = hc.getYaml().getShops().getString(shopname);
    				if (teststring3 == null) {
    					shopname = hc.fixsName(shopname);
    					teststring3 = hc.getYaml().getShops().getString(shopname);
    				}
    				if (teststring3 != null) {
	    				ArrayList<String> unavailable = sal.stringToArray(hc.getYaml().getShops().getString(shopname + ".unavailable"));
	    				if (!s.has(shopname, itemname) || itemname.equalsIgnoreCase("all")) {
	    					if (!itemname.equalsIgnoreCase("all")) {
	    						unavailable.remove(itemname);
		    					hc.getYaml().getShops().set(shopname + ".unavailable", sal.stringArrayToString(unavailable));
		    					sender.sendMessage(ChatColor.GOLD + itemname + " " + L.get("ADDED_TO") + " " + shopname.replace("_", " "));
	    					} else if (itemname.equalsIgnoreCase("all")) {
		    					hc.getYaml().getShops().set(shopname + ".unavailable", null);
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
