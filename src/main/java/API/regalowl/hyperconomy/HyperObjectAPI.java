package regalowl.hyperconomy;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class HyperObjectAPI implements HyperObjectInterface {
	public double getTheoreticalPurchasePrice(int id, int durability, int amount, String economy) {
		if (economy == null) {
			economy = "default";
		}
		HyperConomy hc = HyperConomy.hc;
		Calculation calc = hc.getCalculation();
		String ke = id + ":" + durability;
		String name = hc.getnameData(ke);
		Double price = calc.getCost(name, amount, economy);
		price = calc.twoDecimals(price);
		return price;
	}

	public double getTheoreticalSaleValue(int id, int durability, int amount, String economy) {
		if (economy == null) {
			economy = "default";
		}
		HyperConomy hc = HyperConomy.hc;
		Calculation calc = hc.getCalculation();
		String ke = id + ":" + durability;
		String name = hc.getnameData(ke);
		Double value = calc.getTvalue(name, amount, economy);
		value = calc.twoDecimals(value);
		return value;
	}

	public double getTruePurchasePrice(int id, int durability, int amount, String economy) {
		if (economy == null) {
			economy = "default";
		}
		HyperConomy hc = HyperConomy.hc;
		Calculation calc = hc.getCalculation();
		double durabilityPercent = 1.0;
		if (calc.isDurable(id)) {
			ItemStack item = new ItemStack(id, durability);
			durabilityPercent = 1.0 - ((double) durability / item.getType().getMaxDurability());
			durability = 0;
		}
		String ke = id + ":" + durability;
		String name = hc.getnameData(ke);
		Double price = calc.getCost(name, amount, economy) * durabilityPercent;
		double tax = calc.getPurchaseTax(name, economy, price);
		price = tax + price;
		price = calc.twoDecimals(price);
		return price;
	}

	public double getTrueSaleValue(int id, int durability, int amount, Player player) {
		HyperConomy hc = HyperConomy.hc;
		Calculation calc = hc.getCalculation();
		double durabilityPercent = 1.0;
		if (calc.isDurable(id)) {
			ItemStack item = new ItemStack(id, durability);
			durabilityPercent = 1.0 - ((double) durability / item.getType().getMaxDurability());
			durability = 0;
		}
		String ke = id + ":" + durability;
		String name = hc.getnameData(ke);
		Double value = calc.getValue(name, amount, player) * durabilityPercent;
		double salestax = calc.getSalesTax(player, value);
		value = value - salestax;
		value = calc.twoDecimals(value);
		return value;
	}

	public String getName(String name, String economy) {
		HyperConomy hc = HyperConomy.hc;
		DataFunctions sf = hc.getDataFunctions();
		return sf.getHyperObject(name, economy).getName();
	}

	public String getEconomy(String name, String economy) {
		HyperConomy hc = HyperConomy.hc;
		DataFunctions sf = hc.getDataFunctions();
		return sf.getHyperObject(name, economy).getEconomy();
	}

	public String getType(String name, String economy) {
		HyperConomy hc = HyperConomy.hc;
		DataFunctions sf = hc.getDataFunctions();
		return sf.getHyperObject(name, economy).getType();
	}

	public String getCategory(String name, String economy) {
		HyperConomy hc = HyperConomy.hc;
		DataFunctions sf = hc.getDataFunctions();
		return sf.getHyperObject(name, economy).getCategory();
	}

	public String getMaterial(String name, String economy) {
		HyperConomy hc = HyperConomy.hc;
		DataFunctions sf = hc.getDataFunctions();
		return sf.getHyperObject(name, economy).getMaterial();
	}

	public int getId(String name, String economy) {
		HyperConomy hc = HyperConomy.hc;
		DataFunctions sf = hc.getDataFunctions();
		return sf.getHyperObject(name, economy).getId();
	}

	public int getData(String name, String economy) {
		HyperConomy hc = HyperConomy.hc;
		DataFunctions sf = hc.getDataFunctions();
		return sf.getHyperObject(name, economy).getData();
	}

	public int getDurability(String name, String economy) {
		HyperConomy hc = HyperConomy.hc;
		DataFunctions sf = hc.getDataFunctions();
		return sf.getHyperObject(name, economy).getDurability();
	}

	public double getValue(String name, String economy) {
		HyperConomy hc = HyperConomy.hc;
		DataFunctions sf = hc.getDataFunctions();
		return sf.getHyperObject(name, economy).getValue();
	}

	public String getStatic(String name, String economy) {
		HyperConomy hc = HyperConomy.hc;
		DataFunctions sf = hc.getDataFunctions();
		return sf.getHyperObject(name, economy).getIsstatic();
	}

	public double getStaticPrice(String name, String economy) {
		HyperConomy hc = HyperConomy.hc;
		DataFunctions sf = hc.getDataFunctions();
		return sf.getHyperObject(name, economy).getStaticprice();
	}

	public double getStock(String name, String economy) {
		HyperConomy hc = HyperConomy.hc;
		DataFunctions sf = hc.getDataFunctions();
		return sf.getHyperObject(name, economy).getStock();
	}

	public double getMedian(String name, String economy) {
		HyperConomy hc = HyperConomy.hc;
		DataFunctions sf = hc.getDataFunctions();
		return sf.getHyperObject(name, economy).getMedian();
	}

	public String getInitiation(String name, String economy) {
		HyperConomy hc = HyperConomy.hc;
		DataFunctions sf = hc.getDataFunctions();
		return sf.getHyperObject(name, economy).getInitiation();
	}

	public double getStartPrice(String name, String economy) {
		HyperConomy hc = HyperConomy.hc;
		DataFunctions sf = hc.getDataFunctions();
		return sf.getHyperObject(name, economy).getStartprice();
	}

	public void setName(String name, String economy, String newname) {
		HyperConomy hc = HyperConomy.hc;
		DataFunctions sf = hc.getDataFunctions();
		sf.getHyperObject(name, economy).setName(newname);
	}

	public void setEconomy(String name, String economy, String neweconomy) {
		HyperConomy hc = HyperConomy.hc;
		DataFunctions sf = hc.getDataFunctions();
		sf.getHyperObject(name, economy).setEconomy(neweconomy);
	}

	public void setType(String name, String economy, String newtype) {
		HyperConomy hc = HyperConomy.hc;
		DataFunctions sf = hc.getDataFunctions();
		sf.getHyperObject(name, economy).setType(newtype);
	}

	public void setCategory(String name, String economy, String newcategory) {
		HyperConomy hc = HyperConomy.hc;
		DataFunctions sf = hc.getDataFunctions();
		sf.getHyperObject(name, economy).setCategory(newcategory);
	}

	public void setMaterial(String name, String economy, String newmaterial) {
		HyperConomy hc = HyperConomy.hc;
		DataFunctions sf = hc.getDataFunctions();
		sf.getHyperObject(name, economy).setMaterial(newmaterial);
	}

	public void setId(String name, String economy, int newid) {
		HyperConomy hc = HyperConomy.hc;
		DataFunctions sf = hc.getDataFunctions();
		sf.getHyperObject(name, economy).setId(newid);
	}

	public void setData(String name, String economy, int newdata) {
		HyperConomy hc = HyperConomy.hc;
		DataFunctions sf = hc.getDataFunctions();
		sf.getHyperObject(name, economy).setData(newdata);
	}

	public void setDurability(String name, String economy, int newdurability) {
		HyperConomy hc = HyperConomy.hc;
		DataFunctions sf = hc.getDataFunctions();
		sf.getHyperObject(name, economy).setDurability(newdurability);
	}

	public void setValue(String name, String economy, double newvalue) {
		HyperConomy hc = HyperConomy.hc;
		DataFunctions sf = hc.getDataFunctions();
		sf.getHyperObject(name, economy).setValue(newvalue);
	}

	public void setStatic(String name, String economy, String newstatic) {
		HyperConomy hc = HyperConomy.hc;
		DataFunctions sf = hc.getDataFunctions();
		sf.getHyperObject(name, economy).setIsstatic(newstatic);
	}

	public void setStaticPrice(String name, String economy, double newstaticprice) {
		HyperConomy hc = HyperConomy.hc;
		DataFunctions sf = hc.getDataFunctions();
		sf.getHyperObject(name, economy).setStaticprice(newstaticprice);
	}

	public void setStock(String name, String economy, double newstock) {
		HyperConomy hc = HyperConomy.hc;
		DataFunctions sf = hc.getDataFunctions();
		sf.getHyperObject(name, economy).setStock(newstock);
	}

	public void setMedian(String name, String economy, double newmedian) {
		HyperConomy hc = HyperConomy.hc;
		DataFunctions sf = hc.getDataFunctions();
		sf.getHyperObject(name, economy).setMedian(newmedian);
	}

	public void setInitiation(String name, String economy, String newinitiation) {
		HyperConomy hc = HyperConomy.hc;
		DataFunctions sf = hc.getDataFunctions();
		sf.getHyperObject(name, economy).setInitiation(newinitiation);
	}

	public void setStartPrice(String name, String economy, double newstartprice) {
		HyperConomy hc = HyperConomy.hc;
		DataFunctions sf = hc.getDataFunctions();
		sf.getHyperObject(name, economy).setStartprice(newstartprice);
	}

	public double getItemPurchasePrice(int id, int data, int amount) {
		HyperConomy hc = HyperConomy.hc;
		Calculation calc = hc.getCalculation();
		String ke = id + ":" + data;
		String name = hc.getnameData(ke);
		Double price = calc.getCost(name, amount, "default");
		price = calc.twoDecimals(price);
		return price;
	}

	public double getItemSaleValue(int id, int data, int amount) {
		HyperConomy hc = HyperConomy.hc;
		Calculation calc = hc.getCalculation();
		String ke = id + ":" + data;
		String name = hc.getnameData(ke);
		Double value = calc.getTvalue(name, amount, "default");
		value = calc.twoDecimals(value);
		return value;
	}
}
