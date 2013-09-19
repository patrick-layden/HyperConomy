package regalowl.hyperconomy;

import org.bukkit.entity.Player;

public class Hcpay {
	Hcpay(String args[], Player player) {
		HyperConomy hc = HyperConomy.hc;
		LanguageFile L = hc.getLanguageFile();
		DataHandler dh = hc.getDataFunctions();
		try {
			if (args.length == 2) {
				String recipient = args[0];
				Double amount = Double.parseDouble(args[1]);
				if (dh.hasAccount(recipient)) {
					if (hc.getDataFunctions().getHyperPlayer(player.getName()).hasBalance(amount)) {
						hc.getDataFunctions().getHyperPlayer(player.getName()).withdraw(amount);
						hc.getDataFunctions().getHyperPlayer(recipient).deposit(amount);
						player.sendMessage(L.f(L.get("MONEY_PAYED"), amount, recipient));
					} else {
						player.sendMessage(L.get("INSUFFICIENT_FUNDS"));
					}
				} else {
					player.sendMessage(L.get("ACCOUNT_NOT_EXIST"));
				}
			} else {
				player.sendMessage(L.get("HCPAY_INVALID"));
			}
		} catch (Exception e) {
			player.sendMessage(L.get("HCPAY_INVALID"));
		}
	}
}
