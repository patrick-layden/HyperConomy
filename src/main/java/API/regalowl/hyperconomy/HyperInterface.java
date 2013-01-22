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
	boolean checkPassword(String player, String hash);
	double getAPIVersion();
	
}
