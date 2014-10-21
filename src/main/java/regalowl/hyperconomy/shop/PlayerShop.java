package regalowl.hyperconomy.shop;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;


import regalowl.databukkit.CommonFunctions;
import regalowl.databukkit.sql.BasicStatement;
import regalowl.databukkit.sql.QueryResult;
import regalowl.databukkit.sql.WriteStatement;
import regalowl.hyperconomy.HyperConomy;
import regalowl.hyperconomy.HyperEconomy;
import regalowl.hyperconomy.account.HyperAccount;
import regalowl.hyperconomy.account.HyperPlayer;
import regalowl.hyperconomy.event.ShopModificationEvent;
import regalowl.hyperconomy.hyperobject.BasicObject;
import regalowl.hyperconomy.hyperobject.BasicShopObject;
import regalowl.hyperconomy.hyperobject.CompositeItem;
import regalowl.hyperconomy.hyperobject.CompositeShopItem;
import regalowl.hyperconomy.hyperobject.HyperObject;
import regalowl.hyperconomy.hyperobject.HyperObjectStatus;
import regalowl.hyperconomy.hyperobject.HyperObjectType;
import regalowl.hyperconomy.hyperobject.ShopEnchant;
import regalowl.hyperconomy.util.LanguageFile;
import regalowl.hyperconomy.util.SimpleLocation;

public class PlayerShop implements Shop, Comparable<Shop> {

	private static final long serialVersionUID = -159740615025262195L;
	private String name;
	private String world;
	private String owner;
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
	private ArrayList<String> availableObjects = new ArrayList<String>();
	private boolean useEconomyStock;
	private boolean deleted;
	private boolean useshopexitmessage;
	private ArrayList<String> inShop = new ArrayList<String>();
	
	
	
	public PlayerShop(String name, String economy, HyperAccount owner, String message, SimpleLocation p1, SimpleLocation p2, String banned_objects, String allowed_players, boolean useEconomyStock) {
		HyperConomy hc = HyperConomy.hc;
		CommonFunctions cf = hc.getDataBukkit().getCommonFunctions();
		useshopexitmessage = hc.getConf().getBoolean("shop.display-shop-exit-message");	
		this.deleted = false;
		this.name = name;
		this.economy = economy;
		this.owner = owner.getName();
		this.message = message;
		this.world = p1.getWorld();
		this.p1x = p1.getBlockX();
		this.p1y = p1.getBlockY();
		this.p1z = p1.getBlockZ();
		this.p2x = p2.getBlockX();
		this.p2y = p2.getBlockY();
		this.p2z = p2.getBlockZ();
		this.allowed = cf.explode(allowed_players, ",");
		this.useEconomyStock = useEconomyStock;
		HyperEconomy he = getHyperEconomy();
		availableObjects.clear();
		for (HyperObject ho:he.getHyperObjects()) {
			availableObjects.add(ho.getName());
		}
		ArrayList<String> unavailable = hc.gCF().explode(banned_objects,",");
		for (String objectName : unavailable) {
			HyperObject ho = hc.getDataManager().getEconomy(economy).getHyperObject(objectName);
			availableObjects.remove(ho.getName());
		}
		loadPlayerShopObjects();
	}
	
	
	public PlayerShop(String shopName, String economy, HyperAccount owner, SimpleLocation p1, SimpleLocation p2) {
		HyperConomy hc = HyperConomy.hc;
		useshopexitmessage = hc.getConf().getBoolean("shop.display-shop-exit-message");
		this.deleted = false;
		this.name = shopName;
		this.economy = economy;
		this.owner = owner.getName();
		this.world = p1.getWorld();
		this.message = "";
		this.useEconomyStock = false;
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
		values.put("USE_ECONOMY_STOCK", "0");
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
			availableObjects.add(ho.getName());
		}
	}

	private void loadPlayerShopObjects() {
		HyperConomy hc = HyperConomy.hc;
		HyperEconomy he = hc.getDataManager().getEconomy(economy);
		BasicStatement statement = new BasicStatement("SELECT * FROM hyperconomy_shop_objects WHERE SHOP = ?", hc.getDataBukkit());
		statement.addParameter(name);
		QueryResult result = hc.getSQLRead().select(statement);
		while (result.next()) {
			double buyPrice = result.getDouble("BUY_PRICE");
			double sellPrice = result.getDouble("SELL_PRICE");
			int maxStock = result.getInt("MAX_STOCK");
			HyperObject ho = he.getHyperObject(result.getString("HYPEROBJECT"));
			if (ho == null) {
				continue;
			}
			double stock = result.getDouble("QUANTITY");
			HyperObjectStatus status = HyperObjectStatus.fromString(result.getString("STATUS"));
			if (ho.getType() == HyperObjectType.ITEM && ho.isCompositeObject()) {
				HyperObject pso = new CompositeShopItem(name, (CompositeItem) ho, stock, buyPrice, sellPrice, maxStock, status, useEconomyStock);
				shopContents.put(ho.getName(), pso);
			} else if (ho.getType() == HyperObjectType.ENCHANTMENT) {
				HyperObject pso = new ShopEnchant(name, ho, stock, buyPrice, sellPrice, maxStock, status, useEconomyStock);
				shopContents.put(ho.getName(), pso);
			} else {
				BasicShopObject pso = new BasicShopObject(name, ho, stock, buyPrice, sellPrice, maxStock, status, useEconomyStock);
				shopContents.put(ho.getName(), pso);
			}
		}
		result.close();
	}

	
	public int compareTo(Shop s) {
		return name.compareTo(s.getName());
	}
	
	public void setPoint1(String world, int x, int y, int z) {
		HyperConomy hc = HyperConomy.hc;
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
		hc.getHyperEventHandler().fireEvent(new ShopModificationEvent(this));
	}
	public void setPoint2(String world, int x, int y, int z) {
		HyperConomy hc = HyperConomy.hc;
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
		hc.getHyperEventHandler().fireEvent(new ShopModificationEvent(this));
	}
	public void setPoint1(SimpleLocation l) {
		setPoint1(l.getWorld(), l.getBlockX(), l.getBlockY(), l.getBlockZ());
	}
	public void setPoint2(SimpleLocation l) {
		setPoint2(l.getWorld(), l.getBlockX(), l.getBlockY(), l.getBlockZ());
	}
	
	
	public void setMessage(String message) {
		HyperConomy hc = HyperConomy.hc;
		this.message = message;
		HashMap<String,String> conditions = new HashMap<String,String>();
		HashMap<String,String> values = new HashMap<String,String>();
		conditions.put("NAME", name);
		values.put("MESSAGE", message);
		hc.getSQLWrite().performUpdate("hyperconomy_shops", values, conditions);
		hc.getHyperEventHandler().fireEvent(new ShopModificationEvent(this));
	}
	
	public void setDefaultMessage() {
		HyperConomy hc = HyperConomy.hc;
		LanguageFile L = hc.getLanguageFile();
		setMessage(L.get("SHOP_LINE_BREAK")+"%n&aWelcome to "+name+"%n&9Type &b/hc &9for help.%n"+L.get("SHOP_LINE_BREAK"));
	}
	
	public void setWorld(String world) {
		HyperConomy hc = HyperConomy.hc;
		this.world = world;
		HashMap<String,String> conditions = new HashMap<String,String>();
		HashMap<String,String> values = new HashMap<String,String>();
		conditions.put("NAME", name);
		values.put("WORLD", world);
		hc.getSQLWrite().performUpdate("hyperconomy_shops", values, conditions);
		hc.getHyperEventHandler().fireEvent(new ShopModificationEvent(this));
	}
	
	public void setName(String name) {
		HyperConomy hc = HyperConomy.hc;
		HashMap<String,String> conditions = new HashMap<String,String>();
		HashMap<String,String> values = new HashMap<String,String>();
		conditions.put("NAME", this.name);
		values.put("NAME", name);
		hc.getSQLWrite().performUpdate("hyperconomy_shops", values, conditions);
		this.name = name;
		hc.getHyperEventHandler().fireEvent(new ShopModificationEvent(this));
	}
	
	public void setEconomy(String economy) {
		HyperConomy hc = HyperConomy.hc;
		this.economy = economy;
		HashMap<String,String> conditions = new HashMap<String,String>();
		HashMap<String,String> values = new HashMap<String,String>();
		conditions.put("NAME", name);
		values.put("ECONOMY", economy);
		hc.getSQLWrite().performUpdate("hyperconomy_shops", values, conditions);
		hc.getHyperEventHandler().fireEvent(new ShopModificationEvent(this));
	}
	public void setUseEconomyStock(boolean state) {
		HyperConomy hc = HyperConomy.hc;
		this.useEconomyStock = state;
		HashMap<String,String> conditions = new HashMap<String,String>();
		HashMap<String,String> values = new HashMap<String,String>();
		conditions.put("NAME", name);
		if (useEconomyStock) {
			values.put("USE_ECONOMY_STOCK", "1");
		} else {
			values.put("USE_ECONOMY_STOCK", "0");
		}
		hc.getSQLWrite().performUpdate("hyperconomy_shops", values, conditions);
		for (HyperObject ho:shopContents.values()) {
			ho.setUseEconomyStock(useEconomyStock);
		}
		hc.getHyperEventHandler().fireEvent(new ShopModificationEvent(this));
	}
	
	
	@Override
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

	@Override
	public boolean inShop(SimpleLocation l) {
		return inShop(l.getBlockX(), l.getBlockY(), l.getBlockZ(), l.getWorld());
	}
	@Override
	public boolean inShop(HyperPlayer hp) {
		return inShop(hp.getLocation());
	}
	
	public void sendEntryMessage(HyperPlayer player) {
		HyperConomy hc = HyperConomy.hc;
		LanguageFile L = hc.getLanguageFile();
		if (message == "") {setDefaultMessage();}
		if (message.equalsIgnoreCase("none")) {return;}
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
		HyperConomy hc = HyperConomy.hc;
		HyperEconomy he = getHyperEconomy();
		ArrayList<String> unavailable = new ArrayList<String>();
		ArrayList<HyperObject> allObjects = he.getHyperObjects();
		for (HyperObject ho:allObjects) {
			if (!availableObjects.contains(ho.getName())) {
				unavailable.add(ho.getName());
			}
		}
		HashMap<String,String> conditions = new HashMap<String,String>();
		HashMap<String,String> values = new HashMap<String,String>();
		conditions.put("NAME", name);
		values.put("BANNED_OBJECTS", hc.gCF().implode(unavailable,","));
		hc.getSQLWrite().performUpdate("hyperconomy_shops", values, conditions);
		hc.getHyperEventHandler().fireEvent(new ShopModificationEvent(this));
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
		if (availableObjects.contains(co.getName())) {
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
			availableObjects.add(ho.getName());
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
			if (!availableObjects.contains(add.getName())) {
				availableObjects.add(add.getName());
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
			if (availableObjects.contains(remove.getName())) {
				availableObjects.remove(remove.getName());
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
	
	public SimpleLocation getLocation1() {
		return new SimpleLocation(world, p1x, p1y, p1z);
	}
	
	public SimpleLocation getLocation2() {
		return new SimpleLocation(world, p2x, p2y, p2z);
	}
	
	public boolean getUseEconomyStock() {
		return useEconomyStock;
	}
	
	public HyperAccount getOwner() {
		HyperConomy hc = HyperConomy.hc;
		return hc.getHyperPlayerManager().getAccount(owner);
	}
	
	public void setOwner(HyperAccount owner) {
		HyperConomy hc = HyperConomy.hc;
		this.owner = owner.getName();
		HashMap<String,String> conditions = new HashMap<String,String>();
		HashMap<String,String> values = new HashMap<String,String>();
		conditions.put("NAME", name);
		values.put("OWNER", owner.getName());
		hc.getSQLWrite().performUpdate("hyperconomy_shops", values, conditions);
		hc.getHyperEventHandler().fireEvent(new ShopModificationEvent(this));
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
		HyperConomy hc = HyperConomy.hc;
		HashMap<String,String> conditions = new HashMap<String,String>();
		conditions.put("NAME", name);
		hc.getSQLWrite().performDelete("hyperconomy_shops", conditions);
		conditions = new HashMap<String,String>();
		conditions.put("SHOP", name);
		hc.getSQLWrite().performDelete("hyperconomy_shop_objects", conditions);
		shopContents.clear();
		hc.getHyperShopManager().removeShop(name);
		this.deleted = true;
		hc.getHyperEventHandler().fireEvent(new ShopModificationEvent(this));
	}
	
	public void removePlayerShopObject(HyperObject hyperObject) {
		HyperConomy hc = HyperConomy.hc;
		HyperObject pso = getPlayerShopObject(hyperObject);
		if (pso == null) {
			return;
		} else {
			shopContents.remove(pso);
			hc.getSQLWrite().addToQueue("DELETE FROM hyperconomy_shop_objects WHERE SHOP = '"+name+"' AND HYPEROBJECT = '"+hyperObject.getName()+"'");
			hc.getHyperEventHandler().fireEvent(new ShopModificationEvent(this));
		}
	}
	public HyperObject getPlayerShopObject(HyperObject hyperObject) {
		HyperConomy hc = HyperConomy.hc;
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
			CompositeShopItem pso = new CompositeShopItem(name, (CompositeItem)hyperObject, 0.0, 0.0, 0.0, 100000, HyperObjectStatus.NONE, useEconomyStock);
			shopContents.put(hyperObject.getName(), pso);
			hc.getSQLWrite().addToQueue(ws);
			return pso;
		} else if (hyperObject.getType() == HyperObjectType.ENCHANTMENT) {
			HyperObject pso = new ShopEnchant(name, hyperObject, 0.0, 0.0, 0.0, 100000, HyperObjectStatus.NONE, useEconomyStock);
			shopContents.put(hyperObject.getName(), pso);
			hc.getSQLWrite().addToQueue(ws);
			return pso;
		} else {
			HyperObject pso = new BasicShopObject(name, (BasicObject)hyperObject, 0.0, 0.0, 0.0, 100000, HyperObjectStatus.NONE, useEconomyStock);
			shopContents.put(hyperObject.getName(), pso);
			hc.getSQLWrite().addToQueue(ws);
			return pso;
		}
	}
	

	public boolean hasPlayerShopObject(HyperObject ho) {
		return shopContents.containsKey(ho.getName());
	}
	

	public HyperEconomy getHyperEconomy() {
		HyperConomy hc = HyperConomy.hc;
		HyperEconomy he = hc.getDataManager().getEconomy(economy);
		if (he == null) {
			hc.getDataBukkit().writeError("Null HyperEconomy for economy: " + economy + ", shop: " + name);
			he = hc.getDataManager().getEconomy("default");
		}
		return he;
	}



	public void updatePlayerStatus() {
		HyperConomy hc = HyperConomy.hc;
		LanguageFile L = hc.getLanguageFile();
		for (HyperPlayer p : hc.getHyperPlayerManager().getOnlinePlayers()) {
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
					p.setEconomy(economy);
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
		if (ha.getName().equalsIgnoreCase(owner)) {
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
		HyperConomy hc = HyperConomy.hc;
		CommonFunctions cf = hc.getDataBukkit().getCommonFunctions();
		HashMap<String,String> conditions = new HashMap<String,String>();
		HashMap<String,String> values = new HashMap<String,String>();
		conditions.put("NAME", name);
		values.put("ALLOWED_PLAYERS", cf.implode(allowed, ","));
		hc.getSQLWrite().performUpdate("hyperconomy_shops", values, conditions);
		hc.getHyperEventHandler().fireEvent(new ShopModificationEvent(this));
	}


	public ArrayList<SimpleLocation> getShopBlockLocations() {
		ArrayList<SimpleLocation> shopBlockLocations = new ArrayList<SimpleLocation>();
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
					shopBlockLocations.add(new SimpleLocation(world, xvals.get(x), yvals.get(y), zvals.get(z)));
				}
			}
		}
		return shopBlockLocations;
	}
	
	public boolean intersectsShop(Shop s, int volumeLimit) {
		if (s.getVolume() > volumeLimit) {return false;}
		for (SimpleLocation l:s.getShopBlockLocations()) {
			if (inShop(l)) {
				return true;
			}
		}
		return false;
	}
	
	public boolean deleted() {
		return deleted;
	}
	
	public void updateHyperObject(HyperObject ho) {
		if (!ho.isShopObject()) return;
		if (!ho.getShop().equals(this)) return;
		if (!shopContents.containsKey(ho.getName())) return;
		shopContents.put(ho.getName(), ho);
	}
}
