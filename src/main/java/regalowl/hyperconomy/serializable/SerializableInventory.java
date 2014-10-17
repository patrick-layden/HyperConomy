package regalowl.hyperconomy.serializable;

import java.io.Serializable;
import java.util.ArrayList;

public class SerializableInventory extends SerializableObject implements Serializable {

	private static final long serialVersionUID = 4247823594626362499L;
	private ArrayList<SerializableItemStack> items = new ArrayList<SerializableItemStack>();
	private int heldSlot;

	public SerializableInventory(ArrayList<SerializableItemStack> items, int heldSlot) {
		this.items.addAll(items);
		this.heldSlot = heldSlot;
	}
	
	public ArrayList<SerializableItemStack> getItems() {
		return items;
	}
	
	public int getHeldSlot() {
		return heldSlot;
	}
}
