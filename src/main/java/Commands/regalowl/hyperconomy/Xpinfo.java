package regalowl.hyperconomy;

import org.bukkit.entity.Player;

public class Xpinfo {

	
	
	Xpinfo(String args[], Player player) {
		HyperConomy hc = HyperConomy.hc;
		LanguageFile L = hc.getLanguageFile();
		InventoryManipulation im = hc.getInventoryManipulation();
		try {
			if (args.length == 0) {
				int totalexp = im.gettotalxpPoints(player);
				int lvl = player.getLevel();
				int xpfornextlvl = im.getxpfornextLvl(lvl) - im.getbarxpPoints(player);			
				int xpfor30 = im.getlvlxpPoints(30) - totalexp;				
				player.sendMessage(L.get("LINE_BREAK"));
				player.sendMessage(L.f(L.get("TOTAL_XP_POINTS"), totalexp));
				player.sendMessage(L.f(L.get("XP_FOR_NEXT_LVL"), xpfornextlvl));
				player.sendMessage(L.f(L.get("XP_FOR_LVL_30"), xpfor30));
				player.sendMessage(L.get("LINE_BREAK"));
			} else {
				player.sendMessage(L.get("XPINFO_INVALID"));
			}
		} catch (Exception e) {
			player.sendMessage(L.get("XPINFO_INVALID"));
		}
		
		
		
	}
	
}
