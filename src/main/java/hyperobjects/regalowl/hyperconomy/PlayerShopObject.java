package regalowl.hyperconomy;

public interface PlayerShopObject extends HyperObject{
	public PlayerShop getShop();
	public HyperObject getHyperObject();
	public double getStock();
	public double getBuyPrice();
	public double getSellPrice();
	public int getMaxStock();
	public HyperObjectStatus getStatus();
	
	
	public void setShop(PlayerShop playerShop);
	public void setStock(double stock);
	public void setBuyPrice(double buyPrice);
	public void setSellPrice(double sellPrice);
	public void setMaxStock(int maxStock);
	public void setStatus(HyperObjectStatus status);
}
