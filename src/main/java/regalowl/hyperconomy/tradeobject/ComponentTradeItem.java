package regalowl.hyperconomy.tradeobject;

import java.awt.Image;
import java.net.URL;

import javax.imageio.ImageIO;

import regalowl.hyperconomy.HC;
import regalowl.hyperconomy.account.HyperPlayer;
import regalowl.hyperconomy.event.HyperObjectModificationEvent;
import regalowl.hyperconomy.inventory.HInventory;
import regalowl.hyperconomy.inventory.HInventoryType;
import regalowl.hyperconomy.inventory.HItemStack;


public class ComponentTradeItem extends BasicTradeObject implements TradeObject {

	private static final long serialVersionUID = -845888542311735442L;
	private String base64Item;

	

	public ComponentTradeItem(String name, String economy, String displayName, String aliases, String type, double value, String isstatic, double staticprice, double stock, double median, String initiation, double startprice, double ceiling, double floor, double maxstock, String base64ItemData) {
		super(name, economy, displayName, aliases, type, value, isstatic, staticprice, stock, median, initiation, startprice, ceiling, floor, maxstock);
		this.base64Item = base64ItemData;
	}
	
	private HItemStack getSIS() {
		return new HItemStack(base64Item);
	}
	
	@Override
	public Image getImage(int width, int height) {
		HC hc = HC.hc;
		Image i = null;
		URL url = null;
		HItemStack sis = getSIS();
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
		return super.getSellPrice(amount) * getDamageMultiplier((int)Math.ceil(amount), hp.getInventory());
	}

	@Override
	public int count(HInventory inventory) {
		int totalitems = 0;
		for (int slot = 0; slot < inventory.getSize(); slot++) {
			HItemStack currentItem = inventory.getItem(slot);
			if (matchesItemStack(currentItem)) {
				totalitems += currentItem.getAmount();
			}
		}
		return totalitems;
	}
	@Override
	public int getAvailableSpace(HInventory inventory) {
		return inventory.getAvailableSpace(getSIS());
	}
	
	@Override
	public HItemStack getItemStack(int amount) {
		HItemStack sis = getSIS();
		sis.setAmount(amount);
		return sis;
	}
	@Override
	public HItemStack getItem() {
		return getSIS();
	}
	@Override
	public boolean matchesItemStack(HItemStack stack) {
		HItemStack thisSis = getSIS();
		if (stack == null) {return false;}
		return stack.isSimilarTo(thisSis);
	}
	@Override
	public String getData() {
		return base64Item;
	}
	
	@Override
	public void setItemStack(HItemStack stack) {
		setData(stack.serialize());
	}
	@Override
	public void setData(String data) {
		HC hc = HC.hc;
		this.base64Item = data;
		String statement = "UPDATE hyperconomy_objects SET DATA='" + data + "' WHERE NAME = '" + this.name + "' AND ECONOMY = '" + economy + "'";
		hc.getSQLWrite().addToQueue(statement);
		hc.getHyperEventHandler().fireEvent(new HyperObjectModificationEvent(this));
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
		HC hc = HC.hc;
		HItemStack sis = getSIS();
		try {
			double damage = 0;
			if (!isDurable()) {return 1;}
			int totalitems = 0;
			int heldslot = -1;
			if (inventory.getInventoryType() == HInventoryType.PLAYER) {
				HItemStack ci = inventory.getHeldItem();
				if (ci.isSimilarTo(sis)) {
					damage += ci.getDurabilityPercent();
					totalitems++;
					heldslot = inventory.getHeldSlot();
					if (totalitems >= amount) {return damage;}
				}
			}
			for (int slot = 0; slot < inventory.getSize(); slot++) {
				if (slot == heldslot) {continue;}
				HItemStack ci = inventory.getItem(slot);
				if (!ci.isSimilarTo(sis)) {continue;}
				damage += ci.getDurabilityPercent();
				totalitems++;
				if (totalitems >= amount) {break;}
			}
			damage /= amount;
			if (damage == 0) {return 1;}
			return damage;
		} catch (Exception e) {
			String info = "getDamageMultiplier() passed values amount='" + amount + "'";
			hc.gDB().writeError(e, info);
			return 0;
		}
	}


}
