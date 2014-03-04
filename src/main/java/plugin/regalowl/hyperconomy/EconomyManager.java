package regalowl.hyperconomy;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitTask;

import regalowl.databukkit.QueryResult;
import regalowl.databukkit.SQLRead;
import regalowl.databukkit.SQLWrite;

public class EconomyManager implements Listener {

	private HyperConomy hc;
	private SQLRead sr;
	private boolean dataLoaded;
	private boolean playersLoaded;
	private boolean loadActive;
	private boolean economiesLoaded;
	
	private ConcurrentHashMap<String, HyperEconomy> economies = new ConcurrentHashMap<String, HyperEconomy>();
	private ConcurrentHashMap<String, HyperPlayer> hyperPlayers = new ConcurrentHashMap<String, HyperPlayer>();
	private ConcurrentHashMap<String, HyperBank> hyperBanks = new ConcurrentHashMap<String, HyperBank>();
	private ConcurrentHashMap<String, Shop> shops = new ConcurrentHashMap<String, Shop>();
	
	private DatabaseUpdater du;
	
	private long shopinterval;
	private BukkitTask shopCheckTask;
	private boolean useShops;
	
	public final double version = 1.27;
	
	
	
	
	public EconomyManager() {
		hc = HyperConomy.hc;
		economiesLoaded = false;
		dataLoaded = false;
		playersLoaded = false;
		loadActive = false;
		useShops = hc.gYH().gFC("config").getBoolean("config.use-shops");
		shopinterval = hc.gYH().gFC("config").getLong("config.shopcheckinterval");
		du = new DatabaseUpdater();
		hc.getServer().getPluginManager().registerEvents(this, hc);
	}
	
	public double getVersion() {
		return version;
	}
	
	public ArrayList<String> getTablesList() {
		return du.getTablesList();
	}
	
	public DatabaseUpdater getDatabaseUpdater() {
		return du;
	}
	

	
	public void load() {
		if (loadActive) {return;}
		loadActive = true;
		try {
			hc = HyperConomy.hc;
			sr = hc.getSQLRead();
			String query = "SELECT VALUE FROM hyperconomy_settings WHERE SETTING = 'version'";
			hc.getSQLRead().setErrorLogging(false);
			hc.getSQLRead().syncRead(du, "updateTables", query, null);
		} catch (Exception e) {
			hc.gDB().writeError(e);
		}
	}
	public void load2(QueryResult qr) {
		if (!qr.next()) {
			HyperConomy.hc.getEconomyManager().createEconomyFromYml("default", false);
		}
		hc.getSQLWrite().afterWrite(this, "load3");
	}
	public void load3() {
		hc.getEconomyManager().load4();
	}
	public void load4() {
		hc.getServer().getScheduler().runTaskAsynchronously(hc, new Runnable() {
			public void run() {
				economies.clear();
				ArrayList<String> econs = sr.getStringList("hyperconomy_economies", "NAME", null);
				for (String e : econs) {
					economies.put(e, new HyperEconomy(e));
				}
				waitForEconomyLoad();
			}
		});
	}
	private void waitForEconomyLoad() {
		hc.getServer().getScheduler().runTaskLater(hc, new Runnable() {
			public void run() {
				if (economiesLoaded) {return;}
				for (HyperEconomy he : getEconomies()) {
					if (!he.dataLoaded()) {
						waitForEconomyLoad();
						return;
					}
				}
				economiesLoaded = true;
				hc.getHyperEventHandler().fireEconomyLoadEvent();
				loadRemainingData();
			}
		}, 1L);
	}
	
	
	private void loadRemainingData() {
		hc.getServer().getScheduler().runTaskAsynchronously(hc, new Runnable() {
			public void run() {
				hyperPlayers.clear();
				QueryResult result = sr.select("SELECT * FROM hyperconomy_players");
				while (result.next()) {
					HyperPlayer hplayer = new HyperPlayer(result.getString("PLAYER"), result.getString("ECONOMY"), result.getDouble("BALANCE"), result.getDouble("X"), result.getDouble("Y"), result.getDouble("Z"), result.getString("WORLD"), result.getString("HASH"), result.getString("SALT"));
					hyperPlayers.put(hplayer.getName().toLowerCase(), hplayer);
				}
				result.close();
				playersLoaded = true;
				hyperBanks.clear();
				result = sr.select("SELECT * FROM hyperconomy_banks");
				while (result.next()) {
					HyperBank hBank = new HyperBank(result.getString("NAME"), result.getDouble("BALANCE"), result.getString("OWNERS"), result.getString("MEMBERS"));
					hyperBanks.put(hBank.getName().toLowerCase(), hBank);
				}
				result.close();
				
				shops.clear();
				if (useShops) {
					result = sr.select("SELECT * FROM hyperconomy_shops");
					while (result.next()) {
						String type = result.getString("TYPE");
						if (type.equalsIgnoreCase("server")) {
							String name = result.getString("NAME");
							Location p1 = new Location(Bukkit.getWorld(result.getString("WORLD")), result.getInt("P1X"), result.getInt("P1Y"), result.getInt("P1Z"));
							Location p2 = new Location(Bukkit.getWorld(result.getString("WORLD")), result.getInt("P2X"), result.getInt("P2Y"), result.getInt("P2Z"));
							Shop shop = new ServerShop(name, result.getString("ECONOMY"), getHyperPlayer(result.getString("OWNER")), 
									result.getString("MESSAGE"), p1, p2, result.getString("BANNED_OBJECTS"));
							shops.put(name, shop);
						} else if (type.equalsIgnoreCase("player")) {
							if (!hc.gYH().gFC("config").getBoolean("config.use-player-shops")) {continue;}
							String name = result.getString("NAME");
							Location p1 = new Location(Bukkit.getWorld(result.getString("WORLD")), result.getInt("P1X"), result.getInt("P1Y"), result.getInt("P1Z"));
							Location p2 = new Location(Bukkit.getWorld(result.getString("WORLD")), result.getInt("P2X"), result.getInt("P2Y"), result.getInt("P2Z"));
							Shop shop = new PlayerShop(name, result.getString("ECONOMY"), getHyperPlayer(result.getString("OWNER")), 
									result.getString("MESSAGE"), p1, p2, result.getString("BANNED_OBJECTS"), result.getString("ALLOWED_PLAYERS"));
							shops.put(name, shop);
						}
					}
					result.close();
				} else {
					Shop shop = new ServerShop("GlobalShop", getGlobalShopAccount().getEconomy(), getGlobalShopAccount());
					shops.put("GlobalShop", shop);
				}
				stopShopCheck();
				startShopCheck();

				hc.getServer().getScheduler().runTask(hc, new Runnable() {
					public void run() {
						addOnlinePlayers();
						createGlobalShopAccount();
						waitForDataLoad();
					}
				});
			}
		});
	}
	
	private void waitForDataLoad() {
		hc.getServer().getScheduler().runTaskLater(hc, new Runnable() {
			public void run() {
				if (dataLoaded) {return;}
				for (Shop s : getShops()) {
					if (!s.isLoaded()) {
						waitForDataLoad();
						return;
					}
				}
				dataLoaded = true;
				if (hc.getEconomyManager().getDatabaseUpdater().updateAfterLoad()) {
					hc.restart();
					return;
				}
				hc.getHyperEventHandler().fireDataLoadEvent();
				loadActive = false;
				hc.getHyperLock().setLoadLock(false);
			}
		}, 1L);
	}
	

	public boolean dataLoaded() {
		return dataLoaded;
	}
	public HyperEconomy getEconomy(String name) {
		for (Map.Entry<String,HyperEconomy> entry : economies.entrySet()) {
			HyperEconomy he = entry.getValue();
			if (he.getName().equalsIgnoreCase(name)) {
				return he;
			}
		}
		return null;
	}
	public HyperEconomy getDefaultEconomy() {
		return getEconomy("default");
	}
	
	/*
	public void addEconomy(String name) {
		if (!economies.containsKey(name)) {
			economies.put(name, new HyperEconomy(name));
		}
	}
	*/
	
	public boolean economyExists(String economy) {
		for (Map.Entry<String,HyperEconomy> entry : economies.entrySet()) {
			HyperEconomy he = entry.getValue();
			if (he.getName().equalsIgnoreCase(economy)) {
				return true;
			}
		}
		return false;
	}
	
	public ArrayList<HyperEconomy> getEconomies() {
		ArrayList<HyperEconomy> econs = new ArrayList<HyperEconomy>();
		for (Map.Entry<String,HyperEconomy> entry : economies.entrySet()) {
			econs.add(entry.getValue());
		}
		return econs;
	}
	
	public void clearData() {
		for (HyperEconomy he: economies.values()) {
			he.clearData();
		}
		economies.clear();
	}


	public ArrayList<String> getEconomyList() {
		ArrayList<String> econs = new ArrayList<String>();
		for (Map.Entry<String,HyperEconomy> entry : economies.entrySet()) {
			HyperEconomy he = entry.getValue();
			econs.add(he.getName());
		}
		return econs;
	}


	
	public ArrayList<HyperObject> getHyperObjects() {
		ArrayList<HyperObject> hyperObjects = new ArrayList<HyperObject>();
		for (Map.Entry<String,HyperEconomy> entry : economies.entrySet()) {
			HyperEconomy he = entry.getValue();
			for (HyperObject ho:he.getHyperObjects()) {
				hyperObjects.add(ho);
			}
		}
		return hyperObjects;
	}



	

	
	public void createNewEconomy(String economy) {
		HyperEconomy defaultEconomy = getEconomy("default");
		SQLWrite sw = hc.getSQLWrite();
		HashMap<String,String> values = new HashMap<String,String>();
		values.put("NAME", economy);
		values.put("HYPERACCOUNT", hc.gYH().gFC("config").getString("config.global-shop-account"));
		sw.performInsert("hyperconomy_economies", values);
		for (HyperObject ho:defaultEconomy.getHyperObjects()) {
			values = new HashMap<String,String>();
			values.put("NAME", ho.getName());
			values.put("DISPLAY_NAME", ho.getDisplayName());
			values.put("ALIASES", ho.getAliasesString());
			values.put("ECONOMY", economy);
			values.put("TYPE", ho.getType().toString());
			values.put("VALUE", ho.getValue()+"");
			values.put("STATIC", ho.getIsstatic());
			values.put("STATICPRICE", ho.getStaticprice()+"");
			values.put("STOCK", 0+"");
			values.put("MEDIAN", ho.getMedian()+"");
			values.put("INITIATION", ho.getInitiation());
			values.put("STARTPRICE", ho.getStartprice()+"");
			values.put("CEILING", ho.getCeiling()+"");
			values.put("FLOOR", ho.getFloor()+"");
			values.put("MAXSTOCK", ho.getMaxstock()+"");
			if (ho instanceof HyperItem) {
				HyperItem hi = (HyperItem)ho;
				values.put("MATERIAL", hi.getMaterial());
				values.put("DATA", hi.getData()+"");
				values.put("DURABILITY", hi.getDurability()+"");
			} else if (ho instanceof HyperEnchant) {
				HyperEnchant he = (HyperEnchant)ho;
				values.put("MATERIAL", he.getEnchantmentName());
				values.put("DATA", "-1");
				values.put("DURABILITY", "-1");
			} else {
				values.put("MATERIAL", "none");
				values.put("DATA", "-1");
				values.put("DURABILITY", "-1");
			}
			sw.performInsert("hyperconomy_objects", values);
		}
		hc.restart();
	}
	
	public void deleteEconomy(String economy) {
		HashMap<String,String> conditions = new HashMap<String,String>();
		conditions.put("ECONOMY", economy);
		hc.getSQLWrite().performDelete("hyperconomy_objects", conditions);
		conditions = new HashMap<String,String>();
		conditions.put("NAME", economy);
		hc.getSQLWrite().performDelete("hyperconomy_economies", conditions);
		hc.restart();
	}

	
	
	public void createEconomyFromYml(String econ, boolean restart) {
		if (hc.gYH().gFC("config").getBoolean("config.run-automatic-backups")) {
			new Backup();
		}
		SQLWrite sw = hc.getSQLWrite();
		HashMap<String,String> values = new HashMap<String,String>();
		values.put("NAME", econ);
		values.put("HYPERACCOUNT", hc.gYH().gFC("config").getString("config.global-shop-account"));
		sw.performInsert("hyperconomy_economies", values);
		FileConfiguration objects = hc.gYH().gFC("objects");
		Iterator<String> it = objects.getKeys(false).iterator();
		while (it.hasNext()) {
			String itemname = it.next().toString();
			String category = objects.getString(itemname + ".information.category");
			if (category == null) {
				category = "unknown";
			}
			
			values = new HashMap<String,String>();
			values.put("NAME", itemname);
			values.put("ECONOMY", econ);
			values.put("DISPLAY_NAME", objects.getString(itemname + ".name.display"));
			values.put("ALIASES", objects.getString(itemname + ".name.aliases"));
			values.put("TYPE", objects.getString(itemname + ".information.type"));
			values.put("VALUE", objects.getDouble(itemname + ".value")+"");
			values.put("STATIC", objects.getString(itemname + ".price.static"));
			values.put("STATICPRICE", objects.getDouble(itemname + ".price.staticprice")+"");
			values.put("STOCK", objects.getDouble(itemname + ".stock.stock")+"");
			values.put("MEDIAN", objects.getDouble(itemname + ".stock.median")+"");
			values.put("INITIATION", objects.getString(itemname + ".initiation.initiation"));
			values.put("STARTPRICE", objects.getDouble(itemname + ".initiation.startprice")+"");
			values.put("CEILING", objects.getDouble(itemname + ".price.ceiling")+"");
			values.put("FLOOR", objects.getDouble(itemname + ".price.floor")+"");
			values.put("MAXSTOCK", objects.getDouble(itemname + ".stock.maxstock")+"");
			if (objects.getString(itemname + ".information.type").equalsIgnoreCase("item")) {
				values.put("MATERIAL", objects.getString(itemname + ".information.material"));
				values.put("DATA", objects.getInt(itemname + ".information.data")+"");
				values.put("DURABILITY", objects.getInt(itemname + ".information.data")+"");
			} else if (objects.getString(itemname + ".information.type").equalsIgnoreCase("enchantment")) {
				values.put("MATERIAL", objects.getString(itemname + ".information.material"));
				values.put("DATA", "-1");
				values.put("DURABILITY", "-1");
			} else if (objects.getString(itemname + ".information.type").equalsIgnoreCase("experience")) {
				values.put("MATERIAL", "none");
				values.put("DATA", "-1");
				values.put("DURABILITY", "-1");
			}
			sw.performInsert("hyperconomy_objects", values);
		}
		if (restart) {
			hc.restart();
		}
	}
	
	

	
	

	
	
	

	
	
	public HyperBank getHyperBank(String name) {
		if (name == null) {return null;}
		String bankName = name.toLowerCase();
		if (hyperBanks.containsKey(bankName)) {
			return hyperBanks.get(bankName);
		}
		return null;
	}

	public void addHyperBank(HyperBank hb) {
		if (hb == null) {return;}
		if (!hyperBanks.contains(hb)) {
			hyperBanks.put(hb.getName().toLowerCase(), hb);
		}
	}
	
	public void removeHyperBank(HyperBank hb) {
		if (hb == null) {return;}
		if (hyperBanks.contains(hb)) {
			hyperBanks.remove(hb.getName().toLowerCase());
		}
	}
	
	public boolean hasBank(String name) {
		if (name == null) {return false;}
		return hyperBanks.containsKey(name.toLowerCase());
	}
	
	public ArrayList<HyperBank> getHyperBanks() {
		ArrayList<HyperBank> hbs = new ArrayList<HyperBank>();
		for (HyperBank hb:hyperBanks.values()) {
			hbs.add(hb);
		}
		return hbs;
	}
	
	public ArrayList<String> getHyperBankNames() {
		ArrayList<String> hbs = new ArrayList<String>();
		for (HyperBank hb:hyperBanks.values()) {
			hbs.add(hb.getName());
		}
		return hbs;
	}
	
	
	
	
	
	
	
	
	
	
	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerJoin(PlayerJoinEvent event) {
		try {
			if (!dataLoaded()) {return;}
			String name = event.getPlayer().getName();
			if (name.equalsIgnoreCase(hc.gYH().gFC("config").getString("config.global-shop-account"))) {
				if (hc.gYH().gFC("config").getBoolean("config.block-player-with-same-name-as-global-shop-account")) {
					event.getPlayer().kickPlayer(hc.getLanguageFile().get("CANT_USE_ACCOUNT"));
				}
			}
			if (!hyperPlayerExists(name)) {
				addPlayer(name);
			}
		} catch (Exception e) {
			hc.gDB().writeError(e);
		}
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerQuit(PlayerQuitEvent event) {
		try {
			if (!dataLoaded()) {return;}
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
	
	private void addOnlinePlayers() {
		for (Player p : Bukkit.getOnlinePlayers()) {
			if (p.getName().equalsIgnoreCase(hc.gYH().gFC("config").getString("config.global-shop-account"))) {
				if (hc.gYH().gFC("config").getBoolean("config.block-player-with-same-name-as-global-shop-account")) {
					p.kickPlayer(hc.getLanguageFile().get("CANT_USE_ACCOUNT"));
				}
			}
			if (!hyperPlayerExists(p.getName())) {
				addPlayer(p.getName());
			}
		}
	}
	
	
	public boolean accountExists(String name) {
		if (hyperPlayerExists(name) || hasBank(name)) {
			return true;
		}
		return false;
	}
	
	public HyperAccount getAccount(String name) {
		if (hyperPlayerExists(name)) {
			return getHyperPlayer(name);
		}
		if (hasBank(name)) {
			return getHyperBank(name);
		}
		return null;
	}
	
	
	public boolean hyperPlayerExists(String name) {
		String playerName = name.toLowerCase();
		if (hc.useExternalEconomy()) {
			if (hc.getEconomy().hasAccount(name)) {
				if (!hyperPlayers.containsKey(playerName)) {
					addPlayer(name);
				}
				return true;
			} else {
				return false;
			}
		} else {
			return hyperPlayers.containsKey(playerName);
		}
	}
	

	public HyperPlayer getHyperPlayer(String player) {
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
		return getHyperPlayer(player.getName());
	}
	public ArrayList<HyperPlayer> getHyperPlayers() {
		ArrayList<HyperPlayer> hps = new ArrayList<HyperPlayer>();
		for (HyperPlayer hp:hyperPlayers.values()) {
			hps.add(hp);
		}
		return hps;
	}
	
	
	public void addHyperPlayer(HyperPlayer hp) {
		if (!hyperPlayers.contains(hp)) {
			hyperPlayers.put(hp.getName().toLowerCase(), hp);
		}
	}
	public void removeHyperPlayer(HyperPlayer hp) {
		if (hyperPlayers.contains(hp)) {
			hyperPlayers.remove(hp.getName().toLowerCase());
		}
	}
	
	

	
	

	public HyperPlayer addPlayer(String player) {
		if (!playersLoaded) {return null;}
		String playerName = player.toLowerCase();
		if (!hyperPlayers.containsKey(playerName)) {
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




/*
	public boolean createPlayerAccount(String player) {
		if (!hasAccount(player)) {
			addPlayer(player);
			return true;
		} else {
			return false;
		}
	}
*/
	
	public ArrayList<String> getEconPlayers() {
		ArrayList<String> econplayers = new ArrayList<String>();
		for (String player:hyperPlayers.keySet()) {
			econplayers.add(player);
		}
		return econplayers;
	}

	
	public String fixpN(String player) {
		for (String name:hyperPlayers.keySet()) {
			if (name.equalsIgnoreCase(player)) {
				return name;
			}
		}
		return player;
	}
	
	
	public void createGlobalShopAccount(){		
		HyperConomy hc = HyperConomy.hc;
		String globalAccount = hc.gYH().gFC("config").getString("config.global-shop-account");
		if (!accountExists(globalAccount)) {
			HyperPlayer ga = getHyperPlayer(globalAccount);
			Double initialBalance = hc.gYH().gFC("config").getDouble("config.initialshopbalance");
			ga.setBalance(initialBalance);
			String economyName = "HyperConomy";
			if (hc.useExternalEconomy()) {
				economyName = hc.getEconomy().getName();
			}
			hc.getLog().writeAuditLog(ga.getName(), "setbalance", initialBalance, economyName);
		}
	}
	
	public HyperPlayer getGlobalShopAccount() {
		return getHyperPlayer(hc.gYH().gFC("config").getString("config.global-shop-account"));
	}
	
	
	
	
	
	
	
	
	Shop getShop(Player player) {
		if (player == null) {
			return null;
		}
		for (Shop shop : shops.values()) {
			if (shop.inShop(player)) {
				return shop;
			}
		}
		return null;
	}
	public Shop getShop(String shop) {
		shop = fixShopName(shop);
		if (shops.containsKey(shop)) {
			return shops.get(shop);
		} else {
			return null;
		}
	}
	public boolean inAnyShop(Player player) {
		for (Shop shop : shops.values()) {
			if (shop.inShop(player)) {
				return true;
			}
		}
		return false;
	}
	public boolean shopExists(String name) {
		return shops.containsKey(fixShopName(name));
	}
	
	public void addShop(Shop shop) {
		shops.put(shop.getName(), shop);
		hc.getHyperEventHandler().fireShopCreationEvent(shop);
	}
	
	public void removeShop(String name) {
		if (shopExists(name)) {
			shops.remove(fixShopName(name));
		}
	}
	
	
	public void renameShop(String name, String newName) {
		Shop shop = shops.get(name);
		shop.setName(newName);
		shops.put(newName, shop);
		shops.remove(name);
	}
    public void startShopCheck() {
		shopCheckTask = hc.getServer().getScheduler().runTaskTimer(hc, new Runnable() {
		    public void run() {
				for (Shop shop:shops.values()) {
					shop.updatePlayerStatus();
				}
		    }
		}, shopinterval, shopinterval);
    }
    public void stopShopCheck() {
    	if (shopCheckTask != null) {
    		shopCheckTask.cancel();
    	}
    }
    public long getShopCheckInterval() {
    	return shopinterval;
    }
    public void setShopCheckInterval(long interval) {
    	shopinterval = interval;
    }
	public String fixShopName(String nam) {
		for (String shop:shops.keySet()) {
			if (shop.equalsIgnoreCase(nam)) {
				return shop;
			}
		}
		return nam;
	}
	public ArrayList<Shop> getShops() {
		ArrayList<Shop> shopList = new ArrayList<Shop>();
		for (Shop shop:shops.values()) {
			shopList.add(shop);
		}
		return shopList;
	}
	public ArrayList<Shop> getShops(HyperPlayer hp) {
		ArrayList<Shop> shopList = new ArrayList<Shop>();
		for (Shop shop:shops.values()) {
			if (shop.getOwner().equals(hp)) {
				shopList.add(shop);
			}
		}
		return shopList;
	}
	public ArrayList<String> listShops() {
		ArrayList<String> names = new ArrayList<String>();
		for (Shop shop : shops.values()) {
			names.add(shop.getName());
		}
		return names;
	}

	
	
}
