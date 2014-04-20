package regalowl.hyperconomy.display;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.scheduler.BukkitTask;

import regalowl.databukkit.sql.QueryResult;
import regalowl.databukkit.sql.SQLRead;
import regalowl.hyperconomy.DataManager;
import regalowl.hyperconomy.HyperConomy;
import regalowl.hyperconomy.account.HyperPlayer;
import regalowl.hyperconomy.hyperobject.EnchantmentClass;

public class InfoSignHandler implements Listener {

	private HyperConomy hc;
	private ConcurrentHashMap<Integer, InfoSign> infoSigns = new ConcurrentHashMap<Integer, InfoSign>();
	private AtomicInteger signCounter = new AtomicInteger();
	private final long signUpdateInterval = 1L;
	private QueryResult dbData;


	public InfoSignHandler() {
		hc = HyperConomy.hc;
		if (hc.getConf().getBoolean("enable-feature.info-signs")) {
			hc.getServer().getPluginManager().registerEvents(this, hc);
			loadSigns();
		}
	}

	private void loadSigns() {
		signCounter.set(0);
		infoSigns.clear();
		hc.getServer().getScheduler().runTaskAsynchronously(hc, new Runnable() {
			public void run() {
				SQLRead sr = hc.getSQLRead();
				dbData = sr.select("SELECT * FROM hyperconomy_info_signs");
				hc.getServer().getScheduler().runTask(hc, new Runnable() {
					public void run() {
						while (dbData.next()) {
							Location l = new Location(Bukkit.getWorld(dbData.getString("WORLD")), dbData.getInt("X"),dbData.getInt("Y"),dbData.getInt("Z"));
							infoSigns.put(signCounter.getAndIncrement(), new InfoSign(l, SignType.fromString(dbData.getString("TYPE")), dbData.getString("HYPEROBJECT"), 
									dbData.getDouble("MULTIPLIER"), dbData.getString("ECONOMY"), EnchantmentClass.fromString(dbData.getString("ECLASS"))));
						}
						dbData.close();
						dbData = null;
					}
				});
			}
		});
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onSignChangeEvent(SignChangeEvent scevent) {
		try {
			DataManager em = hc.getDataManager();
			Player p = scevent.getPlayer();
			if (p.hasPermission("hyperconomy.createsign")) {
				String[] lines = scevent.getLines();
				String economy = "default";
				HyperPlayer hp = em.getHyperPlayer(p);
				economy = "default";
				if (hp != null && hp.getEconomy() != null) {
					economy = hp.getEconomy();
				}
				String objectName = lines[0].trim() + lines[1].trim();
				objectName = em.getEconomy(hp.getEconomy()).fixName(objectName);
				int multiplier = 1;
				try {
					multiplier = Integer.parseInt(lines[3]);
				} catch (Exception e) {
					multiplier = 1;
				}
				EnchantmentClass enchantClass = EnchantmentClass.NONE;
				if (EnchantmentClass.fromString(lines[3]) != null) {
					enchantClass = EnchantmentClass.fromString(lines[3]);
				}
				if (em.getEconomy(hp.getEconomy()).enchantTest(objectName) && enchantClass == EnchantmentClass.NONE) {
					enchantClass = EnchantmentClass.DIAMOND;
				}
				if (em.getEconomy(hp.getEconomy()).objectTest(objectName)) {
					SignType type = SignType.fromString(lines[2]);
					if (type != null) {
						Location l = new Location(scevent.getBlock().getWorld(),scevent.getBlock().getX(),scevent.getBlock().getY(),scevent.getBlock().getZ());
						infoSigns.put(signCounter.getAndIncrement(), new InfoSign(l, type, objectName, multiplier, economy, enchantClass, lines));
						updateSigns();
					}
				}
			}
		} catch (Exception e) {
			hc.gDB().writeError(e);
		}
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onSignRemoval(BlockBreakEvent bbevent) {
		if (bbevent.isCancelled()) {return;}
		try {
			Block b = bbevent.getBlock();
			InfoSign is = getInfoSign(b.getLocation());
			if (is != null) {
				is.deleteSign();
			}
		} catch (Exception e) {
			hc.gDB().writeError(e);
		}
	}
	
	public void removeSign(InfoSign is) {
		if (infoSigns.contains(is)) {
			infoSigns.remove(is);
		}
	}

	public void updateSigns() {
		if (hc.getHyperLock().fullLock() || !hc.enabled()) {
			return;
		}
		SignUpdater su = new SignUpdater(getInfoSigns());
		su.update();
	}
	
	private class SignUpdater {
		private ArrayList<InfoSign> signs;
		private BukkitTask updateTask;
		
		SignUpdater(ArrayList<InfoSign> signs) {
			this.signs = signs;
		}
		
		public void update() {
			updateTask = hc.getServer().getScheduler().runTaskTimer(hc, new Runnable() {
				public void run() {
					if (signs.isEmpty()) {
						updateTask.cancel();
						return;
					}
					InfoSign cs = signs.get(0);
					if (cs.getSign() != null) {
						cs.update();
					} else {
						cs.deleteSign();
					}
					signs.remove(0);
				}
			}, signUpdateInterval, signUpdateInterval);
		}
	}
	
	public void reloadSigns() {
		loadSigns();
	}


	public ArrayList<InfoSign> getInfoSigns() {
		ArrayList<InfoSign> isigns = new ArrayList<InfoSign>();
		for (InfoSign is : infoSigns.values()) {
			isigns.add(is);
		}
		return isigns;
	}

	public InfoSign getInfoSign(Location l) {
		for (InfoSign isign : infoSigns.values()) {
			if (isign == null) {continue;}
			if (l.getWorld().getName().equalsIgnoreCase(isign.getWorld()) && isign.getX() == l.getBlockX() && 
					isign.getY() == l.getBlockY() && isign.getZ() == l.getBlockZ()) {
				return isign;
			}
		}
		return null;
	}

}
