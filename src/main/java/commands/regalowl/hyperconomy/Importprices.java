package regalowl.hyperconomy;

import java.util.Iterator;

import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;

public class Importprices {

	Importprices(String args[], CommandSender sender) {
		HyperConomy hc = HyperConomy.hc;
		LanguageFile L = hc.getLanguageFile();
		EconomyManager em = hc.getEconomyManager();

		try {
			if (args.length == 1) {
				String economy = args[0];
				if (em.economyExists(economy)) {
					if (hc.gYH().gFC("config").getBoolean("config.run-automatic-backups")) {
						new Backup();
					}
					FileConfiguration objects = hc.gYH().gFC("objects");
					Iterator<String> it = objects.getKeys(false).iterator();
					while (it.hasNext()) {
						String name = it.next().toString();
						double value = objects.getDouble(name + ".value");
						double staticprice = objects.getDouble(name + ".price.staticprice");
						double startprice = objects.getDouble(name + ".initiation.startprice");
						HyperObject ho = em.getEconomy(economy).getHyperObject(name);
						ho.setValue(value);
						ho.setStartprice(startprice);
						ho.setStaticprice(staticprice);
					}
					sender.sendMessage(L.get("PRICES_IMPORTED"));
				} else {
					sender.sendMessage(L.get("ECONOMY_NOT_EXIST"));
					return;
				}
			} else {
				sender.sendMessage(L.get("IMPORTPRICES_INVALID"));
			}
		} catch (Exception e) {
			sender.sendMessage(L.get("IMPORTPRICES_INVALID"));
		}
	}
}
