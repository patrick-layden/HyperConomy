package regalowl.hyperconomy.command;

import regalowl.hyperconomy.HyperConomy;



public class Hcbalance extends BaseCommand implements HyperCommand {
	
	
	public Hcbalance(HyperConomy hc) {
		super(hc, false);
	}

	@Override
	public CommandData onCommand(CommandData data) {
		if (!validate(data)) return data;
		try {
			if (args.length == 0) {
				double balance = 0;
				balance = hp.getBalance();
				data.addResponse(L.get("SHOP_LINE_BREAK"));
				data.addResponse(L.f(L.get("PLAYER_BALANCE_MESSAGE"), "", L.formatMoney(balance)));
				data.addResponse(L.get("SHOP_LINE_BREAK"));
    		} else if (args.length == 1 && hp.hasPermission("hyperconomy.balanceall")) {
    			if (!dm.accountExists(args[0])) {
    				data.addResponse(L.get("PLAYER_NOT_FOUND"));
    			} else {
    				Double balance = dm.getAccount(args[0]).getBalance();
    				data.addResponse(L.get("SHOP_LINE_BREAK"));
    				data.addResponse(L.f(L.get("BALANCE_MESSAGE"), args[0], L.formatMoney(balance)));
    				data.addResponse(L.get("SHOP_LINE_BREAK"));
    			}
    		} else if (!hp.hasPermission("hyperconomy.balanceall")) {
    			data.addResponse(L.get("YOU_DONT_HAVE_PERMISSION"));
    		} else {
    			data.addResponse(L.get("HCBALANCE_INVALID"));
    		}
		} catch (Exception e) {
			hc.gSDL().getErrorWriter().writeError(e);
		}
		return data;
	}
}
