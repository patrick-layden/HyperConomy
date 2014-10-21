package regalowl.hyperconomy.shop;

import regalowl.hyperconomy.HyperConomy;
import regalowl.hyperconomy.serializable.SerializableInventory;
import regalowl.hyperconomy.util.SimpleLocation;

public class ChestShop {

	private SimpleLocation location;
	
	public ChestShop(SimpleLocation location) {
		this.location = location;
	}
	
	public SerializableInventory getInventory() {
		return HyperConomy.hc.getMC().getChestInventory(location);
	}
	
}
