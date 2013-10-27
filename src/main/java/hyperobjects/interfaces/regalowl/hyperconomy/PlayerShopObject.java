package regalowl.hyperconomy;

public interface PlayerShopObject extends HyperObject{
	public PlayerShop getShop();
	public HyperObject getHyperObject();
	public double getStock();
	public double getPrice();
	public HyperObjectStatus getStatus();
	
	
	public void setShop(PlayerShop playerShop);
	public void setStock(double stock);
	public void setPrice(double price);
	public void setStatus(HyperObjectStatus status);
}
