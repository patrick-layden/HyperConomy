package regalowl.hyperconomy.display;

import regalowl.hyperconomy.minecraft.HLocation;
import regalowl.hyperconomy.shop.Shop;
import regalowl.hyperconomy.tradeobject.TradeObject;

public interface FrameShopHandler {
	boolean frameShopExists(HLocation l);
	void removeFrameShop(HLocation l);
	void createFrameShop(HLocation l, TradeObject to, Shop s);
	void removeFrameShops(TradeObject to);
}
