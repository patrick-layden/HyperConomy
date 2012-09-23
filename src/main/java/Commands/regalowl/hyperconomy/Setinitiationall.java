package regalowl.hyperconomy;

import java.util.ArrayList;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class Setinitiationall {
	Setinitiationall(String args[], CommandSender sender, String playerecon) {
		HyperConomy hc = HyperConomy.hc;
		ArrayList<String> names = hc.getNames();
		SQLFunctions sf = hc.getSQLFunctions();
		InfoSign isign = hc.getInfoSign();
		String name = "";
		if (!(args.length == 1)) {
			sender.sendMessage(ChatColor.DARK_RED + "Invalid parameters. Use /setinitiationall ['true' or 'false']");
			return;
		}
		String setting = "";
		if (args[0].equalsIgnoreCase("true")) {
			setting = "true";
		} else if (args[0].equalsIgnoreCase("false")) {
			setting = "false";
		} else {
			sender.sendMessage(ChatColor.DARK_RED + "Invalid parameters. Use /setinitiationall ['true' or 'false']");
			return;
		}
		for (int i = 0; i < names.size(); i++) {
			name = names.get(i);
			sf.setInitiation(name, playerecon, setting);
		}
		isign.setrequestsignUpdate(true);
		isign.checksignUpdate();
		sender.sendMessage(ChatColor.GOLD + "All objects set to initial pricing: " + setting + ".");
	}
}
