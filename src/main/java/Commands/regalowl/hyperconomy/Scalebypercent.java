package regalowl.hyperconomy;

import java.util.ArrayList;

import org.bukkit.command.CommandSender;

public class Scalebypercent {
	Scalebypercent(CommandSender sender, String[] args, String playerecon) {
		HyperConomy hc = HyperConomy.hc;
		LanguageFile L = hc.getLanguageFile();
		try {
			DataFunctions sf = hc.getDataFunctions();
			Calculation calc = hc.getCalculation();
			InfoSignHandler isign = hc.getInfoSignHandler();
			ArrayList<String> names = new ArrayList<String>();;
			if (args.length == 2 || args.length == 3) {
				if (args.length == 3) {
					if (args[2].contains("item")) {
						names = hc.getInames();
					} else if (args[2].contains("enchantment")) {
						names = hc.getEnames();
					}
				} else {
					names = hc.getNames();
				}
				String type = args[0];
				Double percent = Double.parseDouble(args[1]);
				percent = percent / 100;
				if (percent >= 0) {
					if (type.equalsIgnoreCase("value") || type.equalsIgnoreCase("staticprice") || type.equalsIgnoreCase("stock") || type.equalsIgnoreCase("median") || type.equalsIgnoreCase("startprice")) {
						if (hc.getYaml().getConfig().getBoolean("config.run-automatic-backups")) {
							new Backup();
						}
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
						sender.sendMessage(L.get("ADJUSTMENT_SUCCESSFUL"));
						isign.updateSigns();
					} else {
						sender.sendMessage(L.get("SCALEBYPERCENT_TYPES"));
					}
				} else {
					sender.sendMessage(L.get("PERCENT_GREATER_THAN_0"));
				}
			} else {
				sender.sendMessage(L.get("SCALEBYPERCENT_INVALID"));
			}
		} catch (Exception e) {
			sender.sendMessage(L.get("SCALEBYPERCENT_INVALID"));
		}
	}
}
