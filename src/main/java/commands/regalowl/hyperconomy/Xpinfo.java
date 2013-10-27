package regalowl.hyperconomy;

import org.bukkit.entity.Player;

public class Xpinfo {

	
	
	Xpinfo(String args[], Player player) {
		HyperConomy hc = HyperConomy.hc;
		EconomyManager em = hc.getEconomyManager();
		LanguageFile L = hc.getLanguageFile();
		try {
			if (args.length == 0) {
				HyperXP xp = em.getHyperPlayer(player).getHyperEconomy().getHyperXP();
				int totalexp = xp.getTotalXpPoints(player);
				int lvl = player.getLevel();
				int xpfornextlvl = xp.getXpForNextLvl(lvl) - xp.getBarXpPoints(player);			
				int xpfor30 = xp.getLvlXpPoints(30) - totalexp;				
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
