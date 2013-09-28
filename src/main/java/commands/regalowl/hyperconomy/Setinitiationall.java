package regalowl.hyperconomy;

import java.util.ArrayList;

import org.bukkit.command.CommandSender;

public class Setinitiationall {
	Setinitiationall(String args[], CommandSender sender, String playerecon) {
		HyperConomy hc = HyperConomy.hc;
		HyperEconomy he = hc.getEconomyManager().getEconomy(playerecon);
		ArrayList<String> names = he.getNames();
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
			HyperObject ho = he.getHyperObject(name);
			if (ho instanceof CompositeObject) {continue;}
			ho.setInitiation(setting);
		}
		isign.updateSigns();
		sender.sendMessage(L.f(L.get("ALL_OBJECTS_SET_TO"), setting));
	}
}
