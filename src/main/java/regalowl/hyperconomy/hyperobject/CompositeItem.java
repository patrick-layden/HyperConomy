package regalowl.hyperconomy.hyperobject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import regalowl.databukkit.CommonFunctions;
import regalowl.hyperconomy.HyperConomy;
import regalowl.hyperconomy.HyperEconomy;
import regalowl.hyperconomy.account.HyperPlayer;
import regalowl.hyperconomy.hyperobject.HyperObject;

public class CompositeItem extends ComponentItem implements HyperObject {

	private CommonFunctions cf;
	
	private ConcurrentHashMap<HyperObject,Double> components = new ConcurrentHashMap<HyperObject,Double>();
	
	
	public CompositeItem(HyperEconomy he, String name, String economy, String displayName, String aliases, String type, String composites, String data) {
		super(name,economy,"","","",0,"",0,0,0,"",0,0,0,0,data);
		hc = HyperConomy.hc;
		cf = hc.gCF();
		this.displayName = displayName;
		ArrayList<String> tAliases = hc.gCF().explode(aliases, ",");
		for (String cAlias:tAliases) {
			this.aliases.add(cAlias);
		}
		this.type = HyperObjectType.fromString(type);
		HashMap<String,String> tempComponents = cf.explodeMap(composites);
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
		    HyperObject ho = he.getHyperObject(oname);
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
	@Override
	public double getSellPrice(int amount, HyperPlayer hp) {
		double value = 0;
		for (Map.Entry<HyperObject,Double> entry : components.entrySet()) {
			HyperObject ho = entry.getKey();
		    Double qty = entry.getValue();
		    value += (ho.getSellPrice(amount, hp) * qty);
		}
		double damageMultiplier = getDamageMultiplier(amount, hp.getPlayer().getInventory());
		return value * damageMultiplier;
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
	
	@Override
	public ConcurrentHashMap<HyperObject,Double> getComponents() {
		return components;
	}
	
	@Override
	public void setComponents(String components) {
		String statement = "UPDATE hyperconomy_composites SET COMPONENTS='" + components + "' WHERE NAME = '" + this.name + "'";
		hc.getSQLWrite().addToQueue(statement);
		this.components.clear();
		HashMap<String,String> tempComponents = cf.explodeMap(components);
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
		    HyperObject ho = hc.getDataManager().getEconomy(economy).getHyperObject(oname);
		    this.components.put(ho, amount);
		}
	}
	
	@Override
	public void setName(String name) {
		String statement = "UPDATE hyperconomy_composites SET NAME='" + name + "' WHERE NAME = '" + this.name + "'";
		hc.getSQLWrite().addToQueue(statement);
		this.name = name;
	}
	@Override
	public void setEconomy(String economy) {
		this.economy = economy;
	}
	@Override
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
		String statement = "UPDATE hyperconomy_composites SET DISPLAY_NAME='" + displayName + "' WHERE NAME = '" + this.name + "'";
		hc.getSQLWrite().addToQueue(statement);
	}



	
	
	
	
	//Override the following methods to prevent database changes.
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
	@Override
	public void setType(HyperObjectType type) {}

	
	





	 
	
}
