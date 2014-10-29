package regalowl.hyperconomy.api;

import java.util.ArrayList;





import regalowl.hyperconomy.DataManager;
import regalowl.hyperconomy.HyperConomy;
import regalowl.hyperconomy.HyperEconomy;
import regalowl.hyperconomy.account.HyperPlayer;
import regalowl.hyperconomy.command.Additem;
import regalowl.hyperconomy.command.Sellall;
import regalowl.hyperconomy.display.ItemDisplayFactory;
import regalowl.hyperconomy.hyperobject.EnchantmentClass;
import regalowl.hyperconomy.hyperobject.HyperItemStack;
import regalowl.hyperconomy.hyperobject.HyperObject;
import regalowl.hyperconomy.serializable.SerializableInventory;
import regalowl.hyperconomy.serializable.SerializableItemStack;
import regalowl.hyperconomy.shop.PlayerShop;
import regalowl.hyperconomy.shop.ServerShop;
import regalowl.hyperconomy.shop.Shop;
import regalowl.hyperconomy.transaction.PlayerTransaction;
import regalowl.hyperconomy.transaction.TransactionResponse;
import regalowl.hyperconomy.transaction.TransactionType;
import regalowl.hyperconomy.util.HItem;

public class HyperAPI implements API {


	public String getPlayerShop(HyperPlayer player) {
		HyperConomy hc = HyperConomy.hc;
		Shop shop = hc.getHyperShopManager().getShop(player);
		if (null == shop){
			return "";
		} else {
			return shop.getName();
		}
	}

	public boolean checkHash(String player, String SHA256Hash) {
		HyperConomy hc = HyperConomy.hc;
		if (hc.getDataManager().hyperPlayerExists(player)) {
			if (hc.getDataManager().getHyperPlayer(player).getHash().equals(SHA256Hash)) {
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}
	
	
	public String getSalt(String player) {
		HyperConomy hc = HyperConomy.hc;
		if (hc.getDataManager().hyperPlayerExists(player)) {
			return hc.getDataManager().getHyperPlayer(player).getSalt();
		} else {
			return "";
		}
	}


	public String getDefaultServerShopAccountName() {
		return HyperConomy.hc.getConf().getString("shop.default-server-shop-account");
	}
	
	
	public boolean isItemDisplay(HItem item) {
		try {
			if (item == null) {
				return false;
			}
			ItemDisplayFactory idf = HyperConomy.hc.getItemDisplay();
			if (idf == null) {
				return false;
			} else {
				return idf.isDisplay(item);
			}
		} catch (Exception e) {
			HyperConomy.hc.gDB().writeError(e);
			return false;
		}
	}

	public Shop getShop(String name) {
		HyperConomy hc = HyperConomy.hc;
		return hc.getHyperShopManager().getShop(name);
	}

	public ServerShop getServerShop(String name) {
		HyperConomy hc = HyperConomy.hc;
		Shop s = hc.getHyperShopManager().getShop(name);
		if (s instanceof ServerShop) {
			return (ServerShop)s;
		}
		return null;
	}

	public PlayerShop getPlayerShop(String name) {
		HyperConomy hc = HyperConomy.hc;
		Shop s = hc.getHyperShopManager().getShop(name);
		if (s instanceof PlayerShop) {
			return (PlayerShop)s;
		}
		return null;
	}
	
	@Override
	public ArrayList<String> getServerShopList() {
		HyperConomy hc = HyperConomy.hc;
		ArrayList<String> serverShops = new ArrayList<String>();
		for (Shop s:hc.getHyperShopManager().getShops()) {
			if (s instanceof ServerShop) {
				serverShops.add(s.getName());
			}
		}
		return serverShops;
	}

	@Override
	public ArrayList<String> getPlayerShopList() {
		HyperConomy hc = HyperConomy.hc;
		ArrayList<String> playerShops = new ArrayList<String>();
		for (Shop s:hc.getHyperShopManager().getShops()) {
			if (s instanceof PlayerShop) {
				playerShops.add(s.getName());
			}
		}
		return playerShops;
	}
	
	public String getDefaultServerShopAccount() {
		return HyperConomy.hc.getConf().getString("shop.default-server-shop-account");
	}
	

	public EnchantmentClass getEnchantmentClass(SerializableItemStack stack) {
		return new HyperItemStack(stack).getEnchantmentClass();
	}
	
	
	

	
	
	public HyperObject getHyperObject(String name, String economy) {
		HyperConomy hc = HyperConomy.hc;
		HyperEconomy he = hc.getDataManager().getEconomy(economy);
		return he.getHyperObject(name);
	}
	
	public HyperObject getHyperObject(SerializableItemStack stack, String economy) { 
		HyperConomy hc = HyperConomy.hc;
		HyperEconomy he = hc.getDataManager().getEconomy(economy);
		return he.getHyperObject(stack);
	}
	public HyperObject getHyperObject(SerializableItemStack stack, String economy, Shop s) {
		HyperConomy hc = HyperConomy.hc;
		HyperEconomy he = hc.getDataManager().getEconomy(economy);
		return he.getHyperObject(stack, s);
	}
	public HyperObject getHyperObject(String name, String economy, Shop s) {
		HyperConomy hc = HyperConomy.hc;
		HyperEconomy he = hc.getDataManager().getEconomy(economy);
		return he.getHyperObject(name, s);
	}
	


	
	public HyperPlayer getHyperPlayer(String name) {
		HyperConomy hc = HyperConomy.hc;
		DataManager dm = hc.getDataManager();
		if (dm.hyperPlayerExists(name)) {
			return dm.getHyperPlayer(name);
		} else {
			return null;
		}
	}
		
	
	public ArrayList<HyperObject> getEnchantmentHyperObjects(SerializableItemStack stack, String player) {
		HyperConomy hc = HyperConomy.hc;
		DataManager dm = hc.getDataManager();
		if (dm.hyperPlayerExists(player)) {
			HyperPlayer hp = hc.getDataManager().getHyperPlayer(player);
			return new HyperItemStack(stack).getEnchantmentObjects(hp.getEconomy());
		} else {
			return new HyperItemStack(stack).getEnchantmentObjects("default");
		}

	}

	public TransactionResponse buy(HyperPlayer hp, HyperObject o, int amount) {
		PlayerTransaction pt = new PlayerTransaction(TransactionType.BUY);
		pt.setHyperObject(o);
		pt.setAmount(amount);
		return hp.processTransaction(pt);
	}

	public TransactionResponse buy(HyperPlayer hp, HyperObject o, int amount, Shop shop) {
		PlayerTransaction pt = new PlayerTransaction(TransactionType.BUY);
		pt.setHyperObject(o);
		pt.setAmount(amount);
		pt.setTradePartner(shop.getOwner());
		return hp.processTransaction(pt);
	}
	
	public TransactionResponse sell(HyperPlayer hp, HyperObject o, int amount ) {
		PlayerTransaction pt = new PlayerTransaction(TransactionType.SELL);
		pt.setHyperObject(o);
		pt.setAmount(amount);
		return hp.processTransaction(pt);
	}
	
	public TransactionResponse sell(HyperPlayer hp, HyperObject o, int amount, Shop shop) {
		PlayerTransaction pt = new PlayerTransaction(TransactionType.SELL);
		pt.setHyperObject(o);
		pt.setAmount(amount);
		pt.setTradePartner(shop.getOwner());
		return hp.processTransaction(pt);
	}

	public TransactionResponse sellAll(HyperPlayer hp) {
		Sellall sa = new Sellall();
		return sa.sellAll(hp, null);
	}

	public ArrayList<HyperObject> getAvailableObjects(HyperPlayer p) {
		HyperConomy hc = HyperConomy.hc;
		Shop s = hc.getHyperShopManager().getShop(p);
		if (s != null) {
			return s.getTradeableObjects();
		}
		return new ArrayList<HyperObject>();
	}

	public ArrayList<HyperObject> getAvailableObjects(HyperPlayer p, int startingPosition, int limit) {
		ArrayList<HyperObject> availableObjects = getAvailableObjects(p);
		ArrayList<HyperObject> availableSubset = new ArrayList<HyperObject>();
		for (int i = startingPosition; i <= limit; i++) {
			if (availableObjects.indexOf(i) != -1) {
				availableSubset.add(availableObjects.get(i));
			}
		}
		return availableSubset;
	}
	
	public ArrayList<HyperObject> getAvailableObjects(String shopname) {
		HyperConomy hc = HyperConomy.hc;
		Shop s = hc.getHyperShopManager().getShop(shopname);
		if (s != null) {
			return s.getTradeableObjects();
		}
		return new ArrayList<HyperObject>();
	}

	public ArrayList<HyperObject> getAvailableObjects(String shopname, int startingPosition, int limit) {
		ArrayList<HyperObject> availableObjects = getAvailableObjects(shopname);
		ArrayList<HyperObject> availableSubset = new ArrayList<HyperObject>();
		for (int i = startingPosition; i <= limit; i++) {
			if (availableObjects.indexOf(i) != -1) {
				availableSubset.add(availableObjects.get(i));
			}
		}
		return availableSubset;
	}
	


	public TransactionResponse sellAll(HyperPlayer hp, SerializableInventory inventory) {
		HyperConomy hc = HyperConomy.hc;
		DataManager em = hc.getDataManager();
		HyperEconomy he = hp.getHyperEconomy();
		TransactionResponse totalResponse = new TransactionResponse(hp);
		for (int slot = 0; slot < inventory.getSize(); slot++) {
			if (inventory.getItem(slot) == null) {continue;}
			SerializableItemStack stack = inventory.getItem(slot);
			HyperObject hyperItem = he.getHyperObject(stack, em.getHyperShopManager().getShop(hp));
			PlayerTransaction pt = new PlayerTransaction(TransactionType.SELL);
			pt.setGiveInventory(hp.getInventory());
			pt.setHyperObject(hyperItem);
			pt.setAmount(stack.getAmount());
			TransactionResponse response = hp.processTransaction(pt);
			if (response.successful()) {
				totalResponse.addSuccess(response.getMessage(), response.getPrice(), response.getSuccessfulObjects().get(0));
			} else {
				totalResponse.addFailed(response.getMessage(), response.getFailedObjects().get(0));
			}
		}
		return totalResponse;
	}

	@Override
	public boolean addItemToEconomy(SerializableItemStack stack, String economyName, String requestedName) {
		Additem ai = new Additem();
		HyperObject hobj = ai.generateNewHyperObject(stack, economyName, requestedName, 0);
		return ai.addItem(hobj, economyName);
	}



	
	
	
	

}
