package regalowl.hyperconomy;

import java.util.ArrayList;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
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
	private ConcurrentHashMap<String, String> uuidIndex = new ConcurrentHashMap<String, String>();
	
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
		uuidIndex.clear();
		QueryResult playerData = hc.getSQLRead().select("SELECT * FROM hyperconomy_players");
		while (playerData.next()) {
			HyperPlayer hplayer = new HyperPlayer(playerData.getString("NAME"), playerData.getString("UUID"), playerData.getString("ECONOMY"), 
					playerData.getDouble("BALANCE"), playerData.getDouble("X"), playerData.getDouble("Y"), playerData.getDouble("Z"), 
					playerData.getString("WORLD"), playerData.getString("HASH"), playerData.getString("SALT"));
			hyperPlayers.put(hplayer.getName().toLowerCase(), hplayer);
			if (hplayer.getUUIDString() != null && hplayer.getName() != null) {
				uuidIndex.put(hplayer.getUUIDString(), hplayer.getName().toLowerCase());
			}
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
		for (Player p : getOnlinePlayers()) {
			if (p.getName().equalsIgnoreCase(config.getString("shop.default-server-shop-account"))) {
				p.kickPlayer(hc.getLanguageFile().get("CANT_USE_ACCOUNT"));
				continue;
			}
			if (!playerAccountExists(p.getName())) {
				addPlayer(p.getName());
			}
		}
	}
	
	public ArrayList<Player> getOnlinePlayers() {
		ArrayList<Player> onlinePlayers = new ArrayList<Player>();
		for (World world : Bukkit.getWorlds()) {
			onlinePlayers.addAll(world.getPlayers());
		}
		return onlinePlayers;
	}
	
	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerJoin(PlayerJoinEvent event) {
		try {
			if (hc.getHyperLock().loadLock()) {return;}
			String name = event.getPlayer().getName();
			if (name.equalsIgnoreCase(config.getString("shop.default-server-shop-account"))) {
				event.getPlayer().kickPlayer(hc.getLanguageFile().get("CANT_USE_ACCOUNT"));
			}
			if (!playerAccountExists(name)) {
				addPlayer(name);
			} else {
				getHyperPlayer(name).checkUUID();
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

	@SuppressWarnings("deprecation")
	public boolean playerAccountExists(String name) {
		if (name == null || name == "") {return false;}
		if (hc.useExternalEconomy()) {
			return hc.getEconomy().hasAccount(name);
		} else {
			return hyperPlayers.containsKey(name.toLowerCase());
		}
	}
	
	public boolean playerAccountExists(UUID uuid) {
		if (uuid == null) {return false;}
		if (hc.useExternalEconomy()) {
			return hc.getEconomy().hasAccount(Bukkit.getOfflinePlayer(uuid));
		} else {
			return uuidIndex.containsKey(uuid.toString());
		}
	}
	
	public boolean accountExists(String name) {
		if (playerAccountExists(name) || dm.getHyperBankManager().hasBank(name)) {
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
		if (dm.getHyperBankManager().hasBank(name)) {
			return dm.getHyperBankManager().getHyperBank(name);
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
	public HyperPlayer getHyperPlayer(Player player) {
		if (player == null) {return null;}
		return getHyperPlayer(player.getName());
	}
	public HyperPlayer getHyperPlayer(UUID uuid) {
		if (uuid == null) {return null;}
		if (uuidIndex.containsKey(uuid.toString())) {
			String pName = uuidIndex.get(uuid.toString());
			return hyperPlayers.get(pName);
		} else {
			OfflinePlayer op = Bukkit.getOfflinePlayer(uuid);
			if (op.getName() == null || op.getName() == "") {
				return null;
			}
			HyperPlayer hp = addPlayer(op.getName());
			hp.setUUID(uuid.toString());
			return hp;
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
		if (hyperPlayers.contains(hp)) {
			hyperPlayers.remove(hp.getName().toLowerCase());
			if (hp.getUUIDString() != null && uuidIndex.containsKey(hp.getUUIDString())) {
				uuidIndex.remove(hp.getUUIDString());
			}
		}
	}
	public void addHyperPlayer(HyperPlayer hp) {
		if (!hyperPlayers.contains(hp)) {
			hyperPlayers.put(hp.getName().toLowerCase(), hp);
			if (hp.getUUIDString() != null && hp.getName() != null) {
				uuidIndex.put(hp.getUUIDString(), hp.getName().toLowerCase());
			}
		}
	}
	
	 

	public HyperPlayer addPlayer(String player) {
		if (!playersLoaded) {return null;}
		String playerName = player.toLowerCase();
		if (!hyperPlayers.containsKey(playerName)) {
			//dm.getHyperBankManager().renameBanksWithThisName(playerName);
			HyperPlayer newHp = new HyperPlayer(player);
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
				HyperPlayer newHp = new HyperPlayer(player);
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
