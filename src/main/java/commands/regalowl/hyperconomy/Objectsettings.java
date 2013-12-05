package regalowl.hyperconomy;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Objectsettings {
	Objectsettings(String args[], CommandSender sender, Player player, String playerecon) {
		HyperConomy hc = HyperConomy.hc;
		EconomyManager em = hc.getEconomyManager();
		LanguageFile L = hc.getLanguageFile();
		try {
			if (args.length == 0 && player != null) {
				HyperPlayer hp = em.getHyperPlayer(player.getName());
				HyperEconomy he = hp.getHyperEconomy();
				HyperObject hob = he.getHyperObject(player.getItemInHand());
				if (hob == null) {
					sender.sendMessage(L.get("OBJECT_NOT_IN_DATABASE"));
				} else {
					String nam = hob.getName();
					double val = 0;
					boolean stat = false;
					double statprice = 0;
					double sto = 0;
					double med = 0;
					boolean init = false;
					double starprice = -0;
					HyperObject ho = he.getHyperObject(nam);
					val = ho.getValue();
					stat = Boolean.parseBoolean(ho.getIsstatic());
					statprice = ho.getStaticprice();
					sto = ho.getStock();
					double tsto = ho.getTotalStock();
					med = ho.getMedian();
					init = Boolean.parseBoolean(ho.getInitiation());
					starprice = ho.getStartprice();
					double totalstock = ((med * val)/starprice);
					int maxinitialitems = 0;		
					double roundedtotalstock = Math.rint( totalstock * 1.0d ) / 1.0d;
					maxinitialitems = (int) (roundedtotalstock - sto);
					double ceiling = ho.getCeiling();
					double floor = ho.getFloor();
					String objectType = "";
					if (ho instanceof ComponentItem) {
						objectType = "component";
					} else if (ho instanceof CompositeItem) {
						objectType = "composite";
					} else {
						objectType = "other";
					}
					sender.sendMessage(L.get("LINE_BREAK"));
					sender.sendMessage(L.f(L.get("SETTINGS_NAME"), nam));
					sender.sendMessage(L.f(L.get("SETTINGS_DISPLAY"), ho.getDisplayName()));
					sender.sendMessage(L.f(L.get("SETTINGS_ALIAS"), ho.getAliasesString()));
					sender.sendMessage(L.f(L.get("SETTINGS_VALUE"), val));
					sender.sendMessage(L.f(L.get("SETTINGS_STARTPRICE"), starprice, init));
					sender.sendMessage(L.f(L.get("SETTINGS_STATICPRICE"), statprice, stat));
					sender.sendMessage(L.f(L.get("SETTINGS_STOCK"), sto));
					sender.sendMessage(L.f(L.get("SETTINGS_TOTAL_STOCK"), tsto));
					sender.sendMessage(L.f(L.get("SETTINGS_MEDIAN"), med));
					sender.sendMessage(L.f(L.get("SETTINGS_CEILING"), ceiling));
					sender.sendMessage(L.f(L.get("SETTINGS_FLOOR"), floor));
					sender.sendMessage(L.f(L.get("SETTINGS_REACH_HYPERBOLIC"), maxinitialitems));
					sender.sendMessage(L.f(L.get("SETTINGS_TYPE"), objectType));
    				sender.sendMessage(L.get("LINE_BREAK"));
				}
			} else if (args.length == 1) {
				HyperEconomy he = em.getEconomy(playerecon);
				String nam = he.fixName(args[0]);
				if (he.objectTest(nam)) {
					double val = 0;
					boolean stat = false;
					double statprice = 0;
					double sto = 0;
					double med = 0;
					boolean init = false;
					double starprice = 0;
					HyperObject ho = he.getHyperObject(nam);
					val = ho.getValue();
					stat = Boolean.parseBoolean(ho.getIsstatic());
					statprice = ho.getStaticprice();
					sto = ho.getStock();
					double tsto = ho.getTotalStock();
					med = ho.getMedian();
					init = Boolean.parseBoolean(ho.getInitiation());
					starprice = ho.getStartprice();			
					double totalstock = ((med * val)/starprice);
					int maxinitialitems = 0;
					double roundedtotalstock = Math.rint( totalstock * 1.0d ) / 1.0d;
					maxinitialitems = (int) (roundedtotalstock - sto);
					double ceiling = ho.getCeiling();
					double floor = ho.getFloor();
					String objectType = "";
					if (ho instanceof ComponentItem) {
						objectType = "component";
					} else if (ho instanceof CompositeItem) {
						objectType = "composite";
					} else {
						objectType = "other";
					}
					sender.sendMessage(L.get("LINE_BREAK"));
					sender.sendMessage(L.f(L.get("SETTINGS_NAME"), nam));
					sender.sendMessage(L.f(L.get("SETTINGS_DISPLAY"), ho.getDisplayName()));
					sender.sendMessage(L.f(L.get("SETTINGS_ALIAS"), ho.getAliasesString()));
					sender.sendMessage(L.f(L.get("SETTINGS_VALUE"), val));
					sender.sendMessage(L.f(L.get("SETTINGS_STARTPRICE"), starprice, init));
					sender.sendMessage(L.f(L.get("SETTINGS_STATICPRICE"), statprice, stat));
					sender.sendMessage(L.f(L.get("SETTINGS_STOCK"), sto));
					sender.sendMessage(L.f(L.get("SETTINGS_TOTAL_STOCK"), tsto));
					sender.sendMessage(L.f(L.get("SETTINGS_MEDIAN"), med));
					sender.sendMessage(L.f(L.get("SETTINGS_CEILING"), ceiling));
					sender.sendMessage(L.f(L.get("SETTINGS_FLOOR"), floor));
					sender.sendMessage(L.f(L.get("SETTINGS_REACH_HYPERBOLIC"), maxinitialitems));
					sender.sendMessage(L.f(L.get("SETTINGS_TYPE"), objectType));
    				sender.sendMessage(L.get("LINE_BREAK"));
				} else {
	    			sender.sendMessage(L.get("INVALID_ITEM_NAME"));
	    		}  
			} else {
				sender.sendMessage(L.get("ITEMSETTINGS_INVALID"));
			}
		} catch (Exception e) {
			sender.sendMessage(L.get("ITEMSETTINGS_INVALID"));
		}
	}
}
