package regalowl.hyperconomy.minecraft;

import regalowl.hyperconomy.HC;
import regalowl.hyperconomy.inventory.HItemStack;

public class HItem {

	private int id;
	private HLocation location;
	private HItemStack item;
	
	public HItem(HLocation location, int id, HItemStack item) {
		this.id = id;
		this.location = location;
		this.item = item;
	}
	
	public int getId() {
		return id;
	}
	public HLocation getLocation() {
		return location;
	}
	public HItemStack getItem() {
		return item;
	}
	public void remove() {
		HC.mc.removeItem(this);
	}
	
	
}
