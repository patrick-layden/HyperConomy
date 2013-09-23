package regalowl.hyperconomy;

import org.bukkit.command.CommandSender;

public class Setclassvalue {

	Setclassvalue(String args[], CommandSender sender) {
		HyperConomy hc = HyperConomy.hc;
		InfoSignHandler isign = hc.getInfoSignHandler();
		LanguageFile L = hc.getLanguageFile();
		try {
			if (args.length != 2) {
				sender.sendMessage(L.get("SETCLASSVALUE_INVALID"));
			} else {
				String classtype = args[0];
				if (hc.gYH().gFC("config").get("config.enchantment.classvalue." + classtype) != null) {
					double value = Double.parseDouble(args[1]);
					hc.gYH().gFC("config").set("config.enchantment.classvalue." + classtype, value);
					sender.sendMessage(L.f(L.get("CLASSVALUE_SET"), classtype));
					isign.updateSigns();
				} else {
					sender.sendMessage(L.get("INVALID_ITEM_CLASS"));
				}
			}
	} catch (Exception e) {
		sender.sendMessage(L.get("SETCLASSVALUE_INVALID"));
	}
	}
}
