package regalowl.hyperconomy;


import java.util.ArrayList;

import org.bukkit.Material;

public class TempItem extends ComponentItem implements HyperObject {


	public TempItem(String name, String economy, String displayName, String aliases, String type, String material, int data, int durability, double value, String isstatic, double staticprice, double stock, double median, String initiation, double startprice, double ceiling, double floor, double maxstock) {
		super(name, economy, displayName, aliases, type, material, data, durability, value, isstatic, staticprice, stock, median, initiation, startprice, ceiling, floor, maxstock);
	}
	
	//Override all set methods to prevent database changes.
	
	@Override
	public void setMaterial(String material) {
		this.material = material;
		this.materialEnum = Material.matchMaterial(material);
	}
	@Override
	public void setMaterial(Material material) {
		String materialS = material.toString();
		this.material = materialS;
		this.materialEnum = material;
	}
	@Override
	public void setData(int data) {
		this.data = data;
	}
	@Override
	public void setDurability(int durability) {
		this.durability = durability;
	}
	@Override
	public void setName(String name) {
		this.name = name;
	}
	@Override
	public void setEconomy(String economy) {
		this.economy = economy;
	}
	@Override
	public void setType(HyperObjectType type) {
		this.type = type;
	}
	@Override
	public void setValue(double value) {
		this.value = value;
	}
	@Override
	public void setIsstatic(String isstatic) {
		this.isstatic = isstatic;
	}
	@Override
	public void setStaticprice(double staticprice) {
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
	public void setInitiation(String initiation) {
		this.initiation = initiation;
	}
	@Override
	public void setStartprice(double startprice) {
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
	public void setMaxstock(double maxstock) {
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

	
	
	



}
