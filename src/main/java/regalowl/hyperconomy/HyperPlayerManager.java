package regalowl.hyperconomy;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.Bukkit;
import org.bukkit.Location;
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
	private ConcurrentHashMap<String, HyperPlayer> hyperPlayers = new ConcurrentHashMap<String, HyperPlayer>();
	
	public HyperPlayerManager(DataManager dm) {
		hc = HyperConomy.hc;
		this.dm = dm;
		playersLoaded = false;
		config = hc.getConf();
		defaultServerShopAccount = config.getString("shop.default-server-shop-account");
		hc.getServer().getPluginManager().registerEvents(this, hc);
	}

	public void loadData() {
		hyperPlayers.clear();
		QueryResult playerData = hc.getSQLRead().select("SELECT * FROM hyperconomy_players");
		while (playerData.next()) {
			HyperPlayer hplayer = new HyperPlayer(playerData.getString("NAME"), playerData.getString("UUID"), playerData.getString("ECONOMY"), 
					playerData.getDouble("BALANCE"), playerData.getDouble("X"), playerData.getDouble("Y"), playerData.getDouble("Z"), 
					playerData.getString("WORLD"), playerData.getString("HASH"), playerData.getString("SALT"));
			hyperPlayers.put(hplayer.getName().toLowerCase(), hplayer);
		}
		playerData.close();
		playersLoaded = true;
		if (!accountExists(defaultServerShopAccount)) {
			HyperAccount defaultAccount = addPlayer(defaultServerShopAccount);
			defaultAccount.setBalance(hc.getConfig().getDouble("shop.default-server-shop-account-initial-balance"));
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
			if (!hyperPlayerExists(p.getName())) {
				addPlayer(p.getName());
			}
		}
	}
	
	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerJoin(PlayerJoinEvent event) {
		try {
			if (hc.getHyperLock().loadLock()) {return;}
			String name = event.getPlayer().getName();
			if (name.equalsIgnoreCase(config.getString("shop.default-server-shop-account"))) {
				event.getPlayer().kickPlayer(hc.getLanguageFile().get("CANT_USE_ACCOUNT"));
			}
			if (!hyperPlayerExists(name)) {
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
			if (!hyperPlayerExists(name)) {
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

	
	
	
	public boolean hyperPlayerExists(String name) {
		if (name == null) {return false;}
		String playerName = name.toLowerCase();
		if (hc.useExternalEconomy()) {
			return hc.getEconomy().hasAccount(name);
		} else {
			return hyperPlayers.containsKey(playerName);
		}
	}
	public boolean accountExists(String name) {
		if (hyperPlayerExists(name) || dm.hasBank(name)) {
			return true;
		}
		return false;
	}
	public HyperAccount getAccount(String name) {
		if (hyperPlayerExists(name)) {
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
		}
	}
	public void addHyperPlayer(HyperPlayer hp) {
		if (!hyperPlayers.contains(hp)) {
			hyperPlayers.put(hp.getName().toLowerCase(), hp);
		}
	}
	
	 

	public HyperPlayer addPlayer(String player) {
		if (!playersLoaded) {return null;}
		String playerName = player.toLowerCase();
		if (!hyperPlayers.containsKey(playerName)) {
			dm.renameBanksWithThisName(playerName);
			HyperPlayer newHp = new HyperPlayer(player);
			hyperPlayers.put(playerName, newHp);
			return newHp;
		} else {
			HyperPlayer hp = hyperPlayers.get(playerName);
			if (hp != null) {
				return hp;
			} else {
				hyperPlayers.remove(playerName);
				HyperPlayer newHp = new HyperPlayer(player);
				hyperPlayers.put(playerName, newHp);
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
