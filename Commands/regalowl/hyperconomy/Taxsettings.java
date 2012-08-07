package regalowl.hyperconomy;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;

import regalowl.hyperconomy.HyperConomy;


public class Taxsettings {
	Taxsettings(CommandSender sender) {
		HyperConomy hc = HyperConomy.hc;
		try {
			FileConfiguration conf = hc.getYaml().getConfig();
			Double purchasetaxpercent = conf.getDouble("config.purchasetaxpercent");
			Double initialpurchasetaxpercent = conf.getDouble("config.initialpurchasetaxpercent");
			Double statictaxpercent = conf.getDouble("config.statictaxpercent");
			Double enchanttaxpercent = conf.getDouble("config.enchanttaxpercent");
			Double salestaxpercent = conf.getDouble("config.sales-tax-percent");
			sender.sendMessage(ChatColor.BLACK + "-----------------------------------------------------");
			sender.sendMessage(ChatColor.BLUE + "Purchase Tax Percent: " + ChatColor.GREEN + "" + purchasetaxpercent);
			sender.sendMessage(ChatColor.BLUE + "Initial Purchase Tax Percent: " + ChatColor.GREEN + "" + initialpurchasetaxpercent);
			sender.sendMessage(ChatColor.BLUE + "Static Tax Percent: " + ChatColor.GREEN + "" + statictaxpercent);
			sender.sendMessage(ChatColor.BLUE + "Enchantment Tax Percent: " + ChatColor.GREEN + "" + enchanttaxpercent);
			sender.sendMessage(ChatColor.BLUE + "Sales Tax Percent: " + ChatColor.GREEN + "" + salestaxpercent);
			sender.sendMessage(ChatColor.BLACK + "-----------------------------------------------------");
		} catch (Exception e) {
			sender.sendMessage(ChatColor.DARK_RED + "Invalid parameters. Use /taxsettings");
		}
	}
}
