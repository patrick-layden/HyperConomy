package regalowl.hyperconomy.display;

import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;


import regalowl.simpledatalib.sql.QueryResult;
import regalowl.simpledatalib.sql.SQLRead;
import regalowl.hyperconomy.DataManager;
import regalowl.hyperconomy.HyperConomy;
import regalowl.hyperconomy.account.HyperPlayer;
import regalowl.hyperconomy.event.HyperEvent;
import regalowl.hyperconomy.event.HyperEventListener;
import regalowl.hyperconomy.event.TradeObjectModificationEvent;
import regalowl.hyperconomy.event.minecraft.HBlockBreakEvent;
import regalowl.hyperconomy.event.minecraft.HSignChangeEvent;
import regalowl.hyperconomy.minecraft.HLocation;
import regalowl.hyperconomy.minecraft.HSign;
import regalowl.hyperconomy.tradeobject.EnchantmentClass;
import regalowl.hyperconomy.tradeobject.TradeObject;

public class InfoSignHandler implements HyperEventListener {

	private HyperConomy hc;
	private CopyOnWriteArrayList<InfoSign> infoSigns = new CopyOnWriteArrayList<InfoSign>();
	private AtomicInteger signCounter = new AtomicInteger();
	private AtomicBoolean updateActive = new AtomicBoolean();
	private AtomicBoolean repeatUpdate = new AtomicBoolean();

	private QueryResult dbData;
	
	private final long signUpdateInterval = 1L;

	public InfoSignHandler(HyperConomy hc) {
		this.hc = hc;
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
			@Override
			public void run() {
				SQLRead sr = hc.getSQLRead();
				dbData = sr.select("SELECT * FROM hyperconomy_info_signs");
				hc.getMC().runTask(new Runnable() {
					@Override
					public void run() {
						while (dbData.next()) {
							HLocation l = new HLocation(dbData.getString("WORLD"), dbData.getInt("X"),dbData.getInt("Y"),dbData.getInt("Z"));
							infoSigns.add(new InfoSign(hc, l, SignType.fromString(dbData.getString("TYPE")), dbData.getString("HYPEROBJECT"), 
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

	
	@Override
	public void handleHyperEvent(HyperEvent event) {
		if (event instanceof HBlockBreakEvent) {
			HBlockBreakEvent hevent = (HBlockBreakEvent)event;
			HLocation l = hevent.getBlock().getLocation();
			InfoSign iSign = getInfoSign(l);
			if (iSign == null) {return;}
			iSign.deleteSign();
		} else if (event instanceof TradeObjectModificationEvent) {
			//TradeObjectModificationEvent hevent = (TradeObjectModificationEvent)event;
			updateSigns();
		} else if (event instanceof HSignChangeEvent) {
			HSignChangeEvent hevent = (HSignChangeEvent)event;
			try {
				HSign s = hevent.getSign();
				DataManager em = hc.getDataManager();
				HyperPlayer hp = hevent.getHyperPlayer();
				if (hp.hasPermission("hyperconomy.createsign")) {
					String[] lines = s.getLines();
					String economy = "default";
					if (hp.getEconomy() != null) {
						economy = hp.getEconomy();
					}
					String objectName = lines[0].trim() + lines[1].trim();
					TradeObject to = em.getEconomy(economy).getTradeObject(objectName);
					if (to != null) objectName = to.getDisplayName();
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
							infoSigns.add(new InfoSign(hc, s.getLocation(), type, objectName, multiplier, economy, enchantClass, lines));
							updateSigns();
						}
					}
				}
			} catch (Exception e) {
				hc.gSDL().getErrorWriter().writeError(e);
			}
		}
	}


	
	public void removeSign(InfoSign is) {
		if (infoSigns.contains(is)) {
			infoSigns.remove(is);
		}
	}
	

	public void updateSigns() {
		if (hc.getHyperLock().fullLock() || !hc.loaded()) {return;}
		if (updateActive.get()) {
			repeatUpdate.set(true);
			return;
		}
		updateActive.set(true);
		new SignUpdater();
	}
	
	private class SignUpdater {
		private long updateTaskId;
		private int currentSign;
		SignUpdater() {
			currentSign = 0;
			updateTaskId = hc.getMC().runRepeatingTask(new Runnable() {
				@Override
				public void run() {
					if (currentSign >= infoSigns.size()) {
						if (repeatUpdate.get()) {
							currentSign = 0;
							repeatUpdate.set(false);
							if (infoSigns.isEmpty()) {
								hc.getMC().cancelTask(updateTaskId);
								updateActive.set(false);
								return;
							}
						} else {
							hc.getMC().cancelTask(updateTaskId);
							updateActive.set(false);
							return;
						}
					}
					InfoSign cs = infoSigns.get(currentSign);
					if (cs.getSign() != null) {
						cs.update();
					} else {
						cs.deleteSign();
					}
					currentSign++;
				}
			}, signUpdateInterval, signUpdateInterval);
		}
	}
	
	
	public void reloadSigns() {
		loadSigns();
	}


	public ArrayList<InfoSign> getInfoSigns() {
		ArrayList<InfoSign> iSigns = new ArrayList<InfoSign>();
		for (InfoSign is : infoSigns) {
			iSigns.add(is);
		}
		return iSigns;
	}

	public InfoSign getInfoSign(HLocation l) {
		for (InfoSign isign : infoSigns) {
			if (isign == null) {continue;}
			if (l.equals(isign.getLocation())) return isign;
		}
		return null;
	}





}
