package regalowl.hyperconomy;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class Setmessage {
	Setmessage(String[] args, CommandSender sender) {
		HyperConomy hc = HyperConomy.hc;
		Shop s = hc.getShop();
		try {
			if (args.length >= 3) {
				if (args[0].equalsIgnoreCase("1")) {
					String message = args[1];
					message = message.replace("%s", " ");
					int counter = 2;
					String name = "";
					while (counter < args.length) {
						if (counter == 2) {
							name = args[2];
						} else {
							name = name + "_" + args[counter];
						}
						counter++;
					}
					String teststring = hc.getYaml().getShops().getString(name);
					if (teststring == null) {
						name = hc.fixsName(name);
					}
					int i = 0;
					while (i < s.getshopdataSize()) {
						if (name.equalsIgnoreCase(s.getshopData(i))) {
							s.setMessage1(i, message);
							hc.getYaml().getShops().set(s.getshopData(i) + ".shopmessage1", message);
							sender.sendMessage(ChatColor.GOLD + "Message 1 set!");
							return;
						}
						i++;
					}
					sender.sendMessage(ChatColor.DARK_RED + "That shop doesn't exist!");
				} else if (args[0].equalsIgnoreCase("2")) {
					String message = args[1];
					message = message.replace("%s", " ");
					int counter = 2;
					String name = "";
					while (counter < args.length) {
						if (counter == 2) {
							name = args[2];
						} else {
							name = name + "_" + args[counter];
						}
						counter++;
					}
					String teststring = hc.getYaml().getShops().getString(name);
					if (teststring == null) {
						name = hc.fixsName(name);
					}
					int i = 0;
					while (i < s.getshopdataSize()) {
						if (name.equalsIgnoreCase(s.getshopData(i))) {
							s.setMessage2(i, message);
							hc.getYaml().getShops().set(s.getshopData(i) + ".shopmessage2", message);
							sender.sendMessage(ChatColor.GOLD + "Message 2 set!");
							return;
						}
						i++;
					}
					sender.sendMessage(ChatColor.DARK_RED + "That shop doesn't exist!");
				}
			} else {
				sender.sendMessage(ChatColor.DARK_RED + "Invalid Parameters.  Use /setmessage ['1'/'2'] [message] [shop]");
			}
		} catch (Exception e) {
			sender.sendMessage(ChatColor.DARK_RED + "Invalid Parameters.  Use /setmessage ['1'/'2'] [message] [shop]");
		}
	}
}
