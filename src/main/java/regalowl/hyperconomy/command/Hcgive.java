package regalowl.hyperconomy.command;

import regalowl.hyperconomy.HyperConomy;
import regalowl.hyperconomy.account.HyperPlayer;
import regalowl.hyperconomy.util.MessageBuilder;


public class Hcgive extends BaseCommand implements HyperCommand{
	
	public Hcgive(HyperConomy hc) {
		super(hc, true);
	}

	@Override
	public CommandData onCommand(CommandData data) {
		if (!validate(data)) return data;
		try {
			if (args.length < 2) {
				data.addResponse(L.get("HCGIVE_INVALID"));
				return data;
			}
			HyperPlayer recipient = null;
			if (dm.hyperPlayerExists(args[0])) {
				recipient = dm.getHyperPlayer(args[0]);
			} else {
				data.addResponse(L.get("PLAYER_NOT_FOUND"));
				return data;
			}
			String type = args[1];
			if (type.equalsIgnoreCase("MONEY") || type.equalsIgnoreCase("M")) {
				Double amount = 0.0;
				try {
					amount = Double.parseDouble(args[2]);
				} catch (Exception e) {}
				if (amount <= 0) {
					data.addResponse(L.get("CANNOT_PAY_NEGATIVE"));
					return data;
				}
				recipient.deposit(amount);
				MessageBuilder mb = new MessageBuilder(hc, "MONEY_RECEIVED");
				mb.setAmount(amount);
				if (recipient.isOnline()) recipient.sendMessage(mb.build());
				mb = new MessageBuilder(hc, "MONEY_GIVEN");
				mb.setAmount(amount);
				mb.setPlayerName(recipient.getName());
				hp.sendMessage(mb.build());
			} else {
				data.addResponse(L.get("HCGIVE_INVALID"));
			}
		} catch (Exception e) {
			hc.gSDL().getErrorWriter().writeError(e);
		}
		return data;
		
		
		
		
		
		
	}
}
