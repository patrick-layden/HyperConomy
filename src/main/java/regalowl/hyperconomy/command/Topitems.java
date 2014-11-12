package regalowl.hyperconomy.command;

import java.util.ArrayList;
import java.util.SortedMap;
import java.util.TreeMap;

import regalowl.simpledatalib.CommonFunctions;
import regalowl.hyperconomy.HyperEconomy;
import regalowl.hyperconomy.shop.PlayerShop;
import regalowl.hyperconomy.shop.Shop;
import regalowl.hyperconomy.tradeobject.TradeObject;


public class Topitems extends BaseCommand implements HyperCommand {


	public Topitems() {
		super(false);
	}

	@Override
	public CommandData onCommand(CommandData data) {
		if (!validate(data)) return data;
		HyperEconomy he = super.getEconomy();
		try {
			boolean requireShop = hc.getConf().getBoolean("shop.limit-info-commands-to-shops");
			if (args.length > 1) {
				data.addResponse(L.get("TOPITEMS_INVALID"));
				return data;
			}
			Shop s = null;
			if (hp != null) {
				if (dm.getHyperShopManager().inAnyShop(hp)) {
					s = dm.getHyperShopManager().getShop(hp);
				} 
				if (requireShop && dm.getHyperShopManager().getShop(hp) == null && !hp.hasPermission("hyperconomy.admin")) {
					data.addResponse(L.get("REQUIRE_SHOP_FOR_INFO"));
					return data;
				}
			}
			int page;
			if (args.length == 0) {
				page = 1;
			} else {
				page = Integer.parseInt(args[0]);
			}
			SortedMap<Double, TradeObject> itemstocks = new TreeMap<Double, TradeObject>();
			ArrayList<TradeObject> objects = null;
			if (s != null) {
				objects = he.getHyperObjects(s);
			} else {
				objects = he.getHyperObjects();
			}
			for (TradeObject ho:objects) {
				boolean stocked = false;
				if (ho.getStock() > 0.0) {stocked = true;}
				boolean banned = false;
				boolean allowed = false;
				if (s != null) {
					banned = s.isBanned(ho);
					if (ho.isShopObject()) {
						if (s instanceof PlayerShop) {
							PlayerShop ps = (PlayerShop)s;
							allowed = ps.isAllowed(hp);
						}
					}
					if ((!banned && stocked) || (allowed && stocked)) {
						double samount = ho.getStock();
						while (itemstocks.containsKey(samount)) {
							samount += .00001;
						}
						itemstocks.put(samount, ho);
					}
				} else {
					double samount = ho.getStock();
					if (samount > 0) {
						while (itemstocks.containsKey(samount)) {
							samount += .00001;
						}
						itemstocks.put(samount, ho);
					}
				}
			}
			int numberpage = page * 10;
			int count = 0;
			int le = itemstocks.size();
			double maxpages = le / 10;
			maxpages = Math.ceil(maxpages);
			int maxpi = (int) maxpages + 1;
			data.addResponse(L.f(L.get("PAGE_NUMBER"), page, maxpi));
			try {
				while (count < numberpage) {
					double lk = itemstocks.lastKey();
					if (count > ((page * 10) - 11)) {
						TradeObject ho = itemstocks.get(lk);
						if (ho.isShopObject()) {
							data.addResponse("&f"+ho.getDisplayName() + ": &a" + CommonFunctions.twoDecimals(ho.getStock()) + " &f(&e" + ho.getStatus().toString() + "&f)" );
						} else {
							data.addResponse("&f" + ho.getDisplayName() + "&f: " + "&b" + CommonFunctions.twoDecimals(ho.getStock()));
						}
					}
					itemstocks.remove(lk);
					count++;
				}
			} catch (Exception e) {
				data.addResponse(L.get("YOU_HAVE_REACHED_THE_END"));
			}
		} catch (Exception e) {
			data.addResponse(L.get("TOPITEMS_INVALID"));
		}
		return data;
	}
}
