package regalowl.hyperconomy;

import java.util.ArrayList;

import org.bukkit.command.CommandSender;

public class Removeitem {
	Removeitem(String args[], CommandSender sender) {
		HyperConomy hc = HyperConomy.hc;
		ShopFactory s = hc.getShopFactory();
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
	    				if (s.getShop(shopname).has(itemname) || itemname.equalsIgnoreCase("all")) {
	    					if (!itemname.equalsIgnoreCase("all")) {
	    						unavailable.add(itemname);
		    					hc.getYaml().getShops().set(shopname + ".unavailable", sal.stringArrayToString(unavailable));
		    					sender.sendMessage(L.f(L.get("REMOVED_FROM"), itemname, shopname.replace("_", " ")));
	    					} else if (itemname.equalsIgnoreCase("all")) {
	    	        	        ArrayList<String> names = hc.getNames();
	    	        	        unavailable.clear();
	    	        	        for (int c = 0; c < names.size(); c++) {
	    	        	        	unavailable.add(names.get(c));    	    						
	    	        	        }
		    					hc.getYaml().getShops().set(shopname + ".unavailable", sal.stringArrayToString(unavailable));
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
