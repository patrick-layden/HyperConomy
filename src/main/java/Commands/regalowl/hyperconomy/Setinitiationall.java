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
		if (!(args.length == 0)) {
			sender.sendMessage(ChatColor.DARK_RED
					+ "Invalid parameters. Use /setinitiationall");
			return;
		}
		for (int i = 0; i < names.size(); i++) {
			name = names.get(i);
			sf.setInitiation(name, playerecon, "true");
		}
		isign.setrequestsignUpdate(true);
		isign.checksignUpdate();
		sender.sendMessage(ChatColor.GOLD + "All objects set to initial pricing!");
	}
}
