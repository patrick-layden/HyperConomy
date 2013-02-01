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
				SQLSelect ss = new SQLSelect();
				sender.sendMessage(L.get("LINE_BREAK"));
				sender.sendMessage(ChatColor.BLUE + "Shop Check Interval: " + ChatColor.GREEN + "" + s.getshopInterval() + ChatColor.BLUE + " Ticks/" + ChatColor.GREEN + "" + s.getshopInterval() / 20 + ChatColor.BLUE + " Seconds");
				sender.sendMessage(ChatColor.BLUE + "Save Interval: " + ChatColor.GREEN + "" + hc.getsaveInterval() + ChatColor.BLUE + " Ticks/" + ChatColor.GREEN + "" + hc.getsaveInterval() / 20 + ChatColor.BLUE + " Seconds");
				sender.sendMessage(ChatColor.BLUE + "Sign Update Interval: " + ChatColor.GREEN + "" + isign.getUpdateInterval() + ChatColor.BLUE + " Ticks/" + ChatColor.GREEN + "" + isign.getUpdateInterval() / 20 + ChatColor.BLUE + " Seconds");
				//sender.sendMessage(ChatColor.BLUE + "There are " + ChatColor.GREEN + "" + isign.getUpdateInterval() + ChatColor.BLUE + " signs waiting to update.");
				sender.sendMessage(ChatColor.BLUE + "The log has " + ChatColor.GREEN + "" + ss.countTableEntries("hyperconomy_log") + ChatColor.BLUE + " entries.");
				sender.sendMessage(ChatColor.BLUE + "The SQL buffer contains " + ChatColor.GREEN + "" + sw.getBufferSize() + ChatColor.BLUE + " statements.");
				sender.sendMessage(ChatColor.BLUE + "There are currently " + ChatColor.GREEN + "" + sw.getActiveThreads() + ChatColor.BLUE + " active SQL threads.");
				sender.sendMessage(ChatColor.BLUE + "There are currently " + ChatColor.GREEN + "" + sw.getAvailableThreads() + ChatColor.BLUE + " available SQL threads.");
				sender.sendMessage(L.get("LINE_BREAK"));
			} else {
				sender.sendMessage(ChatColor.DARK_RED + "Invalid Parameters.  Use /intervals");
			}
		} catch (Exception e) {
			sender.sendMessage(ChatColor.DARK_RED + "Invalid Parameters.  Use /intervals");
		}
	}
}
