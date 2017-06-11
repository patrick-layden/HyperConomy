package regalowl.hyperconomy.command;

import regalowl.hyperconomy.HyperConomy;
import regalowl.simpledatalib.CommonFunctions;

public class Listcategories extends BaseCommand implements HyperCommand {


	public Listcategories(HyperConomy hc) {
		super(hc, false);
	}

	@Override
	public CommandData onCommand(CommandData data) {
		if (!validate(data)) return data;
		try {
			data.addResponse("&b" + CommonFunctions.implode(dm.getCategories()));
			return data;
		} catch (Exception e) {
			data.addResponse(L.get("LISTCATEGORIES_INVALID"));
			return data;
		}
	}
}
