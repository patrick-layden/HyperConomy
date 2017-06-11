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


	public String getDefaultServerShopAccountName();
	
	
	public Shop getShop(String name);
	public ServerShop getServerShop(String name);
	public PlayerShop getPlayerShop(String name);
	
	
	public ArrayList<String> getServerShopList();
	public ArrayList<String> getPlayerShopList();
	
	public HyperPlayer getHyperPlayer(String name);
	public HyperPlayer getHyperPlayer(UUID uuid);
	public boolean hyperPlayerExists(String name);
	public boolean hyperPlayerExists(UUID uuid);
	public HyperPlayer createHyperPlayer(String name);
	/**
	 * @param player (name of player)
	 * @return true if the hash matches the player's hash and false if it doesn't
	 */
	public boolean checkHash(String player, String hash);

	/**
	 * @param player (name of player)
	 * @return The random hash for the specified player.  If the player is not in the HyperConomy database it returns ""
	 */
	public String getSalt(String player);
	/**
	 * @param Item entity
	 * @return Returns true if the given Item is being used as an ItemDisplay and false if it is not.
	 */
	public boolean isItemDisplay(HItem item);
	
	public EnchantmentClass getEnchantmentClass(HItemStack stack);

	public TradeObject getHyperObject(String name, String economy);
	public TradeObject getHyperObject(String name, String economy, Shop s);
	public TradeObject getHyperObject(HItemStack stack, String economy);
	public TradeObject getHyperObject(HItemStack stack, String economy, Shop s);
	public ArrayList<TradeObject> getEnchantmentHyperObjects(HItemStack stack, String player);
	
	public ArrayList<TradeObject> getAvailableObjects(String shopname);
	public ArrayList<TradeObject> getAvailableObjects(String shopname, int startingPosition, int limit);
	public ArrayList<TradeObject> getAvailableObjects(HyperPlayer p);
	public ArrayList<TradeObject> getAvailableObjects(HyperPlayer p, int startingPosition, int limit);
	
	public TransactionResponse buy(HyperPlayer p, TradeObject o, int amount);
	public TransactionResponse buy(HyperPlayer p, TradeObject o, int amount, Shop shop);
	public TransactionResponse sell(HyperPlayer p, TradeObject o, int amount);
	public TransactionResponse sell(HyperPlayer p, TradeObject o, int amount, Shop shop);
	public TransactionResponse sellAll(HyperPlayer p);
	public TransactionResponse sellAll(HyperPlayer p, HInventory inventory);
	
	public boolean addItemToEconomy(HItemStack stack, String economyName, String requestedName);

}
