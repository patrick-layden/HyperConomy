package regalowl.hyperconomy;

import java.util.Map;





public class CompositeShopItem extends BasicShopObject implements HyperObject {


	CompositeShopItem(PlayerShop playerShop, CompositeItem ho, double stock, double buyPrice, double sellPrice, int maxStock, HyperObjectStatus status) {
		super(playerShop, ho, stock, buyPrice, sellPrice, maxStock, status);
	}


	@Override
	public double getStock() {
		double stock = 999999999.99;
		for (Map.Entry<HyperObject,Double> entry : ho.getComponents().entrySet()) {
			HyperObject pso = playerShop.getPlayerShopObject(entry.getKey());
		    Double qty = entry.getValue();
		    double cs = (pso.getStock() / qty);
		    if (cs < stock) {
		    	stock = cs;
		    }
		}
		return stock;
	}
	@Override
	public void setStock(double stock) {
		if (stock < 0.0) {stock = 0.0;}
		double difference = stock - getStock();
		for (Map.Entry<HyperObject,Double> entry : ho.getComponents().entrySet()) {
			HyperObject pso = playerShop.getPlayerShopObject(entry.getKey());
		    Double qty = entry.getValue();
		    double newStock = pso.getStock() + (difference * qty);
		    pso.setStock(newStock);
		}
	}
	@Override
	public double getBuyPrice() {
		double price = 0;
		for (Map.Entry<HyperObject,Double> entry : ho.getComponents().entrySet()) {
			HyperObject pso = playerShop.getPlayerShopObject(entry.getKey());
		    Double qty = entry.getValue();
		    price += (pso.getBuyPrice() * qty);
		}
		return price;
	}
	@Override
	public double getSellPrice() {
		double price = 0;
		for (Map.Entry<HyperObject,Double> entry : ho.getComponents().entrySet()) {
			HyperObject pso =  playerShop.getPlayerShopObject(entry.getKey());
		    Double qty = entry.getValue();
		    price += (pso.getSellPrice() * qty);
		}
		return price;
	}

	@Override
	public boolean isCompositeObject() {return true;}


	

	
	
	
	
	

}