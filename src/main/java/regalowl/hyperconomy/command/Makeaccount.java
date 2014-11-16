package regalowl.hyperconomy.command;

import regalowl.hyperconomy.HyperConomy;
import regalowl.hyperconomy.account.HyperPlayer;

public class Makeaccount extends BaseCommand implements HyperCommand {


	public Makeaccount(HyperConomy hc) {
		super(hc, false);
	}

	@Override
	public CommandData onCommand(CommandData data) {
		if (!validate(data)) return data;
		try {
			if (args.length == 1) {
				String account = args[0];
				if (!dm.accountExists(account)) {
					HyperPlayer hp = dm.getHyperPlayerManager().addPlayer(account);
					if (hp != null) {
						data.addResponse(L.get("MAKEACCOUNT_SUCCESS"));
					} else {
						data.addResponse(L.get("MAKEACCOUNT_FAILED"));
					}
				} else {
					data.addResponse(L.get("ACCOUNT_ALREADY_EXISTS"));
				}
			} else {
				data.addResponse(L.get("MAKEACCOUNT_INVALID"));
			}
		} catch (Exception e) {
			data.addResponse(L.get("MAKEACCOUNT_INVALID"));
		}
		return data;
	}
}