package regalowl.hyperconomy.tradeobject;

import java.awt.Image;
import java.net.URL;

import javax.imageio.ImageIO;

import regalowl.hyperconomy.HyperConomy;
import regalowl.hyperconomy.account.HyperPlayer;
import regalowl.hyperconomy.event.TradeObjectModificationEvent;
import regalowl.hyperconomy.inventory.HInventory;
import regalowl.hyperconomy.inventory.HItemStack;
import regalowl.simpledatalib.CommonFunctions;


public class ComponentTradeItem extends BasicTradeObject implements TradeObject {

	private static final long serialVersionUID = -845888542311735442L;
	private String itemData;

	

	public ComponentTradeItem(HyperConomy hc, String name, String economy, String displayName, String aliases, String categories, String type, double value, String isstatic, double staticprice, double stock, double median, String initiation, double startprice, double ceiling, double floor, double maxstock, String itemData) {
		super(hc, name, economy, displayName, aliases, categories, type, value, isstatic, staticprice, stock, median, initiation, startprice, ceiling, floor, maxstock);
		this.itemData = itemData;
	}
	
	@Override
	public Image getImage(int width, int height) {
		Image i = null;
		URL url = null;
		HItemStack sis = getItem();
		if (sis.getMaterial().equalsIgnoreCase("POTION")) {
			url = hc.getClass().getClassLoader().getResource("Images/potion.png");
		} else {
			url = hc.getClass().getClassLoader().getResource("Images/" + sis.getMaterial().toLowerCase() + "_" + sis.getData() + ".png");
		}
		try {
			i = ImageIO.read(url);
			if (i != null) {
				return i.getScaledInstance(width, height, Image.SCALE_DEFAULT);
			}
		} catch (Exception e) {}
		return null;
	}


	@Override
	public double getSellPrice(double amount, HyperPlayer hp) {
		return CommonFunctions.twoDecimals(super.getSellPrice(amount) * getDamageMultiplier((int)Math.ceil(amount), hp.getInventory()));
	}

	@Override
	public int count(HInventory inventory) {
		return inventory.count(getItem());
	}
	@Override
	public int getAvailableSpace(HInventory inventory) {
		return inventory.getAvailableSpace(getItem());
	}
	
	@Override
	public HItemStack getItemStack(int amount) {
		HItemStack sis = getItem();
		sis.setAmount(amount);
		return sis;
	}
	@Override
	public HItemStack getItem() {
		return new HItemStack(itemData);
	}
	@Override
	public boolean matchesItemStack(HItemStack stack) {
		if (stack == null) {return false;}
		return stack.isSimilarTo(getItem());
	}
	@Override
	public String getData() {
		return itemData;
	}
	
	@Override
	public void setItemStack(HItemStack stack) {
		setData(stack.serialize());
	}
	@Override
	public void setData(String data) {
		this.itemData = data;
		String statement = "UPDATE hyperconomy_objects SET DATA='" + data + "' WHERE NAME = '" + this.name + "' AND ECONOMY = '" + economy + "'";
		sw.addToQueue(statement);
		hc.getHyperEventHandler().fireEvent(new TradeObjectModificationEvent(this));
	}


	@Override
	public void add(int amount, HInventory inventory) {
		inventory.add(amount, getItem());
	}
	@Override
	public double remove(int amount, HInventory inventory) {
		return inventory.remove(amount, getItem());
	}
	

	@Override
	public double getDamageMultiplier(int amount, HInventory inventory) {
		return inventory.getPercentDamaged(amount, getItem());
	}


}
