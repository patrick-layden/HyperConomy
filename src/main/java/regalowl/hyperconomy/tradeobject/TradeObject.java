package regalowl.hyperconomy.tradeobject;


import java.awt.Image;
import java.io.Serializable;
import java.util.ArrayList;




import java.util.Map;

import regalowl.hyperconomy.HyperConomy;
import regalowl.hyperconomy.account.HyperPlayer;
import regalowl.hyperconomy.inventory.HEnchantment;
import regalowl.hyperconomy.inventory.HItemStack;
import regalowl.hyperconomy.shop.PlayerShop;
import regalowl.hyperconomy.tradeobject.TradeObjectType;


public interface TradeObject extends Comparable<TradeObject>, Serializable {

	//GENERAL TradeObject METHODS
	
	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	public int compareTo(TradeObject ho);
	
	@Override
	public String toString();
	/**
	 * Deletes the TradeObject.
	 */
	public void delete();
	
	/**
	 * Links this TradeObject with a new HyperConomy object.
	 */
	public void setHyperConomy(HyperConomy hc);
	
	/**
	 * @return The name of the TradeObject.
	 */
	public String getName();
	/**
	 * @return The display name of the TradeObject
	 */
	public String getDisplayName();
	/**
	 * @return A list of all the TradeObject name aliases
	 */
	public ArrayList<String> getAliases();
	/**
	 * @return A comma delimited list of name aliases.
	 */
	public String getAliasesString();
	/**
	 * @return A list of all the categories this TradeObject is in.
	 */
	public ArrayList<String> getCategories();
	/**
	 * @return A list of all the categories this TradeObject is not in
	 */
	public ArrayList<String> getOtherCategories();
	/**
	 * @return A comma delimited list of categories.
	 */
	public String getCategoriesString();
	/**
	 * @param name
	 * @return True if the name, displayname, or an alias matches the given name, false if not.
	 */
	public boolean hasName(String name);
	/**
	 * @return The economy name of the TradeObject.
	 */
	public String getEconomy();
	/**
	 * @return The TradeObjectType enum which represents the TradeObject type for this TradeObject.
	 */
	public TradeObjectType getType();
	/**
	 * @return The value for this TradeObject.
	 */
	public double getValue();
	/**
	 * @return True if this TradeObject uses static pricing, false if not.
	 */
	public boolean isStatic();
	/**
	 * @return The static price for this TradeObject.
	 */
	public double getStaticPrice();
	/**
	 * @return The stock level for this TradeObject.
	 */
	public double getStock();
	/**
	 * @return The total stock for this TradeObject.  (Includes stock which resides in PlayerShops that are a part of this TradeObject's economy)
	 */
	public double getTotalStock();
	/**
	 * @return This TradeObject's median value.
	 */
	public double getMedian();
	/**
	 * @return True if this TradeObject uses initial pricing, false if not.
	 */
	public boolean useInitialPricing();
	/**
	 * @return The TradeObject's initial price.
	 */
	public double getStartPrice();
	/**
	 * @return The TradeObject's ceiling value.  (Max price)
	 */
	public double getCeiling();
	/**
	 * @return The TradeObject's floor value.  (Min price)
	 */
	public double getFloor();
	/**
	 * @return The TradeObject's maximum stock value.
	 */
	public double getMaxStock();
	/**
	 * @return The TradeObject's version number.
	 */
	public double getVersion();
	/**
	 * @return The TradeObject's serialized data String.
	 */
	public String getData();
	/**Sets the TradeObject's serialized data.
	 * @param data 
	 */
	public void setData(String data);

	/**Sets the TradeObject's name to the given name.
	 * @param name 
	 */
	public void setName(String name);
	/**Sets the TradeObject's display name to the given name.
	 * @param displayName 
	 */
	public void setDisplayName(String displayName);
	/**Sets the TradeObject's aliases to the given alias List.
	 * @param aliases 
	 */
	public void setAliases(ArrayList<String> aliases);
	/**Adds the specified alias to the TradeObject.  The TradeObject can be obtained using this alias in the future.
	 * @param alias 
	 */
	public void addAlias(String alias);
	/**Removes the specified alias from the TradeObject.  The TradeObject can no longer be obtained using this alias.
	 * @param alias 
	 */
	public void removeAlias(String alias);
	/**Sets the TradeObject's categories to the given category List.
	 * @param categories 
	 */
	public void setCategories(ArrayList<String> categories);
	/**Adds the TradeObject to the specified category.
	 * @param category 
	 */
	public void addCategory(String category);
	/**Removes the TradeObject from the specified category.
	 * @param category 
	 */
	public void removeCategory(String category);
	/**Returns true if the TradeObject is in the given category.
	 * @param category 
	 */
	public boolean inCategory(String category);
	/**Sets the TradeObject's economy to the given economy.
	 * @param economy
	 */
	public void setEconomy(String economy);
	/** Sets the TradeObject type.
	 * @param type
	 */
	public void setType(TradeObjectType type);
	/** Sets the TradeObject value.
	 * @param value
	 */
	public void setValue(double value);
	/** If set to true, the TradeObject will use static pricing.  It will use standard pricing if set to false.
	 * @param isstatic
	 */
	public void setStatic(boolean isStatic);
	/**Sets the static price.
	 * @param staticprice
	 */
	public void setStaticPrice(double staticprice);
	/**Sets the stock level.
	 * @param stock
	 */
	public void setStock(double stock);
	/**Sets the TradeObject's median.
	 * @param median
	 */
	public void setMedian(double median);
	/**If set to true the TradeObject will use initial pricing.  It will use standard pricing if set to false.
	 * @param initiation
	 */
	public void setUseInitialPricing(boolean initiation);
	/**Sets the TradeObject's start price.
	 * @param startprice
	 */
	public void setStartPrice(double startprice);
	/**Set's the TradeObject's ceiling value.  (Maximum price)
	 * @param ceiling
	 */
	public void setCeiling(double ceiling);
	/**Sets the TradeObject's floor value.  (Minimum price)
	 * @param floor
	 */
	public void setFloor(double floor);
	/**Sets the TradeObject's maximum stock.
	 * @param maxstock
	 */
	public void setMaxStock(double maxstock);
	/**Sets the TradeObject's version number.
	 * @param version
	 */
	public void setVersion(double version);

	
	/**Returns true if this TradeObject is a composite object, false if not.
	 * @return
	 */
	public boolean isCompositeObject();
	/**Returns true if this TradeObject is a player shop object, false if not.
	 * @return
	 */
	public boolean isShopObject();
	/**Returns true if the any of this TradeObject's names start with the specified String part, false if not.
	 * @param part
	 * @return
	 */
	public boolean nameStartsWith(String part);
	/**Returns true if the any of this TradeObject's names contain the specified String part, false if not.
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
	/**Changes the TradeObject to dynamic mode if the stock has reached the proper level.)
	 */
	public void checkInitiationStatus();
	/**Makes sure the specified price complies with this TradeObject's ceiling and floor values.  If the specified value is greater than the ceiling,
	 * it will be set to the ceiling, and if it it is less than the floor it will be set to the floor value.
	 * @param value
	 * @return
	 */
	public double applyCeilingFloor(double value, double quantity);
	
	/**
	 * @param width of image (if negative width with be based on height, preserving aspect ratio)
	 * @param height of image (if negative height will be based on width, preserving aspect ratio)
	 * @return An image of this TradeObject.  If no image is available the method will return null.  If both width and height are negative, the image's
	 * original height and width will be used.
	 */
	public Image getImage(int width, int height);
	
	/**
	 * @return A list of objects that depend on this object.  List will be empty if composite items are disabled.
	 */
	public ArrayList<TradeObject> getDependentObjects();
	
	
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
	public double getBuyPriceWithTax(double amount);
	/**Returns the buy price for the given quantity of this TradeObject.
	 * @param amount
	 * @return
	 */
	public double getBuyPrice(double amount);
	/**Returns the actual sell price (excluding tax) for this TradeObject and HyperPlayer.  The HyperPlayer is needed in case the item being sold is
	 * damaged.  The player's inventory will be checked for damaged items.
	 * @param amount
	 * @param hp
	 * @return
	 */
	public double getSellPrice(double amount, HyperPlayer hp);
	/**Returns the complete sell price for this TradeObject including sales tax. The HyperPlayer is needed in case the item being sold is
	 * damaged.  The player's inventory will be checked for damaged items.
	 * @param amount 
	 * @param hp
	 * @return
	 */
	public double getSellPriceWithTax(double amount, HyperPlayer hp);
	/**Returns a theoretical sale price excluding any damage to the item being sold etc.
	 * @param amount
	 * @return
	 */
	public double getSellPrice(double amount);
	
	
	//GENERAL ADD/REMOVE METHODS
	
	/**Adds the given quantity of this TradeObject to the HyperPlayer.  The means by which the TradeObject is added will depend on the type of TradeObject.
	 * @param amount
	 * @param hp
	 */
	public void add(int amount, HyperPlayer hp);
	/**Removes the given quantity of this TradeObject from the HyperPlayer.  The means by which the TradeObject is removed will depend on the 
	 * type of TradeObject.
	 * @param amount
	 * @param hp
	 */
	public double remove(int amount, HyperPlayer hp);
	
	
	//ITEM METHODS
	
	/**Returns the ItemStack representation of this TradeObject. (Can only be used with items.)
	 * @return
	 */
	public HItemStack getItem();
	/**Sets this TradeObject's ItemStack representation.  (Can only be used with items.)
	 * @param stack
	 */
	public void setItemStack(HItemStack stack);
	/**Returns the ItemStack representation of this TradeObject and applies the given quantity. (Can only be used with items.)
	 * @param amount
	 * @return
	 */
	public HItemStack getItemStack(int amount);
	/**
	 * @param stack
	 * @return True if this TradeObject represents the given SerializableItemStack, false if not.
	 */
	public boolean matchesItemStack(HItemStack stack);
	
	
	//COMPOSITE ITEM METHODS
	
	/**
	 * @return A Map of this composite item's components with the TradeObject as the key and the recipe ratio as the value.  (Can only be used with
	 * composite items.)
	 */
	public Map<String,Double> getComponents();
	/**
	 * @return The components in serialized string format.
	 */
	public String getCompositeData();
	/**Sets this object's component list string.  (Can only be used with composite items.)
	 * @param components
	 */
	public void setCompositeData(String components);
	/**Makes this composite object a component object.  (Doesn't delete the main tradeobject, only clears the COMPONENTS field.)
	 * 
	 */
	public void removeCompositeNature();
	
	//ENCHANTMENT METHODS
	
	/**Returns the buy price for this Enchantment TradeObject.  (Can only be used with Enchantments.)
	 * @param enchantClass
	 * @return
	 */
	public double getBuyPrice(EnchantmentClass enchantClass);
	/**Returns the theoretical sell price for this Enchantment TradeObject ignoring any damage to the item which the enchantment is on.
	 *   (Can only be used with Enchantments.)
	 * @param enchantClass
	 * @return
	 */
	public double getSellPrice(EnchantmentClass enchantClass);
	/**Returns the sell price for this Enchantment TradeObject.  This method will take into account damage to the enchanted item. 
	 * (Can only be used with Enchantments.)
	 * @param enchantClass
	 * @return
	 */
	public double getSellPrice(EnchantmentClass enchantClass, HyperPlayer hp);
	/**Returns the sell price for this Enchantment TradeObject.  This method will take into account damage to the enchanted item. 
	 * (Can only be used with Enchantments.)
	 * @param enchantClass
	 * @return
	 */
	public double getSellPriceWithTax(EnchantmentClass enchantClass, HyperPlayer hp);
	/**
	 * @return The TradeObject in Enchantment form. (Can only be used with Enchantments.)
	 */
	public HEnchantment getEnchantment();
	/**
	 * @return The TradeObject's enchantment level. (Can only be used with Enchantments.)
	 */
	public int getEnchantmentLevel();
	/**
	 * @return The TradeObject's Enchantment name. (Can only be used with Enchantments.)
	 */
	public String getEnchantmentName();
	/**
	 * @param stack
	 * @return True if this TradeObject represents the given SerializableItemStack, false if not.
	 */
	public boolean matchesEnchantment(HEnchantment enchant);
	
	
	//SHOP OBJECT METHODS
	
	/**
	 * @return This PlayerShop object's Shop. (Can only be used with PlayerShop objects.)
	 */
	public PlayerShop getShopObjectShop();
	/**
	 * @return This PlayerShop object's TradeObject. (Can only be used with PlayerShop objects.)
	 */
	public TradeObject getParentTradeObject();
	/**
	 * @return This PlayerShop object's buy price. (Can only be used with PlayerShop objects.)
	 */
	public double getShopObjectBuyPrice();
	/**
	 * @return This PlayerShop object's sell price. (Can only be used with PlayerShop objects.)
	 */
	public double getShopObjectSellPrice();
	/**
	 * @return This PlayerShop object's maximum stock. (Can only be used with PlayerShop objects.)
	 */
	public int getShopObjectMaxStock();
	/**
	 * @return This PlayerShop object's trade status. (Can only be used with PlayerShop objects.)
	 */
	public TradeObjectStatus getShopObjectStatus();
	/**
	 * @return Returns true if this playershop object is set to use its economy's stock levels.
	 */
	public boolean useEconomyStock();
	/**Sets this PlayerShop object's Shop.
	 * @param playerShop
	 */
	public void setShopObjectShop(PlayerShop playerShop);
	/**Sets this PlayerShop object's Shop.
	 * @param playerShop
	 */
	public void setShopObjectShop(String playerShop);
	/**Sets this PlayerShop object's buy price.
	 * @param buyPrice
	 */
	public void setShopObjectBuyPrice(double buyPrice);
	/**Sets this PlayerShop object's sell price.
	 * @param sellPrice
	 */
	public void setShopObjectSellPrice(double sellPrice);
	/**Sets this PlayerShop object's maximum stock.
	 * @param maxStock
	 */
	public void setShopObjectMaxStock(int maxStock);
	/**Sets this PlayerShop object's trade status.
	 * @param status
	 */
	public void setShopObjectStatus(TradeObjectStatus status);
	/**Sets this PlayerShop object's TradeObject.
	 * @param ho
	 */
	public void setParentTradeObject(TradeObject to);
	/**Sets this PlayerShop object's TradeObject.
	 * @param ho
	 */
	public void setParentTradeObject(String to);
	/**Sets this PlayerShop object to use or not use its economy's stock levels.
	 * @param ho
	 */
	public void setUseEconomyStock(boolean state);
	/**
	 * Saves this tradeobject to the database, replacing all current data in the database with the data in RAM.  Does not trigger any events.  Should only be used under
	 * special circumstances such as remote GUI updates.
	 */
	public void save();
}
