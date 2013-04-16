package regalowl.hyperconomy;

import java.util.ArrayList;

import org.bukkit.command.CommandSender;

public class Setstockall {
	Setstockall(String args[], CommandSender sender, String playerecon) {
		HyperConomy hc = HyperConomy.hc;
		ArrayList<String> names = hc.getDataFunctions().getNames();
		DataHandler sf = hc.getDataFunctions();
		InfoSignHandler isign = hc.getInfoSignHandler();
		LanguageFile L = hc.getLanguageFile();
		String name = "";
		double stock = 0;
		try {
			stock = Double.parseDouble(args[0]);
		} catch (Exception e) {
			sender.sendMessage(L.get("SETSTOCKALL_INVALID"));
			return;
		}
		if (!(args.length == 1)) {
			sender.sendMessage(L.get("SETSTOCKALL_INVALID"));
			return;
		}
		for (int i = 0; i < names.size(); i++) {
			name = names.get(i);
			sf.getHyperObject(name, playerecon).setStock(stock);
		}
		isign.updateSigns();
		sender.sendMessage(L.get("ALL_STOCKS_SET"));
	}
}
