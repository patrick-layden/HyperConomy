package regalowl.hyperconomy.shop;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

import regalowl.hyperconomy.HyperConomy;
import regalowl.hyperconomy.HyperEconomy;
import regalowl.hyperconomy.account.HyperAccount;
import regalowl.hyperconomy.account.HyperBank;
import regalowl.hyperconomy.account.HyperPlayer;
import regalowl.hyperconomy.event.ShopModificationEvent;
import regalowl.hyperconomy.minecraft.HLocation;
import regalowl.hyperconomy.tradeobject.BasicShopTradeObject;
import regalowl.hyperconomy.tradeobject.CompositeShopTradeItem;
import regalowl.hyperconomy.tradeobject.CompositeTradeItem;
import regalowl.hyperconomy.tradeobject.ShopTradeEnchant;
import regalowl.hyperconomy.tradeobject.TradeObject;
import regalowl.hyperconomy.tradeobject.TradeObjectStatus;
import regalowl.hyperconomy.tradeobject.TradeObjectType;
import regalowl.hyperconomy.util.LanguageFile;
import regalowl.simpledatalib.CommonFunctions;
import regalowl.simpledatalib.sql.BasicStatement;
import regalowl.simpledatalib.sql.QueryResult;
import regalowl.simpledatalib.sql.WriteStatement;

public class PlayerShop implements Shop {

	private transient HyperConomy hc;
	private static final long serialVersionUID = -159740615025262195L;
	private String name;
	private String world;
	private String owner;
	private boolean ownerIsBank;
	private ArrayList<String> allowed = new ArrayList<String>();
	private String economy;
	private String message;
	private int p1x;
	private int p1y;
	private int p1z;
	private int p2x;
	private int p2y;
	private int p2z;
	private ConcurrentHashMap<String,TradeObject> shopContents = new ConcurrentHashMap<String,TradeObject>();
	private ArrayList<String> availableObjects = new ArrayList<String>();
	private boolean useEconomyStock;
	private boolean deleted;
	private boolean useshopexitmessage;
	private ArrayList<String> inShop = new ArrayList<String>();
	
	
	
	public PlayerShop(HyperConomy hc, String name, String economy, HyperAccount owner, String message, HLocation p1, HLocation p2, String banned_objects, String allowed_players, boolean useEconomyStock) {
		this.hc = hc;
		useshopexitmessage = hc.getConf().getBoolean("shop.display-shop-exit-message");	
		this.deleted = false;
		this.name = name;
		this.economy = economy;
		if (owner instanceof HyperBank) ownerIsBank = true;
		this.owner = owner.getName();
		this.message = message;
		this.world = p1.getWorld();
		this.p1x = p1.getBlockX();
		this.p1y = p1.getBlockY();
		this.p1z = p1.getBlockZ();
		this.p2x = p2.getBlockX();
		this.p2y = p2.getBlockY();
		this.p2z = p2.getBlockZ();
		this.allowed = CommonFunctions.explode(allowed_players);
		this.useEconomyStock = useEconomyStock;
		HyperEconomy he = getHyperEconomy();
		availableObjects.clear();
		for (TradeObject ho:he.getTradeObjects()) {
			availableObjects.add(ho.getName());
		}
		ArrayList<String> unavailable = CommonFunctions.explode(banned_objects);
		for (String objectName : unavailable) {
			TradeObject ho = hc.getDataManager().getEconomy(economy).getTradeObject(objectName);
			if (ho == null) continue;
			availableObjects.remove(ho.getName());
		}
		loadPlayerShopObjects();
	}
	
	
	public PlayerShop(HyperConomy hc, String shopName, String economy, HyperAccount owner, HLocation p1, HLocation p2) {
		this.hc = hc;
		useshopexitmessage = hc.getConf().getBoolean("shop.display-shop-exit-message");
		this.deleted = false;
		this.name = shopName;
		this.economy = economy;
		if (owner instanceof HyperBank) ownerIsBank = true;
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
		if (ownerIsBank) {
			values.put("OWNER", "BANK:"+owner.getName());
		} else {
			values.put("OWNER", "PLAYER:"+owner.getName());
		}
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
		for (TradeObject ho:he.getTradeObjects()) {
			if (ho == null) continue;
			availableObjects.add(ho.getName());
		}
	}
	
	@Override
	public void setHyperConomy(HyperConomy hc) {
		this.hc = hc;
	}

	private void loadPlayerShopObjects() {
		HyperEconomy he = hc.getDataManager().getEconomy(economy);
		BasicStatement statement = new BasicStatement("SELECT * FROM hyperconomy_shop_objects WHERE SHOP = ?", hc.getSimpleDataLib());
		statement.addParameter(name);
		QueryResult result = hc.getSQLRead().select(statement);
		while (result.next()) {
			double buyPrice = result.getDouble("BUY_PRICE");
			double sellPrice = result.getDouble("SELL_PRICE");
			int maxStock = result.getInt("MAX_STOCK");
			TradeObject ho = he.getTradeObject(result.getString("HYPEROBJECT"));
			if (ho == null) {
				continue;
			}
			double stock = result.getDouble("QUANTITY");
			TradeObjectStatus status = TradeObjectStatus.fromString(result.getString("STATUS"));
			if (ho.getType() == TradeObjectType.ITEM && ho.isCompositeObject()) {
				TradeObject pso = new CompositeShopTradeItem(hc, name, (CompositeTradeItem) ho, stock, buyPrice, sellPrice, maxStock, status, useEconomyStock);
				shopContents.put(ho.getName(), pso);
			} else if (ho.getType() == TradeObjectType.ENCHANTMENT) {
				TradeObject pso = new ShopTradeEnchant(hc, name, ho, stock, buyPrice, sellPrice, maxStock, status, useEconomyStock);
				shopContents.put(ho.getName(), pso);
			} else {
				BasicShopTradeObject pso = new BasicShopTradeObject(hc, name, ho, stock, buyPrice, sellPrice, maxStock, status, useEconomyStock);
				shopContents.put(ho.getName(), pso);
			}
		}
		result.close();
	}

	
	@Override
	public int compareTo(Shop s) {
		return name.compareTo(s.getName());
	}
	
	@Override
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
		hc.getHyperEventHandler().fireEvent(new ShopModificationEvent(this));
	}
	@Override
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
		hc.getHyperEventHandler().fireEvent(new ShopModificationEvent(this));
	}
	@Override
	public void setPoint1(HLocation l) {
		setPoint1(l.getWorld(), l.getBlockX(), l.getBlockY(), l.getBlockZ());
	}
	@Override
	public void setPoint2(HLocation l) {
		setPoint2(l.getWorld(), l.getBlockX(), l.getBlockY(), l.getBlockZ());
	}
	
	
	@Override
	public void setMessage(String message) {
		this.message = message;
		HashMap<String,String> conditions = new HashMap<String,String>();
		HashMap<String,String> values = new HashMap<String,String>();
		conditions.put("NAME", name);
		values.put("MESSAGE", message);
		hc.getSQLWrite().performUpdate("hyperconomy_shops", values, conditions);
		hc.getHyperEventHandler().fireEvent(new ShopModificationEvent(this));
	}
	
	@Override
	public void setDefaultMessage() {
		LanguageFile L = hc.getLanguageFile();
		setMessage(L.get("SHOP_LINE_BREAK")+"%n&aWelcome to "+name+"%n&9Type &b/hc &9for help.%n"+L.get("SHOP_LINE_BREAK"));
	}
	
	@Override
	public void setWorld(String world) {
		this.world = world;
		HashMap<String,String> conditions = new HashMap<String,String>();
		HashMap<String,String> values = new HashMap<String,String>();
		conditions.put("NAME", name);
		values.put("WORLD", world);
		hc.getSQLWrite().performUpdate("hyperconomy_shops", values, conditions);
		hc.getHyperEventHandler().fireEvent(new ShopModificationEvent(this));
	}
	
	@Override
	public void setName(String name) {
		HashMap<String,String> conditions = new HashMap<String,String>();
		HashMap<String,String> values = new HashMap<String,String>();
		conditions.put("NAME", this.name);
		values.put("NAME", name);
		hc.getSQLWrite().performUpdate("hyperconomy_shops", values, conditions);
		this.name = name;
		hc.getHyperEventHandler().fireEvent(new ShopModificationEvent(this));
	}
	
	@Override
	public void setEconomy(String economy) {
		this.economy = economy;
		HashMap<String,String> conditions = new HashMap<String,String>();
		HashMap<String,String> values = new HashMap<String,String>();
		conditions.put("NAME", name);
		values.put("ECONOMY", economy);
		hc.getSQLWrite().performUpdate("hyperconomy_shops", values, conditions);
		hc.getHyperEventHandler().fireEvent(new ShopModificationEvent(this));
	}
	public void setUseEconomyStock(boolean state) {
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
		for (TradeObject ho:shopContents.values()) {
			ho.setUseEconomyStock(useEconomyStock);
		}
		hc.getHyperEventHandler().fireEvent(new ShopModificationEvent(this));
	}
	
	
	@Override
	public boolean inShop(int x, int y, int z, String world) {
		if (world == null) return false;
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
	public boolean inShop(HLocation l) {
		if (l == null) return false;
		return inShop(l.getBlockX(), l.getBlockY(), l.getBlockZ(), l.getWorld());
	}
	@Override
	public boolean inShop(HyperPlayer hp) {
		if (hp == null) return false;
		return inShop(hp.getLocation());
	}
	
	@Override
	public void sendEntryMessage(HyperPlayer player) {
		if (message == "") {setDefaultMessage();}
		if (message.equalsIgnoreCase("none")) {return;}
		String[] lines = message.replace("_", " ").split("%n");
		for (String line:lines) {
			player.sendMessage(line);
		}
	}
	
	@Override
	public String getEconomy() {
		return economy;
	}
	
	@Override
	public String getName() {
		return name;
	}
	
	@Override
	public String getDisplayName() {
		return name.replace("_", " ");
	}
	
	

	@Override
	public void saveAvailable() {
		HyperEconomy he = getHyperEconomy();
		ArrayList<String> unavailable = new ArrayList<String>();
		ArrayList<TradeObject> allObjects = he.getTradeObjects();
		for (TradeObject ho:allObjects) {
			if (!availableObjects.contains(ho.getName())) {
				unavailable.add(ho.getName());
			}
		}
		HashMap<String,String> conditions = new HashMap<String,String>();
		HashMap<String,String> values = new HashMap<String,String>();
		conditions.put("NAME", name);
		values.put("BANNED_OBJECTS", CommonFunctions.implode(unavailable));
		hc.getSQLWrite().performUpdate("hyperconomy_shops", values, conditions);
		hc.getHyperEventHandler().fireEvent(new ShopModificationEvent(this));
	}
	
	@Override
	public boolean isStocked(TradeObject ho) {
		TradeObject pso = ho;
		if (!ho.isShopObject()) {
			pso = shopContents.get(ho.getName());
		}
		if (pso == null) {return false;}
		if (pso.getStock() <= 0.0) {return false;}
		return true;
	}
	@Override
	public boolean isStocked(String item) {
		return isStocked(getHyperEconomy().getTradeObject(item));
	}
	@Override
	public boolean isBanned(String to) {
		if (availableObjects.contains(to)) {
			return false;
		}
		return true;
	}
	@Override
	public boolean isTradeable(TradeObject ho) {
		if (!isBanned(ho.getName())) {
			if (ho.isShopObject()) {
				if (ho.getShopObjectStatus() == TradeObjectStatus.NONE) {return false;}
				return true;
			} else {
				return true;
			}
		}
		return false;
	}
	@Override
	public boolean isAvailable(TradeObject ho) {
		if (isTradeable(ho) && isStocked(ho)) {
			return true;
		}
		return false;
	}

	
	@Override
	public ArrayList<TradeObject> getTradeableObjects() {
		ArrayList<TradeObject> available = new ArrayList<TradeObject>();
		for (TradeObject pso:shopContents.values()) {
			if (isTradeable(pso)) {
				available.add(pso);
			}
		}
		return available;
	}
	
	public ArrayList<TradeObject> getShopObjects() {
		ArrayList<TradeObject> objects = new ArrayList<TradeObject>();
		for (TradeObject pso:shopContents.values()) {
			objects.add(pso);
		}
		return objects;
	}
	
	@Override
	public void unBanAllObjects() {
		availableObjects.clear();
		for (TradeObject ho:getHyperEconomy().getTradeObjects()) {
			availableObjects.add(ho.getName());
		}
		saveAvailable();
	}
	@Override
	public void banAllObjects() {
		availableObjects.clear();
		saveAvailable();
	}
	@Override
	public void unBanObjects(ArrayList<TradeObject> objects) {
		for (TradeObject ho:objects) {
			TradeObject add = null;
			if (ho.isShopObject()) {
				add = ho.getParentTradeObject();
			} else {
				add = ho;
			}
			if (!availableObjects.contains(add.getName())) {
				availableObjects.add(add.getName());
			}
		}
		saveAvailable();
	}
	@Override
	public void banObjects(ArrayList<TradeObject> objects) {
		for (TradeObject ho:objects) {
			TradeObject remove = null;
			if (ho.isShopObject()) {
				remove = ho.getParentTradeObject();
			} else {
				remove = ho;
			}
			if (availableObjects.contains(remove.getName())) {
				availableObjects.remove(remove.getName());
			}
		}
		saveAvailable();
	}
	

	
	@Override
	public int getP1x() {
		return p1x;
	}

	@Override
	public int getP1y() {
		return p1y;
	}

	@Override
	public int getP1z() {
		return p1z;
	}

	@Override
	public int getP2x() {
		return p2x;
	}

	@Override
	public int getP2y() {
		return p2y;
	}

	@Override
	public int getP2z() {
		return p2z;
	}
	
	@Override
	public HLocation getLocation1() {
		return new HLocation(world, p1x, p1y, p1z);
	}
	
	@Override
	public HLocation getLocation2() {
		return new HLocation(world, p2x, p2y, p2z);
	}
	
	public boolean getUseEconomyStock() {
		return useEconomyStock;
	}
	
	@Override
	public HyperAccount getOwner() {
		if (ownerIsBank) {
			return hc.getHyperBankManager().getHyperBank(owner);
		} else {
			return hc.getHyperPlayerManager().getHyperPlayer(owner);
		}
	}
	
	@Override
	public void setOwner(HyperAccount owner) {
		this.owner = owner.getName();
		if (owner instanceof HyperBank) ownerIsBank = true;
		HashMap<String,String> conditions = new HashMap<String,String>();
		HashMap<String,String> values = new HashMap<String,String>();
		conditions.put("NAME", name);
		if (ownerIsBank) {
			values.put("OWNER", "BANK:"+owner.getName());
		} else {
			values.put("OWNER", "PLAYER:"+owner.getName());
		}
		hc.getSQLWrite().performUpdate("hyperconomy_shops", values, conditions);
		hc.getHyperEventHandler().fireEvent(new ShopModificationEvent(this));
	}


	
	public boolean isEmpty() {
		for (TradeObject pso:shopContents.values()) {
			if (pso.getStock() > 0) {
				return false;
			}
		}
		return true;
	}
	
	@Override
	public void deleteShop() {
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
	
	public void removePlayerShopObject(TradeObject hyperObject) {
		TradeObject pso = getPlayerShopObject(hyperObject);
		if (pso == null) {
			return;
		} else {
			shopContents.remove(pso);
			hc.getSQLWrite().addToQueue("DELETE FROM hyperconomy_shop_objects WHERE SHOP = '"+name+"' AND HYPEROBJECT = '"+hyperObject.getName()+"'");
			hc.getHyperEventHandler().fireEvent(new ShopModificationEvent(this));
		}
	}
	public TradeObject getPlayerShopObject(TradeObject hyperObject) {
		if (shopContents.containsKey(hyperObject.getName())) {
			return shopContents.get(hyperObject.getName());
		}
		WriteStatement ws = new WriteStatement("INSERT INTO hyperconomy_shop_objects (SHOP, HYPEROBJECT, QUANTITY, BUY_PRICE, SELL_PRICE, MAX_STOCK, STATUS) VALUES (?,?,?,?,?,?,?)", hc.getSimpleDataLib());
		ws.addParameter(name);
		ws.addParameter(hyperObject.getName());
		ws.addParameter(0.0);
		ws.addParameter(0.0);
		ws.addParameter(0.0);
		ws.addParameter(1000000);
		ws.addParameter("none");
		if (hyperObject.getType() == TradeObjectType.ITEM && hyperObject.isCompositeObject()) {
			CompositeShopTradeItem pso = new CompositeShopTradeItem(hc, name, (CompositeTradeItem)hyperObject, 0.0, 0.0, 0.0, 100000, TradeObjectStatus.NONE, useEconomyStock);
			shopContents.put(hyperObject.getName(), pso);
			hc.getSQLWrite().addToQueue(ws);
			return pso;
		} else if (hyperObject.getType() == TradeObjectType.ENCHANTMENT) {
			TradeObject pso = new ShopTradeEnchant(hc, name, hyperObject, 0.0, 0.0, 0.0, 100000, TradeObjectStatus.NONE, useEconomyStock);
			shopContents.put(hyperObject.getName(), pso);
			hc.getSQLWrite().addToQueue(ws);
			return pso;
		} else {
			TradeObject pso = new BasicShopTradeObject(hc, name, hyperObject, 0.0, 0.0, 0.0, 100000,
					TradeObjectStatus.NONE, useEconomyStock);
			shopContents.put(hyperObject.getName(), pso);
			hc.getSQLWrite().addToQueue(ws);
			return pso;
		}
	}
	

	public boolean hasPlayerShopObject(TradeObject ho) {
		return shopContents.containsKey(ho.getName());
	}
	

	@Override
	public HyperEconomy getHyperEconomy() {
		HyperEconomy he = hc.getDataManager().getEconomy(economy);
		if (he == null) {
			hc.getSimpleDataLib().getErrorWriter().writeError("Null HyperEconomy for economy: " + economy + ", shop: " + name);
			he = hc.getDataManager().getEconomy("default");
		}
		return he;
	}



	@Override
	public void updatePlayerStatus() {
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



	@Override
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
		if (ha == null) return false;
		if (allowed.contains(ha.getName())) {
			return true;
		}
		if (ha.getName().equalsIgnoreCase(owner)) {
			return true;
		}
		if (ha instanceof HyperPlayer) {
			HyperPlayer hp = (HyperPlayer)ha;
			if (hp.hasPermission("hyperconomy.admin")) {
				return true;
			}
		}
		return false;
	}
	public void saveAllowed() {
		HashMap<String,String> conditions = new HashMap<String,String>();
		HashMap<String,String> values = new HashMap<String,String>();
		conditions.put("NAME", name);
		values.put("ALLOWED_PLAYERS", CommonFunctions.implode(allowed));
		hc.getSQLWrite().performUpdate("hyperconomy_shops", values, conditions);
		hc.getHyperEventHandler().fireEvent(new ShopModificationEvent(this));
	}


	@Override
	public ArrayList<HLocation> getShopBlockLocations() {
		ArrayList<HLocation> shopBlockLocations = new ArrayList<HLocation>();
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
					shopBlockLocations.add(new HLocation(world, xvals.get(x), yvals.get(y), zvals.get(z)));
				}
			}
		}
		return shopBlockLocations;
	}
	
	@Override
	public boolean intersectsShop(Shop s, int volumeLimit) {
		if (s.getVolume() > volumeLimit) {return false;}
		for (HLocation l:s.getShopBlockLocations()) {
			if (inShop(l)) {
				return true;
			}
		}
		return false;
	}
	
	@Override
	public boolean deleted() {
		return deleted;
	}
	
	public void updateHyperObject(TradeObject ho) {
		if (!ho.isShopObject()) return;
		if (!ho.getShopObjectShop().equals(this)) return;
		if (!shopContents.containsKey(ho.getName())) return;
		shopContents.put(ho.getName(), ho);
	}
	
	@Override
	public void removeTradeObject(TradeObject to) {
		availableObjects.remove(to.getName());
		removePlayerShopObject(to);
	}
}
