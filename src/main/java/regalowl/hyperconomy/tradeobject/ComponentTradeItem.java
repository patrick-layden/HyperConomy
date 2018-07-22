package regalowl.hyperconomy.tradeobject;

import java.awt.Image;
import java.net.URL;

import javax.imageio.ImageIO;

import regalowl.hyperconomy.HyperConomy;
import regalowl.hyperconomy.HyperEconomy;
import regalowl.hyperconomy.account.HyperPlayer;
import regalowl.hyperconomy.inventory.HItemStack;


public class ComponentTradeItem extends BasicTradeObject {

	private static final long serialVersionUID = -845888542311735442L;
	
	public ComponentTradeItem(HyperConomy hc, HyperEconomy he, String name, String economy, String displayName, String aliases, String categories, String type, double value, String isstatic, double staticprice, double stock, double median, String initiation, double startprice, double ceiling, double floor, double maxstock, String compositeData, int objectDataId, String objectData, double version) {
		super(hc, he, name, economy, displayName, aliases, categories, type, value, isstatic, staticprice, stock, median, initiation, startprice, ceiling, floor, maxstock, compositeData, objectDataId, objectData, version);
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
		return getSellPrice(amount) * hp.getInventory().getPercentDamaged((int)Math.ceil(amount), getItem());
	}

	
}
