package regalowl.hyperconomy;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Hcbalance {
	Hcbalance(String args[], CommandSender sender, Player player) {
		HyperConomy hc = HyperConomy.hc;
		DataFunctions sf = hc.getSQLFunctions();
		LanguageFile L = hc.getLanguageFile();
		try {
			if (args.length == 0 && player != null) {
				double balance = sf.getPlayerBalance(player);
				sender.sendMessage(ChatColor.AQUA + "" + player.getName() + ChatColor.BLUE + " has " + ChatColor.GREEN + "" + hc.getYaml().getConfig().getString("config.currency-symbol") + balance);
    		} else if (args.length == 1 && sender.hasPermission("hyperconomy.balanceall")){
    			double balance = sf.getPlayerBalance(args[0]);
    			if (balance == -9999999.0) {
        			sender.sendMessage(ChatColor.DARK_RED + "Player not found!");
    			} else {
        			sender.sendMessage(ChatColor.AQUA + "" + player.getName() + ChatColor.BLUE + " has " + ChatColor.GREEN + "" + hc.getYaml().getConfig().getString("config.currency-symbol") + balance);
    			}
    		} else {
    			sender.sendMessage(L.get("LISTECONOMIES_INVALID"));
    		}
		} catch (Exception e) {
			sender.sendMessage(L.get("LISTECONOMIES_INVALID"));
		}
	}
}
