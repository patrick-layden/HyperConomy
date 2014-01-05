package regalowl.hyperconomy;

import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;



public class ComponentShopItem extends BasicShopObject implements PlayerShopItem {

	
	ComponentShopItem(PlayerShop playerShop, ComponentItem ho, double stock, double buyPrice, double sellPrice, int maxStock, HyperObjectStatus status) {
		super(playerShop, ho, stock, buyPrice, sellPrice, maxStock, status);
	}
	

	
	public void setHyperObject(HyperItem hi) {
		this.ho = hi;
		sw.addToQueue("UPDATE hyperconomy_shop_objects SET HYPEROBJECT='"+ho.getName()+"' WHERE SHOP='"+playerShop.getName()+"' AND HYPEROBJECT='"+ho.getName()+"'");
	}

	public String getMaterial() {
		return ((HyperItem)ho).getMaterial();
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
	public void setMaterial(Material material) {
		//do nothing
	}
	public void setData(int data) {
		//do nothing
	}
	public void setDurability(int durability) {
		//do nothing
	}


	public double getValue(int amount, HyperPlayer hp) {
		if (sellPrice != 0.0) {
			return sellPrice * amount;
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