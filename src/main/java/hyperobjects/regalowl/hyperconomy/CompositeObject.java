package regalowl.hyperconomy;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;

public class CompositeObject implements HyperObject {
	private HyperConomy hc;
	
	
	private String name;
	private String economy;
	private HyperObjectType type;
	private String category;
	private String material;
	private int id;
	private int data;
	private int durability;

	
	private FileConfiguration composites;
	private SerializeArrayList sal;
	
	private ConcurrentHashMap<HyperObject,Double> components = new ConcurrentHashMap<HyperObject,Double>();
	
	
	public CompositeObject(String name, String economy) {
		hc = HyperConomy.hc;
		sal = hc.getSerializeArrayList();
		composites = hc.getYaml().getComposites();
		this.name = name;
		this.economy = economy;
		this.type = HyperObjectType.fromString(composites.getString(this.name + ".information.type"));
		this.category = composites.getString(this.name + ".information.category");
		this.material = composites.getString(this.name + ".information.material");
		this.id = composites.getInt(this.name + ".information.id");
		this.data = composites.getInt(this.name + ".information.data");
		this.durability = composites.getInt(this.name + ".information.data");
		
		HashMap<String,String> tempComponents = sal.explodeMap(composites.getString(this.name + ".components"));
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
		    HyperObject ho = hc.getDataFunctions().getHyperObject(oname, economy);
		    this.components.put(ho, amount);
		}
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
	public String getCategory() {
		return category;
	}
	public String getMaterial() {
		return material;
	}
	public int getId() {
		return id;
	}
	public int getData() {
		return data;
	}
	public int getDurability() {
		return durability;
	}
	public double getValue() {
		double value = 0;
		for (Map.Entry<HyperObject,Double> entry : components.entrySet()) {
		    HyperObject ho = entry.getKey();
		    Double qty = entry.getValue();
		    value += (ho.getValue() * qty);
		}
		return twoDecimals(value);
	}
	public String getIsstatic() {
		String isstatic = "true";
		for (Map.Entry<HyperObject,Double> entry : components.entrySet()) {
		    HyperObject ho = entry.getKey();
		    if (!Boolean.parseBoolean(ho.getIsstatic())) {
		    	isstatic = "false";
		    }
		}
		return isstatic;
	}
	public double getStaticprice() {
		double staticprice = 0;
		for (Map.Entry<HyperObject,Double> entry : components.entrySet()) {
		    HyperObject ho = entry.getKey();
		    Double qty = entry.getValue();
		    staticprice += (ho.getStaticprice() * qty);
		}
		return twoDecimals(staticprice);
	}
	public double getStock() {
		double stock = 999999999;
		for (Map.Entry<HyperObject,Double> entry : components.entrySet()) {
		    HyperObject ho = entry.getKey();
		    Double qty = entry.getValue();
		    double cs = twoDecimals((ho.getStock() / qty));
		    if (cs < stock) {
		    	stock = cs;
		    }
		}
		return stock;
	}
	public double getMedian() {
		/*
		double median = 0;
		int totalQty = 0;
		for (Map.Entry<HyperObject,Double> entry : components.entrySet()) {
		    HyperObject ho = entry.getKey();
		    Double qty = entry.getValue();
		    totalQty += qty;
		    median += (ho.getMedian() * qty);
		    Bukkit.broadcastMessage("getMedian:Name[" + ho.getName() + "]Qty[" + qty + "]Median[" + ho.getMedian() + "]");
		}
		median /= totalQty;
		return twoDecimals(median);
		*/
		double median = 999999999;
		for (Map.Entry<HyperObject,Double> entry : components.entrySet()) {
		    HyperObject ho = entry.getKey();
		    if (ho.getMedian() < median) {
		    	median = ho.getMedian();
		    }
		}
		return median;
	}
	public String getInitiation() {
		String initial = "false";
		for (Map.Entry<HyperObject,Double> entry : components.entrySet()) {
		    HyperObject ho = entry.getKey();
		    if (Boolean.parseBoolean(ho.getInitiation())) {
		    	initial = "true";
		    }
		}
		return initial;
	}
	public double getStartprice() {
		double startprice = 0;
		for (Map.Entry<HyperObject,Double> entry : components.entrySet()) {
		    HyperObject ho = entry.getKey();
		    Double qty = entry.getValue();
		    startprice += (ho.getStartprice() * qty);
		}
		return twoDecimals(startprice);
	}
	public double getCeiling() {
		double ceiling = 9999999999999.99;
		for (Map.Entry<HyperObject,Double> entry : components.entrySet()) {
		    HyperObject ho = entry.getKey();
		    double cc = ho.getCeiling();
		    if (cc < ceiling) {
		    	ceiling = cc;
		    }
		}
		if (ceiling <= 0) {
			return 9999999999999.99;
		}
		return twoDecimals(ceiling);
	}
	public double getFloor() {
		double floor = 0;
		for (Map.Entry<HyperObject,Double> entry : components.entrySet()) {
		    HyperObject ho = entry.getKey();
		    double cf = ho.getFloor();
		    if (cf > floor) {
		    	floor = cf;
		    }
		}
		if (floor < 0) {
			return 0.0;
		}
		return twoDecimals(floor);
	}
	public double getMaxstock() {
		double maxstock = 999999999;
		for (Map.Entry<HyperObject,Double> entry : components.entrySet()) {
		    HyperObject ho = entry.getKey();
		    double cm = ho.getMaxstock();
		    if (cm < maxstock) {
		    	maxstock = cm;
		    }
		}
		return twoDecimals(maxstock);
	}
	

	
	
	public void setName(String name) {
		//TODO change yml
		this.name = name;
	}
	public void setEconomy(String economy) {
		this.economy = economy;
	}
	public void setType(String type) {
		this.type = HyperObjectType.fromString(type);
		composites.set(this.name + ".information.type", this.type.toString());
	}
	public void setCategory(String category) {
		this.category = category;
		composites.set(this.name + ".information.category", this.category);
	}
	public void setMaterial(String material) {
		this.material = material;
		composites.set(this.name + ".information.material", this.material.toString());
	}
	public void setId(int id) {
		this.id = id;
		composites.set(this.name + ".information.id", this.id);
	}
	public void setData(int data) {
		this.data = data;
		composites.set(this.name + ".information.data", this.data);
	}
	public void setDurability(int durability) {
		this.durability = durability;
		composites.set(this.name + ".information.durability", this.durability);
	}
	public void setValue(double value) {
		//irrelevant
	}
	public void setIsstatic(String isstatic) {
		//irrelevant
	}
	public void setStaticprice(double staticprice) {
		//irrelevant
	}
	public void setStock(double stock) {
		double difference = stock - getStock();
		for (Map.Entry<HyperObject,Double> entry : components.entrySet()) {
		    HyperObject ho = entry.getKey();
		    Double qty = entry.getValue();
		    ho.setStock(ho.getStock() + (difference * qty));
		}
	}
	public void setMedian(double median) {
		//irrelevant
	}
	public void setInitiation(String initiation) {
		//irrelevant
	}
	public void setStartprice(double startprice) {
		//irrelevant
	}
	public void setCeiling(double ceiling) {
		//irrelevant
	}
	public void setFloor(double floor) {
		//irrelevant
	}
	public void setMaxstock(double maxstock) {
		//irrelevant
	}
	
	
	
	
	
	
	/**
	 * 
	 * 
	 * This function returns the maximum number of items that can be sold before
	 * reaching the hyperbolic pricing curve.
	 * 
	 */
	public int getMaxInitial() {
		int maxInitial = 999999999;
		for (Map.Entry<HyperObject,Double> entry : components.entrySet()) {
		    HyperObject ho = entry.getKey();
		    Double qty = entry.getValue();
		    int ci = (int) Math.floor(ho.getMaxInitial() / qty);
		    if (ci < maxInitial) {
		    	maxInitial = ci;
		    }
		}
		return maxInitial;
	}

	
	public double getCost(int amount) {
		double cost = 0;
		for (Map.Entry<HyperObject,Double> entry : components.entrySet()) {
		    HyperObject ho = entry.getKey();
		    Double qty = entry.getValue();
		    cost += (ho.getCost(amount) * qty);
		}
		return twoDecimals(cost);
	}
	public double getCost(EnchantmentClass enchantClass) {
		double cost = 0;
		for (Map.Entry<HyperObject,Double> entry : components.entrySet()) {
		    HyperObject ho = entry.getKey();
		    Double qty = entry.getValue();
		    cost += (ho.getCost(enchantClass) * qty);
		}
		return twoDecimals(cost);
	}
	
	public double getValue(int amount) {
		double value = 0;
		for (Map.Entry<HyperObject,Double> entry : components.entrySet()) {
		    HyperObject ho = entry.getKey();
		    Double qty = entry.getValue();
		    value += (ho.getValue(amount) * qty);
		}
		return twoDecimals(value);
	}
	public double getValue(int amount, HyperPlayer hp) {
		double value = 0;
		for (Map.Entry<HyperObject,Double> entry : components.entrySet()) {
		    HyperObject ho = entry.getKey();
		    Double qty = entry.getValue();
		    value += (ho.getValue(amount, hp) * qty);
		}
		return twoDecimals(value);
	}
	
	public double getValue(EnchantmentClass enchantClass) {
		double value = 0;
		for (Map.Entry<HyperObject,Double> entry : components.entrySet()) {
		    HyperObject ho = entry.getKey();
		    Double qty = entry.getValue();
		    value += (ho.getValue(enchantClass) * qty);
		}
		return twoDecimals(value);
	}
	
	public double getValue(EnchantmentClass enchantClass, HyperPlayer hp) {
		double value = 0;
		for (Map.Entry<HyperObject,Double> entry : components.entrySet()) {
		    HyperObject ho = entry.getKey();
		    Double qty = entry.getValue();
		    value += (ho.getValue(enchantClass, hp) * qty);
		}
		return twoDecimals(value);
	}
	

	public double getPurchaseTax(double cost) {
		double tax = 0.0;
		if (Boolean.parseBoolean(getIsstatic())) {
			tax = hc.getYaml().getConfig().getDouble("config.statictaxpercent") / 100.0;
		} else {
			if (getType() == HyperObjectType.ENCHANTMENT) {
				tax = hc.getYaml().getConfig().getDouble("config.enchanttaxpercent") / 100.0;
			} else {
				if (Boolean.parseBoolean(getInitiation())) {
					tax = hc.getYaml().getConfig().getDouble("config.initialpurchasetaxpercent") / 100.0;
				} else {
					tax = hc.getYaml().getConfig().getDouble("config.purchasetaxpercent") / 100.0;
				}
			}
		}
		return twoDecimals(cost * tax);
	}
	
	public double getSalesTaxEstimate(double value) {
		double salestax = 0;
		if (hc.getYaml().getConfig().getBoolean("config.dynamic-tax.use-dynamic-tax")) {
			return 0.0;
		} else {
			double salestaxpercent = hc.getYaml().getConfig().getDouble("config.sales-tax-percent");
			salestax = (salestaxpercent / 100) * value;
		}
		return twoDecimals(salestax);
	}
	
	public boolean isDurable() {
		Material m = Material.getMaterial(this.id);
		if (m != null && m.getMaxDurability() > 0) {
			return true;
		}
		return false;
	}
	
	private double twoDecimals(double input) {
		int nodecimals = (int) Math.ceil((input * 100) - .5);
		double twodecimals = (double) nodecimals / 100.0;
		return twodecimals;
	}
	
	/*
ironblock:
  information:
    type: item
    category: unknown
    material: IRON_BLOCK
    id: 42
    data: 0
  value: 288.0
  price:
    static: false
    staticprice: 576.0
  stock:
    stock: 0.0
    median: 2000.0
  initiation:
    initiation: true
    startprice: 576.0
book:
  information:
    type: item
    category: unknown
    material: BOOK
    id: 340
    data: 0
  value: 15.0
  price:
    static: false
    staticprice: 30.0
  stock:
    stock: 0.0
    median: 10000.0
  initiation:
    initiation: true
    startprice: 30.0
paper:
  information:
    type: item
    category: unknown
    material: PAPER
    id: 339
    data: 0
  value: 5.0
  price:
    static: false
    staticprice: 10.0
  stock:
    stock: 0.0
    median: 10000.0
  initiation:
    initiation: true
    startprice: 10.0
oakwood:
  information:
    type: item
    category: unknown
    material: WOOD
    id: 5
    data: 0
  value: 1.0
  price:
    static: false
    staticprice: 2.0
  stock:
    stock: 0.0
    median: 10000.0
  initiation:
    initiation: true
    startprice: 2.0
bookshelf:
  information:
    type: item
    category: unknown
    material: BOOKSHELF
    id: 47
    data: 0
  value: 55.0
  price:
    static: false
    staticprice: 110.0
  stock:
    stock: 0.0
    median: 1000.0
  initiation:
    initiation: true
    startprice: 110.0
	 */
	
}
