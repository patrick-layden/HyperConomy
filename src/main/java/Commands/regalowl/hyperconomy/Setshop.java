package regalowl.hyperconomy;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class Setshop {
	Setshop(String[] args, Player player) {
		HyperConomy hc = HyperConomy.hc;
		Shop s = hc.getShop();
		if (args.length >= 2) {
			if (args[0].equalsIgnoreCase("p1")) {
				int counter = 1;
				String name = "";
				while (counter < args.length) {
					if (counter == 1) {
						name = args[1];
					} else {
						name = name + "_" + args[counter];
					}
					counter++;
				}
				String teststring = hc.getYaml().getShops().getString(name);
				if (teststring == null) {
					name = hc.fixsName(name);
				}
				name = name.replace(".", "").replace(":", "");
				s.setsShop(name, player);
				s.setShop1();
				player.sendMessage(ChatColor.GOLD + "Shop location p1 has been set!");
			} else if (args[0].equalsIgnoreCase("p2")) {
				int counter = 1;
				String name = "";
				while (counter < args.length) {
					if (counter == 1) {
						name = args[1];
					} else {
						name = name + "_" + args[counter];
					}
					counter++;
				}
				name = name.replace(".", "").replace(":", "");
				String teststring = hc.getYaml().getShops().getString(name);
				if (teststring == null) {
					name = hc.fixsName(name);
				}
				s.setsShop(name, player);
				s.setShop2();
				player.sendMessage(ChatColor.GOLD + "Shop location p2 has been set!");
			}
		} else {
			player.sendMessage(ChatColor.DARK_RED + "Invalid Parameters. Use /setshop ['p1'/'p2'] [name]");
		}
	}
}
