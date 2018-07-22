package regalowl.hyperconomy.shop;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

import regalowl.simpledatalib.sql.QueryResult;
import regalowl.hyperconomy.HyperConomy;
import regalowl.hyperconomy.account.HyperAccount;
import regalowl.hyperconomy.account.HyperPlayer;
import regalowl.hyperconomy.event.DataLoadEvent;
import regalowl.hyperconomy.event.DataLoadEvent.DataLoadType;
import regalowl.hyperconomy.event.HyperEvent;
import regalowl.hyperconomy.event.HyperEventListener;
import regalowl.hyperconomy.minecraft.HLocation;
import regalowl.simpledatalib.file.FileConfiguration;

public class HyperShopManager implements HyperEventListener {
	
	private transient FileConfiguration config;
	private transient HyperConomy hc;

	private transient long shopCheckTaskId;
	
	private ConcurrentHashMap<String, Shop> shops = new ConcurrentHashMap<String, Shop>();
	private boolean useShops;
	private long shopinterval;
	
	public HyperShopManager(HyperConomy hc) {
		this.hc = hc;
		config = hc.getConf();
		useShops = config.getBoolean("enable-feature.shops");
		shopinterval = config.getLong("intervals.shop-check");
		hc.getHyperEventHandler().registerListener(this);
	}
	
	@Override
	public void handleHyperEvent(HyperEvent event) {
		if (event instanceof DataLoadEvent) {
			DataLoadEvent devent = (DataLoadEvent) event;
			if (!(devent.loadType == DataLoadType.DEFAULT_ACCOUNT)) return;
			new Thread(new Runnable() {
				@Override
				public void run() {
					loadData();
				}
			}).start();
		}
		
	}
	

	
	private void loadData() {
		shops.clear();
		QueryResult shopData = hc.getSQLRead().select("SELECT * FROM hyperconomy_shops");
		while (shopData.next()) {
			String type = shopData.getString("TYPE");
			HyperAccount owner = hc.getDataManager().getAccount(shopData.getString("OWNER"));
			if (owner == null) continue;
			if (type.equalsIgnoreCase("server")) {
				if (!useShops) {continue;}
				String name = shopData.getString("NAME");
				HLocation p1 = new HLocation(shopData.getString("WORLD"), shopData.getInt("P1X"), shopData.getInt("P1Y"), shopData.getInt("P1Z"));
				HLocation p2 = new HLocation(shopData.getString("WORLD"), shopData.getInt("P2X"), shopData.getInt("P2Y"), shopData.getInt("P2Z"));
				Shop shop = new ServerShop(hc, name, shopData.getString("ECONOMY"), owner, shopData.getString("MESSAGE"), p1, p2, shopData.getString("BANNED_OBJECTS"));
				shops.put(name, shop);
			} else if (type.equalsIgnoreCase("player")) {
				if (!useShops) {continue;}
				if (!config.getBoolean("enable-feature.player-shops")) {continue;}
				String name = shopData.getString("NAME");
				HLocation p1 = new HLocation(shopData.getString("WORLD"), shopData.getInt("P1X"), shopData.getInt("P1Y"), shopData.getInt("P1Z"));
				HLocation p2 = new HLocation(shopData.getString("WORLD"), shopData.getInt("P2X"), shopData.getInt("P2Y"), shopData.getInt("P2Z"));
				Shop shop = new PlayerShop(hc, name, shopData.getString("ECONOMY"), owner, shopData.getString("MESSAGE"), p1, p2, shopData.getString("BANNED_OBJECTS"), shopData.getString("ALLOWED_PLAYERS"), (shopData.getString("USE_ECONOMY_STOCK").equalsIgnoreCase("1")) ? true : false);
				shops.put(name, shop);
			} else if (type.equalsIgnoreCase("global")) {
				if (useShops) {continue;}
				Shop shop = new GlobalShop(hc, "GlobalShop", "default", hc.getHyperPlayerManager().getDefaultServerShopAccount(), shopData.getString("BANNED_OBJECTS"));
				shops.put("GlobalShop", shop);
			}
		}
		shopData.close();
		if (!useShops && shops.size() == 0) {
			Shop shop = new GlobalShop(hc, "GlobalShop", "default", hc.getHyperPlayerManager().getDefaultServerShopAccount());
			shops.put("GlobalShop", shop);
		}
		hc.getHyperEventHandler().fireEventFromAsyncThread(new DataLoadEvent(DataLoadType.SHOP));
		hc.getDebugMode().ayncDebugConsoleMessage("Shops loaded.");
		stopShopCheck();
		startShopCheck();
	}
	
	
	
	
	public Shop getShop(HyperPlayer hp) {
		if (hp == null) return null;
		for (Shop shop : shops.values()) {
			if (shop.inShop(hp)) return shop;
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

	public boolean inAnyShop(HyperPlayer hp) {
		for (Shop shop : shops.values()) {
			if (shop.inShop(hp)) {
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
		shopCheckTaskId = hc.getMC().runRepeatingTask(new Runnable() {
		    @Override
			public void run() {
				for (Shop shop:shops.values()) {
					shop.updatePlayerStatus();
				}
		    }
		}, shopinterval, shopinterval);
    }
    public void stopShopCheck() {
    	hc.getMC().cancelTask(shopCheckTaskId);
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
