package regalowl.hyperconomy;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;



public class HyperEconomy {
	private ConcurrentHashMap<String, Shop> shops = new ConcurrentHashMap<String, Shop>();
	
	private HyperConomy hc;
	//private String economy;
	
	private long shopinterval;
	private int shoptaskid;
	private boolean useShops;
	

	HyperEconomy() {
		hc = HyperConomy.hc;	
		//this.economy = economy;
		useShops = hc.getYaml().getConfig().getBoolean("config.use-shops");
		shopinterval = hc.getYaml().getConfig().getLong("config.shopcheckinterval");
		buildShopData();
	}
	
	
	private void buildShopData() {
		clearAll();
		FileConfiguration sh = hc.getYaml().getShops();
		if (!useShops) {
			Shop shop = new Shop("GlobalShop", "default");
			shop.setGlobal();
			shops.put("GlobalShop", shop);
			return;
		}
		Iterator<String> it = hc.getYaml().getShops().getKeys(false).iterator();
		while (it.hasNext()) {   			
			Object element = it.next();
			String name = element.toString(); 
			if (!name.equalsIgnoreCase("GlobalShop")) {
				Shop shop = new Shop(name, sh.getString(name + ".economy"));
				shop.setPoint1(sh.getString(name + ".world"), sh.getInt(name + ".p1.x"), sh.getInt(name + ".p1.y"), sh.getInt(name + ".p1.z"));
				shop.setPoint2(sh.getString(name + ".world"), sh.getInt(name + ".p2.x"), sh.getInt(name + ".p2.y"), sh.getInt(name + ".p2.z"));
				shop.setMessage1(sh.getString(name + ".shopmessage1"));
				shop.setMessage2(sh.getString(name + ".shopmessage2"));
				shops.put(name, shop);
			}
		}
	}
	
	
	public void clearAll() {
    	shops.clear();
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
		return shops.containsKey(name);
	}
	public void addShop(Shop shop) {
		shops.put(shop.getName(), shop);
		hc.getWebHandler().addShop(shop);
	}
	public void removeShop(String name) {
		hc.getYaml().getShops().set(name, null);
		shops.remove(name);
	}
	public void renameShop(String name, String newName) {
		Shop shop = shops.get(name);
		shop.setName(newName);
		shops.put(newName, shop);
		shops.remove(name);
	}
    public void startShopCheck() {
		shoptaskid = hc.getServer().getScheduler().scheduleSyncRepeatingTask(hc, new Runnable() {
		    public void run() {
				for (Shop shop:shops.values()) {
					shop.updatePlayerStatus();
				}
		    }
		}, shopinterval, shopinterval);
    }
    public void stopShopCheck() {
    	hc.getServer().getScheduler().cancelTask(shoptaskid);
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

	
}
