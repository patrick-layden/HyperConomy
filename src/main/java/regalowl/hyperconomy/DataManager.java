package regalowl.hyperconomy;


import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
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

import regalowl.databukkit.file.FileTools;
import regalowl.databukkit.sql.QueryResult;
import regalowl.databukkit.sql.SQLRead;
import regalowl.databukkit.sql.SQLWrite;
import regalowl.hyperconomy.account.HyperAccount;
import regalowl.hyperconomy.account.HyperBank;
import regalowl.hyperconomy.account.HyperPlayer;
import regalowl.hyperconomy.hyperobject.HyperObject;
import regalowl.hyperconomy.hyperobject.HyperObjectType;
import regalowl.hyperconomy.shop.PlayerShop;
import regalowl.hyperconomy.shop.ServerShop;
import regalowl.hyperconomy.shop.Shop;
import regalowl.hyperconomy.util.Backup;
import regalowl.hyperconomy.util.DatabaseUpdater;

public class DataManager implements Listener {

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
	private String defaultServerShopAccount;
	
	private QueryResult playerData;
	private QueryResult shopData;
	
	
	
	public DataManager() {
		hc = HyperConomy.hc;
		economiesLoaded = false;
		dataLoaded = false;
		playersLoaded = false;
		loadActive = false;
		useShops = hc.gYH().gFC("config").getBoolean("enable-feature.shops");
		shopinterval = hc.gYH().gFC("config").getLong("intervals.shop-check");
		defaultServerShopAccount = hc.gYH().gFC("config").getString("shop.default-server-shop-account");
		du = new DatabaseUpdater();
		hc.getServer().getPluginManager().registerEvents(this, hc);
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
			HyperConomy.hc.getDataManager().createEconomyFromDefaultCSV("default", false);
			initializeComposites();
		}
		hc.getSQLWrite().afterWrite(this, "load3");
	}
	public void load3() {
		hc.getDataManager().load4();
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
				//load players
				hyperPlayers.clear();
				playerData = sr.select("SELECT * FROM hyperconomy_players");
				hc.getServer().getScheduler().runTaskAsynchronously(hc, new Runnable() {
					public void run() {
						while (playerData.next()) {
							HyperPlayer hplayer = new HyperPlayer(playerData.getString("PLAYER"), playerData.getString("ECONOMY"), 
									playerData.getDouble("BALANCE"), playerData.getDouble("X"), playerData.getDouble("Y"), playerData.getDouble("Z"), 
									playerData.getString("WORLD"), playerData.getString("HASH"), playerData.getString("SALT"));
							hyperPlayers.put(hplayer.getName().toLowerCase(), hplayer);
						}
						playerData.close();
						playerData = null;
						if (!accountExists(defaultServerShopAccount)) {
							HyperPlayer defaultAccount = getHyperPlayer(defaultServerShopAccount);
							defaultAccount.setBalance(hc.getConfig().getDouble("shop.default-server-shop-account-initial-balance"));
						}
						addOnlinePlayers();
						playersLoaded = true;
					}
				});
				//load banks
				hyperBanks.clear();
				QueryResult bankData = sr.select("SELECT * FROM hyperconomy_banks");
				while (bankData.next()) {
					HyperBank hBank = new HyperBank(bankData.getString("NAME"), bankData.getDouble("BALANCE"), bankData.getString("OWNERS"), bankData.getString("MEMBERS"));
					hyperBanks.put(hBank.getName().toLowerCase(), hBank);
				}
				bankData.close();
				//load shops
				shops.clear();
				if (useShops) {
					shopData = sr.select("SELECT * FROM hyperconomy_shops");
					hc.getServer().getScheduler().runTaskAsynchronously(hc, new Runnable() {
						public void run() {
							while (shopData.next()) {
								String type = shopData.getString("TYPE");
								if (type.equalsIgnoreCase("server")) {
									String name = shopData.getString("NAME");
									Location p1 = new Location(Bukkit.getWorld(shopData.getString("WORLD")), shopData.getInt("P1X"), shopData.getInt("P1Y"), shopData.getInt("P1Z"));
									Location p2 = new Location(Bukkit.getWorld(shopData.getString("WORLD")), shopData.getInt("P2X"), shopData.getInt("P2Y"), shopData.getInt("P2Z"));
									Shop shop = new ServerShop(name, shopData.getString("ECONOMY"), getHyperPlayer(shopData.getString("OWNER")), 
											shopData.getString("MESSAGE"), p1, p2, shopData.getString("BANNED_OBJECTS"));
									shops.put(name, shop);
								} else if (type.equalsIgnoreCase("player")) {
									if (!hc.gYH().gFC("config").getBoolean("enable-feature.player-shops")) {continue;}
									String name = shopData.getString("NAME");
									Location p1 = new Location(Bukkit.getWorld(shopData.getString("WORLD")), shopData.getInt("P1X"), shopData.getInt("P1Y"), shopData.getInt("P1Z"));
									Location p2 = new Location(Bukkit.getWorld(shopData.getString("WORLD")), shopData.getInt("P2X"), shopData.getInt("P2Y"), shopData.getInt("P2Z"));
									Shop shop = new PlayerShop(name, shopData.getString("ECONOMY"), getHyperPlayer(shopData.getString("OWNER")), 
											shopData.getString("MESSAGE"), p1, p2, shopData.getString("BANNED_OBJECTS"), shopData.getString("ALLOWED_PLAYERS"));
									shops.put(name, shop);
								}
							}
							shopData.close();
							shopData = null;
						}
					});
				} else {
					Shop shop = new ServerShop("GlobalShop", getHyperPlayer(defaultServerShopAccount).getEconomy(), getHyperPlayer(defaultServerShopAccount));
					shops.put("GlobalShop", shop);
				}
				stopShopCheck();
				startShopCheck();
				waitForDataLoad();
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
		values.put("HYPERACCOUNT", defaultServerShopAccount);
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
			values.put("INITIATION", "true");
			values.put("STARTPRICE", ho.getStartprice()+"");
			values.put("CEILING", ho.getCeiling()+"");
			values.put("FLOOR", ho.getFloor()+"");
			values.put("MAXSTOCK", ho.getMaxstock()+"");
			if (ho.getType() == HyperObjectType.ITEM) {
				values.put("DATA", ho.getData());
			} else if (ho.getType() == HyperObjectType.ENCHANTMENT) {
				values.put("MATERIAL", ho.getEnchantmentName());
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

	
	
	public void createEconomyFromDefaultCSV(String econ, boolean restart) {
		if (hc.gYH().gFC("config").getBoolean("enable-feature.automatic-backups")) {
			new Backup();
		}
		String defaultObjectsPath = hc.getFolderPath() + File.separator + "defaultObjects.csv";
		FileTools ft = hc.getFileTools();
		if (!ft.fileExists(defaultObjectsPath)) {
			ft.copyFileFromJar("defaultObjects.csv", defaultObjectsPath);
		}
		SQLWrite sw = hc.getSQLWrite();
		sw.addToQueue("DELETE FROM hyperconomy_economies WHERE NAME = '"+econ+"'");
		HashMap<String,String> values = new HashMap<String,String>();
		values.put("NAME", econ);
		values.put("HYPERACCOUNT", hc.gYH().gFC("config").getString("shop.default-server-shop-account"));
		sw.performInsert("hyperconomy_economies", values);
		QueryResult data = hc.getFileTools().readCSV(defaultObjectsPath);
		ArrayList<String> columns = data.getColumnNames();
		sw.addToQueue("DELETE FROM hyperconomy_objects WHERE ECONOMY = '"+econ+"'");
		while (data.next()) {
			values = new HashMap<String, String>();
			for (String column : columns) {
				values.put(column, data.getString(column));
			}
			sw.performInsert("hyperconomy_objects", values);
		}
		ft.deleteFile(defaultObjectsPath);
		if (restart) {
			hc.restart();
		}
	}
	
	public void initializeComposites() {
		if (hc.gYH().gFC("config").getBoolean("enable-feature.automatic-backups")) {new Backup();}
		String defaultObjectsPath = hc.getFolderPath() + File.separator + "defaultComposites.csv";
		FileTools ft = hc.getFileTools();
		if (!ft.fileExists(defaultObjectsPath)) {
			ft.copyFileFromJar("defaultComposites.csv", defaultObjectsPath);
		}
		SQLWrite sw = hc.getSQLWrite();
		sw.addToQueue("DELETE FROM hyperconomy_composites");
		QueryResult data = hc.getFileTools().readCSV(defaultObjectsPath);
		ArrayList<String> columns = data.getColumnNames();
		while (data.next()) {
			HashMap<String, String> values = new HashMap<String, String>();
			for (String column : columns) {
				values.put(column, data.getString(column));
			}
			sw.performInsert("hyperconomy_composites", values);
		}
		ft.deleteFile(defaultObjectsPath);
	}
	
	

	
	

	
	//BANK FUNCTIONS
	

	
	
	public HyperBank getHyperBank(String name) {
		if (name == null) {return null;}
		String bankName = name.toLowerCase();
		if (hyperBanks.containsKey(bankName.toLowerCase())) {
			return hyperBanks.get(bankName.toLowerCase());
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
	
	public void renameBanksWithThisName(String name) {
		if (hasBank(name)) {
			HyperBank hb = getHyperBank(name);
			int c = 0;
			while (hasBank(name + c)) {c++;}
			hb.setName(name + c);
		}
	}
	
	
	
	
	
	//ACCOUNT FUNCTIONS
	
	
	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerJoin(PlayerJoinEvent event) {
		try {
			if (!dataLoaded()) {return;}
			String name = event.getPlayer().getName();
			if (name.equalsIgnoreCase(hc.gYH().gFC("config").getString("shop.default-server-shop-account"))) {
				event.getPlayer().kickPlayer(hc.getLanguageFile().get("CANT_USE_ACCOUNT"));
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
			if (p.getName().equalsIgnoreCase(hc.gYH().gFC("config").getString("shop.default-server-shop-account"))) {
				p.kickPlayer(hc.getLanguageFile().get("CANT_USE_ACCOUNT"));
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
	
	public HyperAccount getDefaultServerShopAccount() {
		return getHyperPlayer(defaultServerShopAccount);
	}
	public boolean hyperPlayerExists(String name) {
		String playerName = name.toLowerCase();
		if (hc.useExternalEconomy()) {
			return hc.getEconomy().hasAccount(name);
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
	

	public void removeHyperPlayer(HyperPlayer hp) {
		if (hyperPlayers.contains(hp)) {
			hyperPlayers.remove(hp.getName().toLowerCase());
		}
	}
	 
	

	public HyperPlayer addPlayer(String player) {
		if (!playersLoaded) {return null;}
		String playerName = player.toLowerCase();
		if (!hyperPlayers.containsKey(playerName)) {
			renameBanksWithThisName(playerName);
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


	//SHOP FUNCTIONS
	
	
	
	public Shop getShop(Player player) {
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
