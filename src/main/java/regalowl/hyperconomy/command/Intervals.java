package regalowl.hyperconomy.command;

import regalowl.hyperconomy.HyperConomy;
import regalowl.simpledatalib.sql.SQLWrite;


public class Intervals extends BaseCommand implements HyperCommand {

	public Intervals(HyperConomy hc) {
		super(hc, false);
	}

	@Override
	public CommandData onCommand(CommandData data) {
		if (!validate(data)) return data;
		try {
			if (args.length == 0) {
				SQLWrite sw = hc.getSQLWrite();
				data.addResponse(L.get("LINE_BREAK"));
				data.addResponse("&a" + dm.getHyperShopManager().getShopCheckInterval() + " &9tick (" + "&a" + dm.getHyperShopManager().getShopCheckInterval() / 20 + " &9second) shop update interval.");
				data.addResponse("&a" + hc.gYH().getSaveInterval()/1000  + " &9second save interval.");
				data.addResponse("&a" + sw.getBufferSize() + " &9statements in the SQL write buffer.");
				data.addResponse(L.get("LINE_BREAK"));
			} else {
				data.addResponse("&cInvalid Parameters.  Use /intervals");
			}
		} catch (Exception e) {
			data.addResponse("&cInvalid Parameters.  Use /intervals");
		}
		return data;
	}
}
