package regalowl.hyperconomy;

import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public interface HyperItem extends HyperObject {
	
	public String getMaterial();
	public Material getMaterialEnum();
	//public int getId();
	public int getData();
	public int getDurability();
	public void setData(int data);
	public void setDurability(int durability);
	
	public void setMaterial(String material);
	public void setMaterial(Material material);
	//public void setId(int id);
	public double getValue(int amount);
	public double getValue(int amount, HyperPlayer hp);
	public double getCost(int amount);
	public boolean isDurable();
	
	public int count(Inventory inventory);
	public int getAvailableSpace(Inventory inventory);
	public ItemStack getItemStack();
	public ItemStack getItemStack(int amount);
	public void add(int amount, Inventory inventory);
	public double remove(int amount, Inventory inventory);
}
