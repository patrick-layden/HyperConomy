package regalowl.hyperconomy.event.minecraft;

import regalowl.databukkit.event.Event;
import regalowl.hyperconomy.util.SimpleLocation;

public class HBlockBreakEvent extends Event {

	private SimpleLocation location;
	private boolean isChestShopChest;
	private boolean isChestShopBlock;
	private boolean isTransactionSign;
	private boolean isInfoSign;
	
	
	public HBlockBreakEvent(SimpleLocation location) {
		this.location = location;
	}


	public SimpleLocation getLocation() {
		return location;
	}
	public boolean isChestShopChest() {
		return isChestShopChest;
	}
	public boolean isChestShopBlock() {
		return isChestShopBlock;
	}
	public boolean isTransactionSign() {
		return isTransactionSign;
	}
	public boolean isInfoSign() {
		return isInfoSign;
	}



	public void setChestShopChest() {
		this.isChestShopChest = true;
	}
	public void setChestShopBlock() {
		this.isChestShopBlock = true;
	}
	public void setTransactionSign() {
		this.isTransactionSign = true;
	}
	public void setInfoSign() {
		this.isInfoSign = true;
	}
	
	
	
}
