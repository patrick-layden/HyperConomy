package regalowl.hyperconomy.hyperobject;

import java.awt.Image;
import java.net.URL;

import javax.imageio.ImageIO;


import regalowl.hyperconomy.HyperConomy;
import regalowl.hyperconomy.account.HyperPlayer;
import regalowl.hyperconomy.event.HyperObjectModificationEvent;
import regalowl.hyperconomy.serializable.SerializableEnchantment;
import regalowl.hyperconomy.serializable.SerializableInventory;
import regalowl.hyperconomy.serializable.SerializableItemStack;

public class Enchant extends BasicObject implements HyperObject {

	private static final long serialVersionUID = -6150719215822283210L;
	private String base64Enchant;

	
	public Enchant(String name, String economy, String displayName, String aliases, String type, double value, String isstatic, double staticprice, double stock, double median, String initiation, double startprice, double ceiling, double floor, double maxstock, String base64ItemData) {
		super(name, economy, displayName, aliases, type, value, isstatic, staticprice, stock, median, initiation, startprice, ceiling, floor, maxstock);
		this.base64Enchant = base64ItemData;
	}
	
	private SerializableEnchantment getSE() {
		return new SerializableEnchantment(base64Enchant);
	}
	
	@Override
	public String getEnchantmentName() {
		return base64Enchant;
	}


	
	@Override
	public String getData() {
		return base64Enchant;
	}
	@Override
	public void setData(String data) {
		HyperConomy hc = HyperConomy.hc;
		this.base64Enchant = data;
		String statement = "UPDATE hyperconomy_objects SET DATA='" + data + "' WHERE NAME = '" + this.name + "' AND ECONOMY = '" + economy + "'";
		hc.getSQLWrite().addToQueue(statement);
		hc.getHyperEventHandler().fireEvent(new HyperObjectModificationEvent(this));
	}
	
	
	@Override
	public Image getImage(int width, int height) {
		HyperConomy hc = HyperConomy.hc;
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
		return super.getBuyPrice(1) * EnchantmentClass.getclassValue(eclass);
	}
	

	@Override
	public double getSellPrice(EnchantmentClass eclass) {
		return super.getSellPrice(1) * EnchantmentClass.getclassValue(eclass);
	}
	
	@Override
	public double getSellPrice(EnchantmentClass eclass, HyperPlayer hp) {
		SerializableInventory inv = HyperConomy.hc.getMC().getInventory(hp);
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
	public SerializableEnchantment getEnchantment() {
		return getSE();
	}
	@Override
	public int getEnchantmentLevel() {
		return getSE().getLvl();
	}
	@Override
	public double addEnchantment(SerializableItemStack stack) {
		if (stack == null) {return 0;}
		SerializableEnchantment e = getEnchantment();
		if (stack.canAcceptEnchantment(e) && !stack.containsEnchantment(e)) {
			stack.addEnchantment(e);
			return 1;
		}
		return 0;
	}
	@Override
	public double removeEnchantment(SerializableItemStack stack) {
		if (stack == null) {return 0;}
		SerializableEnchantment e = getEnchantment();
		if (getEnchantmentLevel() == e.getLvl() && stack.containsEnchantment(e)) {
			stack.removeEnchantment(e);
			double duramult = stack.getDurabilityPercent();
			return duramult;
		}
		return 0;
	}

	@Override
	public double getSellPriceWithTax(EnchantmentClass eclass, HyperPlayer hp) {
		HyperConomy hc = HyperConomy.hc;
		double price = getSellPrice(eclass, hp);
		price -= hp.getSalesTax(price);
		return hc.gCF().twoDecimals(price);
	}

	

}