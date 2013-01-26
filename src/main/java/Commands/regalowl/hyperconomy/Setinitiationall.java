package regalowl.hyperconomy;

import java.util.ArrayList;

import org.bukkit.command.CommandSender;

public class Setinitiationall {
	Setinitiationall(String args[], CommandSender sender, String playerecon) {
		HyperConomy hc = HyperConomy.hc;
		ArrayList<String> names = hc.getNames();
		DataFunctions sf = hc.getDataFunctions();
		InfoSignHandler isign = hc.getInfoSignHandler();
		LanguageFile L = hc.getLanguageFile();
		String name = "";
		if (!(args.length == 1)) {
			sender.sendMessage(L.get("SETINITIATIONALL_FALSE"));
			return;
		}
		String setting = "";
		if (args[0].equalsIgnoreCase("true")) {
			setting = "true";
		} else if (args[0].equalsIgnoreCase("false")) {
			setting = "false";
		} else {
			sender.sendMessage(L.get("SETINITIATIONALL_FALSE"));
			return;
		}
		new Backup();
		for (int i = 0; i < names.size(); i++) {
			name = names.get(i);
			sf.setInitiation(name, playerecon, setting);
		}
		isign.updateSigns();
		//sender.sendMessage(ChatColor.GOLD + "All objects set to initial pricing: " + setting + ".");
		sender.sendMessage(L.f(L.get("ALL_OBJECTS_SET_TO"), setting));
	}
}
