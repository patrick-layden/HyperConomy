package regalowl.hyperconomy.command;



import regalowl.hyperconomy.HyperEconomy;
import regalowl.hyperconomy.hyperobject.HyperObject;
import regalowl.hyperconomy.shop.FrameShopHandler;
import regalowl.hyperconomy.shop.PlayerShop;
import regalowl.hyperconomy.shop.Shop;
import regalowl.hyperconomy.util.SimpleLocation;

public class Frameshopcommand extends BaseCommand implements HyperCommand {
	public Frameshopcommand() {
		super(true);
	}

	@Override
	public CommandData onCommand(CommandData data) {
		if (!validate(data)) return data;
		FrameShopHandler fsh = hc.getFrameShopHandler();
		HyperEconomy he = getEconomy();
		if (args.length == 1) {
			SimpleLocation bl = hp.getLocationBeforeTargetLocation();
			HyperObject ho = he.getHyperObject(args[0]);
			if (ho != null) {
				if (hc.getHyperShopManager().inAnyShop(hp)) {
					Shop s = hc.getHyperShopManager().getShop(hp);
					if (s instanceof PlayerShop) {
						PlayerShop ps = (PlayerShop) s;
						if (hp.hasPermission("hyperconomy.admin") || ps.isAllowed(hp)) {
							fsh.createFrameShop(bl, ho, ps);
						} else {
							data.addResponse("You don't have permission to create a frameshop here.");
						}
					} else {
						if (hp.hasPermission("hyperconomy.admin")) {
							fsh.createFrameShop(bl, ho, s);
						} else {
							data.addResponse("You don't have permission to create a frameshop here.");
						}
					}
				} else {
					if (hp.hasPermission("hyperconomy.admin")) {
						fsh.createFrameShop(bl, ho, null);
					} else {
						data.addResponse("You don't have permission to create a frameshop here.");
					}
				}
			} else {
				data.addResponse(L.get("INVALID_ITEM_NAME"));
			}
		} else {
			data.addResponse(L.get("MAKEFRAMESHOP_INVALID"));
		}
		return data;
	}
}
