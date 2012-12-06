package regalowl.hyperconomy;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Hcbalance {
	Hcbalance(String args[], CommandSender sender, Player player) {
		HyperConomy hc = HyperConomy.hc;
		LanguageFile L = hc.getLanguageFile();
		Account acc = hc.getAccount();
		try {
			if (args.length == 0 && player != null) {
				Double balance = acc.getBalance(player.getName());
				sender.sendMessage(L.get("LINE_BREAK"));
				sender.sendMessage(L.f(L.get("BALANCE_MESSAGE"), player.getName(), balance.toString()));
				sender.sendMessage(L.get("LINE_BREAK"));
    		} else if (args.length == 1 && sender.hasPermission("hyperconomy.balanceall")){
    			Double balance = acc.getBalance(args[0]);
    			if (balance == -9999999.0) {
        			sender.sendMessage(L.get("PLAYER_NOT_FOUND"));
    			} else {
    				sender.sendMessage(L.get("LINE_BREAK"));
        			sender.sendMessage(L.f(L.get("BALANCE_MESSAGE"), args[0], balance.toString()));
    				sender.sendMessage(L.get("LINE_BREAK"));
    			}
    		} else {
    			sender.sendMessage(L.get("HCBALANCE_INVALID"));
    		}
		} catch (Exception e) {
			sender.sendMessage(L.get("HCBALANCE_INVALID"));
		}
	}
}
