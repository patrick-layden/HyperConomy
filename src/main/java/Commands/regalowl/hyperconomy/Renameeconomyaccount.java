package regalowl.hyperconomy;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class Renameeconomyaccount {
	Renameeconomyaccount(CommandSender sender, String[] args) {
		HyperConomy hc = HyperConomy.hc;
		Account acc = hc.getAccount();
		try {
			if (args.length == 1) {
				String newaccount = args[0];
				String oldaccount = hc.getYaml().getConfig().getString("config.global-shop-account");
				acc.createAccount(newaccount);
				acc.setBalance(newaccount, acc.getBalance(oldaccount));
				acc.setBalance(oldaccount, 0);
				hc.getYaml().getConfig().set("config.global-shop-account", newaccount);
				sender.sendMessage(ChatColor.GOLD + "The global shop account has been successfully renamed!");
			} else {
				sender.sendMessage(ChatColor.DARK_RED + "Invalid Parameters.  Use /renameeconomyaccount [new name]");
			}
		} catch (Exception e) {
			sender.sendMessage(ChatColor.DARK_RED + "Invalid Parameters.  Use /renameeconomyaccount [new name]");
		}
	}
}
