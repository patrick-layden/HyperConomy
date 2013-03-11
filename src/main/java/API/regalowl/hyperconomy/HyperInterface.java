package regalowl.hyperconomy;



public interface HyperInterface {

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
	
}
