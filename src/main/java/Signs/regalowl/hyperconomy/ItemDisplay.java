package regalowl.hyperconomy;


import java.util.Iterator;
import java.util.List;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.util.Vector;

public class ItemDisplay {
	
	private HyperConomy hc;
	
	private String key;
	private Item item;
	private Location location;
	private String name;
	private String economy;
	private double x;
	private double y;
	private double z;
	private World w;
	private int entityId;
	private Block baseBlock;
	private Block itemBlock;
	
	ItemDisplay(String key, Location location, String name, String economy) {
		this.hc = HyperConomy.hc;
		this.location = location;
		DataHandler dh = hc.getDataFunctions();
		this.x = this.location.getX();
		this.y = this.location.getY();
		this.z = this.location.getZ();
		this.w = this.location.getWorld();
		this.name = dh.fixName(name);
		this.economy = economy;
		this.key = key;
		setProtectedBlocks();
	}
	
	ItemDisplay(Location location, String name, String economy) {
		this.hc = HyperConomy.hc;
		this.location = location;
		DataHandler dh = hc.getDataFunctions();
		this.x = this.location.getX();
		this.y = this.location.getY();
		this.z = this.location.getZ();
		this.w = this.location.getWorld();
		this.name = dh.fixName(name);
		this.economy = economy;
		storeDisplay();
		setProtectedBlocks();
	}
	
	private void setProtectedBlocks() {
		int x = (int) Math.floor(this.x);
		int y = (int) Math.floor(this.y - 1);
		int z = (int) Math.floor(this.z);
		Block cb = this.w.getBlockAt(x, y, z);
		baseBlock = cb;
		cb = w.getBlockAt(x, y + 1, z);
		itemBlock = cb;
	}
	
	public void clearDisplay() {
		removeItem();
		hc = null;
		location = null;
		w = null;
		name = null;
		economy = null;
		item = null;
		key = null;
	}
	
	public String getKey() {
		return key;
	}
	
	public Item getItem() {
		return item;	
	}
	
	public Block getBaseBlock() {
		return baseBlock;
	}
	
	public Block getItemBlock() {
		return itemBlock;
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
	
	public int getEntityId() {
		return entityId;
	}
	
	public Item makeDisplay() {
		DataHandler sf = hc.getDataFunctions();
		Location l = new Location(w, x, y + 1, z);
		ItemStack dropstack = new ItemStack(sf.getHyperObject(name, economy).getId());
		dropstack.setDurability((short) sf.getHyperObject(name, economy).getDurability());
		this.item = w.dropItem(l, dropstack);
		this.entityId = item.getEntityId();
		item.setVelocity(new Vector(0, 0, 0));
		item.setMetadata("HyperConomy", new FixedMetadataValue(hc, "item_display"));
		return item;
	}
	
	public void storeDisplay() {
		Iterator<String> it = hc.getYaml().getDisplays().getKeys(false).iterator();
		int numdisplays = 0;
		while (it.hasNext()) {
			String key = it.next().toString();
			int number = Integer.parseInt(key.substring(1, key.length()));
			if (number > numdisplays) {
				numdisplays = number;
			}
		}
		numdisplays++;
		FileConfiguration disp = hc.getYaml().getDisplays();
		key = "d" + numdisplays;
		disp.set(key + ".name", name);
		disp.set(key + ".economy", economy);
		disp.set(key + ".x", x);
		disp.set(key + ".y", y);
		disp.set(key + ".z", z);
		disp.set(key + ".world", w.getName());
	}
	
	public void removeItem() {
		if (item != null) {
			item.remove();
		}
	}
	
	public void deleteDisplay() {
		FileConfiguration disp = hc.getYaml().getDisplays();
		disp.set(key, null);
		clearDisplay();
	}
	
	public boolean blockItemDrop(Item droppedItem) {
		Calculation calc = hc.getCalculation();
		Location l = droppedItem.getLocation();
		int dropid = droppedItem.getItemStack().getType().getId();
		int dropda = calc.getDamageValue(droppedItem.getItemStack());
		double dropx = l.getX();
		double dropy = l.getY();
		double dropz = l.getZ();
		World dropworld = l.getWorld();
		int id = item.getItemStack().getType().getId();
		int da = calc.getDamageValue(item.getItemStack());
		if (id == dropid) {
			if (da == dropda) {
				if (dropworld.equals(location.getWorld())) {
					if (Math.abs(dropx - location.getX()) < 10) {
						if (Math.abs(dropz - location.getZ()) < 10) {
							if (Math.abs(dropy - location.getY()) < 30) {
								return true;
							} else {
								droppedItem.setVelocity(new Vector(0,0,0));
								Block dblock = droppedItem.getLocation().getBlock();
								while (dblock.getType().equals(Material.AIR)) {
									dblock = dblock.getRelative(BlockFace.DOWN);
								}
								if (dblock.getLocation().getY() <= (location.getBlockY() + 10)) {
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
	

	public void clearNearbyItems() {
		if (item == null) {
			return;
		}
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
