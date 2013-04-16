package regalowl.hyperconomy;


import java.io.File;
import java.util.ArrayList;
import org.bukkit.command.CommandSender;

public class Writeitems {
	Writeitems(String args[], CommandSender sender) {

		HyperConomy hc = HyperConomy.hc;
		SerializeArrayList sal = new SerializeArrayList();
		FileTools ft = new FileTools();
		LanguageFile L = hc.getLanguageFile();
		DataHandler dh = hc.getDataFunctions();
		String path = ft.getJarPath() + File.separator + "plugins" + File.separator + "HyperConomy";
		try {
    		if (args[0].equalsIgnoreCase("row") || args[0].equalsIgnoreCase("column")) {
    			if (args.length == 1) {
					if (args[0].equalsIgnoreCase("column")) {
						ArrayList<String> inames = dh.getItemNames();
						String output = sal.stringArrayToStringNL(inames);
						ft.writeStringToFile(output, path + File.separator + "items.txt");
						sender.sendMessage(L.get("ITEM_NAMES_WRITTEN"));
					} else if (args[0].equalsIgnoreCase("row")) {
						ArrayList<String> inames = dh.getItemNames();
						String output = sal.stringArrayToString(inames);
						ft.writeStringToFile(output, path + File.separator + "items.txt");
						sender.sendMessage(L.get("ITEM_NAMES_WRITTEN"));
					}
				} else if (args.length == 2 && args[1].equalsIgnoreCase("e")) {
					if (args[0].equalsIgnoreCase("column")) {
						ArrayList<String> enames = dh.getEnchantNames();
						String output = sal.stringArrayToStringNL(enames);
						ft.writeStringToFile(output, path + File.separator + "enchants.txt");
						sender.sendMessage(L.get("ENCHANT_NAMES_WRITTEN"));
					} else if (args[0].equalsIgnoreCase("row")) {
						ArrayList<String> enames = dh.getEnchantNames();
						String output = sal.stringArrayToString(enames);
						ft.writeStringToFile(output, path + File.separator + "enchants.txt");
						sender.sendMessage(L.get("ENCHANT_NAMES_WRITTEN"));
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
