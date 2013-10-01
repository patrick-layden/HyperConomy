package regalowl.hyperconomy;

import regalowl.databukkit.SQLWrite;

public class PlayerShopObject implements HyperObject {

	private HyperConomy hc;
	private SQLWrite sw;
	private PlayerShop playerShop;
	private HyperObject ho;
	private double stock;
	private double price;
	private HyperObjectStatus status;
	
	
	PlayerShopObject(PlayerShop playerShop, HyperObject ho, double stock, double price, HyperObjectStatus status) {
		hc = HyperConomy.hc;
		sw = hc.getSQLWrite();
		this.playerShop = playerShop;
		this.ho = ho;
		this.stock = stock;
		this.price = price;
		this.status = status;
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
	public double getPrice() {
		return price;
	}
	public HyperObjectStatus getStatus() {
		return status;
	}
	public void setHyperObject(HyperObject ho) {
		this.ho = ho;
		sw.executeSQL("UPDATE hyperconomy_shop_objects SET HYPEROBJECT='"+ho.getName()+"' WHERE SHOP='"+playerShop.getName()+"' AND HYPEROBJECT='"+ho.getName()+"'");
	}
	public void setShop(PlayerShop playerShop) {
		this.playerShop = playerShop;
		sw.executeSQL("UPDATE hyperconomy_shop_objects SET SHOP='"+playerShop.getName()+"' WHERE SHOP='"+playerShop.getName()+"' AND HYPEROBJECT='"+ho.getName()+"'");
	}
	public void setStock(double stock) {
		this.stock = stock;
		sw.executeSQL("UPDATE hyperconomy_shop_objects SET QUANTITY='"+stock+"' WHERE SHOP='"+playerShop.getName()+"' AND HYPEROBJECT='"+ho.getName()+"'");
	}
	public void setPrice(double price) {
		this.price = price;
		sw.executeSQL("UPDATE hyperconomy_shop_objects SET PRICE='"+price+"' WHERE SHOP='"+playerShop.getName()+"' AND HYPEROBJECT='"+ho.getName()+"'");
	}
	public void setStatus(HyperObjectStatus status) {
		this.status = status;
		sw.executeSQL("UPDATE hyperconomy_shop_objects SET STATUS='"+status.toString()+"' WHERE SHOP='"+playerShop.getName()+"' AND HYPEROBJECT='"+ho.getName()+"'");
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	public double getTotalStock() {
		return ho.getTotalStock();
	}
	public int compareTo(HyperObject ho) {
		return ho.compareTo(ho);
	}
	public String getName() {
		return ho.getName();
	}
	public String getEconomy() {
		return ho.getEconomy();
	}
	public HyperObjectType getType() {
		return ho.getType();
	}
	public String getCategory() {
		return ho.getCategory();
	}
	public String getMaterial() {
		return ho.getMaterial();
	}
	public int getId() {
		return ho.getId();
	}
	public int getData() {
		return ho.getData();
	}
	public int getDurability() {
		return ho.getDurability();
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

	public void setName(String name) {
		//do nothing
	}
	public void setEconomy(String economy) {
		//do nothing
	}
	public void setType(String type) {
		//do nothing
	}
	public void setCategory(String category) {
		//do nothing
	}
	public void setMaterial(String material) {
		//do nothing
	}
	public void setId(int id) {
		//do nothing
	}
	public void setData(int data) {
		//do nothing
	}
	public void setDurability(int durability) {
		//do nothing
	}
	public void setValue(double value) {
		//do nothing
	}
	public void setIsstatic(String isstatic) {
		//do nothing
	}
	public void setStaticprice(double staticprice) {
		//do nothing
	}
	public void setMedian(double median) {
		//do nothing
	}
	public void setInitiation(String initiation) {
		//do nothing
	}
	public void setStartprice(double startprice) {
		//do nothing
	}
	public void setCeiling(double ceiling) {
		//do nothing
	}
	public void setFloor(double floor) {
		//do nothing
	}
	public void setMaxstock(double maxstock) {
		//do nothing
	}
	public int getMaxInitial() {
		return ho.getMaxInitial();
	}

	public double getCost(int amount) {
		if (price != 0.0) {
			return price * amount;
		} else {
			return ho.getCost(amount);
		}
	}

	public double getCost(EnchantmentClass enchantClass) {
		if (price != 0.0) {
			return price;
		} else {
			return ho.getCost(enchantClass);
		}
	}

	public double getValue(int amount) {
		if (price != 0.0) {
			return price * amount;
		} else {
			return ho.getValue(amount);
		}
	}

	public double getValue(int amount, HyperPlayer hp) {
		if (price != 0.0) {
			return price * amount;
		} else {
			return ho.getValue(amount, hp);
		}
	}

	public double getValue(EnchantmentClass enchantClass) {
		if (price != 0.0) {
			return price;
		} else {
			return ho.getValue(enchantClass);
		}
	}

	public double getValue(EnchantmentClass enchantClass, HyperPlayer hp) {
		if (price != 0.0) {
			return price;
		} else {
			return ho.getValue(enchantClass, hp);
		}
	}

	public double getPurchaseTax(double cost) {
		return 0;
	}

	public double getSalesTaxEstimate(double value) {
		return 0;
	}

	public boolean isDurable() {
		return ho.isDurable();
	}
	
	

	
	
	
	
	

}