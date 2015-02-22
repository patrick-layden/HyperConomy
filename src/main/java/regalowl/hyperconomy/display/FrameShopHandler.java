package regalowl.hyperconomy.display;

import regalowl.hyperconomy.minecraft.HLocation;
import regalowl.hyperconomy.shop.Shop;
import regalowl.hyperconomy.tradeobject.TradeObject;

public interface FrameShopHandler {
	public boolean frameShopExists(HLocation l);
	public void removeFrameShop(HLocation l);
	public void createFrameShop(HLocation l, TradeObject to, Shop s);
	public void removeFrameShops(TradeObject to);
}
