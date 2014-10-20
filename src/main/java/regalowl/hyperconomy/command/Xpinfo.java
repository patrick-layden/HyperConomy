package regalowl.hyperconomy.command;

import org.bukkit.entity.Player;

import regalowl.hyperconomy.DataManager;
import regalowl.hyperconomy.HyperConomy;
import regalowl.hyperconomy.account.HyperPlayer;
import regalowl.hyperconomy.util.LanguageFile;

public class Xpinfo extends BaseCommand implements HyperCommand {


	public Xpinfo() {
		super(true);
	}

	@Override
	public CommandData onCommand(CommandData data) {
		if (!validate(data)) return data;
		try {
			if (args.length == 0) {
				int totalexp = hp.getTotalXpPoints();
				int lvl = hp.getLevel();
				int xpfornextlvl = hp.getXpForNextLvl(lvl) - hp.getBarXpPoints();			
				int xpfor30 = hp.getLvlXpPoints(30) - totalexp;				
				data.addResponse(L.get("LINE_BREAK"));
				data.addResponse(L.f(L.get("TOTAL_XP_POINTS"), totalexp));
				data.addResponse(L.f(L.get("XP_FOR_NEXT_LVL"), xpfornextlvl));
				data.addResponse(L.f(L.get("XP_FOR_LVL_30"), xpfor30));
				data.addResponse(L.get("LINE_BREAK"));
			} else {
				data.addResponse(L.get("XPINFO_INVALID"));
			}
		} catch (Exception e) {
			data.addResponse(L.get("XPINFO_INVALID"));
		}
		return data;
	}
	
}
