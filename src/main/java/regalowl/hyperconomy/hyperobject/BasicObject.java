package regalowl.hyperconomy.hyperobject;


import java.awt.Image;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;


import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import regalowl.databukkit.CommonFunctions;
import regalowl.hyperconomy.HyperConomy;
import regalowl.hyperconomy.account.HyperPlayer;
import regalowl.hyperconomy.hyperobject.HyperObject;
import regalowl.hyperconomy.shop.PlayerShop;
import regalowl.hyperconomy.shop.Shop;



public class BasicObject implements HyperObject {
	
	protected HyperConomy hc;
	protected CommonFunctions cf;
	protected String name;
	protected String displayName;
	protected ArrayList<String> aliases = new ArrayList<String>();
	protected String economy;
	protected HyperObjectType type;
	protected double value;
	protected String isstatic;
	protected double staticprice;
	protected double stock;
	protected double median;
	protected String initiation;
	protected double startprice;
	protected double ceiling;
	protected double floor;
	protected double maxstock;
	
	/**
	 * Constructor for BasicShopObjects
	 */
	public BasicObject() {
		hc = HyperConomy.hc;
		cf = hc.gCF();
	}
	/**
	 * Standard Constructor
	 */
	public BasicObject(String name, String economy, String displayName, String aliases, String type, double value, String isstatic, double staticprice, double stock, double median, String initiation, double startprice, double ceiling, double floor, double maxstock) {
		hc = HyperConomy.hc;
		cf = hc.gCF();
		this.name = name;
		this.economy = economy;
		this.displayName = displayName;
		ArrayList<String> tAliases = hc.gCF().explode(aliases, ",");
		for (String cAlias:tAliases) {
			this.aliases.add(cAlias);
		}
		this.type = HyperObjectType.fromString(type);
		this.value = value;
		this.isstatic = isstatic;
		this.staticprice = staticprice;
		this.stock = stock;
		this.median = median;
		this.initiation = initiation;
		this.startprice = startprice;
		this.ceiling = ceiling;
		this.floor = floor;
		this.maxstock = maxstock;
	}
	@Override
	public void delete() {
		hc.getDataManager().getEconomy(economy).removeHyperObject(name);
		String statement = "DELETE FROM hyperconomy_objects WHERE NAME = '" + name + "' AND ECONOMY = '" + this.economy + "'";
		hc.getSQLWrite().addToQueue(statement);
	}
	
	@Override
	public int compareTo(HyperObject ho) {
		return name.compareTo(ho.getName());
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
		return hc.gCF().implode(aliases, ",");
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
	public HyperObjectType getType() {
		return type;
	}
	@Override
	public double getValue() {
		return value;
	}
	@Override
	public String getIsstatic() {
		return isstatic;
	}
	@Override
	public double getStaticprice() {
		return staticprice;
	}
	@Override
	public double getStock() {
		return stock;
	}
	@Override
	public double getTotalStock() {
		double totalStock = 0.0;
		for (Shop s:hc.getDataManager().getShops()) {
			if (!(s instanceof PlayerShop)) {continue;}
			PlayerShop ps = (PlayerShop)s;
			if (!ps.hasPlayerShopObject(this)) {continue;}
			if (!ps.getEconomy().equalsIgnoreCase(economy)) {continue;}
			totalStock += ((PlayerShop) s).getPlayerShopObject(this).getStock();
		}
		totalStock += stock;
		return totalStock;
	}
	@Override
	public double getMedian() {
		return median;
	}
	@Override
	public String getInitiation() {
		return initiation;
	}
	@Override
	public double getStartprice() {
		return startprice;
	}
	@Override
	public double getCeiling() {
		if (ceiling <= 0 || floor > ceiling) {
			return 9999999999999.99;
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
	public double getMaxstock() {
		return maxstock;
	}
	

	
	@Override
	public void setName(String name) {
		String statement = "UPDATE hyperconomy_objects SET NAME='" + name + "' WHERE NAME = '" + this.name + "' AND ECONOMY = '" + economy + "'";
		hc.getSQLWrite().addToQueue(statement);
		this.name = name;
	}
	@Override
	public void setDisplayName(String displayName) {
		String statement = "UPDATE hyperconomy_objects SET DISPLAY_NAME='" + displayName + "' WHERE NAME = '" + this.name + "' AND ECONOMY = '" + economy + "'";
		hc.getSQLWrite().addToQueue(statement);
		this.displayName = displayName;
	}
	@Override
	public void setAliases(ArrayList<String> newAliases) {
		String stringAliases = hc.getCommonFunctions().implode(newAliases, ",");
		String statement = "UPDATE hyperconomy_objects SET ALIASES='" + stringAliases + "' WHERE NAME = '" + this.name + "' AND ECONOMY = '" + economy + "'";
		hc.getSQLWrite().addToQueue(statement);
		aliases.clear();
		for (String cAlias:newAliases) {
			aliases.add(cAlias);
		}
	}
	@Override
	public void addAlias(String addAlias) {
		if (aliases.contains(addAlias)) {return;}
		aliases.add(addAlias);
		String stringAliases = hc.getCommonFunctions().implode(aliases, ",");
		String statement = "UPDATE hyperconomy_objects SET ALIASES='" + stringAliases + "' WHERE NAME = '" + this.name + "' AND ECONOMY = '" + economy + "'";
		hc.getSQLWrite().addToQueue(statement);
	}
	@Override
	public void removeAlias(String removeAlias) {
		if (!aliases.contains(removeAlias)) {return;}
		aliases.remove(removeAlias);
		String stringAliases = hc.getCommonFunctions().implode(aliases, ",");
		String statement = "UPDATE hyperconomy_objects SET ALIASES='" + stringAliases + "' WHERE NAME = '" + this.name + "' AND ECONOMY = '" + economy + "'";
		hc.getSQLWrite().addToQueue(statement);
	}
	@Override
	public void setEconomy(String economy) {
		String statement = "UPDATE hyperconomy_objects SET ECONOMY='" + economy + "' WHERE NAME = '" + name + "' AND ECONOMY = '" + this.economy + "'";
		hc.getSQLWrite().addToQueue(statement);
		this.economy = economy;
	}
	@Override
	public void setType(HyperObjectType type) {
		String statement = "UPDATE hyperconomy_objects SET TYPE='" + type.toString() + "' WHERE NAME = '" + name + "' AND ECONOMY = '" + economy + "'";
		hc.getSQLWrite().addToQueue(statement);
		this.type = type;
	}
	@Override
	public void setValue(double value) {
		String statement = "UPDATE hyperconomy_objects SET VALUE='" + value + "' WHERE NAME = '" + name + "' AND ECONOMY = '" + economy + "'";
		hc.getSQLWrite().addToQueue(statement);
		this.value = value;
	}
	@Override
	public void setIsstatic(String isstatic) {
		String statement = "UPDATE hyperconomy_objects SET STATIC='" + isstatic + "' WHERE NAME = '" + name + "' AND ECONOMY = '" + economy + "'";
		hc.getSQLWrite().addToQueue(statement);
		this.isstatic = isstatic;
	}
	@Override
	public void setStaticprice(double staticprice) {
		String statement = "UPDATE hyperconomy_objects SET STATICPRICE='" + staticprice + "' WHERE NAME = '" + name + "' AND ECONOMY = '" + economy + "'";
		hc.getSQLWrite().addToQueue(statement);
		this.staticprice = staticprice;
	}
	@Override
	public void setStock(double stock) {
		if (stock < 0.0) {stock = 0.0;}
		String statement = "UPDATE hyperconomy_objects SET STOCK='" + stock + "' WHERE NAME = '" + name + "' AND ECONOMY = '" + economy + "'";
		hc.getSQLWrite().addToQueue(statement);
		this.stock = stock;
	}
	@Override
	public void setMedian(double median) {
		String statement = "UPDATE hyperconomy_objects SET MEDIAN='" + median + "' WHERE NAME = '" + name + "' AND ECONOMY = '" + economy + "'";
		hc.getSQLWrite().addToQueue(statement);
		this.median = median;
	}
	@Override
	public void setInitiation(String initiation) {
		String statement = "UPDATE hyperconomy_objects SET INITIATION='" + initiation + "' WHERE NAME = '" + name + "' AND ECONOMY = '" + economy + "'";
		hc.getSQLWrite().addToQueue(statement);
		this.initiation = initiation;
	}
	@Override
	public void setStartprice(double startprice) {
		String statement = "UPDATE hyperconomy_objects SET STARTPRICE='" + startprice + "' WHERE NAME = '" + name + "' AND ECONOMY = '" + economy + "'";
		hc.getSQLWrite().addToQueue(statement);
		this.startprice = startprice;
	}
	@Override
	public void setCeiling(double ceiling) {
		String statement = "UPDATE hyperconomy_objects SET CEILING='" + ceiling + "' WHERE NAME = '" + name + "' AND ECONOMY = '" + economy + "'";
		hc.getSQLWrite().addToQueue(statement);
		this.ceiling = ceiling;
	}
	@Override
	public void setFloor(double floor) {
		String statement = "UPDATE hyperconomy_objects SET FLOOR='" + floor + "' WHERE NAME = '" + name + "' AND ECONOMY = '" + economy + "'";
		hc.getSQLWrite().addToQueue(statement);
		this.floor = floor;
	}
	@Override
	public void setMaxstock(double maxstock) {
		String statement = "UPDATE hyperconomy_objects SET MAXSTOCK='" + maxstock + "' WHERE NAME = '" + name + "' AND ECONOMY = '" + economy + "'";
		hc.getSQLWrite().addToQueue(statement);
		this.maxstock = maxstock;
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
	public double getPurchaseTax(double cost) {
		double tax = 0.0;
		if (Boolean.parseBoolean(getIsstatic())) {
			tax = hc.gYH().gFC("config").getDouble("tax.static") / 100.0;
		} else {
			if (getType() == HyperObjectType.ENCHANTMENT) {
				tax = hc.gYH().gFC("config").getDouble("tax.enchant") / 100.0;
			} else {
				if (Boolean.parseBoolean(getInitiation())) {
					tax = hc.gYH().gFC("config").getDouble("tax.initial") / 100.0;
				} else {
					tax = hc.gYH().gFC("config").getDouble("tax.purchase") / 100.0;
				}
			}
		}
		return cf.twoDecimals(cost * tax);
	}
	@Override
	public double getSalesTaxEstimate(double value) {
		double salestax = 0;
		if (hc.gYH().gFC("config").getBoolean("tax.dynamic-tax.use-dynamic-tax")) {
			return 0.0;
		} else {
			double salestaxpercent = hc.gYH().gFC("config").getDouble("tax.sales");
			salestax = (salestaxpercent / 100) * value;
		}
		return cf.twoDecimals(salestax);
	}
	
	@Override
	public double applyCeilingFloor(double price) {
		double floor = getFloor();
		double ceiling = getCeiling();
		if (price > ceiling) {
			price = ceiling;
		} else if (price < floor) {
			price = floor;
		}
		return price;
	}
	
	@Override
	public double getSellPriceWithTax(int amount, HyperPlayer hp) {
		double price = getSellPrice(amount, hp);
		price -= hp.getSalesTax(price);
		return cf.twoDecimals(price);
	}
	
	@Override
	public double getBuyPriceWithTax(int amount) {
		double price = getBuyPrice(amount);
		price += getPurchaseTax(price);
		return cf.twoDecimals(price);
	}

	@Override
	public double getBuyPrice(int amount) {
		try {
			double cost = 0;
			boolean isstatic = Boolean.parseBoolean(getIsstatic());
			if (isstatic == false) {
				double shopstock = 0;
				double oshopstock = 0;
				double value = 0;
				double median = 0;
				shopstock = getTotalStock();
				oshopstock = shopstock;
				value = getValue();
				median = getMedian();
				shopstock = shopstock - 1;
				int counter = 0;
				while (counter < amount) {
					double price = ((median * value) / shopstock);
					price = applyCeilingFloor(price);
					shopstock = shopstock - 1;
					cost = cost + price;
					counter++;
				}
				boolean initial = Boolean.parseBoolean(getInitiation());
				if (initial == true) {
					double icost = 0;
					icost = getStartprice();
					if (cost < (icost * amount) && oshopstock > 1) {
						setInitiation("false");
					} else {
						double price = applyCeilingFloor(icost);
						cost = price * amount;
					}
				}
			} else {
				double staticcost = getStaticprice();
				double price = applyCeilingFloor(staticcost);
				cost = price * amount;
			}
			return cf.twoDecimals(cost);
		} catch (Exception e) {
			String info = "getBuyPrice() passed values name='" + getName() + "', amount='" + amount + "'";
			hc.gDB().writeError(e, info);
			double cost = 99999999;
			return cost;
		}
	}
	
	@Override
	public double getSellPrice(int amount, HyperPlayer hp) {
		return getSellPrice(amount);
	}
	
	@Override
	public double getSellPrice(int amount) {
		try {
			double cost = 0;
			int counter = 0;
			Boolean initial = false;
			initial = Boolean.parseBoolean(getInitiation());
			boolean isstatic = false;
			isstatic = Boolean.parseBoolean(getIsstatic());
			if (!isstatic) {
				double shopstock = 0;
				double value = 0;
				double median = 0;
				double icost = 0;
				shopstock = getTotalStock();
				value = getValue();
				median = getMedian();
				icost = getStartprice();
				while (counter < amount) {
					double price = ((median * value) / shopstock);
					price = applyCeilingFloor(price);
					shopstock = shopstock + 1;
					cost = cost + price;
					counter++;
				}
				if (initial == true) {
					double price = applyCeilingFloor(icost);
					cost = price * amount;
				}
			} else {
				double statprice = getStaticprice();
				double price = applyCeilingFloor(statprice);
				cost = price * amount;
			}
			return cf.twoDecimals(cost);
		} catch (Exception e) {
			String info = getName() + "', amount='" + amount + "'";
			hc.gDB().writeError(e, info);
			double cost = 99999999;
			return cost;
		}
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
		if (displayName.toLowerCase().contains(part)) {
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
	public void add(int amount, Inventory i) {}
	@Override
	public double remove(int amount, Inventory i) {return 0;}
	@Override
	public int count(Inventory inventory) {return 0;}
	@Override
	public int getAvailableSpace(Inventory inventory) {return 0;}
	@Override
	public ItemStack getItemStack() {return null;}
	@Override
	public ItemStack getItemStack(int amount) {return null;}
	@Override
	public void setData(int data) {}
	@Override
	public void setDurability(int durability) {}
	@Override
	public void setMaterial(String material) {}
	@Override
	public void setMaterial(Material material) {}
	@Override
	public String getMaterial() {return null;}
	@Override
	public Material getMaterialEnum() {return null;}
	@Override
	public int getData() {return 0;}
	@Override
	public int getDurability() {return 0;}
	@Override
	public boolean isDurable() {return false;}
	@Override
	public double getDamageMultiplier(int amount, Inventory inventory) {return 1;}
	
	
	
	//COMPOSITE ITEM METHODS
	@Override
	public ConcurrentHashMap<HyperObject, Double> getComponents() {return null;}
	
	
	
	
	
	//ENCHANTMENT METHODS
	@Override
	public double getBuyPrice(EnchantmentClass enchantClass) {return 0;}
	@Override
	public double getSellPrice(EnchantmentClass enchantClass) {return 0;}
	@Override
	public double getSellPrice(EnchantmentClass enchantClass, HyperPlayer hp) {return 0;}
	@Override
	public Enchantment getEnchantment() {return null;}
	@Override
	public int getEnchantmentLevel() {return 0;}
	@Override
	public double addEnchantment(ItemStack stack) {return 0;}
	@Override
	public double removeEnchantment(ItemStack stack) {return 0;}
	@Override
	public void setEnchantmentName(String name) {}
	@Override
	public String getEnchantmentName() {return null;}
	
	
	
	
	
	//SHOP OBJECT METHODS
	@Override
	public PlayerShop getShop() {return null;}
	@Override
	public HyperObject getHyperObject() {return null;}
	@Override
	public double getBuyPrice() {return 0;}
	@Override
	public double getSellPrice() {return 0;}
	@Override
	public int getMaxStock() {return 0;}
	@Override
	public HyperObjectStatus getStatus() {return null;}
	@Override
	public void setShop(PlayerShop playerShop) {}
	@Override
	public void setBuyPrice(double buyPrice) {}
	@Override
	public void setSellPrice(double sellPrice) {}
	@Override
	public void setMaxStock(int maxStock) {}
	@Override
	public void setStatus(HyperObjectStatus status) {}
	@Override
	public void setHyperObject(HyperObject ho) {}




}