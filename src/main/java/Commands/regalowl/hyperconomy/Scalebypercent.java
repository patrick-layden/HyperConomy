package regalowl.hyperconomy;

import java.util.ArrayList;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class Scalebypercent {
	Scalebypercent(CommandSender sender, String[] args, String playerecon) {
		try {
			HyperConomy hc = HyperConomy.hc;
			SQLFunctions sf = hc.getSQLFunctions();
			Calculation calc = hc.getCalculation();
			InfoSign isign = hc.getInfoSign();
			if (args.length == 2) {
				String type = args[0];
				Double percent = Double.parseDouble(args[1]);
				percent = percent / 100;
				if (percent >= 0) {
					if (type.equalsIgnoreCase("value") || type.equalsIgnoreCase("staticprice") || type.equalsIgnoreCase("stock") || type.equalsIgnoreCase("median") || type.equalsIgnoreCase("startprice")) {
						if (hc.getYaml().getConfig().getBoolean("config.run-automatic-backups")) {
							new Backup();
						}
						ArrayList<String> names = hc.getNames();
						for (int c = 0; c < names.size(); c++) {
							String cname = names.get(c);
							if (type.equalsIgnoreCase("value")) {
								sf.setValue(cname, playerecon, calc.twoDecimals(sf.getValue(cname, playerecon) * percent));
							} else if (type.equalsIgnoreCase("staticprice")) {
								sf.setStaticPrice(cname, playerecon, calc.twoDecimals(sf.getStaticPrice(cname, playerecon) * percent));
							} else if (type.equalsIgnoreCase("stock")) {
								sf.setStock(cname, playerecon, Math.floor(sf.getStock(cname, playerecon) * percent + .5));
							} else if (type.equalsIgnoreCase("median")) {
								sf.setMedian(cname, playerecon, calc.twoDecimals(sf.getMedian(cname, playerecon) * percent));
							} else if (type.equalsIgnoreCase("startprice")) {
								sf.setStartPrice(cname, playerecon, calc.twoDecimals(sf.getStartPrice(cname, playerecon) * percent));
							}
						}
						sender.sendMessage(ChatColor.GOLD + "Adjustment successful!");
						// Updates all information signs.
						isign.setrequestsignUpdate(true);
						isign.checksignUpdate();
					} else {
						sender.sendMessage(ChatColor.DARK_RED + "The setting must be either value, staticprice, stock, median, or startprice!");
					}
				} else {
					sender.sendMessage(ChatColor.DARK_RED + "Percent must be greater than 0!");
				}
			} else {
				sender.sendMessage(ChatColor.DARK_RED + "Invalid Parameters.  Use /scalebypercent [setting] [percent]");
			}
		} catch (Exception e) {
			sender.sendMessage(ChatColor.DARK_RED + "Invalid Parameters.  Use /scalebypercent [setting] [percent]");
		}
	}
}
