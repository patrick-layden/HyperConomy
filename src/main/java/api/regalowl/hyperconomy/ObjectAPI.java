package regalowl.hyperconomy;

import java.util.ArrayList;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

//UNDER CONSTRUCTION

public interface ObjectAPI
{

	/**
	 * 
	 * @param id
	 *            The id of an item, enchantment, or custom object.
	 * @param damageValue
	 *            The durability value of an item, enchantment, or custom
	 *            object.
	 * @param amount
	 *            The amount of the object.
	 * @param nameOfEconomy
	 *            The name of the economy that the object is a part of. Put
	 *            "default" if SQL is not used or to specify the default
	 *            economy.
	 * @return The purchase price of an object before tax and price modifiers.
	 */
	@Deprecated
	double getTheoreticalPurchasePrice(int id, int durability, int amount, String nameOfEconomy);

	/**
	 * 
	 * @param id
	 *            The id of an item, enchantment, or custom object.
	 * @param damageValue
	 *            The durability value of an item, enchantment, or custom
	 *            object.
	 * @param amount
	 *            The amount of the object.
	 * @param nameOfEconomy
	 *            The name of the economy that the object is a part of. Put
	 *            "default" if SQL is not used or to specify the default
	 *            economy.
	 * @return The sale value of an object ignoring durability, tax, and all
	 *         price modifiers.
	 */
	@Deprecated
	double getTheoreticalSaleValue(int id, int durability, int amount, String nameOfEconomy);

	/**
	 * 
	 * @param id
	 *            The id of an item, enchantment, or custom object.
	 * @param damageValue
	 *            The durability value of an item, enchantment, or custom
	 *            object.
	 * @param amount
	 *            The amount of the object.
	 * @param nameOfEconomy
	 *            The name of the economy that the object is a part of. Put
	 *            "default" if SQL is not used or to specify the default
	 *            economy.
	 * @return The purchase price of an object including taxes and all price
	 *         modifiers.
	 */
	@Deprecated
	double getTruePurchasePrice(int id, int durability, int amount, String nameOfEconomy);

	/**
	 * 
	 * @param id
	 *            The id of an item, enchantment, or custom object.
	 * @param damageValue
	 *            The durability value of an item, enchantment, or custom
	 *            object.
	 * @param amount
	 *            The amount of the object.
	 * @param player
	 *            The player that is selling the object, item, or enchantment.
	 * @return The sale value of an object including all taxes, durability, and
	 *         price modifiers.
	 */

	double getTrueSaleValue(int id, int durability, int amount, Player player);
	
	double getTruePurchasePrice(HyperObject hyperObject, EnchantmentClass enchantClass, int amount);

	double getTrueSaleValue(HyperObject hyperObject, HyperPlayer hyperPlayer, EnchantmentClass enchantClass, int amount);
	
	double getTheoreticalSaleValue(HyperObject hyperObject, EnchantmentClass enchantClass, int amount);
	
	
	/**
	 * Use getHyperObject(String name, String economy).getName()
	 */
	@Deprecated
	public String getName(String name, String economy);
	/**
	 * Use getHyperObject(String name, String economy).getEconomy()
	 */
	@Deprecated
	public String getEconomy(String name, String economy);
	/**
	 * Use getHyperObject(String name, String economy).getType()
	 */
	@Deprecated
	public HyperObjectType getType(String name, String economy);
	/**
	 * Use getHyperObject(String name, String economy).getMaterial()
	 */
	@Deprecated
	public String getMaterial(String name, String economy);
	/**
	 * Use getHyperObject(String name, String economy).getData()
	 */
	@Deprecated
	public int getData(String name, String economy);
	/**
	 * Use getHyperObject(String name, String economy).getDurability()
	 */
	@Deprecated
	public int getDurability(String name, String economy);
	/**
	 * Use getHyperObject(String name, String economy).getValue()
	 */
	@Deprecated
	public double getValue(String name, String economy);
	/**
	 * Use getHyperObject(String name, String economy).getIsStatic()
	 */
	@Deprecated
	public String getStatic(String name, String economy);
	/**
	 * Use getHyperObject(String name, String economy).getStaticPrice()
	 */
	@Deprecated
	public double getStaticPrice(String name, String economy);
	/**
	 * Use getHyperObject(String name, String economy).getStock()
	 */
	@Deprecated
	public double getStock(String name, String economy);
	/**
	 * Use getHyperObject(String name, String economy).getMedian()
	 */
	@Deprecated
	public double getMedian(String name, String economy);
	/**
	 * Use getHyperObject(String name, String economy).getInitiation()
	 */
	@Deprecated
	public String getInitiation(String name, String economy);
	/**
	 * Use getHyperObject(String name, String economy).getStartPrice()
	 */
	@Deprecated
	public double getStartPrice(String name, String economy);
	/**
	 * Use getHyperObject(String name, String economy).setName(String name)
	 */
	@Deprecated
	public void setName(String name, String economy, String newname);
	/**
	 * Use getHyperObject(String name, String economy).setEconomy(String economy)
	 */
	@Deprecated
	public void setEconomy(String name, String economy, String neweconomy);
	/**
	 * Use getHyperObject(String name, String economy).setType(String type)
	 */
	@Deprecated
	public void setType(String name, String economy, String newtype);
	/**
	 * Use getHyperObject(String name, String economy).setMaterial(Material name)
	 */
	@Deprecated
	public void setMaterial(String name, String economy, String newmaterial);
	/**
	 * Use getHyperObject(String name, String economy).setData(Double data)
	 */
	@Deprecated
	public void setData(String name, String economy, int newdata);
	/**
	 * Use getHyperObject(String name, String economy).setDurability(Double durability)
	 */
	@Deprecated
	public void setDurability(String name, String economy, int newdurability);
	/**
	 * Use getHyperObject(String name, String economy).setValue(Double value)
	 */
	@Deprecated
	public void setValue(String name, String economy, double newvalue);
	/**
	 * Use getHyperObject(String name, String economy).setStatic(Boolean static)
	 */
	@Deprecated
	public void setStatic(String name, String economy, String newstatic);
	/**
	 * Use getHyperObject(String name, String economy).setStaticPrice(double price)
	 */
	@Deprecated
	public void setStaticPrice(String name, String economy, double newstaticprice);
	/**
	 * Use getHyperObject(String name, String economy).setStock(double stock)
	 */
	@Deprecated
	public void setStock(String name, String economy, double newstock);
	/**
	 * Use getHyperObject(String name, String economy).setMedian(double median)
	 */
	@Deprecated
	public void setMedian(String name, String economy, double newmedian);
	/**
	 * Use getHyperObject(String name, String economy).setInitiation(boolean initiation)
	 */
	@Deprecated
	public void setInitiation(String name, String economy, String newinitiation);
	/**
	 * Use getHyperObject(String name, String economy).setStartPrice(double startprice)
	 */
	@Deprecated
	public void setStartPrice(String name, String economy, double newstartprice);
	
	
	
	
	
	
	public HyperObject getHyperObject(String name, String economy);
	public HyperObject getHyperObject(ItemStack stack, String economy);
	public HyperObject getHyperObject(ItemStack stack, String economy, Shop s);
	public HyperObject getHyperObject(String name, String economy, Shop s);

	
	
	public HyperItem getHyperItem(String name, String economy);
	public HyperEnchant getHyperEnchant(String name, String economy);
	public BasicObject getBasicObject(String name, String economy);
	public HyperXP getHyperXP(String economy);
	
	public PlayerShopObject getPlayerShopObject(String name, PlayerShop s);
	public PlayerShopItem getPlayerShopItem(String name, PlayerShop s);
	public PlayerShopEnchant getPlayerShopEnchant(String name, PlayerShop s);
	public BasicShopObject getBasicShopObject(String name, PlayerShop s);
	public ShopXp getShopXp(String name, PlayerShop s);
	
	public HyperPlayer getHyperPlayer(String name);
	
	public ArrayList<HyperObject> getEnchantmentHyperObjects(ItemStack stack, String player);
	
	public TransactionResponse buy(Player p, HyperObject o, int amount);
	public TransactionResponse buy(Player p, HyperObject o, int amount, Shop shop);
	
	public TransactionResponse sell(Player p, HyperObject o, int amount);
	public TransactionResponse sell(Player p, HyperObject o, int amount, Shop shop);
	
	public TransactionResponse sellAll(Player p);
	public TransactionResponse sellAll(Player p, Inventory inventory);

	
	
	public ArrayList<HyperObject> getAvailableObjects(String shopname);
	public ArrayList<HyperObject> getAvailableObjects(String shopname, int startingPosition, int limit);
	
	public ArrayList<HyperObject> getAvailableObjects(Player p);
	public ArrayList<HyperObject> getAvailableObjects(Player p, int startingPosition, int limit);
	
	

	
	public EnchantmentClass getEnchantmentClass(ItemStack stack);
}
