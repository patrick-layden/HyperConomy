package regalowl.hyperconomy.event.minecraft;

import regalowl.hyperconomy.account.HyperPlayer;
import regalowl.hyperconomy.event.HyperEvent;
import regalowl.hyperconomy.shop.ChestShop;

public class ChestShopOpenEvent extends HyperEvent {

	private HyperPlayer opener;
	private ChestShop chestShop;
	
	public ChestShopOpenEvent(HyperPlayer clicker, ChestShop chestShop) {
		this.opener = clicker;
		this.chestShop = chestShop;
	}

	public HyperPlayer getOpener() {
		return opener;
	}
	public ChestShop getChestShop() {
		return chestShop;
	}

	
}
