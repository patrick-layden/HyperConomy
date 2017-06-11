package regalowl.hyperconomy.minecraft;

import java.io.Serializable;


public class HMob implements Serializable {

	private static final long serialVersionUID = -8536395776719431112L;
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
