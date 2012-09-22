package regalowl.hyperconomy;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class Additem {
	Additem(String args[], CommandSender sender) {
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
	    				if (!s.has(shopname, itemname) || itemname.equalsIgnoreCase("all")) {
	    					if (!itemname.equalsIgnoreCase("all")) {
		    					unavailable = unavailable.replace("," + itemname + ",", ",");
		    					if (itemname.equalsIgnoreCase(unavailable.substring(0, itemname.length()))) {
		    						unavailable = unavailable.substring(itemname.length() + 1, unavailable.length());
		    					}
		    					hc.getYaml().getShops().set(shopname + ".unavailable", unavailable);
		    					sender.sendMessage(ChatColor.GOLD + itemname + " added to " + shopname.replace("_", " "));
	    					} else if (itemname.equalsIgnoreCase("all")) {
		    					hc.getYaml().getShops().set(shopname + ".unavailable", null);
		    					sender.sendMessage(ChatColor.GOLD + "All items have been added to " + shopname.replace("_", " "));
	    					}
	    				} else {
	    					sender.sendMessage(ChatColor.DARK_RED + "The shop already has that item.");
	    				}
    				} else {
    					sender.sendMessage(ChatColor.DARK_RED + "That shop doesn't exist!");
    				}
    			} else {
    				sender.sendMessage(ChatColor.BLUE + "Sorry, that item or enchantment is not in the database.");
    			}
			} else {
				sender.sendMessage(ChatColor.DARK_RED + "Invalid Parameters.  Use /additem [name/'all'] [shop]");
			}
		} catch (Exception e) {
			sender.sendMessage(ChatColor.DARK_RED + "Invalid Parameters.  Use /additem [name/'all'] [shop]");
		}
	}
}
