package regalowl.hyperconomy.tradeobject;

import java.awt.Image;
import java.net.URL;

import javax.imageio.ImageIO;

import regalowl.hyperconomy.HyperConomy;
import regalowl.hyperconomy.account.HyperPlayer;
import regalowl.hyperconomy.event.TradeObjectModificationEvent;
import regalowl.hyperconomy.inventory.HEnchantment;
import regalowl.hyperconomy.inventory.HInventory;

public class TradeEnchant extends BasicTradeObject implements TradeObject {

	private static final long serialVersionUID = -6150719215822283210L;
	private String enchantData;

	
	public TradeEnchant(HyperConomy hc, String name, String economy, String displayName, String aliases, String categories, String type, double value, String isstatic, double staticprice, double stock, double median, String initiation, double startprice, double ceiling, double floor, double maxstock, String base64ItemData) {
		super(hc, name, economy, displayName, aliases, categories, type, value, isstatic, staticprice, stock, median, initiation, startprice, ceiling, floor, maxstock);
		this.enchantData = base64ItemData;
	}
	
	private HEnchantment getSE() {
		return new HEnchantment(enchantData);
	}
	
	@Override
	public String getEnchantmentName() {
		return enchantData;
	}


	
	@Override
	public String getData() {
		return enchantData;
	}
	@Override
	public void setData(String data) {
		this.enchantData = data;
		String statement = "UPDATE hyperconomy_objects SET DATA='" + data + "' WHERE NAME = '" + this.name + "' AND ECONOMY = '" + economy + "'";
		hc.getSQLWrite().addToQueue(statement);
		hc.getHyperEventHandler().fireEvent(new TradeObjectModificationEvent(this));
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
		return getSE();
	}
	@Override
	public int getEnchantmentLevel() {
		return getSE().getLvl();
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