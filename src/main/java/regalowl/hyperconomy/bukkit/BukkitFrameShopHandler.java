package regalowl.hyperconomy.bukkit;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;

import regalowl.simpledatalib.sql.QueryResult;
import regalowl.hyperconomy.DataManager;
import regalowl.hyperconomy.HyperConomy;
import regalowl.hyperconomy.account.HyperPlayer;
import regalowl.hyperconomy.api.MineCraftConnector;
import regalowl.hyperconomy.display.FrameShop;
import regalowl.hyperconomy.display.FrameShopHandler;
import regalowl.hyperconomy.minecraft.HLocation;
import regalowl.hyperconomy.shop.PlayerShop;
import regalowl.hyperconomy.shop.Shop;
import regalowl.hyperconomy.tradeobject.TradeObject;
import regalowl.hyperconomy.util.LanguageFile;

public class BukkitFrameShopHandler implements Listener, FrameShopHandler {

	private BukkitConnector mc;
	private HyperConomy hc;
	private DataManager em;
	private HashMap<HLocation, BukkitFrameShop> frameShops = new HashMap<HLocation, BukkitFrameShop>();
	private QueryResult dbData;
	private BukkitCommon bc;

	public BukkitFrameShopHandler(MineCraftConnector mc) {
		this.mc = (BukkitConnector) mc;
		this.bc = this.mc.getBukkitCommon();
		this.hc = mc.getHC();
		em = hc.getDataManager();
		Bukkit.getPluginManager().registerEvents(this, (BukkitConnector)hc.getMC());
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
							HLocation l = new HLocation(w, x, y, z);
							l.convertToBlockLocation();
							Shop s = em.getHyperShopManager().getShop(dbData.getString("SHOP"));
							String economy = em.getDefaultEconomy().getName();
							if (s != null) {
								economy = s.getEconomy();
							}
							TradeObject ho = em.getEconomy(economy).getTradeObject(dbData.getString("HYPEROBJECT"), s);
							BukkitFrameShop fs = new BukkitFrameShop(hc, (short) (int) dbData.getInt("ID"), l, ho, s, dbData.getInt("TRADE_AMOUNT"));
							frameShops.put(l, fs);

						}
						dbData.close();
						dbData = null;
					}
				});
			}
		}).start();
	}

	public FrameShop getFrameShop(HLocation l) {
		HLocation loc = new HLocation(l);
		loc.convertToBlockLocation();
		if (frameShops.containsKey(loc)) {
			return frameShops.get(loc);
		}
		return null;
	}

	public boolean frameShopExists(HLocation l) {
		if (getFrameShop(l) != null) return true;
		return false;
	}

	public void removeFrameShop(HLocation l) {
		HLocation loc = new HLocation(l);
		loc.convertToBlockLocation();
		if (frameShops.containsKey(loc)) {
			frameShops.remove(loc);
		}
	}

	public void createFrameShop(HLocation l, TradeObject ho, Shop s) {
		HLocation loc = new HLocation(l);
		loc.convertToBlockLocation();
		BukkitFrameShop fs = new BukkitFrameShop(hc, loc, ho, s, 1);
		frameShops.put(loc, fs);
	}


	@EventHandler(priority = EventPriority.NORMAL)
	public void onEntityDamageEvent(EntityDamageByEntityEvent event) {
		if (event.isCancelled()) return;
		Entity entity = event.getEntity();
		LanguageFile L = hc.getLanguageFile();
		if (event.getDamager() instanceof Player) {
			Player p = (Player) event.getDamager();
			if (entity instanceof ItemFrame) {
				if (frameShopExists(bc.getLocation(entity.getLocation()))) {
					if (hc.getHyperLock().isLocked(mc.getBukkitCommon().getPlayer(p))) {
						hc.getHyperLock().sendLockMessage(mc.getBukkitCommon().getPlayer(p));
						event.setCancelled(true);
						return;
					}
					BukkitFrameShop fs = (BukkitFrameShop)getFrameShop(bc.getLocation(entity.getLocation()));
					HyperPlayer hp = mc.getBukkitCommon().getPlayer(p);
					Shop s = fs.getShop();
					PlayerShop ps = null;
					if (s instanceof PlayerShop) {
						ps = (PlayerShop) s;
					}
					if (p.isSneaking() && p.hasPermission("hyperconomy.admin")) {
						fs.delete();
						return;
					}
					if (ps != null && ps.isAllowed(hp) && p.isSneaking()) {
						fs.delete();
						return;
					}
					event.setCancelled(true);
					if (p.hasPermission("hyperconomy.sell")) {
						fs.sell(hp);
					} else {
						p.sendMessage(L.get("YOU_DONT_HAVE_PERMISSION"));
					}
				}
			}
		}
	}
	

	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerInteractEntityEvent(PlayerInteractEntityEvent event) {
		if (event.isCancelled()) return;
		if (!event.getEventName().equals("PlayerInteractEntityEvent")) return; //temp fix
		Entity entity = event.getRightClicked();
		LanguageFile L = hc.getLanguageFile();
		if (entity instanceof ItemFrame) {
			ItemFrame iFrame = (ItemFrame) entity;
			if (iFrame.getItem().getType().equals(Material.MAP)) {
				Location l = entity.getLocation();
				HLocation hl = bc.getLocation(l);
				if (frameShopExists(hl)) {
					event.setCancelled(true);
					Player p = event.getPlayer();
					if (hc.getHyperLock().isLocked(mc.getBukkitCommon().getPlayer(p))) {
						hc.getHyperLock().sendLockMessage(mc.getBukkitCommon().getPlayer(p));
						return;
					}
					HyperPlayer hp = mc.getBukkitCommon().getPlayer(p);
					BukkitFrameShop fs = (BukkitFrameShop)getFrameShop(hl);
					if (p.hasPermission("hyperconomy.buy")) {
						fs.buy(hp);
					} else {
						p.sendMessage(L.get("YOU_DONT_HAVE_PERMISSION"));
					}
				}
			}
		}
	}
	
	@EventHandler(priority = EventPriority.NORMAL)
	public void onBlockPlaceEvent(BlockPlaceEvent event) {
		HLocation placeLocation = bc.getLocation(event.getBlock().getLocation());
		placeLocation.convertToBlockLocation();
		for (BukkitFrameShop fs:frameShops.values()) {
			if (fs.getLocation().equals(placeLocation)) {
				event.setCancelled(true);
			}
		}
	}
	
	@EventHandler(priority = EventPriority.NORMAL)
	public void onBlockBreakEvent(BlockBreakEvent event) {
		for (BukkitFrameShop fs:frameShops.values()) {
			Block attached = fs.getAttachedBlock();
			if (attached == null) {continue;}
			if (attached.equals(event.getBlock())) {
				event.setCancelled(true);
			}
		}
	}

	@Override
	public void removeFrameShops(TradeObject to) {
		for (BukkitFrameShop fs:frameShops.values()) {
			if (fs.getTradeObject().equals(to)) fs.delete();
		}
	}
	

}