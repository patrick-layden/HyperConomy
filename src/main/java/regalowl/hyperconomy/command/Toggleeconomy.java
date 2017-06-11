package regalowl.hyperconomy.command;

import regalowl.hyperconomy.HyperConomy;




public class Toggleeconomy extends BaseCommand implements HyperCommand {


	public Toggleeconomy(HyperConomy hc) {
		super(hc, false);
	}

	@Override
	public CommandData onCommand(CommandData data) {
		if (!validate(data)) return data;
		try {
			if (hc.getConf().getBoolean("economy-plugin.use-external")) {
				hc.getConf().set("economy-plugin.use-external", false);
				data.addResponse(L.get("TOGGLEECONOMY_DISABLED"));
			} else {
				hc.getConf().set("economy-plugin.use-external", true);
				data.addResponse(L.get("TOGGLEECONOMY_ENABLED"));
			}
		} catch (Exception e) {
			data.addResponse(L.get("TOGGLEECONOMY_INVALID"));
		}
		return data;
	}
}
