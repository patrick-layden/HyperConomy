package regalowl.hyperconomy.util;

public class HMob {

	private boolean canPickupItems;
	private SimpleLocation location;
	
	public HMob(SimpleLocation location, boolean canPickupItems) {
		this.canPickupItems = canPickupItems;
		this.location = location;
	}
	
	public SimpleLocation getLocation() {
		return location;
	}
	public boolean canPickupItems() {
		return canPickupItems;
	}
	
}
