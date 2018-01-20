package regalowl.hyperconomy.shop;

import java.io.Serializable;
import java.util.ArrayList;





import regalowl.hyperconomy.HyperConomy;
import regalowl.hyperconomy.HyperEconomy;
import regalowl.hyperconomy.account.HyperAccount;
import regalowl.hyperconomy.account.HyperPlayer;
import regalowl.hyperconomy.minecraft.HLocation;
import regalowl.hyperconomy.tradeobject.TradeObject;

public interface Shop extends Comparable<Shop>, Serializable{
	
	int compareTo(Shop s);
	void setHyperConomy(HyperConomy hc);
	void setPoint1(String world, int x, int y, int z);
	void setPoint2(String world, int x, int y, int z);
	
	void setPoint1(HLocation l);
	void setPoint2(HLocation l);
	
	void setMessage(String message);
	void setDefaultMessage();
	void setWorld(String world);
	void setName(String name);
	void setEconomy(String economy);
	
	boolean inShop(int x, int y, int z, String world);
	boolean inShop(HLocation l);
	boolean inShop(HyperPlayer hp);
	void sendEntryMessage(HyperPlayer player);
	String getEconomy();
	
	HyperEconomy getHyperEconomy();
	String getName();
	HyperAccount getOwner();
	void setOwner(HyperAccount owner);
	String getDisplayName();
	
	/**
	 * Returns true if the Shop has stock of the given object name.
	 */
	boolean isStocked(String item);
	/**
	 * Returns true if the Shop has stock of the given HyperObject.
	 */
	boolean isStocked(TradeObject ho);
	/**
	 * Returns true if the HyperObject is not banned, and is tradeable.
	 */
	boolean isTradeable(TradeObject ho);
	/**
	 * Returns true if the HyperObject with the given name is banned from the Shop.
	 */
	boolean isBanned(String name);
	/**
	 * Returns true if a HyperObject is in stock and tradeable.
	 */
	boolean isAvailable(TradeObject ho);
	
	/**
	 * Returns all HyperObjects that are available for trade in this shop.
	 */
	ArrayList<TradeObject> getTradeableObjects();
	
	void unBanAllObjects();
	void banAllObjects();
	void unBanObjects(ArrayList<TradeObject> objects);
	void banObjects(ArrayList<TradeObject> objects);
	
	int getP1x();
	int getP1y();
	int getP1z();
	int getP2x();
	int getP2y();
	int getP2z();
	HLocation getLocation1();
	HLocation getLocation2();
	void updatePlayerStatus();
	
	int getVolume();
	void deleteShop();
	ArrayList<HLocation> getShopBlockLocations();
	/**
	 * @param s A shop.
	 * @param volumeLimit A maximum volume for this test.
	 * @return true if the given shop intersects with this shop, false if not.  If the given shop's volume is greater than the given volume limit,
	 * it will automatically return false;
	 */
	boolean intersectsShop(Shop s, int volumeLimit);
	void saveAvailable();
	/**
	 *  Returns true if the shop has been deleted.
	 */
	boolean deleted();
	void removeTradeObject(TradeObject to);
}
