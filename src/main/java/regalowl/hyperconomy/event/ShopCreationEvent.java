package regalowl.hyperconomy.event;

import regalowl.hyperconomy.shop.Shop;
import regalowl.simpledatalib.event.Event;

public class ShopCreationEvent extends Event {
	private Shop s;
	
	public ShopCreationEvent(Shop s) {
		this.s = s;
	}
	
	public Shop getShop() {
		return s;
	}
}