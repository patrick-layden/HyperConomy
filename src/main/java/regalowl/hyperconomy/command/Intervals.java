package regalowl.hyperconomy.command;

import regalowl.databukkit.sql.SQLWrite;
import regalowl.hyperconomy.HC;


public class Intervals extends BaseCommand implements HyperCommand {

	public Intervals() {
		super(false);
	}

	@Override
	public CommandData onCommand(CommandData data) {
		if (!validate(data)) return data;
		try {
			if (args.length == 0) {
				SQLWrite sw = hc.getSQLWrite();
				data.addResponse(L.get("LINE_BREAK"));
				data.addResponse(HC.mc.applyColor("&a" + dm.getHyperShopManager().getShopCheckInterval() + " &9tick (" + "&a" + dm.getHyperShopManager().getShopCheckInterval() / 20 + " &9second) shop update interval."));
				data.addResponse(HC.mc.applyColor("&a" + hc.gYH().getSaveInterval()/1000  + " &9second save interval."));
				data.addResponse(HC.mc.applyColor("&a" + sw.getBufferSize() + " &9statements in the SQL write buffer."));
				data.addResponse(L.get("LINE_BREAK"));
			} else {
				data.addResponse(HC.mc.applyColor("&cInvalid Parameters.  Use /intervals"));
			}
		} catch (Exception e) {
			data.addResponse(HC.mc.applyColor("&cInvalid Parameters.  Use /intervals"));
		}
		return data;
	}
}
