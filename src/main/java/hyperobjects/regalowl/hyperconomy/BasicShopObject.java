package regalowl.hyperconomy;

import java.util.ArrayList;

import regalowl.databukkit.SQLWrite;

public class BasicShopObject implements PlayerShopObject {

	protected HyperConomy hc;
	protected SQLWrite sw;
	protected PlayerShop playerShop;
	protected HyperObject ho;
	protected double stock;
	protected double buyPrice;
	protected double sellPrice;
	protected HyperObjectStatus status;
	protected int maxStock;
	
	public BasicShopObject(PlayerShop playerShop, HyperObject ho, double stock, double buyPrice, double sellPrice, int maxStock, HyperObjectStatus status) {
		hc = HyperConomy.hc;
		sw = hc.getSQLWrite();
		this.playerShop = playerShop;
		this.ho = ho;
		this.stock = stock;
		this.buyPrice = buyPrice;
		this.sellPrice = sellPrice;
		this.maxStock = maxStock;
		this.status = status;
	}
	
	
	
	
	
	public void delete() {
		//do nothing - a player shop object will just be remade if deleted
	}
 	
	public PlayerShop getShop() {
		return playerShop;
	}
	public HyperObject getHyperObject() {
		return ho;
	}
	public double getStock() {
		return stock;
	}
	public double getBuyPrice() {
		return buyPrice;
	}
	public double getSellPrice() {
		return sellPrice;
	}
	public HyperObjectStatus getStatus() {
		return status;
	}
	public int getMaxStock() {
		return maxStock;
	}
	
	public void setHyperObject(HyperObject ho) {
		this.ho = ho;
		sw.addToQueue("UPDATE hyperconomy_shop_objects SET HYPEROBJECT='"+ho.getName()+"' WHERE SHOP='"+playerShop.getName()+"' AND HYPEROBJECT='"+ho.getName()+"'");
	}
	public void setShop(PlayerShop playerShop) {
		this.playerShop = playerShop;
		sw.addToQueue("UPDATE hyperconomy_shop_objects SET SHOP='"+playerShop.getName()+"' WHERE SHOP='"+playerShop.getName()+"' AND HYPEROBJECT='"+ho.getName()+"'");
	}
	public void setStock(double stock) {
		if (stock < 0.0) {stock = 0.0;}
		this.stock = stock;
		sw.addToQueue("UPDATE hyperconomy_shop_objects SET QUANTITY='"+stock+"' WHERE SHOP='"+playerShop.getName()+"' AND HYPEROBJECT='"+ho.getName()+"'");
	}
	public void setBuyPrice(double buyPrice) {
		this.buyPrice = buyPrice;
		sw.addToQueue("UPDATE hyperconomy_shop_objects SET BUY_PRICE='"+buyPrice+"' WHERE SHOP='"+playerShop.getName()+"' AND HYPEROBJECT='"+ho.getName()+"'");
	}
	public void setSellPrice(double sellPrice) {
		this.sellPrice = sellPrice;
		sw.addToQueue("UPDATE hyperconomy_shop_objects SET SELL_PRICE='"+sellPrice+"' WHERE SHOP='"+playerShop.getName()+"' AND HYPEROBJECT='"+ho.getName()+"'");
	}
	public void setMaxStock(int maxStock) {
		this.maxStock = maxStock;
		sw.addToQueue("UPDATE hyperconomy_shop_objects SET MAX_STOCK='"+maxStock+"' WHERE SHOP='"+playerShop.getName()+"' AND HYPEROBJECT='"+ho.getName()+"'");
	}
	public void setStatus(HyperObjectStatus status) {
		this.status = status;
		sw.addToQueue("UPDATE hyperconomy_shop_objects SET STATUS='"+status.toString()+"' WHERE SHOP='"+playerShop.getName()+"' AND HYPEROBJECT='"+ho.getName()+"'");
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	

	public double getTotalStock() {
		return ho.getTotalStock();
	}

	public String getName() {
		return ho.getName();
	}
	
	public String getDisplayName() {
		return ho.getDisplayName();
	}
	public ArrayList<String> getAliases() {
		return ho.getAliases();
	}
	public String getAliasesString() {
		return ho.getAliasesString();
	}
	public boolean hasName(String testName) {
		return ho.hasName(testName);
	}

	public String getEconomy() {
		return ho.getEconomy();
	}

	public HyperObjectType getType() {
		return ho.getType();
	}

	public double getValue() {
		return ho.getValue();
	}

	public String getIsstatic() {
		return ho.getIsstatic();
	}

	public double getStaticprice() {
		return ho.getStaticprice();
	}

	public double getMedian() {
		return ho.getMedian();
	}

	public String getInitiation() {
		return ho.getInitiation();
	}

	public double getStartprice() {
		return ho.getStartprice();
	}

	public double getCeiling() {
		return ho.getCeiling();
	}

	public double getFloor() {
		return ho.getFloor();
	}

	public double getMaxstock() {
		return ho.getMaxstock();
	}

	public void setName(String name) {}

	public void setEconomy(String economy) {}
	
	public void setDisplayName(String displayName) {}
	public void setAliases(ArrayList<String> newAliases) {}
	public void addAlias(String addAlias) {}
	public void removeAlias(String removeAlias) {}

	public void setType(String type) {}

	public void setCategory(String category) {}

	public void setValue(double value) {}

	public void setIsstatic(String isstatic) {}

	public void setStaticprice(double staticprice) {}

	public void setMedian(double median) {}

	public void setInitiation(String initiation) {}

	public void setStartprice(double startprice) {}

	public void setCeiling(double ceiling) {}

	public void setFloor(double floor) {}

	public void setMaxstock(double maxstock) {}

	public int getMaxInitial() {
		return ho.getMaxInitial();
	}

	public double getPurchaseTax(double cost) {
		return 0;
	}

	public double getSalesTaxEstimate(double value) {
		return 0;
	}



	public double getCost(int amount) {
		if (buyPrice != 0.0) {
			return buyPrice * amount;
		} else {
			return ((HyperItem)ho).getCost(amount);
		}
	}

	public double getValue(int amount) {
		if (sellPrice != 0.0) {
			return sellPrice * amount;
		} else {
			return ((HyperItem)ho).getValue(amount);
		}
	}



	public int compareTo(HyperObject ho) {
		return this.ho.compareTo(ho);
	}



	public boolean nameStartsWith(String part) {
		return ho.nameStartsWith(part);
	}

	public boolean nameContains(String part) {
		return ho.nameContains(part);
	}
	
	
	

}
