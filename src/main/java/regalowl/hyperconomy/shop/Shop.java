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
	
	@Override
	public int compareTo(Shop s);
	public void setHyperConomy(HyperConomy hc);
	public void setPoint1(String world, int x, int y, int z);	
	public void setPoint2(String world, int x, int y, int z);	
	
	public void setPoint1(HLocation l);
	public void setPoint2(HLocation l);
	
	public void setMessage(String message);	
	public void setDefaultMessage();	
	public void setWorld(String world);	
	public void setName(String name);	
	public void setEconomy(String economy);
	
	public boolean inShop(int x, int y, int z, String world);
	public boolean inShop(HLocation l);
	public boolean inShop(HyperPlayer hp);
	public void sendEntryMessage(HyperPlayer player);
	public String getEconomy();
	
	public HyperEconomy getHyperEconomy();
	public String getName();
	public HyperAccount getOwner();
	public void setOwner(HyperAccount owner);
	public String getDisplayName();
	
	/**
	 * Returns true if the Shop has stock of the given object name.
	 */
	public boolean isStocked(String item);
	/**
	 * Returns true if the Shop has stock of the given HyperObject.
	 */
	public boolean isStocked(TradeObject ho);
	/**
	 * Returns true if the HyperObject is not banned, and is tradeable.
	 */
	public boolean isTradeable(TradeObject ho);
	/**
	 * Returns true if the HyperObject with the given name is banned from the Shop.
	 */
	public boolean isBanned(String name);
	/**
	 * Returns true if a HyperObject is in stock and tradeable.
	 */
	public boolean isAvailable(TradeObject ho);
	
	/**
	 * Returns all HyperObjects that are available for trade in this shop.
	 */
	public ArrayList<TradeObject> getTradeableObjects();
	
	public void unBanAllObjects();
	public void banAllObjects();
	public void unBanObjects(ArrayList<TradeObject> objects);
	public void banObjects(ArrayList<TradeObject> objects);
	
	public int getP1x();
	public int getP1y();
	public int getP1z();
	public int getP2x();
	public int getP2y();
	public int getP2z();
	public HLocation getLocation1();
	public HLocation getLocation2();
	public void updatePlayerStatus();
	
	public int getVolume();
	public void deleteShop();
	public ArrayList<HLocation> getShopBlockLocations();
	/**
	 * @param s A shop.
	 * @param volumeLimit A maximum volume for this test.
	 * @return true if the given shop intersects with this shop, false if not.  If the given shop's volume is greater than the given volume limit,
	 * it will automatically return false;
	 */
	public boolean intersectsShop(Shop s, int volumeLimit);
	public void saveAvailable();
	/**
	 *  Returns true if the shop has been deleted.
	 */
	public boolean deleted();
	public void removeTradeObject(TradeObject to);
}
