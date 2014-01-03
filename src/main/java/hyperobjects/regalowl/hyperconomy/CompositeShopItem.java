package regalowl.hyperconomy;

import java.util.Map;

import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;


public class CompositeShopItem extends BasicShopObject implements PlayerShopItem {


	CompositeShopItem(PlayerShop playerShop, CompositeItem ci, double stock, double price, HyperObjectStatus status) {
		super(playerShop, ci, stock, price, status);
	}

	public void setHyperObject(HyperItem ho) {
		this.ho = ho;
		sw.addToQueue("UPDATE hyperconomy_shop_objects SET HYPEROBJECT='"+ho.getName()+"' WHERE SHOP='"+playerShop.getName()+"' AND HYPEROBJECT='"+ho.getName()+"'");
	}
	

	@Override
	public double getStock() {
		double stock = 999999999.99;
		for (Map.Entry<HyperItem,Double> entry : ((CompositeItem)ho).getComponents().entrySet()) {
			PlayerShopItem pso = (PlayerShopItem) playerShop.getPlayerShopObject(entry.getKey());
		    Double qty = entry.getValue();
		    double cs = (pso.getStock() / qty);
		    if (cs < stock) {
		    	stock = cs;
		    }
		}
		return stock;
	}
	@Override
	public double getPrice() {
		double price = 0;
		for (Map.Entry<HyperItem,Double> entry : ((CompositeItem)ho).getComponents().entrySet()) {
			PlayerShopItem pso = (PlayerShopItem) playerShop.getPlayerShopObject(entry.getKey());
		    Double qty = entry.getValue();
		    price += (pso.getPrice() * qty);
		}
		return price;
	}
	@Override
	public void setStock(double stock) {
		if (stock < 0.0) {stock = 0.0;}
		double difference = stock - getStock();
		for (Map.Entry<HyperItem,Double> entry : ((CompositeItem)ho).getComponents().entrySet()) {
			PlayerShopItem pso = (PlayerShopItem) playerShop.getPlayerShopObject(entry.getKey());
		    Double qty = entry.getValue();
		    double newStock = pso.getStock() + (difference * qty);
		    pso.setStock(newStock);
		}
	}



	public String getMaterial() {
		return ((CompositeItem)ho).getMaterial();
	}
	public int getData() {
		return ((CompositeItem)ho).getData();
	}
	public int getDurability() {
		return ((CompositeItem)ho).getDurability();
	}


	public void setMaterial(String material) {
		//do nothing
	}
	public void setMaterial(Material material) {
		//do nothing
	}
	public void setData(int data) {
		//do nothing
	}
	public void setDurability(int durability) {
		//do nothing
	}
	

	public double getCost(int amount) {
		if (getPrice() != 0.0) {
			return getPrice() * amount;
		} else {
			return ((CompositeItem)ho).getCost(amount);
		}
	}

	public double getValue(int amount) {
		if (getPrice() != 0.0) {
			return getPrice() * amount;
		} else {
			return ((CompositeItem)ho).getValue(amount);
		}
	}

	public double getValue(int amount, HyperPlayer hp) {
		if (getPrice() != 0.0) {
			return getPrice() * amount;
		} else {
			return ((CompositeItem)ho).getValue(amount, hp);
		}
	}

	@Override
	public double getPurchaseTax(double cost) {
		return 0;
	}
	@Override
	public double getSalesTaxEstimate(double value) {
		return 0;
	}
	public boolean isDurable() {
		return ((CompositeItem)ho).isDurable();
	}

	public int count(Inventory inventory) {
		return ((CompositeItem)ho).count(inventory);
	}
	public int getAvailableSpace(Inventory inventory) {
		return ((CompositeItem)ho).getAvailableSpace(inventory);
	}

	public void add(int amount, Inventory inventory) {
		((CompositeItem)ho).add(amount, inventory);
		
	}

	public double remove(int amount, Inventory inventory) {
		return ((CompositeItem)ho).remove(amount, inventory);
	}

	public Material getMaterialEnum() {
		return ((HyperItem)ho).getMaterialEnum();
	}

	public ItemStack getItemStack() {
		return ((HyperItem)ho).getItemStack();
	}

	public ItemStack getItemStack(int amount) {
		return ((HyperItem)ho).getItemStack(amount);
	}












	

	
	
	
	
	

}