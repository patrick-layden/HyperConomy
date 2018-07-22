package regalowl.hyperconomy.display;

import java.util.ArrayList;
import java.util.Collection;







import java.util.concurrent.CopyOnWriteArrayList;

import regalowl.simpledatalib.sql.QueryResult;
import regalowl.simpledatalib.sql.SQLRead;
import regalowl.hyperconomy.HyperConomy;
import regalowl.hyperconomy.event.HyperEvent;
import regalowl.hyperconomy.event.HyperEventListener;
import regalowl.hyperconomy.event.minecraft.HBlockBreakEvent;
import regalowl.hyperconomy.event.minecraft.HBlockPistonExtendEvent;
import regalowl.hyperconomy.event.minecraft.HBlockPistonRetractEvent;
import regalowl.hyperconomy.event.minecraft.HBlockPlaceEvent;
import regalowl.hyperconomy.event.minecraft.HEntityExplodeEvent;
import regalowl.hyperconomy.event.minecraft.HPlayerDropItemEvent;
import regalowl.hyperconomy.minecraft.HBlock;
import regalowl.hyperconomy.minecraft.HItem;
import regalowl.hyperconomy.minecraft.HLocation;

public class ItemDisplayHandler implements HyperEventListener {
	
	private HyperConomy hc; 
	private long refreshthreadid;
	private final long refreshInterval = 4800L;
	//private final long refreshInterval = 100L;
	private CopyOnWriteArrayList<ItemDisplay> displays = new CopyOnWriteArrayList<ItemDisplay>();
	private QueryResult dbData;


	public ItemDisplayHandler(HyperConomy hc) {
		try {
			this.hc = hc;
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
				@Override
				public void run() {
					SQLRead sr = hc.getSQLRead();
					dbData = sr.select("SELECT * FROM hyperconomy_item_displays");
					hc.getMC().runTask(new Runnable() {
						@Override
						public void run() {
							while (dbData.next()) {
								String w = dbData.getString("WORLD");
								double x = dbData.getDouble("X");
								double y = dbData.getDouble("Y");
								double z = dbData.getDouble("Z");
								String name = dbData.getString("HYPEROBJECT");
								HLocation l = new HLocation(w,x,y,z);
								displays.add(new ItemDisplay(hc, l, name, false));
							}
							dbData.close();
							dbData = null;
							for (ItemDisplay display:displays) {
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
		return new ArrayList<ItemDisplay>(displays);
	}

	public void unloadDisplays() {
		for (ItemDisplay display:displays) {
			display.clearNearbyItems(.5,true,true);
			display.clear();
		}
		displays.clear();
	}
	
	

	public void startRefreshThread() {
		refreshthreadid = hc.getMC().runRepeatingTask(new Runnable() {
			@Override
			public void run() {
				for (ItemDisplay display:displays) {
					display.refresh();
				}
			}
		}, refreshInterval, refreshInterval);
	}
	
	public void cancelRefreshThread() {
		hc.getMC().cancelTask(refreshthreadid);
	}
	
	public void removeDisplay(ItemDisplay disp) {
		displays.remove(disp);
	}
	
	public boolean removeDisplay(HLocation sl) {
		sl.convertToBlockLocation();
		for (ItemDisplay display:displays) {
			HLocation dLoc = display.getBaseBlock().getLocation();
			if (dLoc.equals(sl)) {
				display.delete();
				displays.remove(display);
				return true;
			}
		}
		return false;
	}
	
	public boolean removeDisplayInColumn(HLocation l) {
		l.convertToBlockLocation();
		for (ItemDisplay d:displays) {
			HLocation dLoc = d.getBaseBlock().getLocation();
			if (dLoc.getBlockX() == l.getBlockX() && dLoc.getBlockZ() == l.getBlockZ() && dLoc.getWorld().equalsIgnoreCase(l.getWorld())) {
				d.delete();
				displays.remove(d);
				return true;
			}
		}
		return false;
	}

	public boolean addDisplay(double x, double y, double z, String w, String name) {
		x = Math.floor(x) + .5;
		z = Math.floor(z) + .5;	
		for (ItemDisplay display:displays) {
			if (x == display.getX() && y == display.getY() && z == display.getZ() && w.equals(display.getWorld())) {
				return false;
			}
		}
		HLocation l = new HLocation(w, x, y, z);
		ItemDisplay display = new ItemDisplay(hc, l, name, true);
		HLocation loc = new HLocation(l);
		loc.convertToBlockLocation();
		displays.add(display);
		if (l.isLoaded(hc)) {
			display.makeDisplay();
			display.clearNearbyItems(7,false,false);
		}
		return true;
	}
	
	
	@Override
	public void handleHyperEvent(HyperEvent event) {
		if (event instanceof HPlayerDropItemEvent) {
			HPlayerDropItemEvent hevent = (HPlayerDropItemEvent)event;
			HItem i = hevent.getItem();
			for (ItemDisplay display : hc.getItemDisplay().getDisplays()) {
				if (!display.isActive()) continue;
				if (display.blockItemDrop(i)) {
					event.cancel();
					return;
				}
			}
		} else if (event instanceof HBlockBreakEvent) {
			HBlockBreakEvent hevent = (HBlockBreakEvent)event;
			try {
				HBlock b = hevent.getBlock();
				for (ItemDisplay display:displays) {
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
		} else if (event instanceof HBlockPlaceEvent) {
			HBlockPlaceEvent hevent = (HBlockPlaceEvent)event;
			try {
				HBlock b = hevent.getBlock();
				for (ItemDisplay display : displays) {
					if (!display.isActive()) {continue;}
					if (display.getBaseBlock().equals(b) || display.getItemBlock().equals(b)) {
						event.cancel();
						display.refresh();
					}
				}
				if (b.canFall()) {
					HBlock below = b.getFirstNonAirBlockBelow();
					for (ItemDisplay display : displays) {
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
		} else if (event instanceof HBlockPistonRetractEvent) {
			HBlockPistonRetractEvent hevent = (HBlockPistonRetractEvent)event;
			try {
				HBlock b = hevent.getRetractedBlock();
				for (ItemDisplay display : displays) {
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
		} else if (event instanceof HBlockPistonExtendEvent) {
			HBlockPistonExtendEvent hevent = (HBlockPistonExtendEvent)event;
			try {
				for (HBlock cblock : hevent.getBlocks()) {
					for (ItemDisplay display : displays) {
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
		} else if (event instanceof HEntityExplodeEvent) {
			HEntityExplodeEvent hevent = (HEntityExplodeEvent)event;
			try {
				for (HBlock cblock : hevent.getBrokenBlocks()) {
					for (ItemDisplay display : displays) {
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
	}




	public boolean isDisplay(HItem item) {
		try {
			for (ItemDisplay display : displays) {
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
