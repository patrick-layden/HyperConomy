package regalowl.hyperconomy;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Hcbalance {
	Hcbalance(String args[], CommandSender sender, Player player) {
		HyperConomy hc = HyperConomy.hc;
		LanguageFile L = hc.getLanguageFile();
		Calculation calc = hc.getCalculation();
		DataHandler dh = hc.getDataFunctions();
		try {
			if (args.length == 0 && player != null) {
				double balance = 0;
				if (dh.hasAccount(player.getName())) {
					balance = dh.getHyperPlayer(player.getName()).getBalance();
				} else {
        			sender.sendMessage(L.get("PLAYER_NOT_FOUND"));
        			return;
				}
				sender.sendMessage(L.get("SHOP_LINE_BREAK"));
				sender.sendMessage(L.f(L.get("PLAYER_BALANCE_MESSAGE"), "", calc.formatMoney(balance)));
				sender.sendMessage(L.get("SHOP_LINE_BREAK"));
    		} else if (args.length == 1 && sender.hasPermission("hyperconomy.balanceall")) {
    			if (!dh.hasAccount(args[0])) {
        			sender.sendMessage(L.get("PLAYER_NOT_FOUND"));
    			} else {
    				Double balance = dh.getHyperPlayer(args[0]).getBalance();
    				sender.sendMessage(L.get("SHOP_LINE_BREAK"));
        			sender.sendMessage(L.f(L.get("BALANCE_MESSAGE"), args[0], calc.formatMoney(balance)));
    				sender.sendMessage(L.get("SHOP_LINE_BREAK"));
    			}
    		} else if (!sender.hasPermission("hyperconomy.balanceall")) {
    			sender.sendMessage(L.get("YOU_DONT_HAVE_PERMISSION"));
    		} else {
    			sender.sendMessage(L.get("HCBALANCE_INVALID"));
    		}
		} catch (Exception e) {
			sender.sendMessage(L.get("HCBALANCE_INVALID"));
		}
	}
}
