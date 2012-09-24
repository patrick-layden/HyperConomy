package regalowl.hyperconomy;

import java.util.ArrayList;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class Setstaticall {
	Setstaticall(String args[], CommandSender sender, String playerecon) {
		HyperConomy hc = HyperConomy.hc;
		ArrayList<String> names = hc.getNames();
		SQLFunctions sf = hc.getSQLFunctions();
		InfoSign isign = hc.getInfoSign();
		String name = "";
		if (!(args.length == 1)) {
			sender.sendMessage(ChatColor.DARK_RED + "Invalid parameters. Use /setstaticall ['true' or 'false']");
			return;
		}
		String setting = "";
		if (args[0].equalsIgnoreCase("true")) {
			setting = "true";
		} else if (args[0].equalsIgnoreCase("false")) {
			setting = "false";
		} else {
			sender.sendMessage(ChatColor.DARK_RED + "Invalid parameters. Use /setstaticall ['true' or 'false']");
			return;
		}
		new Backup();
		for (int i = 0; i < names.size(); i++) {
			name = names.get(i);
			sf.setStatic(name, playerecon, setting);
		}
		isign.setrequestsignUpdate(true);
		isign.checksignUpdate();
		sender.sendMessage(ChatColor.GOLD + "All objects set to static pricing: " + setting + ".");
	}
}
