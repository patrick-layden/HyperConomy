package regalowl.hyperconomy.command;

import regalowl.hyperconomy.HC;
import regalowl.hyperconomy.HyperEconomy;
import regalowl.hyperconomy.inventory.HItemStack;
import regalowl.hyperconomy.tradeobject.TradeObject;

public class Iteminfo extends BaseCommand implements HyperCommand{

	public Iteminfo() {
		super(true);
	}

	@Override
	public CommandData onCommand(CommandData data) {
		if (!validate(data)) return data;
		HyperEconomy he = hp.getHyperEconomy();
		HItemStack sis = null;
		TradeObject ho = null;
		if (args.length == 0) {
			sis = hp.getItemInHand();
			ho = he.getHyperObject(sis);
		} else {
			if (args[0].equalsIgnoreCase("pd")) {
				sis = hp.getItemInHand();
				data.addResponse(sis.serialize());
				return data;
			} else if (args[0].equalsIgnoreCase("comp")) {
				if (args.length == 2) {
					TradeObject to = HC.hc.getDataManager().getDefaultEconomy().getHyperObject(args[1]);
					sis = hp.getItemInHand();
					HItemStack sis2 = to.getItem();
					if (sis.equals(sis2)) {
						data.addResponse("Objects equal.");
						return data;
					} else {
						data.addResponse("Objects not equal.");
						data.addResponse("Held: "+sis.serialize());
						data.addResponse("TradeObject: "+sis2.serialize());
						return data;
					}
				}
			}
			ho = he.getHyperObject(args[0]);
			if (ho == null) {
				data.addResponse(HC.mc.applyColor("&9" + "Object not found."));
				return data;
			}
			sis = ho.getItem();
		}
		data.addResponse(L.get("LINE_BREAK"));
		if (ho == null) {
			data.addResponse(HC.mc.applyColor("&cItem not in database."));
		} else {
			data.addResponse(HC.mc.applyColor("&9Identifier: " + "&b" + ho.getName()));
			data.addResponse(HC.mc.applyColor("&9HyperConomy Name: " + "&b" + ho.getDisplayName()));
			data.addResponse(HC.mc.applyColor("&9Aliases: " + "&b" + ho.getAliasesString()));
		}
		data.addResponses(sis.displayInfo(hp, "&9", "&b"));
		data.addResponse(L.get("LINE_BREAK"));
		return data;
	}
}
