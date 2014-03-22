package regalowl.hyperconomy.shop;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import regalowl.databukkit.BasicStatement;
import regalowl.databukkit.CommonFunctions;
import regalowl.databukkit.QueryResult;
import regalowl.databukkit.WriteStatement;
import regalowl.hyperconomy.HyperConomy;
import regalowl.hyperconomy.HyperEconomy;
import regalowl.hyperconomy.account.HyperAccount;
import regalowl.hyperconomy.account.HyperPlayer;
import regalowl.hyperconomy.hyperobject.BasicObject;
import regalowl.hyperconomy.hyperobject.BasicShopObject;
import regalowl.hyperconomy.hyperobject.CompositeItem;
import regalowl.hyperconomy.hyperobject.CompositeShopItem;
import regalowl.hyperconomy.hyperobject.HyperObject;
import regalowl.hyperconomy.hyperobject.HyperObjectStatus;
import regalowl.hyperconomy.hyperobject.HyperObjectType;
import regalowl.hyperconomy.hyperobject.ShopEnchant;
import regalowl.hyperconomy.util.LanguageFile;

public class PlayerShop implements Shop, Comparable<Shop> {

	private String name;
	private String world;
	private HyperAccount owner;
	private ArrayList<String> allowed = new ArrayList<String>();
	private String economy;
	private String message;
	private int p1x;
	private int p1y;
	private int p1z;
	private int p2x;
	private int p2y;
	private int p2z;
	private ConcurrentHashMap<String,HyperObject> shopContents = new ConcurrentHashMap<String,HyperObject>();
	private ArrayList<HyperObject> availableObjects = new ArrayList<HyperObject>();
	

	
	private HyperConomy hc;
	private LanguageFile L;
	private PlayerShop ps;
	private CommonFunctions cf;
	private boolean useshopexitmessage;
	private boolean loaded;
	private ArrayList<String> inShop = new ArrayList<String>();
	
	
	
	public PlayerShop(String name, String economy, HyperPlayer owner, String message, Location p1, Location p2, String banned_objects, String allowed_players) {
		loaded = false;
		hc = HyperConomy.hc;
		cf = hc.getDataBukkit().getCommonFunctions();
		ps = this;
		L = hc.getLanguageFile();
		useshopexitmessage = hc.gYH().gFC("config").getBoolean("config.use-shop-exit-message");	
		this.name = name;
		this.economy = economy;
		this.owner = owner;
		this.message = message;
		this.world = p1.getWorld().getName();
		this.p1x = p1.getBlockX();
		this.p1y = p1.getBlockY();
		this.p1z = p1.getBlockZ();
		this.p2x = p2.getBlockX();
		this.p2y = p2.getBlockY();
		this.p2z = p2.getBlockZ();
		this.allowed = cf.explode(allowed_players, ",");
		HyperEconomy he = getHyperEconomy();
		availableObjects.clear();
		for (HyperObject ho:he.getHyperObjects()) {
			availableObjects.add(ho);
		}
		ArrayList<String> unavailable = hc.gCF().explode(banned_objects,",");
		for (String objectName : unavailable) {
			HyperObject ho = hc.getDataManager().getEconomy(economy).getHyperObject(objectName);
			availableObjects.remove(ho);
		}
		loadPlayerShopObjects();
	}
	
	
	public PlayerShop(String shopName, String economy, HyperAccount owner, Location p1, Location p2) {
		loaded = false;
		hc = HyperConomy.hc;
		cf = hc.getDataBukkit().getCommonFunctions();
		ps = this;
		L = hc.getLanguageFile();
		useshopexitmessage = hc.gYH().gFC("config").getBoolean("config.use-shop-exit-message");
		this.name = shopName;
		this.economy = economy;
		this.owner = owner;
		this.world = p1.getWorld().getName();
		this.message = "";
		p1x = p1.getBlockX();
		p1y = p1.getBlockY();
		p1z = p1.getBlockZ();
		p2x = p2.getBlockX();
		p2y = p2.getBlockY();
		p2z = p2.getBlockZ();
		HashMap<String,String> values = new HashMap<String,String>();
		values.put("NAME", name);
		values.put("ECONOMY", economy);
		values.put("OWNER", owner.getName());
		values.put("WORLD", world);
		values.put("P1X", p1x+"");
		values.put("P1Y", p1y+"");
		values.put("P1Z", p1z+"");
		values.put("P2X", p2x+"");
		values.put("P2Y", p2y+"");
		values.put("P2Z", p2z+"");
		values.put("ALLOWED_PLAYERS", "");
		values.put("BANNED_OBJECTS", "");
		values.put("MESSAGE", "");
		values.put("TYPE", "player");
		hc.getSQLWrite().performInsert("hyperconomy_shops", values);
		HyperEconomy he = getHyperEconomy();
		for (HyperObject ho:he.getHyperObjects()) {
			availableObjects.add(ho);
		}
		loaded = true;
	}
	
	private void loadPlayerShopObjects() {
		hc.getServer().getScheduler().runTaskAsynchronously(hc, new Runnable() {
			public void run() {
				HyperEconomy he = hc.getDataManager().getEconomy(economy);
				BasicStatement statement = new BasicStatement("SELECT * FROM hyperconomy_shop_objects WHERE SHOP = ?", hc.getDataBukkit());
				statement.addParameter(name);
				QueryResult result = hc.getSQLRead().select(statement);
				while (result.next()) {
					double buyPrice = result.getDouble("BUY_PRICE");
					double sellPrice = result.getDouble("SELL_PRICE");
					int maxStock = result.getInt("MAX_STOCK");
					HyperObject ho = he.getHyperObject(result.getString("HYPEROBJECT"));
					if (ho == null) {continue;}
					double stock = result.getDouble("QUANTITY");
					HyperObjectStatus status = HyperObjectStatus.fromString(result.getString("STATUS"));
					if (ho.getType() == HyperObjectType.ITEM && ho.isCompositeObject()) {
						HyperObject pso = new CompositeShopItem(ps, (CompositeItem)ho, stock, buyPrice, sellPrice, maxStock, status);
						shopContents.put(ho.getName(), pso);
					} else if (ho.getType() == HyperObjectType.ENCHANTMENT) {
						HyperObject pso = new ShopEnchant(ps, ho, stock, buyPrice, sellPrice, maxStock, status);
						shopContents.put(ho.getName(), pso);
					} else { 
						BasicShopObject pso = new BasicShopObject(ps, ho, stock, buyPrice, sellPrice, maxStock, status);
						shopContents.put(ho.getName(), pso);
					}
				}
				result.close();
				loaded = true;
			}
		});
	}

	public boolean isLoaded() {
		return loaded;
	}
	
	public int compareTo(Shop s) {
		return name.compareTo(s.getName());
	}
	
	public void setPoint1(String world, int x, int y, int z) {
		this.world = world;
		p1x = x;
		p1y = y;
		p1z = z;
		HashMap<String,String> conditions = new HashMap<String,String>();
		HashMap<String,String> values = new HashMap<String,String>();
		conditions.put("NAME", name);
		values.put("WORLD", world);
		values.put("P1X", x+"");
		values.put("P1Y", y+"");
		values.put("P1Z", z+"");
		hc.getSQLWrite().performUpdate("hyperconomy_shops", values, conditions);
	}
	public void setPoint2(String world, int x, int y, int z) {
		this.world = world;
		p2x = x;
		p2y = y;
		p2z = z;
		HashMap<String,String> conditions = new HashMap<String,String>();
		HashMap<String,String> values = new HashMap<String,String>();
		conditions.put("NAME", name);
		values.put("WORLD", world);
		values.put("P2X", x+"");
		values.put("P2Y", y+"");
		values.put("P2Z", z+"");
		hc.getSQLWrite().performUpdate("hyperconomy_shops", values, conditions);
	}
	public void setPoint1(Location l) {
		setPoint1(l.getWorld().getName(), l.getBlockX(), l.getBlockY(), l.getBlockZ());
	}
	public void setPoint2(Location l) {
		setPoint2(l.getWorld().getName(), l.getBlockX(), l.getBlockY(), l.getBlockZ());
	}
	
	
	public void setMessage(String message) {
		this.message = message;
		HashMap<String,String> conditions = new HashMap<String,String>();
		HashMap<String,String> values = new HashMap<String,String>();
		conditions.put("NAME", name);
		values.put("MESSAGE", message);
		hc.getSQLWrite().performUpdate("hyperconomy_shops", values, conditions);
	}
	
	public void setDefaultMessage() {
		setMessage(L.get("SHOP_LINE_BREAK")+"%n&aWelcome to "+name+"%n&9Type &b/hc &9for help.%n"+L.get("SHOP_LINE_BREAK"));
	}
	
	public void setWorld(String world) {
		this.world = world;
		HashMap<String,String> conditions = new HashMap<String,String>();
		HashMap<String,String> values = new HashMap<String,String>();
		conditions.put("NAME", name);
		values.put("WORLD", world);
		hc.getSQLWrite().performUpdate("hyperconomy_shops", values, conditions);
	}
	
	public void setName(String name) {
		HashMap<String,String> conditions = new HashMap<String,String>();
		HashMap<String,String> values = new HashMap<String,String>();
		conditions.put("NAME", this.name);
		values.put("NAME", name);
		hc.getSQLWrite().performUpdate("hyperconomy_shops", values, conditions);
		this.name = name;
	}
	
	public void setEconomy(String economy) {
		this.economy = economy;
		HashMap<String,String> conditions = new HashMap<String,String>();
		HashMap<String,String> values = new HashMap<String,String>();
		conditions.put("NAME", name);
		values.put("ECONOMY", economy);
		hc.getSQLWrite().performUpdate("hyperconomy_shops", values, conditions);
	}
	
	
	public boolean inShop(int x, int y, int z, String world) {
		if (world.equalsIgnoreCase(this.world)) {
			int rangex = Math.abs(p1x - p2x);
			if (Math.abs(x - p1x) <= rangex && Math.abs(x - p2x) <= rangex) {
				int rangez = Math.abs(p1z - p2z);
				if (Math.abs(z - p1z) <= rangez && Math.abs(z - p2z) <= rangez) {
					int rangey = Math.abs(p1y - p2y);
					if (Math.abs(y - p1y) <= rangey && Math.abs(y - p2y) <= rangey) {
						return true;
					}
				}
			}
		}
		return false;
	}	
	public boolean inShop(Location l) {
		return inShop(l.getBlockX(), l.getBlockY(), l.getBlockZ(), l.getWorld().getName());
	}
	public boolean inShop(Player player) {
		Location l = player.getLocation();
		return inShop(l.getBlockX(), l.getBlockY(), l.getBlockZ(), l.getWorld().getName());
	}
	
	public void sendEntryMessage(Player player) {
		if (message == "") {setDefaultMessage();}
		String[] lines = message.replace("_", " ").split("%n");
		for (String line:lines) {
			player.sendMessage(L.applyColor(line));
		}
	}
	
	public String getEconomy() {
		return economy;
	}
	
	public String getName() {
		return name;
	}
	
	public String getDisplayName() {
		return name.replace("_", " ");
	}
	
	

	public void saveAvailable() {
		HyperEconomy he = getHyperEconomy();
		ArrayList<String> unavailable = new ArrayList<String>();
		ArrayList<HyperObject> allObjects = he.getHyperObjects();
		for (HyperObject ho:allObjects) {
			if (!availableObjects.contains(ho)) {
				unavailable.add(ho.getName());
			}
		}
		HashMap<String,String> conditions = new HashMap<String,String>();
		HashMap<String,String> values = new HashMap<String,String>();
		conditions.put("NAME", name);
		values.put("BANNED_OBJECTS", hc.gCF().implode(unavailable,","));
		hc.getSQLWrite().performUpdate("hyperconomy_shops", values, conditions);
	}
	
	public boolean isStocked(HyperObject ho) {
		HyperObject pso = ho;
		if (!ho.isShopObject()) {
			pso = shopContents.get(ho.getName());
		}
		if (pso == null) {return false;}
		if (pso.getStock() <= 0.0) {return false;}
		return true;
	}
	public boolean isStocked(String item) {
		return isStocked(getHyperEconomy().getHyperObject(item));
	}
	public boolean isBanned(HyperObject ho) {
		HyperObject co = ho;
		if (ho.isShopObject()) {
			co = ho.getHyperObject();
		}
		if (availableObjects.contains(co)) {
			return false;
		}
		return true;
	}
	public boolean isBanned(String name) {
		return isBanned(getHyperEconomy().getHyperObject(name));
	}
	public boolean isTradeable(HyperObject ho) {
		if (!isBanned(ho)) {
			if (ho.isShopObject()) {
				if (ho.getStatus() == HyperObjectStatus.NONE) {return false;}
				return true;
			} else {
				return true;
			}
		}
		return false;
	}
	public boolean isAvailable(HyperObject ho) {
		if (isTradeable(ho) && isStocked(ho)) {
			return true;
		}
		return false;
	}

	
	public ArrayList<HyperObject> getTradeableObjects() {
		ArrayList<HyperObject> available = new ArrayList<HyperObject>();
		for (HyperObject pso:shopContents.values()) {
			if (isTradeable(pso)) {
				available.add(pso);
			}
		}
		return available;
	}
	
	public ArrayList<HyperObject> getShopObjects() {
		ArrayList<HyperObject> objects = new ArrayList<HyperObject>();
		for (HyperObject pso:shopContents.values()) {
			objects.add(pso);
		}
		return objects;
	}
	
	public void unBanAllObjects() {
		availableObjects.clear();
		for (HyperObject ho:getHyperEconomy().getHyperObjects()) {
			availableObjects.add(ho);
		}
		saveAvailable();
	}
	public void banAllObjects() {
		availableObjects.clear();
		saveAvailable();
	}
	public void unBanObjects(ArrayList<HyperObject> objects) {
		for (HyperObject ho:objects) {
			HyperObject add = null;
			if (ho.isShopObject()) {
				add = ho.getHyperObject();
			} else {
				add = ho;
			}
			if (!availableObjects.contains(add)) {
				availableObjects.add(add);
			}
		}
		saveAvailable();
	}
	public void banObjects(ArrayList<HyperObject> objects) {
		for (HyperObject ho:objects) {
			HyperObject remove = null;
			if (ho.isShopObject()) {
				remove = ho.getHyperObject();
			} else {
				remove = ho;
			}
			if (availableObjects.contains(remove)) {
				availableObjects.remove(remove);
			}
		}
		saveAvailable();
	}
	

	
	public int getP1x() {
		return p1x;
	}

	public int getP1y() {
		return p1y;
	}

	public int getP1z() {
		return p1z;
	}

	public int getP2x() {
		return p2x;
	}

	public int getP2y() {
		return p2y;
	}

	public int getP2z() {
		return p2z;
	}
	
	public Location getLocation1() {
		return new Location(Bukkit.getWorld(world), p1x, p1y, p1z);
	}
	
	public Location getLocation2() {
		return new Location(Bukkit.getWorld(world), p2x, p2y, p2z);
	}
	
	public HyperAccount getOwner() {
		return owner;
	}
	
	public void setOwner(HyperAccount owner) {
		this.owner = owner;
		HashMap<String,String> conditions = new HashMap<String,String>();
		HashMap<String,String> values = new HashMap<String,String>();
		conditions.put("NAME", name);
		values.put("OWNER", owner.getName());
		hc.getSQLWrite().performUpdate("hyperconomy_shops", values, conditions);
	}


	
	public boolean isEmpty() {
		for (HyperObject pso:shopContents.values()) {
			if (pso.getStock() > 0) {
				return false;
			}
		}
		return true;
	}
	
	public void deleteShop() {
		HashMap<String,String> conditions = new HashMap<String,String>();
		conditions.put("NAME", name);
		hc.getSQLWrite().performDelete("hyperconomy_shops", conditions);
		conditions = new HashMap<String,String>();
		conditions.put("SHOP", name);
		hc.getSQLWrite().performDelete("hyperconomy_shop_objects", conditions);
		shopContents.clear();
		hc.getDataManager().removeShop(name);
	}
	
	public void removePlayerShopObject(HyperObject hyperObject) {
		HyperObject pso = getPlayerShopObject(hyperObject);
		if (pso == null) {
			return;
		} else {
			shopContents.remove(pso);
			hc.getSQLWrite().addToQueue("DELETE FROM hyperconomy_shop_objects WHERE SHOP = '"+name+"' AND HYPEROBJECT = '"+hyperObject.getName()+"'");
		}
	}
	public HyperObject getPlayerShopObject(HyperObject hyperObject) {
		if (shopContents.containsKey(hyperObject.getName())) {
			return shopContents.get(hyperObject.getName());
		}
		WriteStatement ws = new WriteStatement("INSERT INTO hyperconomy_shop_objects (SHOP, HYPEROBJECT, QUANTITY, BUY_PRICE, SELL_PRICE, MAX_STOCK, STATUS) VALUES (?,?,?,?,?,?,?)", hc.getDataBukkit());
		ws.addParameter(name);
		ws.addParameter(hyperObject.getName());
		ws.addParameter(0.0);
		ws.addParameter(0.0);
		ws.addParameter(0.0);
		ws.addParameter(1000000);
		ws.addParameter("none");
		if (hyperObject.getType() == HyperObjectType.ITEM && hyperObject.isCompositeObject()) {
			CompositeShopItem pso = new CompositeShopItem(this, (CompositeItem)hyperObject, 0.0, 0.0, 0.0, 100000, HyperObjectStatus.NONE);
			shopContents.put(hyperObject.getName(), pso);
			hc.getSQLWrite().addToQueue(ws);
			return pso;
		} else if (hyperObject.getType() == HyperObjectType.ENCHANTMENT) {
			HyperObject pso = new ShopEnchant(ps, hyperObject, 0.0, 0.0, 0.0, 100000, HyperObjectStatus.NONE);
			shopContents.put(hyperObject.getName(), pso);
			hc.getSQLWrite().addToQueue(ws);
			return pso;
		} else {
			HyperObject pso = new BasicShopObject(this, (BasicObject)hyperObject, 0.0, 0.0, 0.0, 100000, HyperObjectStatus.NONE);
			shopContents.put(hyperObject.getName(), pso);
			hc.getSQLWrite().addToQueue(ws);
			return pso;
		}
	}
	

	public boolean hasPlayerShopObject(HyperObject ho) {
		return shopContents.containsKey(ho.getName());
	}
	

	public HyperEconomy getHyperEconomy() {
		HyperEconomy he = hc.getDataManager().getEconomy(economy);
		if (he == null) {
			hc.getDataBukkit().writeError("Null HyperEconomy for economy: " + economy + ", shop: " + name);
			he = hc.getDataManager().getEconomy("default");
		}
		return he;
	}



	public void updatePlayerStatus() {
		for (Player p : Bukkit.getOnlinePlayers()) {
			if (inShop.contains(p.getName())) {
				if (!inShop(p)) {
					inShop.remove(p.getName());
					if (useshopexitmessage) {
						p.sendMessage(L.get("SHOP_EXIT_MESSAGE"));
					}
				}
			} else {
				if (inShop(p)) {
					inShop.add(p.getName());
					sendEntryMessage(p);
					hc.getDataManager().getHyperPlayer(p.getName()).setEconomy(economy);
				}
			}
		}
	}



	public int getVolume() {
		return Math.abs(p1x - p2x) * Math.abs(p1y - p2y) * Math.abs(p1z - p2z);
	}
	
	public ArrayList<String> getAllowed() {
		return allowed;
	}
	public void addAllowed(HyperAccount ha) {
		if (!allowed.contains(ha.getName())) {
			allowed.add(ha.getName());
		}
		saveAllowed();
	}
	public void removeAllowed(HyperAccount ha) {
		if (allowed.contains(ha.getName())) {
			allowed.remove(ha.getName());
		}
		saveAllowed();
	}
	public boolean isAllowed(HyperAccount ha) {
		if (allowed.contains(ha.getName())) {
			return true;
		}
		if (ha.getName().equalsIgnoreCase(owner.getName())) {
			return true;
		}
		if (ha instanceof HyperPlayer) {
			HyperPlayer hp = (HyperPlayer)ha;
			if (hp.getPlayer() != null && hp.getPlayer().hasPermission("hyperconomy.admin")) {
				return true;
			}
		}
		return false;
	}
	public void saveAllowed() {
		HashMap<String,String> conditions = new HashMap<String,String>();
		HashMap<String,String> values = new HashMap<String,String>();
		conditions.put("NAME", name);
		values.put("ALLOWED_PLAYERS", cf.implode(allowed, ","));
		hc.getSQLWrite().performUpdate("hyperconomy_shops", values, conditions);
	}


	public ArrayList<Location> getShopBlockLocations() {
		ArrayList<Location> shopBlockLocations = new ArrayList<Location>();
		ArrayList<Integer> xvals = new ArrayList<Integer>();
		ArrayList<Integer> yvals = new ArrayList<Integer>();
		ArrayList<Integer> zvals = new ArrayList<Integer>();
		if (p1x <= p2x) {
			for (int c = 0; c < (p2x - p1x + 1); c++) {
				xvals.add(p1x + c);
			}
		} else if (p1x > p2x) {
			for (int c = 0; c < (p1x - p2x + 1); c++) {
				xvals.add(p1x - c);
			}
		}
		if (p1y <= p2y) {
			for (int c = 0; c < (p2y - p1y + 1); c++) {
				yvals.add(p1y + c);
			}
		} else if (p1y > p2y) {
			for (int c = 0; c < (p1y - p2y + 1); c++) {
				yvals.add(p1y - c);
			}
		}
		if (p1z <= p2z) {
			for (int c = 0; c < (p2z - p1z + 1); c++) {
				zvals.add(p1z + c);
			}
		} else if (p1z > p2z) {
			for (int c = 0; c < (p1z - p2z + 1); c++) {
				zvals.add(p1z - c);
			}
		}
		for (int x = 0; x < xvals.size(); x++) {
			for (int y = 0; y < yvals.size(); y++) {
				for (int z = 0; z < zvals.size(); z++) {
					shopBlockLocations.add(new Location(Bukkit.getWorld(world), xvals.get(x), yvals.get(y), zvals.get(z)));
				}
			}
		}
		return shopBlockLocations;
	}
	
	public boolean intersectsShop(Shop s, int volumeLimit) {
		if (s.getVolume() > volumeLimit) {return false;}
		for (Location l:s.getShopBlockLocations()) {
			if (inShop(l)) {
				return true;
			}
		}
		return false;
	}
}
