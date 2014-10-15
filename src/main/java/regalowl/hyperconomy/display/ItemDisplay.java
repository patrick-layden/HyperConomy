package regalowl.hyperconomy.display;


import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.util.Vector;

import regalowl.hyperconomy.HyperConomy;
import regalowl.hyperconomy.HyperEconomy;
import regalowl.hyperconomy.hyperobject.HyperItemStack;
import regalowl.hyperconomy.hyperobject.HyperObject;
import regalowl.hyperconomy.util.SimpleLocation;

public class ItemDisplay {
	
	private HyperConomy hc;
	private Item item;
	private String name;
	private double x;
	private double y;
	private double z;
	private String w;
	private int entityId;
	private boolean active;
	
	ItemDisplay(SimpleLocation location, String name, boolean newDisplay) {
		this.hc = HyperConomy.hc;
		this.active = false;
		HyperEconomy he = hc.getDataManager().getEconomy("default");
		this.x = location.getX();
		this.y = location.getY();
		this.z = location.getZ();
		this.w = location.getWorld();
		this.name = he.fixName(name);
		if (newDisplay) {
			HashMap<String,String> values = new HashMap<String,String>();
			values.put("WORLD", w);
			values.put("X", x+"");
			values.put("Y", y+"");
			values.put("Z", z+"");
			values.put("HYPEROBJECT", name);
			hc.getSQLWrite().performInsert("hyperconomy_item_displays", values);
		}
	}
	
	public boolean isActive() {
		return active;
	}
	
	public Item getItem() {
		return item;	
	}
	
	public Block getBaseBlock() {
		int x = (int) Math.floor(this.x);
		int y = (int) Math.floor(this.y - 1);
		int z = (int) Math.floor(this.z);
		return getWorld().getBlockAt(x, y, z);
	}
	
	public Block getItemBlock() {
		int x = (int) Math.floor(this.x);
		int y = (int) Math.floor(this.y - 1);
		int z = (int) Math.floor(this.z);
		return getWorld().getBlockAt(x, y+1, z);
	}
	
	public Location getLocation() {
		return new Location(Bukkit.getWorld(w),x,y,z);
	}
	
	public Chunk getChunk() {
		return getLocation().getChunk();
	}
	
	public String getName() {
		return name;
	}
	
	public double getX() {
		return x;
	}
	
	public double getY() {
		return y;
	}
	
	public double getZ() {
		return z;
	}
	
	public World getWorld() {
		return Bukkit.getWorld(w);
	}
	
	public int getEntityId() {
		return entityId;
	}
	
	public HyperObject getHyperObject() {
		return hc.getDataManager().getDefaultEconomy().getHyperObject(name);
	}
	
	public void makeDisplay() {
		if (!getLocation().getChunk().isLoaded()) {return;}
		HyperEconomy he = hc.getDataManager().getEconomy("default");
		Location l = new Location(getWorld(), x, y + 1, z);
		ItemStack dropstack = he.getHyperObject(name).getItemStack();
		dropstack.setDurability((short) he.getHyperObject(name).getItemStack().getDurability());
		this.item = getWorld().dropItem(l, dropstack);
		this.entityId = item.getEntityId();
		item.setVelocity(new Vector(0, 0, 0));
		item.setMetadata("HyperConomy", new FixedMetadataValue(hc.getMC().getConnector(), "item_display"));
		active = true;
	}
	
	public void refresh() {
		removeItem();
		makeDisplay();
	}
	

	public void removeItem() {
		getChunk().load();
		if (item != null) {
			item.remove();
		}
		clearNearbyItems(.5,true,true);
		active = false;
	}
	
	public void delete() {
		HashMap<String,String> conditions = new HashMap<String,String>();
		conditions.put("WORLD", w);
		conditions.put("X", x+"");
		conditions.put("Y", y+"");
		conditions.put("Z", z+"");
		hc.getSQLWrite().performDelete("hyperconomy_item_displays", conditions);
		clear();
	}
	
	public void clear() {
		removeItem();
		hc = null;
		w = null;
		name = null;
		item = null;
	}
	
	
	/**
	 *
	 * @param droppedItem
	 * @return true if the item drop event shop be blocked to prevent item stacking, false if not
	 */
	public boolean blockItemDrop(Item droppedItem) {
		if (item == null) {return false;}
		HyperItemStack dropped = new HyperItemStack(droppedItem.getItemStack());
		HyperItemStack displayItem = new HyperItemStack(item.getItemStack());
		Location l = droppedItem.getLocation();
		Material dropType = droppedItem.getItemStack().getType();
		int dropda = dropped.getDamageValue();
		double dropx = l.getX();
		double dropy = l.getY();
		double dropz = l.getZ();
		World dropworld = l.getWorld();
		Material type = item.getItemStack().getType();
		int da = displayItem.getDamageValue();
		if (type == dropType) {
			if (da == dropda) {
				if (dropworld.equals(getWorld())) {
					if (Math.abs(dropx - x) < 10) {
						if (Math.abs(dropz - z) < 10) {
							if (Math.abs(dropy - y) < 30) {
								return true;
							} else {
								droppedItem.setVelocity(new Vector(0,0,0));
								Block dblock = droppedItem.getLocation().getBlock();
								while (dblock.getType().equals(Material.AIR)) {
									dblock = dblock.getRelative(BlockFace.DOWN);
								}
								if (dblock.getLocation().getY() <= (y + 10)) {
									return true;
								}
							}
						}
					}
				}
			}
		}
		return false;
	}
	
	
	public boolean blockEntityPickup(Entity entity) {
		if (entity.getType() == EntityType.SKELETON || entity.getType() == EntityType.ZOMBIE || entity.getType() == EntityType.PIG_ZOMBIE) {
			Location entityLocation = entity.getLocation();	
			if (Bukkit.getWorld(w).equals(entityLocation.getWorld())) {
				if (Math.abs(entityLocation.getX() - x) < 1000) {
					if (Math.abs(entityLocation.getZ() - z) < 1000) {
						return true;
					}
				}
			}
		}
		return false;
	}
	

	public void clearNearbyItems(double radius, boolean removeDisplays, boolean removeSelf) {
		HyperObject hi = getHyperObject();
		if (hi == null) {return;}
		Item tempItem = getWorld().dropItem(getLocation(), hi.getItemStack());
		List<Entity> nearbyEntities = tempItem.getNearbyEntities(radius, radius, radius);
		for (Entity entity : nearbyEntities) {
			if (!(entity instanceof Item)) {continue;}
			Item nearbyItem = (Item) entity;
			boolean display = false;
			for (MetadataValue cmeta: nearbyItem.getMetadata("HyperConomy")) {
				if (cmeta.asString().equalsIgnoreCase("item_display")) {
					display = true;
					break;
				}
			}
			if (nearbyItem.equals(tempItem)) {continue;}
			if (nearbyItem.equals(item) && !removeSelf) {continue;}
			if (nearbyItem.getItemStack().getType() != tempItem.getItemStack().getType()) {continue;}
			if (!removeDisplays && display) {continue;}
			HyperItemStack near = new HyperItemStack(nearbyItem.getItemStack());
			HyperItemStack displayItem = new HyperItemStack(tempItem.getItemStack());
			if (near.getDamageValue() == displayItem.getDamageValue()) {
				entity.remove();
			}
		}
		tempItem.remove();
	}

}
