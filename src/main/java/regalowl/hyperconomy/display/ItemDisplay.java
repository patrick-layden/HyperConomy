package regalowl.hyperconomy.display;


import java.util.HashMap;



import regalowl.hyperconomy.HyperConomy;
import regalowl.hyperconomy.HyperEconomy;
import regalowl.hyperconomy.hyperobject.HyperObject;
import regalowl.hyperconomy.serializable.SerializableItemStack;
import regalowl.hyperconomy.util.HBlock;
import regalowl.hyperconomy.util.HItem;
import regalowl.hyperconomy.util.HMob;
import regalowl.hyperconomy.util.SimpleLocation;

public class ItemDisplay {
	
	private HyperConomy hc;
	private String name;
	private SimpleLocation l;
	private HItem item;
	private boolean active;
	
	ItemDisplay(SimpleLocation location, String name, boolean newDisplay) {
		this.hc = HyperConomy.hc;
		this.active = false;
		HyperEconomy he = hc.getDataManager().getEconomy("default");
		this.l = location;
		this.name = he.fixName(name);
		if (newDisplay) {
			HashMap<String,String> values = new HashMap<String,String>();
			values.put("WORLD", location.getWorld());
			values.put("X", location.getX()+"");
			values.put("Y", location.getY()+"");
			values.put("Z", location.getZ()+"");
			values.put("HYPEROBJECT", name);
			hc.getSQLWrite().performInsert("hyperconomy_item_displays", values);
		}
	}
	
	public boolean isActive() {
		return active;
	}
	
	public HItem getItem() {
		return item;	
	}
	
	public HBlock getBaseBlock() {
		int x = (int) Math.floor(l.getX());
		int y = (int) Math.floor(l.getY() - 1);
		int z = (int) Math.floor(l.getZ());
		return new HBlock(new SimpleLocation(l.getWorld(), x, y, z));
	}
	
	public HBlock getItemBlock() {
		int x = (int) Math.floor(l.getX());
		int y = (int) Math.floor(l.getY());
		int z = (int) Math.floor(l.getZ());
		return new HBlock(new SimpleLocation(l.getWorld(), x, y, z));
	}
	
	public SimpleLocation getLocation() {
		return l;
	}
	
	/*
	public Chunk getChunk() {
		return getLocation().getChunk();
	}
	*/
	public String getName() {
		return name;
	}
	
	public double getX() {
		return l.getX();
	}
	
	public double getY() {
		return l.getY();
	}
	
	public double getZ() {
		return l.getZ();
	}
	
	public String getWorld() {
		return l.getWorld();
	}
	
	public int getEntityId() {
		if (item == null) return -1;
		return item.getId();
	}
	
	public HyperObject getHyperObject() {
		return hc.getDataManager().getDefaultEconomy().getHyperObject(name);
	}
	
	public void makeDisplay() {
		if (!getLocation().isLoaded()) {return;}
		HyperEconomy he = hc.getDataManager().getEconomy("default");
		SerializableItemStack dropstack = he.getHyperObject(name).getItem();
		this.item = HyperConomy.mc.dropItemDisplay(l, dropstack);
		active = true;
	}
	
	public void refresh() {
		removeItem();
		makeDisplay();
	}
	

	public void removeItem() {
		l.load();
		if (item != null) {
			item.remove();
		}
		clearNearbyItems(.5,true,true);
		active = false;
	}
	
	public void delete() {
		HashMap<String,String> conditions = new HashMap<String,String>();
		conditions.put("WORLD", l.getWorld());
		conditions.put("X", l.getX()+"");
		conditions.put("Y", l.getY()+"");
		conditions.put("Z", l.getZ()+"");
		hc.getSQLWrite().performDelete("hyperconomy_item_displays", conditions);
		clear();
	}
	
	public void clear() {
		removeItem();
		hc = null;
		l = null;
		name = null;
	}
	
	
	/**
	 *
	 * @param droppedItem
	 * @return true if the item drop event shop be blocked to prevent item stacking, false if not
	 */
	public boolean blockItemDrop(HItem droppedItem) {
		if (droppedItem == null) {return false;}
		SerializableItemStack displayStack = item.getItem();
		SimpleLocation dl = droppedItem.getLocation();
		if (!displayStack.equals(droppedItem.getItem())) return false;
		if (!l.getWorld().equals(getWorld())) return false;
		if (Math.abs(dl.getX() - l.getX()) > 10) return false;
		if (Math.abs(dl.getZ() - l.getZ()) > 10) return false;
		if (Math.abs(dl.getY() - l.getY()) > 30) return false;
		HyperConomy.mc.zeroVelocity(droppedItem);
		if (HyperConomy.mc.getFirstNonAirBlockInColumn(dl).getLocation().getY() > (l.getY() + 10)) return false;
		return true;
	}
	
	
	public boolean blockEntityPickup(HMob mob) {
		if (mob.canPickupItems()) {
			SimpleLocation el = mob.getLocation();	
			if (el.getWorld().equals(l.getWorld())) {
				if (Math.abs(el.getX() - l.getX()) < 500) {
					if (Math.abs(el.getZ() - l.getZ()) < 500) {
						return true;
					}
				}
			}
		}
		return false;
	}
	

	public void clearNearbyItems(double radius, boolean removeDisplays, boolean removeSelf) {
		HyperConomy.mc.clearNearbyNonDisplayItems(item, radius);
	}

}
