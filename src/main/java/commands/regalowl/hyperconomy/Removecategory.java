package regalowl.hyperconomy;

import java.util.ArrayList;

import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;

public class Removecategory {
	private HyperConomy hc;

	Removecategory(String args[], CommandSender sender) {
		hc = HyperConomy.hc;
		EconomyManager em = hc.getEconomyManager();
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
			if (args.length == 2) {
				String shopname = args[1];
				if (em.shopExists(shopname)) {
					Shop shop = em.getShop(shopname);
					shop.removeObjects(objects);
					sender.sendMessage(L.f(L.get("REMOVED_FROM"), args[0], shopname.replace("_", " ")));
				} else {
					sender.sendMessage(L.get("SHOP_NOT_EXIST"));
				}
			} else {
				sender.sendMessage(L.get("REMOVECATEGORY_INVALID"));
			}
		} catch (Exception e) {
			sender.sendMessage(L.get("REMOVECATEGORY_INVALID"));
		}
	}
}
