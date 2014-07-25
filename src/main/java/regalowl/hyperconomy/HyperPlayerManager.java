package regalowl.hyperconomy;

import java.util.ArrayList;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import regalowl.databukkit.sql.QueryResult;
import regalowl.hyperconomy.account.HyperAccount;
import regalowl.hyperconomy.account.HyperPlayer;
import regalowl.hyperconomy.util.HyperConfig;

public class HyperPlayerManager implements Listener {

	private HyperConomy hc;
	private DataManager dm;
	private boolean playersLoaded;
	private String defaultServerShopAccount;
	private HyperConfig config;
	private boolean uuidSupport;
	private ConcurrentHashMap<String, HyperPlayer> hyperPlayers = new ConcurrentHashMap<String, HyperPlayer>();
	private ConcurrentHashMap<String, String> uuids = new ConcurrentHashMap<String, String>();
	
	public HyperPlayerManager(DataManager dm) {
		hc = HyperConomy.hc;
		this.dm = dm;
		playersLoaded = false;
		config = hc.getConf();
		defaultServerShopAccount = config.getString("shop.default-server-shop-account");
		uuidSupport = config.getBoolean("enable-feature.uuid-support");
		hc.getServer().getPluginManager().registerEvents(this, hc);
	}

	public void loadData() {
		hyperPlayers.clear();
		uuids.clear();
		QueryResult playerData = hc.getSQLRead().select("SELECT * FROM hyperconomy_players");
		while (playerData.next()) {
			HyperPlayer hplayer = new HyperPlayer(playerData.getString("NAME"), playerData.getString("UUID"), playerData.getString("ECONOMY"), 
					playerData.getDouble("BALANCE"), playerData.getDouble("X"), playerData.getDouble("Y"), playerData.getDouble("Z"), 
					playerData.getString("WORLD"), playerData.getString("HASH"), playerData.getString("SALT"));
			hyperPlayers.put(hplayer.getName().toLowerCase(), hplayer);
			uuids.put(hplayer.getName().toLowerCase(), hplayer.getUUIDString());
		}
		playerData.close();
		playersLoaded = true;
		if (!accountExists(defaultServerShopAccount)) {
			HyperPlayer defaultAccount = addPlayer(defaultServerShopAccount);
			defaultAccount.setBalance(hc.getConfig().getDouble("shop.default-server-shop-account-initial-balance"));
			defaultAccount.setUUID(UUID.randomUUID().toString());
		}
		hc.getServer().getScheduler().runTask(hc, new Runnable() {
			public void run() {
				addOnlinePlayers();
			}
		});
		hc.getDebugMode().ayncDebugConsoleMessage("Players loaded.");
	}
	
	private void addOnlinePlayers() {
		for (Player p : Bukkit.getOnlinePlayers()) {
			if (p.getName().equalsIgnoreCase(config.getString("shop.default-server-shop-account"))) {
				p.kickPlayer(hc.getLanguageFile().get("CANT_USE_ACCOUNT"));
			}
			if (!playerAccountExists(p.getName())) {
				addPlayer(p.getName());
			}
		}
	}
	
	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerJoin(PlayerJoinEvent event) {
		try {
			if (hc.getHyperLock().loadLock()) {return;}//not safe to do anything while HyperConomy is loading
			String name = event.getPlayer().getName();
			if (name.equalsIgnoreCase(config.getString("shop.default-server-shop-account"))) {
				event.getPlayer().kickPlayer(hc.getLanguageFile().get("CANT_USE_ACCOUNT"));
			}
			if (!playerAccountExists(name)) {
				addPlayer(name);
			} else {
				HyperPlayer hp = getHyperPlayer(name);
				hp.checkUUID();
			}
		} catch (Exception e) {
			hc.gDB().writeError(e);
		}
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerQuit(PlayerQuitEvent event) {
		try {
			if (hc.getHyperLock().loadLock()) {return;}
			Location l = event.getPlayer().getLocation();
			String name = event.getPlayer().getName();
			if (!playerAccountExists(name)) {
				addPlayer(name);
			}
			if (hyperPlayers.containsKey(name.toLowerCase())) {
				HyperPlayer hp = hyperPlayers.get(name.toLowerCase());
				if (hp == null) {return;}
				hp.setX(l.getX());
				hp.setY(l.getY());
				hp.setZ(l.getZ());
				hp.setWorld(l.getWorld().getName());
			}
		} catch (Exception e) {
			hc.gDB().writeError(e);
		}
	}

	public boolean uuidSupport() {
		return uuidSupport;
	}
	
	public boolean playerAccountExists(OfflinePlayer player) {
		if (player == null) {return false;}
		if (hc.useExternalEconomy()) {
			return hc.getEconomy().hasAccount(player);
		} else {
			return hyperPlayers.containsKey(player.getName());
		}
	}

	public boolean playerAccountExists(String name) {
		if (name == null || name == "") {return false;}
		String sUuid = uuids.get(name.toLowerCase());
		if (sUuid == null) {return false;}
		UUID id = UUID.fromString(sUuid);
		return playerAccountExists(Bukkit.getOfflinePlayer(id));
	}
	
	public boolean accountExists(String name) {
		if (playerAccountExists(name) || dm.hasBank(name)) {
			return true;
		}
		return false;
	}
	public HyperAccount getAccount(String name) {
		if (playerAccountExists(name)) {
			return getHyperPlayer(name);
		}
		if (dm.hasBank(name)) {
			return dm.getHyperBank(name);
		}
		return null;
	}
	
	public HyperAccount getDefaultServerShopAccount() {
		return getAccount(defaultServerShopAccount);
	}
	
	
	

	
	public HyperPlayer getHyperPlayer(String player) {
		if (player == null || player.equals("")) {return null;}
		String playerName = player.toLowerCase();
		if (hyperPlayers.containsKey(playerName) && hyperPlayers.get(playerName) != null) {
			return hyperPlayers.get(playerName);
		} else {
			if (hyperPlayers.get(playerName) == null) {
				hyperPlayers.remove(playerName);
				uuids.remove(playerName);
			}
			return addPlayer(player);
		}
	}
	public HyperPlayer getHyperPlayer(Player player) {
		if (player == null) {return null;}
		return getHyperPlayer(player.getName());
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
		if (hyperPlayers.contains(hp)) {
			hyperPlayers.remove(hp.getName().toLowerCase());
			uuids.remove(hp.getName().toLowerCase());
		}
	}
	public void addHyperPlayer(HyperPlayer hp) {
		if (!hyperPlayers.contains(hp)) {
			hyperPlayers.put(hp.getName().toLowerCase(), hp);
			uuids.put(hp.getName().toLowerCase(), hp.getUUIDString());
		}
	}
	
	 

	public HyperPlayer addPlayer(String player) {
		if (!playersLoaded) {return null;}
		String playerName = player.toLowerCase();
		if (!hyperPlayers.containsKey(playerName)) {
			dm.renameBanksWithThisName(playerName);
			HyperPlayer newHp = new HyperPlayer(player);
			hyperPlayers.put(playerName, newHp);
			uuids.put(playerName, newHp.getUUIDString());
			return newHp;
		} else {
			HyperPlayer hp = hyperPlayers.get(playerName);
			if (hp != null) {
				return hp;
			} else {
				hyperPlayers.remove(playerName);
				uuids.remove(playerName);
				HyperPlayer newHp = new HyperPlayer(player);
				hyperPlayers.put(playerName, newHp);
				uuids.put(playerName, newHp.getUUIDString());
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
