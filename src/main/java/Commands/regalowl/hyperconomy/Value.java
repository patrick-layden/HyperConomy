package regalowl.hyperconomy;


import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import static regalowl.hyperconomy.Messages.*;

public class Value {
	HyperConomy hc;
	Value(String args[], CommandSender sender, String playerecon) {
		hc = HyperConomy.hc;
		SQLFunctions sf = hc.getSQLFunctions();
		Calculation calc = hc.getCalculation();
		FormatString fs = new FormatString();
		Player player;
		try {
			String name = args[0];
			int amount;
			if (args.length == 2) {
				amount = Integer.parseInt(args[1]);
			} else {
				amount = 1;
			}
			String teststring = hc.testiString(name);
			if (teststring != null) {
				double val = calc.getTvalue(name, amount, playerecon);
				double salestax = 0;
				if (sender instanceof Player) {
					player = (Player) sender;
					salestax = calc.getSalesTax(player, val);
				}
				val = calc.twoDecimals(val - salestax);
				sender.sendMessage(LINE_BREAK);
				//sender.sendMessage(ChatColor.GREEN + "" + amount + ChatColor.AQUA + " " + name + ChatColor.BLUE + " can be sold for: " + ChatColor.GREEN + hc.getYaml().getConfig().getString("config.currency-symbol") + val);
				sender.sendMessage(fs.formatString(CAN_BE_SOLD_FOR, amount, val, name));
				double cost = calc.getCost(name, amount, playerecon);
				double taxpaid = calc.getPurchaseTax(name, playerecon, cost);
				cost = calc.twoDecimals(cost + taxpaid);
				//String scost = "";
				if (cost > Math.pow(10, 10)) {
					cost = -1;
				}
				double stock = 0;
				stock = sf.getStock(name, playerecon);
				//sender.sendMessage(ChatColor.GREEN + "" + amount + ChatColor.AQUA + " " + name + ChatColor.BLUE + " can be purchased for: " + ChatColor.GREEN + hc.getYaml().getConfig().getString("config.currency-symbol") + scost);
				sender.sendMessage(fs.formatString(CAN_BE_PURCHASED_FOR, amount, cost, name));
				sender.sendMessage(fs.formatString(GLOBAL_SHOP_CURRENTLY_HAS, stock, name));
				//sender.sendMessage(ChatColor.BLUE + "The global shop currently has " + ChatColor.GREEN + "" + stock + ChatColor.AQUA + " " + name + ChatColor.BLUE + " available.");
				sender.sendMessage(LINE_BREAK);
			} else {
				sender.sendMessage(INVALID_ITEM_NAME);
				return;
			}
		} catch (Exception e) {
			sender.sendMessage(VALUE_INVALID);
			return;
		}
	}
}
