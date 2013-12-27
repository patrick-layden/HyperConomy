package regalowl.hyperconomy;

import java.util.ArrayList;


import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
public class Additem implements CommandExecutor {
	
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		HyperConomy hc = HyperConomy.hc;
		EconomyManager em = hc.getEconomyManager();
		LanguageFile L = hc.getLanguageFile();
		try {
			if (args.length == 2) {
				String itemname = em.getEconomy("default").fixName(args[0]);
				String shopname = args[1];
				if (em.getEconomy("default").objectTest(itemname) || itemname.equalsIgnoreCase("all")) {
    				if (em.shopExists(shopname)) {
    					Shop shop = em.getShop(shopname);
	    				if (shop.isBanned(itemname) || itemname.equalsIgnoreCase("all")) {
	    					if (!itemname.equalsIgnoreCase("all")) {
	    						ArrayList<HyperObject> add = new ArrayList<HyperObject>();
	    						HyperObject ho = shop.getHyperEconomy().getHyperObject(itemname);
	    						add.add(ho);
	    						shop.unBanObjects(add);
		    					sender.sendMessage(ChatColor.GOLD + itemname + " " + L.get("ADDED_TO") + " " + shopname.replace("_", " "));
	    					} else if (itemname.equalsIgnoreCase("all")) {
		    					shop.unBanAllObjects();
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
		return true;
	}
}
