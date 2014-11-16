package regalowl.hyperconomy.command;

import java.util.ArrayList;

import regalowl.hyperconomy.HyperConomy;
import regalowl.hyperconomy.account.HyperPlayer;

public class Hctop extends BaseCommand implements HyperCommand {
	
	public Hctop(HyperConomy hc) {
		super(hc, false);
	}

	@Override
	public CommandData onCommand(CommandData data) {
		if (!validate(data)) return data;
		try {
			if (hc.getMC().useExternalEconomy()) {
				data.addResponse(L.get("ONLY_AVAILABLE_INTERNAL"));
				return data;
			}
			int pe;
			if (args.length == 1) {
				pe = Integer.parseInt(args[0]);
			} else if (args.length == 0) {
				pe = 1;
			} else {
				data.addResponse(L.get("HCTOP_INVALID"));
				return data;
			}
			
			ArrayList<String> players = new ArrayList<String>();
			ArrayList<Double> balances = new ArrayList<Double>();
			for (HyperPlayer hp:dm.getHyperPlayerManager().getHyperPlayers()) {
				players.add(hp.getName());
				balances.add(hp.getBalance());
			}
			ArrayList<String> splayers = new ArrayList<String>();
			ArrayList<Double> sbalances = new ArrayList<Double>();
			while (balances.size() > 0) {
				int topBalanceIndex = 0;
				double topBalance = 0;
				for (int i = 0; i < balances.size(); i++) {
					double curBal = balances.get(i);
					if (curBal > topBalance) {
						topBalance = curBal;
						topBalanceIndex = i;
					}
				}
				sbalances.add(topBalance);
				splayers.add(players.get(topBalanceIndex));
				balances.remove(topBalanceIndex);
				players.remove(topBalanceIndex);
			}
			double serverTotal = 0.0;
			for (int i = 0; i < sbalances.size(); i++) {
				serverTotal += sbalances.get(i);
			}
			data.addResponse(L.get("TOP_BALANCE"));
			data.addResponse(L.f(L.get("TOP_BALANCE_PAGE"), pe, (int)Math.ceil(sbalances.size()/10.0)));
			data.addResponse(L.f(L.get("TOP_BALANCE_TOTAL"), L.formatMoney(serverTotal)));
			int ps = pe - 1;
			ps *= 10;
			pe *= 10;
			for (int i = ps; i < pe; i++) {
				if (i > (sbalances.size() - 1)) {
					data.addResponse(L.get("REACHED_END"));
					return data;
				}
				data.addResponse(L.f(L.get("TOP_BALANCE_BALANCE"), splayers.get(i), L.formatMoney(sbalances.get(i)), (i + 1)));
			}
		} catch (Exception e) {
			data.addResponse(L.get("HCTOP_INVALID"));
		}
		return data;
	}
}
