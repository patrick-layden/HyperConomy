package regalowl.hyperconomy;

import org.bukkit.command.CommandSender;
import static regalowl.hyperconomy.Messages.*;

public class Classvalues {
	Classvalues(CommandSender sender) {
		HyperConomy hc = HyperConomy.hc;
		try {
			FormatString fs = new FormatString();
			sender.sendMessage(LINE_BREAK);
			sender.sendMessage(fs.formatString(BOW_VALUE, hc.getYaml().getConfig().getDouble("config.enchantment.classvalue.bow")));
			sender.sendMessage(fs.formatString(WOOD_VALUE, hc.getYaml().getConfig().getDouble("config.enchantment.classvalue.wood")));
			sender.sendMessage(fs.formatString(LEATHER_VALUE, hc.getYaml().getConfig().getDouble("config.enchantment.classvalue.leather")));
			sender.sendMessage(fs.formatString(STONE_VALUE, hc.getYaml().getConfig().getDouble("config.enchantment.classvalue.stone")));
			sender.sendMessage(fs.formatString(CHAINMAIL_VALUE, hc.getYaml().getConfig().getDouble("config.enchantment.classvalue.chainmail")));
			sender.sendMessage(fs.formatString(IRON_VALUE, hc.getYaml().getConfig().getDouble("config.enchantment.classvalue.iron")));
			sender.sendMessage(fs.formatString(GOLD_VALUE, hc.getYaml().getConfig().getDouble("config.enchantment.classvalue.gold")));
			sender.sendMessage(fs.formatString(DIAMOND_VALUE, hc.getYaml().getConfig().getDouble("config.enchantment.classvalue.diamond")));
			sender.sendMessage(LINE_BREAK);
		} catch (Exception e) {
			sender.sendMessage(CLASSVALUES_INVALID);
		}
	}
}
