package regalowl.hyperconomy;

import java.util.ArrayList;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class Removeitem {
	Removeitem(String args[], CommandSender sender) {
		HyperConomy hc = HyperConomy.hc;
		Shop s = hc.getShop();
		try {
			String itemname = args[0];
			String teststring2 = hc.testeString(itemname);
			String teststring = hc.testiString(itemname);
			if (args.length >= 2) {
				if (teststring != null || teststring2 != null || itemname.equalsIgnoreCase("all")) {
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
		    					sender.sendMessage(ChatColor.GOLD + itemname + " removed from " + shopname.replace("_", " "));
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
		    					sender.sendMessage(ChatColor.GOLD + "All items and enchantments have been removed from " + shopname.replace("_", " "));
	    					}
	    				} else {
	    					sender.sendMessage(ChatColor.DARK_RED + "That item has already been removed from the shop.");
	    				}
    				} else {
    					sender.sendMessage(ChatColor.DARK_RED + "That shop doesn't exist!");
    				}
    			} else {
    				sender.sendMessage(ChatColor.BLUE + "Sorry, that item or enchantment is not in the database.");
    			}
			} else {
				sender.sendMessage(ChatColor.DARK_RED + "Invalid Parameters.  Use /removeitem [name/'all'] [shop] ");
			}
		} catch (Exception e) {
			sender.sendMessage(ChatColor.DARK_RED + "Invalid Parameters.  Use /removeitem [name/'all'] [shop]");
		}
	}
}
