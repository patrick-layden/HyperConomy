package regalowl.hyperconomy;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;

import regalowl.databukkit.QueryResult;

public class FrameShopHandler implements Listener {

	private HyperConomy hc;
	private EconomyManager em;
	private HashMap<String, FrameShop> frameShops = new HashMap<String, FrameShop>();

	public FrameShopHandler() {
		hc = HyperConomy.hc;
		em = hc.getEconomyManager();
		hc.getServer().getPluginManager().registerEvents(this, hc);
		load();
	}

	private void load() {
		hc.getServer().getScheduler().runTaskAsynchronously(hc, new Runnable() {
			public void run() {
				frameShops.clear();
				QueryResult result = hc.getSQLRead().select("SELECT * FROM hyperconomy_frame_shops");
				while (result.next()) {
					double x = result.getDouble("X");
					double y = result.getDouble("Y");
					double z = result.getDouble("Z");
					World w = Bukkit.getWorld(result.getString("WORLD"));
					Location l = new Location(w, x, y, z);
					Shop s = em.getShop(result.getString("SHOP"));
					String economy = em.getDefaultEconomy().getEconomy();
					if (s != null) {
						economy = s.getEconomy();
					}
					HyperObject ho = em.getEconomy(economy).getHyperObject(result.getString("HYPEROBJECT"), s);
					FrameShop fs = new FrameShop((short) (int) result.getInt("ID"), l, ho, s, result.getInt("TRADE_AMOUNT"));
					frameShops.put(fs.getKey(), fs);

				}
				result.close();
			}
		});
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

	public void createFrameShop(Location l, HyperObject ho, Shop s) {
		FrameShop fs = new FrameShop(l, ho, s, 1);
		frameShops.put(fs.getKey(), fs);
	}


	@EventHandler(priority = EventPriority.NORMAL)
	public void onEntityDamageEvent(EntityDamageByEntityEvent event) {
		if (event.isCancelled()) {
			return;
		}
		Entity entity = event.getEntity();
		if (event.getDamager() instanceof Player) {
			Player p = (Player) event.getDamager();
			if (entity instanceof ItemFrame) {
				if (frameShopExists(entity.getLocation())) {
					FrameShop fs = getFrameShop(entity.getLocation());
					HyperPlayer hp = em.getHyperPlayer(p);
					Shop s = fs.getShop();
					PlayerShop ps = null;
					if (s instanceof PlayerShop) {
						ps = (PlayerShop) s;
					}
					if (p.isSneaking() && p.hasPermission("hyperconomy.admin")) {
						return;
					}
					if (ps != null && ps.isAllowed(hp) && p.isSneaking()) {
						return;
					}
					event.setCancelled(true);
					fs.sell(hp);
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
		if (entity instanceof ItemFrame) {
			ItemFrame iFrame = (ItemFrame) entity;
			if (iFrame.getItem().getType().equals(Material.MAP)) {
				Location l = entity.getLocation();
				if (frameShopExists(l)) {
					event.setCancelled(true);
					Player p = event.getPlayer();
					HyperPlayer hp = em.getHyperPlayer(p);
					FrameShop fs = getFrameShop(l);
					fs.buy(hp);
				}
			}
		}

	}

}