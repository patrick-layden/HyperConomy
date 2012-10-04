package regalowl.hyperconomy;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class Setbalance {
	Setbalance(CommandSender sender, String[] args) {
		HyperConomy hc = HyperConomy.hc;
		Account acc = hc.getAccount();
		try {
			if (args.length == 2) {
				String accountname = args[0];
				if (acc.checkAccount(accountname)) {
					Double balance = Double.parseDouble(args[1]);
					acc.setBalance(accountname, balance);
					sender.sendMessage(ChatColor.GOLD + "Balance set!");
				} else {
					sender.sendMessage(ChatColor.DARK_RED + "That account doesn't exist!");
				}
			} else {
				sender.sendMessage(ChatColor.DARK_RED + "Invalid Parameters.  Use /setbalance [account] [balance]");
			}
		} catch (Exception e) {
			sender.sendMessage(ChatColor.DARK_RED + "Invalid Parameters.  Use /setbalance [account] [balance]");
		}
	}
}
