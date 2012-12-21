package regalowl.hyperconomy;


import java.util.List;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.util.Vector;

public class ItemDisplay {
	
	private HyperConomy hc;
	
	private Item item;
	private Location location;
	private String name;
	private String economy;
	private double x;
	private double y;
	private double z;
	private World w;
	private int id;
	
	ItemDisplay(Location location, String name, String economy) {
		this.hc = HyperConomy.hc;
		this.location = location;
		this.x = this.location.getX();
		this.y = this.location.getY();
		this.z = this.location.getZ();
		this.w = this.location.getWorld();
		this.name = hc.fixName(name);
		this.economy = economy;
		this.item = makeDisplay();
	}
	
	public void clearDisplay() {
		removeDisplay();
		hc = null;
		location = null;
		w = null;
		name = null;
		economy = null;
		item = null;
	}
	
	public Item getItem() {
		return item;	
	}
	
	public Location getLocation() {
		return location;
	}
	
	public String getName() {
		return name;
	}
	
	public String getEconomy() {
		return economy;
	}
	
	public double getX() {
		return location.getX();
	}
	
	public double getY() {
		return location.getY();
	}
	
	public double getZ() {
		return location.getZ();
	}
	
	public World getWorld() {
		return location.getWorld();
	}
	
	public int getId() {
		return id;
	}
	
	public Item makeDisplay() {
		DataFunctions sf = hc.getSQLFunctions();
		Location l = new Location(w, x, y + 1, z);
		ItemStack dropstack = new ItemStack(sf.getId(name, economy));
		dropstack.setDurability((short) sf.getDurability(name, economy));
		Item item = w.dropItem(l, dropstack);
		this.id = item.getEntityId();
		item.setVelocity(new Vector(0, 0, 0));
		item.setMetadata("HyperConomy", new FixedMetadataValue(hc, "item_display"));
		return item;
	}
	
	public void removeDisplay() {
		item.remove();
	}
	

	public void clearNearbyItems() {
		Calculation calc = hc.getCalculation();
		List<Entity> nearbyEntities = item.getNearbyEntities(7, 7, 7);
		for (Entity entity : nearbyEntities) {
			if (entity instanceof Item) {
				Item nearbyItem = (Item) entity;
				boolean display = false;
				for (MetadataValue cmeta: nearbyItem.getMetadata("HyperConomy")) {
					if (cmeta.asString().equalsIgnoreCase("item_display")) {
						display = true;
						break;
					}
				}
				if (!nearbyItem.equals(item) && !display) {
					if (nearbyItem.getItemStack().getType().getId() == item.getItemStack().getType().getId()) {
						if (calc.getDamageValue(nearbyItem.getItemStack()) == calc.getDamageValue(item.getItemStack())) {
							entity.remove();
						}
					}
				}
			}
		}
	}
}
