package regalowl.hyperconomy.tradeobject;


import regalowl.hyperconomy.HyperConomy;
import regalowl.hyperconomy.account.HyperPlayer;
import regalowl.hyperconomy.inventory.HEnchantment;


public class ShopTradeEnchant extends BasicShopTradeObject implements TradeObject {


	private static final long serialVersionUID = -213806188136759445L;

	public ShopTradeEnchant(HyperConomy hc, String playerShop, TradeObject ho, double stock, double buyPrice, double sellPrice, int maxStock, TradeObjectStatus status, boolean useEconomyStock) {
		super(hc, playerShop, ho, stock, buyPrice, sellPrice, maxStock, status, useEconomyStock);
	}

	@Override
	public String getEnchantmentName() {
		return getParentTradeObject().getEnchantmentName();
	}
	@Override
	public double getBuyPrice(EnchantmentClass enchantClass) {
		if (buyPrice != 0.0) {
			return buyPrice;
		} else {
			return getParentTradeObject().getBuyPrice(enchantClass);
		}
	}
	@Override
	public double getSellPrice(EnchantmentClass enchantClass) {
		if (sellPrice != 0.0) {
			return sellPrice;
		} else {
			return getParentTradeObject().getSellPrice(enchantClass);
		}
	}
	@Override
	public double getSellPrice(EnchantmentClass enchantClass, HyperPlayer hp) {
		if (sellPrice != 0.0) {
			return sellPrice;
		} else {
			return getParentTradeObject().getSellPrice(enchantClass, hp);
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
		return getParentTradeObject().getEnchantment();
	}

	@Override
	public int getEnchantmentLevel() {
		return getParentTradeObject().getEnchantmentLevel();
	}

	@Override
	public boolean matchesEnchantment(HEnchantment enchant) {
		return getParentTradeObject().matchesEnchantment(enchant);
	}





}