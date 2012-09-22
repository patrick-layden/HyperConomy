package regalowl.hyperconomy;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class Classvalues {
	Classvalues(CommandSender sender) {
		HyperConomy hc = HyperConomy.hc;
		try {
			sender.sendMessage(ChatColor.BLACK + "-----------------------------------------------------");
			sender.sendMessage(ChatColor.BLUE + "Bow Value: " + ChatColor.GREEN + "" + hc.getYaml().getConfig().getDouble("config.enchantment.classvalue.bow"));
			sender.sendMessage(ChatColor.BLUE + "Wood Value: " + ChatColor.GREEN + "" + hc.getYaml().getConfig().getDouble("config.enchantment.classvalue.wood"));
			sender.sendMessage(ChatColor.BLUE + "Leather Value: " + ChatColor.GREEN + "" + hc.getYaml().getConfig().getDouble("config.enchantment.classvalue.leather"));
			sender.sendMessage(ChatColor.BLUE + "Stone Value: " + ChatColor.GREEN + "" + hc.getYaml().getConfig().getDouble("config.enchantment.classvalue.stone"));
			sender.sendMessage(ChatColor.BLUE + "Chainmail Value: " + ChatColor.GREEN + "" + hc.getYaml().getConfig().getDouble("config.enchantment.classvalue.chainmail"));
			sender.sendMessage(ChatColor.BLUE + "Iron Value: " + ChatColor.GREEN + "" + hc.getYaml().getConfig().getDouble("config.enchantment.classvalue.iron"));
			sender.sendMessage(ChatColor.BLUE + "Gold Value: " + ChatColor.GREEN + "" + hc.getYaml().getConfig().getDouble("config.enchantment.classvalue.gold"));
			sender.sendMessage(ChatColor.BLUE + "Diamond Value: " + ChatColor.GREEN + "" + hc.getYaml().getConfig().getDouble("config.enchantment.classvalue.diamond"));
			sender.sendMessage(ChatColor.BLACK + "-----------------------------------------------------");
		} catch (Exception e) {
			sender.sendMessage(ChatColor.DARK_RED + "Invalid Parameters. Use /classvalues");
		}
	}
}
