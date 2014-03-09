package regalowl.hyperconomy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;

import regalowl.databukkit.CommonFunctions;
import regalowl.hyperconomy.HyperObject;

public class CompositeItem extends ComponentItem implements HyperObject {

	private FileConfiguration composites;
	private CommonFunctions cf;
	
	private ConcurrentHashMap<HyperObject,Double> components = new ConcurrentHashMap<HyperObject,Double>();
	
	
	public CompositeItem(String name, String economy) {
		super(name,economy,"","","","", 0,0, 0, "",0,0,0,"",0,0,0,0);
		hc = HyperConomy.hc;
		cf = hc.gCF();
		composites = hc.gYH().gFC("composites");
		this.displayName = composites.getString(this.name + ".name.display");
		String sAliases = composites.getString(this.name + ".name.aliases");
		ArrayList<String> tAliases = hc.gCF().explode(sAliases, ",");
		for (String cAlias:tAliases) {
			this.aliases.add(cAlias);
		}
		this.type = HyperObjectType.fromString(composites.getString(this.name + ".information.type"));
		this.material = composites.getString(this.name + ".information.material");
		this.materialEnum = Material.matchMaterial(this.material);
		this.data = composites.getInt(this.name + ".information.data");
		this.durability = composites.getInt(this.name + ".information.data");
		
		HashMap<String,String> tempComponents = cf.explodeMap(composites.getString(this.name + ".components"));
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
		    HyperObject ho = hc.getEconomyManager().getEconomy(economy).getHyperObject(oname);
		    this.components.put(ho, amount);
		}
	}

	
	
	//The following methods calculate the HyperObject's values based on the CompositeItem's component items.
	
	@Override
	public double getValue() {
		double value = 0;
		for (Map.Entry<HyperObject,Double> entry : components.entrySet()) {
		    HyperObject ho = entry.getKey();
		    Double qty = entry.getValue();
		    value += (ho.getValue() * qty);
		}
		return value;
	}
	@Override
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
	@Override
	public double getStaticprice() {
		double staticprice = 0;
		for (Map.Entry<HyperObject,Double> entry : components.entrySet()) {
		    HyperObject ho = entry.getKey();
		    Double qty = entry.getValue();
		    staticprice += (ho.getStaticprice() * qty);
		}
		return staticprice;
	}
	@Override
	public double getStock() {
		double stock = 999999999.99;
		for (Map.Entry<HyperObject,Double> entry : components.entrySet()) {
		    HyperObject ho = entry.getKey();
		    Double qty = entry.getValue();
		    double cs = (ho.getStock() / qty);
		    if (cs < stock) {
		    	stock = cs;
		    }
		}
		return stock;
	}
	@Override
	public double getTotalStock() {
		double stock = 999999999.99;
		for (Map.Entry<HyperObject,Double> entry : components.entrySet()) {
		    HyperObject ho = entry.getKey();
		    Double qty = entry.getValue();
		    double cs = (ho.getTotalStock() / qty);
		    if (cs < stock) {
		    	stock = cs;
		    }
		}
		return stock;
	}
	@Override
	public double getMedian() {
		double median = 999999999;
		for (Map.Entry<HyperObject,Double> entry : components.entrySet()) {
		    HyperObject ho = entry.getKey();
		    if (ho.getMedian() < median) {
		    	median = ho.getMedian();
		    }
		}
		return median;
	}
	@Override
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
	@Override
	public double getStartprice() {
		double startprice = 0;
		for (Map.Entry<HyperObject,Double> entry : components.entrySet()) {
		    HyperObject ho = entry.getKey();
		    Double qty = entry.getValue();
		    startprice += (ho.getStartprice() * qty);
		}
		return startprice;
	}
	@Override
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
		return ceiling;
	}
	@Override
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
		return floor;
	}
	@Override
	public double getMaxstock() {
		double maxstock = 999999999;
		for (Map.Entry<HyperObject,Double> entry : components.entrySet()) {
		    HyperObject ho = entry.getKey();
		    double cm = ho.getMaxstock();
		    if (cm < maxstock) {
		    	maxstock = cm;
		    }
		}
		return maxstock;
	}
	@Override
	public int getMaxInitial() {
		int maxInitial = 999999999;
		for (Map.Entry<HyperObject,Double> entry : components.entrySet()) {
		    HyperObject ho = entry.getKey();
		    Double qty = entry.getValue();
		    if (Boolean.parseBoolean(ho.getInitiation())) {
				int ci = (int) Math.floor(ho.getMaxInitial() / qty);
				if (ci < maxInitial) {
				    maxInitial = ci;
				}
		    }
		}
		return maxInitial;
	}
	@Override
	public double getBuyPrice(int amount) {
		double cost = 0;
		for (Map.Entry<HyperObject,Double> entry : components.entrySet()) {
			HyperObject ho = entry.getKey();
		    Double qty = entry.getValue();
		    cost += (ho.getBuyPrice(amount) * qty);
		}
		return cost;
	}
	
	@Override
	public double getSellPrice(int amount) {
		double value = 0;
		for (Map.Entry<HyperObject,Double> entry : components.entrySet()) {
			HyperObject ho = entry.getKey();
		    Double qty = entry.getValue();
		    value += (ho.getSellPrice(amount) * qty);
		}
		return value;
	}
	public double getSellPrice(int amount, HyperPlayer hp) {
		double value = 0;
		for (Map.Entry<HyperObject,Double> entry : components.entrySet()) {
			HyperObject ho = entry.getKey();
		    Double qty = entry.getValue();
		    value += (ho.getSellPrice(amount, hp) * qty);
		}
		return value;
	}
	

	//Override the following getter/setter methods to store data in composites.yml

	@Override
	public void setType(HyperObjectType type) {
		this.type = type;
		composites.set(this.name + ".information.type", this.type.toString());
	}
	@Override
	public void setMaterial(String material) {
		this.material = material;
		this.materialEnum = Material.matchMaterial(material);
		composites.set(this.name + ".information.material", this.material);
	}
	@Override
	public void setMaterial(Material material) {
		String materialS = material.toString();
		this.material = materialS;
		this.materialEnum = material;
		composites.set(this.name + ".information.material", materialS);
	}
	@Override
	public void setData(int data) {
		this.data = data;
		composites.set(this.name + ".information.data", this.data);
	}
	@Override
	public void setDurability(int durability) {
		this.durability = durability;
		composites.set(this.name + ".information.durability", this.durability);
	}
	@Override
	public ConcurrentHashMap<HyperObject,Double> getComponents() {
		return components;
	}


	
	
	//The setStock method updates the stock for all component items to the correct level.
	
	@Override
	public void setStock(double stock) {
		if (stock < 0.0) {stock = 0.0;}
		double difference = stock - getStock();
		for (Map.Entry<HyperObject,Double> entry : components.entrySet()) {
		    HyperObject ho = entry.getKey();
		    Double qty = entry.getValue();
		    double newStock = ho.getStock() + (difference * qty);
		    ho.setStock(newStock);
		}
	}

	@Override
	public boolean isCompositeObject() {return true;}
	


	
	
	
	
	//Override the following methods to prevent database changes.
	
	@Override
	public void setName(String name) {
		this.name = name;
	}
	@Override
	public void setEconomy(String economy) {
		this.economy = economy;
	}
	@Override
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}
	@Override
	public void setAliases(ArrayList<String> newAliases) {
		aliases.clear();
		for (String cAlias:newAliases) {
			aliases.add(cAlias);
		}
	}
	@Override
	public void addAlias(String addAlias) {
		if (aliases.contains(addAlias)) {return;}
		aliases.add(addAlias);
	}
	@Override
	public void removeAlias(String removeAlias) {
		if (!aliases.contains(removeAlias)) {return;}
		aliases.remove(removeAlias);
	}
	@Override
	public void setMedian(double median) {}
	@Override
	public void setInitiation(String initiation) {}
	@Override
	public void setStartprice(double startprice) {}
	@Override
	public void setCeiling(double ceiling) {}
	@Override
	public void setFloor(double floor) {}
	@Override
	public void setMaxstock(double maxstock) {}
	@Override
	public void setValue(double value) {}
	@Override
	public void setIsstatic(String isstatic) {}
	@Override
	public void setStaticprice(double staticprice) {}
	
	
	
	





	 
	
}
