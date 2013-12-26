package regalowl.hyperconomy;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

public interface HyperEnchant extends HyperObject {
	public String getEnchantmentName();
	public void setEnchantmentName(String name);

	public double getCost(EnchantmentClass enchantClass);
	public double getValue(EnchantmentClass enchantClass);
	public double getValue(EnchantmentClass enchantClass, HyperPlayer hp);
	
	public Enchantment getEnchantment();
	public int getEnchantmentLevel();
	public double addEnchantment(ItemStack stack);
	public double removeEnchantment(ItemStack stack);
}
