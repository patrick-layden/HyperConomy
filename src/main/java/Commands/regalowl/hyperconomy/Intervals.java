package regalowl.hyperconomy;


import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class Intervals {
	Intervals(CommandSender sender, String[] args) {
		HyperConomy hc = HyperConomy.hc;
		InfoSignHandler isign = hc.getInfoSignHandler();
		ShopFactory s = hc.getShopFactory();
		LanguageFile L = hc.getLanguageFile();
		try {
			if (args.length == 0) {
				SQLWrite sw = hc.getSQLWrite();
				SQLRead sr = hc.getSQLRead();
				sender.sendMessage(L.get("LINE_BREAK"));
				sender.sendMessage(ChatColor.GREEN + "" + s.getshopInterval() + ChatColor.BLUE + " tick (" + ChatColor.GREEN + "" + s.getshopInterval() / 20 + ChatColor.BLUE + " second) shop update interval.");
				sender.sendMessage(ChatColor.GREEN + "" + hc.getsaveInterval() + ChatColor.BLUE + " tick (" + ChatColor.GREEN + "" + hc.getsaveInterval() / 20 + ChatColor.BLUE + " second) save interval.");
				sender.sendMessage(ChatColor.GREEN + "" + isign.getUpdateInterval() + ChatColor.BLUE + " tick (" + ChatColor.GREEN + "" + isign.getUpdateInterval() / 20 + ChatColor.BLUE + " second) sign update interval.");
				sender.sendMessage(ChatColor.GREEN + "" + isign.signsWaitingToUpdate() + ChatColor.BLUE + " signs waiting to update.");
				//TODO Make async
				//sender.sendMessage(ChatColor.GREEN + "" + sr.countTableEntries("hyperconomy_log") + ChatColor.BLUE + " log entries.");
				sender.sendMessage(ChatColor.GREEN + "" + sw.getBufferSize() + ChatColor.BLUE + " statements in the SQL write buffer.");
				sender.sendMessage(ChatColor.GREEN + "" + sw.getActiveThreads() + ChatColor.BLUE + " active SQL write connections.");
				sender.sendMessage(ChatColor.GREEN + "" + sw.getAvailableThreads() + ChatColor.BLUE + " available SQL write connections.");
				sender.sendMessage(ChatColor.GREEN + "" + sr.getActiveReadConnections() + ChatColor.BLUE + " active SQL read connections.");
				sender.sendMessage(L.get("LINE_BREAK"));
			} else {
				sender.sendMessage(ChatColor.DARK_RED + "Invalid Parameters.  Use /intervals");
			}
		} catch (Exception e) {
			sender.sendMessage(ChatColor.DARK_RED + "Invalid Parameters.  Use /intervals");
		}
	}
}
