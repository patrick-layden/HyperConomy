package regalowl.hyperconomy;

import org.bukkit.entity.Player;

public class Xpinfo {

	
	
	Xpinfo(String args[], Player player) {
		HyperConomy hc = HyperConomy.hc;
		Calculation calc = hc.getCalculation();
		LanguageFile L = hc.getLanguageFile();
		try {
			if (args.length == 0) {
				int totalexp = calc.gettotalxpPoints(player);
				int lvl = player.getLevel();
				int xpfornextlvl = calc.getxpfornextLvl(lvl) - calc.getbarxpPoints(player);
				
				int xpfor30 = calc.getlvlxpPoints(30) - totalexp;
				
				player.sendMessage(L.get("LINE_BREAK"));
				player.sendMessage(L.f(L.get("TOTAL_XP_POINTS"), totalexp));
				player.sendMessage(L.f(L.get("XP_FOR_NEXT_LVL"), xpfornextlvl));
				player.sendMessage(L.f(L.get("XP_FOR_LVL_30"), xpfor30));
				//player.sendMessage(ChatColor.BLUE + "Total Experience Points: " + ChatColor.GREEN + "" + totalexp);
				//player.sendMessage(ChatColor.BLUE + "Experience Needed For The Next Level: " + ChatColor.GREEN + "" + xpfornextlvl);
				//player.sendMessage(ChatColor.BLUE + "Experience Needed For Level 30: " + ChatColor.GREEN + "" + xpfor30);
				player.sendMessage(L.get("LINE_BREAK"));
			} else {
				player.sendMessage(L.get("XPINFO_INVALID"));
			}
		} catch (Exception e) {
			player.sendMessage(L.get("XPINFO_INVALID"));
		}
		
		
		
	}
	
}
