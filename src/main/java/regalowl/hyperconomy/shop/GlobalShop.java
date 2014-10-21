package regalowl.hyperconomy.shop;

import java.util.ArrayList;
import java.util.HashMap;


import regalowl.hyperconomy.HyperConomy;
import regalowl.hyperconomy.HyperEconomy;
import regalowl.hyperconomy.account.HyperAccount;
import regalowl.hyperconomy.account.HyperPlayer;
import regalowl.hyperconomy.event.ShopModificationEvent;
import regalowl.hyperconomy.hyperobject.HyperObject;
import regalowl.hyperconomy.util.SimpleLocation;


public class GlobalShop implements Shop, Comparable<Shop>{
	
	private static final long serialVersionUID = -4886663609354167778L;
	private String name;
	private String economy;
	private String owner;
	private ArrayList<String> availableObjects = new ArrayList<String>();
	private boolean deleted;

	
	public GlobalShop(String name, String economy, HyperAccount owner, String banned_objects) {
		HyperConomy hc = HyperConomy.hc;
		this.deleted = false;
		this.name = name;
		this.economy = economy;
		this.owner = owner.getName();
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
	}
	
	
	public GlobalShop(String shopName, String economy, HyperAccount owner) {
		HyperConomy hc = HyperConomy.hc;
		this.deleted = false;
		this.name = shopName;
		this.economy = economy;
		this.owner = owner.getName();
		HashMap<String,String> values = new HashMap<String,String>();
		values.put("NAME", name);
		values.put("ECONOMY", economy);
		values.put("OWNER", owner.getName());
		values.put("WORLD", "none");
		values.put("P1X", "0");
		values.put("P1Y", "0");
		values.put("P1Z", "0");
		values.put("P2X", "0");
		values.put("P2Y", "0");
		values.put("P2Z", "0");
		values.put("ALLOWED_PLAYERS", "");
		values.put("BANNED_OBJECTS", "");
		values.put("MESSAGE", "");
		values.put("TYPE", "global");
		hc.getSQLWrite().performInsert("hyperconomy_shops", values);
		availableObjects.clear();
		HyperEconomy he = getHyperEconomy();
		for (HyperObject ho:he.getHyperObjects()) {
			availableObjects.add(ho.getName());
		}
	}
	

	@Override
	public int compareTo(Shop s) {
		return name.compareTo(s.getName());
	}

	@Override
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
	@Override
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

	

	@Override
	public String getEconomy() {
		return economy;
	}
	@Override
	public HyperEconomy getHyperEconomy() {
		HyperConomy hc = HyperConomy.hc;
		HyperEconomy he = hc.getDataManager().getEconomy(economy);
		if (he == null) {
			hc.getDataBukkit().writeError("Null HyperEconomy for economy: " + economy + ", shop: " + name);
			he = hc.getDataManager().getEconomy("default");
		}
		return he;
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
	@Override
	public boolean isStocked(HyperObject ho) {
		if (ho != null && ho.getStock() > 0) {
			return true;
		}
		return false;
	}
	@Override
	public boolean isBanned(HyperObject ho) {
		if (availableObjects.contains(ho.getName())) {
			return false;
		}
		return true;
	}
	@Override
	public boolean isBanned(String name) {
		return isBanned(getHyperEconomy().getHyperObject(name));
	}
	@Override
	public boolean isTradeable(HyperObject ho) {
		if (!isBanned(ho)) {
			return true;
		}
		return false;
	}
	@Override
	public boolean isStocked(String item) {
		return isStocked(getHyperEconomy().getHyperObject(item));
	}
	@Override
	public boolean isAvailable(HyperObject ho) {
		if (isTradeable(ho) && isStocked(ho)) {
			return true;
		}
		return false;
	}
	@Override
	public ArrayList<HyperObject> getTradeableObjects() {
		HyperEconomy he = getHyperEconomy();
		ArrayList<HyperObject> available = new ArrayList<HyperObject>();
		for (String name:availableObjects) {
			available.add(he.getHyperObject(name));
		}
		return available;
	}
	@Override
	public void unBanAllObjects() {
		availableObjects.clear();
		for (HyperObject ho:getHyperEconomy().getHyperObjects()) {
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
	public void unBanObjects(ArrayList<HyperObject> objects) {
		for (HyperObject ho:objects) {
			if (!availableObjects.contains(ho.getName())) {
				availableObjects.add(ho.getName());
			}
		}
		saveAvailable();
	}
	@Override
	public void banObjects(ArrayList<HyperObject> objects) {
		for (HyperObject ho:objects) {
			if (availableObjects.contains(ho.getName())) {
				availableObjects.remove(ho.getName());
			}
		}
		saveAvailable();
	}
	

	@Override
	public HyperAccount getOwner() {
		HyperConomy hc = HyperConomy.hc;
		return hc.getHyperPlayerManager().getAccount(owner);
	}

	@Override
	public void deleteShop() {
		HyperConomy hc = HyperConomy.hc;
		HashMap<String,String> conditions = new HashMap<String,String>();
		conditions.put("NAME", name);
		hc.getSQLWrite().performDelete("hyperconomy_shops", conditions);
		hc.getHyperShopManager().removeShop(name);
		deleted = true;
		hc.getHyperEventHandler().fireEvent(new ShopModificationEvent(this));
	}
	@Override
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
	
	@Override
	public ArrayList<SimpleLocation> getShopBlockLocations() {return null;}
	@Override
	public boolean intersectsShop(Shop s, int volumeLimit) {return false;}
	@Override
	public void setPoint1(String world, int x, int y, int z) {}
	@Override
	public void setPoint2(String world, int x, int y, int z) {}
	@Override
	public void setPoint1(SimpleLocation l) {}
	@Override
	public void setPoint2(SimpleLocation l) {}
	@Override
	public void setMessage(String message) {}
	@Override
	public void sendEntryMessage(HyperPlayer player) {}
	@Override
	public void setDefaultMessage() {}
	@Override
	public void setWorld(String world) {}
	@Override
	public boolean inShop(int x, int y, int z, String world) {return true;}	
	@Override
	public boolean inShop(SimpleLocation l) {return true;}
	@Override
	public boolean inShop(HyperPlayer hp) {return true;}
	@Override
	public void updatePlayerStatus() {}
	@Override
	public int getVolume() {return 0;}
	@Override
	public int getP1x() {
		return 0;
	}
	@Override
	public int getP1y() {
		return 0;
	}
	@Override
	public int getP1z() {
		return 0;
	}
	@Override
	public int getP2x() {
		return 0;
	}
	@Override
	public int getP2y() {
		return 0;
	}
	@Override
	public int getP2z() {
		return 0;
	}
	@Override
	public SimpleLocation getLocation1() {
		return null;
	}
	@Override
	public SimpleLocation getLocation2() {
		return null;
	}
	@Override
	public boolean deleted() {
		return deleted;
	}
}
