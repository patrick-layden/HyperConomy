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
import org.bukkit.scheduler.BukkitTask;

public class DataHandler implements Listener {
	private HyperConomy hc;
	private SQLRead sr;
	private boolean objectsLoaded;
	private ConcurrentHashMap<String, HyperObject> hyperObjects = new ConcurrentHashMap<String, HyperObject>();
	private ConcurrentHashMap<String, HyperPlayer> hyperPlayers = new ConcurrentHashMap<String, HyperPlayer>();
	private BukkitTask waitToLoad;
	private BukkitTask waitForLoad;
	private ArrayList<String> economies = new ArrayList<String>();
	private boolean loadActive;

	DataHandler() {
		hc = HyperConomy.hc;
		sr = hc.getSQLRead();
		objectsLoaded = false;
		loadActive = false;
		hc.getServer().getPluginManager().registerEvents(this, hc);
	}
	
	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerJoin(PlayerJoinEvent event) {
		if (!objectsLoaded) {
			return;
		}
		String name = event.getPlayer().getName();
		if (!hasAccount(name)) {
			addPlayer(name);
		}
	}
	
	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerQuit(PlayerQuitEvent event) {
		if (!objectsLoaded) {
			return;
		}
		Location l = event.getPlayer().getLocation();
		String name = event.getPlayer().getName();
		if (!hasAccount(name)) {
			addPlayer(name);
		}
		HyperPlayer hp = hyperPlayers.get(name);
		if (hp == null) {
			return;
		}
		hp.setX(l.getX());
		hp.setY(l.getY());
		hp.setZ(l.getZ());
		hp.setWorld(l.getWorld().getName());
	}

	/*
	public HyperObject getHyperObject(String key) {
		if (hyperObjects.containsKey(key)) {
			return hyperObjects.get(key);
		} else {
			return null;
		}
	}
	*/
	
	
	public HyperObject getHyperObject(int id, int data, String economy) {
		for (HyperObject ho:hyperObjects.values()) {
			if (ho.getId() == id && ho.getData() == data && ho.getEconomy().equalsIgnoreCase(economy)) {
				return ho;
			}
		}
		return null;
	}
	
	public HyperObject getHyperObject(String name, String economy) {
		String key = name + ":" + economy;
		if (hyperObjects.containsKey(key)) {
			return hyperObjects.get(key);
		} else {
			return null;
		}
	}
	
	public ArrayList<HyperObject> getHyperObjects() {
		ArrayList<HyperObject> hos = new ArrayList<HyperObject>();
		for (HyperObject ho:hyperObjects.values()) {
			hos.add(ho);
		}
		return hos;
	}
	
	public ArrayList<HyperObject> getHyperObjects(String economy) {
		ArrayList<HyperObject> hos = new ArrayList<HyperObject>();
		for (HyperObject ho:hyperObjects.values()) {
			if (ho.getEconomy().equalsIgnoreCase(economy)) {
				hos.add(ho);
			}
		}
		return hos;
	}
	
	
	
	public HyperPlayer getHyperPlayer(String player) {
		player = fixpN(player);
		if (hyperPlayers.containsKey(player) && hyperPlayers.get(player) != null) {
			return hyperPlayers.get(player);
		} else {
			if (hyperPlayers.get(player) == null) {
				hyperPlayers.remove(player);
			}
			return addPlayer(player);
		}
	}
	
	
	public HyperPlayer getHyperPlayer(Player player) {
		String name = player.getName();
		if (hyperPlayers.containsKey(name) && hyperPlayers.get(name) != null) {
			return hyperPlayers.get(name);
		} else {
			if (hyperPlayers.get(name) == null) {
				hyperPlayers.remove(name);
			}
			return addPlayer(name);
		}
	}
	
	public ArrayList<HyperPlayer> getHyperPlayers() {
		ArrayList<HyperPlayer> hps = new ArrayList<HyperPlayer>();
		for (HyperPlayer hp:hyperPlayers.values()) {
			hps.add(hp);
		}
		return hps;
	}
	
	
	public String testName(String name, String economy) {
		if (!hyperObjects.containsKey(name + ":" + economy)) {
			return null;
		} else {
			return name;
		}
	}

	public void load() {
		if (!loadActive) {
			loadActive = true;
			objectsLoaded = false;
			hc.loadLock(true);
			waitToLoad = hc.getServer().getScheduler().runTaskTimer(hc, new Runnable() {
				public void run() {
					SQLWrite sw = hc.getSQLWrite();
					if (sw.getBuffer().size() == 0 && !sw.initialWrite()) {
						loadSQL();
						waitForLoad();
						waitToLoad.cancel();
					}
				}
			}, 0L, 10L);
		}
	}
	
	
	private void waitForLoad() {
		waitForLoad = hc.getServer().getScheduler().runTaskTimer(hc, new Runnable() {
			public void run() {
				if (objectsLoaded) {
					hc.onDataLoad();
					waitForLoad.cancel();
					for (Player p : Bukkit.getOnlinePlayers()) {
						if (!hasAccount(p.getName())) {
							addPlayer(p.getName());
						}
					}
					loadActive = false;
				}
			}
		}, 3L, 3L);
	}

	private void loadSQL() {
		hyperObjects.clear();
		hyperPlayers.clear();
		hc.getServer().getScheduler().runTaskAsynchronously(hc, new Runnable() {
			public void run() {
				QueryResult result = sr.getDatabaseConnection().read("SELECT * FROM hyperconomy_objects");
				while (result.next()) {
					HyperObject hobj = new HyperObject(result.getString("NAME"), result.getString("ECONOMY"), 
							result.getString("TYPE"), result.getString("CATEGORY"), result.getString("MATERIAL"), 
							result.getInt("ID"), result.getInt("DATA"), result.getInt("DURABILITY"), result.getDouble("VALUE"), 
							result.getString("STATIC"), result.getDouble("STATICPRICE"), result.getDouble("STOCK"),
							result.getDouble("MEDIAN"), result.getString("INITIATION"), result.getDouble("STARTPRICE"), result
							.getDouble("CEILING"), result.getDouble("FLOOR"), result.getDouble("MAXSTOCK"));
					hyperObjects.put(hobj.getName() + ":" + hobj.getEconomy(), hobj);
				}
				result.close();

				result = sr.getDatabaseConnection().read("SELECT * FROM hyperconomy_players");
				while (result.next()) {
					HyperPlayer hplayer = new HyperPlayer();
					hplayer.setName(result.getString("PLAYER"));
					hplayer.setEconomy(result.getString("ECONOMY"));
					hplayer.setBalance(result.getDouble("BALANCE"));
					hplayer.setX(result.getDouble("X"));
					hplayer.setY(result.getDouble("Y"));
					hplayer.setZ(result.getDouble("Z"));
					hplayer.setWorld(result.getString("WORLD"));
					hplayer.setHash(result.getString("HASH"));
					hplayer.setSalt(result.getString("SALT"));
					hyperPlayers.put(hplayer.getName(), hplayer);
				}
				result.close();

				economies = sr.getStringColumn("SELECT DISTINCT ECONOMY FROM hyperconomy_objects");
				objectsLoaded = true;
			}
		});
	}

	public ArrayList<String> getObjectKeys() {
		ArrayList<String> keys = new ArrayList<String>();
		for (String key:hyperObjects.keySet()) {
			keys.add(key);
		}
		return keys;
	}



	public HyperPlayer addPlayer(String player) {
		player = fixpN(player);
		if (!hyperPlayers.containsKey(player)) {
			return hyperPlayers.put(player, new HyperPlayer(player));
		} else {
			HyperPlayer hp = hyperPlayers.get(player);
			if (hp != null) {
				return hp;
			} else {
				hyperPlayers.remove(player);
				return hyperPlayers.put(player, new HyperPlayer(player));
			}
		}
	}



	public boolean hasAccount(String name) {
		return hyperPlayers.containsKey(fixpN(name));
	}



	public boolean createPlayerAccount(String player) {
		player = fixpN(player);
		if (!hasAccount(player)) {
			addPlayer(player);
			return true;
		} else {
			return false;
		}
	}

	
	public ArrayList<String> getEconPlayers() {
		ArrayList<String> econplayers = new ArrayList<String>();
		for (String player:hyperPlayers.keySet()) {
			econplayers.add(player);
		}
		return econplayers;
	}

	
	public String formatSQLiteTime(int time) {
		if (time < 0) {
			return "-" + Math.abs(time);
		} else if (time > 0) {
			return "+" + time;
		} else {
			return "0";
		}
	}


	public boolean testEconomy(String economy) {
		if (economies.contains(economy)) {
			return true;
		} else {
			return false;
		}
	}


	public ArrayList<String> getEconomyList() {
		ArrayList<String> econs = new ArrayList<String>();
		for (int i = 0; i < economies.size(); i++) {
			if (!econs.contains(economies.get(i))) {
				econs.add(economies.get(i));
			}
		}
		return econs;
	}

	public boolean objectsLoaded() {
		return objectsLoaded;
	}

	//public boolean dataBuilt() {
	//	return databuilt;
	//}

	//public void reset() {
	//	objectsLoaded = false;
		//databuilt = false;
	//}

	public void clearData() {
		hyperObjects.clear();
		hyperPlayers.clear();
		economies.clear();
	}



	public String fixpN(String player) {
		for (String name:hyperPlayers.keySet()) {
			if (name.equalsIgnoreCase(player)) {
				return name;
			}
		}
		return player;
	}
	
	
	
	public ArrayList<String> getNames() {
		ArrayList<String> names = new ArrayList<String>();
		for (HyperObject ho:hyperObjects.values()) {
			if (ho.getEconomy().equalsIgnoreCase("default")) {
				names.add(ho.getName());
			}
		}
		return names;
	}

	public ArrayList<String> getItemNames() {
		ArrayList<String> names = new ArrayList<String>();
		for (HyperObject ho:hyperObjects.values()) {
			if (ho.getEconomy().equalsIgnoreCase("default") && (ho.getType() == HyperObjectType.ITEM || ho.getType() == HyperObjectType.EXPERIENCE)) {
				names.add(ho.getName());
			}
		}
		return names;
	}

	public ArrayList<String> getEnchantNames() {
		ArrayList<String> names = new ArrayList<String>();
		for (HyperObject ho:hyperObjects.values()) {
			if (ho.getEconomy().equalsIgnoreCase("default") && ho.getType() == HyperObjectType.ENCHANTMENT) {
				names.add(ho.getName());
			}
		}
		return names;
	}
	
	//public String getnameData(String key) {
	//	return namedata.get(key);
	//}

	//public String getEnchantData(String key) {
	//	return enchantdata.get(key);
	//}
	
	public String getEnchantNameWithoutLevel(String bukkitName) {
		for (HyperObject ho:hyperObjects.values()) {
			if (ho.getType() == HyperObjectType.ENCHANTMENT && ho.getMaterial().equalsIgnoreCase(bukkitName)) {
				String name = ho.getName();
				return name.substring(0, name.length() - 1);
			}
		}
		return null;
	}
	
	public boolean objectTest(String name) {
		for (HyperObject ho:hyperObjects.values()) {
			if (ho.getName().equalsIgnoreCase(name)) {
				return true;
			}
		}
		return false;
	}
	
	
	public boolean itemTest(String name) {
		for (HyperObject ho:hyperObjects.values()) {
			if (ho.getName().equals(name) && ho.getType() != HyperObjectType.ENCHANTMENT) {
				return true;
			}
		}
		return false;
	}
	

	public boolean enchantTest(String name) {
		for (HyperObject ho:hyperObjects.values()) {
			if (ho.getName().equalsIgnoreCase(name) && ho.getType() == HyperObjectType.ENCHANTMENT) {
				return true;
			}
		}
		return false;
	}
	
	public String fixName(String nam) {
		for (String name:getNames()) {
			if (name.equalsIgnoreCase(nam)) {
				return name;
			}
		}
		return nam;
	}
	
	public String fixNameTest(String nam) {
		ArrayList<String> names = getNames();
		for (int i = 0; i < names.size(); i++) {
			if (names.get(i).equalsIgnoreCase(nam)) {
				return names.get(i);
			}
		}
		return null;
	}
	
	public void shutDown() {
		QueryResult result = sr.getDatabaseConnection().read("SELECT PLAYER FROM hyperconomy_players");
		ArrayList<String> inDatabase = new ArrayList<String>();
		while (result.next()) {
			inDatabase.add(result.getString("PLAYER"));
		}
		result.close();
		for (String player:hyperPlayers.keySet()) {
			if (!inDatabase.contains(player)) {
				hyperPlayers.get(player).save();
			}
		}
	}

}
