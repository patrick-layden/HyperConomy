package regalowl.hyperconomy.tradeobject;


import java.util.ArrayList;

import regalowl.hyperconomy.HyperConomy;
import regalowl.hyperconomy.HyperEconomy;
import regalowl.hyperconomy.inventory.HItemStack;


public class TempTradeItem extends ComponentTradeItem implements TradeObject {


	private static final long serialVersionUID = 4228578172340543286L;
	public TempTradeItem(HyperConomy hc, HyperEconomy he, String name, String economy, String displayName, String aliases, String categories, String type, double value, String isstatic, double staticprice, double stock, double median, String initiation, double startprice, double ceiling, double floor, double maxstock, String compositeData, String objectData) {
		super(hc, he, name, economy, displayName, aliases, categories, type, value, isstatic, staticprice, stock, median, initiation, startprice, ceiling, floor, maxstock, compositeData, objectData);
	}
	
	//Override all set methods to prevent database changes.
	@Override
	public void setName(String name) {
		this.name = name;
	}
	@Override
	public void setEconomy(String economy) {
		this.economy = economy;
	}
	@Override
	public void setType(TradeObjectType type) {
		this.type = type;
	}
	@Override
	public void setValue(double value) {
		this.value = value;
	}
	@Override
	public void setStatic(boolean isstatic) {
		this.isstatic = isstatic;
	}
	@Override
	public void setStaticPrice(double staticprice) {
		this.staticprice = staticprice;
	}
	@Override
	public void setStock(double stock) {
		if (stock < 0.0) {stock = 0.0;}
		this.stock = stock;
	}
	@Override
	public void setMedian(double median) {
		this.median = median;
	}
	@Override
	public void setUseInitialPricing(boolean initiation) {
		this.initiation = initiation;
	}
	@Override
	public void setStartPrice(double startprice) {
		this.startprice = startprice;
	}
	@Override
	public void setCeiling(double ceiling) {
		this.ceiling = ceiling;
	}
	@Override
	public void setFloor(double floor) {
		this.floor = floor;
	}
	@Override
	public void setMaxStock(double maxstock) {
		this.maxstock = maxstock;
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
	public void checkInitiationStatus() {}
	@Override
	public void save() {}
	public static TradeObject generate(HyperConomy hc, HItemStack stack){
		if (stack.isBlank()) {return null;}
		String name = generateName(hc, stack);
		double value = 10.0;
		double median = 10000;
		double startprice = 20.0;
		return new TempTradeItem(hc, null, name, "default", name, "", "", "item", value, "false", startprice, 0.0, median, "true", startprice, 0.0, 0.0, 0.0, "", stack.serialize());
	}
	
	public static String generateName(HyperConomy hc, HItemStack stack) {
		HyperEconomy econ = hc.getDataManager().getDefaultEconomy();
		String name = stack.getMaterial() + "_" + stack.getDurability();
		if (econ.objectTest(name)) {
			name = "object1";
			int counter = 1;
			while (econ.objectTest(name)) {
				name = "object" + counter;
				counter++;
			}
		}
		return name;
	}


}
