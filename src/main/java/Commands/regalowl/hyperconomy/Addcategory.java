package regalowl.hyperconomy;

import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;


public class Addcategory {
	private HyperConomy hc;
	private CommandSender sender;
	private HashMap<CommandSender, String> messages = new HashMap<CommandSender, String>();
	private boolean messageActive = false;

	Addcategory(String args[], CommandSender se) {
		sender = se;
		hc = HyperConomy.hc;
		ShopFactory s = hc.getShopFactory();
		LanguageFile L = hc.getLanguageFile();
		SerializeArrayList sal = new SerializeArrayList();
		try {
			FileConfiguration category = hc.getYaml().getCategories();
			String testcategory = category.getString(args[0]);
			if (testcategory == null) {
				sender.sendMessage(L.get("CATEGORY_NOT_EXIST"));
				return;
			}
			ArrayList<String> objects = sal.stringToArray(testcategory);
			if (args.length >= 2) {
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
					shopname = s.fixShopName(shopname);
					teststring3 = hc.getYaml().getShops().getString(shopname);
				}
				if (teststring3 != null) {
					for (int i = 0; i < objects.size(); i++) {
						String itemname = objects.get(i);
						if (hc.getDataFunctions().itemTest(itemname) || hc.getDataFunctions().enchantTest(itemname)) {
							String unavailable = hc.getYaml().getShops().getString(shopname + ".unavailable");
							if (!s.getShop(shopname).has(itemname)) {
								unavailable = unavailable.replace("," + itemname + ",", ",");
								if (itemname.equalsIgnoreCase(unavailable.substring(0, itemname.length()))) {
									unavailable = unavailable.substring(itemname.length() + 1, unavailable.length());
								}
								hc.getYaml().getShops().set(shopname + ".unavailable", unavailable);
								sendMessage(ChatColor.GOLD + args[0] + " " + L.get("ADDED_TO") + " " + shopname.replace("_", " "));
							}
						}
					}
				} else {
					sender.sendMessage(L.get("SHOP_NOT_EXIST"));
				}
			} else {
				sender.sendMessage(L.get("ADD_CATEGORY_INVALID"));
			}
		} catch (Exception e) {
			sender.sendMessage(L.get("ADD_CATEGORY_INVALID"));
		}
	}

	/**
	 * 
	 * Limits messages sent to player to 1 per second.
	 */
	private void sendMessage(String message) {
		messages.put(sender, message);
		if (!messageActive) {
			messageActive = true;
			hc.getServer().getScheduler().scheduleSyncDelayedTask(hc, new Runnable() {
				public void run() {
					sender.sendMessage(messages.get(sender));
					messages.remove(sender);
					messageActive = false;
				}
			}, 20L);
		}
	}
}
