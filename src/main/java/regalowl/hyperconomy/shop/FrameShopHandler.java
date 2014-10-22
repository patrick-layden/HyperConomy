package regalowl.hyperconomy.shop;

import java.util.HashMap;





import regalowl.databukkit.event.EventHandler;
import regalowl.databukkit.sql.QueryResult;
import regalowl.hyperconomy.DataManager;
import regalowl.hyperconomy.HyperConomy;
import regalowl.hyperconomy.HyperPlayerManager;
import regalowl.hyperconomy.account.HyperPlayer;
import regalowl.hyperconomy.event.minecraft.FrameShopEvent;
import regalowl.hyperconomy.hyperobject.HyperObject;
import regalowl.hyperconomy.transaction.TransactionType;
import regalowl.hyperconomy.util.LanguageFile;
import regalowl.hyperconomy.util.SimpleLocation;

public class FrameShopHandler {

	private HyperConomy hc;
	private DataManager em;
	private HyperPlayerManager hpm;
	private HashMap<String, FrameShop> frameShops = new HashMap<String, FrameShop>();
	private QueryResult dbData;

	public FrameShopHandler() {
		hc = HyperConomy.hc;
		em = hc.getDataManager();
		hpm = hc.getHyperPlayerManager();
		hc.getHyperEventHandler().registerListener(this);
		load();
	}

	private void load() {
		new Thread(new Runnable() {
			public void run() {
				frameShops.clear();
				dbData = hc.getSQLRead().select("SELECT * FROM hyperconomy_frame_shops");
				hc.getMC().runTask(new Runnable() {
					public void run() {
						while (dbData.next()) {
							double x = dbData.getDouble("X");
							double y = dbData.getDouble("Y");
							double z = dbData.getDouble("Z");
							String w = dbData.getString("WORLD");
							SimpleLocation l = new SimpleLocation(w, x, y, z);
							Shop s = em.getHyperShopManager().getShop(dbData.getString("SHOP"));
							String economy = em.getDefaultEconomy().getName();
							if (s != null) {
								economy = s.getEconomy();
							}
							HyperObject ho = em.getEconomy(economy).getHyperObject(dbData.getString("HYPEROBJECT"), s);
							FrameShop fs = new FrameShop((short) (int) dbData.getInt("ID"), l, ho, s, dbData.getInt("TRADE_AMOUNT"));
							frameShops.put(fs.getKey(), fs);

						}
						dbData.close();
						dbData = null;
					}
				});
			}
		}).start();
	}

	public FrameShop getFrameShop(String key) {
		if (frameShops.containsKey(key)) {
			return frameShops.get(key);
		}
		return null;
	}

	public FrameShop getFrameShop(SimpleLocation l) {
		return getFrameShop(l.getBlockX() + "|" + l.getBlockY() + "|" + l.getBlockZ() + "|" + l.getWorld());
	}

	public boolean frameShopExists(SimpleLocation l) {
		if (getFrameShop(l) != null) {
			return true;
		}
		return false;
	}

	public void removeFrameShop(String key) {
		if (frameShops.containsKey(key)) {
			frameShops.remove(key);
		}
	}

	public void createFrameShop(SimpleLocation l, HyperObject ho, Shop s) {
		FrameShop fs = new FrameShop(l, ho, s, 1);
		frameShops.put(fs.getKey(), fs);
	}

	
	
	@EventHandler
	public void onFrameShopEvent(FrameShopEvent event) {
		FrameShop fs = event.getFs();
		HyperPlayer hp = event.getHp();
		TransactionType type = event.getType();
		LanguageFile L = hc.getLanguageFile();
		if (type == TransactionType.BUY) {
			if (hc.getHyperLock().isLocked(hp)) {
				hc.getHyperLock().sendLockMessage(hp);
				return;
			}
			if (hp.hasPermission("hyperconomy.buy")) {
				fs.buy(hp);
			} else {
				hp.sendMessage(L.get("YOU_DONT_HAVE_PERMISSION"));
			}
		} else if (type == TransactionType.SELL) {
			if (hc.getHyperLock().isLocked(hp)) {
				hc.getHyperLock().sendLockMessage(hp);
				return;
			}
			Shop s = fs.getShop();
			PlayerShop ps = null;
			if (s instanceof PlayerShop) {
				ps = (PlayerShop) s;
			}
			if (hp.isSneaking() && hp.hasPermission("hyperconomy.admin")) {
				fs.delete();
				return;
			}
			if (ps != null && ps.isAllowed(hp) && hp.isSneaking()) {
				fs.delete();
				return;
			}
			if (hp.hasPermission("hyperconomy.sell")) {
				fs.sell(hp);
			} else {
				hp.sendMessage(L.get("YOU_DONT_HAVE_PERMISSION"));
			}
		}
	}
	

	
	@EventHandler(priority = EventPriority.NORMAL)
	public void onBlockPlaceEvent(BlockPlaceEvent event) {
		Location placeLocation = event.getBlock().getLocation();
		for (FrameShop fs:frameShops.values()) {
			if (fs.getLocation().equals(placeLocation)) {
				event.setCancelled(true);
			}
		}
	}
	
	@EventHandler(priority = EventPriority.NORMAL)
	public void onBlockBreakEvent(BlockBreakEvent event) {
		Location breakLocation = event.getBlock().getLocation();
		for (FrameShop fs:frameShops.values()) {
			if (isAdjacent(fs.getLocation(), breakLocation)) {
				Block attached = fs.getAttachedBlock();
				if (attached == null) {continue;}
				if (attached.equals(event.getBlock())) {
					event.setCancelled(true);
				}
			}
		}
	}
	
	private boolean isAdjacent(Location l, Location l2) {
		if (l == null || l2 == null) {return false;}
		if (!l.getWorld().equals(l2.getWorld())) {return false;}
		int matching = 0;
		if (Math.abs(l.getBlockX() - l2.getBlockX()) == 0) {matching++;}
		if (Math.abs(l.getBlockY() - l2.getBlockY()) == 0) {matching++;}
		if (Math.abs(l.getBlockZ() - l2.getBlockZ()) == 0) {matching++;}
		if (matching == 2) {return true;}
		return false;
	}

}