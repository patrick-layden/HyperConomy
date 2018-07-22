package regalowl.hyperconomy.tradeobject;

import java.awt.Image;
import java.net.URL;

import javax.imageio.ImageIO;

import regalowl.hyperconomy.HyperConomy;
import regalowl.hyperconomy.HyperEconomy;
import regalowl.hyperconomy.account.HyperPlayer;
import regalowl.hyperconomy.inventory.HEnchantment;
import regalowl.hyperconomy.inventory.HInventory;

public class TradeEnchant extends BasicTradeObject {

	private static final long serialVersionUID = -6150719215822283210L;


	public TradeEnchant(HyperConomy hc, HyperEconomy he, String name, String economy, String displayName, String aliases, String categories, String type, double value, String isstatic, double staticprice, double stock, double median, String initiation, double startprice, double ceiling, double floor, double maxstock, String compositeData, int objectDataId, String objectData, double version) {
		super(hc, he, name, economy, displayName, aliases, categories, type, value, isstatic, staticprice, stock, median, initiation, startprice, ceiling, floor, maxstock, compositeData, objectDataId, objectData, version);
	}
	
	private HEnchantment getHEnchant() {
		return new HEnchantment(objectData);
	}
	@Override
	public String getEnchantmentName() {
		return getHEnchant().getEnchantmentKeyString();
	}

	
	@Override
	public Image getImage(int width, int height) {
		Image i = null;
		URL url = hc.getClass().getClassLoader().getResource("Images/enchanted_book_0.png");
		try {
			i = ImageIO.read(url);
			if (i != null) {
				return i.getScaledInstance(width, height, Image.SCALE_DEFAULT);
			}
		} catch (Exception e) {}
		return null;
	}

	
	@Override
	public double getBuyPrice(EnchantmentClass eclass) {
		return super.getBuyPrice(1) * EnchantmentClass.getClassValue(hc, eclass);
	}
	
	@Override
	public double getSellPrice(EnchantmentClass eclass) {
		return super.getSellPrice(1) * EnchantmentClass.getClassValue(hc, eclass);
	}
	
	@Override
	public double getSellPrice(EnchantmentClass eclass, HyperPlayer hp) {
		HInventory inv = hc.getMC().getInventory(hp);
		return inv.getHeldItem().getDurabilityPercent() * getSellPrice(eclass);
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
		return getHEnchant();
	}
	@Override
	public int getEnchantmentLevel() {
		return getHEnchant().getLvl();
	}

	@Override
	public double getSellPriceWithTax(EnchantmentClass eclass, HyperPlayer hp) {
		double price = getSellPrice(eclass, hp);
		price -= hp.getSalesTax(price);
		return price;
	}
	
	@Override
	public boolean matchesEnchantment(HEnchantment enchant) {
		HEnchantment thisE = getEnchantment();
		if (enchant == null) {return false;}
		return thisE.equals(enchant);
	}


}