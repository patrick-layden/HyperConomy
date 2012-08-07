package regalowl.hyperconomy;

import org.bukkit.entity.Player;

//UNDER CONSTRUCTION

public interface HyperAPI {
	
	/**
	 * 
	 * @param id The id of an item, enchantment, or custom object.
	 * @param damageValue The damage value of an item, enchantment, or custom object.
	 * @param amount The amount of the object.
	 * @param nameOfEconomy The name of the economy that the object is a part of.  Put "default" if SQL is not used or to specify the default economy.
	 * @return The purchase price of an object before tax and price modifiers.
	 */
	double getTheoreticalPurchasePrice(int id, int damageValue, int amount, String nameOfEconomy);
	
	/**
	 * 
	 * @param id The id of an item, enchantment, or custom object.
	 * @param damageValue The damage value of an item, enchantment, or custom object.
	 * @param amount The amount of the object.
	 * @param nameOfEconomy The name of the economy that the object is a part of.  Put "default" if SQL is not used or to specify the default economy.
	 * @return The sale value of an object ignoring durability, tax, and all price modifiers.
	 */
	double getTheoreticalSaleValue(int id, int damageValue, int amount, String nameOfEconomy);
	
	/**
	 * 
	 * @param id The id of an item, enchantment, or custom object.
	 * @param damageValue The damage value of an item, enchantment, or custom object.
	 * @param amount The amount of the object.
	 * @param nameOfEconomy The name of the economy that the object is a part of.  Put "default" if SQL is not used or to specify the default economy.
	 * @return The purchase price of an object including taxes and all price modifiers.
	 */
	double getTruePurchasePrice(int id, int damageValue, int amount, String nameOfEconomy);
	
	/**
	 * 
	 * @param id The id of an item, enchantment, or custom object.
	 * @param damageValue The damage value of an item, enchantment, or custom object.
	 * @param amount The amount of the object.
	 * @param player The player that is selling the object, item, or enchantment.
	 * @return The sale value of an object including all taxes, durability, and price modifiers.
	 */
	double getTrueSaleValue(int id, int damageValue, int amount, Player player);
	
	
	
	
	
	
	
	
	
	
	
	
	
	/**
	 * @deprecated The economy should be specified for future versions of the plugin.  "default" is the main economy used when SQL is not enabled.
	 * @param id
	 * @param data
	 * @param amount
	 * @return
	 */
	@Deprecated double getItemPurchasePrice(int id, int data, int amount);
	
	/**
	 * @deprecated The economy should be specified for future versions of the plugin.  "default" is the main economy used when SQL is not enabled.
	 * @param id
	 * @param data
	 * @param amount
	 * @return
	 */
	@Deprecated double getItemSaleValue(int id, int data, int amount);
}
