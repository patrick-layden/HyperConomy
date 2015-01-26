package regalowl.hyperconomy;


import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import regalowl.simpledatalib.CommonFunctions;
import regalowl.simpledatalib.event.EventHandler;
import regalowl.simpledatalib.file.FileConfiguration;
import regalowl.simpledatalib.file.FileTools;
import regalowl.simpledatalib.sql.QueryResult;
import regalowl.simpledatalib.sql.SQLRead;
import regalowl.simpledatalib.sql.SQLWrite;
import regalowl.hyperconomy.account.HyperAccount;
import regalowl.hyperconomy.account.HyperPlayer;
import regalowl.hyperconomy.event.DataLoadEvent;
import regalowl.hyperconomy.event.DataLoadEvent.DataLoadType;
import regalowl.hyperconomy.inventory.HEnchantment;
import regalowl.hyperconomy.inventory.HItemStack;
import regalowl.hyperconomy.shop.PlayerShop;
import regalowl.hyperconomy.shop.Shop;
import regalowl.hyperconomy.tradeobject.ComponentTradeItem;
import regalowl.hyperconomy.tradeobject.CompositeTradeItem;
import regalowl.hyperconomy.tradeobject.TradeEnchant;
import regalowl.hyperconomy.tradeobject.TradeObject;
import regalowl.hyperconomy.tradeobject.TradeObjectType;
import regalowl.hyperconomy.tradeobject.TradeXp;



public class HyperEconomy implements Serializable {

	private static final long serialVersionUID = 4820082604724045149L;
	private transient HyperConomy hc;
	
	private HyperAccount defaultAccount;
	private ConcurrentHashMap<String, TradeObject> tradeObjectsNameMap = new ConcurrentHashMap<String, TradeObject>();
	private CopyOnWriteArrayList<TradeObject> tradeObjects = new CopyOnWriteArrayList<TradeObject>();
	private HashMap<String,String> composites = new HashMap<String,String>();
	private boolean useComposites;
	private String economyName;
	private String xpName = null;
	private String lastFailedToLoadComposite = "";
	

	public HyperEconomy(HyperConomy hc, String economy) {
		this.hc = hc;	
		SQLRead sr = hc.getSQLRead();
		this.economyName = economy;
		hc.getHyperEventHandler().registerListener(this);
		useComposites = hc.getConf().getBoolean("enable-feature.composite-items");
		loadData(sr);
	}
	
	public HyperEconomy(String economy, SQLRead sr, FileConfiguration config) {
		this.economyName = economy;
		useComposites = config.getBoolean("enable-feature.composite-items");
		loadData(sr);
	}
	
	private void loadData(SQLRead sr) {
		composites.clear();
		QueryResult result = sr.select("SELECT * FROM hyperconomy_composites");
		while (result.next()) {
			composites.put(result.getString("NAME").toLowerCase(), result.getString("COMPONENTS"));
		}
		tradeObjectsNameMap.clear();
		result = sr.select("SELECT * FROM hyperconomy_objects WHERE ECONOMY = '"+economyName+"'");
		while (result.next()) {
			if (useComposites && composites.containsKey(result.getString("NAME").toLowerCase())) {continue;}
			TradeObjectType type = TradeObjectType.fromString(result.getString("TYPE"));
			TradeObject to = null;
			if (type == TradeObjectType.ITEM) {
				to = new ComponentTradeItem(hc, result.getString("NAME"), result.getString("ECONOMY"), 
						result.getString("DISPLAY_NAME"), result.getString("ALIASES"), result.getString("CATEGORIES"), result.getString("TYPE"), result.getDouble("VALUE"), result.getString("STATIC"), result.getDouble("STATICPRICE"),
						result.getDouble("STOCK"), result.getDouble("MEDIAN"), result.getString("INITIATION"), result.getDouble("STARTPRICE"), 
						result.getDouble("CEILING"),result.getDouble("FLOOR"), result.getDouble("MAXSTOCK"), result.getString("DATA"));
			} else if (type == TradeObjectType.ENCHANTMENT) {
				to = new TradeEnchant(hc, result.getString("NAME"), result.getString("ECONOMY"), 
						result.getString("DISPLAY_NAME"), result.getString("ALIASES"), result.getString("CATEGORIES"), result.getString("TYPE"), 
						result.getDouble("VALUE"), result.getString("STATIC"), result.getDouble("STATICPRICE"),
						result.getDouble("STOCK"), result.getDouble("MEDIAN"), result.getString("INITIATION"), result.getDouble("STARTPRICE"), 
						result.getDouble("CEILING"),result.getDouble("FLOOR"), result.getDouble("MAXSTOCK"), result.getString("DATA"));
			} else if (type == TradeObjectType.EXPERIENCE) {
				to = new TradeXp(hc, result.getString("NAME"), result.getString("ECONOMY"), 
						result.getString("DISPLAY_NAME"), result.getString("ALIASES"), result.getString("CATEGORIES"), result.getString("TYPE"), 
						result.getDouble("VALUE"), result.getString("STATIC"), result.getDouble("STATICPRICE"),
						result.getDouble("STOCK"), result.getDouble("MEDIAN"), result.getString("INITIATION"), result.getDouble("STARTPRICE"), 
						result.getDouble("CEILING"),result.getDouble("FLOOR"), result.getDouble("MAXSTOCK"));
				xpName = result.getString("NAME");
			}
			if (to != null) addObject(to);
		}
		result.close();
		if (xpName == null) xpName = "xp";
		if (useComposites) loadComposites(sr);
	}

	
	
	public void addObject(TradeObject to) {
		if (to == null) return;
		tradeObjectsNameMap.put(to.getName().toLowerCase(), to);
		tradeObjectsNameMap.put(to.getDisplayName().toLowerCase(), to);
		for (String alias:to.getAliases()) {
			tradeObjectsNameMap.put(alias.toLowerCase(), to);
		}
		tradeObjects.add(to);
	}
	
	public void removeObject(TradeObject to) {
		if (to == null) return;
		tradeObjectsNameMap.remove(to.getName().toLowerCase());
		tradeObjectsNameMap.remove(to.getDisplayName().toLowerCase());
		for (String alias:to.getAliases()) {
			tradeObjectsNameMap.remove(alias.toLowerCase());
		}
		tradeObjects.remove(to);
	}
	
	public void removeObject(String name) {
		TradeObject to = getTradeObject(name);
		if (to == null) return;
		removeObject(to);
	}

	
	private void loadComposites(SQLRead sr) {
		boolean loaded = false;
		int counter = 0;
		QueryResult result = sr.select("SELECT hyperconomy_objects.NAME, hyperconomy_objects.DISPLAY_NAME, "
				+ "hyperconomy_objects.ALIASES, hyperconomy_objects.CATEGORIES, hyperconomy_objects.TYPE, hyperconomy_composites.COMPONENTS,"
				+ " hyperconomy_objects.DATA FROM hyperconomy_composites, hyperconomy_objects WHERE "
				+ "hyperconomy_composites.NAME = hyperconomy_objects.NAME");
		while (!loaded) {
			counter++;
			if (counter > 100) {
				if (hc != null) {
					hc.getSimpleDataLib().getErrorWriter().writeError("Infinite loop when loading composites.yml.  You likely have an error in your composites.yml file.  Your items will not work properly until this is fixed.");
					hc.getSimpleDataLib().getErrorWriter().writeError("The last missing object is: ["+lastFailedToLoadComposite+"]  Make sure this object is available or remove it from the composites table.");
				}
				return;
			}
			loaded = true;
			result.reset();
			while (result.next()) {
				String name = result.getString("NAME");
				if (getTradeObject(name) != null) continue;
				if (!componentsLoaded(name)) {
					loaded = false;
					continue;
				}
				TradeObject to = new CompositeTradeItem(hc, this, name, economyName, result.getString("DISPLAY_NAME"), result.getString("ALIASES"), result.getString("CATEGORIES"), 
						result.getString("TYPE"), result.getString("COMPONENTS"), result.getString("DATA"));
				addObject(to);
			}
		}
	}
	
	
	
	private boolean componentsLoaded(String name) {
		HashMap<String,String> tempComponents = CommonFunctions.explodeMap(composites.get(name.toLowerCase()));
		for (Map.Entry<String,String> entry : tempComponents.entrySet()) {
		    String oname = entry.getKey();
		    TradeObject ho = getTradeObject(oname);
		    if (ho == null) {
		    	lastFailedToLoadComposite = oname;
		    	//System.out.println("Not loaded: " + oname);
		    	return false;
		    }
		}
		return true;
	}
	
	
	@EventHandler
	public void onDataLoadEvent(DataLoadEvent event) {
		if (!(event.loadType == DataLoadType.COMPLETE)) return;
		new Thread(new Runnable() {
			public void run() {
				SQLRead sr = hc.getSQLRead();
				HashMap<String, String> conditions = new HashMap<String, String>();
				conditions.put("NAME", economyName);
				String account = sr.getString("hyperconomy_economies", "hyperaccount", conditions);
				defaultAccount = hc.getDataManager().getAccount(account);
			}
		}).start();
	}
	
	public void delete() {
		for (Shop shop:hc.getDataManager().getHyperShopManager().getShops()) {
			if (shop.getEconomy().equalsIgnoreCase(economyName)) {
				shop.setEconomy("default");
			}
		}
		for (HyperPlayer hp:hc.getDataManager().getHyperPlayerManager().getHyperPlayers()) {
			if (hp.getEconomy().equalsIgnoreCase(economyName)) {
				hp.setEconomy("default");
			}
		}
		hc.getDataManager().deleteEconomy(economyName);
	}

	public HyperAccount getDefaultAccount() {
		return defaultAccount;
	}
	
	public void setDefaultAccount(HyperAccount account) {
		if (account == null) {return;}
		HashMap<String,String> conditions = new HashMap<String,String>();
		HashMap<String,String> values = new HashMap<String,String>();
		conditions.put("NAME", economyName);
		values.put("HYPERACCOUNT", account.getName());
		hc.getSQLWrite().performUpdate("hyperconomy_economies", values, conditions);
		this.defaultAccount = account;
	}
	
	public String getName() {
		return economyName;
	}
	


	public TradeObject getTradeObject(HEnchantment enchant) {
		return getTradeObject(enchant, null);
	}
	public TradeObject getTradeObject(HEnchantment enchant, Shop s) {
		if (enchant == null) {return null;}
		if (s != null && s instanceof PlayerShop) {
			for (TradeObject ho:tradeObjects) {
				if (ho.getType() != TradeObjectType.ENCHANTMENT) {continue;}
				if (!ho.matchesEnchantment(enchant)) {continue;}
				return (TradeObject) ((PlayerShop) s).getPlayerShopObject(ho);
			}
		} else {
			for (TradeObject ho:tradeObjects) {
				if (ho.getType() != TradeObjectType.ENCHANTMENT) {continue;}
				if (ho.matchesEnchantment(enchant)) {return ho;}
			}
		}
		return null;
	}
	
	
	public TradeObject getTradeObject(HItemStack stack) {
		return getTradeObject(stack, null);
	}
	public TradeObject getTradeObject(HItemStack stack, Shop s) {
		if (stack == null) {return null;}
		if (s != null && s instanceof PlayerShop) {
			for (TradeObject ho:tradeObjects) {
				if (ho.getType() != TradeObjectType.ITEM) {continue;}
				if (!ho.matchesItemStack(stack)) {continue;}
				return (TradeObject) ((PlayerShop) s).getPlayerShopObject(ho);
			}
		} else {
			for (TradeObject ho:tradeObjects) {
				if (ho.getType() != TradeObjectType.ITEM) {continue;}
				if (ho.matchesItemStack(stack)) {return ho;}
			}
		}
		return null;
	}
	public TradeObject getTradeObject(String name, Shop s) {
		if (name == null) {return null;}
		String sname = name.toLowerCase();
		if (s != null && s instanceof PlayerShop) {
			if (tradeObjectsNameMap.containsKey(sname)) {
				return (TradeObject) ((PlayerShop) s).getPlayerShopObject(tradeObjectsNameMap.get(sname));
			} else {
				return null;
			}
		} else {
			if (tradeObjectsNameMap.containsKey(sname)) {
				return tradeObjectsNameMap.get(sname);
			} else {
				return null;
			}
		}
	}
	public TradeObject getTradeObject(String name) {
		return getTradeObject(name, null);
	}
	
	public ArrayList<TradeObject> getCategory(String category) {
		ArrayList<TradeObject> objs = new ArrayList<TradeObject>();
		for (TradeObject t:tradeObjects) {
			if (t.inCategory(category)) objs.add(t);
		}
		return objs;
	}
	public ArrayList<TradeObject> getCategory(String category, Shop s) {
		ArrayList<TradeObject> tos = new ArrayList<TradeObject>();
		for (TradeObject to:getCategory(category)) {
			tos.add(getTradeObject(to.getName(), s));
		}
		return tos;
	}

	public ArrayList<TradeObject> getTradeObjects(Shop s) {
		ArrayList<TradeObject> hos = new ArrayList<TradeObject>();
		for (TradeObject ho:tradeObjects) {
			hos.add(getTradeObject(ho.getName(), s));
		}
		return hos;
	}
	
	
	public ArrayList<TradeObject> getTradeObjects() {
		return new ArrayList<TradeObject>(tradeObjects);
	}



	public void clearData() {
		tradeObjectsNameMap.clear();
		tradeObjects.clear();
	}




	
	
	
	public ArrayList<String> getNames() {
		ArrayList<String> names = new ArrayList<String>();
		for (TradeObject ho:tradeObjects) {
			names.add(ho.getName());
		}
		return names;
	}
	

	
	public String getEnchantNameWithoutLevel(String bukkitName) {
		for (TradeObject ho:tradeObjectsNameMap.values()) {
			if (ho.getType() == TradeObjectType.ENCHANTMENT) {
				if (ho.getEnchantmentName().equalsIgnoreCase(bukkitName)) {
					String name = ho.getName();
					return name.substring(0, name.length() - 1);
				}
			}
		}
		return null;
	}
	
	public boolean objectTest(String name) {
		if (!tradeObjectsNameMap.containsKey(name.toLowerCase())) return false;
		return true;
	}
	public boolean itemTest(String name) {
		if (!objectTest(name)) return false;
		if (tradeObjectsNameMap.get(name.toLowerCase()).getType() != TradeObjectType.ITEM) return false;
		return true;
	}
	public boolean enchantTest(String name) {
		if (!objectTest(name)) return false;
		if (tradeObjectsNameMap.get(name.toLowerCase()).getType() != TradeObjectType.ENCHANTMENT) return false;
		return true;
	}
	


	
	
	public ArrayList<String> loadNewItems() {
		ArrayList<String> objectsAdded = new ArrayList<String>();
		String defaultObjectsPath = hc.getFolderPath() + File.separator + "defaultObjects.csv";
		FileTools ft = hc.getFileTools();
		if (!ft.fileExists(defaultObjectsPath)) {
			ft.copyFileFromJar("defaultObjects.csv", defaultObjectsPath);
		}
		SQLWrite sw = hc.getSQLWrite();
		QueryResult data = hc.getFileTools().readCSV(defaultObjectsPath);
		ArrayList<String> columns = data.getColumnNames();
		while (data.next()) {
			String objectName = data.getString("NAME");
			if (tradeObjectsNameMap.keySet().contains(objectName.toLowerCase())) {continue;}
			objectsAdded.add(objectName);
			HashMap<String, String> values = new HashMap<String, String>();
			for (String column : columns) {
				values.put(column, data.getString(column));
			}
			sw.performInsert("hyperconomy_objects", values);
		}
		ft.deleteFile(defaultObjectsPath);
		hc.restart();
		return objectsAdded;
	}
	
	
	public void updateNamesFromYml() {
		String defaultObjectsPath = hc.getFolderPath() + File.separator + "defaultObjects.csv";
		FileTools ft = hc.getFileTools();
		if (!ft.fileExists(defaultObjectsPath)) {
			ft.copyFileFromJar("defaultObjects.csv", defaultObjectsPath);
		}
		QueryResult data = hc.getFileTools().readCSV(defaultObjectsPath);
		while (data.next()) {
			String objectName = data.getString("NAME");
			String aliasString = data.getString("ALIASES");
			ArrayList<String> names = CommonFunctions.explode(aliasString);
			String displayName = data.getString("DISPLAY_NAME");
			names.add(displayName);
			names.add(objectName);
			for (String cname:names) {
				TradeObject ho = getTradeObject(cname);
				if (ho == null) {continue;}
				ho.setAliases(CommonFunctions.explode(aliasString));
				ho.setDisplayName(displayName);
				ho.setName(objectName);
			}
		}
		for (Shop s:hc.getHyperShopManager().getShops()) {
			if (s instanceof PlayerShop) {
				PlayerShop ps = (PlayerShop)s;
				for (TradeObject ho:ps.getShopObjects()) {
					ho.setParentTradeObject(ho.getParentTradeObject());
				}
			}
		}
		ft.deleteFile(defaultObjectsPath);
	}

	public String getXpName() {
		return xpName;
	}


}
