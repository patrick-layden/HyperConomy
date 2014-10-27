package regalowl.hyperconomy.util;

import regalowl.hyperconomy.HyperConomy;



public class HBlock {

	private SimpleLocation location;
	
	
	public HBlock(SimpleLocation location) {
		this.location = location;
	}

	public SimpleLocation getLocation() {
		return location;
	}
	
	public boolean isChest() {
		return HyperConomy.mc.isChest(location);
	}
	
	public boolean canHoldChestShopSign() {
		return HyperConomy.mc.canHoldChestShopSign(location);
	}
	
}
