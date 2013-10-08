package regalowl.hyperconomy;


import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.bukkit.configuration.file.FileConfiguration;


import regalowl.databukkit.QueryResult;
import regalowl.databukkit.SQLRead;
import regalowl.databukkit.SQLWrite;



public class HyperEconomy {

	private ConcurrentHashMap<String, HyperObject> hyperObjects = new ConcurrentHashMap<String, HyperObject>();


	private ArrayList<String> compositeKeys = new ArrayList<String>();
	private boolean useComposites;
	private HyperConomy hc;
	private SQLRead sr;
	private String economy;
	private boolean dataLoaded;
	

	HyperEconomy(String economy) {
		dataLoaded = false;
		hc = HyperConomy.hc;	
		this.economy = economy;
		sr = hc.getSQLRead();
		loadCompositeKeys();
		useComposites = hc.gYH().gFC("config").getBoolean("config.use-composite-items");
		load();
	}


	public boolean dataLoaded() {
		return dataLoaded;
	}

	private void load() {
		hc.getServer().getScheduler().runTaskAsynchronously(hc, new Runnable() {
			public void run() {
				hyperObjects.clear();
				QueryResult result = sr.aSyncSelect("SELECT * FROM hyperconomy_objects WHERE ECONOMY = '"+economy+"'");
				while (result.next()) {
					if (useComposites && compositeKeys.contains(result.getString("NAME"))) {continue;}
					HyperObject hobj = new ComponentObject(result.getString("NAME"), result.getString("ECONOMY"), result.getString("TYPE"), 
							result.getString("CATEGORY"), result.getString("MATERIAL"), result.getInt("ID"), result.getInt("DATA"),
							result.getInt("DURABILITY"), result.getDouble("VALUE"), result.getString("STATIC"), result.getDouble("STATICPRICE"),
							result.getDouble("STOCK"), result.getDouble("MEDIAN"), result.getString("INITIATION"), result.getDouble("STARTPRICE"), 
							result.getDouble("CEILING"),result.getDouble("FLOOR"), result.getDouble("MAXSTOCK"));
					hyperObjects.put(hobj.getName(), hobj);
				}
				result.close();
				dataLoaded = true;
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
			compositeKeys.add(it.next().toString());
		}
	}
	
	
	/* Code to display recipes from bukkit.  Doesn't include potions.
		Iterator<Recipe> iter = hc.getServer().recipeIterator();
		while (iter.hasNext()) {
		  Recipe recipe = iter.next();
		  if (recipe instanceof ShapedRecipe) {
			  ShapedRecipe sr = (ShapedRecipe)recipe;
			  ItemStack result = sr.getResult();
			  hc.getLogger().severe("Result: [" + result.getType().toString() + "," + result.getAmount() + "]");

			  for (Map.Entry<Character,ItemStack> entry : sr.getIngredientMap().entrySet()) {
				  Character ch = entry.getKey();
				  ItemStack stack = entry.getValue();
				  hc.getLogger().severe("Char: [" + ch + "] Stack: [" + stack.getType().toString() + "," + stack.getAmount() + "]");
			  }
		  } else if (recipe instanceof ShapelessRecipe) {
			  ShapelessRecipe sr = (ShapelessRecipe)recipe;
			  ItemStack result = sr.getResult();
			  hc.getLogger().severe("Result: [" + result.getType().toString() + "," + result.getAmount() + "]");
			  for (ItemStack stack:sr.getIngredientList()) {
				  hc.getLogger().severe("Stack: [" + stack.getType().toString() + "," + stack.getAmount() + "]");
			  }
		  }
		}
	 */
	
	private void loadComposites() {
		if (!useComposites) {
			return;
		}
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
	

	
	
	public String getEconomy() {
		return economy;
	}
	

	
	
	
	public HyperObject getHyperObject(int id, int data, Shop s) {
		if (s != null && s instanceof PlayerShop) {
			for (HyperObject ho:hyperObjects.values()) {
				if (ho.getId() == id && ho.getData() == data) {
					return ((PlayerShop) s).getPlayerShopObject(ho);
				}
			}
			return null;
		} else {
			for (HyperObject ho:hyperObjects.values()) {
				if (ho.getId() == id && ho.getData() == data) {
					return ho;
				}
			}
		}
		return null;
	}
	public HyperObject getHyperObject(String name, Shop s) {
		name = fixName(name);
		if (s != null && s instanceof PlayerShop) {
			if (hyperObjects.containsKey(name)) {
				return ((PlayerShop) s).getPlayerShopObject(hyperObjects.get(name));
			} else {
				return null;
			}
		} else {
			if (hyperObjects.containsKey(name)) {
				return hyperObjects.get(name);
			} else {
				return null;
			}
		}
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
	
	public ArrayList<HyperObject> getHyperObjects(Shop s) {
		ArrayList<HyperObject> hos = new ArrayList<HyperObject>();
		for (HyperObject ho:hyperObjects.values()) {
			hos.add(getHyperObject(ho.getName(), s));
		}
		return hos;
	}
	
	
	public ArrayList<HyperObject> getHyperObjects() {
		ArrayList<HyperObject> hos = new ArrayList<HyperObject>();
		for (HyperObject ho:hyperObjects.values()) {
			hos.add(ho);
		}
		return hos;
	}




	public ArrayList<String> getObjectKeys() {
		ArrayList<String> keys = new ArrayList<String>();
		for (String key:hyperObjects.keySet()) {
			keys.add(key);
		}
		return keys;
	}



	



	public void clearData() {
		hyperObjects.clear();
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
	


	
	
	public ArrayList<String> loadNewItems() {
		FileConfiguration itemsyaml = hc.gYH().gFC("items");
		FileConfiguration enchantsyaml = hc.gYH().gFC("enchants");
		ArrayList<String> statements = new ArrayList<String>();
		ArrayList<String> objectsAdded = new ArrayList<String>();
		Iterator<String> it = itemsyaml.getKeys(false).iterator();
		ArrayList<String> keys = getObjectKeys();
		while (it.hasNext()) {
			String itemname = it.next().toString();
			if (!keys.contains(itemname)) {
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
			if (!keys.contains(ename)) {
				objectsAdded.add(ename);
				statements.add("Insert Into hyperconomy_objects (NAME, ECONOMY, TYPE, CATEGORY, MATERIAL, ID, DATA, DURABILITY, VALUE, STATIC, STATICPRICE, STOCK, MEDIAN, INITIATION, STARTPRICE, CEILING, FLOOR)" + " Values ('" + ename + "','" + economy + "','" + "enchantment" + "','" + "unknown" + "','" + enchantsyaml.getString(ename + ".information.name") + "','" + enchantsyaml.getInt(ename + ".information.id") + "','" + "-2" + "','" + "-2" + "','" + enchantsyaml.getDouble(ename + ".value") + "','"
						+ enchantsyaml.getString(ename + ".price.static") + "','" + enchantsyaml.getDouble(ename + ".price.staticprice") + "','" + enchantsyaml.getDouble(ename + ".stock.stock") + "','" + enchantsyaml.getDouble(ename + ".stock.median") + "','" + enchantsyaml.getString(ename + ".initiation.initiation") + "','" + enchantsyaml.getDouble(ename + ".initiation.startprice") + "','" + enchantsyaml.getDouble(ename + ".price.ceiling") + "','" + enchantsyaml.getDouble(ename + ".price.floor")
						+ "','" + enchantsyaml.getDouble(ename + ".stock.maxstock") + "')");
			}
		}
		SQLWrite sw = hc.getSQLWrite();
		sw.executeSQL(statements);
		hc.restart();
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
