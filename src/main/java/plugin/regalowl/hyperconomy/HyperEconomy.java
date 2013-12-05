package regalowl.hyperconomy;


import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;

import regalowl.databukkit.CommonFunctions;
import regalowl.databukkit.QueryResult;
import regalowl.databukkit.SQLRead;
import regalowl.databukkit.SQLWrite;



public class HyperEconomy {

	private ConcurrentHashMap<String, HyperObject> hyperObjectsName = new ConcurrentHashMap<String, HyperObject>();
	private ConcurrentHashMap<String, HyperObject> hyperObjectsData = new ConcurrentHashMap<String, HyperObject>();
	private ConcurrentHashMap<String, String> hyperObjectsAliases = new ConcurrentHashMap<String, String>();
	

	private ArrayList<String> compositeKeys = new ArrayList<String>();
	private boolean useComposites;
	private HyperConomy hc;
	private SQLRead sr;
	private String economy;
	private boolean dataLoaded;
	
	private String xpName = null;
	

	HyperEconomy(String economy) {
		dataLoaded = false;
		hc = HyperConomy.hc;	
		this.economy = economy;
		sr = hc.getSQLRead();
		useComposites = hc.gYH().gFC("config").getBoolean("config.use-composite-items");
		loadCompositeKeys();
		load();
	}


	public boolean dataLoaded() {
		return dataLoaded;
	}

	private void load() {
		hc.getServer().getScheduler().runTaskAsynchronously(hc, new Runnable() {
			public void run() {
				hyperObjectsName.clear();
				hyperObjectsData.clear();
				QueryResult result = sr.aSyncSelect("SELECT * FROM hyperconomy_objects WHERE ECONOMY = '"+economy+"'");
				while (result.next()) {
					if (useComposites && compositeKeys.contains(result.getString("NAME").toLowerCase())) {continue;}
					HyperObjectType type = HyperObjectType.fromString(result.getString("TYPE"));
					if (type == HyperObjectType.ITEM) {
						HyperItem hobj = new ComponentItem(result.getString("NAME"), result.getString("ECONOMY"), 
								result.getString("DISPLAY_NAME"), result.getString("ALIASES"), result.getString("TYPE"), 
								result.getString("MATERIAL"), result.getInt("DATA"),
								result.getInt("DURABILITY"), result.getDouble("VALUE"), result.getString("STATIC"), result.getDouble("STATICPRICE"),
								result.getDouble("STOCK"), result.getDouble("MEDIAN"), result.getString("INITIATION"), result.getDouble("STARTPRICE"), 
								result.getDouble("CEILING"),result.getDouble("FLOOR"), result.getDouble("MAXSTOCK"));
						hyperObjectsName.put(hobj.getName().toLowerCase(), hobj);
						hyperObjectsData.put(hobj.getMaterialEnum() + "|" + hobj.getData(), hobj);
						for (String alias:hobj.getAliases()) {
							hyperObjectsAliases.put(alias.toLowerCase(), hobj.getName().toLowerCase());
						}
						hyperObjectsAliases.put(hobj.getName().toLowerCase(), hobj.getName().toLowerCase());
						hyperObjectsAliases.put(hobj.getDisplayName().toLowerCase(), hobj.getName().toLowerCase());
					} else if (type == HyperObjectType.ENCHANTMENT) {
						HyperObject hobj = new Enchant(result.getString("NAME"), result.getString("ECONOMY"), 
								result.getString("DISPLAY_NAME"), result.getString("ALIASES"), result.getString("TYPE"), 
								result.getString("MATERIAL"), result.getDouble("VALUE"), result.getString("STATIC"), result.getDouble("STATICPRICE"),
								result.getDouble("STOCK"), result.getDouble("MEDIAN"), result.getString("INITIATION"), result.getDouble("STARTPRICE"), 
								result.getDouble("CEILING"),result.getDouble("FLOOR"), result.getDouble("MAXSTOCK"));
						hyperObjectsName.put(hobj.getName().toLowerCase(), hobj);
						for (String alias:hobj.getAliases()) {
							hyperObjectsAliases.put(alias.toLowerCase(), hobj.getName().toLowerCase());
						}
						hyperObjectsAliases.put(hobj.getName().toLowerCase(), hobj.getName().toLowerCase());
						hyperObjectsAliases.put(hobj.getDisplayName().toLowerCase(), hobj.getName().toLowerCase());
					} else if (type == HyperObjectType.EXPERIENCE) {
						HyperObject hobj = new Xp(result.getString("NAME"), result.getString("ECONOMY"), 
								result.getString("DISPLAY_NAME"), result.getString("ALIASES"), result.getString("TYPE"), 
								result.getDouble("VALUE"), result.getString("STATIC"), result.getDouble("STATICPRICE"),
								result.getDouble("STOCK"), result.getDouble("MEDIAN"), result.getString("INITIATION"), result.getDouble("STARTPRICE"), 
								result.getDouble("CEILING"),result.getDouble("FLOOR"), result.getDouble("MAXSTOCK"));
						hyperObjectsName.put(hobj.getName().toLowerCase(), hobj);
						xpName = result.getString("NAME");
						for (String alias:hobj.getAliases()) {
							hyperObjectsAliases.put(alias.toLowerCase(), hobj.getName().toLowerCase());
						}
						hyperObjectsAliases.put(hobj.getName().toLowerCase(), hobj.getName().toLowerCase());
						hyperObjectsAliases.put(hobj.getDisplayName().toLowerCase(), hobj.getName().toLowerCase());
					}
				}
				result.close();
				dataLoaded = true;
				if (xpName == null) {xpName = "xp";}
				hc.getServer().getScheduler().runTask(hc, new Runnable() {
					public void run() {
						loadComposites();
					}
				});
			}
		});
	}
	
	private void loadCompositeKeys() {
		if (!useComposites) {
			return;
		}
		compositeKeys.clear();
		FileConfiguration composites = hc.gYH().gFC("composites");
		Iterator<String> it = composites.getKeys(false).iterator();
		while (it.hasNext()) {
			compositeKeys.add(it.next().toString().toLowerCase());
		}
	}
	
	private void loadComposites() {
		if (!useComposites) {
			return;
		}
		boolean loaded = false;
		FileConfiguration composites = hc.gYH().gFC("composites");
		int counter = 0;
		while (!loaded) {
			counter++;
			if (counter > 100) {
				hc.getDataBukkit().writeError("Infinite loop when loading composites.yml.  You likely have an error in your composites.yml file.  Your items will not work properly until this is fixed.");
				return;
			}
			loaded = true;
			Iterator<String> it = composites.getKeys(false).iterator();
			while (it.hasNext()) {
				String name = it.next().toString();
				//hc.getLogger().severe(name);
				if (!componentsLoaded(name)) {
					loaded = false;
					continue;
				}
				HyperItem ho = new CompositeItem(name, economy);
				hyperObjectsName.put(ho.getName().toLowerCase(), ho);
				hyperObjectsData.put(ho.getMaterialEnum() + "|" + ho.getData(), ho);
				for (String alias:ho.getAliases()) {
					hyperObjectsAliases.put(alias.toLowerCase(), ho.getName().toLowerCase());
				}
				hyperObjectsAliases.put(ho.getName().toLowerCase(), ho.getName().toLowerCase());
				hyperObjectsAliases.put(ho.getDisplayName().toLowerCase(), ho.getName().toLowerCase());
			}
		}
	}
	private boolean componentsLoaded(String name) {
		CommonFunctions cf = hc.gCF();
		HashMap<String,String> tempComponents = cf.explodeMap(hc.gYH().gFC("composites").getString(name + ".components"));
		for (Map.Entry<String,String> entry : tempComponents.entrySet()) {
		    String oname = entry.getKey();
		    HyperObject ho = getHyperObject(oname);
		    if (ho == null) {
		    	return false;
		    }
		}
		return true;
	}
	

	
	
	public String getEconomy() {
		return economy;
	}
	


	public HyperObject getHyperObject(ItemStack stack) {
		return getHyperObject(stack, null);
	}
	public HyperObject getHyperObject(ItemStack stack, Shop s) {
		HyperItemStack his = new HyperItemStack(stack);
		if (s != null && s instanceof PlayerShop) {
			if (hyperObjectsData.containsKey(his.getKey())) {
				HyperObject ho = hyperObjectsData.get(his.getKey());
				return (HyperObject) ((PlayerShop) s).getPlayerShopObject(ho);
			}
		} else {
			if (hyperObjectsData.containsKey(his.getKey())) {
				return hyperObjectsData.get(his.getKey());
			}
		}
		return null;
	}
	public HyperObject getHyperObject(String name, Shop s) {
		String sname = name.toLowerCase();
		if (hyperObjectsAliases.containsKey(sname)) {
			sname = hyperObjectsAliases.get(sname);
		}
		if (s != null && s instanceof PlayerShop) {
			if (hyperObjectsName.containsKey(sname)) {
				return (HyperObject) ((PlayerShop) s).getPlayerShopObject(hyperObjectsName.get(sname));
			} else {
				return null;
			}
		} else {
			if (hyperObjectsName.containsKey(sname)) {
				return hyperObjectsName.get(sname);
			} else {
				return null;
			}
		}
	}
	public HyperObject getHyperObject(String name) {
		return getHyperObject(name, null);
	}
	
	public void removeHyperObject(String name) {
		HyperObject ho = null;
		if (hyperObjectsName.containsKey(name)) {
			ho = hyperObjectsName.get(name);
			hyperObjectsName.remove(name);
		}
		if (ho instanceof HyperItem) {
			HyperItem hi = (HyperItem)ho;
			if (hyperObjectsData.containsKey(hi.getMaterialEnum() + "|" + hi.getData())) {
				hyperObjectsData.remove(hi.getMaterialEnum() + "|" + hi.getData());
			}
		}
	}
	
	public HyperItem getHyperItem(String name) {
		HyperObject ho = getHyperObject(name);
		if (ho != null && ho instanceof HyperItem) {
			return (HyperItem)ho;
		}
		return null;
	}
	public HyperItem getHyperItem(ItemStack stack) {
		HyperObject ho = getHyperObject(stack);
		if (ho != null && ho instanceof HyperItem) {
			return (HyperItem)ho;
		}
		return null;
	}
	public HyperItem getHyperItem(ItemStack stack, Shop s) {
		HyperObject ho = getHyperObject(stack, s);
		if (ho != null && ho instanceof HyperItem) {
			return (HyperItem)ho;
		}
		return null;
	}
	public HyperEnchant getHyperEnchant(String name) {
		HyperObject ho = getHyperObject(name);
		if (ho != null && ho instanceof HyperEnchant) {
			return (HyperEnchant)ho;
		}
		return null;
	}
	public BasicObject getBasicObject(String name) {
		HyperObject ho = getHyperObject(name);
		if (ho != null && ho instanceof BasicObject) {
			return (BasicObject)ho;
		}
		return null;
	}
	public HyperItem getHyperItem(String name, Shop s) {
		HyperObject ho = getHyperObject(name, s);
		if (ho != null && ho instanceof HyperItem) {
			return (HyperItem)ho;
		}
		return null;
	}
	public HyperEnchant getHyperEnchant(String name, Shop s) {
		HyperObject ho = getHyperObject(name, s);
		if (ho != null && ho instanceof HyperEnchant) {
			return (HyperEnchant)ho;
		}
		return null;
	}
	public BasicObject getBasicObject(String name, Shop s) {
		HyperObject ho = getHyperObject(name, s);
		if (ho != null && ho instanceof BasicObject) {
			return (BasicObject)ho;
		}
		return null;
	}
	public HyperXP getHyperXP() {
		HyperObject ho = getHyperObject(xpName);
		if (ho != null && ho instanceof HyperXP) {
			return (HyperXP)ho;
		}
		return null;
	}
	public HyperXP getHyperXP(Shop s) {
		HyperObject ho = getHyperObject(xpName, s);
		if (ho != null && ho instanceof HyperXP) {
			return (HyperXP)ho;
		}
		return null;
	}
	
	
	public ArrayList<HyperObject> getHyperObjects(Shop s) {
		ArrayList<HyperObject> hos = new ArrayList<HyperObject>();
		for (HyperObject ho:hyperObjectsName.values()) {
			hos.add(getHyperObject(ho.getName(), s));
		}
		return hos;
	}
	
	
	public ArrayList<HyperObject> getHyperObjects() {
		ArrayList<HyperObject> hos = new ArrayList<HyperObject>();
		for (HyperObject ho:hyperObjectsName.values()) {
			hos.add(ho);
		}
		return hos;
	}




	public ArrayList<String> getObjectKeys() {
		ArrayList<String> keys = new ArrayList<String>();
		for (String key:hyperObjectsName.keySet()) {
			keys.add(key);
		}
		return keys;
	}



	



	public void clearData() {
		hyperObjectsName.clear();
		hyperObjectsData.clear();
	}




	
	
	
	public ArrayList<String> getNames() {
		ArrayList<String> names = new ArrayList<String>();
		for (HyperObject ho:hyperObjectsName.values()) {
			names.add(ho.getName());
		}
		return names;
	}
	
/*
	public ArrayList<String> getItemNames() {
		ArrayList<String> names = new ArrayList<String>();
		for (HyperObject ho:hyperObjectsName.values()) {
			if (ho.getType() == HyperObjectType.ITEM || ho.getType() == HyperObjectType.EXPERIENCE) {
				names.add(ho.getName());
			}
		}
		return names;
	}

	public ArrayList<String> getEnchantNames() {
		ArrayList<String> names = new ArrayList<String>();
		for (HyperObject ho:hyperObjectsName.values()) {
			if (ho.getType() == HyperObjectType.ENCHANTMENT) {
				names.add(ho.getName());
			}
		}
		return names;
	}
	*/
	
	public String getEnchantNameWithoutLevel(String bukkitName) {
		for (HyperObject ho:hyperObjectsName.values()) {
			if (ho instanceof HyperEnchant) {
				HyperEnchant he = (HyperEnchant)ho;
				if (ho.getType() == HyperObjectType.ENCHANTMENT && he.getEnchantmentName().equalsIgnoreCase(bukkitName)) {
					String name = ho.getName();
					return name.substring(0, name.length() - 1);
				}
			}
		}
		return null;
	}
	
	public boolean objectTest(String name) {
		String sname = name.toLowerCase();
		if (hyperObjectsAliases.containsKey(name)) {
			sname = hyperObjectsAliases.get(name);
		}
		if (hyperObjectsName.containsKey(sname)) {
			return true;
		}
		return false;
	}
	
	
	public boolean itemTest(String name) {
		String sname = name.toLowerCase();
		if (hyperObjectsAliases.containsKey(name)) {
			sname = hyperObjectsAliases.get(name);
		}
		if (hyperObjectsName.containsKey(sname)) {
			HyperObject ho = hyperObjectsName.get(sname);
			if (ho instanceof HyperItem) {
				return true;
			}
		}
		return false;
	}
	

	public boolean enchantTest(String name) {
		String sname = name.toLowerCase();
		if (hyperObjectsAliases.containsKey(name)) {
			sname = hyperObjectsAliases.get(name);
		}
		if (hyperObjectsName.containsKey(sname)) {
			HyperObject ho = hyperObjectsName.get(sname);
			if (ho instanceof HyperEnchant) {
				return true;
			}
		}
		return false;
	}
	
	
	public String fixName(String nam) {
		String sname = nam.toLowerCase();
		if (hyperObjectsAliases.containsKey(nam)) {
			sname = hyperObjectsAliases.get(nam);
		}
		for (String name:getNames()) {
			if (name.equalsIgnoreCase(sname)) {
				return name;
			}
		}
		return nam;
	}
	
	public String fixNameTest(String nam) {
		String sname = nam.toLowerCase();
		if (hyperObjectsAliases.containsKey(nam)) {
			sname = hyperObjectsAliases.get(nam);
		}
		ArrayList<String> names = getNames();
		for (int i = 0; i < names.size(); i++) {
			if (names.get(i).equalsIgnoreCase(sname)) {
				return names.get(i);
			}
		}
		return null;
	}
	


	
	
	public ArrayList<String> loadNewItems() {
		FileConfiguration objects = hc.gYH().gFC("objects");
		ArrayList<String> objectsAdded = new ArrayList<String>();
		SQLWrite sw = hc.getSQLWrite();
		Iterator<String> it = objects.getKeys(false).iterator();
		ArrayList<String> keys = getObjectKeys();
		while (it.hasNext()) {
			String itemname = it.next().toString();
			if (!keys.contains(itemname)) {
				objectsAdded.add(itemname);
				String category = objects.getString(itemname + ".information.category");
				if (category == null) {
					category = "unknown";
				}
				HashMap<String, String> values = new HashMap<String, String>();
				values.put("NAME", itemname);
				values.put("ECONOMY", economy);
				values.put("DISPLAY_NAME", objects.getString(itemname + ".name.display"));
				values.put("ALIASES", objects.getString(itemname + ".name.aliases"));
				values.put("TYPE", objects.getString(itemname + ".information.type"));
				values.put("VALUE", objects.getDouble(itemname + ".value") + "");
				values.put("STATIC", objects.getString(itemname + ".price.static"));
				values.put("STATICPRICE", objects.getDouble(itemname + ".price.staticprice") + "");
				values.put("STOCK", objects.getDouble(itemname + ".stock.stock") + "");
				values.put("MEDIAN", objects.getDouble(itemname + ".stock.median") + "");
				values.put("INITIATION", objects.getString(itemname + ".initiation.initiation"));
				values.put("STARTPRICE", objects.getDouble(itemname + ".initiation.startprice") + "");
				values.put("CEILING", objects.getDouble(itemname + ".price.ceiling") + "");
				values.put("FLOOR", objects.getDouble(itemname + ".price.floor") + "");
				values.put("MAXSTOCK", objects.getDouble(itemname + ".stock.maxstock") + "");
				if (objects.getString(itemname + ".information.type").equalsIgnoreCase("item")) {
					values.put("MATERIAL", objects.getString(itemname + ".information.material"));
					values.put("DATA", objects.getInt(itemname + ".information.data") + "");
					values.put("DURABILITY", objects.getInt(itemname + ".information.data") + "");
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
		}
		hc.restart();
		return objectsAdded;
	}
	
	
	public void exportToYml() {
		FileConfiguration objects = hc.gYH().gFC("objects");
		ArrayList<String> names = getNames();
		Collections.sort(names, String.CASE_INSENSITIVE_ORDER);
		for (int i = 0; i < names.size(); i++) {
			String name = names.get(i);
			objects.set(name, null);
			HyperObject ho = getHyperObject(name);
			String displayName = ho.getDisplayName();
			String aliases = ho.getAliasesString();
			String newtype = ho.getType().toString();
			String newmaterial = "none";
			int newdata = -1;
			if (ho instanceof HyperItem) {
				HyperItem hi = (HyperItem)ho;
				newmaterial = hi.getMaterial();
				newdata = hi.getData();
			} else if (ho instanceof HyperEnchant) {
				HyperEnchant he = (HyperEnchant)ho;
				newmaterial = he.getEnchantmentName();
			}
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
			objects.set(name + ".name.display", displayName);
			objects.set(name + ".name.aliases", aliases);
			objects.set(name + ".information.type", newtype);
			objects.set(name + ".information.material", newmaterial);
			objects.set(name + ".information.data", newdata);
			objects.set(name + ".value", newvalue);
			objects.set(name + ".price.static", Boolean.parseBoolean(newstatic));
			objects.set(name + ".price.staticprice", newstaticprice);
			objects.set(name + ".stock.stock", newstock);
			objects.set(name + ".stock.median", newmedian);
			objects.set(name + ".initiation.initiation", Boolean.parseBoolean(newinitiation));
			objects.set(name + ".initiation.startprice", newstartprice);
			objects.set(name + ".price.ceiling", newceiling);
			objects.set(name + ".price.floor", newfloor);
			objects.set(name + ".stock.maxstock", newmaxstock);
		}
		hc.gYH().saveYamls();
	}

	public String getXpName() {
		return xpName;
	}
}
