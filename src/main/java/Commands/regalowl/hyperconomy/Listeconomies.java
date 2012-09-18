package regalowl.hyperconomy;

import java.util.ArrayList;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class Listeconomies {
	Listeconomies(String args[], CommandSender sender) {
		HyperConomy hc = HyperConomy.hc;
		SQLFunctions sf = hc.getSQLFunctions();
		try {
			if (hc.useSQL()) {
				if (args.length == 0) {
    				ArrayList<String> economies = sf.getEconomyList();
    				sender.sendMessage(ChatColor.AQUA + economies.toString());
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
