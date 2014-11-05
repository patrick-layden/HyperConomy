package regalowl.hyperconomy.display;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;









import regalowl.databukkit.event.EventHandler;
import regalowl.databukkit.sql.QueryResult;
import regalowl.databukkit.sql.SQLRead;
import regalowl.hyperconomy.DataManager;
import regalowl.hyperconomy.HC;
import regalowl.hyperconomy.account.HyperPlayer;
import regalowl.hyperconomy.event.HyperObjectModificationEvent;
import regalowl.hyperconomy.event.minecraft.HBlockBreakEvent;
import regalowl.hyperconomy.event.minecraft.HSignChangeEvent;
import regalowl.hyperconomy.minecraft.HBlock;
import regalowl.hyperconomy.minecraft.HLocation;
import regalowl.hyperconomy.minecraft.HSign;
import regalowl.hyperconomy.tradeobject.EnchantmentClass;

public class InfoSignHandler {

	private HC hc;
	private ConcurrentHashMap<Integer, InfoSign> infoSigns = new ConcurrentHashMap<Integer, InfoSign>();
	private AtomicInteger signCounter = new AtomicInteger();
	private final long signUpdateInterval = 1L;
	private QueryResult dbData;
	private AtomicBoolean updateActive = new AtomicBoolean();
	private AtomicBoolean repeatUpdate = new AtomicBoolean();


	public InfoSignHandler() {
		hc = HC.hc;
		updateActive.set(false);
		repeatUpdate.set(false);
		if (hc.getConf().getBoolean("enable-feature.info-signs")) {
			hc.getHyperEventHandler().registerListener(this);
			loadSigns();
		}
	}

	private void loadSigns() {
		signCounter.set(0);
		infoSigns.clear();
		new Thread(new Runnable() {
			public void run() {
				SQLRead sr = hc.getSQLRead();
				dbData = sr.select("SELECT * FROM hyperconomy_info_signs");
				HC.mc.runTask(new Runnable() {
					public void run() {
						while (dbData.next()) {
							HLocation l = new HLocation(dbData.getString("WORLD"), dbData.getInt("X"),dbData.getInt("Y"),dbData.getInt("Z"));
							infoSigns.put(signCounter.getAndIncrement(), new InfoSign(l, SignType.fromString(dbData.getString("TYPE")), dbData.getString("HYPEROBJECT"), 
									dbData.getDouble("MULTIPLIER"), dbData.getString("ECONOMY"), EnchantmentClass.fromString(dbData.getString("ECLASS"))));
						}
						dbData.close();
						dbData = null;
						updateSigns();
					}
				});
			}
		}).start();
	}



	@EventHandler
	public void onSignRemoval(HBlockBreakEvent bbevent) {
		HBlock b = bbevent.getBlock();
		if (!b.isInfoSign()) {return;}
		try {
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
	
	
	@EventHandler
	public void onHyperObjectModification(HyperObjectModificationEvent event) {
		updateSigns();
	}
	
	@EventHandler
	public void onHyperSignChangeEvent(HSignChangeEvent ev) {
		try {
			HSign s = ev.getSign();
			DataManager em = HC.hc.getDataManager();
			HyperPlayer hp = ev.getHyperPlayer();
			if (hp.hasPermission("hyperconomy.createsign")) {
				String[] lines = s.getLines();
				String economy = "default";
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
						HC.mc.logSevere("create");
						infoSigns.put(signCounter.getAndIncrement(), new InfoSign(s.getLocation(), type, objectName, multiplier, economy, enchantClass, lines));
						updateSigns();
					}
				}
			}
		} catch (Exception e) {
			hc.gDB().writeError(e);
		}
	}

	public void updateSigns() {
		if (hc.getHyperLock().fullLock() || !hc.enabled()) {return;}
		if (updateActive.get()) {
			repeatUpdate.set(true);
			return;
		}
		updateActive.set(true);
		new SignUpdater();
	}
	
	private class SignUpdater {
		private ArrayList<InfoSign> signs;
		private long updateTaskId;
		SignUpdater() {
			this.signs = getInfoSigns();
			updateTaskId = HC.mc.runRepeatingTask(new Runnable() {
				public void run() {
					if (signs.isEmpty()) {
						if (repeatUpdate.get()) {
							signs = getInfoSigns();
							if (signs.isEmpty()) {
								HC.mc.cancelTask(updateTaskId);
								updateActive.set(false);
								return;
							}
							repeatUpdate.set(false);
						} else {
							HC.mc.cancelTask(updateTaskId);
							updateActive.set(false);
							return;
						}
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

	public InfoSign getInfoSign(HLocation l) {
		for (InfoSign isign : infoSigns.values()) {
			if (isign == null) {continue;}
			if (l.getWorld().equalsIgnoreCase(isign.getWorld()) && isign.getX() == l.getBlockX() && 
					isign.getY() == l.getBlockY() && isign.getZ() == l.getBlockZ()) {
				return isign;
			}
		}
		return null;
	}



}
