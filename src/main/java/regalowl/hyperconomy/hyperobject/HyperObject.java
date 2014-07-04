package regalowl.hyperconomy.hyperobject;


import java.awt.Image;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import regalowl.hyperconomy.account.HyperPlayer;
import regalowl.hyperconomy.hyperobject.HyperObjectType;
import regalowl.hyperconomy.shop.PlayerShop;


public interface HyperObject extends Comparable<HyperObject> {

	//GENERAL HYPEROBJECT METHODS
	
	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	public int compareTo(HyperObject ho);
	/**
	 * Deletes the HyperObject.
	 */
	public void delete();
	
	/**
	 * @return The name of the HyperObject.
	 */
	public String getName();
	/**
	 * @return The display name of the HyperObject
	 */
	public String getDisplayName();
	/**
	 * @return A list of all the HyperObject name aliases
	 */
	public ArrayList<String> getAliases();
	/**
	 * @return A comma delimited list of name aliases.
	 */
	public String getAliasesString();
	/**
	 * @param name
	 * @return True if the name, displayname, or an alias matches the given name, false if not.
	 */
	public boolean hasName(String name);
	/**
	 * @return The economy name of the HyperObject.
	 */
	public String getEconomy();
	/**
	 * @return The HyperObjectType enum which represents the HyperObject type for this HyperObject.
	 */
	public HyperObjectType getType();
	/**
	 * @return The value for this HyperObject.
	 */
	public double getValue();
	/**
	 * @return True if this HyperObject uses static pricing, false if not.
	 */
	public String getIsstatic();
	/**
	 * @return The static price for this HyperObject.
	 */
	public double getStaticprice();
	/**
	 * @return The stock level for this HyperObject.
	 */
	public double getStock();
	/**
	 * @return The total stock for this HyperObject.  (Includes stock which resides in PlayerShops that are a part of this HyperObject's economy)
	 */
	public double getTotalStock();
	/**
	 * @return This HyperObject's median value.
	 */
	public double getMedian();
	/**
	 * @return True if this HyperObject uses initial pricing, false if not.
	 */
	public String getInitiation();
	/**
	 * @return The HyperObject's initial price.
	 */
	public double getStartprice();
	/**
	 * @return The HyperObject's ceiling value.  (Max price)
	 */
	public double getCeiling();
	/**
	 * @return The HyperObject's floor value.  (Min price)
	 */
	public double getFloor();
	/**
	 * @return The HyperObject's maximum stock value.
	 */
	public double getMaxstock();
	/**
	 * @return The HyperObject's serialized data String.
	 */
	public String getData();
	/**Sets the HyperObject's serialized data.
	 * @param data 
	 */
	public void setData(String data);

	/**Sets the HyperObject's name to the given name.
	 * @param name 
	 */
	public void setName(String name);
	/**Sets the HyperObject's display name to the given name.
	 * @param displayName 
	 */
	public void setDisplayName(String displayName);
	/**Sets the HyperObject's aliases to the given alias List.
	 * @param aliases 
	 */
	public void setAliases(ArrayList<String> aliases);
	/**Adds the specified alias to the HyperObject.  The HyperObject can be obtained using this alias in the future.
	 * @param alias 
	 */
	public void addAlias(String alias);
	/**Removes the specified alias from the HyperObject.  The HyperObject can no longer be obtained using this alias.
	 * @param alias 
	 */
	public void removeAlias(String alias);
	/**Sets the HyperObject's economy to the given economy.
	 * @param economy
	 */
	public void setEconomy(String economy);
	/** Sets the HyperObject type.
	 * @param type
	 */
	public void setType(HyperObjectType type);
	/** Sets the HyperObject value.
	 * @param value
	 */
	public void setValue(double value);
	/** If set to true, the HyperObject will use static pricing.  It will use standard pricing if set to false.
	 * @param isstatic
	 */
	public void setIsstatic(String isstatic);
	/**Sets the static price.
	 * @param staticprice
	 */
	public void setStaticprice(double staticprice);
	/**Sets the stock level.
	 * @param stock
	 */
	public void setStock(double stock);
	/**Sets the HyperObject's median.
	 * @param median
	 */
	public void setMedian(double median);
	/**If set to true the HyperObject will use initial pricing.  It will use standard pricing if set to false.
	 * @param initiation
	 */
	public void setInitiation(String initiation);
	/**Sets the HyperObject's start price.
	 * @param startprice
	 */
	public void setStartprice(double startprice);
	/**Set's the HyperObject's ceiling value.  (Maximum price)
	 * @param ceiling
	 */
	public void setCeiling(double ceiling);
	/**Sets the HyperObject's floor value.  (Minimum price)
	 * @param floor
	 */
	public void setFloor(double floor);
	/**Sets the HyperObject's maximum stock.
	 * @param maxstock
	 */
	public void setMaxstock(double maxstock);

	
	/**Returns true if this HyperObject is a composite object, false if not.
	 * @return
	 */
	public boolean isCompositeObject();
	/**Returns true if this HyperObject is a player shop object, false if not.
	 * @return
	 */
	public boolean isShopObject();
	/**Returns true if the any of this HyperObject's names start with the specified String part, false if not.
	 * @param part
	 * @return
	 */
	public boolean nameStartsWith(String part);
	/**Returns true if the any of this HyperObject's names contain the specified String part, false if not.
	 * @param part
	 * @return
	 */
	public boolean nameContains(String part);
	/**
	 * @return Returns the maximum quantity that can be sold.  (Prevents pricing problems when switching between initial and dynamic pricing.)
	 */
	public int getMaxInitial();
	/**
	 * @return Returns 'initial', 'static', or 'dynamic'.)
	 */
	public String getStatusString();
	/**Changes the HyperObject to dynamic mode if the stock has reached the proper level.)
	 */
	public void checkInitiationStatus();
	/**Makes sure the specified price complies with this HyperObject's ceiling and floor values.  If the specified value is greater than the ceiling,
	 * it will be set to the ceiling, and if it it is less than the floor it will be set to the floor value.
	 * @param value
	 * @return
	 */
	public double applyCeilingFloor(double value);
	
	/**
	 * @param width of image (if negative width with be based on height, preserving aspect ratio)
	 * @param height of image (if negative height will be based on width, preserving aspect ratio)
	 * @return An image of this HyperObject.  If no image is available the method will return null.  If both width and height are negative, the image's
	 * original height and width will be used.
	 */
	public Image getImage(int width, int height);
	
	
	//GENERAL PRICING METHODS
	
	/**Returns the purchase tax for the specified purchase price.
	 * @param cost
	 * @return
	 */
	public double getPurchaseTax(double cost);
	/**Returns an estimated sales tax value for the given sell price.  To get an exact value dynamic tax rates must be applied which depends upon the
	 * HyperPlayer.  Use the getSalesTax(value) method in the HyperPlayer object for an exact sales tax.
	 * @param value
	 * @return
	 */
	public double getSalesTaxEstimate(double value);
	/**Returns the complete buy price including sales tax.
	 * @param amount
	 * @return
	 */
	public double getBuyPriceWithTax(int amount);
	/**Returns the buy price for the given quantity of this HyperObject.
	 * @param amount
	 * @return
	 */
	public double getBuyPrice(int amount);
	/**Returns the actual sell price (excluding tax) for this HyperObject and HyperPlayer.  The HyperPlayer is needed in case the item being sold is
	 * damaged.  The player's inventory will be checked for damaged items.
	 * @param amount
	 * @param hp
	 * @return
	 */
	public double getSellPrice(int amount, HyperPlayer hp);
	/**Returns the complete sell price for this HyperObject including sales tax. The HyperPlayer is needed in case the item being sold is
	 * damaged.  The player's inventory will be checked for damaged items.
	 * @param amount 
	 * @param hp
	 * @return
	 */
	public double getSellPriceWithTax(int amount, HyperPlayer hp);
	/**Returns a theoretical sale price excluding any damage to the item being sold etc.
	 * @param amount
	 * @return
	 */
	public double getSellPrice(int amount);
	
	
	//GENERAL ADD/REMOVE METHODS
	
	/**Adds the given quantity of this HyperObject to the HyperPlayer.  The means by which the HyperObject is added will depend on the type of HyperObject.
	 * @param amount
	 * @param hp
	 */
	public void add(int amount, HyperPlayer hp);
	/**Removes the given quantity of this HyperObject from the HyperPlayer.  The means by which the HyperObject is removed will depend on the 
	 * type of HyperObject.
	 * @param amount
	 * @param hp
	 */
	public double remove(int amount, HyperPlayer hp);
	
	
	//ITEM METHODS
	
	/**Adds the given quantity of the HyperObject to the given inventory.  (Can only be used with items.)
	 * @param amount
	 * @param i
	 */
	public void add(int amount, Inventory i);
	/**Removes the given quantity of the HyperObject from the given inventory.  (Can only be used with items.)
	 * @param amount
	 * @param i
	 */
	public double remove(int amount, Inventory i);
	/**Counts how many of this HyperObject are in the given inventory.  (Can only be used with items.)
	 * @param inventory
	 * @return
	 */
	public int count(Inventory inventory);
	/**Counts how many of this HyperObject can be placed in the given inventory. (Can only be used with items.)
	 * @param inventory
	 * @return
	 */
	public int getAvailableSpace(Inventory inventory);
	/**Returns the ItemStack representation of this HyperObject. (Can only be used with items.)
	 * @return
	 */
	public ItemStack getItemStack();
	/**Sets this HyperObject's ItemStack representation.  (Can only be used with items.)
	 * @param stack
	 */
	public void setItemStack(ItemStack stack);
	/**Returns the ItemStack representation of this HyperObject and applies the given quantity. (Can only be used with items.)
	 * @param amount
	 * @return
	 */
	public ItemStack getItemStack(int amount);
	/**
	 * @param stack
	 * @return True if this HyperObject represents the given ItemStack, false if not.
	 */
	public boolean matchesItemStack(ItemStack stack);
	/**
	 * @return True if this HyperObject is damaged, false if not. (Can only be used with items.)
	 */
	public boolean isDamaged();
	/**
	 * @return A number between 1 and 0 representing the HyperObject's durability remaining percentage.  (Can only be used with items.)
	 */
	public double getDurabilityPercent();
	/**
	 * @return True if this HyperObject has durability, false if not. (Can only be used with items.)
	 */
	public boolean isDurable();
	/** Returns a number between 0 and 1 based on the percent that the selected number of this HyperObject is damaged in the given inventory. 1 would mean 
	 * no damage, and 0 would mean completely destroyed.  This number can be used to determine the price for damaged items when multiplied by the 
	 * undamaged price. (Can only be used with items.)
	 * @param amount
	 * @param inventory
	 * @return
	 */
	public double getDamageMultiplier(int amount, Inventory inventory);
	
	
	//COMPOSITE ITEM METHODS
	
	/**
	 * @return A Map of this composite item's components with the HyperObject as the key and the recipe ratio as the value.  (Can only be used with
	 * composite items.)
	 */
	public ConcurrentHashMap<HyperObject,Double> getComponents();
	/**Sets this object's component list string.  (Can only be used with composite items.)
	 * @param components
	 */
	public void setComponents(String components);
	
	
	//ENCHANTMENT METHODS
	
	/**Returns the buy price for this Enchantment HyperObject.  (Can only be used with Enchantments.)
	 * @param enchantClass
	 * @return
	 */
	public double getBuyPrice(EnchantmentClass enchantClass);
	/**Returns the theoretical sell price for this Enchantment HyperObject ignoring any damage to the item which the enchantment is on.
	 *   (Can only be used with Enchantments.)
	 * @param enchantClass
	 * @return
	 */
	public double getSellPrice(EnchantmentClass enchantClass);
	/**Returns the sell price for this Enchantment HyperObject.  This method will take into account damage to the enchanted item. 
	 * (Can only be used with Enchantments.)
	 * @param enchantClass
	 * @return
	 */
	public double getSellPrice(EnchantmentClass enchantClass, HyperPlayer hp);
	/**Returns the sell price for this Enchantment HyperObject.  This method will take into account damage to the enchanted item. 
	 * (Can only be used with Enchantments.)
	 * @param enchantClass
	 * @return
	 */
	public double getSellPriceWithTax(EnchantmentClass enchantClass, HyperPlayer hp);
	/**
	 * @return The HyperObject in Enchantment form. (Can only be used with Enchantments.)
	 */
	public Enchantment getEnchantment();
	/**
	 * @return The HyperObject's enchantment level. (Can only be used with Enchantments.)
	 */
	public int getEnchantmentLevel();
	/** Adds this HyperObject's enchantment to the given ItemStack. (Can only be used with Enchantments.)
	 * @param stack
	 * @return 1 if successful, 0 if not.
	 */
	public double addEnchantment(ItemStack stack);
	/** Removes this HyperObject's enchantment from the given ItemStack. (Can only be used with Enchantments.)
	 * @param stack
	 * @return 1 if successful, 0 if not.
	 */
	public double removeEnchantment(ItemStack stack);
	/**
	 * @return The HyperObject's Enchantment name. (Can only be used with Enchantments.)
	 */
	public String getEnchantmentName();
	
	
	//SHOP OBJECT METHODS
	
	/**
	 * @return This PlayerShop object's Shop. (Can only be used with PlayerShop objects.)
	 */
	public PlayerShop getShop();
	/**
	 * @return This PlayerShop object's HyperObject. (Can only be used with PlayerShop objects.)
	 */
	public HyperObject getHyperObject();
	/**
	 * @return This PlayerShop object's buy price. (Can only be used with PlayerShop objects.)
	 */
	public double getBuyPrice();
	/**
	 * @return This PlayerShop object's sell price. (Can only be used with PlayerShop objects.)
	 */
	public double getSellPrice();
	/**
	 * @return This PlayerShop object's maximum stock. (Can only be used with PlayerShop objects.)
	 */
	public int getMaxStock();
	/**
	 * @return This PlayerShop object's trade status. (Can only be used with PlayerShop objects.)
	 */
	public HyperObjectStatus getStatus();
	/**Sets this PlayerShop object's Shop.
	 * @param playerShop
	 */
	public void setShop(PlayerShop playerShop);
	/**Sets this PlayerShop object's buy price.
	 * @param buyPrice
	 */
	public void setBuyPrice(double buyPrice);
	/**Sets this PlayerShop object's sell price.
	 * @param sellPrice
	 */
	public void setSellPrice(double sellPrice);
	/**Sets this PlayerShop object's maximum stock.
	 * @param maxStock
	 */
	public void setMaxStock(int maxStock);
	/**Sets this PlayerShop object's trade status.
	 * @param status
	 */
	public void setStatus(HyperObjectStatus status);
	/**Sets this PlayerShop object's HyperObject.
	 * @param ho
	 */
	public void setHyperObject(HyperObject ho);
}
