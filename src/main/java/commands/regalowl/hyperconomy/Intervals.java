package regalowl.hyperconomy;


import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class Intervals {
	Intervals(CommandSender sender, String[] args) {
		HyperConomy hc = HyperConomy.hc;
		InfoSignHandler isign = hc.getInfoSignHandler();
		EconomyManager em = hc.getEconomyManager();
		LanguageFile L = hc.getLanguageFile();
		try {
			if (args.length == 0) {
				SQLWrite sw = hc.getSQLWrite();
				sender.sendMessage(L.get("LINE_BREAK"));
				sender.sendMessage(ChatColor.GREEN + "" + em.getEconomy("default").getShopCheckInterval() + ChatColor.BLUE + " tick (" + ChatColor.GREEN + "" + em.getEconomy("default").getShopCheckInterval() / 20 + ChatColor.BLUE + " second) shop update interval.");
				sender.sendMessage(ChatColor.GREEN + "" + hc.s().getsaveInterval() + ChatColor.BLUE + " tick (" + ChatColor.GREEN + "" + hc.s().getsaveInterval() / 20 + ChatColor.BLUE + " second) save interval.");
				sender.sendMessage(ChatColor.GREEN + "" + isign.getUpdateInterval() + ChatColor.BLUE + " tick (" + ChatColor.GREEN + "" + isign.getUpdateInterval() / 20 + ChatColor.BLUE + " second) sign update interval.");
				sender.sendMessage(ChatColor.GREEN + "" + isign.signsWaitingToUpdate() + ChatColor.BLUE + " signs waiting to update.");
				sender.sendMessage(ChatColor.GREEN + "" + sw.getBufferSize() + ChatColor.BLUE + " statements in the SQL write buffer.");
				sender.sendMessage(L.get("LINE_BREAK"));
			} else {
				sender.sendMessage(ChatColor.DARK_RED + "Invalid Parameters.  Use /intervals");
			}
		} catch (Exception e) {
			sender.sendMessage(ChatColor.DARK_RED + "Invalid Parameters.  Use /intervals");
		}
	}
}
