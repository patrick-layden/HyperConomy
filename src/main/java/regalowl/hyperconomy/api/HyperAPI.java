package regalowl.hyperconomy.api;

import java.util.ArrayList;









import regalowl.hyperconomy.DataManager;
import regalowl.hyperconomy.HC;
import regalowl.hyperconomy.HyperEconomy;
import regalowl.hyperconomy.account.HyperPlayer;
import regalowl.hyperconomy.command.Additem;
import regalowl.hyperconomy.command.Sellall;
import regalowl.hyperconomy.display.ItemDisplayFactory;
import regalowl.hyperconomy.inventory.HEnchantment;
import regalowl.hyperconomy.inventory.HInventory;
import regalowl.hyperconomy.inventory.HItemStack;
import regalowl.hyperconomy.minecraft.HItem;
import regalowl.hyperconomy.shop.PlayerShop;
import regalowl.hyperconomy.shop.ServerShop;
import regalowl.hyperconomy.shop.Shop;
import regalowl.hyperconomy.tradeobject.EnchantmentClass;
import regalowl.hyperconomy.tradeobject.TradeObject;
import regalowl.hyperconomy.transaction.PlayerTransaction;
import regalowl.hyperconomy.transaction.TransactionResponse;
import regalowl.hyperconomy.transaction.TransactionType;

public class HyperAPI implements API {


	public String getPlayerShop(HyperPlayer player) {
		HC hc = HC.hc;
		Shop shop = hc.getHyperShopManager().getShop(player);
		if (null == shop){
			return "";
		} else {
			return shop.getName();
		}
	}

	public boolean checkHash(String player, String SHA256Hash) {
		if (HC.hc.getDataManager().hyperPlayerExists(player)) {
			if (HC.hc.getDataManager().getHyperPlayer(player).getHash().equals(SHA256Hash)) {
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}
	
	
	public String getSalt(String player) {
		if (HC.hc.getDataManager().hyperPlayerExists(player)) {
			return HC.hc.getDataManager().getHyperPlayer(player).getSalt();
		} else {
			return "";
		}
	}


	public String getDefaultServerShopAccountName() {
		return HC.hc.getConf().getString("shop.default-server-shop-account");
	}
	
	
	public boolean isItemDisplay(HItem item) {
		try {
			if (item == null) {
				return false;
			}
			ItemDisplayFactory idf = HC.hc.getItemDisplay();
			if (idf == null) {
				return false;
			} else {
				return idf.isDisplay(item);
			}
		} catch (Exception e) {
			HC.hc.gSDL().getErrorWriter().writeError(e);
			return false;
		}
	}

	public Shop getShop(String name) {
		HC hc = HC.hc;
		return hc.getHyperShopManager().getShop(name);
	}

	public ServerShop getServerShop(String name) {
		HC hc = HC.hc;
		Shop s = hc.getHyperShopManager().getShop(name);
		if (s instanceof ServerShop) {
			return (ServerShop)s;
		}
		return null;
	}

	public PlayerShop getPlayerShop(String name) {
		HC hc = HC.hc;
		Shop s = hc.getHyperShopManager().getShop(name);
		if (s instanceof PlayerShop) {
			return (PlayerShop)s;
		}
		return null;
	}
	
	@Override
	public ArrayList<String> getServerShopList() {
		HC hc = HC.hc;
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
		HC hc = HC.hc;
		ArrayList<String> playerShops = new ArrayList<String>();
		for (Shop s:hc.getHyperShopManager().getShops()) {
			if (s instanceof PlayerShop) {
				playerShops.add(s.getName());
			}
		}
		return playerShops;
	}
	
	public String getDefaultServerShopAccount() {
		return HC.hc.getConf().getString("shop.default-server-shop-account");
	}
	

	public EnchantmentClass getEnchantmentClass(HItemStack stack) {
		return EnchantmentClass.fromString(stack.getMaterial());
	}
	
	
	

	
	
	public TradeObject getHyperObject(String name, String economy) {
		HyperEconomy he = HC.hc.getDataManager().getEconomy(economy);
		return he.getHyperObject(name);
	}
	
	public TradeObject getHyperObject(HItemStack stack, String economy) { 
		HyperEconomy he = HC.hc.getDataManager().getEconomy(economy);
		return he.getHyperObject(stack);
	}
	public TradeObject getHyperObject(HItemStack stack, String economy, Shop s) {
		HyperEconomy he = HC.hc.getDataManager().getEconomy(economy);
		return he.getHyperObject(stack, s);
	}
	public TradeObject getHyperObject(String name, String economy, Shop s) {
		HyperEconomy he = HC.hc.getDataManager().getEconomy(economy);
		return he.getHyperObject(name, s);
	}
	


	
	public HyperPlayer getHyperPlayer(String name) {
		DataManager dm = HC.hc.getDataManager();
		if (dm.hyperPlayerExists(name)) {
			return dm.getHyperPlayer(name);
		} else {
			return null;
		}
	}
		
	
	public ArrayList<TradeObject> getEnchantmentHyperObjects(HItemStack stack, String player) {
		DataManager dm = HC.hc.getDataManager();
		ArrayList<TradeObject> objects = new ArrayList<TradeObject>();
		HyperEconomy he = dm.getDefaultEconomy();
		if (dm.hyperPlayerExists(player)) he = HC.hc.getDataManager().getHyperPlayer(player).getHyperEconomy();
		for (HEnchantment se:stack.getItemMeta().getEnchantments()) {
			TradeObject ho = he.getHyperObject(se);
			if (ho != null) objects.add(ho);
		}
		return objects;
	}

	public TransactionResponse buy(HyperPlayer hp, TradeObject o, int amount) {
		PlayerTransaction pt = new PlayerTransaction(TransactionType.BUY);
		pt.setHyperObject(o);
		pt.setAmount(amount);
		return hp.processTransaction(pt);
	}

	public TransactionResponse buy(HyperPlayer hp, TradeObject o, int amount, Shop shop) {
		PlayerTransaction pt = new PlayerTransaction(TransactionType.BUY);
		pt.setHyperObject(o);
		pt.setAmount(amount);
		pt.setTradePartner(shop.getOwner());
		return hp.processTransaction(pt);
	}
	
	public TransactionResponse sell(HyperPlayer hp, TradeObject o, int amount ) {
		PlayerTransaction pt = new PlayerTransaction(TransactionType.SELL);
		pt.setHyperObject(o);
		pt.setAmount(amount);
		return hp.processTransaction(pt);
	}
	
	public TransactionResponse sell(HyperPlayer hp, TradeObject o, int amount, Shop shop) {
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

	public ArrayList<TradeObject> getAvailableObjects(HyperPlayer p) {
		HC hc = HC.hc;
		Shop s = hc.getHyperShopManager().getShop(p);
		if (s != null) {
			return s.getTradeableObjects();
		}
		return new ArrayList<TradeObject>();
	}

	public ArrayList<TradeObject> getAvailableObjects(HyperPlayer p, int startingPosition, int limit) {
		ArrayList<TradeObject> availableObjects = getAvailableObjects(p);
		ArrayList<TradeObject> availableSubset = new ArrayList<TradeObject>();
		for (int i = startingPosition; i <= limit; i++) {
			if (availableObjects.indexOf(i) != -1) {
				availableSubset.add(availableObjects.get(i));
			}
		}
		return availableSubset;
	}
	
	public ArrayList<TradeObject> getAvailableObjects(String shopname) {
		HC hc = HC.hc;
		Shop s = hc.getHyperShopManager().getShop(shopname);
		if (s != null) {
			return s.getTradeableObjects();
		}
		return new ArrayList<TradeObject>();
	}

	public ArrayList<TradeObject> getAvailableObjects(String shopname, int startingPosition, int limit) {
		ArrayList<TradeObject> availableObjects = getAvailableObjects(shopname);
		ArrayList<TradeObject> availableSubset = new ArrayList<TradeObject>();
		for (int i = startingPosition; i <= limit; i++) {
			if (availableObjects.indexOf(i) != -1) {
				availableSubset.add(availableObjects.get(i));
			}
		}
		return availableSubset;
	}
	


	public TransactionResponse sellAll(HyperPlayer hp, HInventory inventory) {
		DataManager em = HC.hc.getDataManager();
		HyperEconomy he = hp.getHyperEconomy();
		TransactionResponse totalResponse = new TransactionResponse(hp);
		for (int slot = 0; slot < inventory.getSize(); slot++) {
			if (inventory.getItem(slot) == null) {continue;}
			HItemStack stack = inventory.getItem(slot);
			TradeObject hyperItem = he.getHyperObject(stack, em.getHyperShopManager().getShop(hp));
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
	public boolean addItemToEconomy(HItemStack stack, String economyName, String requestedName) {
		Additem ai = new Additem();
		TradeObject hobj = ai.generateNewHyperObject(stack, economyName, requestedName, 0);
		return ai.addItem(hobj, economyName);
	}



	
	
	
	

}
