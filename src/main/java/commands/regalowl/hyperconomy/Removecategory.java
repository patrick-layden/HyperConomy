package regalowl.hyperconomy;

import java.util.ArrayList;

import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;

import regalowl.databukkit.CommonFunctions;

public class Removecategory {
	private HyperConomy hc;

	Removecategory(String args[], CommandSender sender) {
		hc = HyperConomy.hc;
		EconomyManager em = hc.getEconomyManager();
		LanguageFile L = hc.getLanguageFile();
		CommonFunctions cf = hc.gCF();
		try {
			FileConfiguration category = hc.gYH().gFC("categories");
			String testcategory = category.getString(args[0]);
			if (testcategory == null) {
				sender.sendMessage(L.get("CATEGORY_NOT_EXIST"));
				return;
			}
			ArrayList<String> objects = cf.explode(testcategory,",");
			if (args.length == 2) {
				String shopname = args[1];
				if (em.shopExists(shopname)) {
					Shop shop = em.getShop(shopname);
					HyperEconomy he = shop.getHyperEconomy();
					ArrayList<HyperObject> remove = new ArrayList<HyperObject>();
					for (String name:objects) {
						HyperObject ho = he.getHyperObject(name);
						if (ho != null) {
							remove.add(ho);
						}
					}
					shop.removeObjects(remove);
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
