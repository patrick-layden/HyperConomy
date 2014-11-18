package regalowl.hyperconomy.account;

import java.util.ArrayList;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import regalowl.simpledatalib.sql.QueryResult;
import regalowl.hyperconomy.HyperConomy;
import regalowl.hyperconomy.api.MineCraftConnector;
import regalowl.hyperconomy.event.DataLoadEvent;
import regalowl.hyperconomy.event.DataLoadEvent.DataLoadType;
import regalowl.hyperconomy.event.minecraft.HPlayerJoinEvent;
import regalowl.hyperconomy.event.minecraft.HPlayerQuitEvent;
import regalowl.hyperconomy.minecraft.HLocation;
import regalowl.simpledatalib.event.EventHandler;
import regalowl.simpledatalib.file.FileConfiguration;

public class HyperPlayerManager {

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
	
	
	@EventHandler
	public void onDataLoad(DataLoadEvent event) {
		if (!(event.loadType == DataLoadType.ECONOMY)) return;
		new Thread(new Runnable() {
			public void run() {
				loadData();
			}
		}).start();
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
			public void run() {
				playersLoaded = true;
				if (!accountExists(defaultServerShopAccount)) {
					HyperPlayer defaultAccount = addPlayer(defaultServerShopAccount);
					defaultAccount.setBalance(hc.getConf().getDouble("shop.default-server-shop-account-initial-balance"));
					defaultAccount.setUUID(UUID.randomUUID().toString());
				}
				addOnlinePlayers();
				hc.getHyperEventHandler().fireEventFromAsyncThread(new DataLoadEvent(DataLoadType.PLAYER));
				hc.getDebugMode().ayncDebugConsoleMessage("Players loaded.");
			}
		});
	}
	
	
	private void addOnlinePlayers() {
		for (HyperPlayer p : getOnlinePlayers()) {
			if (p.getName().equalsIgnoreCase(config.getString("shop.default-server-shop-account"))) {
				p.kickPlayer(hc.getLanguageFile().get("CANT_USE_ACCOUNT"));
				continue;
			}
			if (!playerAccountExists(p.getName())) {
				addPlayer(p.getName());
			}
		}
	}
	
	public ArrayList<HyperPlayer> getOnlinePlayers() {
		return hc.getMC().getOnlinePlayers();
	}
	
	
	@EventHandler
	public void onHyperPlayerJoinEvent(HPlayerJoinEvent event) {
		HPlayerJoinEvent ev = (HPlayerJoinEvent) event;
		try {
			if (hc.getHyperLock().loadLock()) {
				return;
			}
			String name = ev.getHyperPlayer().getName();
			if (name.equalsIgnoreCase(config.getString("shop.default-server-shop-account"))) {
				hc.getMC().kickPlayer(ev.getHyperPlayer(), hc.getLanguageFile().get("CANT_USE_ACCOUNT"));
			}
			if (!playerAccountExists(name)) {
				addPlayer(name);
			} else {
				getHyperPlayer(name).checkUUID();
			}
		} catch (Exception e) {
			hc.gSDL().getErrorWriter().writeError(e);
		}
	}

	@EventHandler
	public void onPlayerQuit(HPlayerQuitEvent event) {
		try {
			if (hc.getHyperLock().loadLock()) {return;}
			HLocation l = event.getHyperPlayer().getLocation();
			String name = event.getHyperPlayer().getName();
			if (hyperPlayers.containsKey(name.toLowerCase())) {
				HyperPlayer hp = hyperPlayers.get(name.toLowerCase());
				if (hp == null) {return;}
				hp.setLocation(l);
			}
		} catch (Exception e) {
			hc.gSDL().getErrorWriter().writeError(e);
		}
	}

	public boolean uuidSupport() {
		return uuidSupport;
	}
	/*
	public boolean playerAccountExists(OfflinePlayer player) {
		if (player == null) {return false;}
		if (hc.getMC().useExternalEconomy()) {
			return hc.getMC().getEconomy().hasAccount(player);
		} else {
			return hyperPlayers.containsKey(player.getName());
		}
	}
	*/

	public boolean playerAccountExists(String name) {
		if (name == null || name == "") {return false;}
		if (hc.getMC().useExternalEconomy()) {
			return hc.getMC().getEconomyProvider().hasAccount(name);
		} else {
			return hyperPlayers.containsKey(name.toLowerCase());
		}
	}
	

	public boolean playerAccountExists(UUID uuid) {
		if (uuid == null) {return false;}
		if (hc.getMC().useExternalEconomy()) {
			return hc.getMC().getEconomyProvider().hasAccount(getHyperPlayer(uuid).getName());
		} else {
			return uuidIndex.containsKey(uuid.toString());
		}
	}
	
	public boolean accountExists(String name) {
		if (playerAccountExists(name) || hc.getDataManager().getHyperBankManager().hasBank(name)) {
			return true;
		}
		return false;
	}
	/**
	 * 
	 * @param name of account
	 * @return the HyperBank or HyperPlayer with the specified name.  This method will not create an account if it does not already exists.  Returns null if the account
	 * does not exist.
	 */
	public HyperAccount getAccount(String name) {
		if (playerAccountExists(name)) {
			return getHyperPlayer(name);
		}
		if (hc.getDataManager().getHyperBankManager().hasBank(name)) {
			return hc.getDataManager().getHyperBankManager().getHyperBank(name);
		}
		return null;
	}
	
	public HyperAccount getDefaultServerShopAccount() {
		return getAccount(defaultServerShopAccount);
	}
	
	
	

	/**
	 * 
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
			return hyperPlayers.get(pName);
		} else {
			MineCraftConnector mc = hc.getMC();
			if (!mc.playerExists(uuid)) return null;
			return mc.getPlayer(uuid);
		}
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
	

	public String fixpN(String player) {
		for (String name:hyperPlayers.keySet()) {
			if (name.equalsIgnoreCase(player)) {
				return name;
			}
		}
		return player;
	}
	
	public void removeHyperPlayer(HyperPlayer hp) {
		if (hyperPlayers.containsValue(hp)) {
			hyperPlayers.remove(hp.getName().toLowerCase());
			if (hp.getUUIDString() != null && uuidIndex.containsKey(hp.getUUIDString())) {
				uuidIndex.remove(hp.getUUIDString());
			}
		}
	}
	public void addHyperPlayer(HyperPlayer hp) {
		if (!hyperPlayers.containsValue(hp)) {
			hyperPlayers.put(hp.getName().toLowerCase(), hp);
			if (hp.getUUIDString() != null && hp.getName() != null) {
				uuidIndex.put(hp.getUUIDString(), hp.getName().toLowerCase());
			}
		}
	}
	
	 

	public HyperPlayer addPlayer(String player) {
		if (!playersLoaded) {
			hc.getDebugMode().ayncDebugConsoleMessage("addPlayer() called before players loaded for: " + player);
			return null;
		}
		String playerName = player.toLowerCase();
		if (!hyperPlayers.containsKey(playerName)) {
			//dm.getHyperBankManager().renameBanksWithThisName(playerName);
			HyperPlayer newHp = new HyperPlayer(hc, player);
			hyperPlayers.put(playerName, newHp);
			if (newHp.getUUIDString() != null && playerName != null) {
				uuidIndex.put(newHp.getUUIDString(), playerName);
			}
			return newHp;
		} else {
			HyperPlayer hp = hyperPlayers.get(playerName);
			if (hp != null) {
				return hp;
			} else {
				HyperPlayer newHp = new HyperPlayer(hc, player);
				hyperPlayers.put(playerName, newHp);
				if (newHp.getUUIDString() != null && playerName != null) {
					uuidIndex.put(newHp.getUUIDString(), playerName);
				}
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
