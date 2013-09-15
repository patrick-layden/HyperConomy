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
				HyperObject ho = sf.getHyperObject(name, playerecon);
				if (ho instanceof CompositeObject) {continue;}
				ho.setStaticprice(ho.getStartprice());
				ho.setIsstatic("true");
			}
			setting = "true + dynamic prices copied";
		} else {
			for (int i = 0; i < names.size(); i++) {
				HyperObject ho = sf.getHyperObject(names.get(i), playerecon);
				if (ho instanceof CompositeObject) {continue;}
				ho.setIsstatic(setting);
			}
		}
		isign.updateSigns();
		sender.sendMessage(L.f(L.get("ALL_OBJECTS_SET_TO_STATIC"), setting));
	}
}
