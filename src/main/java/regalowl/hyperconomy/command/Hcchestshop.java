package regalowl.hyperconomy.command;

import java.util.ArrayList;

import regalowl.hyperconomy.HyperConomy;
import regalowl.hyperconomy.shop.ChestShop;
import regalowl.simpledatalib.CommonFunctions;



public class Hcchestshop extends BaseCommand implements HyperCommand {
	
	
	public Hcchestshop(HyperConomy hc) {
		super(hc, true);
	}

	@Override
	public CommandData onCommand(CommandData data) {
		if (!validate(data)) return data;
		try {
			if (!hp.hasPermission("hyperconomy.chestshop")) {
				data.addResponse(L.get("CHESTSHOP_NO_PERMISSION"));
				return data;
			}
			if (args.length == 0) {
				data.addResponse(L.get("HCCHESTSHOP_INVALID"));
    		} else if (args.length >= 1) {
    			if (args[0].equalsIgnoreCase("list")) {
    				ArrayList<String> chestLocs = new ArrayList<String>();
    				for (ChestShop cs:hc.getChestShopHandler().getChestShops(hp)) {
    					chestLocs.add(cs.getChestLocation().toReadableString());
    				}
    				data.addResponse(CommonFunctions.implode(chestLocs, ","));
    			} else if (args[0].equalsIgnoreCase("removeall")) {
    				if (args.length >= 2 && args[1].equalsIgnoreCase("confirm")) {
        				for (ChestShop cs:hc.getChestShopHandler().getChestShops(hp)) {
        					hc.getChestShopHandler().deleteChestShop(cs);
        				}
        				data.addResponse(L.get("HCCHESTSHOP_DELETED"));
    				} else {
    					data.addResponse(L.get("CHESTSHOP_DELETE_CONFIRM"));
    				}
    			} else {
    				data.addResponse(L.get("HCCHESTSHOP_INVALID"));
    			}
    		}
		} catch (Exception e) {
			hc.gSDL().getErrorWriter().writeError(e);
		}
		return data;
	}
}
