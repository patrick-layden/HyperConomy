package regalowl.hyperconomy.tradeobject;


import java.awt.Image;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import regalowl.simpledatalib.CommonFunctions;
import regalowl.simpledatalib.sql.SQLWrite;
import regalowl.hyperconomy.HyperConomy;
import regalowl.hyperconomy.HyperEconomy;
import regalowl.hyperconomy.account.HyperPlayer;
import regalowl.hyperconomy.display.FrameShopHandler;
import regalowl.hyperconomy.display.InfoSign;
import regalowl.hyperconomy.display.ItemDisplay;
import regalowl.hyperconomy.event.TradeObjectModificationEvent;
import regalowl.hyperconomy.event.TradeObjectModificationType;
import regalowl.hyperconomy.inventory.HEnchantment;
import regalowl.hyperconomy.inventory.HItemStack;
import regalowl.hyperconomy.shop.PlayerShop;
import regalowl.hyperconomy.shop.Shop;
import regalowl.hyperconomy.tradeobject.TradeObject;



public class BasicTradeObject implements TradeObject {
	
	protected transient HyperConomy hc;
	protected transient SQLWrite sw;
	
	private static final long serialVersionUID = 3220675400415233555L;
	protected String name;
	protected String displayName;
	protected ArrayList<String> aliases = new ArrayList<String>();
	protected ArrayList<String> categories = new ArrayList<String>();
	protected String economy;
	protected TradeObjectType type;
	protected double value;
	protected boolean isstatic;
	protected double staticprice;
	protected double stock;
	protected double median;
	protected boolean initiation;
	protected double startprice;
	protected double ceiling;
	protected double floor;
	protected double maxstock;
	protected String objectData;
	protected String compositeData;
	protected ConcurrentHashMap<String,Double> components = new ConcurrentHashMap<String,Double>();

	public BasicTradeObject(HyperConomy hc) {
		this.hc = hc;
		this.sw = hc.getSQLWrite();
	}
	
	/**
	 * Standard Constructor
	 */
	public BasicTradeObject(HyperConomy hc, HyperEconomy he, String name, String economy, String displayName, String aliases, String categories, String type, double value, String isstatic, double staticprice, double stock, double median, String initiation, double startprice, double ceiling, double floor, double maxstock, String compositeData, String objectData) {
		this.hc = hc;
		this.sw = hc.getSQLWrite();
		this.name = name;
		this.economy = economy;
		this.displayName = displayName;
		this.aliases = CommonFunctions.explode(aliases);
		this.categories = CommonFunctions.explode(categories);
		this.type = TradeObjectType.fromString(type);
		this.value = value;
		this.isstatic = Boolean.parseBoolean(isstatic);
		this.staticprice = staticprice;
		this.stock = stock;
		this.median = median;
		this.initiation = Boolean.parseBoolean(initiation);
		this.startprice = startprice;
		this.ceiling = ceiling;
		this.floor = floor;
		this.maxstock = maxstock;
		this.objectData = objectData;
		this.compositeData = compositeData;
	}
	
	@Override
	public void setHyperConomy(HyperConomy hc) {
		this.hc = hc;
		this.sw = hc.getSQLWrite();
	}
	
	@Override
	public void save() {
		String statement = "DELETE FROM hyperconomy_objects WHERE NAME = '" + name + "' AND ECONOMY = '" + this.economy + "'";
		sw.addToQueue(statement);
		HashMap<String,String> values = new HashMap<String,String>();
		values.put("NAME", getName());
		values.put("ECONOMY", getEconomy());
		values.put("DISPLAY_NAME", getDisplayName());
		values.put("ALIASES", getAliasesString());
		values.put("CATEGORIES", getCategoriesString());
		values.put("TYPE", getType().toString());
		values.put("VALUE", getValue()+"");
		values.put("STATIC", isStatic()+"");
		values.put("STATICPRICE", getStaticPrice()+"");
		values.put("STOCK", getStock()+"");
		values.put("MEDIAN", getMedian()+"");
		values.put("INITIATION", useInitialPricing()+"");
		values.put("STARTPRICE",getStartPrice()+"");
		values.put("CEILING", getCeiling()+"");
		values.put("FLOOR", getFloor()+"");
		values.put("MAXSTOCK", getMaxStock()+"");
		values.put("COMPONENTS", getCompositeData());
		values.put("DATA", getData());
		hc.getSQLWrite().performInsert("hyperconomy_objects", values);
	}
	
	@Override
	public void delete() {
		HyperEconomy he = hc.getDataManager().getEconomy(economy);
		for (Shop s:hc.getHyperShopManager().getShops()) {
			if (!s.getEconomy().equals(economy)) continue;
			s.removeTradeObject(this);
		}
		for (InfoSign iSign:hc.getInfoSignHandler().getInfoSigns()) {
			if (!iSign.getEconomy().equals(economy)) continue;
			if (iSign.getTradeObject().equals(this)) {
				iSign.deleteSign();
			}
		}
		if (economy.equalsIgnoreCase("default")) {
			for (ItemDisplay disp:hc.getItemDisplay().getDisplays()) {
				if (disp.getHyperObject().equals(this)) disp.delete();
			}
		}
		FrameShopHandler fsh = hc.getFrameShopHandler();
		if (fsh != null) fsh.removeFrameShops(this); // null when using GUIConnector
		int compositesRemoved = 0;
		for (TradeObject to:getDependentObjects()) {
			to.removeCompositeNature();
			compositesRemoved++;
		}
		he.removeObject(name);
		String statement = "DELETE FROM hyperconomy_objects WHERE NAME = '" + name + "' AND ECONOMY = '" + this.economy + "'";
		sw.addToQueue(statement);
		fireModificationEvent(TradeObjectModificationType.DELETED);
		if (compositesRemoved > 0) hc.restart();
	}
	
	@Override
	public ArrayList<TradeObject> getDependentObjects() {
		ArrayList<TradeObject> dependencies = new ArrayList<TradeObject>();
		HyperEconomy he = hc.getDataManager().getEconomy(economy);
		if (!he.useComposites()) return dependencies;
		for (TradeObject to:he.getTradeObjects()) {
			if (!to.isCompositeObject()) continue;
			for (String dString: to.getComponents().keySet()) {
				if (dString.equalsIgnoreCase(name)) dependencies.add(to);
			}
		}
		return dependencies;
	}
	
	@Override
	public String toString() {
		return displayName;
	}
	
	@Override
	public int compareTo(TradeObject ho) {
		if (ho == null) return 1;
		String d1 = (getDisplayName() == null) ? getName():getDisplayName();
		String d2 = (ho.getDisplayName() == null) ? ho.getName():ho.getDisplayName();
		if (d1 == null) return -1;
		if (d2 == null) return 1;
		return d1.compareTo(d2);
	}
	@Override
	public String getName() {
		return name;
	}
	@Override
	public String getDisplayName() {
		if (displayName != null) {
			return displayName;
		} else {
			return name;
		}
	}
	@Override
	public ArrayList<String> getAliases() {
		return new ArrayList<String>(aliases);
	}
	@Override
	public String getAliasesString() {
		return CommonFunctions.implode(aliases);
	}
	@Override
	public ArrayList<String> getCategories() {
		return new ArrayList<String>(categories);
	}
	@Override
	public ArrayList<String> getOtherCategories() {
		ArrayList<String> otherCats = hc.getDataManager().getCategories();
		for (String category:categories) {
			if (otherCats.contains(category)) {
				otherCats.remove(category);
			}
		}
		return otherCats;
	}
	@Override
	public String getCategoriesString() {
		return CommonFunctions.implode(categories);
	}
	@Override
	public boolean hasName(String testName) {
		if (name.equalsIgnoreCase(testName)) {
			return true;
		}
		if (displayName.equalsIgnoreCase(testName)) {
			return true;
		}
		for (int i = 0; i < aliases.size(); i++) {
			String alias = aliases.get(i);
			if (alias.equalsIgnoreCase(testName)) {
				return true;
			}
		}
		return false;
	}
	@Override
	public String getEconomy() {
		return economy;
	}
	@Override
	public TradeObjectType getType() {
		return type;
	}
	@Override
	public double getValue() {
		return value;
	}
	@Override
	public boolean isStatic() {
		return isstatic;
	}
	@Override
	public double getStaticPrice() {
		return staticprice;
	}
	@Override
	public double getStock() {
		return stock;
	}
	@Override
	public double getTotalStock() {
		double totalStock = 0.0;
		for (Shop s:hc.getHyperShopManager().getShops()) {
			if (!(s instanceof PlayerShop)) {continue;}
			PlayerShop ps = (PlayerShop)s;
			if (ps.getUseEconomyStock()) continue;
			if (!ps.hasPlayerShopObject(this)) {continue;}
			if (!ps.getEconomy().equalsIgnoreCase(economy)) {continue;}
			totalStock += ps.getPlayerShopObject(this).getStock();
		}
		totalStock += stock;
		return totalStock;
	}
	@Override
	public double getMedian() {
		return median;
	}
	@Override
	public boolean useInitialPricing() {
		return initiation;
	}
	@Override
	public double getStartPrice() {
		return startprice;
	}
	@Override
	public double getCeiling() {
		if (ceiling <= 0 || floor > ceiling) {
			return 1000000000000000.0;
		}
		return ceiling;
	}
	@Override
	public double getFloor() {
		if (floor < 0 || ceiling < floor) {
			return 0.0;
		}
		return floor;
	}
	@Override
	public double getMaxStock() {
		return maxstock;
	}
	@Override
	public ConcurrentHashMap<String,Double> getComponents() {
		return components;
	}
	@Override
	public String getCompositeData() {
		return compositeData;
	}
	@Override
	public String getData() {
		return objectData;
	}
	
	
	
	@Override
	public void setName(String name) {
		hc.getDataManager().getEconomy(economy).removeObject(this);
		String statement = "UPDATE hyperconomy_objects SET NAME='" + name + "' WHERE NAME = '" + this.name + "' AND ECONOMY = '" + economy + "'";
		sw.addToQueue(statement);
		this.name = name;
		hc.getDataManager().getEconomy(economy).addObject(this);
		fireModificationEvent(TradeObjectModificationType.NAME);
	}
	@Override
	public void setDisplayName(String displayName) {
		hc.getDataManager().getEconomy(economy).removeObject(this);
		String statement = "UPDATE hyperconomy_objects SET DISPLAY_NAME='" + displayName + "' WHERE NAME = '" + this.name + "' AND ECONOMY = '" + economy + "'";
		sw.addToQueue(statement);
		this.displayName = displayName;
		hc.getDataManager().getEconomy(economy).addObject(this);
		fireModificationEvent(TradeObjectModificationType.DISPLAY_NAME);
	}
	@Override
	public void setAliases(ArrayList<String> newAliases) {
		hc.getDataManager().getEconomy(economy).removeObject(this);
		aliases.clear();
		for (String cAlias:newAliases) {
			aliases.add(cAlias);
		}
		String statement = "UPDATE hyperconomy_objects SET ALIASES='" + getAliasesString() + "' WHERE NAME = '" + this.name + "' AND ECONOMY = '" + economy + "'";
		sw.addToQueue(statement);
		hc.getDataManager().getEconomy(economy).addObject(this);
		fireModificationEvent(TradeObjectModificationType.ALIASES);
	}
	@Override
	public void addAlias(String addAlias) {
		if (aliases.contains(addAlias)) {return;}
		hc.getDataManager().getEconomy(economy).removeObject(this);
		aliases.add(addAlias);
		String statement = "UPDATE hyperconomy_objects SET ALIASES='" + getAliasesString() + "' WHERE NAME = '" + this.name + "' AND ECONOMY = '" + economy + "'";
		sw.addToQueue(statement);
		hc.getDataManager().getEconomy(economy).addObject(this);
		fireModificationEvent(TradeObjectModificationType.ALIASES);
	}
	@Override
	public void removeAlias(String removeAlias) {
		if (!aliases.contains(removeAlias)) {return;}
		hc.getDataManager().getEconomy(economy).removeObject(this);
		aliases.remove(removeAlias);
		String statement = "UPDATE hyperconomy_objects SET ALIASES='" + getAliasesString() + "' WHERE NAME = '" + this.name + "' AND ECONOMY = '" + economy + "'";
		sw.addToQueue(statement);
		hc.getDataManager().getEconomy(economy).addObject(this);
		fireModificationEvent(TradeObjectModificationType.ALIASES);
	}
	@Override
	public void setCategories(ArrayList<String> newCategories) {
		categories.clear();
		for (String cCat:newCategories) {
			categories.add(cCat);
		}
		String statement = "UPDATE hyperconomy_objects SET CATEGORIES='" + getCategoriesString() + "' WHERE NAME = '" + this.name + "' AND ECONOMY = '" + economy + "'";
		sw.addToQueue(statement);
		fireModificationEvent(TradeObjectModificationType.CATEGORIES);
	}
	@Override
	public void addCategory(String category) {
		if (categories.contains(category)) {return;}
		categories.add(category);
		String statement = "UPDATE hyperconomy_objects SET CATEGORIES='" + getCategoriesString() + "' WHERE NAME = '" + this.name + "' AND ECONOMY = '" + economy + "'";
		sw.addToQueue(statement);
		fireModificationEvent(TradeObjectModificationType.CATEGORIES);
	}
	@Override
	public void removeCategory(String category) {
		if (!categories.contains(category)) {return;}
		categories.remove(category);
		String statement = "UPDATE hyperconomy_objects SET CATEGORIES='" + getCategoriesString() + "' WHERE NAME = '" + this.name + "' AND ECONOMY = '" + economy + "'";
		sw.addToQueue(statement);
		fireModificationEvent(TradeObjectModificationType.CATEGORIES);
	}
	@Override
	public boolean inCategory(String category) {
		return categories.contains(category);
	}
	@Override
	public void setEconomy(String economy) {
		String statement = "UPDATE hyperconomy_objects SET ECONOMY='" + economy + "' WHERE NAME = '" + name + "' AND ECONOMY = '" + this.economy + "'";
		sw.addToQueue(statement);
		this.economy = economy;
		fireModificationEvent(TradeObjectModificationType.ECONOMY);
	}
	@Override
	public void setType(TradeObjectType type) {
		String statement = "UPDATE hyperconomy_objects SET TYPE='" + type.toString() + "' WHERE NAME = '" + name + "' AND ECONOMY = '" + economy + "'";
		sw.addToQueue(statement);
		this.type = type;
		fireModificationEvent(TradeObjectModificationType.TYPE);
	}
	@Override
	public void setValue(double value) {
		String statement = "UPDATE hyperconomy_objects SET VALUE='" + value + "' WHERE NAME = '" + name + "' AND ECONOMY = '" + economy + "'";
		sw.addToQueue(statement);
		this.value = value;
		fireModificationEvent(TradeObjectModificationType.VALUE);
	}
	@Override
	public void setStatic(boolean isStatic) {
		String statement = "UPDATE hyperconomy_objects SET STATIC='" + isStatic + "' WHERE NAME = '" + name + "' AND ECONOMY = '" + economy + "'";
		sw.addToQueue(statement);
		this.isstatic = isStatic;
		fireModificationEvent(TradeObjectModificationType.USE_STATIC_PRICE);
	}
	@Override
	public void setStaticPrice(double staticprice) {
		String statement = "UPDATE hyperconomy_objects SET STATICPRICE='" + staticprice + "' WHERE NAME = '" + name + "' AND ECONOMY = '" + economy + "'";
		sw.addToQueue(statement);
		this.staticprice = staticprice;
		fireModificationEvent(TradeObjectModificationType.STATIC_PRICE);
	}
	@Override
	public void setStock(double stock) {
		if (stock < 0.0) {stock = 0.0;}
		String statement = "UPDATE hyperconomy_objects SET STOCK='" + stock + "' WHERE NAME = '" + name + "' AND ECONOMY = '" + economy + "'";
		sw.addToQueue(statement);
		this.stock = stock;
		fireModificationEvent(TradeObjectModificationType.STOCK);
	}
	@Override
	public void setMedian(double median) {
		String statement = "UPDATE hyperconomy_objects SET MEDIAN='" + median + "' WHERE NAME = '" + name + "' AND ECONOMY = '" + economy + "'";
		sw.addToQueue(statement);
		this.median = median;
		fireModificationEvent(TradeObjectModificationType.MEDIAN);
	}
	@Override
	public void setUseInitialPricing(boolean initiation) {
		String statement = "UPDATE hyperconomy_objects SET INITIATION='" + initiation + "' WHERE NAME = '" + name + "' AND ECONOMY = '" + economy + "'";
		sw.addToQueue(statement);
		this.initiation = initiation;
		fireModificationEvent(TradeObjectModificationType.USE_INITIAL_PRICING);
	}
	@Override
	public void setStartPrice(double startprice) {
		String statement = "UPDATE hyperconomy_objects SET STARTPRICE='" + startprice + "' WHERE NAME = '" + name + "' AND ECONOMY = '" + economy + "'";
		sw.addToQueue(statement);
		this.startprice = startprice;
		fireModificationEvent(TradeObjectModificationType.START_PRICE);
	}
	@Override
	public void setCeiling(double ceiling) {
		String statement = "UPDATE hyperconomy_objects SET CEILING='" + ceiling + "' WHERE NAME = '" + name + "' AND ECONOMY = '" + economy + "'";
		sw.addToQueue(statement);
		this.ceiling = ceiling;
		fireModificationEvent(TradeObjectModificationType.CEILING);
	}
	@Override
	public void setFloor(double floor) {
		String statement = "UPDATE hyperconomy_objects SET FLOOR='" + floor + "' WHERE NAME = '" + name + "' AND ECONOMY = '" + economy + "'";
		sw.addToQueue(statement);
		this.floor = floor;
		fireModificationEvent(TradeObjectModificationType.FLOOR);
	}
	@Override
	public void setMaxStock(double maxstock) {
		String statement = "UPDATE hyperconomy_objects SET MAXSTOCK='" + maxstock + "' WHERE NAME = '" + name + "' AND ECONOMY = '" + economy + "'";
		sw.addToQueue(statement);
		this.maxstock = maxstock;
		fireModificationEvent(TradeObjectModificationType.MAX_STOCK);
	}
	@Override
	public void setCompositeData(String compositeData) {
		this.compositeData = compositeData;
		String statement = "UPDATE hyperconomy_objects SET COMPONENTS='" + compositeData + "' WHERE NAME = '"+this.name+"' AND ECONOMY = '"+this.economy+"' ";
		hc.getSQLWrite().addToQueue(statement);
		this.components.clear();
		HashMap<String,String> tempComponents = CommonFunctions.explodeMap(compositeData);
		for (Map.Entry<String,String> entry : tempComponents.entrySet()) {
		    String oname = entry.getKey();
		    String amountString = entry.getValue();
		    double amount = 0.0;
		    if (amountString.contains("/")) {
				int top = Integer.parseInt(amountString.substring(0, amountString.indexOf("/")));
				int bottom = Integer.parseInt(amountString.substring(amountString.indexOf("/") + 1, amountString.length()));
				amount = ((double)top/(double)bottom);
		    } else {
		    	int number = Integer.parseInt(amountString);
		    	amount = (double)number;
		    }
		    TradeObject ho = hc.getDataManager().getEconomy(economy).getTradeObject(oname);
		    this.components.put(ho.getName(), amount);
		}
		fireModificationEvent(TradeObjectModificationType.COMPOSITE_DATA);
	}
	@Override
	public void setData(String data) {
		this.objectData = data;
		String statement = "UPDATE hyperconomy_objects SET DATA='" + data + "' WHERE NAME = '" + this.name + "' AND ECONOMY = '" + economy + "'";
		sw.addToQueue(statement);
		fireModificationEvent(TradeObjectModificationType.DATA);
	}
	

	@Override
	public int getMaxInitial() {
		double medianStock = ((median * value) / startprice);
		int maxInitial = (int) (Math.ceil(medianStock) - stock);
		if (maxInitial < 0) {
			maxInitial = 0;
		}
		return maxInitial;
	}
	
	@Override
	public void checkInitiationStatus() {
		if (((getMedian() * getValue()) / getTotalStock()) <= getStartPrice()) {
			setUseInitialPricing(false);
		}
	}
	
	@Override
	public String getStatusString() {
		String status = "dynamic";
		if (useInitialPricing()) {
			status = "initial";
		} else if (isstatic) {
			status = "static";
		}
		return status;
	}
	

	@Override
	public double getPurchaseTax(double cost) {
		double tax = 0.0;
		if (isstatic) {
			tax = hc.getConf().getDouble("tax.static") / 100.0;
		} else {
			if (getType() == TradeObjectType.ENCHANTMENT) {
				tax = hc.getConf().getDouble("tax.enchant") / 100.0;
			} else {
				if (useInitialPricing()) {
					tax = hc.getConf().getDouble("tax.initial") / 100.0;
				} else {
					tax = hc.getConf().getDouble("tax.purchase") / 100.0;
				}
			}
		}
		return cost * tax;
	}
	@Override
	public double getSalesTaxEstimate(double value) {
		double salestax = 0;
		if (hc.getConf().getBoolean("tax.dynamic.enable")) {
			return 0.0;
		} else {
			double salestaxpercent = hc.getConf().getDouble("tax.sales");
			salestax = (salestaxpercent / 100) * value;
		}
		return salestax;
	}
	
	@Override
	public double applyCeilingFloor(double price, double quantity) {
		double floor = getFloor() * quantity;
		double ceiling = getCeiling() * quantity;
		if (price > ceiling) {
			price = ceiling;
		} else if (price < floor) {
			price = floor;
		}
		return price;
	}
	
	@Override
	public double getSellPriceWithTax(double amount, HyperPlayer hp) {
		double price = getSellPrice(amount, hp);
		price -= hp.getSalesTax(price);
		return price;
	}
	
	@Override
	public double getBuyPriceWithTax(double amount) {
		double price = getBuyPrice(amount);
		price += getPurchaseTax(price);
		return price;
	}
	
	@Override
	public double getSellPrice(double amount) {
		try {
			double totalPrice = 0;
			if (isstatic) {
				totalPrice = getStaticPrice() * amount;
			} else {
				if (getTotalStock() <= 0) {
					totalPrice = Math.pow(10, 21);
				} else {
					totalPrice = (Math.log(getTotalStock() + amount) - Math.log(getTotalStock())) * getMedian() * getValue();
				}
				if (useInitialPricing() && totalPrice > (getStartPrice() * amount)) {
					totalPrice = getStartPrice() * amount;
				}
			}
			return applyCeilingFloor(totalPrice, amount);
		} catch (Exception e) {
			hc.gSDL().getErrorWriter().writeError(e);
			return Math.pow(10, 21);
		}
	}
	

	@Override
	public double getBuyPrice(double amount) {
		try {
			double totalPrice = 0;
			if (isstatic) {
				totalPrice = getStaticPrice() * amount;
			} else {
				if (getTotalStock() - amount <= 0) {
					totalPrice = Math.pow(10, 21);
				} else {
					totalPrice = (Math.log(getTotalStock()) - Math.log(getTotalStock() - amount)) * getMedian() * getValue();
				}
				if (useInitialPricing() && totalPrice > (getStartPrice() * amount)) {
					totalPrice = getStartPrice() * amount;
				}
			}
			return applyCeilingFloor(totalPrice, amount);
		} catch (Exception e) {
			hc.gSDL().getErrorWriter().writeError(e);
			return 0;
		}
	}
	
	@Override
	public double getSellPrice(double amount, HyperPlayer hp) {
		return getSellPrice(amount);
	}
	
	@Override
	public boolean nameStartsWith(String part) {
		part = part.toLowerCase();
		if (displayName.toLowerCase().startsWith(part)) {
			return true;
		}
		if (name.toLowerCase().startsWith(part)) {
			return true;
		}
		for (String alias:aliases) {
			if (alias.toLowerCase().startsWith(part)) {
				return true;
			}
		}
		return false;
	}
	@Override
	public boolean nameContains(String part) {
		part = part.toLowerCase();
		if (displayName != null && displayName.toLowerCase().contains(part)) {
			return true;
		}
		if (name.toLowerCase().contains(part)) {
			return true;
		}
		for (String alias:aliases) {
			if (alias.toLowerCase().contains(part)) {
				return true;
			}
		}
		return false;
	}
	

	
	@Override
	public boolean isShopObject() {return false;}
	@Override
	public boolean isCompositeObject() {return false;}

	
	//SUBCLASS METHODS
	
	@Override
    public Image getImage(int width, int height) {return null;}
	
	//GENERAL ADD AND REMOVE OBJECT METHODS
	@Override
	public void add(int amount, HyperPlayer hp) {}
	@Override
	public double remove(int amount, HyperPlayer hp) {return 0;}

	
	//ITEM METHODS
	@Override
	public HItemStack getItem() {
		return new HItemStack(objectData);
	}
	@Override
	public HItemStack getItemStack(int amount) {
		HItemStack sis = getItem();
		sis.setAmount(amount);
		return sis;
	}
	@Override
	public void setItemStack(HItemStack stack) {
		setData(stack.serialize());
	}
	@Override
	public boolean matchesItemStack(HItemStack stack) {
		if (stack == null) {return false;}
		return stack.isSimilarTo(getItem());
	}
	
	
	//COMPOSITE ITEM METHODS

	@Override
	public void removeCompositeNature() {
		for (TradeObject to:getDependentObjects()) {
			to.removeCompositeNature();
		}
		String statement = "UPDATE hyperconomy_objects SET COMPONENTS = '' WHERE NAME = '" + this.name + "' AND ECONOMY = '" + this.economy + "'";
		hc.getSQLWrite().addToQueue(statement);
	}
	
	
	
	
	//ENCHANTMENT METHODS
	@Override
	public double getBuyPrice(EnchantmentClass enchantClass) {return 0;}
	@Override
	public double getSellPrice(EnchantmentClass enchantClass) {return 0;}
	@Override
	public double getSellPrice(EnchantmentClass enchantClass, HyperPlayer hp) {return 0;}
	@Override
	public double getSellPriceWithTax(EnchantmentClass enchantClass, HyperPlayer hp) {return 0;}
	@Override
	public HEnchantment getEnchantment() {return null;}
	@Override
	public int getEnchantmentLevel() {return 0;}
	@Override
	public String getEnchantmentName() {return null;}
	@Override
	public boolean matchesEnchantment(HEnchantment enchant) {return false;}
	
	
	
	
	//SHOP OBJECT METHODS
	@Override
	public PlayerShop getShopObjectShop() {return null;}
	@Override
	public TradeObject getParentTradeObject() {return null;}
	@Override
	public double getShopObjectBuyPrice() {return 0;}
	@Override
	public double getShopObjectSellPrice() {return 0;}
	@Override
	public int getShopObjectMaxStock() {return 0;}
	@Override
	public TradeObjectStatus getShopObjectStatus() {return null;}
	@Override
	public boolean useEconomyStock() {return true;}
	@Override
	public void setShopObjectShop(PlayerShop playerShop) {}
	@Override
	public void setShopObjectShop(String playerShop) {}
	@Override
	public void setShopObjectBuyPrice(double buyPrice) {}
	@Override
	public void setShopObjectSellPrice(double sellPrice) {}
	@Override
	public void setShopObjectMaxStock(int maxStock) {}
	@Override
	public void setShopObjectStatus(TradeObjectStatus status) {}
	@Override
	public void setParentTradeObject(TradeObject ho) {}
	@Override
	public void setParentTradeObject(String ho) {}
	@Override
	public void setUseEconomyStock(boolean state) {}


	protected void fireModificationEvent(TradeObjectModificationType type) {
		if (hc != null) hc.getHyperEventHandler().fireEvent(new TradeObjectModificationEvent(this, type));
	}









}