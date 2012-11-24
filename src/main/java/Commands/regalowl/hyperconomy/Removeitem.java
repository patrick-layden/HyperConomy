package regalowl.hyperconomy;

import java.util.ArrayList;

import org.bukkit.command.CommandSender;

public class Removeitem {
	Removeitem(String args[], CommandSender sender) {
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
	    				if (s.has(shopname, itemname) || itemname.equalsIgnoreCase("all")) {
	    					if (!itemname.equalsIgnoreCase("all")) {
	    						if (unavailable == null) {
	    							unavailable = "";
	    						}
		    					unavailable = unavailable + itemname + ",";
		    					hc.getYaml().getShops().set(shopname + ".unavailable", unavailable);
		    					//sender.sendMessage(ChatColor.GOLD + itemname + " removed from " + shopname.replace("_", " "));
		    					sender.sendMessage(L.f(L.get("REMOVED_FROM"), itemname, shopname.replace("_", " ")));
	    					} else if (itemname.equalsIgnoreCase("all")) {
	    						String itemlist = "";
	    						String enchantlist = "";
	    	        	        ArrayList<String> inames = hc.getInames();
	    	        	        for (int c = 0; c < inames.size(); c++) {
		    	    				String elst2 = inames.get(c);			
		    	    				itemlist = itemlist + elst2 + ",";	    	    						
	    	        	        }
	    	        	        ArrayList<String> enames = hc.getEnames();
	    	        	        for (int c = 0; c < enames.size(); c++) {
		    	    				String elst2 = enames.get(c);			
		    	    				enchantlist = enchantlist + elst2 + ",";    	    						
	    	        	        }
		    					hc.getYaml().getShops().set(shopname + ".unavailable", itemlist + enchantlist);
		    					//sender.sendMessage(ChatColor.GOLD + "All items and enchantments have been removed from " + shopname.replace("_", " "));
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
