package regalowl.hyperconomy.minecraft;

import regalowl.hyperconomy.HyperConomy;
import regalowl.hyperconomy.inventory.HItemStack;

public class HItem {

	private transient HyperConomy hc;
	
	private int id;
	private HLocation location;
	private HItemStack item;
	
	public HItem(HyperConomy hc, HLocation location, int id, HItemStack item) {
		this.hc = hc;
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
		hc.getMC().removeItem(this);
	}
	
	
}
