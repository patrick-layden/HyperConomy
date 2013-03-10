package regalowl.hyperconomy;

import java.util.ArrayList;
import java.util.Collections;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Browseshop {
	
	Browseshop(String args[], CommandSender sender, Player player, String playerecon) {
		HyperConomy hc = HyperConomy.hc;
		ShopFactory s = hc.getShopFactory();
		DataHandler sf = hc.getDataFunctions();
		Calculation calc = hc.getCalculation();
		LanguageFile L = hc.getLanguageFile();
		ArrayList<String> aargs = new ArrayList<String>();
		for (int i = 0; i < args.length; i++) {
			aargs.add(args[i]);
		}
		//try {
			boolean requireShop = hc.getConfig().getBoolean("config.limit-info-commands-to-shops");
    		if (player != null) {
    			if ((requireShop && s.inAnyShop(player)) && !player.hasPermission("hyperconomy.admin")) {
    				sender.sendMessage(L.get("REQUIRE_SHOP_FOR_INFO"));
    				return;
    			}			
    		}
			if (aargs.size() > 3) {
				sender.sendMessage(L.get("BROWSE_SHOP_INVALID"));
				return;
			}
			boolean alphabetic = false;
			if (aargs.contains("-a") && aargs.size() >= 2) {
				alphabetic = true;
				aargs.remove("-a");
			}
			int page;
			if (aargs.size() <= 2) {
				try {
					page = Integer.parseInt(aargs.get(0));
					aargs.remove(0);
				} catch (Exception e) {
					try {
						page = Integer.parseInt(aargs.get(1));
						aargs.remove(1);
					} catch (Exception f) {
						page = 1;
					}
				}
			} else {
				sender.sendMessage(L.get("BROWSE_SHOP_INVALID"));
				return;
			}
			String input = "";
			if (aargs.size() == 1) {
				input = aargs.get(0);
			} else {
				sender.sendMessage(L.get("BROWSE_SHOP_INVALID"));
				return;
			}
    		String nameshop = null;
    		if (player != null) {
    			if (s.inAnyShop(player)) {
    				nameshop = null;
    			} else {
    				nameshop = s.getShop(player).getName();
    			}		
    		}
			ArrayList<String> names = sf.getNames();
			ArrayList<String> rnames = new ArrayList<String>();
			int i = 0;
			while(i < names.size()) {
				String cname = names.get(i);
				if (alphabetic) {
					if (cname.startsWith(input)) {
						String itemname = cname;
						if (nameshop == null || s.getShop(nameshop).has(itemname)) {
							rnames.add(cname);
						}
					}
				} else {
					if (cname.contains(input)) {
						String itemname = cname;
						if (nameshop == null || s.getShop(nameshop).has(itemname)) {
							rnames.add(cname);
						}
					}
				}
				i++;
			}
			Collections.sort(rnames, String.CASE_INSENSITIVE_ORDER);
			int numberpage = page * 10;
			int count = 0;
			int rsize = rnames.size();
			double maxpages = rsize/10;
			maxpages = Math.ceil(maxpages);
			int maxpi = (int)maxpages + 1;
			sender.sendMessage(ChatColor.RED + L.get("PAGE") + " " + ChatColor.WHITE + "(" + ChatColor.RED + "" + page + ChatColor.WHITE + "/" + ChatColor.RED + "" + maxpi + ChatColor.WHITE + ")");
			while (count < numberpage) {
				if (count > ((page * 10) - 11)) {
					if (count < rsize) {
						String iname = sf.fixName(rnames.get(count));
			            Double cost = 0.0;
			            double stock = 0;
			            if (sf.itemTest(iname)) {
							cost = calc.getCost(iname, 1, playerecon);
							double taxpaid = calc.getPurchaseTax(iname, playerecon, cost);
							cost = calc.twoDecimals(cost + taxpaid);
							stock = sf.getHyperObject(iname, playerecon).getStock();
						} else if (sf.enchantTest(iname)) {
							cost = calc.getEnchantCost(iname, EnchantmentClass.DIAMOND, playerecon);
							cost = cost + calc.getEnchantTax(iname, playerecon, cost);
							stock = sf.getHyperObject(iname, playerecon).getStock();
						}
						sender.sendMessage("\u00A7b" + iname + " \u00A79[\u00A7a" + stock + " \u00A79" + L.get("AVAILABLE") + ": \u00A7a" + L.get("CURRENCY") + cost + " \u00A79" + L.get("EACH") + ".]");
					} else {
						sender.sendMessage(L.get("REACHED_END"));
						break;
					}			
				}
				count++;
			}
		//} catch (Exception e) {
		//	sender.sendMessage(L.get("BROWSE_SHOP_INVALID"));
		//}
	}
}
