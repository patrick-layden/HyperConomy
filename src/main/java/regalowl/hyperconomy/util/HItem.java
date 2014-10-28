package regalowl.hyperconomy.util;

public class HItem {

	private int id;
	private SimpleLocation location;
	
	public HItem(SimpleLocation location, int id) {
		this.id = id;
		this.location = location;
	}
	
	public int getId() {
		return id;
	}
	public SimpleLocation getLocation() {
		return location;
	}
	
	
}
