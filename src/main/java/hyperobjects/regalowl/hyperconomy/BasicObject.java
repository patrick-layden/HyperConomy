package regalowl.hyperconomy;



public class BasicObject implements HyperObject {
	
	protected HyperConomy hc;
	protected String name;
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
	
	/*
	public BasicObject() {
		hc = HyperConomy.hc;
	}
	*/
	public BasicObject(String name, String economy, String type, double value, String isstatic, double staticprice, double stock, double median, String initiation, double startprice, double ceiling, double floor, double maxstock) {
		hc = HyperConomy.hc;
		this.name = name;
		this.economy = economy;
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
	
	public void delete() {
		hc.getEconomyManager().getEconomy(economy).removeHyperObject(name);
		String statement = "DELETE FROM hyperconomy_objects WHERE NAME = '" + name + "' AND ECONOMY = '" + this.economy + "'";
		hc.getSQLWrite().addToQueue(statement);
	}
	
	
	public int compareTo(HyperObject ho) {
		return name.compareTo(ho.getName());
	}
	
	public String getName() {
		return name;
	}
	public String getEconomy() {
		return economy;
	}
	public HyperObjectType getType() {
		return type;
	}
	public double getValue() {
		return value;
	}
	public String getIsstatic() {
		return isstatic;
	}
	public double getStaticprice() {
		return staticprice;
	}
	public double getStock() {
		return stock;
	}
	public double getTotalStock() {
		double totalStock = 0.0;
		for (Shop s:hc.getEconomyManager().getShops()) {
			if (s instanceof PlayerShop && ((PlayerShop) s).hasPlayerShopObject(this)) {
				totalStock += ((PlayerShop) s).getPlayerShopObject(this).getStock();
			}
		}
		totalStock += stock;
		return totalStock;
	}
	public double getMedian() {
		return median;
	}
	public String getInitiation() {
		return initiation;
	}
	public double getStartprice() {
		return startprice;
	}
	public double getCeiling() {
		if (ceiling <= 0 || floor > ceiling) {
			return 9999999999999.99;
		}
		return ceiling;
	}
	public double getFloor() {
		if (floor < 0 || ceiling < floor) {
			return 0.0;
		}
		return floor;
	}
	public double getMaxstock() {
		return maxstock;
	}
	

	
	
	public void setName(String name) {
		String statement = "UPDATE hyperconomy_objects SET NAME='" + name + "' WHERE NAME = '" + this.name + "' AND ECONOMY = '" + economy + "'";
		hc.getSQLWrite().addToQueue(statement);
		this.name = name;
	}
	public void setEconomy(String economy) {
		String statement = "UPDATE hyperconomy_objects SET ECONOMY='" + economy + "' WHERE NAME = '" + name + "' AND ECONOMY = '" + this.economy + "'";
		hc.getSQLWrite().addToQueue(statement);
		this.economy = economy;
	}
	public void setType(String type) {
		String statement = "UPDATE hyperconomy_objects SET TYPE='" + type + "' WHERE NAME = '" + name + "' AND ECONOMY = '" + economy + "'";
		hc.getSQLWrite().addToQueue(statement);
		this.type = HyperObjectType.fromString(type);
	}
	public void setValue(double value) {
		String statement = "UPDATE hyperconomy_objects SET VALUE='" + value + "' WHERE NAME = '" + name + "' AND ECONOMY = '" + economy + "'";
		hc.getSQLWrite().addToQueue(statement);
		this.value = value;
	}
	public void setIsstatic(String isstatic) {
		String statement = "UPDATE hyperconomy_objects SET STATIC='" + isstatic + "' WHERE NAME = '" + name + "' AND ECONOMY = '" + economy + "'";
		hc.getSQLWrite().addToQueue(statement);
		this.isstatic = isstatic;
	}
	public void setStaticprice(double staticprice) {
		String statement = "UPDATE hyperconomy_objects SET STATICPRICE='" + staticprice + "' WHERE NAME = '" + name + "' AND ECONOMY = '" + economy + "'";
		hc.getSQLWrite().addToQueue(statement);
		this.staticprice = staticprice;
	}
	public void setStock(double stock) {
		String statement = "UPDATE hyperconomy_objects SET STOCK='" + stock + "' WHERE NAME = '" + name + "' AND ECONOMY = '" + economy + "'";
		hc.getSQLWrite().addToQueue(statement);
		this.stock = stock;
	}
	public void setMedian(double median) {
		String statement = "UPDATE hyperconomy_objects SET MEDIAN='" + median + "' WHERE NAME = '" + name + "' AND ECONOMY = '" + economy + "'";
		hc.getSQLWrite().addToQueue(statement);
		this.median = median;
	}
	public void setInitiation(String initiation) {
		String statement = "UPDATE hyperconomy_objects SET INITIATION='" + initiation + "' WHERE NAME = '" + name + "' AND ECONOMY = '" + economy + "'";
		hc.getSQLWrite().addToQueue(statement);
		this.initiation = initiation;
	}
	public void setStartprice(double startprice) {
		String statement = "UPDATE hyperconomy_objects SET STARTPRICE='" + startprice + "' WHERE NAME = '" + name + "' AND ECONOMY = '" + economy + "'";
		hc.getSQLWrite().addToQueue(statement);
		this.startprice = startprice;
	}
	public void setCeiling(double ceiling) {
		String statement = "UPDATE hyperconomy_objects SET CEILING='" + ceiling + "' WHERE NAME = '" + name + "' AND ECONOMY = '" + economy + "'";
		hc.getSQLWrite().addToQueue(statement);
		this.ceiling = ceiling;
	}
	public void setFloor(double floor) {
		String statement = "UPDATE hyperconomy_objects SET FLOOR='" + floor + "' WHERE NAME = '" + name + "' AND ECONOMY = '" + economy + "'";
		hc.getSQLWrite().addToQueue(statement);
		this.floor = floor;
	}
	public void setMaxstock(double maxstock) {
		String statement = "UPDATE hyperconomy_objects SET MAXSTOCK='" + maxstock + "' WHERE NAME = '" + name + "' AND ECONOMY = '" + economy + "'";
		hc.getSQLWrite().addToQueue(statement);
		this.maxstock = maxstock;
	}
	
	
	
	
	
	
	/**
	 * 
	 * 
	 * This function returns the maximum number of items that can be sold before
	 * reaching the hyperbolic pricing curve.
	 * 
	 */
	public int getMaxInitial() {
		double medianStock = ((median * value) / startprice);
		return (int) (Math.ceil(medianStock) - stock);
	}
	
	public double getPurchaseTax(double cost) {
		double tax = 0.0;
		if (Boolean.parseBoolean(getIsstatic())) {
			tax = hc.gYH().gFC("config").getDouble("config.statictaxpercent") / 100.0;
		} else {
			if (getType() == HyperObjectType.ENCHANTMENT) {
				tax = hc.gYH().gFC("config").getDouble("config.enchanttaxpercent") / 100.0;
			} else {
				if (Boolean.parseBoolean(getInitiation())) {
					tax = hc.gYH().gFC("config").getDouble("config.initialpurchasetaxpercent") / 100.0;
				} else {
					tax = hc.gYH().gFC("config").getDouble("config.purchasetaxpercent") / 100.0;
				}
			}
		}
		return twoDecimals(cost * tax);
	}
	
	public double getSalesTaxEstimate(double value) {
		double salestax = 0;
		if (hc.gYH().gFC("config").getBoolean("config.dynamic-tax.use-dynamic-tax")) {
			return 0.0;
		} else {
			double salestaxpercent = hc.gYH().gFC("config").getDouble("config.sales-tax-percent");
			salestax = (salestaxpercent / 100) * value;
		}
		return twoDecimals(salestax);
	}
	
	protected double twoDecimals(double input) {
		int nodecimals = (int) Math.ceil((input * 100) - .5);
		double twodecimals = (double) nodecimals / 100.0;
		return twodecimals;
	}
	
	
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
	

	
	public double getCost(int amount) {
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
			return twoDecimals(cost);
		} catch (Exception e) {
			String info = "Calculation getCost() passed values name='" + getName() + "', amount='" + amount + "'";
			hc.gDB().writeError(e, info);
			double cost = 99999999;
			return cost;
		}
	}
	public double getValue(int amount) {
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
			return twoDecimals(cost);
		} catch (Exception e) {
			String info = "Calculation getTvalue() passed values name='" + getName() + "', amount='" + amount + "'";
			hc.gDB().writeError(e, info);
			double cost = 99999999;
			return cost;
		}
	}
}