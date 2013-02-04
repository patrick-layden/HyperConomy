package regalowl.hyperconomy;

import java.util.ArrayList;

import org.bukkit.command.CommandSender;

public class Setstaticall {
	Setstaticall(String args[], CommandSender sender, String playerecon) {
		HyperConomy hc = HyperConomy.hc;
		ArrayList<String> names = hc.getDataFunctions().getNames();
		DataHandler sf = hc.getDataFunctions();
		InfoSignHandler isign = hc.getInfoSignHandler();
		LanguageFile L = hc.getLanguageFile();
		if (!(args.length == 1)) {
			sender.sendMessage(L.get("SETSTATICALL_INVALID"));
			return;
		}
		String setting = "";
		if (args[0].equalsIgnoreCase("true")) {
			setting = "true";
		} else if (args[0].equalsIgnoreCase("false")) {
			setting = "false";
		} else if (args[0].equalsIgnoreCase("copy")) {
			setting = "copy";
		} else {
			sender.sendMessage(L.get("SETSTATICALL_INVALID"));
			return;
		}
		new Backup();

		if (setting.equalsIgnoreCase("copy")) {
			for (int i = 0; i < names.size(); i++) {
				String name = names.get(i);
				sf.getHyperObject(name, playerecon).setStaticprice(sf.getHyperObject(name, playerecon).getStartprice());
				sf.getHyperObject(name, playerecon).setIsstatic("true");
			}
			setting = "true + dynamic prices copied";
		} else {
			for (int i = 0; i < names.size(); i++) {
				sf.getHyperObject(names.get(i), playerecon).setIsstatic(setting);
			}
		}
		isign.updateSigns();
		sender.sendMessage(L.f(L.get("ALL_OBJECTS_SET_TO_STATIC"), setting));
	}
}
