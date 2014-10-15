package regalowl.hyperconomy.shop;

import java.io.Serializable;
import java.util.ArrayList;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import regalowl.hyperconomy.HyperEconomy;
import regalowl.hyperconomy.account.HyperAccount;
import regalowl.hyperconomy.account.HyperPlayer;
import regalowl.hyperconomy.hyperobject.HyperObject;
import regalowl.hyperconomy.util.SimpleLocation;

public interface Shop extends Comparable<Shop>, Serializable{
	
	public int compareTo(Shop s);	
	public void setPoint1(String world, int x, int y, int z);	
	public void setPoint2(String world, int x, int y, int z);	
	
	public void setPoint1(Location l);
	public void setPoint2(Location l);
	
	public void setMessage(String message);	
	public void setDefaultMessage();	
	public void setWorld(String world);	
	public void setName(String name);	
	public void setEconomy(String economy);
	
	public boolean inShop(int x, int y, int z, String world);
	public boolean inShop(SimpleLocation l);
	public boolean inShop(Player player);
	public boolean inShop(HyperPlayer hp);
	public void sendEntryMessage(Player player);
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
	public boolean isStocked(HyperObject ho);
	/**
	 * Returns true if the HyperObject is not banned, and is tradeable.
	 */
	public boolean isTradeable(HyperObject ho);
	/**
	 * Returns true if a HyperObject is banned from the Shop.
	 */
	public boolean isBanned(HyperObject ho);
	/**
	 * Returns true if the HyperObject with the given name is banned from the Shop.
	 */
	public boolean isBanned(String name);
	/**
	 * Returns true if a HyperObject is in stock and tradeable.
	 */
	public boolean isAvailable(HyperObject ho);
	
	/**
	 * Returns all HyperObjects that are available for trade in this shop.
	 */
	public ArrayList<HyperObject> getTradeableObjects();
	
	public void unBanAllObjects();
	public void banAllObjects();
	public void unBanObjects(ArrayList<HyperObject> objects);
	public void banObjects(ArrayList<HyperObject> objects);
	
	public int getP1x();
	public int getP1y();
	public int getP1z();
	public int getP2x();
	public int getP2y();
	public int getP2z();
	public SimpleLocation getLocation1();
	public SimpleLocation getLocation2();
	public void updatePlayerStatus();
	
	public int getVolume();
	public void deleteShop();
	public ArrayList<SimpleLocation> getShopBlockLocations();
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
}
