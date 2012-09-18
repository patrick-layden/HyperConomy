package regalowl.hyperconomy;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class APIBridge implements HyperAPI
{

	@Override
	public double getTheoreticalPurchasePrice(int id, int durability, int amount, String economy)
	{
		if (economy == null)
		{
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

	@Override
	public double getTheoreticalSaleValue(int id, int durability, int amount, String economy)
	{
		if (economy == null)
		{
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

	@Override
	public double getTruePurchasePrice(int id, int durability, int amount, String economy)
	{
		if (economy == null)
		{
			economy = "default";
		}
		HyperConomy hc = HyperConomy.hc;
		Calculation calc = hc.getCalculation();
		double durabilityPercent = 1.0;
		if (calc.testId(id))
		{
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

	@Override
	public double getTrueSaleValue(int id, int durability, int amount, Player player)
	{
		HyperConomy hc = HyperConomy.hc;
		Calculation calc = hc.getCalculation();
		double durabilityPercent = 1.0;
		if (calc.testId(id))
		{
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

	@Override
	public String getName(String name, String economy)
	{
		HyperConomy hc = HyperConomy.hc;
		SQLFunctions sf = hc.getSQLFunctions();
		return sf.getName(name, economy);
	}

	@Override
	public String getEconomy(String name, String economy)
	{
		HyperConomy hc = HyperConomy.hc;
		SQLFunctions sf = hc.getSQLFunctions();
		return sf.getEconomy(name, economy);
	}

	@Override
	public String getType(String name, String economy)
	{
		HyperConomy hc = HyperConomy.hc;
		SQLFunctions sf = hc.getSQLFunctions();
		return sf.getType(name, economy);
	}

	@Override
	public String getCategory(String name, String economy)
	{
		HyperConomy hc = HyperConomy.hc;
		SQLFunctions sf = hc.getSQLFunctions();
		return sf.getCategory(name, economy);
	}

	@Override
	public String getMaterial(String name, String economy)
	{
		HyperConomy hc = HyperConomy.hc;
		SQLFunctions sf = hc.getSQLFunctions();
		return sf.getMaterial(name, economy);
	}

	@Override
	public int getId(String name, String economy)
	{
		HyperConomy hc = HyperConomy.hc;
		SQLFunctions sf = hc.getSQLFunctions();
		return sf.getId(name, economy);
	}

	@Override
	public int getData(String name, String economy)
	{
		HyperConomy hc = HyperConomy.hc;
		SQLFunctions sf = hc.getSQLFunctions();
		return sf.getData(name, economy);
	}

	@Override
	public int getDurability(String name, String economy)
	{
		HyperConomy hc = HyperConomy.hc;
		SQLFunctions sf = hc.getSQLFunctions();
		return sf.getDurability(name, economy);
	}

	@Override
	public double getValue(String name, String economy)
	{
		HyperConomy hc = HyperConomy.hc;
		SQLFunctions sf = hc.getSQLFunctions();
		return sf.getValue(name, economy);
	}

	@Override
	public String getStatic(String name, String economy)
	{
		HyperConomy hc = HyperConomy.hc;
		SQLFunctions sf = hc.getSQLFunctions();
		return sf.getStatic(name, economy);
	}

	@Override
	public double getStaticPrice(String name, String economy)
	{
		HyperConomy hc = HyperConomy.hc;
		SQLFunctions sf = hc.getSQLFunctions();
		return sf.getStaticPrice(name, economy);
	}

	@Override
	public double getStock(String name, String economy)
	{
		HyperConomy hc = HyperConomy.hc;
		SQLFunctions sf = hc.getSQLFunctions();
		return sf.getStock(name, economy);
	}

	@Override
	public double getMedian(String name, String economy)
	{
		HyperConomy hc = HyperConomy.hc;
		SQLFunctions sf = hc.getSQLFunctions();
		return sf.getMedian(name, economy);
	}

	@Override
	public String getInitiation(String name, String economy)
	{
		HyperConomy hc = HyperConomy.hc;
		SQLFunctions sf = hc.getSQLFunctions();
		return sf.getInitiation(name, economy);
	}

	@Override
	public double getStartPrice(String name, String economy)
	{
		HyperConomy hc = HyperConomy.hc;
		SQLFunctions sf = hc.getSQLFunctions();
		return sf.getStartPrice(name, economy);
	}

	@Override
	public void setName(String name, String economy, String newname)
	{
		HyperConomy hc = HyperConomy.hc;
		SQLFunctions sf = hc.getSQLFunctions();
		sf.setName(name, economy, newname);
	}

	@Override
	public void setEconomy(String name, String economy, String neweconomy)
	{
		HyperConomy hc = HyperConomy.hc;
		SQLFunctions sf = hc.getSQLFunctions();
		sf.setEconomy(name, economy, neweconomy);
	}

	@Override
	public void setType(String name, String economy, String newtype)
	{
		HyperConomy hc = HyperConomy.hc;
		SQLFunctions sf = hc.getSQLFunctions();
		sf.setType(name, economy, newtype);
	}

	@Override
	public void setCategory(String name, String economy, String newcategory)
	{
		HyperConomy hc = HyperConomy.hc;
		SQLFunctions sf = hc.getSQLFunctions();
		sf.setCategory(name, economy, newcategory);
	}

	@Override
	public void setMaterial(String name, String economy, String newmaterial)
	{
		HyperConomy hc = HyperConomy.hc;
		SQLFunctions sf = hc.getSQLFunctions();
		sf.setMaterial(name, economy, newmaterial);
	}

	@Override
	public void setId(String name, String economy, int newid)
	{
		HyperConomy hc = HyperConomy.hc;
		SQLFunctions sf = hc.getSQLFunctions();
		sf.setId(name, economy, newid);
	}

	@Override
	public void setData(String name, String economy, int newdata)
	{
		HyperConomy hc = HyperConomy.hc;
		SQLFunctions sf = hc.getSQLFunctions();
		sf.setData(name, economy, newdata);
	}

	@Override
	public void setDurability(String name, String economy, int newdurability)
	{
		HyperConomy hc = HyperConomy.hc;
		SQLFunctions sf = hc.getSQLFunctions();
		sf.setDurability(name, economy, newdurability);
	}

	@Override
	public void setValue(String name, String economy, double newvalue)
	{
		HyperConomy hc = HyperConomy.hc;
		SQLFunctions sf = hc.getSQLFunctions();
		sf.setValue(name, economy, newvalue);
	}

	@Override
	public void setStatic(String name, String economy, String newstatic)
	{
		HyperConomy hc = HyperConomy.hc;
		SQLFunctions sf = hc.getSQLFunctions();
		sf.setStatic(name, economy, newstatic);
	}

	@Override
	public void setStaticPrice(String name, String economy, double newstaticprice)
	{
		HyperConomy hc = HyperConomy.hc;
		SQLFunctions sf = hc.getSQLFunctions();
		sf.setStaticPrice(name, economy, newstaticprice);
	}

	@Override
	public void setStock(String name, String economy, double newstock)
	{
		HyperConomy hc = HyperConomy.hc;
		SQLFunctions sf = hc.getSQLFunctions();
		sf.setStock(name, economy, newstock);
	}

	@Override
	public void setMedian(String name, String economy, double newmedian)
	{
		HyperConomy hc = HyperConomy.hc;
		SQLFunctions sf = hc.getSQLFunctions();
		sf.setMedian(name, economy, newmedian);
	}

	@Override
	public void setInitiation(String name, String economy, String newinitiation)
	{
		HyperConomy hc = HyperConomy.hc;
		SQLFunctions sf = hc.getSQLFunctions();
		sf.setInitiation(name, economy, newinitiation);
	}

	@Override
	public void setStartPrice(String name, String economy, double newstartprice)
	{
		HyperConomy hc = HyperConomy.hc;
		SQLFunctions sf = hc.getSQLFunctions();
		sf.setStartPrice(name, economy, newstartprice);
	}

	@Override
	public double getItemPurchasePrice(int id, int data, int amount)
	{
		HyperConomy hc = HyperConomy.hc;
		Calculation calc = hc.getCalculation();
		String ke = id + ":" + data;
		String name = hc.getnameData(ke);
		Double price = calc.getCost(name, amount, "default");
		price = calc.twoDecimals(price);
		return price;
	}

	@Override
	public double getItemSaleValue(int id, int data, int amount)
	{
		HyperConomy hc = HyperConomy.hc;
		Calculation calc = hc.getCalculation();
		String ke = id + ":" + data;
		String name = hc.getnameData(ke);
		Double value = calc.getTvalue(name, amount, "default");
		value = calc.twoDecimals(value);
		return value;
	}

}
