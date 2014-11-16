package regalowl.hyperconomy.tradeobject;


import regalowl.hyperconomy.HyperConomy;
import regalowl.hyperconomy.account.HyperPlayer;
import regalowl.hyperconomy.inventory.HEnchantment;
import regalowl.hyperconomy.inventory.HItemStack;



public class ShopTradeEnchant extends BasicShopTradeObject implements TradeObject {


	private static final long serialVersionUID = -213806188136759445L;

	public ShopTradeEnchant(HyperConomy hc, String playerShop, TradeObject ho, double stock, double buyPrice, double sellPrice, int maxStock, TradeObjectStatus status, boolean useEconomyStock) {
		super(hc, playerShop, ho, stock, buyPrice, sellPrice, maxStock, status, useEconomyStock);
	}

	@Override
	public String getEnchantmentName() {
		return getTradeObject().getEnchantmentName();
	}
	@Override
	public double getBuyPrice(EnchantmentClass enchantClass) {
		if (buyPrice != 0.0) {
			return buyPrice;
		} else {
			return getTradeObject().getBuyPrice(enchantClass);
		}
	}
	@Override
	public double getSellPrice(EnchantmentClass enchantClass) {
		if (sellPrice != 0.0) {
			return sellPrice;
		} else {
			return getTradeObject().getSellPrice(enchantClass);
		}
	}
	@Override
	public double getSellPrice(EnchantmentClass enchantClass, HyperPlayer hp) {
		if (sellPrice != 0.0) {
			return sellPrice;
		} else {
			return getTradeObject().getSellPrice(enchantClass, hp);
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
	public HEnchantment getEnchantment() {
		return getTradeObject().getEnchantment();
	}

	@Override
	public int getEnchantmentLevel() {
		return getTradeObject().getEnchantmentLevel();
	}

	@Override
	public double addEnchantment(HItemStack stack) {
		return getTradeObject().addEnchantment(stack);
	}

	@Override
	public double removeEnchantment(HItemStack stack) {
		return getTradeObject().removeEnchantment(stack);
	}

	@Override
	public boolean matchesEnchantment(HEnchantment enchant) {
		return getTradeObject().matchesEnchantment(enchant);
	}





}