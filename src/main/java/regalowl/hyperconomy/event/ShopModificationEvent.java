package regalowl.hyperconomy.event;


import regalowl.simpledatalib.event.Event;
import regalowl.hyperconomy.shop.Shop;

public class ShopModificationEvent extends Event {
	private Shop s;
	
	public ShopModificationEvent(Shop s) {
		this.s = s;
	}
	
	public Shop getShop() {
		return s;
	}
}