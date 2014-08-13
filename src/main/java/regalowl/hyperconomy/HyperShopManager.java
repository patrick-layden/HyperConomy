package regalowl.hyperconomy;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import regalowl.databukkit.sql.QueryResult;
import regalowl.hyperconomy.account.HyperPlayer;
import regalowl.hyperconomy.shop.GlobalShop;
import regalowl.hyperconomy.shop.PlayerShop;
import regalowl.hyperconomy.shop.ServerShop;
import regalowl.hyperconomy.shop.Shop;
import regalowl.hyperconomy.util.HyperConfig;
import regalowl.hyperconomy.util.SimpleLocation;

public class HyperShopManager {
	
	private ConcurrentHashMap<String, Shop> shops = new ConcurrentHashMap<String, Shop>();
	private boolean useShops;
	private long shopinterval;
	private HyperConfig config;
	private HyperConomy hc;
	private BukkitTask shopCheckTask;
	
	public HyperShopManager() {
		hc = HyperConomy.hc;
		config = hc.getConf();
		useShops = config.getBoolean("enable-feature.shops");
		shopinterval = config.getLong("intervals.shop-check");
	}
	
	public void loadData() {
		shops.clear();
		QueryResult shopData = hc.getSQLRead().select("SELECT * FROM hyperconomy_shops");
		while (shopData.next()) {
			String type = shopData.getString("TYPE");
			if (type.equalsIgnoreCase("server")) {
				if (!useShops) {continue;}
				String name = shopData.getString("NAME");
				SimpleLocation p1 = new SimpleLocation(shopData.getString("WORLD"), shopData.getInt("P1X"), shopData.getInt("P1Y"), shopData.getInt("P1Z"));
				SimpleLocation p2 = new SimpleLocation(shopData.getString("WORLD"), shopData.getInt("P2X"), shopData.getInt("P2Y"), shopData.getInt("P2Z"));
				Shop shop = new ServerShop(name, shopData.getString("ECONOMY"), hc.getHyperPlayerManager().getAccount(shopData.getString("OWNER")), shopData.getString("MESSAGE"), p1, p2, shopData.getString("BANNED_OBJECTS"));
				shops.put(name, shop);
			} else if (type.equalsIgnoreCase("player")) {
				if (!useShops) {continue;}
				if (!config.getBoolean("enable-feature.player-shops")) {continue;}
				String name = shopData.getString("NAME");
				SimpleLocation p1 = new SimpleLocation(shopData.getString("WORLD"), shopData.getInt("P1X"), shopData.getInt("P1Y"), shopData.getInt("P1Z"));
				SimpleLocation p2 = new SimpleLocation(shopData.getString("WORLD"), shopData.getInt("P2X"), shopData.getInt("P2Y"), shopData.getInt("P2Z"));
				Shop shop = new PlayerShop(name, shopData.getString("ECONOMY"), hc.getHyperPlayerManager().getAccount(shopData.getString("OWNER")), shopData.getString("MESSAGE"), p1, p2, shopData.getString("BANNED_OBJECTS"), shopData.getString("ALLOWED_PLAYERS"), (shopData.getString("USE_ECONOMY_STOCK").equalsIgnoreCase("1")) ? true : false);
				shops.put(name, shop);
			} else if (type.equalsIgnoreCase("global")) {
				if (useShops) {continue;}
				Shop shop = new GlobalShop("GlobalShop", "default", hc.getHyperPlayerManager().getDefaultServerShopAccount(), shopData.getString("BANNED_OBJECTS"));
				shops.put("GlobalShop", shop);
			}
		}
		shopData.close();
		if (!useShops && shops.size() == 0) {
			Shop shop = new GlobalShop("GlobalShop", "default", hc.getHyperPlayerManager().getDefaultServerShopAccount());
			shops.put("GlobalShop", shop);
		}
		hc.getDebugMode().ayncDebugConsoleMessage("Shops loaded.");
		stopShopCheck();
		startShopCheck();
	}
	
	
	
	
	
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
