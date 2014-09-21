package regalowl.hyperconomy.event;


import regalowl.hyperconomy.shop.Shop;

public interface ShopModificationListener extends HyperListener {
	public void onShopModification(Shop s);
}