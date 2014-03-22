package regalowl.hyperconomy.api;

import java.util.ArrayList;

import org.bukkit.entity.Item;

import regalowl.hyperconomy.shop.PlayerShop;
import regalowl.hyperconomy.shop.ServerShop;
import regalowl.hyperconomy.shop.Shop;



public interface GeneralAPI {

	String listShops();
	String listEconomies();
	int getShopP1X(String shop);
	int getShopP1Y(String shop);
	int getShopP1Z(String shop);
	int getShopP2X(String shop);
	int getShopP2Y(String shop);
	int getShopP2Z(String shop);
	double getPlayerX(String player);
	double getPlayerY(String player);
	double getPlayerZ(String player);
	String getShopEconomy(String shop);
	String getGlobalShopAccount();
	
	
	ArrayList<String> getPlayerShopList();
	ArrayList<String> getServerShopList();
	Shop getShop(String name);
	ServerShop getServerShop(String name);
	PlayerShop getPlayerShop(String name);
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
	double getAPIVersion();
	/**
	 * @param Item entity
	 * @return Returns true if the given Item is being used as an ItemDisplay and false if it is not.
	 */
	boolean isItemDisplay(Item item);
}
