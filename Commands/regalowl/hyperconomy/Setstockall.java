package regalowl.hyperconomy;

import java.util.ArrayList;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class Setstockall {
	Setstockall(String args[], CommandSender sender, String playerecon) {
		HyperConomy hc = HyperConomy.hc;
		ArrayList<String> names = hc.getNames();
		SQLFunctions sf = hc.getSQLFunctions();
		InfoSign isign = hc.getInfoSign();
		String name = "";
		double stock = 0;
		try {
			stock = Double.parseDouble(args[0]);
		} catch (Exception e) {
			sender.sendMessage(ChatColor.DARK_RED
					+ "Invalid parameters. Use /setstockall [stock]");
			return;
		}
		if (!(args.length == 1)) {
			sender.sendMessage(ChatColor.DARK_RED
					+ "Invalid parameters. Use /setstockall [stock]");
			return;
		}
		for (int i = 0; i < names.size(); i++) {
			name = names.get(i);
			sf.setStock(name, playerecon, stock);
		}
		isign.setrequestsignUpdate(true);
		isign.checksignUpdate();
		sender.sendMessage(ChatColor.GOLD + "All stocks set!");
	}
}
