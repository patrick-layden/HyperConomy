package regalowl.hyperconomy;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
public class Additem {
	Additem(String args[], CommandSender sender) {
		HyperConomy hc = HyperConomy.hc;
		Shop s = hc.getShop();
		LanguageFile L = hc.getLanguageFile();
		try {
			String itemname = args[0];
			if (args.length >= 2) {
				if (hc.itemTest(itemname) || hc.enchantTest(itemname) || itemname.equalsIgnoreCase("all")) {
    				int counter = 1;
    				String shopname = "";
    				while (counter < args.length) {
    					if (counter == 1) {
    						shopname = args[1];
    					} else {
    						shopname = shopname + "_" + args[counter];
    					}
    					counter++;
    				}
    				String teststring3 = hc.getYaml().getShops().getString(shopname);
    				if (teststring3 == null) {
    					shopname = hc.fixsName(shopname);
    					teststring3 = hc.getYaml().getShops().getString(shopname);
    				}
    				if (teststring3 != null) {
	    				String unavailable = hc.getYaml().getShops().getString(shopname + ".unavailable");
	    				if (!s.has(shopname, itemname) || itemname.equalsIgnoreCase("all")) {
	    					if (!itemname.equalsIgnoreCase("all")) {
		    					unavailable = unavailable.replace("," + itemname + ",", ",");
		    					if (itemname.equalsIgnoreCase(unavailable.substring(0, itemname.length()))) {
		    						unavailable = unavailable.substring(itemname.length() + 1, unavailable.length());
		    					}
		    					hc.getYaml().getShops().set(shopname + ".unavailable", unavailable);
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
