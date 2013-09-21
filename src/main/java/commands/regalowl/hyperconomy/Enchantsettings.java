package regalowl.hyperconomy;

import org.bukkit.command.CommandSender;

public class Enchantsettings {
	Enchantsettings(String args[], CommandSender sender, String playerecon) {
		HyperConomy hc = HyperConomy.hc;
		EconomyManager em = hc.getEconomyManager();
		LanguageFile L = hc.getLanguageFile();
		try {
			String nam = args[0];
			if (nam == null) {
				sender.sendMessage(L.get("ENCHANTMENT_NOT_IN_DATABASE"));
			} else {
				double val = 0;
				boolean stat = false;
				double statprice = 0;
				double sto = 0;
				double med = 0;
				boolean init = false;
				double starprice = 0;
				HyperObject ho = em.getEconomy(playerecon).getHyperObject(nam);
				val = ho.getValue();
				stat = Boolean.parseBoolean(ho.getIsstatic());
				statprice = ho.getStaticprice();
				sto = ho.getStock();
				med = ho.getMedian();
				init = Boolean.parseBoolean(ho.getInitiation());
				starprice = ho.getStartprice();		
				double totalstock = ((med * val)/starprice);
				int maxinitialitems = 0;			
				double roundedtotalstock = Math.rint( totalstock * 1.0d ) / 1.0d;
				maxinitialitems = (int) (roundedtotalstock - sto);
				double ceiling = ho.getCeiling();
				double floor = ho.getFloor();
				sender.sendMessage(L.get("LINE_BREAK"));
				sender.sendMessage(L.f(L.get("SETTINGS_NAME"), nam));
				sender.sendMessage(L.f(L.get("SETTINGS_VALUE"), val));
				sender.sendMessage(L.f(L.get("SETTINGS_STARTPRICE"), starprice, init));
				sender.sendMessage(L.f(L.get("SETTINGS_STATICPRICE"), statprice, stat));
				sender.sendMessage(L.f(L.get("SETTINGS_STOCK"), sto));
				sender.sendMessage(L.f(L.get("SETTINGS_MEDIAN"), med));
				sender.sendMessage(L.f(L.get("SETTINGS_CEILING"), ceiling));
				sender.sendMessage(L.f(L.get("SETTINGS_FLOOR"), floor));
				sender.sendMessage(L.f(L.get("SETTINGS_REACH_HYPERBOLIC"), maxinitialitems));
				sender.sendMessage(L.get("LINE_BREAK"));
			}
	} catch (Exception e) {
		sender.sendMessage(L.get("ENCHANTSETTINGS_INVALID"));
	}
	}
}
