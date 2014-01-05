package regalowl.hyperconomy;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;


public class ShopEnchant extends BasicShopObject implements PlayerShopEnchant {



	ShopEnchant(PlayerShop playerShop, HyperEnchant ho, double stock, double price, HyperObjectStatus status) {
		super(playerShop, ho, stock, price, status);
	}

	
	public void setHyperObject(HyperEnchant ho) {
		this.ho = ho;
		sw.addToQueue("UPDATE hyperconomy_shop_objects SET HYPEROBJECT='"+ho.getName()+"' WHERE SHOP='"+playerShop.getName()+"' AND HYPEROBJECT='"+ho.getName()+"'");
	}


	public String getEnchantmentName() {
		return ((HyperEnchant)ho).getEnchantmentName();
	}

	public void setEnchantmentName(String name) {
		//do nothing
	}

	public double getCost(EnchantmentClass enchantClass) {
		if (price != 0.0) {
			return price;
		} else {
			return ((HyperEnchant)ho).getCost(enchantClass);
		}
	}

	public double getValue(EnchantmentClass enchantClass) {
		if (price != 0.0) {
			return price;
		} else {
			return ((HyperEnchant)ho).getValue(enchantClass);
		}
	}

	public double getValue(EnchantmentClass enchantClass, HyperPlayer hp) {
		if (price != 0.0) {
			return price;
		} else {
			return ((HyperEnchant)ho).getValue(enchantClass, hp);
		}
	}
	
	@Override
	public double getCost(int amount) {
		return getCost(EnchantmentClass.DIAMOND) * amount;
	}
	@Override
	public double getValue(int amount) {
		return getValue(EnchantmentClass.DIAMOND) * amount;
	}


	public Enchantment getEnchantment() {
		return ((HyperEnchant)ho).getEnchantment();
	}


	public int getEnchantmentLevel() {
		return ((HyperEnchant)ho).getEnchantmentLevel();
	}


	public double addEnchantment(ItemStack stack) {
		return ((HyperEnchant)ho).addEnchantment(stack);
	}


	public double removeEnchantment(ItemStack stack) {
		return ((HyperEnchant)ho).removeEnchantment(stack);
	}







}