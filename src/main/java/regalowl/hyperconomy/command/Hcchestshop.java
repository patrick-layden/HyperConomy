package regalowl.hyperconomy.command;

import java.util.ArrayList;

import regalowl.hyperconomy.HyperConomy;
import regalowl.hyperconomy.account.HyperAccount;
import regalowl.hyperconomy.minecraft.HLocation;
import regalowl.hyperconomy.minecraft.HSign;
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
    				for (ChestShop cs:hc.getDataManager().getChestShopHandler().getChestShops(hp)) {
    					chestLocs.add(cs.getChestLocation().toReadableString());
    				}
    				data.addResponse(CommonFunctions.implode(chestLocs, ","));
    			} else if (args[0].equalsIgnoreCase("removeall")) {
    				if (args.length >= 2 && args[1].equalsIgnoreCase("confirm")) {
        				for (ChestShop cs:hc.getDataManager().getChestShopHandler().getChestShops(hp)) {
        					hc.getDataManager().getChestShopHandler().deleteChestShop(cs);
        				}
        				data.addResponse(L.get("HCCHESTSHOP_DELETED"));
    				} else {
    					data.addResponse(L.get("CHESTSHOP_DELETE_CONFIRM"));
    				}
    			} else if (args[0].equalsIgnoreCase("setline") || args[0].equalsIgnoreCase("sl")) {
    				if (args.length != 3 ) {
    					data.addResponse(L.get("HCCHESTSHOP_SETLINE_INVALID"));
    					return data;
    				}
    				int line = 0;
    				try {
    					line = Integer.parseInt(args[1]);
    				} catch (Exception e) {
    					data.addResponse(L.get("HCCHESTSHOP_SETLINE_INVALID"));
    					return data;
    				}
    				String text = args[2];
    				HLocation l = hp.getTargetLocation();
    				ChestShop cs = hc.getDataManager().getChestShopHandler().getChestShopFromAnyPart(l);
    				if (cs == null) {
    					data.addResponse(L.get("LOOK_AT_VALID_CHESTSHOP"));
    					return data;
    				}	
    				if (!cs.getOwner().equals(hp) && !hp.hasPermission("hyperconomy.admin")) {
    					data.addResponse(L.get("HCCHESTSHOP_NOT_OWNER"));
    					return data;
    				}
    				HSign s = cs.getSign();
    				s.setLine(line, text);
    				s.update();
    				data.addResponse(L.get("HCCHESTSHOP_SETLINE_UPDATED"));
    			} else if (args[0].equalsIgnoreCase("setowner") || args[0].equalsIgnoreCase("so")) {
    				if (!hp.hasPermission("hyperconomy.admin")) {
    					data.addResponse(L.get("YOU_DONT_HAVE_PERMISSION"));
    					return data;
    				}
    				if (args.length != 2) {
    					data.addResponse(L.get("HCCHESTSHOP_SETOWNER_INVALID"));
    					return data;
    				}
    				String name = args[1];
    				if (!hc.getDataManager().accountExists(name)) {
    					data.addResponse(L.get("ACCOUNT_NOT_EXIST"));
    					return data;
    				}
    				HyperAccount account = hc.getDataManager().getAccount(name);
    				HLocation l = hp.getTargetLocation();
    				ChestShop cs = hc.getDataManager().getChestShopHandler().getChestShopFromAnyPart(l);
    				if (cs == null) {
    					data.addResponse(L.get("LOOK_AT_VALID_CHESTSHOP"));
    					return data;
    				}	
    				cs.setOwner(account);
    				data.addResponse(L.get("CHEST_OWNER_UPDATED"));
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
