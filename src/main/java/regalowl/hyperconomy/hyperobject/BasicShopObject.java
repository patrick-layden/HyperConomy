package regalowl.hyperconomy.hyperobject;

import java.awt.Image;
import java.util.ArrayList;


import regalowl.databukkit.sql.SQLWrite;
import regalowl.databukkit.sql.WriteStatement;
import regalowl.hyperconomy.HyperConomy;
import regalowl.hyperconomy.account.HyperPlayer;
import regalowl.hyperconomy.event.HyperObjectModificationEvent;
import regalowl.hyperconomy.serializable.SerializableInventory;
import regalowl.hyperconomy.serializable.SerializableItemStack;
import regalowl.hyperconomy.shop.PlayerShop;

public class BasicShopObject extends BasicObject implements HyperObject {

	private static final long serialVersionUID = -8506945265990355676L;
	protected String playerShop;
	protected String hyperObject;
	protected String economy;
	protected double stock;
	protected double buyPrice;
	protected double sellPrice;
	protected HyperObjectStatus status;
	protected int maxStock;
	protected boolean useEconomyStock;
	
	public BasicShopObject(String playerShop, HyperObject ho, double stock, double buyPrice, double sellPrice, int maxStock, HyperObjectStatus status, boolean useEconomyStock) {
		this.playerShop = playerShop;
		this.hyperObject = ho.getName();
		this.economy = ho.getEconomy();
		this.stock = stock;
		this.buyPrice = buyPrice;
		this.sellPrice = sellPrice;
		this.maxStock = maxStock;
		this.status = status;
		this.useEconomyStock = useEconomyStock;
	}
	

	@Override
	public PlayerShop getShop() {
		HyperConomy hc = HyperConomy.hc;
		return (PlayerShop)hc.getHyperShopManager().getShop(playerShop);
	}
	@Override
	public HyperObject getHyperObject() {
		HyperConomy hc = HyperConomy.hc;
		HyperObject ho = hc.getDataManager().getEconomy(economy).getHyperObject(hyperObject);
		return ho;
	}
	@Override
	public double getStock() {
		if (useEconomyStock) {
			return getHyperObject().getStock();
		} else {
			return stock;
		}
	}
	@Override
	public double getBuyPrice() {
		return buyPrice;
	}
	@Override
	public double getSellPrice() {
		return sellPrice;
	}
	@Override
	public HyperObjectStatus getStatus() {
		return status;
	}
	@Override
	public int getMaxStock() {
		return maxStock;
	}
	@Override
	public boolean useEconomyStock() {
		return useEconomyStock;
	}
	@Override
	public void setHyperObject(HyperObject ho) {
		HyperConomy hc = HyperConomy.hc;
		SQLWrite sw = hc.getSQLWrite();
		WriteStatement ws = new WriteStatement("UPDATE hyperconomy_shop_objects SET HYPEROBJECT=? WHERE SHOP=? AND HYPEROBJECT=?", hc.getDataBukkit());
		ws.addParameter(ho.getName());
		ws.addParameter(playerShop);
		ws.addParameter(hyperObject);
		sw.addToQueue(ws);
		hc.getHyperEventHandler().fireEvent(new HyperObjectModificationEvent(this));
	}
	@Override
	public void setShop(PlayerShop playerShop) {
		HyperConomy hc = HyperConomy.hc;
		SQLWrite sw = hc.getSQLWrite();
		this.playerShop = playerShop.getName();
		WriteStatement ws = new WriteStatement("UPDATE hyperconomy_shop_objects SET SHOP=? WHERE SHOP=? AND HYPEROBJECT=?", hc.getDataBukkit());
		ws.addParameter(playerShop.getName());
		ws.addParameter(playerShop.getName());
		ws.addParameter(hyperObject);
		sw.addToQueue(ws);
		hc.getHyperEventHandler().fireEvent(new HyperObjectModificationEvent(this));
	}
	@Override
	public void setStock(double stock) {
		HyperConomy hc = HyperConomy.hc;
		SQLWrite sw = hc.getSQLWrite();
		if (useEconomyStock) {
			getHyperObject().setStock(stock);
		} else {
			if (stock < 0.0) {stock = 0.0;}
			this.stock = stock;
			WriteStatement ws = new WriteStatement("UPDATE hyperconomy_shop_objects SET QUANTITY=? WHERE SHOP=? AND HYPEROBJECT=?", hc.getDataBukkit());
			ws.addParameter(stock);
			ws.addParameter(playerShop);
			ws.addParameter(hyperObject);
			sw.addToQueue(ws);
			hc.getHyperEventHandler().fireEvent(new HyperObjectModificationEvent(this));
		}
	}
	@Override
	public void setBuyPrice(double buyPrice) {
		HyperConomy hc = HyperConomy.hc;
		SQLWrite sw = hc.getSQLWrite();
		this.buyPrice = buyPrice;
		WriteStatement ws = new WriteStatement("UPDATE hyperconomy_shop_objects SET BUY_PRICE=? WHERE SHOP=? AND HYPEROBJECT=?", hc.getDataBukkit());
		ws.addParameter(buyPrice);
		ws.addParameter(playerShop);
		ws.addParameter(hyperObject);
		sw.addToQueue(ws);
		hc.getHyperEventHandler().fireEvent(new HyperObjectModificationEvent(this));
	}
	@Override
	public void setSellPrice(double sellPrice) {
		HyperConomy hc = HyperConomy.hc;
		SQLWrite sw = hc.getSQLWrite();
		this.sellPrice = sellPrice;
		WriteStatement ws = new WriteStatement("UPDATE hyperconomy_shop_objects SET SELL_PRICE=? WHERE SHOP=? AND HYPEROBJECT=?", hc.getDataBukkit());
		ws.addParameter(sellPrice);
		ws.addParameter(playerShop);
		ws.addParameter(hyperObject);
		sw.addToQueue(ws);
		hc.getHyperEventHandler().fireEvent(new HyperObjectModificationEvent(this));
	}
	@Override
	public void setMaxStock(int maxStock) {
		HyperConomy hc = HyperConomy.hc;
		SQLWrite sw = hc.getSQLWrite();
		this.maxStock = maxStock;
		WriteStatement ws = new WriteStatement("UPDATE hyperconomy_shop_objects SET MAX_STOCK=? WHERE SHOP=? AND HYPEROBJECT=?", hc.getDataBukkit());
		ws.addParameter(maxStock);
		ws.addParameter(playerShop);
		ws.addParameter(hyperObject);
		sw.addToQueue(ws);
		hc.getHyperEventHandler().fireEvent(new HyperObjectModificationEvent(this));
	}
	@Override
	public void setStatus(HyperObjectStatus status) {
		HyperConomy hc = HyperConomy.hc;
		SQLWrite sw = hc.getSQLWrite();
		this.status = status;
		WriteStatement ws = new WriteStatement("UPDATE hyperconomy_shop_objects SET STATUS=? WHERE SHOP=? AND HYPEROBJECT=?", hc.getDataBukkit());
		ws.addParameter(status.toString());
		ws.addParameter(playerShop);
		ws.addParameter(hyperObject);
		sw.addToQueue(ws);
		hc.getHyperEventHandler().fireEvent(new HyperObjectModificationEvent(this));
	}
	@Override
	public void setUseEconomyStock(boolean state) {
		this.useEconomyStock = state;
	}
	
	
	



	@Override
	public double getSellPriceWithTax(double amount, HyperPlayer hp) {
		return getSellPrice(amount);//PlayerShop objects aren't taxed.
	}
	@Override
	public double getSellPrice(double amount, HyperPlayer hp) {
		return getSellPrice(amount);//PlayerShop objects aren't taxed.
	}
	@Override
	public double getBuyPriceWithTax(double amount) {
		return getBuyPrice(amount);//PlayerShop objects aren't taxed.
	}
	@Override
	public double getBuyPrice(double amount) {
		if (getBuyPrice() != 0.0) {
			return getBuyPrice() * amount;
		} else {
			return getHyperObject().getBuyPrice(amount);
		}
	}
	@Override
	public double getSellPrice(double amount) {
		if (getSellPrice() != 0.0) {
			return getSellPrice() * amount;
		} else {
			return getHyperObject().getSellPrice(amount);
		}
	}
	@Override
	public boolean isShopObject() {return true;}
	
	@Override
	public void checkInitiationStatus() {
		getHyperObject().checkInitiationStatus();
	}
	
	
	
	//The following methods don't apply to PlayerShop objects and have been overridden to prevent database changes.
	@Override
	public double applyCeilingFloor(double value, double quantity) {return value;}
	@Override
	public void setName(String name) {}
	@Override
	public void setEconomy(String economy) {}
	@Override
	public void setDisplayName(String displayName) {}
	@Override
	public void setAliases(ArrayList<String> newAliases) {}
	@Override
	public void addAlias(String addAlias) {}
	@Override
	public void removeAlias(String removeAlias) {}
	@Override
	public void setType(HyperObjectType type) {}
	@Override
	public void setValue(double value) {}
	@Override
	public void setIsstatic(String isstatic) {}
	@Override
	public void setStaticprice(double staticprice) {}
	@Override
	public void setMedian(double median) {}
	@Override
	public void setInitiation(String initiation) {}
	@Override
	public void setStartprice(double startprice) {}
	@Override
	public void setCeiling(double ceiling) {}
	@Override
	public void setFloor(double floor) {}
	@Override
	public void setMaxstock(double maxstock) {}
	@Override
	public double getPurchaseTax(double cost) {return 0;}
	@Override
	public double getSalesTaxEstimate(double value) {return 0;}
	@Override
	public void delete() {}



	
	
	//The following methods don't apply to PlayerShop objects and have been overridden to forward the request to the PlayerShop object's parent object.
	@Override
	public String getData() {
		return getHyperObject().getData();
	}
	@Override
	public void setData(String data) {
		getHyperObject().setData(data);
	}
	@Override
	public Image getImage(int width, int height) {
		return getHyperObject().getImage(width, height);
	}
	@Override
	public double getTotalStock() {
		return getHyperObject().getTotalStock();
	}
	@Override
	public String getName() {
		return getHyperObject().getName();
	}
	@Override
	public String getDisplayName() {
		return getHyperObject().getDisplayName();
	}
	@Override
	public ArrayList<String> getAliases() {
		return getHyperObject().getAliases();
	}
	@Override
	public String getAliasesString() {
		return getHyperObject().getAliasesString();
	}
	@Override
	public boolean hasName(String testName) {
		return getHyperObject().hasName(testName);
	}
	@Override
	public String getEconomy() {
		return getHyperObject().getEconomy();
	}
	@Override
	public HyperObjectType getType() {
		return getHyperObject().getType();
	}
	@Override
	public double getValue() {
		return getHyperObject().getValue();
	}
	@Override
	public String getIsstatic() {
		return getHyperObject().getIsstatic();
	}
	@Override
	public double getStaticprice() {
		return getHyperObject().getStaticprice();
	}
	@Override
	public double getMedian() {
		return getHyperObject().getMedian();
	}
	@Override
	public String getInitiation() {
		return getHyperObject().getInitiation();
	}
	@Override
	public double getStartprice() {
		return getHyperObject().getStartprice();
	}
	@Override
	public double getCeiling() {
		return getHyperObject().getCeiling();
	}
	@Override
	public double getFloor() {
		return getHyperObject().getFloor();
	}
	@Override
	public double getMaxstock() {
		return getHyperObject().getMaxstock();
	}
	@Override
	public int getMaxInitial() {
		return getHyperObject().getMaxInitial();
	}
	@Override
	public boolean nameStartsWith(String part) {
		return getHyperObject().nameStartsWith(part);
	}
	@Override
	public boolean nameContains(String part) {
		return getHyperObject().nameContains(part);
	}
	
	
	@Override
	public boolean isDurable() {
		return getHyperObject().isDurable();
	}
	@Override
	public int count(SerializableInventory inventory) {
		return getHyperObject().count(inventory);
	}
	@Override
	public int getAvailableSpace(SerializableInventory inventory) {
		return getHyperObject().getAvailableSpace(inventory);
	}
	@Override
	public void add(int amount, HyperPlayer hp) {
		getHyperObject().add(amount, hp);
	}
	@Override
	public double remove(int amount, HyperPlayer hp) {
		return getHyperObject().remove(amount, hp);
	}
	@Override
	public SerializableItemStack getItem() {
		return getHyperObject().getItem();
	}
	@Override
	public SerializableItemStack getItemStack(int amount) {
		return getHyperObject().getItemStack(amount);
	}
	@Override
	public void add(int amount, SerializableInventory inventory) {
		getHyperObject().add(amount, inventory);
	}
	@Override
	public double remove(int amount, SerializableInventory inventory) {
		return getHyperObject().remove(amount, inventory);
	}
	@Override
	public double getDamageMultiplier(int amount, SerializableInventory inventory) {
		return getHyperObject().getDamageMultiplier(amount, inventory);
	}
	
	
	
	
	
	
	
	
	
	
	
	
	

}
