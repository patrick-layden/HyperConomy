package regalowl.hyperconomy.command;

import regalowl.hyperconomy.HyperConomy;
import regalowl.hyperconomy.HyperEconomy;
import regalowl.hyperconomy.hyperobject.HyperObject;
import regalowl.hyperconomy.serializable.SerializableItemStack;

public class Iteminfo extends BaseCommand implements HyperCommand{

	public Iteminfo() {
		super(true);
	}

	@Override
	public CommandData onCommand(CommandData data) {
		if (!validate(data)) return data;
		HyperEconomy he = hp.getHyperEconomy();
		SerializableItemStack sis = null;
		HyperObject ho = null;
		if (args.length == 0) {
			ho = he.getHyperObject(hp.getItemInHand());
			sis = hp.getItemInHand();
		} else {
			ho = he.getHyperObject(args[0]);
			if (ho == null) {
				data.addResponse(HyperConomy.mc.applyColor("&9" + "Object not found."));
				return data;
			}
			sis = ho.getItem();
		}
		data.addResponse(L.get("LINE_BREAK"));
		if (ho == null) {
			data.addResponse(HyperConomy.mc.applyColor("&cItem not in database."));
		} else {
			data.addResponse(HyperConomy.mc.applyColor("&9Identifier: " + "&b" + ho.getName()));
			data.addResponse(HyperConomy.mc.applyColor("&9HyperConomy Name: " + "&b" + ho.getDisplayName()));
			data.addResponse(HyperConomy.mc.applyColor("&9Aliases: " + "&b" + ho.getAliasesString()));
		}
		sis.displayInfo(hp, "&9", "&b");
		data.addResponse(L.get("LINE_BREAK"));
		return data;
	}
}
