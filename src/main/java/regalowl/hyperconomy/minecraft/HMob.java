package regalowl.hyperconomy.minecraft;


public class HMob {

	private boolean canPickupItems;
	private HLocation location;
	
	public HMob(HLocation location, boolean canPickupItems) {
		this.canPickupItems = canPickupItems;
		this.location = location;
	}
	
	public HLocation getLocation() {
		return location;
	}
	public boolean canPickupItems() {
		return canPickupItems;
	}
	
}
