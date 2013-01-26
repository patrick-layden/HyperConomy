package regalowl.hyperconomy;

import java.util.ArrayList;
import org.bukkit.command.CommandSender;

public class Hctop {
	Hctop(String args[], CommandSender sender) {
		HyperConomy hc = HyperConomy.hc;
		DataFunctions df = hc.getDataFunctions();
		LanguageFile L = hc.getLanguageFile();
		Calculation calc = hc.getCalculation();
		try {
			if (hc.useExternalEconomy()) {
				sender.sendMessage(L.get("ONLY_AVAILABLE_INTERNAL"));
				return;
			}
			int pe;
			if (args.length == 1) {
				pe = Integer.parseInt(args[0]);
			} else if (args.length == 0) {
				pe = 1;
			} else {
				sender.sendMessage(L.get("HCTOP_INVALID"));
				return;
			}
			ArrayList<String> p = df.getEconPlayers();
			ArrayList<Double> b = df.getPlayerBalances();
			
			ArrayList<String> players = new ArrayList<String>();
			ArrayList<Double> balances = new ArrayList<Double>();
			
			//sender.sendMessage(b.toString());
			for (int i = 0; i < p.size(); i++) {
				players.add(p.get(i));
				balances.add(b.get(i));
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
			sender.sendMessage(L.get("TOP_BALANCE"));
			sender.sendMessage(L.f(L.get("TOP_BALANCE_PAGE"), pe, (int)Math.ceil(sbalances.size()/10.0)));
			sender.sendMessage(L.f(L.get("TOP_BALANCE_TOTAL"), calc.formatMoney(serverTotal)));
			int ps = pe - 1;
			ps *= 10;
			pe *= 10;
			for (int i = ps; i < pe; i++) {
				if (i > (sbalances.size() - 1)) {
					sender.sendMessage(L.get("REACHED_END"));
					return;
				}
				sender.sendMessage(L.f(L.get("TOP_BALANCE_BALANCE"), splayers.get(i), calc.formatMoney(sbalances.get(i)), (i + 1)));
				//sender.sendMessage(ChatColor.WHITE + "" + (i + 1) + ". " + ChatColor.AQUA + splayers.get(i) + ChatColor.BLUE + ": " + ChatColor.GREEN + L.get("CURRENCY") + calc.formatMoney(sbalances.get(i)));
			}
		} catch (Exception e) {
			sender.sendMessage(L.get("HCTOP_INVALID"));
		}
	}
}
