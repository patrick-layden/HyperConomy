package regalowl.hyperconomy;

import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class Listeconomies {
	Listeconomies(String args[], CommandSender sender) {
		HyperConomy hc = HyperConomy.hc;
		SQLFunctions sf = hc.getSQLFunctions();
		try {
			if (hc.useSQL()) {
				if (args.length == 0) {
    				ArrayList<String> economies = sf.getEconomies();
    				HashMap<String, String> uecons = new HashMap<String, String>();
    				for (int c = 0; c < economies.size(); c++) {
    					uecons.put(economies.get(c), "irrelevant");
    				}
    				sender.sendMessage(ChatColor.AQUA + uecons.keySet().toString());
    			} else {
    				sender.sendMessage(ChatColor.RED + "Invalid Parameters.  Use /listeconomies");
    			}
			} else {
				sender.sendMessage(ChatColor.RED + "This command is only available when SQL is enabled!");
			}
		} catch (Exception e) {
			sender.sendMessage(ChatColor.RED + "Invalid Parameters.  Use /listeconomies");
		}
	}
}
