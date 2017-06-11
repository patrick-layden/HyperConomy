package regalowl.hyperconomy.command;

import regalowl.hyperconomy.HyperConomy;


public class Economyinfo extends BaseCommand implements HyperCommand {
	public Economyinfo(HyperConomy hc) {
		super(hc, false);
	}

	@Override
	public CommandData onCommand(CommandData data) {
		if (!validate(data)) return data;
		try {
			if (args.length == 0) {
				data.addResponse(L.f(L.get("PART_OF_ECONOMY"), getEconomyName()));
			} else {
				data.addResponse(L.get("ECONOMYINFO_INVALID"));
			}
		} catch (Exception e) {
			hc.gSDL().getErrorWriter().writeError(e);
		}
		return data;
	}
}
