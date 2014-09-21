package regalowl.hyperconomy.hyperobject;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

import regalowl.hyperconomy.account.HyperPlayer;



public class ShopEnchant extends BasicShopObject implements HyperObject {


	private static final long serialVersionUID = -213806188136759445L;

	public ShopEnchant(String playerShop, HyperObject ho, double stock, double buyPrice, double sellPrice, int maxStock, HyperObjectStatus status, boolean useEconomyStock) {
		super(playerShop, ho, stock, buyPrice, sellPrice, maxStock, status, useEconomyStock);
	}

	@Override
	public String getEnchantmentName() {
		return getHyperObject().getEnchantmentName();
	}
	@Override
	public double getBuyPrice(EnchantmentClass enchantClass) {
		if (buyPrice != 0.0) {
			return buyPrice;
		} else {
			return getHyperObject().getBuyPrice(enchantClass);
		}
	}
	@Override
	public double getSellPrice(EnchantmentClass enchantClass) {
		if (sellPrice != 0.0) {
			return sellPrice;
		} else {
			return getHyperObject().getSellPrice(enchantClass);
		}
	}
	@Override
	public double getSellPrice(EnchantmentClass enchantClass, HyperPlayer hp) {
		if (sellPrice != 0.0) {
			return sellPrice;
		} else {
			return getHyperObject().getSellPrice(enchantClass, hp);
		}
	}
	
	@Override
	public double getBuyPrice(double amount) {
		return getBuyPrice(EnchantmentClass.DIAMOND) * amount;
	}
	@Override
	public double getSellPrice(double amount) {
		return getSellPrice(EnchantmentClass.DIAMOND) * amount;
	}
	@Override
	public double getSellPrice(double amount, HyperPlayer hp) {
		return getSellPrice(EnchantmentClass.DIAMOND, hp) * amount;
	}

	@Override
	public Enchantment getEnchantment() {
		return getHyperObject().getEnchantment();
	}

	@Override
	public int getEnchantmentLevel() {
		return getHyperObject().getEnchantmentLevel();
	}

	@Override
	public double addEnchantment(ItemStack stack) {
		return getHyperObject().addEnchantment(stack);
	}

	@Override
	public double removeEnchantment(ItemStack stack) {
		return getHyperObject().removeEnchantment(stack);
	}







}