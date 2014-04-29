package regalowl.hyperconomy.event;

import regalowl.hyperconomy.shop.Shop;

public interface ShopCreationListener extends HyperListener {
	public void onShopCreation(Shop s);
}