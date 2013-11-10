package regalowl.hyperconomy;


import java.io.File;
import java.util.ArrayList;

import org.bukkit.command.CommandSender;

import regalowl.databukkit.CommonFunctions;
import regalowl.databukkit.FileTools;

public class Writeitems {
	Writeitems(String args[], CommandSender sender) {
		HyperConomy hc = HyperConomy.hc;
		CommonFunctions cf = hc.gCF();
		FileTools ft = hc.getFileTools();
		LanguageFile L = hc.getLanguageFile();
		HyperEconomy defaultEcon = hc.getEconomyManager().getEconomy("default");
		String path = ft.getJarPath() + File.separator + "plugins" + File.separator + "HyperConomy";
		try {
    		if (args[0].equalsIgnoreCase("row") || args[0].equalsIgnoreCase("column")) {
    			if (args.length == 1) {
					if (args[0].equalsIgnoreCase("column")) {
						ArrayList<String> inames = defaultEcon.getNames();
						String output = cf.implode(inames, "\n");
						ft.writeStringToFile(output, path + File.separator + "items.txt");
						sender.sendMessage(L.get("ITEM_NAMES_WRITTEN"));
					} else if (args[0].equalsIgnoreCase("row")) {
						ArrayList<String> inames = defaultEcon.getNames();
						String output = cf.implode(inames,",");
						ft.writeStringToFile(output, path + File.separator + "items.txt");
						sender.sendMessage(L.get("ITEM_NAMES_WRITTEN"));
					}
				} else {
					sender.sendMessage(L.get("WRITEITEMS_INVALID"));
				}
    		} else {
    			sender.sendMessage(L.get("WRITEITEMS_INVALID"));
    		}
		} catch (Exception e) {
			sender.sendMessage(L.get("WRITEITEMS_INVALID"));
		}
	}

}
