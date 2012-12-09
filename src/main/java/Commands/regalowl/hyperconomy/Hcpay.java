package regalowl.hyperconomy;

import org.bukkit.entity.Player;

public class Hcpay {
	Hcpay(String args[], Player player) {
		HyperConomy hc = HyperConomy.hc;
		LanguageFile L = hc.getLanguageFile();
		Account acc = hc.getAccount();
		try {
			if (args.length == 2) {
				String recipient = args[0];
				Double amount = Double.parseDouble(args[1]);
				if (acc.checkAccount(recipient)) {
					if (acc.checkFunds(amount, player)) {
						acc.withdraw(amount, player);
						acc.depositAccount(amount, recipient);
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
