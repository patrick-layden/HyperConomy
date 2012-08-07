package regalowl.hyperconomy;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class Enchantsettings {
	Enchantsettings(String args[], CommandSender sender, String playerecon) {
		HyperConomy hc = HyperConomy.hc;
		SQLFunctions sf = hc.getSQLFunctions();
		try {
			String nam = args[0];
			if (nam == null) {
				sender.sendMessage(ChatColor.BLUE + "Sorry, that enchantment is not in the enchantment database.");
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
				sender.sendMessage(ChatColor.BLACK + "-----------------------------------------------------");
				sender.sendMessage(ChatColor.BLUE + "Name: " + ChatColor.AQUA + "" + nam);
				sender.sendMessage(ChatColor.BLUE + "Value: " + ChatColor.AQUA + "" + val);
				sender.sendMessage(ChatColor.BLUE + "Use Start Price: " + ChatColor.AQUA + "" + init + ChatColor.BLUE + ", " + ChatColor.GREEN + starprice);
				sender.sendMessage(ChatColor.BLUE + "Static price: " + ChatColor.AQUA + "" + stat + ChatColor.BLUE + ", " + ChatColor.GREEN + "" + statprice);       				
				sender.sendMessage(ChatColor.BLUE + "Stock: " + ChatColor.GREEN + "" + sto);
				sender.sendMessage(ChatColor.BLUE + "Median stock: " + ChatColor.GREEN + "" + med);			
				sender.sendMessage(ChatColor.BLUE + "Items Needed To Reach Hyperbolic Curve: " + ChatColor.GREEN + "" + maxinitialitems);
				sender.sendMessage(ChatColor.BLACK + "-----------------------------------------------------");
			}
	} catch (Exception e) {
		sender.sendMessage(ChatColor.DARK_RED + "Invalid parameters. Use /enchantsettings [enchantment] or /es [enchantment]");
	}
	}
}
