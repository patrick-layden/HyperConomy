package regalowl.hyperconomy;


import java.io.File;
import java.util.ArrayList;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class Writeitems {
	Writeitems(String args[], CommandSender sender) {

		HyperConomy hc = HyperConomy.hc;
		SerializeArrayList sal = new SerializeArrayList();
		FileTools ft = new FileTools();
		String path = ft.getJarPath() + File.separator + "plugins" + File.separator + "HyperConomy";
		try {
    		if (args[0].equalsIgnoreCase("row") || args[0].equalsIgnoreCase("column")) {
    			if (args.length == 1) {
					if (args[0].equalsIgnoreCase("column")) {
						ArrayList<String> inames = hc.getInames();
						String output = sal.stringArrayToStringColumn(inames);
						ft.writeStringToFile(output, path + File.separator + "items.txt");
						sender.sendMessage(ChatColor.GOLD + "Item names written to items.txt");
					} else if (args[0].equalsIgnoreCase("row")) {
						ArrayList<String> inames = hc.getInames();
						String output = sal.stringArrayToString(inames);
						ft.writeStringToFile(output, path + File.separator + "items.txt");
						sender.sendMessage(ChatColor.GOLD + "Item names written to items.txt");
					}
				} else if (args.length == 2 && args[1].equalsIgnoreCase("e")) {
					if (args[0].equalsIgnoreCase("column")) {
						ArrayList<String> enames = hc.getEnames();
						String output = sal.stringArrayToStringColumn(enames);
						ft.writeStringToFile(output, path + File.separator + "enchants.txt");
						sender.sendMessage(ChatColor.GOLD + "Enchantment names written to enchants.txt");
					} else if (args[0].equalsIgnoreCase("row")) {
						ArrayList<String> enames = hc.getEnames();
						String output = sal.stringArrayToString(enames);
						ft.writeStringToFile(output, path + File.separator + "enchants.txt");
						sender.sendMessage(ChatColor.GOLD + "Enchantment names written to enchants.txt");
					}
				} else {
					sender.sendMessage(ChatColor.DARK_RED + "Invalid parameters. Use /writeitems ['row'/'column'] ('e')");
				}
    		} else {
    			sender.sendMessage(ChatColor.DARK_RED + "Invalid parameters. Use /writeitems ['row'/'column'] ('e')");
    		}
		} catch (Exception e) {
			sender.sendMessage(ChatColor.DARK_RED + "Invalid parameters. Use /writeitems ['row'/'column'] ('e')");
		}
	}

}
