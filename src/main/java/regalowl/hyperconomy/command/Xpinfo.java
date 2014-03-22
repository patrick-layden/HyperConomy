package regalowl.hyperconomy.command;

import org.bukkit.entity.Player;

import regalowl.hyperconomy.DataManager;
import regalowl.hyperconomy.HyperConomy;
import regalowl.hyperconomy.account.HyperPlayer;
import regalowl.hyperconomy.util.LanguageFile;

public class Xpinfo {

	
	
	Xpinfo(String args[], Player player) {
		HyperConomy hc = HyperConomy.hc;
		DataManager em = hc.getDataManager();
		LanguageFile L = hc.getLanguageFile();
		try {
			if (args.length == 0) {
				HyperPlayer hp = em.getHyperPlayer(player);
				int totalexp = hp.getTotalXpPoints();
				int lvl = player.getLevel();
				int xpfornextlvl = hp.getXpForNextLvl(lvl) - hp.getBarXpPoints();			
				int xpfor30 = hp.getLvlXpPoints(30) - totalexp;				
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
