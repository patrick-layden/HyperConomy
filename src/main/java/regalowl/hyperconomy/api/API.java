package regalowl.hyperconomy.api;

import java.util.ArrayList;








import java.util.UUID;

import regalowl.hyperconomy.account.HyperPlayer;
import regalowl.hyperconomy.inventory.HInventory;
import regalowl.hyperconomy.inventory.HItemStack;
import regalowl.hyperconomy.minecraft.HItem;
import regalowl.hyperconomy.shop.PlayerShop;
import regalowl.hyperconomy.shop.ServerShop;
import regalowl.hyperconomy.shop.Shop;
import regalowl.hyperconomy.tradeobject.EnchantmentClass;
import regalowl.hyperconomy.tradeobject.TradeObject;
import regalowl.hyperconomy.transaction.TransactionResponse;



public interface API {


	String getDefaultServerShopAccountName();
	
	
	Shop getShop(String name);
	ServerShop getServerShop(String name);
	PlayerShop getPlayerShop(String name);
	
	
	ArrayList<String> getServerShopList();
	ArrayList<String> getPlayerShopList();
	
	HyperPlayer getHyperPlayer(String name);
	HyperPlayer getHyperPlayer(UUID uuid);
	boolean hyperPlayerExists(String name);
	boolean hyperPlayerExists(UUID uuid);
	HyperPlayer createHyperPlayer(String name);
	/**
	 * @param player (name of player)
	 * @return true if the hash matches the player's hash and false if it doesn't
	 */
	boolean checkHash(String player, String hash);

	/**
	 * @param player (name of player)
	 * @return The random hash for the specified player.  If the player is not in the HyperConomy database it returns ""
	 */
	String getSalt(String player);
	/**
	 * @param Item entity
	 * @return Returns true if the given Item is being used as an ItemDisplay and false if it is not.
	 */
	boolean isItemDisplay(HItem item);
	
	EnchantmentClass getEnchantmentClass(HItemStack stack);

	TradeObject getHyperObject(String name, String economy);
	TradeObject getHyperObject(String name, String economy, Shop s);
	TradeObject getHyperObject(HItemStack stack, String economy);
	TradeObject getHyperObject(HItemStack stack, String economy, Shop s);
	ArrayList<TradeObject> getEnchantmentHyperObjects(HItemStack stack, String player);
	
	ArrayList<TradeObject> getAvailableObjects(String shopname);
	ArrayList<TradeObject> getAvailableObjects(String shopname, int startingPosition, int limit);
	ArrayList<TradeObject> getAvailableObjects(HyperPlayer p);
	ArrayList<TradeObject> getAvailableObjects(HyperPlayer p, int startingPosition, int limit);
	
	TransactionResponse buy(HyperPlayer p, TradeObject o, int amount);
	TransactionResponse buy(HyperPlayer p, TradeObject o, int amount, Shop shop);
	TransactionResponse sell(HyperPlayer p, TradeObject o, int amount);
	TransactionResponse sell(HyperPlayer p, TradeObject o, int amount, Shop shop);
	TransactionResponse sellAll(HyperPlayer p);
	TransactionResponse sellAll(HyperPlayer p, HInventory inventory);
	
	boolean addItemToEconomy(HItemStack stack, String economyName, String requestedName);

}
