package regalowl.hyperconomy.util;

import regalowl.hyperconomy.HyperConomy;
import regalowl.hyperconomy.serializable.SerializableItemStack;

public class HItem {

	private int id;
	private SimpleLocation location;
	private SerializableItemStack item;
	
	public HItem(SimpleLocation location, int id, SerializableItemStack item) {
		this.id = id;
		this.location = location;
		this.item = item;
	}
	
	public int getId() {
		return id;
	}
	public SimpleLocation getLocation() {
		return location;
	}
	public SerializableItemStack getItem() {
		return item;
	}
	public void remove() {
		HyperConomy.mc.removeItem(this);
	}
	
	
}
