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
import org.bukkit.metadata.MetadataValue;
import org.bukkit.util.Vector;

public class ItemDisplayFactory implements Listener {
	
	private HyperConomy hc; 
	private int refreshthreadid;
	private ArrayList<ItemDisplay> displays;
	private ArrayList<Block> protectedBlocks;
	private Calculation calc;

	ItemDisplayFactory() {
		hc = HyperConomy.hc;
		calc = hc.getCalculation();
		if (hc.getYaml().getConfig().getBoolean("config.use-item-displays")) {
			hc.getServer().getPluginManager().registerEvents(this, hc);
			displays = new ArrayList<ItemDisplay>();
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
		try {
			hc = HyperConomy.hc;
			unloadDisplays();
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
					ItemDisplay display = new ItemDisplay(l, name, economy);
					displays.add(display);
					display.clearNearbyItems();
				}
			}
		} catch (Exception e) {
			new HyperError(e);
		}
	}
	

	public void unloadDisplays() {
		for (ItemDisplay display:displays) {
			display.clearNearbyItems();
			display.clearDisplay();
		}
		displays.clear();
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
		loadDisplays();
	}
	
	
	public boolean removeDisplay(int x, int z, World w) {
		FileConfiguration disp = hc.getYaml().getDisplays();
		for (ItemDisplay display:displays) {
			Location il = display.getLocation();
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
						display.clearDisplay();
						displays.remove(display);
						loadProtectedBlocks();
						loadDisplays();
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

	

	
	
	
	
	
	

	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerDropItemEvent(PlayerDropItemEvent event) {
		Item droppedItem = event.getItemDrop();
		Location l = droppedItem.getLocation();
		int did = droppedItem.getItemStack().getType().getId();
		int dda = calc.getDamageValue(droppedItem.getItemStack());
		double dx = l.getX();
		double dy = l.getY();
		double dz = l.getZ();
		World w = l.getWorld();
		
		for (ItemDisplay display:displays) {
			Location dl = display.getLocation();
			int id = display.getItem().getItemStack().getType().getId();
			int da = calc.getDamageValue(display.getItem().getItemStack());
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
		try {
			boolean refresh = false;
			Chunk chunk = event.getChunk();
			for (ItemDisplay display:displays) {
				if (chunk.equals(display.getLocation().getChunk())) {
					refresh = true;
				}
			}
			if (refresh) {
				loadDisplays();
			}
		} catch (Exception e) {
			new HyperError(e);
		}
	}
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onChunkUnload(ChunkUnloadEvent event) {
		try {
			Chunk unloadchunk = event.getChunk();
			boolean refresh = false;
			for (ItemDisplay display:displays) {
				Chunk displaychunk = display.getLocation().getChunk();
				if (displaychunk.equals(unloadchunk)) {
					refresh = true;
				}
			}
			if (refresh) {
				loadDisplays();
			}
		} catch (Exception e) {
			new HyperError(e);
		}
	}
	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerPickupItemEvent(PlayerPickupItemEvent event) {
		Item item = event.getItem();
		for (ItemDisplay display:displays) {
			if (display.getId() == item.getEntityId()) {
				event.setCancelled(true);
				break;
			}
		}
		if (!event.isCancelled()) {
			List<MetadataValue> meta = item.getMetadata("HyperConomy");
			for (MetadataValue cmeta: meta) {
				if (cmeta.asString().equalsIgnoreCase("item_display")) {
					Bukkit.broadcastMessage("cancelled meta");
					event.setCancelled(true);
					break;
				}
			}
		}
		if (!event.isCancelled()) {
			for (ItemDisplay display:displays) {
				if (item.equals(display.getItem())) {
					event.setCancelled(true);
					break;
				}
			}
		}
	}
	@EventHandler(priority = EventPriority.NORMAL)
	public void onBlockBreakEvent(BlockBreakEvent event) {
		Block bb = event.getBlock();
		for (Block cb:protectedBlocks) {
			if (cb.equals(bb)) {
				event.setCancelled(true);
				loadDisplays();
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
		if (bb.getType().equals(Material.GRAVEL) || bb.getType().equals(Material.SAND)) {
			Block below = bb.getRelative(BlockFace.DOWN);
			while (below.getType().equals(Material.AIR)) {
				below = below.getRelative(BlockFace.DOWN);
			}
			for (Block cb:protectedBlocks) {
				if (cb.equals(below)) {
					event.setCancelled(true);
					loadDisplays();
				}
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
