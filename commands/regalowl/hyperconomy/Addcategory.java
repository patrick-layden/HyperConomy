package regalowl.hyperconomy;

import java.util.ArrayList;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;

import regalowl.databukkit.CommonFunctions;




public class Addcategory implements CommandExecutor {
	
	
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		HyperConomy hc = HyperConomy.hc;
		CommonFunctions cf = hc.gCF();
		if (hc.getHyperLock().isLocked(sender)) {
			hc.getHyperLock().sendLockMessage(sender);;
			return true;
		}
		EconomyManager em = hc.getEconomyManager();
		LanguageFile L = hc.getLanguageFile();
		try {
			FileConfiguration category = hc.gYH().gFC("categories");
			String testcategory = category.getString(args[0]);
			if (testcategory == null) {
				sender.sendMessage(L.get("CATEGORY_NOT_EXIST"));
				return true;
			}
			ArrayList<String> objects = cf.explode(testcategory, ",");
			if (args.length == 2) {
				String shopname = args[1];
				if (em.shopExists(shopname)) {
					Shop shop = em.getShop(shopname);
					shop.addObjects(objects);
					sender.sendMessage(ChatColor.GOLD + args[0] + " " + L.get("ADDED_TO") + " " + shopname.replace("_", " "));
				} else {
					sender.sendMessage(L.get("SHOP_NOT_EXIST"));
				}
			} else {
				sender.sendMessage(L.get("ADD_CATEGORY_INVALID"));
			}
		} catch (Exception e) {
			sender.sendMessage(L.get("ADD_CATEGORY_INVALID"));
		}
		return true;
	}
	
	

}
