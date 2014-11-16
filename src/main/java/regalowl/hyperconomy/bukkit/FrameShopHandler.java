package regalowl.hyperconomy.bukkit;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
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
import regalowl.hyperconomy.minecraft.HLocation;
import regalowl.hyperconomy.shop.PlayerShop;
import regalowl.hyperconomy.shop.Shop;
import regalowl.hyperconomy.tradeobject.TradeObject;
import regalowl.hyperconomy.util.LanguageFile;

public class FrameShopHandler implements Listener {

	private BukkitConnector mc;
	private HyperConomy hc;
	private DataManager em;
	private HashMap<String, FrameShop> frameShops = new HashMap<String, FrameShop>();
	private QueryResult dbData;

	public FrameShopHandler(MineCraftConnector mc) {
		this.mc = (BukkitConnector) mc;
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
							World w = Bukkit.getWorld(dbData.getString("WORLD"));
							Location l = new Location(w, x, y, z);
							Shop s = em.getHyperShopManager().getShop(dbData.getString("SHOP"));
							String economy = em.getDefaultEconomy().getName();
							if (s != null) {
								economy = s.getEconomy();
							}
							TradeObject ho = em.getEconomy(economy).getTradeObject(dbData.getString("HYPEROBJECT"), s);
							FrameShop fs = new FrameShop(hc, (short) (int) dbData.getInt("ID"), l, ho, s, dbData.getInt("TRADE_AMOUNT"));
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

	public FrameShop getFrameShop(Location l) {
		return getFrameShop(l.getBlockX() + "|" + l.getBlockY() + "|" + l.getBlockZ() + "|" + l.getWorld().getName());
	}

	public boolean frameShopExists(Location l) {
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

	public void createFrameShop(HLocation l, TradeObject ho, Shop s) {
		FrameShop fs = new FrameShop(mc, l, ho, s, 1);
		frameShops.put(fs.getKey(), fs);
	}


	@EventHandler(priority = EventPriority.NORMAL)
	public void onEntityDamageEvent(EntityDamageByEntityEvent event) {
		if (event.isCancelled()) {
			return;
		}
		Entity entity = event.getEntity();
		LanguageFile L = hc.getLanguageFile();
		if (event.getDamager() instanceof Player) {
			Player p = (Player) event.getDamager();
			if (entity instanceof ItemFrame) {
				if (frameShopExists(entity.getLocation())) {
					if (hc.getHyperLock().isLocked(mc.getBukkitCommon().getPlayer(p))) {
						hc.getHyperLock().sendLockMessage(mc.getBukkitCommon().getPlayer(p));
						event.setCancelled(true);
						return;
					}
					FrameShop fs = getFrameShop(entity.getLocation());
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
		if (event.isCancelled()) {
			return;
		}
		Entity entity = event.getRightClicked();
		LanguageFile L = hc.getLanguageFile();
		if (entity instanceof ItemFrame) {
			ItemFrame iFrame = (ItemFrame) entity;
			if (iFrame.getItem().getType().equals(Material.MAP)) {
				Location l = entity.getLocation();
				if (frameShopExists(l)) {
					event.setCancelled(true);
					Player p = event.getPlayer();
					if (hc.getHyperLock().isLocked(mc.getBukkitCommon().getPlayer(p))) {
						hc.getHyperLock().sendLockMessage(mc.getBukkitCommon().getPlayer(p));
						return;
					}
					HyperPlayer hp = mc.getBukkitCommon().getPlayer(p);
					FrameShop fs = getFrameShop(l);
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