package regalowl.hyperconomy;

import org.bukkit.inventory.Inventory;



public class ComponentShopItem extends BasicShopObject implements PlayerShopItem {

	
	ComponentShopItem(PlayerShop playerShop, ComponentItem ho, double stock, double price, HyperObjectStatus status) {
		super(playerShop, ho, stock, price, status);
	}
	

	
	public void setHyperObject(HyperItem hi) {
		this.ho = hi;
		sw.executeSQL("UPDATE hyperconomy_shop_objects SET HYPEROBJECT='"+ho.getName()+"' WHERE SHOP='"+playerShop.getName()+"' AND HYPEROBJECT='"+ho.getName()+"'");
	}

	public String getMaterial() {
		return ((HyperItem)ho).getMaterial();
	}
	public int getId() {
		return ((HyperItem)ho).getId();
	}
	public int getData() {
		return ((HyperItem)ho).getData();
	}
	public int getDurability() {
		return ((HyperItem)ho).getDurability();
	}

	public void setMaterial(String material) {
		//do nothing
	}
	public void setId(int id) {
		//do nothing
	}
	public void setData(int data) {
		//do nothing
	}
	public void setDurability(int durability) {
		//do nothing
	}

	public double getCost(int amount) {
		if (price != 0.0) {
			return price * amount;
		} else {
			return ((HyperItem)ho).getCost(amount);
		}
	}

	public double getValue(int amount) {
		if (price != 0.0) {
			return price * amount;
		} else {
			return ((HyperItem)ho).getValue(amount);
		}
	}

	public double getValue(int amount, HyperPlayer hp) {
		if (price != 0.0) {
			return price * amount;
		} else {
			return ((HyperItem)ho).getValue(amount, hp);
		}
	}


	public boolean isDurable() {
		return ((HyperItem)ho).isDurable();
	}



	public int count(Inventory inventory) {
		return ((HyperItem)ho).count(inventory);
	}
	
	public int getAvailableSpace(Inventory inventory) {
		return ((HyperItem)ho).getAvailableSpace(inventory);
	}



	public void add(int amount, Inventory inventory) {
		((HyperItem)ho).add(amount, inventory);
	}



	public double remove(int amount, Inventory inventory) {
		return ((HyperItem)ho).remove(amount, inventory);
	}
	
	
	
	
	
	

}