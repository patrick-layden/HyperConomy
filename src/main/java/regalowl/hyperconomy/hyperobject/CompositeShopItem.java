package regalowl.hyperconomy.hyperobject;

import java.util.Map;

import regalowl.hyperconomy.HyperConomy;
import regalowl.hyperconomy.HyperEconomy;
import regalowl.hyperconomy.shop.PlayerShop;





public class CompositeShopItem extends BasicShopObject implements HyperObject {


	private static final long serialVersionUID = -6802879836491318792L;


	public CompositeShopItem(String playerShop, CompositeItem ho, double stock, double buyPrice, double sellPrice, int maxStock, HyperObjectStatus status, boolean useEconomyStock) {
		super(playerShop, ho, stock, buyPrice, sellPrice, maxStock, status, useEconomyStock);
	}


	@Override
	public double getStock() {
		HyperConomy hc = HyperConomy.hc;
		HyperEconomy he = hc.getDataManager().getEconomy(getHyperObject().getEconomy());
		PlayerShop ps = (PlayerShop)hc.getHyperShopManager().getShop(playerShop);
		double stock = 999999999.99;
		for (Map.Entry<String,Double> entry : getHyperObject().getComponents().entrySet()) {
			HyperObject pso = ps.getPlayerShopObject(he.getHyperObject(entry.getKey()));
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
		HyperConomy hc = HyperConomy.hc;
		HyperEconomy he = hc.getDataManager().getEconomy(getHyperObject().getEconomy());
		PlayerShop ps = (PlayerShop)hc.getHyperShopManager().getShop(playerShop);
		if (stock < 0.0) {stock = 0.0;}
		double difference = stock - getStock();
		for (Map.Entry<String,Double> entry : getHyperObject().getComponents().entrySet()) {
			HyperObject pso = ps.getPlayerShopObject(he.getHyperObject(entry.getKey()));
		    Double qty = entry.getValue();
		    double newStock = pso.getStock() + (difference * qty);
		    pso.setStock(newStock);
		}
	}
	@Override
	public double getBuyPrice() {
		HyperConomy hc = HyperConomy.hc;
		HyperEconomy he = hc.getDataManager().getEconomy(getHyperObject().getEconomy());
		PlayerShop ps = (PlayerShop)hc.getHyperShopManager().getShop(playerShop);
		double price = 0;
		for (Map.Entry<String,Double> entry : getHyperObject().getComponents().entrySet()) {
			HyperObject pso = ps.getPlayerShopObject(he.getHyperObject(entry.getKey()));
		    Double qty = entry.getValue();
		    price += (pso.getBuyPrice() * qty);
		}
		return price;
	}
	@Override
	public double getSellPrice() {
		HyperConomy hc = HyperConomy.hc;
		HyperEconomy he = hc.getDataManager().getEconomy(getHyperObject().getEconomy());
		PlayerShop ps = (PlayerShop)hc.getHyperShopManager().getShop(playerShop);
		double price = 0;
		for (Map.Entry<String,Double> entry : getHyperObject().getComponents().entrySet()) {
			HyperObject pso = ps.getPlayerShopObject(he.getHyperObject(entry.getKey()));
		    Double qty = entry.getValue();
		    price += (pso.getSellPrice() * qty);
		}
		return price;
	}

	@Override
	public boolean isCompositeObject() {return true;}


	@Override
	public void checkInitiationStatus() {
		HyperConomy hc = HyperConomy.hc;
		HyperEconomy he = hc.getDataManager().getEconomy(getHyperObject().getEconomy());
		PlayerShop ps = (PlayerShop)hc.getHyperShopManager().getShop(playerShop);
		for (Map.Entry<String,Double> entry : getHyperObject().getComponents().entrySet()) {
			HyperObject pso = ps.getPlayerShopObject(he.getHyperObject(entry.getKey()));
			pso.checkInitiationStatus();
		}
	}

	
	
	
	
	

}