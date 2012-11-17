package regalowl.hyperconomy;

import java.util.ArrayList;
import java.util.SortedMap;
import java.util.TreeMap;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Topitems {
	Topitems(String args[], Player player, CommandSender sender, String playerecon) {
		HyperConomy hc = HyperConomy.hc;
		LanguageFile L = hc.getLanguageFile();
		Shop s = hc.getShop();
		SQLFunctions sf = hc.getSQLFunctions();
		try {
			if (args.length > 1) {
				sender.sendMessage(L.get("TOPITEMS_INVALID"));
				return;
			}
			// Gets the shop name if the player is in a shop.
			String nameshop = "";
			if (player != null) {
				s.setinShop(player);
				if (s.inShop() != -1) {
					nameshop = s.getShop(player);
				}
			}
			int page;
			if (args.length == 0) {
				page = 1;
			} else {
				page = Integer.parseInt(args[0]);
			}
			SortedMap<Double, String> itemstocks = new TreeMap<Double, String>();
			ArrayList<String> inames = hc.getInames();
			for (int c = 0; c < inames.size(); c++) {
				String elst = inames.get(c);
				boolean unavailable = false;
				if (nameshop != "") {
					if (!s.has(nameshop, elst)) {
						unavailable = true;
					}
				}
				if (!unavailable) {
					double samount = sf.getStock(elst, playerecon);
					if (samount > 0) {
						while (itemstocks.containsKey(samount)) {
							samount = samount + .0000001;
						}
						itemstocks.put(samount * 100, elst);
					}
				}
			}
			int numberpage = page * 10;
			int count = 0;
			int le = itemstocks.size();
			double maxpages = le / 10;
			maxpages = Math.ceil(maxpages);
			int maxpi = (int) maxpages + 1;
			sender.sendMessage(L.f(L.get("PAGE_NUMBER"), page, maxpi));
			//sender.sendMessage(ChatColor.RED + "Page " + ChatColor.WHITE + "(" + ChatColor.RED + "" + page + ChatColor.WHITE + "/" + ChatColor.RED + "" + maxpi + ChatColor.WHITE + ")");
			try {
				while (count < numberpage) {
					double lk = itemstocks.lastKey();
					if (count > ((page * 10) - 11)) {
						sender.sendMessage(ChatColor.WHITE + itemstocks.get(lk) + ChatColor.WHITE + ": " + ChatColor.AQUA + "" + Math.floor(lk)/100);
					}
					itemstocks.remove(lk);
					count++;
				}
			} catch (Exception e) {
				sender.sendMessage(L.get("YOU_HAVE_REACHED_THE_END"));
			}
		} catch (Exception e) {
			sender.sendMessage(L.get("TOPITEMS_INVALID"));
		}
	}
}
