package regalowl.hyperconomy.command;



import regalowl.hyperconomy.HyperConomy;
import regalowl.hyperconomy.HyperEconomy;
import regalowl.hyperconomy.display.FrameShopHandler;
import regalowl.hyperconomy.minecraft.HLocation;
import regalowl.hyperconomy.shop.PlayerShop;
import regalowl.hyperconomy.shop.Shop;
import regalowl.hyperconomy.tradeobject.TradeObject;

public class Frameshopcommand extends BaseCommand implements HyperCommand {
	public Frameshopcommand(HyperConomy hc) {
		super(hc, true);
	}

	@Override
	public CommandData onCommand(CommandData data) {
		if (!validate(data)) return data;
		FrameShopHandler fsh = hc.getFrameShopHandler();
		HyperEconomy he = getEconomy();
		if (args.length == 1) {
			HLocation bl = hp.getLocationBeforeTargetLocation();
			TradeObject ho = he.getTradeObject(args[0]);
			if (ho != null) {
				if (hc.getHyperShopManager().inAnyShop(hp)) {
					Shop s = hc.getHyperShopManager().getShop(hp);
					if (s instanceof PlayerShop) {
						PlayerShop ps = (PlayerShop) s;
						if (hp.hasPermission("hyperconomy.admin") || ps.isAllowed(hp)) {
							fsh.createFrameShop(bl, ho, ps);
							data.addResponse("Frameshop created.");
						} else {
							data.addResponse("You don't have permission to create a frameshop here.");
						}
					} else {
						if (hp.hasPermission("hyperconomy.admin")) {
							fsh.createFrameShop(bl, ho, s);
							data.addResponse("Frameshop created.");
						} else {
							data.addResponse("You don't have permission to create a frameshop here.");
						}
					}
				} else {
					if (hp.hasPermission("hyperconomy.admin")) {
						fsh.createFrameShop(bl, ho, null);
						data.addResponse("Frameshop created.");
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
