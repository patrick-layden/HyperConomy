package regalowl.hyperconomy.command;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import regalowl.simpledatalib.CommonFunctions;
import regalowl.hyperconomy.HyperConomy;
import regalowl.hyperconomy.HyperEconomy;
import regalowl.hyperconomy.shop.PlayerShop;
import regalowl.hyperconomy.shop.Shop;
import regalowl.hyperconomy.tradeobject.TradeObject;
import regalowl.hyperconomy.tradeobject.TradeObjectStatus;


public class Topitems extends BaseCommand implements HyperCommand {

	private final int numberPerPage = 10;
	
	public Topitems(HyperConomy hc) {
		super(hc, false);
	}

	@Override
	public CommandData onCommand(CommandData data) {
		if (!validate(data)) return data;
		HyperEconomy he = super.getEconomy();
		boolean requireShop = hc.getConf().getBoolean("shop.limit-info-commands-to-shops");
		if (args.length > 1) {
			data.addResponse(L.get("TOPITEMS_INVALID"));
			return data;
		}
		Shop s = dm.getHyperShopManager().getShop(hp);
		boolean hasPlayer = (hp != null) ? true:false;
		boolean hasShop = (s != null) ? true:false;
		if (hasPlayer && requireShop && !hasShop && !hp.hasPermission("hyperconomy.admin")) {
			data.addResponse(L.get("REQUIRE_SHOP_FOR_INFO"));
			return data;
		}
		ArrayList<TradeObject> allObjects = he.getTradeObjects(s);
		Collections.sort(allObjects, new Comparator<TradeObject>(){
		    public int compare(TradeObject to1, TradeObject to2) {
		    	Double s1 = to1.getStock();
		    	Double s2 = to2.getStock();
		    	if (s1 < s2) return 1;
		    	if (s1 > s2) return -1;
		    	return 0;
		    }
		});
		ArrayList<TradeObject> displayObjects = new ArrayList<TradeObject>();
		for (TradeObject to:allObjects) {
		    if (to.getStock() == 0.0) continue;
		    if (hasShop && s.isBanned(to)) continue;
		    if (hasShop && to.isShopObject()) {
		    	PlayerShop ps = (PlayerShop)s;
		    	if (!ps.isAllowed(hp) && !hp.hasPermission("hyperconomy.admin")) {
		    		if (to.getShopObjectStatus() == TradeObjectStatus.NONE) continue;
		    	}
		    }
		    displayObjects.add(to);
		}
		int selectedPage = (args.length == 0) ? 1 : Integer.parseInt(args[0]);
		if (selectedPage < 1) selectedPage = 1;
		int startIndex = (selectedPage - 1) * numberPerPage;
		int endIndex = startIndex + numberPerPage;
		int numberOfPages = displayObjects.size()/numberPerPage + 1;
		data.addResponse(L.f(L.get("PAGE_NUMBER"), selectedPage, numberOfPages));
		for (int i = startIndex; i < endIndex; i++) {
			if (i >= displayObjects.size()) {
				data.addResponse(L.get("YOU_HAVE_REACHED_THE_END"));
				break;
			}
			TradeObject to = displayObjects.get(i);
			if (to.isShopObject()) {
				data.addResponse("&f"+to.getDisplayName() + ": &a" + CommonFunctions.twoDecimals(to.getStock()) + " &f(&e" + to.getShopObjectStatus().toString() + "&f)" );
			} else {
				data.addResponse("&f" + to.getDisplayName() + "&f: " + "&b" + CommonFunctions.twoDecimals(to.getStock()));
			}
		}
		return data;
	}
}
