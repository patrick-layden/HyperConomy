package regalowl.hyperconomy.display;

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;









import regalowl.simpledatalib.event.EventHandler;
import regalowl.simpledatalib.sql.QueryResult;
import regalowl.simpledatalib.sql.SQLRead;
import regalowl.hyperconomy.HC;
import regalowl.hyperconomy.event.minecraft.HBlockBreakEvent;
import regalowl.hyperconomy.event.minecraft.HBlockPistonExtendEvent;
import regalowl.hyperconomy.event.minecraft.HBlockPistonRetractEvent;
import regalowl.hyperconomy.event.minecraft.HBlockPlaceEvent;
import regalowl.hyperconomy.event.minecraft.HEntityExplodeEvent;
import regalowl.hyperconomy.event.minecraft.HPlayerDropItemEvent;
import regalowl.hyperconomy.minecraft.HBlock;
import regalowl.hyperconomy.minecraft.HItem;
import regalowl.hyperconomy.minecraft.HLocation;

public class ItemDisplayFactory {
	
	private HC hc; 
	private long refreshthreadid;
	private final long refreshInterval = 4800L;
	//private final long refreshInterval = 100L;
	private ConcurrentHashMap<HLocation, ItemDisplay> displays = new ConcurrentHashMap<HLocation, ItemDisplay>();
	private QueryResult dbData;


	public ItemDisplayFactory() {
		try {
			hc = HC.hc;
			if (hc.getConf().getBoolean("enable-feature.item-displays")) {
				hc.getHyperEventHandler().registerListener(this);
				loadDisplays();
				startRefreshThread();
			}
		} catch (Exception e) {
			hc.gSDL().getErrorWriter().writeError(e);
		}
	}
	

	public void loadDisplays() {
		try {
			unloadDisplays();
			new Thread(new Runnable() {
				public void run() {
					SQLRead sr = hc.getSQLRead();
					dbData = sr.select("SELECT * FROM hyperconomy_item_displays");
					HC.mc.runTask(new Runnable() {
						public void run() {
							while (dbData.next()) {
								String w = dbData.getString("WORLD");
								double x = dbData.getDouble("X");
								double y = dbData.getDouble("Y");
								double z = dbData.getDouble("Z");
								String name = dbData.getString("HYPEROBJECT");
								HLocation l = new HLocation(w,x,y,z);
								ItemDisplay display = new ItemDisplay(l, name, false);
								HLocation key = new HLocation(w, x, y, z);
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
			hc.gSDL().getErrorWriter().writeError(e);
		}
	}
	
	public Collection<ItemDisplay> getDisplays() {
		return displays.values();
	}

	public void unloadDisplays() {
		for (ItemDisplay display:displays.values()) {
			display.clearNearbyItems(.5,true,true);
			display.clear();
		}
		displays.clear();
	}
	
	

	public void startRefreshThread() {
		refreshthreadid = HC.mc.runRepeatingTask(new Runnable() {
			public void run() {
				for (ItemDisplay display:displays.values()) {
					display.refresh();
				}
			}
		}, refreshInterval, refreshInterval);
	}
	
	public void cancelRefreshThread() {
		HC.mc.cancelTask(refreshthreadid);
	}
	

	public boolean removeDisplay(HLocation sl) {
		if (displays.containsKey(sl)) {
			ItemDisplay display = displays.get(sl);
			display.delete();
			displays.remove(sl);
			return true;
		} 
		return false;
	}
	
	public boolean removeDisplay(double x, double z, String w) {
		for (HLocation key:displays.keySet()) {
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
		HLocation l = new HLocation(w, x, y, z);
		ItemDisplay display = new ItemDisplay(l, name, true);
		displays.put(l, display);
		if (l.isLoaded()) {
			display.makeDisplay();
			display.clearNearbyItems(7,false,false);
		}
		return true;
	}

	

	@EventHandler
	public void onPlayerDropItemEvent(HPlayerDropItemEvent event) {
		HItem i = event.getItem();
		for (ItemDisplay display : HC.hc.getItemDisplay().getDisplays()) {
			if (!display.isActive()) continue;
			if (display.blockItemDrop(i)) {
				event.cancel();
				return;
			}
		}
	}
	
	
	@EventHandler
	public void onBlockBreakEvent(HBlockBreakEvent event) {
		try {
		HBlock b = event.getBlock();
		for (ItemDisplay display:displays.values()) {
			if (!display.isActive()) {continue;}
			if (display.getBaseBlock().equals(b) || display.getItemBlock().equals(b)) {
				event.cancel();
				display.removeItem();
				display.makeDisplay();
			}
		}
		} catch (Exception e) {
			hc.gSDL().getErrorWriter().writeError(e);
		}
	}

	@EventHandler
	public void onBlockPlaceEvent(HBlockPlaceEvent event) {
		try {
			HBlock b = event.getBlock();
			for (ItemDisplay display : displays.values()) {
				if (!display.isActive()) {continue;}
				if (display.getBaseBlock().equals(b) || display.getItemBlock().equals(b)) {
					event.cancel();
					display.refresh();
				}
			}
			if (b.canFall()) {
				HBlock below = b.getFirstNonAirBlockBelow();
				for (ItemDisplay display : displays.values()) {
					if (!display.isActive()) {continue;}
					if (display.getBaseBlock().equals(below) || display.getItemBlock().equals(below)) {
						event.cancel();
						display.refresh();
					}
				}
			}
		} catch (Exception e) {
			hc.gSDL().getErrorWriter().writeError(e);
		}
	}

	@EventHandler
	public void onBlockPistonRetractEvent(HBlockPistonRetractEvent event) {
		try {
			HBlock b = event.getRetractedBlock();
			for (ItemDisplay display : displays.values()) {
				if (!display.isActive()) {
					continue;
				}
				if (display.getBaseBlock().equals(b) || display.getItemBlock().equals(b)) {
					event.cancel();
					display.refresh();
				}
			}
		} catch (Exception e) {
			hc.gSDL().getErrorWriter().writeError(e);
		}
	}

	@EventHandler
	public void onBlockPistonExtendEvent(HBlockPistonExtendEvent event) {
		try {
			for (HBlock cblock : event.getBlocks()) {
				for (ItemDisplay display : displays.values()) {
					if (!display.isActive()) {continue;}
					if (display.getBaseBlock().equals(cblock) || display.getItemBlock().equals(cblock)) {
						event.cancel();
						display.refresh();
					}
				}
			}
		} catch (Exception e) {
			hc.gSDL().getErrorWriter().writeError(e);
		}
	}

	@EventHandler
	public void onEntityExplodeEvent(HEntityExplodeEvent event) {
		try {
			for (HBlock cblock : event.getBrokenBlocks()) {
				for (ItemDisplay display : displays.values()) {
					if (!display.isActive()) {
						continue;
					}
					if (display.getBaseBlock().equals(cblock) || display.getItemBlock().equals(cblock)) {
						event.cancel();
						display.refresh();
					}
				}
			}
		} catch (Exception e) {
			hc.gSDL().getErrorWriter().writeError(e);
		}
	}



	public boolean isDisplay(HItem item) {
		try {
			for (ItemDisplay display : displays.values()) {
				if (item.equals(display.getItem())) {
					return true;
				}
			}
			return false;
		} catch (Exception e) {
			hc.gSDL().getErrorWriter().writeError(e);
			return false;
		}
	}

}
