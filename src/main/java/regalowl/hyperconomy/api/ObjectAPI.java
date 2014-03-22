package regalowl.hyperconomy.api;

import java.util.ArrayList;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import regalowl.hyperconomy.account.HyperPlayer;
import regalowl.hyperconomy.hyperobject.EnchantmentClass;
import regalowl.hyperconomy.hyperobject.HyperObject;
import regalowl.hyperconomy.shop.Shop;
import regalowl.hyperconomy.transaction.TransactionResponse;


public interface ObjectAPI
{



	
	/**
	 * 
	 * @param material
	 *            The Material of an item, enchantment, or custom object.
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
	double getTheoreticalPurchasePrice(Material material, short durability, int amount, String nameOfEconomy);
	
	
	/**
	 * 
	 * @param name
	 *            The name of the HyperObject (item, enchantment, etc.)
	 * @param economy
	 *            The name of the economy the HyperObject is a part of.  Use "default" if you don't know.
	 * @param amount
	 *            The amount of the object.
	 * @return The purchase price of the given HyperObject/
	 */
	double getPurchasePrice(String name, String economy, int amount);
	/**
	 * 
	 * @param name
	 *            The name of the HyperObject (item, enchantment, etc.)
	 * @param economy
	 *            The name of the economy the HyperObject is a part of.  Use "default" if you don't know.
	 * @param amount
	 *            The amount of the object.
	 * @param enchantmentClass
	 *            The EnchantmentClass of the given enchantment (if it's not an enchantment leave null).
	 * @return The purchase price of the given HyperObject/
	 */
	double getPurchasePrice(String name, String economy, String enchantmentClass, int amount);
	

	
	
	/**
	 * 
	 * @param material
	 *            The Material of an item, enchantment, or custom object.
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
	double getTheoreticalSaleValue(Material material, short durability, int amount, String nameOfEconomy);
	
	

	
	
	/**
	 * 
	 * @param material
	 *            The Material of an item, enchantment, or custom object.
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
	double getTruePurchasePrice(Material material, short durability, int amount, String nameOfEconomy);

	

	
	
	/**
	 * 
	 * @param material
	 *            The Material of an item, enchantment, or custom object.
	 * @param damageValue
	 *            The durability value of an item, enchantment, or custom
	 *            object.
	 * @param amount
	 *            The amount of the object.
	 * @param player
	 *            The player that is selling the object, item, or enchantment.
	 * @param nameOfEconomy
	 *            The name of the economy that the object is a part of. Put
	 *            "default" if SQL is not used or to specify the default
	 *            economy.
	 * @return The sale value of an object including all taxes, durability, and
	 *         price modifiers.
	 */
	double getTrueSaleValue(Material material, short durability, int amount, Player player, String nameOfEconomy);
	
	
	/**
	 * 
	 * @param name
	 *            The name of the HyperObject (item, enchantment, etc.)
	 * @param economy
	 *            The name of the economy the HyperObject is a part of.
	 * @param player
	 *            The name of the player trading the HyperObject.
	 * @param amount
	 *            The amount of the HyperObject
	 * @return The sale value of an object including all taxes, durability, and
	 *         price modifiers.
	 */
	double getEstimatedSaleValue(String name, String economy, String player, int amount);
	
	/**
	 * 
	 * @param name
	 *            The name of the HyperObject (item, enchantment, etc.)
	 * @param economy
	 *            The name of the economy the HyperObject is a part of.
	 * @param player
	 *            The name of the player trading the HyperObject.
	 * @param amount
	 *            The amount of the HyperObject
	 * @return The sale value of an object including all taxes, durability, and
	 *         price modifiers.
	 */
	double getTrueSaleValue(String name, String economy, String player, int amount);
	
	
	double getTruePurchasePrice(HyperObject hyperObject, EnchantmentClass enchantClass, int amount);

	double getTrueSaleValue(HyperObject hyperObject, HyperPlayer hyperPlayer, EnchantmentClass enchantClass, int amount);
	
	double getTheoreticalSaleValue(HyperObject hyperObject, EnchantmentClass enchantClass, int amount);
	
	
	
	
	public HyperObject getHyperObject(String name, String economy);
	public HyperObject getHyperObject(ItemStack stack, String economy);
	public HyperObject getHyperObject(ItemStack stack, String economy, Shop s);
	public HyperObject getHyperObject(String name, String economy, Shop s);

	

	
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
