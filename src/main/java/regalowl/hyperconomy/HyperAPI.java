package regalowl.hyperconomy;

import java.util.ArrayList;
import java.util.UUID;

import regalowl.hyperconomy.account.HyperPlayer;
import regalowl.hyperconomy.account.HyperPlayerManager;
import regalowl.hyperconomy.api.API;
import regalowl.hyperconomy.command.Additem;
import regalowl.hyperconomy.command.Sellall;
import regalowl.hyperconomy.display.ItemDisplayHandler;
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

	private transient HyperConomy hc;
	
	public HyperAPI(HyperConomy hc) {
		this.hc = hc;
	}

	public String getPlayerShop(HyperPlayer player) {
		Shop shop = hc.getHyperShopManager().getShop(player);
		return shop == null ? "" : shop.getName();
	}

	@Override
	public boolean checkHash(String player, String SHA256Hash) {
		return hc.getHyperPlayerManager().hyperPlayerExists(player)
				&& hc.getHyperPlayerManager().getHyperPlayer(player).getHash().equals(SHA256Hash);
	}
	
	@Override
	public String getSalt(String player) {
		return hc.getHyperPlayerManager().hyperPlayerExists(player) ? ""
				: hc.getHyperPlayerManager().getHyperPlayer(player).getSalt();
	}

	@Override
	public String getDefaultServerShopAccountName() {
		return hc.getConf().getString("shop.default-server-shop-account");
	}
	
	@Override
	public boolean isItemDisplay(HItem item) {
		try {
			if (item == null) {
				return false;
			}
			ItemDisplayHandler idf = hc.getItemDisplay();
			if (idf == null) {
				return false;
			} else {
				return idf.isDisplay(item);
			}
		} catch (Exception e) {
			hc.gSDL().getErrorWriter().writeError(e);
			return false;
		}
	}

	@Override
	public Shop getShop(String name) {
		return hc.getHyperShopManager().getShop(name);
	}

	@Override
	public ServerShop getServerShop(String name) {
		Shop s = hc.getHyperShopManager().getShop(name);
		if (s instanceof ServerShop) {
			return (ServerShop)s;
		}
		return null;
	}

	@Override
	public PlayerShop getPlayerShop(String name) {
		Shop s = hc.getHyperShopManager().getShop(name);
		if (s instanceof PlayerShop) {
			return (PlayerShop)s;
		}
		return null;
	}
	
	@Override
	public ArrayList<String> getServerShopList() {
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
		ArrayList<String> playerShops = new ArrayList<String>();
		for (Shop s:hc.getHyperShopManager().getShops()) {
			if (s instanceof PlayerShop) {
				playerShops.add(s.getName());
			}
		}
		return playerShops;
	}
	
	@Override
	public EnchantmentClass getEnchantmentClass(HItemStack stack) {
		return EnchantmentClass.fromString(stack.getMaterial());
	}
	
	@Override
	public TradeObject getHyperObject(String name, String economy) {
		HyperEconomy he = hc.getDataManager().getEconomy(economy);
		return he.getTradeObject(name);
	}
	
	@Override
	public TradeObject getHyperObject(HItemStack stack, String economy) { 
		HyperEconomy he = hc.getDataManager().getEconomy(economy);
		return he.getTradeObject(stack);
	}

	@Override
	public TradeObject getHyperObject(HItemStack stack, String economy, Shop s) {
		HyperEconomy he = hc.getDataManager().getEconomy(economy);
		return he.getTradeObject(stack, s);
	}

	@Override
	public TradeObject getHyperObject(String name, String economy, Shop s) {
		HyperEconomy he = hc.getDataManager().getEconomy(economy);
		return he.getTradeObject(name, s);
	}
	
	@Override
	public HyperPlayer getHyperPlayer(String name) {
		HyperPlayerManager hpm = hc.getHyperPlayerManager();
		return hpm.hyperPlayerExists(name) ? hpm.getHyperPlayer(name) : null;
	}

	@Override
	public HyperPlayer getHyperPlayer(UUID uuid) {
		HyperPlayerManager hpm = hc.getHyperPlayerManager();
		return hpm.hyperPlayerExistsWithUUID(uuid) ? hpm.getHyperPlayer(uuid) : null;
	}
	@Override
	public boolean hyperPlayerExists(String name) {
		HyperPlayerManager hpm = hc.getHyperPlayerManager();
		return hpm.hyperPlayerExists(name);
	}
	@Override
	public boolean hyperPlayerExists(UUID uuid) {
		HyperPlayerManager hpm = hc.getHyperPlayerManager();
		return hpm.hyperPlayerExistsWithUUID(uuid);
	}

	@Override
	public HyperPlayer createHyperPlayer(String name) {
		HyperPlayerManager hpm = hc.getHyperPlayerManager();
		return hpm.getHyperPlayer(name);
	}

	@Override
	public ArrayList<TradeObject> getEnchantmentHyperObjects(HItemStack stack, String player) {
		DataManager dm = hc.getDataManager();
		ArrayList<TradeObject> objects = new ArrayList<TradeObject>();
		HyperEconomy he = dm.getDefaultEconomy();
		if (hc.getHyperPlayerManager().hyperPlayerExists(player)) he = hc.getHyperPlayerManager().getHyperPlayer(player).getHyperEconomy();
		for (HEnchantment se:stack.getItemMeta().getEnchantments()) {
			TradeObject ho = he.getTradeObject(se);
			if (ho != null) objects.add(ho);
		}
		return objects;
	}

	@Override
	public TransactionResponse buy(HyperPlayer hp, TradeObject o, int amount) {
		PlayerTransaction pt = new PlayerTransaction(TransactionType.BUY);
		pt.setHyperObject(o);
		pt.setAmount(amount);
		return hp.processTransaction(pt);
	}

	@Override
	public TransactionResponse buy(HyperPlayer hp, TradeObject o, int amount, Shop shop) {
		PlayerTransaction pt = new PlayerTransaction(TransactionType.BUY);
		pt.setHyperObject(o);
		pt.setAmount(amount);
		pt.setTradePartner(shop.getOwner());
		return hp.processTransaction(pt);
	}
	
	@Override
	public TransactionResponse sell(HyperPlayer hp, TradeObject o, int amount ) {
		PlayerTransaction pt = new PlayerTransaction(TransactionType.SELL);
		pt.setHyperObject(o);
		pt.setAmount(amount);
		return hp.processTransaction(pt);
	}
	
	@Override
	public TransactionResponse sell(HyperPlayer hp, TradeObject o, int amount, Shop shop) {
		PlayerTransaction pt = new PlayerTransaction(TransactionType.SELL);
		pt.setHyperObject(o);
		pt.setAmount(amount);
		pt.setTradePartner(shop.getOwner());
		return hp.processTransaction(pt);
	}

	@Override
	public TransactionResponse sellAll(HyperPlayer hp) {
		Sellall sa = new Sellall(hc);
		return sa.sellAll(hp, null);
	}

	@Override
	public ArrayList<TradeObject> getAvailableObjects(HyperPlayer p) {
		Shop s = hc.getHyperShopManager().getShop(p);
		if (s != null) {
			return s.getTradeableObjects();
		}
		return new ArrayList<TradeObject>();
	}

	@Override
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
	
	@Override
	public ArrayList<TradeObject> getAvailableObjects(String shopname) {
		Shop s = hc.getHyperShopManager().getShop(shopname);
		if (s != null) {
			return s.getTradeableObjects();
		}
		return new ArrayList<TradeObject>();
	}

	@Override
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
	
	@Override
	public TransactionResponse sellAll(HyperPlayer hp, HInventory inventory) {
		DataManager em = hc.getDataManager();
		HyperEconomy he = hp.getHyperEconomy();
		TransactionResponse totalResponse = new TransactionResponse(hc, hp);
		for (int slot = 0; slot < inventory.getSize(); slot++) {
			if (inventory.getItem(slot) == null) {continue;}
			HItemStack stack = inventory.getItem(slot);
			TradeObject hyperItem = he.getTradeObject(stack, em.getHyperShopManager().getShop(hp));
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
		HyperEconomy he = hc.getDataManager().getEconomy(economyName);
		if (he == null) return false;
		if (he.objectTest(requestedName) || requestedName.equalsIgnoreCase("")) return false;
		Additem ai = new Additem(hc);
		TradeObject hobj = ai.generateNewHyperObject(stack, economyName, requestedName, 0);
		hobj.save();
		return true;
		//return ai.addItem(hobj, economyName);
	}
}
