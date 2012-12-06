package regalowl.hyperconomy;

import java.util.ArrayList;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class Hctop {
	Hctop(String args[], CommandSender sender) {
		HyperConomy hc = HyperConomy.hc;
		DataFunctions sf = hc.getSQLFunctions();
		LanguageFile L = hc.getLanguageFile();
		try {
			int pe;
			if (args.length == 1) {
				pe = Integer.parseInt(args[0]);
			} else if (args.length == 0) {
				pe = 1;
			} else {
				sender.sendMessage(L.get("HCTOP_INVALID"));
				return;
			}
			ArrayList<String> p = sf.getEconPlayers();
			ArrayList<Double> b = sf.getPlayerBalances();
			
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
			
			Double serverTotal = 0.0;
			for (int i = 0; i < sbalances.size(); i++) {
				serverTotal += sbalances.get(i);
			}
			Calculation calc = hc.getCalculation();
			sender.sendMessage(ChatColor.WHITE + "----- Top Balances -----");
			sender.sendMessage(ChatColor.WHITE + "------ Page (" + pe + "/" + (int)Math.ceil(sbalances.size()/10.0) + " )------");
			sender.sendMessage(ChatColor.WHITE + "Server Total: " + L.get("CURRENCY") + calc.twoDecimals(serverTotal));
			int ps = pe - 1;
			ps *= 10;
			pe *= 10;
			for (int i = ps; i < pe; i++) {
				if (i > (sbalances.size() - 1)) {
					sender.sendMessage(L.get("REACHED_END"));
					return;
				}
				sender.sendMessage(ChatColor.WHITE + "" + (i + 1) + ". " + ChatColor.AQUA + splayers.get(i) + ChatColor.BLUE + ": " + ChatColor.GREEN + L.get("CURRENCY") + sbalances.get(i).toString());
			}
		} catch (Exception e) {
			sender.sendMessage(L.get("HCTOP_INVALID"));
		}
	}
}
