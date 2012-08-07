package regalowl.hyperconomy;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Itemsettings {
	Itemsettings(String args[], CommandSender sender, Player player, String playerecon) {
		HyperConomy hc = HyperConomy.hc;
		Calculation calc = hc.getCalculation();
		SQLFunctions sf = hc.getSQLFunctions();
		try {
			if (args.length == 0 && player != null) {
				int itd = player.getItemInHand().getTypeId();
				int da = calc.getpotionDV(player.getItemInHand());
				int newdat = calc.newData(itd, da);
				String ke = itd + ":" + newdat;
				String nam = hc.getnameData(ke);
				if (nam == null) {
					sender.sendMessage(ChatColor.BLUE + "Sorry, that item or enchantment is not in the database.");
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
					player.sendMessage(ChatColor.BLACK + "-----------------------------------------------------");
					player.sendMessage(ChatColor.BLUE + "Name: " + ChatColor.AQUA + "" + nam);
    				player.sendMessage(ChatColor.BLUE + "Value: " + ChatColor.AQUA + "" + val);
    				player.sendMessage(ChatColor.BLUE + "Use Start Price: " + ChatColor.AQUA + "" + init + ChatColor.BLUE + ", " + ChatColor.GREEN + starprice);
    				player.sendMessage(ChatColor.BLUE + "Static price: " + ChatColor.AQUA + "" + stat + ChatColor.BLUE + ", " + ChatColor.GREEN + "" + statprice);
    				player.sendMessage(ChatColor.BLUE + "Stock: " + ChatColor.GREEN + "" + sto);
    				player.sendMessage(ChatColor.BLUE + "Median stock: " + ChatColor.GREEN + "" + med);		
    				player.sendMessage(ChatColor.BLUE + "Items Needed To Reach Hyperbolic Curve: " + ChatColor.GREEN + "" + maxinitialitems);
    				player.sendMessage(ChatColor.BLACK + "-----------------------------------------------------");
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
					sender.sendMessage(ChatColor.BLACK + "-----------------------------------------------------");
					sender.sendMessage(ChatColor.BLUE + "Name: " + ChatColor.AQUA + "" + nam);
    				sender.sendMessage(ChatColor.BLUE + "Value: " + ChatColor.AQUA + "" + val);
    				sender.sendMessage(ChatColor.BLUE + "Use Start Price: " + ChatColor.AQUA + "" + init + ChatColor.BLUE + ", " + ChatColor.GREEN + starprice);
    				sender.sendMessage(ChatColor.BLUE + "Static price: " + ChatColor.AQUA + "" + stat + ChatColor.BLUE + ", " + ChatColor.GREEN + "" + statprice);
    				sender.sendMessage(ChatColor.BLUE + "Stock: " + ChatColor.GREEN + "" + sto);
    				sender.sendMessage(ChatColor.BLUE + "Median stock: " + ChatColor.GREEN + "" + med);		
    				sender.sendMessage(ChatColor.BLUE + "Items Needed To Reach Hyperbolic Curve: " + ChatColor.GREEN + "" + maxinitialitems);
    				sender.sendMessage(ChatColor.BLACK + "-----------------------------------------------------");
				} else {
	    			sender.sendMessage(ChatColor.DARK_RED + "Invalid item name!");
	    		}  
			} else {
				sender.sendMessage(ChatColor.DARK_RED + "Invalid parameters. Hold an item and use /itemsettings (id) (damage value)");
			}
		} catch (Exception e) {
			sender.sendMessage(ChatColor.DARK_RED + "Invalid parameters. Hold an item and use /itemsettings (id) (damage value)");
		}
	}
}
