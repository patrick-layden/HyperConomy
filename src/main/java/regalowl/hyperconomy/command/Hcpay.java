package regalowl.hyperconomy.command;

import regalowl.hyperconomy.HyperConomy;
import regalowl.hyperconomy.account.HyperAccount;
import regalowl.hyperconomy.account.HyperPlayer;
import regalowl.hyperconomy.util.MessageBuilder;


public class Hcpay extends BaseCommand implements HyperCommand{
	
	public Hcpay(HyperConomy hc) {
		super(hc, true);
	}

	@Override
	public CommandData onCommand(CommandData data) {
		if (!validate(data)) return data;
		try {
			if (args.length == 2) {
				String recipient = args[0];
				Double amount = 0.0;
				try {
					amount = Double.parseDouble(args[1]);
				} catch (Exception e) {}
				if (amount <= 0) {
					data.addResponse(L.get("CANNOT_PAY_NEGATIVE"));
					return data;
				}
				if (dm.accountExists(recipient)) {
					if (hp.hasBalance(amount)) {
						hp.withdraw(amount);
						HyperAccount rAccount = dm.getAccount(recipient);
						if (rAccount instanceof HyperPlayer) {
							HyperPlayer rPlayer = (HyperPlayer)rAccount;
							MessageBuilder mb = new MessageBuilder(hc, "HCPAY_PAID");
							mb.setAmount(amount);
							mb.setPlayerName(hp.getName());
							if (rPlayer.isOnline()) rPlayer.sendMessage(mb.build());
						}
						dm.getAccount(recipient).deposit(amount);
						data.addResponse(L.f(L.get("MONEY_PAID"), amount, recipient));
					} else {
						data.addResponse(L.get("INSUFFICIENT_FUNDS"));
					}
				} else {
					data.addResponse(L.get("ACCOUNT_NOT_EXIST"));
				}
			} else {
				data.addResponse(L.get("HCPAY_INVALID"));
			}
		} catch (Exception e) {
			hc.gSDL().getErrorWriter().writeError(e);
		}
		return data;
		
		
		
		
		
		
	}
}
