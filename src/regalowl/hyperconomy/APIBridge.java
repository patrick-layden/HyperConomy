package regalowl.hyperconomy;



public class APIBridge implements HyperAPI{



	@Override
	public double getItemPurchasePrice(int id, int data, int amount) {
		HyperConomy hc = HyperConomy.hyperobject.getHyperConomy();
		Calculation calc = HyperConomy.hyperobject.getCalculation();
		String ke = id + ":" + data;
		String name = hc.getnameData(ke);		
		calc.setVC(hc, null, amount, name, null);
		Double price = calc.getCost();
		price = calc.twoDecimals(price);	
		return price;
	}



	@Override
	public double getItemSaleValue(int id, int data, int amount) {
		HyperConomy hc = HyperConomy.hyperobject.getHyperConomy();
		Calculation calc = HyperConomy.hyperobject.getCalculation();
		String ke = id + ":" + data;
		String name = hc.getnameData(ke);		
		calc.setVC(hc, null, amount, name, null);
		Double value = calc.getTvalue();
		value = calc.twoDecimals(value);
		return value;	
	}

}
