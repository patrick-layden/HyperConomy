package regalowl.hyperconomy;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class Xpinfo {

	
	
	Xpinfo(String args[], Player player) {
		HyperConomy hc = HyperConomy.hc;
		Calculation calc = hc.getCalculation();
		try {
			if (args.length == 0) {
				int totalexp = calc.gettotalxpPoints(player);
				int lvl = player.getLevel();
				int xpfornextlvl = calc.getxpfornextLvl(lvl) - calc.getbarxpPoints(player);
				
				int xpfor50 = calc.getlvlxpPoints(50) - totalexp;
				
				player.sendMessage(ChatColor.BLACK + "-----------------------------------------------------");
				player.sendMessage(ChatColor.BLUE + "Total Experience Points: " + ChatColor.GREEN + "" + totalexp);
				player.sendMessage(ChatColor.BLUE + "Experience Needed For The Next Level: " + ChatColor.GREEN + "" + xpfornextlvl);
				player.sendMessage(ChatColor.BLUE + "Experience Needed For Level 50: " + ChatColor.GREEN + "" + xpfor50);
				player.sendMessage(ChatColor.BLACK + "-----------------------------------------------------");
			} else {
				player.sendMessage(ChatColor.DARK_RED + "Invalid Parameters.  Use /xpinfo");
			}
		} catch (Exception e) {
			player.sendMessage(ChatColor.DARK_RED + "Invalid Parameters.  Use /xpinfo");
		}
		
		
		
	}
	
}
