package regalowl.hyperconomy.command;


import regalowl.simpledatalib.CommonFunctions;
import regalowl.hyperconomy.HyperEconomy;
import regalowl.hyperconomy.tradeobject.TradeObject;

public class Objectsettings extends BaseCommand implements HyperCommand {

	public Objectsettings() {
		super(false);
	}

	@Override
	public CommandData onCommand(CommandData data) {
		if (!validate(data)) return data;
		try {
			TradeObject ho = null;
			if (args.length == 0) {
				HyperEconomy he = hp.getHyperEconomy();
				ho = he.getHyperObject(hp.getItemInHand());
				if (ho == null) {
					data.addResponse(L.get("OBJECT_NOT_IN_DATABASE"));
					return data;
				}
			} else if (args.length == 1) {
				HyperEconomy he = super.getEconomy();
				String nam = he.fixName(args[0]);
				if (he.objectTest(nam)) {
					ho = he.getHyperObject(nam);
				} else {
	    			data.addResponse(L.get("INVALID_ITEM_NAME"));
	    			return data;
	    		}  
			} else {
				data.addResponse(L.get("ITEMSETTINGS_INVALID"));
				return data;
			}
			int itemsBeforeDynamicPricing = (int) ((ho.getMedian() * ho.getValue())/ho.getStartprice() - ho.getTotalStock());
			data.addResponse(L.get("LINE_BREAK"));
			data.addResponse(L.f(L.get("SETTINGS_NAME"), ho.getName()));
			data.addResponse(L.f(L.get("SETTINGS_DISPLAY"), ho.getDisplayName()));
			data.addResponse(L.f(L.get("SETTINGS_ALIAS"), ho.getAliasesString()));
			data.addResponse(L.f(L.get("SETTINGS_VALUE"), ho.getValue()));
			data.addResponse(L.f(L.get("SETTINGS_STARTPRICE"), ho.getStartprice(), Boolean.parseBoolean(ho.getInitiation())));
			data.addResponse(L.f(L.get("SETTINGS_STATICPRICE"), ho.getStaticprice(), Boolean.parseBoolean(ho.getIsstatic())));
			data.addResponse(L.f(L.get("SETTINGS_STOCK"), CommonFunctions.round(ho.getStock(), 3)));
			data.addResponse(L.f(L.get("SETTINGS_TOTAL_STOCK"), CommonFunctions.round(ho.getTotalStock(),3)));
			data.addResponse(L.f(L.get("SETTINGS_MEDIAN"), ho.getMedian()));
			data.addResponse(L.f(L.get("SETTINGS_CEILING"), ho.getCeiling()));
			data.addResponse(L.f(L.get("SETTINGS_FLOOR"), ho.getFloor()));
			data.addResponse(L.f(L.get("SETTINGS_REACH_HYPERBOLIC"), itemsBeforeDynamicPricing));
			data.addResponse(L.f(L.get("SETTINGS_TYPE"), ho.getClass().getSimpleName()));
			data.addResponse(L.get("LINE_BREAK"));
		} catch (Exception e) {
			data.addResponse(L.get("ITEMSETTINGS_INVALID"));
		}
		return data;
	}
}
