package regalowl.hyperconomy;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Itemsettings {
	Itemsettings(String args[], CommandSender sender, Player player, String playerecon) {
		HyperConomy hc = HyperConomy.hc;
		Calculation calc = hc.getCalculation();
		DataHandler sf = hc.getDataFunctions();
		LanguageFile L = hc.getLanguageFile();
		try {
			HyperPlayer hp = sf.getHyperPlayer(player);
			if (args.length == 0 && player != null) {
				int itd = player.getItemInHand().getTypeId();
				int da = calc.getDamageValue(player.getItemInHand());
				HyperObject hob = hc.getDataFunctions().getHyperObject(itd, da, hp.getEconomy());
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
					HyperObject ho = sf.getHyperObject(nam, playerecon);
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
			} else if (args.length == 1) {
				String nam = sf.fixName(args[0]);
				if (sf.itemTest(nam)) {
					double val = 0;
					boolean stat = false;
					double statprice = 0;
					double sto = 0;
					double med = 0;
					boolean init = false;
					double starprice = 0;
					HyperObject ho = sf.getHyperObject(nam, playerecon);
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
