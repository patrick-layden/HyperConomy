package regalowl.hyperconomy;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Itemsettings {
	Itemsettings(String args[], CommandSender sender, Player player, String playerecon) {
		HyperConomy hc = HyperConomy.hc;
		Calculation calc = hc.getCalculation();
		SQLFunctions sf = hc.getSQLFunctions();
		LanguageFile L = hc.getLanguageFile();
		try {
			if (args.length == 0 && player != null) {
				int itd = player.getItemInHand().getTypeId();
				int da = calc.getDamageValue(player.getItemInHand());
				String ke = itd + ":" + da;
				String nam = hc.getnameData(ke);
				if (nam == null) {
					sender.sendMessage(L.get("OBJECT_NOT_IN_DATABASE"));
				} else {
					double val = 0;
					boolean stat = false;
					double statprice = 0;
					double sto = 0;
					double med = 0;
					boolean init = false;
					double starprice = -0;
					val = sf.getValue(nam, playerecon);
					stat = Boolean.parseBoolean(sf.getStatic(nam, playerecon));
					statprice = sf.getStaticPrice(nam, playerecon);
					sto = sf.getStock(nam, playerecon);
					med = sf.getMedian(nam, playerecon);
					init = Boolean.parseBoolean(sf.getInitiation(nam, playerecon));
					starprice = sf.getStartPrice(nam, playerecon);
					double totalstock = ((med * val)/starprice);
					int maxinitialitems = 0;		
					double roundedtotalstock = Math.rint( totalstock * 1.0d ) / 1.0d;
					maxinitialitems = (int) (roundedtotalstock - sto);
					double ceiling = sf.getCeiling(nam, playerecon);
					double floor = sf.getFloor(nam, playerecon);
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
					/*
					player.sendMessage(ChatColor.BLUE + "Name: " + ChatColor.AQUA + "" + nam);
    				player.sendMessage(ChatColor.BLUE + "Value: " + ChatColor.AQUA + "" + val);
    				player.sendMessage(ChatColor.BLUE + "Use Start Price: " + ChatColor.AQUA + "" + init + ChatColor.BLUE + ", " + ChatColor.GREEN + starprice);
    				player.sendMessage(ChatColor.BLUE + "Static price: " + ChatColor.AQUA + "" + stat + ChatColor.BLUE + ", " + ChatColor.GREEN + "" + statprice);
    				player.sendMessage(ChatColor.BLUE + "Stock: " + ChatColor.GREEN + "" + sto);
    				player.sendMessage(ChatColor.BLUE + "Median stock: " + ChatColor.GREEN + "" + med);		
    				player.sendMessage(ChatColor.BLUE + "Ceiling: " + ChatColor.GREEN + "" + ceiling);	
    				player.sendMessage(ChatColor.BLUE + "Floor: " + ChatColor.GREEN + "" + floor);	
    				player.sendMessage(ChatColor.BLUE + "Items Needed To Reach Hyperbolic Curve: " + ChatColor.GREEN + "" + maxinitialitems);
					 */
				}
			} else if (args.length == 1) {
				String nam = args[0];
				String teststring = hc.testiString(nam);
				if (teststring != null) {
					double val = 0;
					boolean stat = false;
					double statprice = 0;
					double sto = 0;
					double med = 0;
					boolean init = false;
					double starprice = -0;
					val = sf.getValue(nam, playerecon);
					stat = Boolean.parseBoolean(sf.getStatic(nam, playerecon));
					statprice = sf.getStaticPrice(nam, playerecon);
					sto = sf.getStock(nam, playerecon);
					med = sf.getMedian(nam, playerecon);
					init = Boolean.parseBoolean(sf.getInitiation(nam, playerecon));
					starprice = sf.getStartPrice(nam, playerecon);				
					double totalstock = ((med * val)/starprice);
					int maxinitialitems = 0;
					double roundedtotalstock = Math.rint( totalstock * 1.0d ) / 1.0d;
					maxinitialitems = (int) (roundedtotalstock - sto);
					double ceiling = sf.getCeiling(nam, playerecon);
					double floor = sf.getFloor(nam, playerecon);
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
