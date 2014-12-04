package regalowl.hyperconomy.tradeobject;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import regalowl.simpledatalib.CommonFunctions;
import regalowl.hyperconomy.HyperConomy;
import regalowl.hyperconomy.HyperEconomy;
import regalowl.hyperconomy.account.HyperPlayer;
import regalowl.hyperconomy.event.TradeObjectModificationEvent;
import regalowl.hyperconomy.tradeobject.TradeObject;

public class CompositeTradeItem extends ComponentTradeItem implements TradeObject {

	private static final long serialVersionUID = -2104610162054897073L;

	private ConcurrentHashMap<String,Double> components = new ConcurrentHashMap<String,Double>();
	
	
	public CompositeTradeItem(HyperConomy hc, HyperEconomy he, String name, String economy, String displayName, String aliases, String categories, String type, String composites, String data) {
		super(hc,name,economy,displayName,aliases,categories,"",0,"",0,0,0,"",0,0,0,0,data);
		this.type = TradeObjectType.fromString(type);
		HashMap<String,String> tempComponents = CommonFunctions.explodeMap(composites);
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
		    TradeObject ho = he.getTradeObject(oname);
		    this.components.put(ho.getName(), amount);
		}
	}

	
	//The following methods calculate the HyperObject's values based on the CompositeItem's component items.
	
	@Override
	public double getValue() {
		HyperEconomy he = hc.getDataManager().getEconomy(economy);
		double value = 0;
		for (Map.Entry<String,Double> entry : components.entrySet()) {
		    TradeObject ho = he.getTradeObject(entry.getKey());
		    Double qty = entry.getValue();
		    value += (ho.getValue() * qty);
		}
		return value;
	}
	@Override
	public boolean isStatic() {
		HyperEconomy he = hc.getDataManager().getEconomy(economy);
		boolean isstatic = true;
		for (Map.Entry<String,Double> entry : components.entrySet()) {
		    TradeObject ho = he.getTradeObject(entry.getKey());
		    if (!ho.isStatic()) {
		    	isstatic = false;
		    }
		}
		return isstatic;
	}
	@Override
	public double getStaticPrice() {
		HyperEconomy he = hc.getDataManager().getEconomy(economy);
		double staticprice = 0;
		for (Map.Entry<String,Double> entry : components.entrySet()) {
		    TradeObject ho = he.getTradeObject(entry.getKey());
		    Double qty = entry.getValue();
		    staticprice += (ho.getStaticPrice() * qty);
		}
		return staticprice;
	}
	@Override
	public double getStock() {
		HyperEconomy he = hc.getDataManager().getEconomy(economy);
		double stock = 999999999.99;
		for (Map.Entry<String,Double> entry : components.entrySet()) {
		    TradeObject ho = he.getTradeObject(entry.getKey());
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
		HyperEconomy he = hc.getDataManager().getEconomy(economy);
		double stock = 1000000000000.0;
		for (Map.Entry<String,Double> entry : components.entrySet()) {
		    TradeObject ho = he.getTradeObject(entry.getKey());
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
		HyperEconomy he = hc.getDataManager().getEconomy(economy);
		double median = 999999999;
		for (Map.Entry<String,Double> entry : components.entrySet()) {
		    TradeObject ho = he.getTradeObject(entry.getKey());
		    if (ho.getMedian() < median) {
		    	median = ho.getMedian();
		    }
		}
		return median;
	}
	@Override
	public boolean useInitialPricing() {
		HyperEconomy he = hc.getDataManager().getEconomy(economy);
		boolean initial = false;
		for (Map.Entry<String,Double> entry : components.entrySet()) {
		    TradeObject ho = he.getTradeObject(entry.getKey());
		    if (ho.useInitialPricing()) {
		    	initial = true;
		    }
		}
		return initial;
	}
	@Override
	public double getStartPrice() {
		HyperEconomy he = hc.getDataManager().getEconomy(economy);
		double startprice = 0;
		for (Map.Entry<String,Double> entry : components.entrySet()) {
		    TradeObject ho = he.getTradeObject(entry.getKey());
		    Double qty = entry.getValue();
		    startprice += (ho.getStartPrice() * qty);
		}
		return startprice;
	}
	@Override
	public double getCeiling() {
		HyperEconomy he = hc.getDataManager().getEconomy(economy);
		double ceiling = 1000000000000000.0;
		for (Map.Entry<String,Double> entry : components.entrySet()) {
		    TradeObject ho = he.getTradeObject(entry.getKey());
		    if (ho.getCeiling() < ceiling) {
		    	ceiling = ho.getCeiling();
		    }
		}
		if (ceiling <= 0) return 1000000000000000.0;
		return ceiling;
	}
	@Override
	public double getFloor() {
		HyperEconomy he = hc.getDataManager().getEconomy(economy);
		double floor = 0;
		for (Map.Entry<String,Double> entry : components.entrySet()) {
		    TradeObject ho = he.getTradeObject(entry.getKey());
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
	public double getMaxStock() {
		HyperEconomy he = hc.getDataManager().getEconomy(economy);
		double maxstock = 999999999;
		for (Map.Entry<String,Double> entry : components.entrySet()) {
		    TradeObject ho = he.getTradeObject(entry.getKey());
		    double cm = ho.getMaxStock();
		    if (cm < maxstock) {
		    	maxstock = cm;
		    }
		}
		return maxstock;
	}
	@Override
	public int getMaxInitial() {
		HyperEconomy he = hc.getDataManager().getEconomy(economy);
		int maxInitial = 999999999;
		for (Map.Entry<String,Double> entry : components.entrySet()) {
		    TradeObject ho = he.getTradeObject(entry.getKey());
		    Double qty = entry.getValue();
		    if (ho.useInitialPricing()) {
				int ci = (int) Math.floor(ho.getMaxInitial() / qty);
				if (ci < maxInitial) {
				    maxInitial = ci;
				}
		    }
		}
		return maxInitial;
	}
	@Override
	public double getBuyPrice(double amount) {
		HyperEconomy he = hc.getDataManager().getEconomy(economy);
		double cost = 0;
		for (Map.Entry<String,Double> entry : components.entrySet()) {
		    TradeObject ho = he.getTradeObject(entry.getKey());
		    Double compositeFactor = entry.getValue();
		    cost += (ho.getBuyPrice(amount * compositeFactor));
		}
		return cost;
	}
	
	@Override
	public double getSellPrice(double amount) {
		HyperEconomy he = hc.getDataManager().getEconomy(economy);
		double value = 0;
		for (Map.Entry<String,Double> entry : components.entrySet()) {
		    TradeObject ho = he.getTradeObject(entry.getKey());
		    Double compositeFactor = entry.getValue();
		    value += (ho.getSellPrice(amount * compositeFactor));
		}
		return value;
	}
	@Override
	public double getSellPrice(double amount, HyperPlayer hp) {
		return getSellPrice(amount) * getDamageMultiplier((int)Math.ceil(amount), hp.getInventory());
	}
	

	
	
	

	//The setStock method updates the stock for all component items to the correct level.
	
	@Override
	public void setStock(double stock) {
		HyperEconomy he = hc.getDataManager().getEconomy(economy);
		if (stock < 0.0) {stock = 0.0;}
		double difference = stock - getStock();
		for (Map.Entry<String,Double> entry : components.entrySet()) {
		    TradeObject ho = he.getTradeObject(entry.getKey());
		    Double qty = entry.getValue();
		    double newStock = ho.getStock() + (difference * qty);
		    ho.setStock(newStock);
		}
	}
	
	
	@Override
	public void checkInitiationStatus() {
		HyperEconomy he = hc.getDataManager().getEconomy(economy);
		for (Map.Entry<String,Double> entry : components.entrySet()) {
		    TradeObject ho = he.getTradeObject(entry.getKey());
		    ho.checkInitiationStatus();
		}
	}

	@Override
	public boolean isCompositeObject() {return true;}
	
	@Override
	public ConcurrentHashMap<String,Double> getComponents() {
		return components;
	}
	
	@Override
	public void setComponents(String components) {
		String statement = "UPDATE hyperconomy_composites SET COMPONENTS='" + components + "' WHERE NAME = '" + this.name + "'";
		hc.getSQLWrite().addToQueue(statement);
		this.components.clear();
		HashMap<String,String> tempComponents = CommonFunctions.explodeMap(components);
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
		hc.getHyperEventHandler().fireEvent(new TradeObjectModificationEvent(this));
	}
	
	@Override
	public void setName(String name) {
		String statement = "UPDATE hyperconomy_composites SET NAME='" + name + "' WHERE NAME = '" + this.name + "'";
		hc.getSQLWrite().addToQueue(statement);
		this.name = name;
		hc.getHyperEventHandler().fireEvent(new TradeObjectModificationEvent(this));
	}
	@Override
	public void setEconomy(String economy) {
		this.economy = economy;
		hc.getHyperEventHandler().fireEvent(new TradeObjectModificationEvent(this));
	}
	@Override
	public void setDisplayName(String displayName) {
		super.setDisplayName(displayName);
		String statement = "UPDATE hyperconomy_composites SET DISPLAY_NAME='" + displayName + "' WHERE NAME = '" + this.name + "'";
		hc.getSQLWrite().addToQueue(statement);
	}



	
	
	
	
	//Override the following methods to prevent database changes.
	@Override
	public void setMedian(double median) {}
	@Override
	public void setUseInitialPricing(boolean initiation) {}
	@Override
	public void setStartPrice(double startprice) {}
	@Override
	public void setCeiling(double ceiling) {}
	@Override
	public void setFloor(double floor) {}
	@Override
	public void setMaxStock(double maxstock) {}
	@Override
	public void setValue(double value) {}
	@Override
	public void setStatic(boolean isstatic) {}
	@Override
	public void setStaticPrice(double staticprice) {}
	@Override
	public void setType(TradeObjectType type) {}

	
	





	 
	
}
