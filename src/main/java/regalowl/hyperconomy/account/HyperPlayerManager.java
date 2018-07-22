package regalowl.hyperconomy.account;

import java.util.ArrayList;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import regalowl.simpledatalib.sql.QueryResult;
import regalowl.hyperconomy.HyperConomy;
import regalowl.hyperconomy.event.DataLoadEvent;
import regalowl.hyperconomy.event.HyperEvent;
import regalowl.hyperconomy.event.DataLoadEvent.DataLoadType;
import regalowl.hyperconomy.event.HyperEventListener;
import regalowl.hyperconomy.event.minecraft.HPlayerJoinEvent;
import regalowl.hyperconomy.event.minecraft.HPlayerQuitEvent;
import regalowl.hyperconomy.minecraft.HLocation;
import regalowl.simpledatalib.file.FileConfiguration;

public class HyperPlayerManager implements HyperEventListener {

	private transient HyperConomy hc;
	private transient FileConfiguration config;
	private boolean playersLoaded;
	private String defaultServerShopAccount;
	private boolean uuidSupport;
	private ConcurrentHashMap<String, HyperPlayer> hyperPlayers = new ConcurrentHashMap<String, HyperPlayer>();
	private ConcurrentHashMap<String, String> uuidIndex = new ConcurrentHashMap<String, String>();
	
	public HyperPlayerManager(HyperConomy hc) {
		this.hc = hc;
		playersLoaded = false;
		config = hc.getConf();
		defaultServerShopAccount = config.getString("shop.default-server-shop-account");
		uuidSupport = config.getBoolean("enable-feature.uuid-support");
		hc.getHyperEventHandler().registerListener(this);
	}
	
	
	@Override
	public void handleHyperEvent(HyperEvent event) {
		if (event instanceof DataLoadEvent) {
			DataLoadEvent devent = (DataLoadEvent)event;
			if (devent.loadType == DataLoadType.ECONOMY) {
				new Thread(() -> loadData()).start();
			} else if (devent.loadType == DataLoadType.BANK) {
				hc.getMC().runTask(new Runnable() {
					@Override
					public void run() {
						if (!hc.getDataManager().accountExists(defaultServerShopAccount)) {
							HyperPlayer defaultAccount = new HyperPlayer(hc, defaultServerShopAccount);
							hyperPlayers.put(defaultServerShopAccount.toLowerCase(), defaultAccount);
							defaultAccount.setBalance(hc.getConf().getDouble("shop.default-server-shop-account-initial-balance"));
							defaultAccount.setUUID(UUID.randomUUID().toString());
						}
						hc.getHyperEventHandler().fireEventFromAsyncThread(new DataLoadEvent(DataLoadType.DEFAULT_ACCOUNT));
						hc.getDebugMode().ayncDebugConsoleMessage("Default account loaded.");
					}
				});
			}
		} else if (event instanceof HPlayerJoinEvent) {
			HPlayerJoinEvent hevent = (HPlayerJoinEvent)event;
			try {
				HyperPlayer hp = hevent.getHyperPlayer();
				hp.validate();
				if (hp.getName().equalsIgnoreCase(config.getString("shop.default-server-shop-account"))) hc.getMC().kickPlayer(hevent.getHyperPlayer(), hc.getLanguageFile().get("CANT_USE_ACCOUNT"));
			} catch (Exception e) {
				hc.gSDL().getErrorWriter().writeError(e);
			}
		} else if (event instanceof HPlayerQuitEvent) {
			HPlayerQuitEvent hevent = (HPlayerQuitEvent)event;
			try {
				if (hc.getHyperLock().loadLock()) {return;}
				HLocation l = hevent.getHyperPlayer().getLocation();
				String name = hevent.getHyperPlayer().getName();
				if (hyperPlayers.containsKey(name.toLowerCase())) {
					HyperPlayer hp = hyperPlayers.get(name.toLowerCase());
					if (hp == null) {return;}
					hp.setLocation(l);
				}
			} catch (Exception e) {
				hc.gSDL().getErrorWriter().writeError(e);
			}
		}
	}
	

	private void loadData() {
		hyperPlayers.clear();
		uuidIndex.clear();
		QueryResult playerData = hc.getSQLRead().select("SELECT * FROM hyperconomy_players");
		while (playerData.next()) {
			HLocation sl = new HLocation(playerData.getString("WORLD"), playerData.getDouble("X"), playerData.getDouble("Y"), playerData.getDouble("Z"));
			HyperPlayer hplayer = new HyperPlayer(hc, playerData.getString("NAME"), playerData.getString("UUID"), playerData.getString("ECONOMY"), 
					playerData.getDouble("BALANCE"), sl, playerData.getString("HASH"), playerData.getString("SALT"));
			hyperPlayers.put(hplayer.getName().toLowerCase(), hplayer);
			if (hplayer.getUUIDString() != null && hplayer.getName() != null) {
				uuidIndex.put(hplayer.getUUIDString(), hplayer.getName().toLowerCase());
			}
		}
		playerData.close();
		hc.getMC().runTask(new Runnable() {
			@Override
			public void run() {
				for (HyperPlayer hp:hyperPlayers.values()) {
					hp.validate();
				}
				for (String p : hc.getMC().getOnlinePlayerNames()) {
					HyperPlayer hp = null;
					if (!hyperPlayers.containsKey(p.toLowerCase())) {
						hp = new HyperPlayer(hc, p);
						hyperPlayers.put(p.toLowerCase(), hp);
						if (hp.getUUIDString() != null) uuidIndex.put(hp.getUUIDString(), p.toLowerCase());
					} else {
						hp = hyperPlayers.get(p.toLowerCase());
					}
					if (p.equalsIgnoreCase(config.getString("shop.default-server-shop-account"))) hp.kickPlayer(hc.getLanguageFile().get("CANT_USE_ACCOUNT"));
				}
				hc.getHyperEventHandler().fireEventFromAsyncThread(new DataLoadEvent(DataLoadType.PLAYER));
				hc.getDebugMode().ayncDebugConsoleMessage("Players loaded.");
				playersLoaded = true;
			}
		});
	}
	
	

	public ArrayList<HyperPlayer> getOnlinePlayers() {
		return hc.getMC().getOnlinePlayers();
	}


	public boolean uuidSupport() {
		return uuidSupport;
	}


	
	
	public HyperAccount getDefaultServerShopAccount() {
		return hc.getDataManager().getAccount(defaultServerShopAccount);
	}
	

	
	public boolean hyperPlayerExists(String name) {
		if (name == null || name == "") {return false;}
		return hyperPlayers.containsKey(name.toLowerCase());
	}
	public boolean hyperPlayerExistsWithUUID(UUID uuid) {
		if (uuid == null) {return false;}
		return uuidIndex.containsKey(uuid.toString());
	}
	
	/**
	 * @param player
	 * @return The HyperPlayer with the specified name.  If the HyperPlayer doesn't exist it will be created.  This method should only return null if the given name is null.
	 */
	public HyperPlayer getHyperPlayer(String player) {
		if (player == null || player.equals("")) {return null;}
		String playerName = player.toLowerCase();
		if (hyperPlayers.containsKey(playerName) && hyperPlayers.get(playerName) != null) {
			return hyperPlayers.get(playerName);
		} else {
			return addPlayer(player);
		}
	}
	public HyperPlayer getHyperPlayer(UUID uuid) {
		if (uuid == null) {return null;}
		if (uuidIndex.containsKey(uuid.toString())) {
			String pName = uuidIndex.get(uuid.toString());
			return hyperPlayers.get(pName.toLowerCase());
		}
		return null;
	}
	
	public ArrayList<HyperPlayer> getHyperPlayers() {
		ArrayList<HyperPlayer> hps = new ArrayList<HyperPlayer>();
		for (HyperPlayer hp:hyperPlayers.values()) {
			hps.add(hp);
		}
		return hps;
	}
	public ArrayList<String> getHyperPlayerNames() {
		ArrayList<String> names = new ArrayList<String>();
		for (String player:hyperPlayers.keySet()) {
			names.add(player);
		}
		return names;
	}
	
	public void removeHyperPlayer(HyperPlayer hp) {
		if (hp == null) return;
		if (hp.getName() != null) hyperPlayers.remove(hp.getName().toLowerCase());
		if (hp.getUUIDString() != null) uuidIndex.remove(hp.getUUIDString());
	}
	public void addHyperPlayer(HyperPlayer hp) {
		hyperPlayers.put(hp.getName().toLowerCase(), hp);
		if (hp.getUUIDString() != null) uuidIndex.put(hp.getUUIDString(), hp.getName().toLowerCase());
	}
	
	 

	public HyperPlayer addPlayer(String player) {
		if (player == null || player.equalsIgnoreCase("")) return null;
		if (!playersLoaded) {
			String trace = "";
			for (StackTraceElement e:Thread.currentThread().getStackTrace()) {
				trace += e.getMethodName() + ", ";
			}
			hc.getDebugMode().ayncDebugConsoleMessage("addPlayer() called before players loaded for: " + player + "; trace: " + trace);
			return null;
		}
		String playerName = player.toLowerCase();
		if (!hyperPlayers.containsKey(playerName)) {
			//dm.getHyperBankManager().renameBanksWithThisName(playerName);
			HyperPlayer newHp = new HyperPlayer(hc, player);
			hyperPlayers.put(playerName, newHp);
			if (newHp.getUUIDString() != null) uuidIndex.put(newHp.getUUIDString(), playerName);
			return newHp;
		} else {
			HyperPlayer hp = hyperPlayers.get(playerName);
			if (hp != null) {
				return hp;
			} else {
				HyperPlayer newHp = new HyperPlayer(hc, player);
				hyperPlayers.put(playerName, newHp);
				if (newHp.getUUIDString() != null) uuidIndex.put(newHp.getUUIDString(), playerName);
				return newHp;
			}
		}
	}
	
	public int purgeDeadAccounts() {
		int purgeCount = 0;
		for (HyperPlayer hp:getHyperPlayers()) {
			if (hp.safeToDelete()) {
				hp.delete();
				purgeCount++;
			}
		}
		return purgeCount;
	}
	
	
}
