package regalowl.hyperconomy;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

public class ItemDisplay implements Listener {
	
	private HyperConomy hc; 
	private SQLFunctions sf;
	private int refreshthreadid;
	private ArrayList<Item> displayItems;
	private ArrayList<Location> displayLocations;
	private ArrayList<String> displayNames;
	private ArrayList<String> displayEconomies;
	private ArrayList<Block> protectedBlocks;
	private Calculation calc;

	ItemDisplay() {
		hc = HyperConomy.hc;
		calc = hc.getCalculation();
		if (hc.getYaml().getConfig().getBoolean("config.use-item-displays")) {
			sf = hc.getSQLFunctions();
			hc.getServer().getPluginManager().registerEvents(this, hc);
			displayItems = new ArrayList<Item>();
			displayLocations = new ArrayList<Location>();
			displayNames = new ArrayList<String>();
			displayEconomies = new ArrayList<String>();
			protectedBlocks = new ArrayList<Block>();
			loadProtectedBlocks();
			loadDisplays();
			startRefreshThread();
		}
	}
	
	
	public void loadProtectedBlocks() {
		FileConfiguration disp = hc.getYaml().getDisplays();
		Iterator<String> it = hc.getYaml().getDisplays().getKeys(false).iterator();
		protectedBlocks.clear();
		while (it.hasNext()) {
			String key = it.next().toString();
			int x = (int) Math.floor(disp.getDouble(key + ".x"));
			int y = (int) Math.floor(disp.getDouble(key + ".y") - 1);
			int z = (int) Math.floor(disp.getDouble(key + ".z"));
			World w = Bukkit.getWorld(disp.getString(key + ".world"));
			Block cb = w.getBlockAt(x, y, z);
			protectedBlocks.add(cb);
			cb = w.getBlockAt(x, y + 1, z);
			protectedBlocks.add(cb);
		}
	}
	
	
	public void loadDisplays() {
		clearDisplays();
		FileConfiguration disp = hc.getYaml().getDisplays();
		Iterator<String> it = hc.getYaml().getDisplays().getKeys(false).iterator();
		while (it.hasNext()) {
			String key = it.next().toString();
			String name = disp.getString(key + ".name");
			String economy = disp.getString(key + ".economy");
			double x = disp.getDouble(key + ".x");
			double y = disp.getDouble(key + ".y");
			double z = disp.getDouble(key + ".z");
			World w = Bukkit.getWorld(disp.getString(key + ".world"));
			Location l = new Location(w, x, y, z);
			Chunk locChunk = l.getChunk();
			if (locChunk.isLoaded()) {
				Item newitem = makeDisplay(x, y, z, w, name, economy);
				clearNearbyItems(newitem);
				displayItems.add(newitem);
				displayLocations.add(l);
				displayNames.add(name);
				displayEconomies.add(economy);
			}
		}
	}
	
	public void clearDisplays() {
		for (int i = 0; i < displayItems.size(); i++) {
			Item item = displayItems.get(i);
			item.remove();
		}
		displayItems.clear();
		displayLocations.clear();
		displayNames.clear();
		displayEconomies.clear();
	}
	
	public Item makeDisplay(double x, double y, double z, World w, String name, String economy) {
		Location l = new Location(w, x, y + 1, z);
		name = hc.fixName(name);
		ItemStack dropstack = new ItemStack(sf.getId(name, economy));
		dropstack.setDurability((short) sf.getDurability(name, economy));
		Item item = w.dropItem(l, dropstack);
		item.setPickupDelay(200);
		item.setVelocity(new Vector(0, .1, 0));
		return item;
	}
	
	
	public void clearNearbyItems(Item item) {
		List<Entity> nearbyEntities = item.getNearbyEntities(10, 10, 10);
		for (Entity entity : nearbyEntities) {
			if (entity instanceof Item) {
				Item citem = (Item) entity;
				int id = citem.getItemStack().getType().getId();
				int da = calc.getDamageValue(citem.getItemStack());
				for (int i = 0; i < displayItems.size(); i++) {
					Item titem = displayItems.get(i);
					if (!titem.equals(citem)) {
						if (id == titem.getItemStack().getType().getId()) {
							if (da == calc.getDamageValue(titem.getItemStack())) {
								entity.remove();
								continue;
							}
						}
					}
				}
			}
		}
	}


	public void startRefreshThread() {
		refreshthreadid = hc.getServer().getScheduler().scheduleSyncRepeatingTask(hc, new Runnable() {
			public void run() {
				loadDisplays();
			}
		}, 4800L, 4800L);
	}
	
	public void cancelRefreshThread() {
		hc.getServer().getScheduler().cancelTask(refreshthreadid);
	}
	

	public void storeDisplay(double x, double y, double z, World w, String name, String economy) {
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
		
		String key = "d" + numdisplays;
		disp.set(key + ".name", name);
		disp.set(key + ".economy", economy);
		disp.set(key + ".x", x);
		disp.set(key + ".y", y);
		disp.set(key + ".z", z);
		disp.set(key + ".world", w.getName());
		loadProtectedBlocks();
	}
	
	
	public boolean removeDisplay(int x, int z, World w) {
		FileConfiguration disp = hc.getYaml().getDisplays();
		for (int i = 0; i < displayItems.size(); i++) {
			Location il = displayLocations.get(i);
			int tx =  il.getBlockX();
			int tz =  il.getBlockZ();
			World tw = il.getWorld();
			if (tw == w && tx == x && tz == z) {
				Iterator<String> it = hc.getYaml().getDisplays().getKeys(false).iterator();
				while (it.hasNext()) {
					String key = it.next().toString();
					int ymlx = (int) Math.floor(disp.getDouble(key + ".x"));
					int ymlz = (int) Math.floor(disp.getDouble(key + ".z"));
					World ymlw = Bukkit.getWorld(disp.getString(key + ".world"));
					if (ymlw == w && ymlx == x && ymlz == z) {
						disp.set(key, null);
						loadDisplays();
						loadProtectedBlocks();
						return true;
					}
				}
			}
		}
		return false;
	}
	
	
	public boolean testDisplay(double x, double y, double z, World w, String name, String economy) {
		x = Math.floor(x) + .5;
		z = Math.floor(z) + .5;
		
		FileConfiguration disp = hc.getYaml().getDisplays();
		Iterator<String> it = hc.getYaml().getDisplays().getKeys(false).iterator();
		while (it.hasNext()) {
			String key = it.next().toString();
			double tx = disp.getDouble(key + ".x");
			double ty = disp.getDouble(key + ".y");
			double tz = disp.getDouble(key + ".z");
			World tw = Bukkit.getWorld(disp.getString(key + ".world"));
			if (x == tx && y == ty && z == tz && w == tw) {
				return false;
			}
		}
		storeDisplay(x, y, z, w, name, economy);
		loadDisplays();
		return true;
	}

	

	
	
	
	
	
	

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerDropItemEvent(PlayerDropItemEvent event) {
		Item droppedItem = event.getItemDrop();
		Location l = droppedItem.getLocation();
		int did = droppedItem.getItemStack().getType().getId();
		int dda = calc.getDamageValue(droppedItem.getItemStack());
		double dx = l.getX();
		double dy = l.getY();
		double dz = l.getZ();
		World w = l.getWorld();
		
		for (int i = 0; i < displayLocations.size(); i++) {
			Location dl = displayLocations.get(i);
			int id = displayItems.get(i).getItemStack().getType().getId();
			int da = calc.getDamageValue(displayItems.get(i).getItemStack());
			if (id == did) {
				if (dda == da) {
					if (w.equals(dl.getWorld())) {
						if (Math.abs(dx - dl.getX()) < 10) {
							if (Math.abs(dz - dl.getZ()) < 10) {
								if (Math.abs(dy - dl.getY()) < 30) {
									event.setCancelled(true);
								} else {
									droppedItem.setVelocity(new Vector(0,0,0));
									Block dblock = droppedItem.getLocation().getBlock();
									while (dblock.getType().equals(Material.AIR)) {
										dblock = dblock.getRelative(BlockFace.DOWN);
									}
									if (dblock.getLocation().getY() <= (dl.getBlockY() + 10)) {
										event.setCancelled(true);
									}
									clearNearbyItems(displayItems.get(i));
								}
							}
						}
					}
				}
			}

		}
	}
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onChunkLoad(ChunkLoadEvent event) {
		boolean refresh = false;
		Chunk chunk = event.getChunk();
		FileConfiguration disp = hc.getYaml().getDisplays();
		Iterator<String> it = hc.getYaml().getDisplays().getKeys(false).iterator();
		while (it.hasNext()) {
			String key = it.next().toString();
			double x = disp.getDouble(key + ".x");
			double y = disp.getDouble(key + ".y");
			double z = disp.getDouble(key + ".z");
			World w = Bukkit.getWorld(disp.getString(key + ".world"));
			Location l = new Location(w, x, y, z);
			Chunk locChunk = l.getChunk();
			if (locChunk.equals(chunk)) {
				refresh = true;
				//Bukkit.broadcastMessage("Chunk load--Displays reloaded");
			}
		}
		if (refresh) {
			loadDisplays();
		}
	}
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onChunkUnload(ChunkUnloadEvent event) {
		//Bukkit.broadcastMessage("Chunk Unload");
		Chunk unloadchunk = event.getChunk();
		boolean refresh = false;
		for (int i = 0; i < displayLocations.size(); i++) {
			Location il = displayLocations.get(i);
			Chunk displaychunk = il.getChunk();
			if (displaychunk.equals(unloadchunk)) {
				//Bukkit.broadcastMessage("Chunk unload--Displays reloaded");
				refresh = true;
			}
		}
		if (refresh) {
			loadDisplays();
		}
	}
	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerPickupItemEvent(PlayerPickupItemEvent event) {
		Item item = event.getItem();
		if (displayItems.contains(item)) {
			event.setCancelled(true);
		}
	}
	@EventHandler(priority = EventPriority.NORMAL)
	public void onBlockBreakEvent(BlockBreakEvent event) {
		Block bb = event.getBlock();
		for (Block cb:protectedBlocks) {
			if (cb.equals(bb)) {
				event.setCancelled(true);
			}
		}

	}
	@EventHandler(priority = EventPriority.NORMAL)
	public void onBlockPlaceEvent(BlockPlaceEvent event) {
		Block bb = event.getBlock();
		for (Block cb:protectedBlocks) {
			if (cb.equals(bb)) {
				event.setCancelled(true);
				loadDisplays();
			}
		}
	}
	@EventHandler(priority = EventPriority.NORMAL)
	public void onBlockPistonRetractEvent(BlockPistonRetractEvent event) {
		if (hc.getYaml().getConfig().getBoolean("config.use-chest-shops")) {
			Location l = event.getRetractLocation();
			Block b = l.getBlock();
			for (Block cb:protectedBlocks) {
				if (cb.equals(b)) {
					event.setCancelled(true);
				}
			}
		}
	}
	@EventHandler(priority = EventPriority.NORMAL)
	public void onBlockPistonExtendEvent(BlockPistonExtendEvent event) {
		List<Block> blocks = event.getBlocks();
		for (int i = 0; i < blocks.size(); i++) {
			Block cblock = blocks.get(i);
			for (Block cb:protectedBlocks) {
				if (cb.equals(cblock)) {
					event.setCancelled(true);
				}
			}
		}
	}
	@EventHandler(priority = EventPriority.NORMAL)
	public void onEntityExplodeEvent(EntityExplodeEvent event) {
		if (hc.getYaml().getConfig().getBoolean("config.use-chest-shops")) {
			List<Block> blocks = event.blockList();
			for (int i = 0; i < blocks.size(); i++) {
				Block cblock = blocks.get(i);
				for (Block cb:protectedBlocks) {
					if (cb.equals(cblock)) {
						event.setCancelled(true);
						loadDisplays();
					}
				}
			}
		}
	}

	
}
