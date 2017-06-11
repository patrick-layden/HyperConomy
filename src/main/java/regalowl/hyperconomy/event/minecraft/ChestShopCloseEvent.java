package regalowl.hyperconomy.event.minecraft;

import regalowl.hyperconomy.account.HyperPlayer;
import regalowl.hyperconomy.event.HyperEvent;
import regalowl.hyperconomy.shop.ChestShop;

public class ChestShopCloseEvent extends HyperEvent {

	private HyperPlayer closer;
	private ChestShop chestShop;
	
	public ChestShopCloseEvent(HyperPlayer clicker, ChestShop chestShop) {
		this.closer = clicker;
		this.chestShop = chestShop;
	}

	public HyperPlayer getCloser() {
		return closer;
	}
	public ChestShop getChestShop() {
		return chestShop;
	}

	
}
