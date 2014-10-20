package regalowl.hyperconomy.command;


import regalowl.hyperconomy.HyperEconomy;

import regalowl.hyperconomy.hyperobject.HyperObject;

public class Objectsettings extends BaseCommand implements HyperCommand {

	public Objectsettings() {
		super(false);
	}

	@Override
	public CommandData onCommand(CommandData data) {
		if (!validate(data)) return data;
		try {
			if (args.length == 0) {
				HyperEconomy he = hp.getHyperEconomy();
				HyperObject hob = he.getHyperObject(hp.getItemInHand());
				if (hob == null) {
					data.addResponse(L.get("OBJECT_NOT_IN_DATABASE"));
				} else {
					String nam = hob.getName();
					double val = 0;
					boolean stat = false;
					double statprice = 0;
					double sto = 0;
					double med = 0;
					boolean init = false;
					double starprice = -0;
					HyperObject ho = he.getHyperObject(nam);
					val = ho.getValue();
					stat = Boolean.parseBoolean(ho.getIsstatic());
					statprice = ho.getStaticprice();
					sto = hc.gCF().round(ho.getStock(), 3);
					double tsto = hc.gCF().round(ho.getTotalStock(),3);
					med = ho.getMedian();
					init = Boolean.parseBoolean(ho.getInitiation());
					starprice = ho.getStartprice();
					double totalstock = ((med * val)/starprice);
					int maxinitialitems = 0;		
					double roundedtotalstock = Math.rint( totalstock * 1.0d ) / 1.0d;
					maxinitialitems = (int) (roundedtotalstock - sto);
					double ceiling = ho.getCeiling();
					double floor = ho.getFloor();
					String objectType = ho.getClass().getSimpleName();
					data.addResponse(L.get("LINE_BREAK"));
					data.addResponse(L.f(L.get("SETTINGS_NAME"), nam));
					data.addResponse(L.f(L.get("SETTINGS_DISPLAY"), ho.getDisplayName()));
					data.addResponse(L.f(L.get("SETTINGS_ALIAS"), ho.getAliasesString()));
					data.addResponse(L.f(L.get("SETTINGS_VALUE"), val));
					data.addResponse(L.f(L.get("SETTINGS_STARTPRICE"), starprice, init));
					data.addResponse(L.f(L.get("SETTINGS_STATICPRICE"), statprice, stat));
					data.addResponse(L.f(L.get("SETTINGS_STOCK"), sto));
					data.addResponse(L.f(L.get("SETTINGS_TOTAL_STOCK"), tsto));
					data.addResponse(L.f(L.get("SETTINGS_MEDIAN"), med));
					data.addResponse(L.f(L.get("SETTINGS_CEILING"), ceiling));
					data.addResponse(L.f(L.get("SETTINGS_FLOOR"), floor));
					data.addResponse(L.f(L.get("SETTINGS_REACH_HYPERBOLIC"), maxinitialitems));
					data.addResponse(L.f(L.get("SETTINGS_TYPE"), objectType));
    				data.addResponse(L.get("LINE_BREAK"));
				}
			} else if (args.length == 1) {
				HyperEconomy he = super.getEconomy();
				String nam = he.fixName(args[0]);
				if (he.objectTest(nam)) {
					double val = 0;
					boolean stat = false;
					double statprice = 0;
					double sto = 0;
					double med = 0;
					boolean init = false;
					double starprice = 0;
					HyperObject ho = he.getHyperObject(nam);
					val = ho.getValue();
					stat = Boolean.parseBoolean(ho.getIsstatic());
					statprice = ho.getStaticprice();
					sto = hc.gCF().round(ho.getStock(), 3);
					double tsto = hc.gCF().round(ho.getTotalStock(),3);
					med = ho.getMedian();
					init = Boolean.parseBoolean(ho.getInitiation());
					starprice = ho.getStartprice();			
					double totalstock = ((med * val)/starprice);
					int maxinitialitems = 0;
					double roundedtotalstock = Math.rint( totalstock * 1.0d ) / 1.0d;
					maxinitialitems = (int) (roundedtotalstock - sto);
					double ceiling = ho.getCeiling();
					double floor = ho.getFloor();
					String objectType = ho.getClass().getSimpleName();
					data.addResponse(L.get("LINE_BREAK"));
					data.addResponse(L.f(L.get("SETTINGS_NAME"), nam));
					data.addResponse(L.f(L.get("SETTINGS_DISPLAY"), ho.getDisplayName()));
					data.addResponse(L.f(L.get("SETTINGS_ALIAS"), ho.getAliasesString()));
					data.addResponse(L.f(L.get("SETTINGS_VALUE"), val));
					data.addResponse(L.f(L.get("SETTINGS_STARTPRICE"), starprice, init));
					data.addResponse(L.f(L.get("SETTINGS_STATICPRICE"), statprice, stat));
					data.addResponse(L.f(L.get("SETTINGS_STOCK"), sto));
					data.addResponse(L.f(L.get("SETTINGS_TOTAL_STOCK"), tsto));
					data.addResponse(L.f(L.get("SETTINGS_MEDIAN"), med));
					data.addResponse(L.f(L.get("SETTINGS_CEILING"), ceiling));
					data.addResponse(L.f(L.get("SETTINGS_FLOOR"), floor));
					data.addResponse(L.f(L.get("SETTINGS_REACH_HYPERBOLIC"), maxinitialitems));
					data.addResponse(L.f(L.get("SETTINGS_TYPE"), objectType));
    				data.addResponse(L.get("LINE_BREAK"));
				} else {
	    			data.addResponse(L.get("INVALID_ITEM_NAME"));
	    		}  
			} else {
				data.addResponse(L.get("ITEMSETTINGS_INVALID"));
			}
		} catch (Exception e) {
			data.addResponse(L.get("ITEMSETTINGS_INVALID"));
		}
		return data;
	}
}
