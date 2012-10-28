package regalowl.hyperconomy;

import org.bukkit.command.CommandSender;

public class Classvalues {
	Classvalues(CommandSender sender) {
		HyperConomy hc = HyperConomy.hc;
		LanguageFile L = hc.getLanguageFile();
		try {
			sender.sendMessage(L.get("LINE_BREAK"));
			sender.sendMessage(L.f(L.get("BOW_VALUE"), hc.getYaml().getConfig().getDouble("config.enchantment.classvalue.bow")));
			sender.sendMessage(L.f(L.get("WOOD_VALUE"), hc.getYaml().getConfig().getDouble("config.enchantment.classvalue.wood")));
			sender.sendMessage(L.f(L.get("LEATHER_VALUE"), hc.getYaml().getConfig().getDouble("config.enchantment.classvalue.leather")));
			sender.sendMessage(L.f(L.get("STONE_VALUE"), hc.getYaml().getConfig().getDouble("config.enchantment.classvalue.stone")));
			sender.sendMessage(L.f(L.get("CHAINMAIL_VALUE"), hc.getYaml().getConfig().getDouble("config.enchantment.classvalue.chainmail")));
			sender.sendMessage(L.f(L.get("IRON_VALUE"), hc.getYaml().getConfig().getDouble("config.enchantment.classvalue.iron")));
			sender.sendMessage(L.f(L.get("GOLD_VALUE"), hc.getYaml().getConfig().getDouble("config.enchantment.classvalue.gold")));
			sender.sendMessage(L.f(L.get("DIAMOND_VALUE"), hc.getYaml().getConfig().getDouble("config.enchantment.classvalue.diamond")));
			sender.sendMessage(L.get("LINE_BREAK"));
		} catch (Exception e) {
			sender.sendMessage(L.get("CLASSVALUES_INVALID"));
		}
	}
}
