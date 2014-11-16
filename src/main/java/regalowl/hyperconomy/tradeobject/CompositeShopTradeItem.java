package regalowl.hyperconomy.tradeobject;

import java.util.Map;

import regalowl.hyperconomy.HyperConomy;
import regalowl.hyperconomy.HyperEconomy;
import regalowl.hyperconomy.shop.PlayerShop;





public class CompositeShopTradeItem extends BasicShopTradeObject implements TradeObject {


	private static final long serialVersionUID = -6802879836491318792L;


	public CompositeShopTradeItem(HyperConomy hc, String playerShop, CompositeTradeItem ho, double stock, double buyPrice, double sellPrice, int maxStock, TradeObjectStatus status, boolean useEconomyStock) {
		super(hc, playerShop, ho, stock, buyPrice, sellPrice, maxStock, status, useEconomyStock);
	}


	@Override
	public double getStock() {
		HyperEconomy he = hc.getDataManager().getEconomy(getTradeObject().getEconomy());
		PlayerShop ps = (PlayerShop)hc.getHyperShopManager().getShop(playerShop);
		double stock = 999999999.99;
		for (Map.Entry<String,Double> entry : getTradeObject().getComponents().entrySet()) {
			TradeObject pso = ps.getPlayerShopObject(he.getTradeObject(entry.getKey()));
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
		HyperEconomy he = hc.getDataManager().getEconomy(getTradeObject().getEconomy());
		PlayerShop ps = (PlayerShop)hc.getHyperShopManager().getShop(playerShop);
		if (stock < 0.0) {stock = 0.0;}
		double difference = stock - getStock();
		for (Map.Entry<String,Double> entry : getTradeObject().getComponents().entrySet()) {
			TradeObject pso = ps.getPlayerShopObject(he.getTradeObject(entry.getKey()));
		    Double qty = entry.getValue();
		    double newStock = pso.getStock() + (difference * qty);
		    pso.setStock(newStock);
		}
	}
	@Override
	public double getBuyPrice() {
		HyperEconomy he = hc.getDataManager().getEconomy(getTradeObject().getEconomy());
		PlayerShop ps = (PlayerShop)hc.getHyperShopManager().getShop(playerShop);
		double price = 0;
		for (Map.Entry<String,Double> entry : getTradeObject().getComponents().entrySet()) {
			TradeObject pso = ps.getPlayerShopObject(he.getTradeObject(entry.getKey()));
		    Double qty = entry.getValue();
		    price += (pso.getBuyPrice() * qty);
		}
		return price;
	}
	@Override
	public double getSellPrice() {
		HyperEconomy he = hc.getDataManager().getEconomy(getTradeObject().getEconomy());
		PlayerShop ps = (PlayerShop)hc.getHyperShopManager().getShop(playerShop);
		double price = 0;
		for (Map.Entry<String,Double> entry : getTradeObject().getComponents().entrySet()) {
			TradeObject pso = ps.getPlayerShopObject(he.getTradeObject(entry.getKey()));
		    Double qty = entry.getValue();
		    price += (pso.getSellPrice() * qty);
		}
		return price;
	}

	@Override
	public boolean isCompositeObject() {return true;}


	@Override
	public void checkInitiationStatus() {
		HyperEconomy he = hc.getDataManager().getEconomy(getTradeObject().getEconomy());
		PlayerShop ps = (PlayerShop)hc.getHyperShopManager().getShop(playerShop);
		for (Map.Entry<String,Double> entry : getTradeObject().getComponents().entrySet()) {
			TradeObject pso = ps.getPlayerShopObject(he.getTradeObject(entry.getKey()));
			pso.checkInitiationStatus();
		}
	}

	
	
	
	
	

}