package regalowl.hyperconomy;


public interface HyperObject extends Comparable<HyperObject> {


	
	public int compareTo(HyperObject ho);
	public void delete();
	
	public String getName();
	public String getEconomy();
	public HyperObjectType getType();
	public double getValue();
	public String getIsstatic();
	public double getStaticprice();
	public double getStock();
	public double getTotalStock();
	public double getMedian();
	public String getInitiation();
	public double getStartprice();
	public double getCeiling();
	public double getFloor();
	public double getMaxstock();
	

	
	
	public void setName(String name);
	public void setEconomy(String economy);
	public void setType(String type);
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
	public double getPurchaseTax(double cost);
	public double getSalesTaxEstimate(double value);


}
