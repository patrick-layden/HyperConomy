package regalowl.hyperconomy;


public interface HyperObject extends Comparable<HyperObject> {


	
	public int compareTo(HyperObject ho);
	
	public String getName();
	public String getEconomy();
	public HyperObjectType getType();
	public String getCategory();
	public String getMaterial();
	public int getId();
	public int getData();
	public int getDurability();
	public double getValue();
	public String getIsstatic();
	public double getStaticprice();
	public double getStock();
	public double getMedian();
	public String getInitiation();
	public double getStartprice();
	public double getCeiling();
	public double getFloor();
	public double getMaxstock();
	

	
	
	public void setName(String name);
	public void setEconomy(String economy);
	public void setType(String type);
	public void setCategory(String category);
	public void setMaterial(String material);
	public void setId(int id);
	public void setData(int data);
	public void setDurability(int durability);
	public void setValue(double value);
	public void setIsstatic(String isstatic);
	public void setStaticprice(double staticprice);
	public void setStock(double stock);
	public void setMedian(double median);
	public void setInitiation(String initiation);
	public void setStartprice(double startprice);
	public void setCeiling(double ceiling);
	public void setFloor(double floor);
	public void setMaxstock(double maxstock);
	
	
	
	
	
	
	/**
	 * 
	 * 
	 * This function returns the maximum number of items that can be sold before
	 * reaching the hyperbolic pricing curve.
	 * 
	 */
	public int getMaxInitial();
	public double getCost(int amount);
	public double getCost(EnchantmentClass enchantClass);
	public double getValue(int amount);
	public double getValue(int amount, HyperPlayer hp);
	public double getValue(EnchantmentClass enchantClass);
	public double getValue(EnchantmentClass enchantClass, HyperPlayer hp);
	public double getPurchaseTax(double cost);
	public double getSalesTaxEstimate(double value);
	public boolean isDurable();

}
