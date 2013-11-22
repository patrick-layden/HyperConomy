package regalowl.hyperconomy;

import java.util.ArrayList;

import org.bukkit.command.CommandSender;

public class Setstaticall {
	Setstaticall(String args[], CommandSender sender, String playerecon) {
		HyperConomy hc = HyperConomy.hc;
		HyperEconomy he = hc.getEconomyManager().getEconomy(playerecon);
		ArrayList<String> names = he.getNames();
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
		if (hc.gYH().gFC("config").getBoolean("config.run-automatic-backups")) {
			new Backup();
		}

		if (setting.equalsIgnoreCase("copy")) {
			for (int i = 0; i < names.size(); i++) {
				String name = names.get(i);
				HyperObject ho = he.getHyperObject(name);
				if (ho instanceof CompositeItem) {continue;}
				ho.setStaticprice(ho.getStartprice());
				ho.setIsstatic("true");
			}
			setting = "true + dynamic prices copied";
		} else {
			for (int i = 0; i < names.size(); i++) {
				HyperObject ho = he.getHyperObject(names.get(i));
				if (ho instanceof CompositeItem) {continue;}
				ho.setIsstatic(setting);
			}
		}
		isign.updateSigns();
		sender.sendMessage(L.f(L.get("ALL_OBJECTS_SET_TO_STATIC"), setting));
	}
}
