package regalowl.hyperconomy;

import org.bukkit.entity.Player;



public class APIBridge implements HyperAPI{



	@Override
	public double getTheoreticalPurchasePrice(int id, int data, int amount, String economy) {
		if (economy == null) {
			economy = "default";
		}
		HyperConomy hc = HyperConomy.hc;
		Calculation calc = hc.getCalculation();
		String ke = id + ":" + data;
		String name = hc.getnameData(ke);		
		Double price = calc.getCost(name, amount, economy);
		price = calc.twoDecimals(price);	
		return price;
	}

	@Override
	public double getTheoreticalSaleValue(int id, int data, int amount, String economy) {
		if (economy == null) {
			economy = "default";
		}
		HyperConomy hc = HyperConomy.hc;
		Calculation calc = hc.getCalculation();
		String ke = id + ":" + data;
		String name = hc.getnameData(ke);		
		Double value = calc.getTvalue(name, amount, economy);
		value = calc.twoDecimals(value);
		return value;	
	}
	
	@Override
	public double getTruePurchasePrice(int id, int data, int amount, String economy) {
		if (economy == null) {
			economy = "default";
		}
		HyperConomy hc = HyperConomy.hc;
		Calculation calc = hc.getCalculation();
		String ke = id + ":" + data;
		String name = hc.getnameData(ke);		
		Double price = calc.getCost(name, amount, economy);
		double tax = calc.getPurchaseTax(name, economy, price);
		price = tax + price;
		price = calc.twoDecimals(price);	
		return price;
	}
	
	@Override
	public double getTrueSaleValue(int id, int data, int amount, Player player) {
		HyperConomy hc = HyperConomy.hc;
		Calculation calc = hc.getCalculation();
		String ke = id + ":" + data;
		String name = hc.getnameData(ke);		
		Double value = calc.getValue(name, amount, player);
		double salestax = calc.getSalesTax(player, value);
		value = value - salestax;
		value = calc.twoDecimals(value);
		return value;	
	}

	
	
	
	
	
	
	
	@Override
	public double getItemPurchasePrice(int id, int data, int amount) {
		HyperConomy hc = HyperConomy.hc;
		Calculation calc = hc.getCalculation();
		String ke = id + ":" + data;
		String name = hc.getnameData(ke);		
		Double price = calc.getCost(name, amount, "default");
		price = calc.twoDecimals(price);	
		return price;
	}


	
	@Override
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
