package regalowl.hyperconomy;

import java.util.ArrayList;
import java.util.Collections;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import static regalowl.hyperconomy.Messages.*;

public class Browseshop {
	
	Browseshop(String args[], CommandSender sender, Player player, String playerecon) {
		HyperConomy hc = HyperConomy.hc;
		Shop s = hc.getShop();
		SQLFunctions sf = hc.getSQLFunctions();
		Calculation calc = hc.getCalculation();
		ArrayList<String> aargs = new ArrayList<String>();
		for (int i = 0; i < args.length; i++) {
			aargs.add(args[i]);
		}
		try {
			if (aargs.size() > 3) {
				sender.sendMessage(BROWSE_SHOP_INVALID);
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
				sender.sendMessage(BROWSE_SHOP_INVALID);
				return;
			}
			String input = "";
			if (aargs.size() == 1) {
				input = aargs.get(0);
			} else {
				sender.sendMessage(BROWSE_SHOP_INVALID);
				return;
			}

    		String nameshop = null;
    		if (player != null) {
    			s.setinShop(player);
    			if (s.inShop() != -1) {
    				nameshop = s.getShop(player);
    			}	    			
    		}
			ArrayList<String> names = hc.getNames();
			ArrayList<String> rnames = new ArrayList<String>();
			int i = 0;
			while(i < names.size()) {
				String cname = names.get(i);
				if (alphabetic) {
					if (cname.startsWith(input)) {
						String itemname = cname;
						if (nameshop == null || s.has(nameshop, itemname)) {
							rnames.add(cname);
						}
					}
				} else {
					if (cname.contains(input)) {
						String itemname = cname;
						if (nameshop == null || s.has(nameshop, itemname)) {
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
			sender.sendMessage(ChatColor.RED + PAGE + " " + ChatColor.WHITE + "(" + ChatColor.RED + "" + page + ChatColor.WHITE + "/" + ChatColor.RED + "" + maxpi + ChatColor.WHITE + ")");
			while (count < numberpage) {
				if (count > ((page * 10) - 11)) {
					if (count < rsize) {
						String iname = rnames.get(count);
						String t = "";
						String t2 = "";
						t = hc.testiString(iname);
						t2 = hc.testeString(iname);
			            Double cost = 0.0;
			            double stock = 0;
			            if (t != null) {
							cost = calc.getCost(iname, 1, playerecon);
							double taxpaid = calc.getPurchaseTax(iname, playerecon, cost);
							cost = calc.twoDecimals(cost + taxpaid);
							stock = sf.getStock(iname, playerecon);
						} else if (t2 != null) {
							cost = calc.getEnchantCost(iname, "diamond", playerecon);
							cost = cost + calc.getEnchantTax(iname, playerecon, cost);
							stock = sf.getStock(iname, playerecon);
						}
						sender.sendMessage("\u00A7b" + iname + " \u00A79[\u00A7a" + stock + " \u00A79" + AVAILABLE + ": \u00A7a" + hc.getYaml().getConfig().getString("config.currency-symbol") + cost + " \u00A79" + EACH + ".]");
					} else {
						sender.sendMessage(REACHED_END);
						break;
					}			
				}
				count++;
			}
		} catch (Exception e) {
			sender.sendMessage(BROWSE_SHOP_INVALID);
		}
	}
}
