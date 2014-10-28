package regalowl.hyperconomy.display;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.metadata.MetadataValue;

import regalowl.databukkit.sql.QueryResult;
import regalowl.databukkit.sql.SQLRead;
import regalowl.hyperconomy.HyperConomy;
import regalowl.hyperconomy.util.SimpleLocation;

public class ItemDisplayFactory implements Listener {
	
	private HyperConomy hc; 
	private long refreshthreadid;
	private final long refreshInterval = 4800L;
	//private final long refreshInterval = 100L;
	private ConcurrentHashMap<SimpleLocation, ItemDisplay> displays = new ConcurrentHashMap<SimpleLocation, ItemDisplay>();
	private QueryResult dbData;


	public ItemDisplayFactory() {
		try {
			hc = HyperConomy.hc;
			if (hc.getConf().getBoolean("enable-feature.item-displays")) {
				hc.mc.getConnector().getServer().getPluginManager().registerEvents(this, hc.mc.getConnector());
				loadDisplays();
				startRefreshThread();
			}
		} catch (Exception e) {
			hc.gDB().writeError(e);
		}
	}
	

	public void loadDisplays() {
		try {
			unloadDisplays();
			new Thread(new Runnable() {
				public void run() {
					SQLRead sr = hc.getSQLRead();
					dbData = sr.select("SELECT * FROM hyperconomy_item_displays");
					hc.mc.runTask(new Runnable() {
						public void run() {
							while (dbData.next()) {
								String w = dbData.getString("WORLD");
								double x = dbData.getDouble("X");
								double y = dbData.getDouble("Y");
								double z = dbData.getDouble("Z");
								String name = dbData.getString("HYPEROBJECT");
								SimpleLocation l = new SimpleLocation(w,x,y,z);
								ItemDisplay display = new ItemDisplay(l, name, false);
								SimpleLocation key = new SimpleLocation(w, x, y, z);
								displays.put(key, display);
							}
							dbData.close();
							dbData = null;
							for (ItemDisplay display:displays.values()) {
								display.makeDisplay();
								display.clearNearbyItems(7,false,false);
							}
						}
					});
				}
			}).start();
		} catch (Exception e) {
			hc.gDB().writeError(e);
		}
	}
	

	public void unloadDisplays() {
		for (ItemDisplay display:displays.values()) {
			display.clearNearbyItems(.5,true,true);
			display.clear();
		}
		displays.clear();
	}
	
	

	public void startRefreshThread() {
		refreshthreadid = hc.mc.runRepeatingTask(new Runnable() {
			public void run() {
				for (ItemDisplay display:displays.values()) {
					display.refresh();
				}
			}
		}, refreshInterval, refreshInterval);
	}
	
	public void cancelRefreshThread() {
		hc.mc.cancelTask(refreshthreadid);
	}
	

	public boolean removeDisplay(SimpleLocation sl) {
		if (displays.containsKey(sl)) {
			ItemDisplay display = displays.get(sl);
			display.delete();
			displays.remove(sl);
			return true;
		} 
		return false;
	}
	
	public boolean removeDisplay(double x, double z, String w) {
		for (SimpleLocation key:displays.keySet()) {
			if (key.getX() == x && key.getZ() == z && key.getWorld().equalsIgnoreCase(w)) {
				ItemDisplay display = displays.get(key);
				display.delete();
				displays.remove(key);
				return true;
			}
		}
		return false;
	}

	public boolean addDisplay(double x, double y, double z, String w, String name) {
		x = Math.floor(x) + .5;
		z = Math.floor(z) + .5;	
		for (ItemDisplay display:displays.values()) {
			if (x == display.getX() && y == display.getY() && z == display.getZ() && w.equals(display.getWorld())) {
				return false;
			}
		}
		SimpleLocation l = new SimpleLocation(w, x, y, z);
		ItemDisplay display = new ItemDisplay(l, name, true);
		displays.put(l, display);
		Location loc = new Location(Bukkit.getWorld(w),x,y,z);
		Chunk locChunk = loc.getChunk();
		if (locChunk.isLoaded()) {
			display.makeDisplay();
			display.clearNearbyItems(7,false,false);
		}
		return true;
	}

	

	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerDropItemEvent(PlayerDropItemEvent event) {
		try {
			Item droppedItem = event.getItemDrop();
			for (ItemDisplay display:displays.values()) {
				if (!display.isActive()) {continue;}
				if (display.blockItemDrop(droppedItem)) {
					event.setCancelled(true);
					return;
				}
			}
		} catch (Exception e) {
			hc.gDB().writeError(e);
		}
	}
	
	
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onChunkLoad(ChunkLoadEvent event) {
		try {
			Chunk chunk = event.getChunk();
			if (chunk == null) {return;}
			for (ItemDisplay display:displays.values()) {
				if (display == null) {continue;}
				if (chunk.equals(display.getChunk())) {
					display.refresh();
					return;
				}
			}
		} catch (Exception e) {
			hc.gDB().writeError(e);
		}
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onChunkUnload(ChunkUnloadEvent event) {
		try {
			Chunk chunk = event.getChunk();
			if (chunk == null) {return;}
			for (ItemDisplay display:displays.values()) {
				if (display == null) {continue;}
				if (chunk.equals(display.getChunk())) {
					display.removeItem();
					return;
				}
			}
		} catch (Exception e) {
			hc.gDB().writeError(e);
		}
	}
	
	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerPickupItemEvent(PlayerPickupItemEvent event) {
		try {
			Item item = event.getItem();
			if (!event.isCancelled()) {
				List<MetadataValue> meta = item.getMetadata("HyperConomy");
				for (MetadataValue cmeta : meta) {
					if (cmeta.asString().equalsIgnoreCase("item_display")) {
						event.setCancelled(true);
						break;
					}
				}
			}
			for (ItemDisplay display : displays.values()) {
				if (display.getEntityId() == item.getEntityId() || item.equals(display.getItem())) {
					event.setCancelled(true);
					break;
				}
			}
		} catch (Exception e) {
			hc.gDB().writeError(e);
		}
	}
	
	
	@EventHandler(priority = EventPriority.NORMAL)
	public void onBlockBreakEvent(BlockBreakEvent event) {
		try {
		Block bb = event.getBlock();
		for (ItemDisplay display:displays.values()) {
			if (!display.isActive()) {continue;}
			if (display.getBaseBlock().equals(bb) || display.getItemBlock().equals(bb)) {
				event.setCancelled(true);
				display.removeItem();
				display.makeDisplay();
			}
		}
		} catch (Exception e) {
			hc.gDB().writeError(e);
		}
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onBlockPlaceEvent(BlockPlaceEvent event) {
		try {
			Block bb = event.getBlock();
			for (ItemDisplay display : displays.values()) {
				if (!display.isActive()) {continue;}
				if (display.getBaseBlock().equals(bb) || display.getItemBlock().equals(bb)) {
					event.setCancelled(true);
					display.refresh();
				}
			}
			if (bb.getType().equals(Material.GRAVEL) || bb.getType().equals(Material.SAND)) {
				Block below = bb.getRelative(BlockFace.DOWN);
				while (below.getType().equals(Material.AIR)) {
					below = below.getRelative(BlockFace.DOWN);
				}
				for (ItemDisplay display : displays.values()) {
					if (!display.isActive()) {continue;}
					if (display.getBaseBlock().equals(below) || display.getItemBlock().equals(below)) {
						event.setCancelled(true);
						display.refresh();
					}
				}
			}
		} catch (Exception e) {
			hc.gDB().writeError(e);
		}
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onBlockPistonRetractEvent(BlockPistonRetractEvent event) {
		try {
			Location l = event.getRetractLocation();
			Block b = l.getBlock();
			for (ItemDisplay display : displays.values()) {
				if (!display.isActive()) {
					continue;
				}
				if (display.getBaseBlock().equals(b) || display.getItemBlock().equals(b)) {
					event.setCancelled(true);
					display.refresh();
				}
			}
		} catch (Exception e) {
			hc.gDB().writeError(e);
		}
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onBlockPistonExtendEvent(BlockPistonExtendEvent event) {
		try {
			List<Block> blocks = event.getBlocks();
			for (Block cblock : blocks) {
				for (ItemDisplay display : displays.values()) {
					if (!display.isActive()) {continue;}
					if (display.getBaseBlock().equals(cblock) || display.getItemBlock().equals(cblock)) {
						event.setCancelled(true);
						display.refresh();
					}
				}
			}
		} catch (Exception e) {
			hc.gDB().writeError(e);
		}
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onEntityExplodeEvent(EntityExplodeEvent event) {
		try {
			List<Block> blocks = event.blockList();
			for (Block cblock : blocks) {
				for (ItemDisplay display : displays.values()) {
					if (!display.isActive()) {
						continue;
					}
					if (display.getBaseBlock().equals(cblock) || display.getItemBlock().equals(cblock)) {
						event.setCancelled(true);
						display.refresh();
					}
				}
			}
		} catch (Exception e) {
			hc.gDB().writeError(e);
		}
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onCreatureSpawnEvent(CreatureSpawnEvent event) {
		try {
			for (ItemDisplay display : displays.values()) {
				if (!display.isActive()) {continue;}
				if (display.blockEntityPickup(event.getEntity())) {
					event.getEntity().setCanPickupItems(false);
				}
			}
		} catch (Exception e) {
			hc.gDB().writeError(e);
		}
	}

	public boolean isDisplay(Item item) {
		try {
			for (ItemDisplay display : displays.values()) {
				if (item.equals(display.getItem())) {
					return true;
				}
			}
			return false;
		} catch (Exception e) {
			hc.gDB().writeError(e);
			return false;
		}
	}

}
