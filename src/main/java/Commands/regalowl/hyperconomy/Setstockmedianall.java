package regalowl.hyperconomy;

import java.util.ArrayList;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class Setstockmedianall {
	Setstockmedianall(CommandSender sender, String[] args, String playerecon) {
		HyperConomy hc = HyperConomy.hc;
		SQLFunctions sf = hc.getSQLFunctions();
		InfoSign isign = hc.getInfoSign();
		try {
			if (args.length == 0) {
				sender.sendMessage(ChatColor.RED + "Are you sure you wish to do this?");
				sender.sendMessage(ChatColor.RED + "All item and enchantment stocks will be set to their median.");
				sender.sendMessage(ChatColor.RED + "All item and enchantments will have initial pricing disabled.");
				sender.sendMessage(ChatColor.RED + "Type /setstockmedianall confirm to proceed.");
			} else if (args[0].equalsIgnoreCase("confirm")) {
				if (hc.getYaml().getConfig().getBoolean("config.run-automatic-backups")) {
					new Backup();
				}
				ArrayList<String> names = hc.getNames();
				for (int c = 0; c < names.size(); c++) {
					sf.setStock(names.get(c), playerecon, sf.getMedian(names.get(c), playerecon));
					sf.setInitiation(names.get(c), playerecon, "false");
				}
				sender.sendMessage(ChatColor.GOLD + "Shop stocks of all items/enchantments have been set to their medians and initial pricing has been disabled.");
				isign.setrequestsignUpdate(true);
				isign.checksignUpdate();
			} else {
				sender.sendMessage(ChatColor.DARK_RED + "Invalid Parameters.  Use /setstockmedianall");
			}
		} catch (Exception e) {
			sender.sendMessage(ChatColor.DARK_RED + "Invalid Parameters.  Use /setstockmedianall");
		}
	}
}
