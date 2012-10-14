package regalowl.hyperconomy;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class Hv {
	Hv(String args[], Player player, String playerecon) {
		HyperConomy hc = HyperConomy.hc;
		SQLFunctions sf = hc.getSQLFunctions();
		Calculation calc = hc.getCalculation();
		Transaction tran = hc.getTransaction();
		ETransaction ench = hc.getETransaction();
		int amount;
		try {
			ItemStack iinhand = player.getItemInHand();
			if (ench.hasenchants(iinhand) == false) {
				if (args.length == 0) {
					amount = 1;
				} else {
					amount = Integer.parseInt(args[0]);
				}
				int itd = player.getItemInHand().getTypeId();
				int da = calc.getpotionDV(player.getItemInHand());
				int newdat = calc.newData(itd, da);
				String ke = itd + ":" + newdat;
				String nam = hc.getnameData(ke);
				if (nam == null) {
					player.sendMessage(ChatColor.BLUE + "Sorry, that item is not currently available.");
				} else {
					double val = calc.getValue(nam, amount, player);
					if (calc.testId(itd) && amount > 1) {
						int numberofitem = tran.countInvitems(itd, player.getItemInHand().getData().getData(), player);
						if (amount - numberofitem > 0) {
							int addamount = amount - numberofitem;
							val = val + calc.getTvalue(nam, addamount, playerecon);
						}
					}
					double salestax = calc.getSalesTax(player, val);
					val = calc.twoDecimals(val - salestax);
					player.sendMessage(ChatColor.BLACK + "-----------------------------------------------------");
					player.sendMessage(ChatColor.GREEN + "" + amount + ChatColor.AQUA + " " + nam + ChatColor.BLUE + " can be sold for: " + ChatColor.GREEN + hc.getYaml().getConfig().getString("config.currency-symbol") + val);
					double cost = calc.getCost(nam, amount, playerecon);
					double taxpaid = calc.getPurchaseTax(nam, playerecon, cost);
					cost = calc.twoDecimals(cost + taxpaid);
					String scost = "";
					if (cost > Math.pow(10, 10)) {
						scost = "INFINITY";
					} else {
						scost = cost + "";
					}
					double stock = 0;
					stock = sf.getStock(nam, playerecon);
					player.sendMessage(ChatColor.GREEN + "" + amount + ChatColor.AQUA + " " + nam + ChatColor.BLUE + " can be purchased for: " + ChatColor.GREEN + hc.getYaml().getConfig().getString("config.currency-symbol") + scost);
					player.sendMessage(ChatColor.BLUE + "The global shop currently has " + ChatColor.GREEN + "" + stock + ChatColor.AQUA + " " + nam + ChatColor.BLUE + " available.");
					player.sendMessage(ChatColor.BLACK + "-----------------------------------------------------");
				}
			} else {
				player.sendMessage("You cannot buy or sell enchanted items.");
			}
		} catch (Exception e) {
			player.sendMessage(ChatColor.DARK_RED + "Use /hv (amount)");
		}
	}
}
