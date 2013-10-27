package regalowl.hyperconomy;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

public interface HyperEnchant extends HyperObject {
	public String getEnchantmentName();
	public int getEnchantmentId();
	
	public void setEnchantmentName(String name);
	public void setEnchantmentId(int id);
	
	public double getCost(EnchantmentClass enchantClass);
	public double getValue(EnchantmentClass enchantClass);
	public double getValue(EnchantmentClass enchantClass, HyperPlayer hp);
	
	public Enchantment getEnchantment();
	public int getEnchantmentLevel();
	public boolean addEnchantment(ItemStack stack);
	public boolean removeEnchantment(ItemStack stack);
}
