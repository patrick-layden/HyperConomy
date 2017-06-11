package regalowl.hyperconomy.command;

import regalowl.hyperconomy.HyperConomy;



public class Xpinfo extends BaseCommand implements HyperCommand {


	public Xpinfo(HyperConomy hc) {
		super(hc, true);
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
