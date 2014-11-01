package regalowl.hyperconomy.command;

import java.util.SortedMap;
import java.util.TreeMap;




import regalowl.hyperconomy.HC;
import regalowl.hyperconomy.HyperEconomy;
import regalowl.hyperconomy.tradeobject.TradeObject;
import regalowl.hyperconomy.tradeobject.TradeObjectStatus;
import regalowl.hyperconomy.tradeobject.TradeObjectType;


public class Topenchants extends BaseCommand implements HyperCommand {

	public Topenchants() {
		super(false);
	}

	@Override
	public CommandData onCommand(CommandData data) {
		if (!validate(data)) return data;
		HyperEconomy he = super.getEconomy();
		try {
			boolean requireShop = hc.getConf().getBoolean("shop.limit-info-commands-to-shops");
			if (args.length > 1) {
				data.addResponse(L.get("TOPENCHANTS_INVALID"));
				return data;
			}
			String nameshop = "";
			if (hp != null) {
				if (dm.getHyperShopManager().inAnyShop(hp)) {
					nameshop = dm.getHyperShopManager().getShop(hp).getName();
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
			SortedMap<Double, String> enchantstocks = new TreeMap<Double, String>();
			for (TradeObject ho:he.getHyperObjects()) {
				if (!(ho.getType() == TradeObjectType.ENCHANTMENT)) {continue;}
				boolean allowed = false;
				boolean stocked = false;
				boolean banned = false;
				if (nameshop != "") {
					banned = dm.getHyperShopManager().getShop(nameshop).isBanned(ho);
				}
				if (ho.getStock() > 0) {stocked = true;}
				if (ho.isShopObject()) {
					allowed = ho.getShop().isAllowed(hp);
					if (ho.getStatus() == TradeObjectStatus.NONE && !allowed) {
						continue;
					}
				}
				boolean unavailable = false;
				if (nameshop != "") {
					if (banned && !(allowed && stocked)) {
						unavailable = true;
					}
				}
				if (!unavailable) {
					double samount = he.getHyperObject(ho.getName(), dm.getHyperShopManager().getShop(hp)).getStock();
					if (samount > 0) {
						while (enchantstocks.containsKey(samount * 100)) {
							samount += .00001;
						}
						enchantstocks.put(samount * 100, ho.getDisplayName());
					}
				}
			}
			int numberpage = page * 10;
			int count = 0;
			int le = enchantstocks.size();
			double maxpages = le / 10;
			maxpages = Math.ceil(maxpages);
			int maxpi = (int) maxpages + 1;
			data.addResponse(L.f(L.get("PAGE_NUMBER"), page, maxpi));
			try {
				while (count < numberpage) {
					double lk = enchantstocks.lastKey();
					if (count > ((page * 10) - 11)) {
						data.addResponse(HC.mc.applyColor("&f" + enchantstocks.get(lk) + "&f: " + "&b" + Math.floor(lk)/100));
					}
					enchantstocks.remove(lk);
					count++;
				}
			} catch (Exception e) {
				data.addResponse(L.get("YOU_HAVE_REACHED_THE_END"));
			}
		} catch (Exception e) {
			data.addResponse(L.get("TOPENCHANTS_INVALID"));
		}
		return data;
	}
}
