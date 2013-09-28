package regalowl.hyperconomy;


import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

import net.milkbowl.vault.economy.Economy;

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



public class HyperEconomy implements Listener {
	private ConcurrentHashMap<String, Shop> shops = new ConcurrentHashMap<String, Shop>();
	private ConcurrentHashMap<String, HyperObject> hyperObjects = new ConcurrentHashMap<String, HyperObject>();
	private ConcurrentHashMap<String, HyperPlayer> hyperPlayers = new ConcurrentHashMap<String, HyperPlayer>();
	

	
	private HyperConomy hc;
	private EconomyManager em;
	private SQLRead sr;
	private String economy;
	
	private long shopinterval;
	private BukkitTask shopCheckTask;
	private boolean useShops;
	private boolean dataLoaded;
	

	HyperEconomy(String economy) {
		dataLoaded = false;
		hc = HyperConomy.hc;	
		em = hc.getEconomyManager();
		this.economy = economy;
		sr = hc.getSQLRead();
		hc.getServer().getPluginManager().registerEvents(this, hc);
		useShops = hc.gYH().gFC("config").getBoolean("config.use-shops");
		shopinterval = hc.gYH().gFC("config").getLong("config.shopcheckinterval");
		load();
	}
	
	
	
	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerJoin(PlayerJoinEvent event) {
		try {
			if (!em.economiesLoaded()) {
				return;
			}
			String name = event.getPlayer().getName();
			if (name.equalsIgnoreCase(hc.gYH().gFC("config").getString("config.global-shop-account"))) {
				if (hc.gYH().gFC("config").getBoolean("config.block-player-with-same-name-as-global-shop-account")) {
					event.getPlayer().kickPlayer(hc.getLanguageFile().get("CANT_USE_ACCOUNT"));
				}
			}
			if (!em.hyperPlayerExists(name) && economy.equalsIgnoreCase("default")) {
				addPlayer(name);
			}
		} catch (Exception e) {
			hc.gDB().writeError(e);
		}
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerQuit(PlayerQuitEvent event) {
		try {
			if (!em.economiesLoaded()) {
				return;
			}
			Location l = event.getPlayer().getLocation();
			String name = event.getPlayer().getName();
			if (!em.hyperPlayerExists(name) && economy.equalsIgnoreCase("default")) {
				addPlayer(name);
			}
			if (hyperPlayers.containsKey(name)) {
				HyperPlayer hp = hyperPlayers.get(name);
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
	
	
	

	public boolean dataLoaded() {
		return dataLoaded;
	}
	
	
	
	private void loadShops() {
		stopShopCheck();
		shops.clear();
		FileConfiguration sh = hc.gYH().gFC("shops");
		if (!useShops && economy.equalsIgnoreCase("default")) {
			Shop shop = new ServerShop("GlobalShop", em.getGlobalShopAccount());
			shop.setGlobal();
			shops.put("GlobalShop", shop);
			return;
		}
		Iterator<String> it = sh.getKeys(false).iterator();
		while (it.hasNext()) {   			
			Object element = it.next();
			String name = element.toString(); 
			if (name.equalsIgnoreCase("GlobalShop")) {continue;}
			if (sh.getString(name + ".economy").equalsIgnoreCase(economy)) {
				String owner = sh.getString(name + ".owner");
				if (owner == null && economy.equalsIgnoreCase("default")) {
					owner = em.getGlobalShopAccount().getName();
				}
				Shop shop = new ServerShop(name, getHyperPlayer(owner));
				shop.setPoint1(sh.getString(name + ".world"), sh.getInt(name + ".p1.x"), sh.getInt(name + ".p1.y"), sh.getInt(name + ".p1.z"));
				shop.setPoint2(sh.getString(name + ".world"), sh.getInt(name + ".p2.x"), sh.getInt(name + ".p2.y"), sh.getInt(name + ".p2.z"));
				shop.setMessage1(sh.getString(name + ".shopmessage1"));
				shop.setMessage2(sh.getString(name + ".shopmessage2"));
				shops.put(name, shop);
			}
		}
		startShopCheck();
	}
	private void load() {
		hc.getServer().getScheduler().runTaskAsynchronously(hc, new Runnable() {
			public void run() {
				hyperObjects.clear();
				hyperPlayers.clear();
				QueryResult result = sr.aSyncSelect("SELECT * FROM hyperconomy_objects WHERE ECONOMY = '"+economy+"'");
				while (result.next()) {
					HyperObject hobj = new ComponentObject(result.getString("NAME"), result.getString("ECONOMY"), result.getString("TYPE"), 
							result.getString("CATEGORY"), result.getString("MATERIAL"), result.getInt("ID"), result.getInt("DATA"),
							result.getInt("DURABILITY"), result.getDouble("VALUE"), result.getString("STATIC"), result.getDouble("STATICPRICE"),
							result.getDouble("STOCK"), result.getDouble("MEDIAN"), result.getString("INITIATION"), result.getDouble("STARTPRICE"), 
							result.getDouble("CEILING"),result.getDouble("FLOOR"), result.getDouble("MAXSTOCK"));
					hyperObjects.put(hobj.getName(), hobj);
				}
				result.close();
		
				result = sr.aSyncSelect("SELECT * FROM hyperconomy_players WHERE ECONOMY = '"+economy+"'");
				while (result.next()) {
					HyperPlayer hplayer = new HyperPlayer(result.getString("PLAYER"), result.getString("ECONOMY"), result.getDouble("BALANCE"), result.getDouble("X"), result.getDouble("Y"), result.getDouble("Z"), result.getString("WORLD"), result.getString("HASH"), result.getString("SALT"));
					hyperPlayers.put(hplayer.getName(), hplayer);
				}
				result.close();
				if (economy.equalsIgnoreCase("default")) {
					createGlobalShopAccount();
				}
				dataLoaded = true;
				hc.getServer().getScheduler().runTask(hc, new Runnable() {
					public void run() {
						if (economy.equalsIgnoreCase("default")) {
							loadComposites();
							addOnlinePlayers();
							loadShops();
						}
					}
				});
			}
		});
	}
	
	private void loadComposites() {
		boolean loaded = false;
		FileConfiguration composites = hc.gYH().gFC("composites");
		while (!loaded) {
			loaded = true;
			Iterator<String> it = composites.getKeys(false).iterator();
			while (it.hasNext()) {
				String name = it.next().toString();
				if (!componentsLoaded(name)) {
					loaded = false;
					continue;
				}
				HyperObject ho = new CompositeObject(name, economy);
				hyperObjects.put(ho.getName(), ho);
			}
		}
	}
	private boolean componentsLoaded(String name) {
		HashMap<String,String> tempComponents = hc.getSerializeArrayList().explodeMap(hc.gYH().gFC("composites").getString(name + ".components"));
		for (Map.Entry<String,String> entry : tempComponents.entrySet()) {
		    String oname = entry.getKey();
		    HyperObject ho = getHyperObject(oname);
		    if (ho == null) {
		    	return false;
		    }
		}
		return true;
	}
	
	private void addOnlinePlayers() {
		for (Player p : Bukkit.getOnlinePlayers()) {
			if (p.getName().equalsIgnoreCase(hc.gYH().gFC("config").getString("config.global-shop-account"))) {
				if (hc.gYH().gFC("config").getBoolean("config.block-player-with-same-name-as-global-shop-account")) {
					p.kickPlayer(hc.getLanguageFile().get("CANT_USE_ACCOUNT"));
				}
			}
			if (!hasAccount(p.getName())) {
				addPlayer(p.getName());
			}
		}
	}

	
	
	
	
	
	
	
	
	
	public String getEconomy() {
		return economy;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	Shop getShop(Player player) {
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
		hc.getWebHandler().addShop(shop);
	}
	public void removeShop(String name) {
		if (shopExists(name)) {
			shops.remove(fixShopName(name));
		}
	}
	
	public void deleteShop(String name) {
		hc.gYH().gFC("shops").set(name, null);
		shops.remove(name);
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
	public ArrayList<String> listShops() {
		ArrayList<String> names = new ArrayList<String>();
		for (Shop shop : shops.values()) {
			names.add(shop.getName());
		}
		return names;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	

	public HyperObject getHyperObject(int id, int data) {
		for (HyperObject ho:hyperObjects.values()) {
			if (ho.getId() == id && ho.getData() == data) {
				return ho;
			}
		}
		return null;
	}
	
	public HyperObject getHyperObject(String name) {
		name = fixName(name);
		if (hyperObjects.containsKey(name)) {
			return hyperObjects.get(name);
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
	
	public void addHyperPlayer(HyperPlayer hp) {
		if (!hyperPlayers.contains(hp)) {
			hyperPlayers.put(hp.getName(), hp);
		}
	}
	public void removeHyperPlayer(HyperPlayer hp) {
		if (hyperPlayers.contains(hp)) {
			hyperPlayers.remove(hp.getName());
		}
	}
	
	
	public ArrayList<HyperPlayer> getHyperPlayers() {
		ArrayList<HyperPlayer> hps = new ArrayList<HyperPlayer>();
		for (HyperPlayer hp:hyperPlayers.values()) {
			hps.add(hp);
		}
		return hps;
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
		if (hc.s().gB("use-external-economy-plugin")) {
			Economy economy = hc.getEconomy();
			if (economy.hasAccount(name)) {
				if (!hyperPlayers.containsKey(fixpN(name))) {
					addPlayer(name);
				}
				return true;
			} else {
				return false;
			}
		} else {
			return hyperPlayers.containsKey(fixpN(name));
		}
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


	public void clearData() {
		hyperObjects.clear();
		hyperPlayers.clear();
		shops.clear();
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
			names.add(ho.getName());
		}
		return names;
	}

	public ArrayList<String> getItemNames() {
		ArrayList<String> names = new ArrayList<String>();
		for (HyperObject ho:hyperObjects.values()) {
			if (ho.getType() == HyperObjectType.ITEM || ho.getType() == HyperObjectType.EXPERIENCE) {
				names.add(ho.getName());
			}
		}
		return names;
	}

	public ArrayList<String> getEnchantNames() {
		ArrayList<String> names = new ArrayList<String>();
		for (HyperObject ho:hyperObjects.values()) {
			if (ho.getType() == HyperObjectType.ENCHANTMENT) {
				names.add(ho.getName());
			}
		}
		return names;
	}
	
	
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
	
	public void createGlobalShopAccount(){		
		HyperConomy hc = HyperConomy.hc;
		Economy economy = hc.getEconomy();
		LanguageFile L = hc.getLanguageFile();
		Log l = hc.getLog();
		boolean useExternalEconomy = hc.s().gB("use-external-economy-plugin");
		String globalaccount = hc.gYH().gFC("config").getString("config.global-shop-account");
		if (useExternalEconomy) {
			if (economy != null) {
				if (!economy.hasAccount(globalaccount)) {
					getHyperPlayer(globalaccount).setBalance(hc.gYH().gFC("config").getDouble("config.initialshopbalance"));
					l.writeAuditLog(globalaccount, "initialization", hc.gYH().gFC("config").getDouble("config.initialshopbalance"), economy.getName());
				}
			} else {
				Bukkit.broadcast(L.get("NO_ECON_PLUGIN"), "hyperconomy.admin");
		    	Logger log = Logger.getLogger("Minecraft");
		    	log.info(L.get("LOG_NO_ECON_PLUGIN"));
			}
		} else {
			if (!hasAccount(globalaccount)) {
				createPlayerAccount(globalaccount);
				getHyperPlayer(globalaccount).setBalance(hc.gYH().gFC("config").getDouble("config.initialshopbalance"));
				l.writeAuditLog(globalaccount, "initialization", hc.gYH().gFC("config").getDouble("config.initialshopbalance"), "HyperConomy");
			}
		}
	}
	

	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	public ArrayList<String> loadNewItems() {
		FileConfiguration itemsyaml = hc.gYH().gFC("items");
		FileConfiguration enchantsyaml = hc.gYH().gFC("enchants");
		ArrayList<String> statements = new ArrayList<String>();
		ArrayList<String> objectsAdded = new ArrayList<String>();
		Iterator<String> it = itemsyaml.getKeys(false).iterator();
		ArrayList<String> keys = getObjectKeys();
		while (it.hasNext()) {
			String itemname = it.next().toString();
			String key = itemname + ":" + economy;
			if (!keys.contains(key)) {
				objectsAdded.add(itemname);
				if (!itemname.equalsIgnoreCase("xp")) {
					statements.add("Insert Into hyperconomy_objects (NAME, ECONOMY, TYPE, CATEGORY, MATERIAL, ID, DATA, DURABILITY, VALUE, STATIC, STATICPRICE, STOCK, MEDIAN, INITIATION, STARTPRICE, CEILING, FLOOR, MAXSTOCK)" + " Values ('" + itemname + "','" + economy + "','" + "item" + "','" + "unknown" + "','" + itemsyaml.getString(itemname + ".information.material") + "','" + itemsyaml.getInt(itemname + ".information.id") + "','" + itemsyaml.getInt(itemname + ".information.data") + "','"
							+ itemsyaml.getInt(itemname + ".information.data") + "','" + itemsyaml.getDouble(itemname + ".value") + "','" + itemsyaml.getString(itemname + ".price.static") + "','" + itemsyaml.getDouble(itemname + ".price.staticprice") + "','" + itemsyaml.getDouble(itemname + ".stock.stock") + "','" + itemsyaml.getDouble(itemname + ".stock.median") + "','" + itemsyaml.getString(itemname + ".initiation.initiation") + "','" + itemsyaml.getDouble(itemname + ".initiation.startprice")
							+ "','"+ itemsyaml.getDouble(itemname + ".price.ceiling") + "','" + itemsyaml.getDouble(itemname + ".price.floor") + "','" + itemsyaml.getDouble(itemname + ".stock.maxstock") + "')");
				} else {
					statements.add("Insert Into hyperconomy_objects (NAME, ECONOMY, TYPE, CATEGORY, MATERIAL, ID, DATA, DURABILITY, VALUE, STATIC, STATICPRICE, STOCK, MEDIAN, INITIATION, STARTPRICE, CEILING, FLOOR, MAXSTOCK)" + " Values ('" + itemname + "','" + economy + "','" + "experience" + "','" + "unknown" + "','" + "none" + "','" + itemsyaml.getInt(itemname + ".information.id") + "','" + itemsyaml.getInt(itemname + ".information.data") + "','" + itemsyaml.getInt(itemname + ".information.data") + "','"
							+ itemsyaml.getDouble(itemname + ".value") + "','" + itemsyaml.getString(itemname + ".price.static") + "','" + itemsyaml.getDouble(itemname + ".price.staticprice") + "','" + itemsyaml.getDouble(itemname + ".stock.stock") + "','" + itemsyaml.getDouble(itemname + ".stock.median") + "','" + itemsyaml.getString(itemname + ".initiation.initiation") + "','" + itemsyaml.getDouble(itemname + ".initiation.startprice") + "','" + itemsyaml.getDouble(itemname + ".price.ceiling") + "','"
							+ itemsyaml.getDouble(itemname + ".price.floor") + "','" + itemsyaml.getDouble(itemname + ".stock.maxstock") + "')");
				}
			}
		}
		Iterator<String> it2 = enchantsyaml.getKeys(false).iterator();
		while (it2.hasNext()) {
			String ename = it2.next().toString();
			String key = ename + ":" + economy;
			if (!keys.contains(key)) {
				objectsAdded.add(ename);
				statements.add("Insert Into hyperconomy_objects (NAME, ECONOMY, TYPE, CATEGORY, MATERIAL, ID, DATA, DURABILITY, VALUE, STATIC, STATICPRICE, STOCK, MEDIAN, INITIATION, STARTPRICE, CEILING, FLOOR)" + " Values ('" + ename + "','" + economy + "','" + "enchantment" + "','" + "unknown" + "','" + enchantsyaml.getString(ename + ".information.name") + "','" + enchantsyaml.getInt(ename + ".information.id") + "','" + "-2" + "','" + "-2" + "','" + enchantsyaml.getDouble(ename + ".value") + "','"
						+ enchantsyaml.getString(ename + ".price.static") + "','" + enchantsyaml.getDouble(ename + ".price.staticprice") + "','" + enchantsyaml.getDouble(ename + ".stock.stock") + "','" + enchantsyaml.getDouble(ename + ".stock.median") + "','" + enchantsyaml.getString(ename + ".initiation.initiation") + "','" + enchantsyaml.getDouble(ename + ".initiation.startprice") + "','" + enchantsyaml.getDouble(ename + ".price.ceiling") + "','" + enchantsyaml.getDouble(ename + ".price.floor")
						+ "','" + enchantsyaml.getDouble(ename + ".stock.maxstock") + "')");
			}
		}
		SQLWrite sw = hc.getSQLWrite();
		sw.executeSQL(statements);
		em.load();
		return objectsAdded;
	}
	
	
	public void exportToYml() {
		FileConfiguration items = hc.gYH().gFC("items");
		FileConfiguration enchants = hc.gYH().gFC("enchants");
		ArrayList<String> names = getNames();
		Collections.sort(names, String.CASE_INSENSITIVE_ORDER);
		for (int i = 0; i < names.size(); i++) {
			String name = names.get(i);
			items.set(name, null);
			enchants.set(name, null);
			HyperObject ho = getHyperObject(name);
			String newtype = HyperObjectType.getString(ho.getType());
			String newcategory = ho.getCategory();
			String newmaterial = ho.getMaterial();
			int newid = ho.getId();
			int newdata = ho.getData();
			int newdurability = ho.getDurability();
			double newvalue = ho.getValue();
			String newstatic = ho.getIsstatic();
			double newstaticprice = ho.getStaticprice();
			double newstock = ho.getStock();
			double newmedian = ho.getMedian();
			String newinitiation = ho.getInitiation();
			double newstartprice = ho.getStartprice();
			double newceiling = ho.getCeiling();
			double newfloor = ho.getFloor();
			double newmaxstock = ho.getMaxstock();
			if (itemTest(name)) {
				items.set(name + ".information.type", newtype);
			} else if (enchantTest(name)) {
				enchants.set(name + ".information.type", newtype);
			}
			if (itemTest(name)) {
				items.set(name + ".information.category", newcategory);
			} else if (enchantTest(name)) {
				enchants.set(name + ".information.category", newcategory);
			}
			if (itemTest(name)) {
				items.set(name + ".information.material", newmaterial);
			} else if (enchantTest(name)) {
				enchants.set(name + ".information.name", newmaterial);
			}
			if (itemTest(name)) {
				items.set(name + ".information.id", newid);
			} else if (enchantTest(name)) {
				enchants.set(name + ".information.id", newid);
			}
			if (itemTest(name)) {
				items.set(name + ".information.data", newdata);
			}
			if (itemTest(name)) {
				items.set(name + ".information.data", newdurability);
			}
			if (itemTest(name)) {
				items.set(name + ".value", newvalue);
			} else if (enchantTest(name)) {
				enchants.set(name + ".value", newvalue);
			}
			if (itemTest(name)) {
				items.set(name + ".price.static", Boolean.parseBoolean(newstatic));
			} else if (enchantTest(name)) {
				enchants.set(name + ".price.static", Boolean.parseBoolean(newstatic));
			}
			if (itemTest(name)) {
				items.set(name + ".price.staticprice", newstaticprice);
			} else if (enchantTest(name)) {
				enchants.set(name + ".price.staticprice", newstaticprice);
			}
			if (itemTest(name)) {
				items.set(name + ".stock.stock", newstock);
			} else if (enchantTest(name)) {
				enchants.set(name + ".stock.stock", newstock);
			}
			if (itemTest(name)) {
				items.set(name + ".stock.median", newmedian);
			} else if (enchantTest(name)) {
				enchants.set(name + ".stock.median", newmedian);
			}
			if (itemTest(name)) {
				items.set(name + ".initiation.initiation", Boolean.parseBoolean(newinitiation));
			} else if (enchantTest(name)) {
				enchants.set(name + ".initiation.initiation", Boolean.parseBoolean(newinitiation));
			}
			if (itemTest(name)) {
				items.set(name + ".initiation.startprice", newstartprice);
			} else if (enchantTest(name)) {
				enchants.set(name + ".initiation.startprice", newstartprice);
			}
			if (itemTest(name)) {
				items.set(name + ".price.ceiling", newceiling);
			} else if (enchantTest(name)) {
				enchants.set(name + ".price.ceiling", newceiling);
			}
			if (itemTest(name)) {
				items.set(name + ".price.floor", newfloor);
			} else if (enchantTest(name)) {
				enchants.set(name + ".price.floor", newfloor);
			}
			if (itemTest(name)) {
				items.set(name + ".stock.maxstock", newmaxstock);
			} else if (enchantTest(name)) {
				enchants.set(name + ".stock.maxstock", newmaxstock);
			}
		}
		hc.gYH().saveYamls();
	}

	
}
