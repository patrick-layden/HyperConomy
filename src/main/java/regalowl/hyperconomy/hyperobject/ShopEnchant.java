package regalowl.hyperconomy.hyperobject;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

import regalowl.hyperconomy.account.HyperPlayer;
import regalowl.hyperconomy.shop.PlayerShop;



public class ShopEnchant extends BasicShopObject implements HyperObject {



	public ShopEnchant(PlayerShop playerShop, HyperObject ho, double stock, double buyPrice, double sellPrice, int maxStock, HyperObjectStatus status) {
		super(playerShop, ho, stock, buyPrice, sellPrice, maxStock, status);
	}

	@Override
	public String getEnchantmentName() {
		return ho.getEnchantmentName();
	}
	@Override
	public double getBuyPrice(EnchantmentClass enchantClass) {
		if (buyPrice != 0.0) {
			return buyPrice;
		} else {
			return ho.getBuyPrice(enchantClass);
		}
	}
	@Override
	public double getSellPrice(EnchantmentClass enchantClass) {
		if (sellPrice != 0.0) {
			return sellPrice;
		} else {
			return ho.getSellPrice(enchantClass);
		}
	}
	@Override
	public double getSellPrice(EnchantmentClass enchantClass, HyperPlayer hp) {
		if (sellPrice != 0.0) {
			return sellPrice;
		} else {
			return ho.getSellPrice(enchantClass, hp);
		}
	}
	
	@Override
	public double getBuyPrice(int amount) {
		return getBuyPrice(EnchantmentClass.DIAMOND) * amount;
	}
	@Override
	public double getSellPrice(int amount) {
		return getSellPrice(EnchantmentClass.DIAMOND) * amount;
	}
	@Override
	public double getSellPrice(int amount, HyperPlayer hp) {
		return getSellPrice(EnchantmentClass.DIAMOND, hp) * amount;
	}

	@Override
	public Enchantment getEnchantment() {
		return ho.getEnchantment();
	}

	@Override
	public int getEnchantmentLevel() {
		return ho.getEnchantmentLevel();
	}

	@Override
	public double addEnchantment(ItemStack stack) {
		return ho.addEnchantment(stack);
	}

	@Override
	public double removeEnchantment(ItemStack stack) {
		return ho.removeEnchantment(stack);
	}







}